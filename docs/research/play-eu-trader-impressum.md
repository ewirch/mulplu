# EU DSA trader status on Google Play, and the German Impressum question

Research note for [#50](https://github.com/ewirch/mulplu/issues/50) (part of map
[#39](https://github.com/ewirch/mulplu/issues/39)). All sources fetched and verified on
**2026-08-25**. Google Help Center pages are undated; where a document carries a real date it is
given inline.

**Nothing here is legal advice.** Sections 4–6 are legal-adjacent and expressly flagged. A German
lawyer should sign off before anything is published under a real name and address.

---

## Bottom line

- **Google Play has no trader / non-trader declaration.** This is now confirmed from a **primary
  Google source**, not by absence of evidence: in Google's own published DSA audit reports for
  Google Play, Google Ireland Limited states that DSA Articles **30, 31 and 32 do not apply to
  Google Play at all**, because *"Google Commerce Limited (GCL) is the only trader on Google
  Play"*. No Article 30 duty means no trader-declaration flow. [1][2]
- The widely-circulating claim that "Google Play requires a trader status like Apple does" traces
  back to secondary blog posts that describe **Apple's** App Store Connect flow and then assert Play
  by analogy. No such assertion is sourced to a Google page. [11][12]
- **#40's finding stands unchanged and was re-verified today:** a personal, non-monetising account
  publishes **legal name, country and developer email** — not the street address. The full address
  is published only for **merchant** accounts (paid apps / IAP). That is EU/consumer-protection
  driven, but it is *not* a DSA trader mechanism. [3][4]
- **The real identity exposure for Mulplu is German, not European.** §5 DDG probably does **not**
  bite (no `geschäftsmäßig` use — no ads, no affiliate links, no monetisation). But **§18 Abs. 1
  MStV** does: it covers offerings that are *not commercial* yet go *beyond purely personal or
  family purposes*, and it demands **real first and last name (no pseudonym) plus a serviceable
  postal address (no PO box, no virtual office)**. [7][8]
- **Practical consequence for #45:** the postal address Google will *not* publish would, on the
  German reading, have to be published **by the developer himself** on the privacy-policy web page
  that Play mandates. That is the decision that needs making — not the trader question.

---

## 1. Does Play have a trader / non-trader declaration? — No

### 1.1 The primary source

Google publishes the independent DSA audit reports for Google Play (a designated Very Large Online
Platform) on its Transparency Report site. Each report opens with a *"Report of Management of Google
Ireland Limited"* listing, sub-article by sub-article, which DSA obligations management considers
applicable.

Both published Play audits carry the identical determination for the trader articles:

> "These sub articles are only applicable to providers of 'online platforms allowing consumers to
> conclude distance contracts with traders'. As Google Commerce Limited (GCL) is the only trader on
> Google Play, GIL's interpretation is that Google Play does not meet the regulatory definition.
> Therefore, the sub articles do not apply to Google Play."
> — for sub-articles **30.1–30.7, 31.1–31.3, 32.1, 32.2** [1] (identically in [2])

The audit body confirms the scoping: *"Section 4 — Additional provisions applicable to providers of
online platforms allowing consumers to conclude distance contracts with traders - **Not
applicable**"*. [1]

- **DSA Art. 30** is *Traceability of traders* — the obligation to collect name, address, phone,
  email from traders. **Art. 31** is the interface-design duty that lets traders supply it.
  **Art. 32** is the right to information. All three are declared inapplicable. [10]
- Report periods: 28 Aug 2023 – 31 May 2024 [2] and 1 Jun 2024 – 31 May 2025 [1], the latter signed
  by management on **27 Aug 2025** and audited by Ernst & Young. As of 2026-08-25 the 2024/25 report
  is the most recent Play audit published. [1]

**Reading of Google's argument.** Google's position is that on Play the *seller of record* is Google
Commerce Limited: the user contracts with Google, not with the developer. Play is therefore, in
Google's view, not a platform on which consumers conclude distance contracts *with traders* (plural,
third-party). Whether the Commission agrees is not something Google's own report can settle — the
text is careful to call it *"GIL's interpretation"*. [1]

### 1.2 Corroborating absence in the developer documentation

Every Play Console Help page that would have to carry a trader declaration if one existed was
checked on 2026-08-25. None mentions *trader*, *Digital Services Act* or *DSA*:

| Page | Result |
| --- | --- |
| Required information to create a Play Console developer account [3] | no trader/DSA mention |
| View and manage your developer account information [4] | no trader/DSA mention |
| Keeping your developer account information up to date [5] | no trader/DSA mention |
| Contact information requirements for developer accounts [6] | no trader/DSA mention |
| Play Console Requirements [13] | no trader/DSA mention |
| General conditions of access for Google Play in the EEA [14] | DMA only; no trader/DSA mention |
| Google Play Developer Program Policy (policy centre) [15] | no occurrence of trader / DSA |

### 1.3 What the conflicting secondary sources actually say

- A frequently linked tutorial ("Trader Status for Developer: DSA of EU — Tutorial 2026") asserts
  *"Developers must provide their trader status to submit new apps or app updates for distribution in
  the European Union (EU)"* and names Play alongside Apple — but gives **no Play Console location,
  no Play screenshot and no Google citation**. The sentence it uses is Apple's wording. [11]
- The Apple flow it is copied from is real and documented by Apple: trader status must be declared in
  App Store Connect, and once verified Apple publishes address, phone and email on the App Store
  product page in the 27 EU member states. [12]
- **Google's own trader declaration exists — but for the Chrome Web Store, not Play.** *"All
  developers on the Chrome Web Store are required to declare if they are a Trader or Non-Trader"*,
  and trader details *"will be posted publicly at the bottom of your extension listing"*. This is
  the page that gets mistaken for a Play requirement. [16]

The distinction is coherent: on the Chrome Web Store the developer is the counterparty; on Play,
Google Commerce Limited is.

### 1.4 Caveat on method

I could not inspect the **live Play Console signup UI** (no account, and the Console is behind
authentication). The finding therefore rests on Google's published DSA compliance position plus the
absence of any documentation, not on a screenshot of the account-creation flow. Given that Google
formally declares Art. 30 inapplicable in an audited filing, a hidden UI-only trader declaration
would be surprising — but see §7.

---

## 2. What a free hobby app would declare, and what becomes public

Since the declaration does not exist, there is nothing to declare. What Play *does* publish for a
personal account is unchanged from #40 and was re-verified today:

> "Google will display your **legal name**, your **country** (as per your legal address), and
> **developer email address** on Google Play. If you decide to monetize on Google Play then Google
> will display your **full address**." [3]

> "To comply with consumer protection laws, **merchant accounts** (developer accounts with apps that
> monetize via paid apps or in-app purchases) must show their full address on Google Play. The
> address used is the one taken from the Google payments profile linked to your Google Play developer
> account." [4]

