package br.com.infoplus.infoplus.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta INFO+ (Violeta como identidade)
 *
 * Heurísticas atendidas:
 * - Identidade do projeto: VIOLETA (primary)
 * - Seriedade institucional sem "police-state look": Navy em surfaces/containers (apoio, não dominante)
 * - Tema escuro com contraste e conforto visual
 * - Semântica de status: sucesso/aviso para sincronização offline-first
 */

// ===== Brand =====
// Violeta (identidade)
val InfoPrimary = Color(0xFF451962)        // violeta institucional (já era seu rumo)
val InfoPrimaryDark = Color(0xFF35307A)    // violeta mais profundo (dark/topbar)

// Navy (apoio institucional / credibilidade)
val InfoNavy = Color(0xFF0B1F3B)
val InfoNavyDeep = Color(0xFF08162B)

// Acentos de acolhimento
val InfoSecondary = Color(0xFF7C6DFF)      // violeta vivo (ações/realce)
val InfoTertiary = Color(0xFFB7AEFF)       // lavanda suave (apoio/realce leve)

// ===== Light =====
val InfoBgLight = Color(0xFFFBFAFF)
val InfoSurfaceLight = Color(0xFFFFFFFF)
val InfoSurfaceVariantLight = Color(0xFFF3F1FF)     // leve lavanda
val InfoOnLight = Color(0xFF14141A)
val InfoOnLightMuted = Color(0xFF4B4B5A)

// Containers (chips/badges com boa hierarquia)
val InfoPrimaryContainerLight = Color(0xFFE7E4FF)   // lavanda clara
val InfoOnPrimaryContainerLight = Color(0xFF2C2468)

val InfoSecondaryContainerLight = Color(0xFFE6EEF9) // navy tint (institucional)
val InfoOnSecondaryContainerLight = Color(0xFF0B1F3B)

// ===== Dark =====
val InfoBgDark = Color(0xFF0F0F16)                  // escuro confortável (não preto puro)
val InfoSurfaceDark = Color(0xFF161622)
val InfoSurfaceVariantDark = Color(0xFF1F1F30)
val InfoOnDark = Color(0xFFEAEAF2)
val InfoOnDarkMuted = Color(0xFFB9B9C9)

val InfoPrimaryContainerDark = Color(0xFF2A2758)    // violeta “container”
val InfoOnPrimaryContainerDark = Color(0xFFEAEAF2)

val InfoSecondaryContainerDark = Color(0xFF14233E)  // navy container (seriedade)
val InfoOnSecondaryContainerDark = Color(0xFFEAF0FF)

// ===== Feedback / Semânticas =====
val InfoError = Color(0xFFB3261E)
val InfoSuccess = Color(0xFF1B9E77)   // "Sincronizado"
val InfoWarning = Color(0xFFF59E0B)   // "Salvo localmente / Pendente"

val InfoOutlineLight = Color(0xFFD3D2E6)
val InfoOutlineDark = Color(0xFF3A3A4A)
