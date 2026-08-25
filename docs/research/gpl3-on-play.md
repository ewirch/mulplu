# GPL-3.0 and Google Play distribution: compatible?

Research note for [#43](https://github.com/ewirch/mulplu/issues/43), part of the map [#39](https://github.com/ewirch/mulplu/issues/39).

Researched and written **2026-08-25**. All source dates given below are the dates stated by the source itself or the date this note verified them.

> **Not legal advice.** This note reports positions, primary texts and their dates. It does not resolve them. Where the answer is genuinely unsettled, that is said explicitly.

## Short answer

Nothing blocks it in practice, and the parts that are formally clean are clean for reasons that can be checked against the license text:

- The GPLv3 **anti-tivoization** clause does not apply to an app published on Play. It is triggered only by conveying object code *in a User Product as part of a transaction transferring the device*. Publishing an app is not that transaction.
- **Play App Signing** (Google holds the app signing key) does **not** create a GPL problem. The FSF's own FAQ says the GPL forces disclosure of signing keys only in the tivoization case above.
- The **source-availability** obligation is easy to satisfy via GPLv3 §6(d): a public repository, with the exact source corresponding to each published binary, plus clear directions from the binary to the source.
- The one **genuinely unsettled** point is the same one the FSF raised against Apple in 2010/2011: the store's *user-facing* terms restrict copying/redistribution of "Content", while the GPL grants those rights. Google Play's Terms of Service contain such a clause. Whether that is a GPL §10/§12 problem, and whose problem it is (Google's or the developer's), has never been litigated and the FSF has never publicly taken the position against Google that it took against Apple.

For Mulplu specifically, the actionable consequences are in [What this means for Mulplu](#what-this-means-for-mulplu).

## 1. The historical friction: Apple, not Google

- **2010-05-25** — FSF, ["GPL Enforcement in Apple's App Store"](https://www.fsf.org/news/2010-05-app-store-compliance): an iPhone port of GNU Go was on the App Store; the FSF's stated objection was that "Apple imposes numerous legal restrictions on use and distribution of GNU Go through the iTunes Store Terms of Service, which is forbidden by section 6 of GPLv2." Apple removed the app rather than change its terms (per the follow-up note appended to the same page).
- **2010-10-29** — FSF, ["VLC developer takes a stand against DRM enforcement in Apple's App Store"](https://www.fsf.org/blogs/licensing/vlc-enforcement): quotes VLC developer Rémi Denis-Courmont writing that the GPL's terms "are contradicted by the products usage rules of the AppStore". The FSF's framing: the GPL gave Apple permission to distribute; Apple preferred its own "Usage Rules" and DRM. VLC was subsequently pulled from the iOS App Store (2011).
- The mechanism was Apple's **Usage Rules** — a closed list of permitted activities, including a device-count limit — which the FSF characterised as doing legally what DRM does technically.

**No equivalent enforcement action or public statement exists against Google Play.** Searches of gnu.org and fsf.org surfaced no FSF or GNU statement singling out Google Play as GPL-incompatible (checked 2026-08-25). The GPL FAQ (gnu.org/licenses/gpl-faq.html) has no "app store" entry at all.

## 2. What the current Google terms actually say

Two distinct documents matter, and conflating them is the usual source of confusion.

### 2.1 Developer Distribution Agreement (developer ↔ Google)

[Google Play Developer Distribution Agreement](https://play.google.com/intl/en_us/about/developer-distribution-agreement.html), **effective as of 15 September 2025** (fetched 2026-08-25).

- **§5.1** — the developer authorizes Google, non-exclusively and royalty-free, to "reproduce, perform, display, analyze, and use" the Product for operating and marketing Play, hosting, platform improvement, and compliance checking. Sub-licensable to "application security partners" for compliance checking only. Granting this is well within what GPL-3.0 already permits any distributor to do, so the developer is not granting rights he does not have.
- **§5.3** — "You grant to the user a nonexclusive, worldwide, and perpetual license to perform, modify color of, or add themes to, your Product icons, display … and use the Product." It then says the developer *may* attach a separate EULA governing the user's rights, "but, to the extent that EULA conflicts with this Agreement, this Agreement will supersede the EULA."
  - The supersession clause is the one people worry about. Note what it actually does: §5.3 sets a **floor** of rights the user gets. The GPL grants strictly *more* than that floor (copy, modify, redistribute). Granting more is not obviously a "conflict"; a conflict would be a EULA granting *less* than §5.3 requires. This reading is not authoritative — it is the plain-text reading, and no ruling or Google statement confirms it.
- The DDA contains **no clause about open source licences**, no DRM/copy-protection mandate, and no clause requiring the developer to forbid redistribution (grep for `open source`, `DRM`, `copy protection`, `reverse engineer` over the full text returns nothing, 2026-08-25).

### 2.2 Play Terms of Service (user ↔ Google) — the actual grey area

[Google Play Terms of Service](https://play.google.com/intl/en_us/about/play-terms/index.html), US version dated **29 July 2026** (fetched 2026-08-25). Section 4, "Rights and Restrictions":

- The user gets "the non-exclusive right, solely as expressly permitted in these Terms and associated policies, to store, access, view, use, and display copies of the applicable Content on your Devices … for your personal, non-commercial use only."
- Restrictions list: the user may not "sell, rent, lease, redistribute, broadcast, transmit, communicate, modify, sublicense, transfer, assign any Content to any third party … **except as expressly authorized and only in the exact manner provided**." (emphasis added)
- Same section: "Your use of apps and games may be governed by the additional terms and conditions of the end user license agreement between you and the Provider."

This is structurally the same shape of clause that the FSF objected to at Apple: a store telling the user they may not redistribute or modify, over software whose licence says they may.

**Arguments that it is not a problem** (none authoritative):
1. The carve-out "except as expressly authorized" arguably lets the GPL's own grant through — the GPL *is* express authorization from the rights holder. Apple's 2010 Usage Rules were read by the FSF as a closed list without such a general escape hatch.
2. The sentence pointing at the Provider's EULA acknowledges that the developer's own licence governs the app.
3. GPLv3 **§7** ("Additional Terms"): "If the Program as you received it, or any part of it, contains a notice stating that it is governed by this License along with a term that is a further restriction, you may remove that term." The App Fair Project relies on this — [The GPL and Commercial App Stores: Time for a Reconsideration](https://appfair.org/blog/gpl-and-the-app-stores/), 2025-03-19 — arguing the stores "can slap whatever GPL-violating terms and conditions they want" and users may ignore them. **Caveat:** §7's text is about a further-restriction term *carried in the Program's own notices*, not about a distributor's separate contract with the user. Whether §7 reaches a store ToS is exactly the kind of question that has never been tested.

**Arguments that it is a problem** (none authoritative either):
1. GPLv3 **§10**: "You may not impose any further restrictions on the exercise of the rights granted or affirmed under this License." Google is itself a conveyor of the binary to the user, and Play ToS §4 imposes restrictions on that user.
2. GPLv3 **§12** ("No Surrender of Others' Freedom"): conditions imposed by agreement that contradict the licence do not excuse compliance; if the developer cannot satisfy both, "you may not convey it at all."
3. The App Fair article itself concedes that "the Google Play Store also has nearly identical policies" to Apple's, and attributes the difference in perception to the FSF's differing posture toward the two companies, not to a difference in the terms.

**Status: unsettled.** No case law, no FSF enforcement action against Google, no Google statement. The practical position of the ecosystem — thousands of GPL apps on Play, over 15 years, with no enforcement — is evidence of tolerance, not of legal resolution.

## 3. Anti-tivoization (§6) and Play App Signing

### 3.1 Does §6 Installation Information apply?

[GPL-3.0 text](https://www.gnu.org/licenses/gpl-3.0.html), §6. The Installation Information requirement is conditional:

> "If you convey an object code work under this section in, or with, or specifically for use in, a **User Product**, and the conveying occurs as part of a transaction in which the right of possession and use of the User Product is transferred to the recipient in perpetuity or for a fixed term …"

Publishing an app on Play is not a transaction transferring possession of a phone. The trigger does not fire. The GPL FAQ ([What is tivoization?](https://www.gnu.org/licenses/gpl-faq.html#Tivoization), gnu.org, checked 2026-08-25) frames it the same way: the obligation lands on "people [who] distribute User Products that include software under GPLv3."

A locked bootloader on the child's Android device is a question about **the device vendor's** GPL obligations for the software *it* ships, not about an app developer publishing to a store. Cleanly outside this ticket.

### 3.2 Play App Signing and the signing key

- [Android developer docs, "Sign your app"](https://developer.android.com/studio/publish/app-signing) (fetched 2026-08-25): "With Play App Signing, Google manages and protects your app's signing key". The developer keeps a separate **upload key**; Google verifies the upload and re-signs distributed APKs with the app signing key. Play App Signing "is required to sign your app for distribution through Google Play (except for apps created before August 2021, which may continue distributing self-signed APKs)." A new app therefore has no opt-out.
- GPL FAQ, [*"Is it true that GPLv3 forces me to release my private signing keys?"*](https://www.gnu.org/licenses/gpl-faq.html#GiveUpKeys) (checked 2026-08-25): **"No.** The only time you would be required to release signing keys is if you conveyed GPLed software inside a User Product, and its hardware checked the software for a valid cryptographic signature before it would function."

So: Google holding the app signing key is **not** a GPL-3.0 problem. It is not "Corresponding Source", not "Installation Information", and §6 is not triggered. Related FAQ entries confirm the boundary is the device, not the store: [#TwoPartyTivoization](https://www.gnu.org/licenses/gpl-faq.html#TwoPartyTivoization) (two companies splitting signing and hardware-lock roles is still a violation) and [#RemoteAttestation](https://www.gnu.org/licenses/gpl-faq.html#RemoteAttestation) — both are about User Products, i.e. devices.

A practical, non-legal consequence remains: because Google re-signs, a user rebuilding the published source cannot produce a byte-identical, same-signature APK. That is a reproducibility/verifiability limitation (the reason F-Droid maintains its own build+signing pipeline), not a licence violation.

## 4. What GPL-3.0 actually obliges when distributing via Play

### 4.1 Source availability — §6(d) is the fit

GPLv3 §6(d): convey the object code "by offering access from a designated place (gratis or for a charge), and offer equivalent access to the Corresponding Source in the same way through the same place at no further charge." The same paragraph explicitly allows the source to sit on a *different* server, "provided you maintain clear directions next to the object code saying where to find the Corresponding Source", and puts the obligation on the developer to keep it available.

FSF FAQ confirms the shape:
- [#AnonFTPAndSendSources](https://www.gnu.org/licenses/gpl-faq.html#AnonFTPAndSendSources): binaries on a network server ⇒ Corresponding Source on a network server. Another server or a VCS is fine. "the source should be just as easy to access as the object code" and "The sources you provide must correspond exactly to the binaries."
- [#SourceInCVS](https://www.gnu.org/licenses/gpl-faq.html#SourceInCVS): a link to a version-control repository is acceptable "as long as the source checkout process does not become burdensome", and users must get "clear and convenient instructions for how to get the source for the exact object code they downloaded — they may not necessarily want the latest development code".

⇒ A public GitHub repository satisfies §6(d), **provided** each published build is reachable as an exact, identifiable revision (a tag per release), and the Play listing points at it.

### 4.2 Licence text and notices

GPLv3 §6 conveys object code "under the terms of sections 4 and 5". §4 requires that you "conspicuously and appropriately publish on each copy an appropriate copyright notice; keep intact all notices …; and **give all recipients a copy of this License along with the Program**."

⇒ The full GPL-3.0 text must reach the user *with the app*, not only in the repo. In Android practice this means bundling the licence and surfacing it (an About/Licence screen, or an entry in the app's settings).

### 4.3 The Play listing itself

- **Nothing in the DDA or in Google Play policy requires stating the app's licence in the store listing** (DDA full-text grep, 2026-08-25; no such requirement found in Play developer policy). Naming the licence and linking the source in the listing description is a convention, and a convenient way to discharge the §6(d) "clear directions" duty, but Play does not demand it.
- Conversely, no Play policy forbids open-source or GPL apps. No documented case of Play removing an app *because* it was GPL-licensed was found.

### 4.4 Second-order consequence worth knowing

GPL-3.0 means anyone may take the published source, rebuild it, and republish it — including on Play. Google's counterweight is its [Impersonation policy](https://support.google.com/googleplay/android-developer/answer/9888374?hl=en) (checked 2026-08-25), which restricts deceptively similar names/icons/developer identities, not re-publication as such. The App Fair article (2025-03-19) makes the flip-side argument: copyleft is what gives the original author leverage against ad-injected repackagings, since the repackager must also publish source.

## 5. Are GPL apps in fact on Play?

Verified 2026-08-25 by checking the Play listing responds (HTTP 200 for `play.google.com/store/apps/details?id=…&hl=en`) and reading the SPDX licence from the project's GitHub repository via the GitHub API:

| App | Play package | Licence (repo) | Listing live |
|---|---|---|---|
| AntennaPod | `de.danoeh.antennapod` | **GPL-3.0** ([AntennaPod/AntennaPod](https://github.com/AntennaPod/AntennaPod)) | yes |
| Signal | `org.thoughtcrime.securesms` | **AGPL-3.0** ([signalapp/Signal-Android](https://github.com/signalapp/Signal-Android)) | yes |
| VLC for Android | `org.videolan.vlc` | **GPL-2.0** ([videolan/vlc-android](https://github.com/videolan/vlc-android)) | yes |
| Nextcloud | `com.nextcloud.client` | **GPL-2.0** ([nextcloud/android](https://github.com/nextcloud/android)) | yes |
| Bitwarden | `com.x8bit.bitwarden` | **GPL-3.0** ([bitwarden/android](https://github.com/bitwarden/android)) | yes |
| OsmAnd | `net.osmand` | GPL-3.0 per project `LICENSE`; GitHub API reports `NOASSERTION` (multi-licence repo) | yes |

The VLC row is the pointed one: **the same app whose licence forced its removal from Apple's App Store has been distributed on Google Play continuously.** AntennaPod is the cleanest single data point for this ticket — plain GPL-3.0, on Play, no ambiguity.

Caveats on this table: a live listing does not prove Google reviewed and blessed the licence, and repository licence metadata can lag the shipped app. The AGPL entries are listed for completeness; AGPL adds §13 network-source obligations that do not apply to an offline app.

## What this means for Mulplu

Mulplu is GPL-3.0, single-author, offline, no network, no third-party SDKs. Concretely:

1. **No blocker.** Publish. There is no rule, policy or documented case preventing a GPL-3.0 app from being on Google Play.
2. **Play App Signing is fine.** Accept it (it is mandatory for a new app anyway). It creates no GPL obligation to hand over any key.
3. **Anti-tivoization is out of scope.** §6 Installation Information is not triggered by app-store distribution. The Family Link device's bootloader is not the developer's GPL problem.
4. **Do these two things to be compliant:**
   - Tag each released build in the public repo so the exact Corresponding Source for each `versionCode` is retrievable, and link the repo from the Play listing description ("clear directions next to the object code", §6(d)).
   - Ship the GPL-3.0 licence text inside the app and surface it (About/Licence screen). §4, via §6, requires the recipient to get a copy of the licence with the program. Mulplu currently has `LICENSE` in the repo only — this is a real gap that publishing creates.
5. **Accept the residual grey area consciously.** Play ToS §4 tells users they may not redistribute or modify "Content"; GPL-3.0 tells them they may. Whether those coexist is unsettled and untested. The single-author situation limits the practical exposure: the only party who could plausibly complain about the developer's own licence being under-honoured is the developer himself. Third-party GPL code pulled in later would change that calculus.
6. **Expect no licence question during review.** Nothing in Play's review surface asks about licensing.

## Sources

Primary:
- [GNU GPL-3.0 full text](https://www.gnu.org/licenses/gpl-3.0.html) — §4, §6, §7, §10, §12 (fetched 2026-08-25)
- [GNU GPL FAQ](https://www.gnu.org/licenses/gpl-faq.html) — `#Tivoization`, `#GiveUpKeys`, `#TwoPartyTivoization`, `#RemoteAttestation`, `#AnonFTPAndSendSources`, `#SourceInCVS` (fetched 2026-08-25)
- [Google Play Developer Distribution Agreement](https://play.google.com/intl/en_us/about/developer-distribution-agreement.html) — effective 2025-09-15 (fetched 2026-08-25)
- [Google Play Terms of Service (US)](https://play.google.com/intl/en_us/about/play-terms/index.html) — dated 2026-07-29 (fetched 2026-08-25)
- [Android: Sign your app / Play App Signing](https://developer.android.com/studio/publish/app-signing) (fetched 2026-08-25)
- [FSF: GPL Enforcement in Apple's App Store](https://www.fsf.org/news/2010-05-app-store-compliance) — 2010-05-25
- [FSF: VLC developer takes a stand against DRM enforcement in Apple's App Store](https://www.fsf.org/blogs/licensing/vlc-enforcement) — 2010-10-29
- [Google Play Impersonation policy](https://support.google.com/googleplay/android-developer/answer/9888374?hl=en) (checked 2026-08-25)

Secondary / opinion (clearly labelled as such above):
- [The App Fair Project: The GPL and Commercial App Stores: Time for a Reconsideration](https://appfair.org/blog/gpl-and-the-app-stores/) — 2025-03-19