| Field | Personal account, free app, no IAP |
| --- | --- |
| Developer name (free text, may be a pseudonym) | public |
| **Legal name** | **public — unavoidable** |
| Country | public |
| Developer email address | public |
| Full street address | **not public** (becomes public on any monetisation) [3][4] |
| Developer phone number | not required for personal accounts (public + mandatory for organisation accounts) [6] |
| Developer website | **not** required for personal accounts (organisation accounts only) [3] |
| Contact name / contact email / contact phone | Google-internal, never public [4] |
| Store-listing contact details (email required; phone, website optional) | public on the listing [6] |

Confirmations of detail:
- Personal accounts require developer name, legal name, legal address, contact email, contact phone,
  developer email. No website. [3]
- Organisation accounts *"provide a verified phone number and email address that is shown on Google
  Play as part of their public developer profile"*; for personal accounts only the email is public. [5]
- Germany is **not** among the regions with extra published-information requirements. The
  country-specific article names Japan (paid/IAP), Korea (individuals must supply a contact phone)
  and Brazil (merchants); the only EU-specific item is the geo-blocking prohibition. [17]

---

## 3. Interaction with #40

No conflict. #40 concluded that (a) a non-monetising personal account publishes only name, country
and developer email, and (b) no Play-specific DSA trader declaration could be found and Play seemed
to run consumer transparency through the **merchant-account** distinction instead. #50 upgrades (b)
from an inference to a documented Google position [1][2], and leaves (a) untouched.

Two refinements to #40:

1. #40 called the merchant-address rule a possible *"implementation"* of the DSA. It is better read
   as **EU consumer-protection law** (Consumer Rights / UCP-type identity duties for the seller
   side) than as DSA Art. 30 — Google's own filing denies Art. 30 applies to Play at all. The
   practical effect for Mulplu is identical either way.
2. The privacy-relevant decision #40 flagged is confirmed and slightly sharpened: **"stay free, no
   IAP" is what keeps the street address off Play.** Adding any paid item later flips the account to
   merchant status and publishes the home address from the payments profile. [3][4]

---

