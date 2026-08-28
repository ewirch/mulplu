package com.mulplu.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mulplu.app.BuildConfig
import com.mulplu.app.R

/** Legal-screen colours; `Muted` matches the map's labels (#51). */
private object LegalColors {
    val Ink = Color(0xFF2B3A4A)
    val Muted = Color(0xFF5B6B7C)
}

/**
 * The legal screen (#51) — Impressum, privacy, licence, version, in that
 * order. Reached from the map's footer line; the GPL text itself sits one
 * level below on [LicenseScreen].
 *
 * The texts are hardcoded German like every other string in this app. They are
 * word-for-word identical to the hosted privacy page Play requires, so the two
 * must be edited together.
 */
@Composable
fun LegalScreen(onBack: () -> Unit, onShowLicense: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        BackLink(onBack)
        Spacer(Modifier.height(20.dp))

        // No postal address, by the decision in #45: Play publishes none while
        // the app is free and IAP-free, and § 18 Abs. 1 MStV — which does want
        // a serviceable one — is knowingly accepted as a residual risk for a
        // single-family app. Revisit if any of these change: monetisation of
        // any kind, contact from an authority, or use beyond the family device.
        // The norm is deliberately not cited above: quoting it while leaving
        // out the address it demands would only make the gap explicit.
        Heading("Impressum")
        Body("Eduard Wirch\nmulplu@proton.me")
        Spacer(Modifier.height(28.dp))

        Heading("Datenschutz")
        Body(PRIVACY_TEXT)
        Spacer(Modifier.height(28.dp))

        Heading("Lizenz")
        Body(
            "Mulplu ist freie Software und steht unter der GNU General Public " +
                "License, Version 3 oder später.\n\n" +
                "Der vollständige Quellcode dieser Version:\n" +
                SOURCE_URL,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Lizenztext anzeigen",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MulpluColors.AccentBlue,
            modifier = Modifier
                .clickable(onClick = onShowLicense)
                .padding(vertical = 8.dp),
        )
        Spacer(Modifier.height(28.dp))

        Text(
            text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            fontSize = 12.sp,
            color = LegalColors.Muted,
        )
    }
}

/**
 * The GPL text, verbatim from `res/raw/gpl_3_0.txt` — GPLv3 §4 via §6 wants a
 * copy inside the app (#43). English only: the FSF recognises no translation
 * as legally valid. Split into blocks so a 35 KB text is not one Compose node
 * on the Android 9 device this ships to (#52).
 */
@Composable
fun LicenseScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val blocks = remember {
        context.resources.openRawResource(R.raw.gpl_3_0)
            .bufferedReader()
            .use { it.readText() }
            .split("\n\n")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp),
    ) {
        BackLink(onBack)
        Spacer(Modifier.height(20.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(blocks) { block ->
                Text(
                    text = block,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    color = LegalColors.Ink,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
        }
    }
}

/**
 * The visible way out. System back does the same thing, but a child who lands
 * here by a mis-tap should not have to know that (#51).
 */
@Composable
private fun BackLink(onBack: () -> Unit) {
    Text(
        text = "← Zurück",
        fontSize = 16.sp,
        color = MulpluColors.AccentBlue,
        modifier = Modifier
            .clickable(onClick = onBack)
            .padding(vertical = 8.dp, horizontal = 4.dp),
    )
}

@Composable
private fun Heading(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = LegalColors.Ink,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun Body(text: String) {
    Text(text = text, fontSize = 14.sp, lineHeight = 20.sp, color = LegalColors.Ink)
}

/**
 * The tag, not the branch: GPLv3 §6(d) wants the source corresponding to *this*
 * binary, and `main` moves on. The tag scheme is `v<versionName>` (#46), so the
 * URL cannot go stale — but the tag has to exist before a rollout, or the
 * shipped app points at a 404.
 *
 * Plain text, not a link: user 10 on the child device has no browser installed
 * (#44), so an ACTION_VIEW intent would have no receiver. §6(d) wants access,
 * not a tap.
 */
private val SOURCE_URL = "github.com/ewirch/mulplu/releases/tag/v${BuildConfig.VERSION_NAME}"

private const val PRIVACY_TEXT =
    """Mulplu erhebt keine Daten.

Die App besitzt keine Berechtigung, auf das Internet zuzugreifen. Sie überträgt nichts. Sie enthält keine Werbung, keine Analyse- oder Tracking-Dienste und keine Programmbausteine von Drittanbietern. Es gibt kein Benutzerkonto und keine Anmeldung.

Der Lernfortschritt – welche Aufgaben geübt wurden und wie sicher sie sitzen – wird ausschließlich auf diesem Gerät gespeichert, im privaten Speicherbereich der App. Er enthält keinen Namen, kein Alter und keine Gerätekennung. Er wird weder an den Anbieter noch an Dritte übermittelt und ist von der Datensicherung des Geräts ausgenommen.

Die App richtet sich an Kinder. Auch deshalb ist sie so gebaut, dass gar nicht erst Daten entstehen, die geschützt werden müssten.

Wer die App deinstalliert, löscht damit den gesamten Lernfortschritt. Einen anderen Weg, ihn zu sichern oder zu übertragen, gibt es nicht.

Fragen zum Datenschutz: mulplu@proton.me

Stand: August 2026"""
