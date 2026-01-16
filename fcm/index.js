const { onRequest } = require("firebase-functions/v2/https");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { setGlobalOptions } = require("firebase-functions/v2");
const { onSchedule } = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");

if (admin.apps.length === 0) {
    admin.initializeApp();
}

// 서울 리전 설정
setGlobalOptions({ region: "asia-northeast3" });

/**
 * [함수 1] HTTP 테스트용
 */
exports.sendPushNotification = onRequest(async (req, res) => {
    const fcmToken = req.query.token;
    if (!fcmToken) {
        res.status(400).send("FCM 토큰이 없습니다.");
        return;
    }
    try {
        await admin.messaging().send({
            notification: { title: "테스트", body: "성공" },
            token: fcmToken,
        });
        res.status(200).send("성공");
    } catch (e) {
        res.status(500).send(e.message);
    }
});

/**
 * [함수 2] 최저가 및 목표가 알림 (디버그 로그 강화)
 */
exports.checkPriceAndNotify = onDocumentCreated("priceHistorys/{historyId}", async (event) => {
    const snapshot = event.data;
    if (!snapshot) return null;

    const newData = snapshot.data();
    const productIdNum = Number(newData.productId);
    const newPrice = newData.price;

    console.log(`[1단계: 이력 감지] 상품ID: ${productIdNum}, 신규가격: ${newPrice}`);

    try {
        const productQuerySnap = await admin.firestore()
            .collection("products")
            .where("id", "==", productIdNum)
            .limit(1)
            .get();

        if (productQuerySnap.empty) {
            console.log(`[중단] products 컬렉션에 id:${productIdNum} 필드를 가진 상품 문서가 없습니다.`);
            return null;
        }

        const productDoc = productQuerySnap.docs[0];
        const productData = productDoc.data();
        const productName = productData.name;
        const currentPrice = productData.currentPrice || 0;
        const productIdStr = String(productIdNum);

        if (newPrice < currentPrice) {
            console.log(`[2단계: 하락 확인] ${productName}: ${currentPrice} -> ${newPrice}`);

            const usersSnapshot = await admin.firestore().collection("users").get();
            console.log(`[디버그] 전체 사용자 수: ${usersSnapshot.size}명`);

            const messages = [];

            for (const userDoc of usersSnapshot.docs) {
                const userData = userDoc.data();
                const userId = userDoc.id;

                // 찜 목록 확인
                const favoriteDoc = await userDoc.ref
                    .collection("product")
                    .doc(productIdStr)
                    .get();

                if (!favoriteDoc.exists) {
                    console.log(`[디버그] 유저(${userId}): 이 상품을 찜하지 않음 (경로: users/${userId}/product/${productIdStr})`);
                    continue;
                }

                const favoriteData = favoriteDoc.data();
                const isEnabled = favoriteData.notificationEnabled === true;
                const targetPrice = Number(favoriteData.targetPrice || 0);

                console.log(`[디버그] 유저(${userId}) 찜 확인됨: 알림설정=${isEnabled}, 목표가=${targetPrice}, 토큰존재=${!!userData.fcmToken}`);

                if (isEnabled && userData.fcmToken) {
                    // 목표가 알림 조건
                    if (targetPrice > 0 && newPrice <= targetPrice) {
                        console.log(`[디버그] 유저(${userId}): 목표가 달성 조건 충족!`);
                        messages.push({
                            notification: {
                                title: "🎯 목표가 달성 알림!",
                                body: `${productName}이 설정하신 목표가(${targetPrice}원) 이하인 ${newPrice}원이 되었습니다!`
                            },
                            token: userData.fcmToken,
                            android: { priority: "high" }
                        });
                    }
                    // 일반 최저가 알림 조건
                    else {
                        console.log(`[디버그] 유저(${userId}): 일반 최저가 하락 조건 충족!`);
                        messages.push({
                            notification: {
                                title: "📉 최저가 하락 알림!",
                                body: `${productName}의 가격이 ${newPrice}원으로 더 낮아졌습니다!`
                            },
                            token: userData.fcmToken,
                            android: { priority: "high" }
                        });
                    }
                } else if (!isEnabled) {
                    console.log(`[디버그] 유저(${userId}): notificationEnabled가 false입니다.`);
                } else if (!userData.fcmToken) {
                    console.log(`[디버그] 유저(${userId}): fcmToken 필드가 없습니다.`);
                }
            }

            if (messages.length > 0) {
                const response = await admin.messaging().sendEach(messages);
                console.log(`[4단계: 발송 성공] ${response.successCount}명에게 전송 완료.`);
            } else {
                console.log("[3단계: 발송 스킵] 모든 사용자가 조건(찜/설정/목표가)에 맞지 않아 메시지 배열이 비어있습니다.");
            }

            await productDoc.ref.update({ currentPrice: newPrice });

        } else {
            console.log(`[중단] 가격이 하락하지 않음 (신규:${newPrice} >= 현재:${currentPrice})`);
        }
    } catch (error) {
        console.error("오류 발생:", error);
    }
    return null;
});

/**
 * [공통 로직] 가격 변동 처리용 함수
 * 요구하신 필드 형식(datetime, price, productId)에 맞춰 저장합니다.
 */
async function performPriceUpdate() {
    console.log("--- 가격 업데이트 로직 실행 시작 ---");
    const productsSnap = await admin.firestore().collection("products").get();

    if (productsSnap.empty) {
        console.log("업데이트할 상품이 없습니다.");
        return "상품 없음";
    }

    const batch = admin.firestore().batch();
    const historyCollection = admin.firestore().collection("priceHistorys");

    // 오늘 날짜를 "YYYY-MM-DD" 형식의 문자열로 생성
    const today = new Date();
    const dateString = today.toISOString().split('T')[0]; // 결과 예: "2026-01-16"

    for (const doc of productsSnap.docs) {
        const productData = doc.data();
        const currentPrice = productData.currentPrice || 10000;

        // 랜덤 가격 변동 (-10% ~ +10%)
        const changeType = Math.floor(Math.random() * 3); // 0:유지, 1:하락, 2:상승
        let newPrice = currentPrice;

        if (changeType === 1) { // 하락
            newPrice = Math.floor(currentPrice * (0.9 + Math.random() * 0.05));
        } else if (changeType === 2) { // 상승
            newPrice = Math.floor(currentPrice * (1.05 + Math.random() * 0.05));
        }

        console.log(`[변동 확인] ${productData.name}: ${currentPrice} -> ${newPrice}`);

        // 요청하신 형식으로 필드 구성
        const newHistoryRef = historyCollection.doc();
        batch.set(newHistoryRef, {
            datetime: dateString,     // "2026-01-16" (string)
            price: newPrice,          // 4800 (number)
            productId: productData.id // 1 (number)
        });
    }

    await batch.commit();
    return `상품 ${productsSnap.size}개 업데이트 완료 (날짜: ${dateString})`;
}

/**
 * [함수 3] 매일 새벽 2시 스케줄러 실행
 */
exports.dailyPriceUpdate = onSchedule({
    schedule: "0 2 * * *",
    timeZone: "Asia/Seoul",
}, async (event) => {
    console.log("정기 가격 업데이트 시작");
    await performPriceUpdate();
});

/**
 * [함수 4] 수동 실행 테스트용 URL
 */
exports.manualPriceUpdate = onRequest(async (req, res) => {
    try {
        const result = await performPriceUpdate();
        res.status(200).send(`수동 실행 성공: ${result}`);
    } catch (e) {
        res.status(500).send(e.message);
    }
});