## 4. §5 DDG — does the Impressumspflicht apply? (not legal advice)

### 4.1 The statute

§5 DDG replaced §5 TMG when the Digitale-Dienste-Gesetz entered into force (**14 May 2024**); the
substance of the Impressum duty was carried over. [18][19]

§5 Abs. 1 DDG binds providers of *"geschäftsmäßige, in der Regel gegen Entgelt angebotene digitale
Dienste"* and requires, *leicht erkennbar, unmittelbar erreichbar und ständig verfügbar*: name and
address of establishment (plus legal form and representatives for legal persons), details enabling
*"schnelle elektronische Kontaktaufnahme und unmittelbare Kommunikation"*, and — where applicable —
supervisory authority, register and register number, chamber/professional details, VAT or economic
ID, and liquidation status. Nr. 3–8 are the conditional ones. [19]

A *"digitaler Dienst"* is defined by reference to Art. 1(1)(b) of Directive (EU) 2015/1535 — a
service normally provided for remuneration, at a distance, by electronic means, at the individual
request of a recipient. [20]

### 4.2 The `geschäftsmäßig` threshold

The clearest German-language statement of the test comes from a **state media regulator** — the
Medienanstalt Rheinland-Pfalz *Leitfaden zur Impressumspflicht in Telemedienangeboten*, Stand
06/2024. This is regulator guidance, i.e. the view of the body that would actually enforce it: [7]

> "Der Begriff der Geschäftsmäßigkeit ist hierbei nicht mit 'gewerblich' gleichzusetzen.
> Geschäftsmäßig sind vielmehr Angebote, die *nachhaltig* mit oder ohne Gewinnerzielungsabsicht
> betrieben werden." [7]

Its indicators for `geschäftsmäßig` use are: advertising banners and ads, affiliate links,
advertising cooperations, presentation of goods for consideration, a "business enquiries" email
address, monetisation tools, participation in platform partner programmes. [7]

**Applied to Mulplu: none of the seven indicators is present.** No ads, no affiliate links, no IAP,
no partner programme, no goods, no business contact address. Sustained operation alone is present
(the app is maintained over time), so the point is arguable, but on the regulator's own indicator
list a free, ad-free, non-monetised learning app for one's own child does not read as
`geschäftsmäßig`. Secondary commentary agrees on the flip side of the test: *"auch bei kostenfreien
Apps kann ein Impressum nötig sein. Binden Sie Werbebanner oder Affiliate-Links ein oder analysieren
Nutzerdaten, ist ebenfalls eine Geschäftsmäßigkeit gegeben"* — free is not the safe harbour, the
absence of monetisation and tracking is. [21]

### 4.3 The provision that does bite: §18 Abs. 1 MStV

This is the finding that matters, and it is easy to miss because it lives in state media law rather
than in the DDG.

> "Anbieter von Telemedien, die **nicht ausschließlich persönlichen oder familiären Zwecken dienen**,
> haben folgende Informationen leicht erkennbar, unmittelbar erreichbar und ständig verfügbar zu
> halten: 1. Name und Anschrift sowie 2. bei juristischen Personen auch Name und Anschrift des
> Vertretungsberechtigten."
> — § 18 Abs. 1 MStV [8]

The regulator's guidance is explicit that this catches the non-commercial-but-public case, i.e.
exactly the gap left by §5 DDG:

> "Online-Angebote, die **nicht geschäftlich genutzt** werden und über eine rein persönliche oder
> familiäre Nutzung **hinausgehen, unterliegen bereits der Impressumspflicht**." [7]

Required under this "einfache Impressumspflicht": **Vor- und Nachname (keine Pseudonyme)** and
**Anschrift — explicitly *not* a Postfachadresse, Packstation or virtual office**. [7]

And on when an offering counts as *öffentlichkeitsgerichtet* rather than personal/family: [7]

> "Ein Impressum ist immer dann erforderlich, wenn es sich um ein an die Öffentlichkeit gerichtetes
> Angebot handelt." Indicators: large number of users, intent to generate subscribers, a
> "Person des öffentlichen Lebens" framing. Personal/family character is indicated by
> **restricted accessibility** ("Das Angebot steht nicht für jedermann offen") and by content drawn
> exclusively from the personal or family sphere. [7]

A world-readable Google Play listing is the definition of *"steht für jedermann offen"*. The
private-purpose exemption is therefore not available once the app is published on Play, even though
the app was written for one child. Commentary states this bluntly: *"Die Befreiung greift jedoch in
der Praxis fast nie, da eine Verfügbarkeit im App-Store von den Gerichten regelmäßig als öffentliches
Angebot gewertet wird, welches den privaten Charakter ausschließt."* [21] — asserted as settled case
law but **without a case citation**; see §7.

