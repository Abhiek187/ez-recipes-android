package com.abhiek.ezrecipes.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

inline fun <reified T> AnimatedContentTransitionScope<T>.slideLeftEnter() = fadeIn(
    animationSpec = tween(
        300, easing = LinearEasing
    )
) + slideIntoContainer(
    animationSpec = tween(300, easing = EaseIn),
    towards = AnimatedContentTransitionScope.SlideDirection.Start
)

inline fun <reified T> AnimatedContentTransitionScope<T>.slideLeftExit() = fadeOut(
    animationSpec = tween(
        300, easing = LinearEasing
    )
) + slideOutOfContainer(
    animationSpec = tween(300, easing = EaseIn),
    towards = AnimatedContentTransitionScope.SlideDirection.Start
)

inline fun <reified T> AnimatedContentTransitionScope<T>.slideRightEnter() = fadeIn(
    animationSpec = tween(
        300, easing = LinearEasing
    )
) + slideIntoContainer(
    animationSpec = tween(300, easing = EaseOut),
    towards = AnimatedContentTransitionScope.SlideDirection.End
)

inline fun <reified T> AnimatedContentTransitionScope<T>.slideRightExit() = fadeOut(
    animationSpec = tween(
        300, easing = LinearEasing
    )
) + slideOutOfContainer(
    animationSpec = tween(300, easing = EaseOut),
    towards = AnimatedContentTransitionScope.SlideDirection.End
)
