package com.devhjs.ttackjigeum_android.data.repository.mock

import com.devhjs.ttackjigeum_android.domain.model.PriceHistory
import com.devhjs.ttackjigeum_android.domain.model.Product
import com.devhjs.ttackjigeum_android.domain.model.UserConfig

object MockData {
    val MockProducts = mutableListOf(
        Product(
            id = 1L,
            name = "신라면 (5봉)",
            originalPrice = 4500,
            currentPrice = 3900,
            targetPrice = 3500,
            lowestPrice = 3400,
            averagePrice = 4100,
            isFavorite = true,
            url = "https://www.coupang.com/vp/products/7038366959?itemId=17397483274&vendorItemId=84566941756&src=1042503&spec=10304982&addtag=400&ctag=7038366959&lptag=7038366959-17397483274&itime=20260114181549&pageType=PRODUCT&pageValue=7038366959&wPcid=17664129008434401342420&wRef=www.google.com&wTime=20260114181549&redirect=landing&gclid=CjwKCAiAmp3LBhAkEiwAJM2JUL-VMgBuiqdHaHc_vzmqjAFtPTGtRhdW-OT7EA55O0Zr5ZMvncZ9ZxoCqUcQAvD_BwE&mcid=4d7f7620f0224ae7a728bda1a7ff5500&campaignid=21793140442&adgroupid=",
            imageUrl = "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcRwXU2SFixfY9C4PX2un5j09NSMWnqPR_lRTz6OKMQznCUEyugn5CVFf4ny7dI_bYghYpWGZCuRxZcQHDNffDa7BKwv07R2VGZYA10bDO8m92qioweDKr2hTbvz4U0WijdNGpzVeA&usqp=CAc",
        ),
        Product(
            id = 2L,
            name = "서울우유 멸균우유 200ml, 24개",
            originalPrice = 2980,
            currentPrice = 2850,
            targetPrice = 2500,
            lowestPrice = 2480,
            averagePrice = 2900,
            isFavorite = false,
            url = "https://brand.naver.com/seoulmilk/products/5165839057",
            imageUrl = "https://shop-phinf.pstatic.net/20230508_87/16835230282512Xy2A_JPEG/12241589062042700_1371104950.jpg?type=o1000",
        ),
        Product(
            id = 3L,
            name = "코카-콜라 CAN 250ml 30개",
            originalPrice = 2100,
            currentPrice = 1900,
            targetPrice = 1500,
            lowestPrice = 1450,
            averagePrice = 2000,
            isFavorite = true,
            url = "https://brand.naver.com/cocacola/products/4608999354",
            imageUrl = "https://shop-phinf.pstatic.net/20251013_130/1760340943545xGxK3_JPEG/6298017364967674_1319074167.jpg?type=o1000",
        ),
    )

    val mockProduct = Product(
        id = 123L,
        name = "두바이쫀득쿠키 6구 세트 (300g) 두바이쫀득모찌 쫀득쿠키택배 두바이쫀득볼 에이미미버터",
        originalPrice = 39800,
        currentPrice = 37800,
        targetPrice = 36000,
        lowestPrice = 36800,
        averagePrice = 37800,
        isFavorite = false,
        url = "https://smartstore.naver.com/mang9dessert/products/12576943701",
        imageUrl = "https://shop-phinf.pstatic.net/20251105_269/1762347066392sk1VA_JPEG/38808412204101421_509762020.jpg?type=m1000_pd",
    )

    val MockPriceHistories = mapOf(
        1L to listOf(
            PriceHistory("2024-01-10", 4500),
            PriceHistory("2024-01-11", 4300),
            PriceHistory("2024-01-12", 3900),
            PriceHistory("2024-01-13", 5000),
            PriceHistory("2024-01-14", 10000),
        ),
        2L to listOf(
            PriceHistory("2024-01-08", 2980),
            PriceHistory("2024-01-12", 2850),
        ),
        3L to listOf(
            PriceHistory("2024-01-05", 2100),
            PriceHistory("2024-01-07", 2000),
            PriceHistory("2024-01-12", 1900),
        ),
    )

    val MockUserConfigs = mutableListOf(
        UserConfig(
            productId = 1L,
            targetPrice = 3500,
            notificationEnabled = true,
        ),
        UserConfig(
            productId = 2L,
            targetPrice = 2500,
            notificationEnabled = false,
        ),
    )
}