### 4.4 Sanction exposure, and who can act

- Breach of the Impressum rules is an **Ordnungswidrigkeit**, fineable up to **€50,000**
  (§ 115 Abs. 1 S. 2 Nr. 1, Abs. 2 MStV; § 33 Abs. 2 Nr. 1, Abs. 6 DDG). [7]
- **The Abmahnung risk is structurally different for a non-commercial app.** The German
  Abmahnung industry around Impressum breaches runs on UWG — which needs a *Mitbewerber* (a
  competitor in a commercial relationship). If the app is genuinely non-`geschäftsmäßig`, there is no
  commercial act and no competitor, so the classic cease-and-desist letter has no hook; what remains
  is the regulatory fine route via the Landesmedienanstalt. Secondary German sources nonetheless
  report an active Abmahnung practice specifically for missing Impressum in the Play Store — those
  cases concern commercial apps. [22][23]
- This inference (no UWG hook without commerciality) is **mine, not a sourced legal opinion**. It is
  the kind of point a lawyer should confirm.

### 4.5 Where the Impressum would have to sit

Regulator requirements for placement, applying to both MStV and DDG: *leicht erkennbar* (visible
position, clearly labelled "Impressum" / "Anbieterkennzeichnung"; **not** buried inside the privacy
policy or the T&Cs, not requiring long scrolling), *unmittelbar erreichbar* (no material
intermediate steps; the "max. 2 clicks" rule of thumb is stated as applying to social-media profiles
specifically), *ständig verfügbar* (available on every distribution path of the offering, e.g.
mobile; not placed in graphics or pop-ups). [7]

Commentary on apps specifically says both locations: *"Aber nicht nur in der App, sondern auch im App
Store muss das Impressum hinterlegt werden"*, with the reasoning that users should be able to see it
**before** downloading. [21]

**Cheap fit with existing obligations.** #42 already established that a child-directed app needs a
**bundled, offline, in-app privacy policy**. Adding an Impressum block to that same in-app screen is
near-zero extra work. The Play side can be served by the store-listing website field pointing at the
hosted policy/Impressum page.

### 4.6 Nuance worth naming: is a fully offline app itself a "digitaler Dienst" / "Telemedium"?

Mulplu has no `INTERNET` permission and no network code. Both the DDG definition (via Directive
2015/1535: *at a distance, by electronic means, at the individual request of a recipient*) [20] and
the MStV notion of *Telemedien* (electronic information and communication services) are framed
around a service delivered over a network. A purely local binary is arguably not itself such a
service; what unambiguously **is** one is (a) the **Play Store listing page** for the app and (b) the
**privacy-policy web page**. General commentary counts apps among electronic information and
communication services without addressing the offline case. [24]

Practically this does not change the answer — the two things that are unambiguously covered are
exactly the two public-facing surfaces — but it is the honest state of the analysis, and it means the
duty attaches to the *offer*, not to the APK.

---

## 5. The privacy-policy web page (not legal advice)

Play requires a publicly reachable privacy-policy URL, and #42 additionally requires it bundled in
the app. The hosted page is a website operated by the developer, so:

- **§18 Abs. 1 MStV applies to it** on the same reasoning as above: it is a Telemedium that does not
  serve exclusively personal or family purposes. Consequence: **real name + serviceable postal
  address, no PO box, no pseudonym**, in a separately labelled and directly reachable Impressum. [7][8]
- **§5 DDG** applies only if the page is `geschäftsmäßig` — same analysis as §4.2; on the regulator's
  indicators, a policy page with no ads, no affiliate links and no tracking is not. [7][19]
