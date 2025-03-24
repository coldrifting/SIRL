package com.coldrifting.sirl.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF8E4956)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFD9DD)
val onPrimaryContainerLight = Color(0xFF72333F)
val secondaryLight = Color(0xFF5F5790)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE5DEFF)
val onSecondaryContainerLight = Color(0xFF473F77)
val tertiaryLight = Color(0xFF006A61)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFF9EF2E6)
val onTertiaryContainerLight = Color(0xFF005049)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFF8F7)
val onBackgroundLight = Color(0xFF22191A)
val surfaceLight = Color(0xFFFFF7FA)
val onSurfaceLight = Color(0xFF201A1E)
val surfaceVariantLight = Color(0xFFEEDEE7)
val onSurfaceVariantLight = Color(0xFF4E444B)
val outlineLight = Color(0xFF80747B)
val outlineVariantLight = Color(0xFFD1C2CB)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF352E33)
val inverseOnSurfaceLight = Color(0xFFFAEDF4)
val inversePrimaryLight = Color(0xFFFFB2BD)
val surfaceDimLight = Color(0xFFE3D7DD)
val surfaceBrightLight = Color(0xFFFFF7FA)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFDF0F7)
val surfaceContainerLight = Color(0xFFF7EBF1)
val surfaceContainerHighLight = Color(0xFFF1E5EB)
val surfaceContainerHighestLight = Color(0xFFEBDFE6)

val primaryDark = Color(0xFFFFB2BD)
val onPrimaryDark = Color(0xFF561D29)
val primaryContainerDark = Color(0xFF72333F)
val onPrimaryContainerDark = Color(0xFFFFD9DD)
val secondaryDark = Color(0xFFC9BFFF)
val onSecondaryDark = Color(0xFF31285F)
val secondaryContainerDark = Color(0xFF473F77)
val onSecondaryContainerDark = Color(0xFFE5DEFF)
val tertiaryDark = Color(0xFF82D5CA)
val onTertiaryDark = Color(0xFF003732)
val tertiaryContainerDark = Color(0xFF005049)
val onTertiaryContainerDark = Color(0xFF9EF2E6)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF191112)
val onBackgroundDark = Color(0xFFF0DEDF)
val surfaceDark = Color(0xFF171216)
val onSurfaceDark = Color(0xFFEBDFE6)
val surfaceVariantDark = Color(0xFF4E444B)
val onSurfaceVariantDark = Color(0xFFD1C2CB)
val outlineDark = Color(0xFF9A8D95)
val outlineVariantDark = Color(0xFF4E444B)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFEBDFE6)
val inverseOnSurfaceDark = Color(0xFF352E33)
val inversePrimaryDark = Color(0xFF8E4956)
val surfaceDimDark = Color(0xFF171216)
val surfaceBrightDark = Color(0xFF3E373C)
val surfaceContainerLowestDark = Color(0xFF120D11)
val surfaceContainerLowDark = Color(0xFF201A1E)
val surfaceContainerDark = Color(0xFF241E22)
val surfaceContainerHighDark = Color(0xFF2E282D)
val surfaceContainerHighestDark = Color(0xFF3A3338)

val DelColor = Color(0xFFC43838)
//val PinColor = Color(0xFFDFB84C)
val EditColor = Color(0xFF4CAF70)

val AmbientColorLight = Color(0xFF_84BA84)
val ChilledColorLight = Color(0xFF_88ACDB)
val FrozenColorLight  = Color(0xFF_B5B0B0)

val AmbientColorDark = Color(0xFF_524343)
val ChilledColorDark = Color(0xFF_3D4C59)
val FrozenColorDark  = Color(0xFF_4D4259)

@Immutable
data class CustomColorsPalette(
    val ambientColor: Color = Color.Unspecified,
    val chilledColor: Color = Color.Unspecified,
    val frozenColor: Color = Color.Unspecified
)

val LightCustomColorsPalette = CustomColorsPalette(
    ambientColor = AmbientColorLight,
    chilledColor = ChilledColorLight,
    frozenColor = FrozenColorLight
)

val DarkCustomColorsPalette = CustomColorsPalette(
    ambientColor = AmbientColorDark,
    chilledColor = ChilledColorDark,
    frozenColor = FrozenColorDark
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }