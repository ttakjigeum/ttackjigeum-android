package com.devhjs.ttackjigeum_android.core.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

/**
 * 키보드 및 포커스 관련 유틸리티 확장 함수들
 */


/**
 * 숫자를 3자리마다 콤마가 찍힌 금액 형식으로 변환해주는 VisualTransformation입니다.
 */
class PriceVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val formatter = DecimalFormat("#,###")
        val formattedText = formatter.format(originalText.toLong())

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val subString = originalText.substring(0, offset)
                return formatter.format(subString.toLong()).length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val commasBefore = formattedText.substring(0, offset).count { it == ',' }
                return (offset - commasBefore).coerceIn(0, originalText.length)
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}

/**
 * '완료(Done)' 액션 시 포커스를 해제하는 KeyboardActions를 반환합니다.
 */
fun clearFocusOnDone(focusManager: FocusManager): KeyboardActions {
    return KeyboardActions(onDone = { focusManager.clearFocus() })
}


/**
 * 화면의 빈 공간을 터치했을 때 포커스를 해제하고 키보드를 내리는 Modifier입니다.
 *
 * 사용법:
 * val focusManager = LocalFocusManager.current
 * Column(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .addFocusCleaner(focusManager)
 * ) { ... }
 */
fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {}): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            doOnClear()
            focusManager.clearFocus()
        })
    }
}

/**
 * 키보드가 닫힐 때(시스템 백 버튼 등) 자동으로 포커스를 해제하는 Effect입니다.
 *
 * @param isFocused 현재 컴포넌트(TextField 등)가 포커스 상태인지 여부
 * @param focusManager 포커스를 제어할 FocusManager (기본값: LocalFocusManager.current)
 */
@Composable
fun ClearFocusOnKeyboardDismissEffect(
    isFocused: Boolean,
    focusManager: FocusManager = LocalFocusManager.current
) {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val currentIsFocused by rememberUpdatedState(isFocused)

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && currentIsFocused) {
            focusManager.clearFocus()
        }
    }
}