- **GDPR is a separate, independent duty.** Art. 13(1)(a) GDPR requires *"the identity and the
  contact details of the controller"* to be given to data subjects. [25] The **app** processes no
  personal data at all (#42), so the app-side duty is thin; but the **hosted page** does — a web
  server processes IP addresses and log data — so the page's own policy has to name a controller.
  Whether "identity and contact details" requires a postal address, or whether name + email
  suffices, is contested; the safe reading, and the one the §18 MStV analysis forces anyway, is
  name + address.

**So the postal address that Google will not publish is the one German law would have the developer
publish himself.** That is the decision-relevant tension for #45.

Options, with the same honesty as #40's address-hiding ranking:

1. **Publish home name + address on the policy/Impressum page.** Legally cleanest; maximum exposure.
   Note that the **legal name is already public via Play regardless** [3], so the marginal
   disclosure is the street address, not the identity.
2. **c/o or lawyer/service address.** The regulator's guidance rules out *Postfach, Packstation,
   virtual office* [7]; a genuine service address at a third party (lawyer, association) is a
   different animal but requires that third party's cooperation. Not researched further. Note this
   is **not** the same trap as #40's warning about c/o addresses — that warning was about the Google
   *payments profile*, which must match ID documents; the Impressum address is a separate
   publication and need not match the payments profile.
3. **Register a small entity (e.V. / UG)** so the published address is the entity's. Publishes name
   and address of the *Vertretungsberechtigter* anyway under §18 Abs. 1 Nr. 2 MStV [8], and #40
   already ruled out an organisation Play account for other reasons.
4. **Rely on the personal/family exemption.** Untenable once the app is world-readable on Play [7],
   and it is the option that would fail hardest if challenged.
5. **Publish nothing and accept the residual risk.** Exposure is the Ordnungswidrigkeit route via
   the Landesmedienanstalt, not (on my reading, §4.4) a competitor Abmahnung. This is a risk
   decision for the dev, not a research finding.

---

## 6. Decision-relevant summary for #45 (developer account identity)

1. **No trader declaration exists on Play.** Do not budget time or a decision for it. If the Console
   ever presents one, it is new and undocumented — see §7. [1][2]
2. **Legal name is public no matter what.** A pseudonymous developer name does not hide it. [3]
3. **Street address stays off Play as long as the app is free with no IAP** — re-verified. Treat
   monetisation as an identity decision, not a product one. [3][4]
4. **The address question does not go away, it moves.** German §18 Abs. 1 MStV points at the
   privacy-policy page that Play forces the project to publish anyway. This is the open item; it
   belongs in #45 or in the privacy-policy ticket, and it needs the dev's own risk call (and ideally
   a lawyer's).
5. **No German-specific extra publication is demanded by Play itself** — Germany is not in the
   country-specific list. [17]
6. **Cheap consolidation:** one in-app screen can carry the offline privacy policy (#42
   requirement) *and* the Impressum block; the store listing's website field can point at the hosted
   copy of the same. [7][21]

---

## 7. Unresolved / could not confirm

- **The live Play Console signup and "App content" flows were not inspected.** No Console access.
  The no-trader-declaration finding rests on Google's audited DSA filing [1][2] plus documented
  absence [3][4][5][6][13][14][15], not on a screenshot of the UI. If the orchestrating session ever
  has Console access, one look at *Account details* / *App content* would close this for good.
- **Whether the European Commission accepts Google's Art. 30 scoping.** The audit text calls it
  *"GIL's interpretation"* [1]. No Commission decision, DSC finding or litigation on the point was
  found. If it is ever rejected, Play would have to build a trader flow, and free non-monetising
  developers would presumably declare **non-trader** (as on the Chrome Web Store [16]) — which
  publishes nothing extra. So the downside of being wrong here looks small.
- **Whether an offline-only APK is itself a "digitaler Dienst" / "Telemedium".** Nothing found that
  addresses the no-network case directly (§4.6). Immaterial in practice, because the store listing
  and the policy page are covered regardless.
- **No German case law located** on the Impressumspflicht of a genuinely non-commercial, free,
  ad-free app or of a private hobby website. Commentary asserts that courts "regularly" treat app
  store availability as a public offering [21] but cites no decision for it; the one decision cited
  on that page (BGH 20 Jul 2006, I ZR 228/03) concerns permissible *labelling* of an Impressum, not
  scope. The §18 MStV conclusion therefore rests on statutory text [8] plus regulator guidance [7],
  which is strong but not court-tested for this fact pattern.
- **My inference that the absence of commerciality removes the UWG/Abmahnung hook** (§4.4) is
  reasoning, not a sourced legal opinion.
- **Whether GDPR Art. 13(1)(a) requires a postal address** or accepts name + email is contested and
  was not resolved; moot here because §18 MStV demands the address anyway.
- **The German privacy/Impressum package as a whole has not had legal review.** #42 already flagged
  this; #50 does not change it.

---

## 8. Sources

Primary — Google
1. *Independent Audit on Google Play – Digital Services Act*, period 1 Jun 2024 – 31 May 2025,
   management report dated 27 Aug 2025, auditor Ernst & Young LLP. Google Transparency Report.
   https://storage.googleapis.com/transparencyreport/report-downloads/dsa-audit-g-play_2024-6-1_2025-5-31_en_v1.pdf
2. *Independent Audit on Google Play*, period 28 Aug 2023 – 31 May 2024. Google Transparency Report.
   https://storage.googleapis.com/transparencyreport/report-downloads/dsa-audit-google-play_2023-8-28_2024-5-31_en_v1.pdf
3. *Required information to create a Play Console developer account* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/13628312
4. *View and manage your developer account information (for Play Console Requirements-verified
   accounts)* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/13634081
5. *Keeping your developer account information up to date* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/13634888
6. *Contact information requirements for developer accounts* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/10840893
13. *Play Console Requirements* — Play Console Help.
    https://support.google.com/googleplay/android-developer/answer/10788890
14. *General conditions of access for Google Play in the EEA* — Play Console Help.
    https://support.google.com/googleplay/android-developer/answer/14659200
15. *Google Play Developer Program Policy* — Play policy centre.
    https://play.google/developer-content-policy/
16. *Trader FAQ: Chrome Web Store* — Chrome for Developers (Chrome Web Store program policies).
    https://developer.chrome.com/docs/webstore/program-policies/trader-verification-faq
17. *Requirements for distributing apps in specific countries/regions* — Play Console Help.
    https://support.google.com/googleplay/android-developer/answer/6223646

Primary — law and regulators
7. *Leitfaden zur Impressumspflicht in Telemedienangeboten*, Medienanstalt Rheinland-Pfalz,
   Stand 06/2024 (state media regulator guidance).
   https://medienanstalt-rlp.de/fileadmin/dateien/medienvielfalt/Medienregulierung/Merkblaetter_intern/Leitfaden_Impressum.pdf
8. § 18 Abs. 1 MStV — Medienstaatsvertrag (consolidated text published by die medienanstalten).
   https://www.die-medienanstalten.de/fileadmin/user_upload/Rechtsgrundlagen/Gesetze_Staatsvertraege/Medienstaatsvertrag_MStV.pdf
10. DSA Art. 30 *Traceability of traders*, Regulation (EU) 2022/2065.
    https://www.eu-digital-services-act.com/Digital_Services_Act_Article_30.html
18. Digitale-Dienste-Gesetz (DDG) — full text, gesetze-im-internet.de.
    https://www.gesetze-im-internet.de/ddg/
19. § 5 DDG *Allgemeine Informationspflichten* — gesetze-im-internet.de.
    https://www.gesetze-im-internet.de/ddg/__5.html
20. § 1 DDG (scope and definitions; *digitaler Dienst* via Directive (EU) 2015/1535) —
    gesetze-im-internet.de.
    https://www.gesetze-im-internet.de/ddg/__1.html
25. Art. 13 GDPR *Information to be provided where personal data are collected from the data
    subject*. https://gdpr-info.eu/art-13-gdpr/

Primary — Apple (for contrast)
12. *Manage European Union Digital Services Act trader requirements* — App Store Connect Help.
    https://developer.apple.com/help/app-store-connect/manage-compliance-information/manage-european-union-digital-services-act-trader-requirements/

Secondary (clearly marked; German legal commentary and blogs)
11. *Trader Status for Developer: DSA of EU — Tutorial 2026*, makaka.org. The source of the
    "Play needs a trader status" claim. Unsourced as to Play.
    https://makaka.org/unity-tutorials/trader-status
21. *Achtung App Anbieter: Fehlendes Impressum in App Stores wird abgemahnt*, e-recht24.
    https://www.e-recht24.de/impressum/10176-app-impressum.html
22. *Vorsicht, Abmahngefahr: Auch im Google Play Store ist ein Impressum nötig!*, Online.Spiele.Recht
    (spielerecht.de). Referenced from search results; the site's TLS certificate had expired on
    2026-08-25 and the page could not be fetched, so it is cited for its existence only.
    https://www.spielerecht.de/vorsicht-abmahngefahr-auch-im-google-play-store-ist-ein-impressum-noetig/
23. *Rechtliche Fallstricke bei Online-Verkäufen über Apps*, IT-Recht Kanzlei. Surfaced in search
    but not fetched; listed for follow-up, not relied on above.
    https://www.it-recht-kanzlei.de/online-verkauf-apps-rechtliche-fallstricke.html
24. *§ 18 Abs. 1 MStV — Das Impressum und der Verantwortliche*, RESMEDIA.
    https://www.res-media.net/18-mstv-das-impressum-und-der-verantwortliche/
