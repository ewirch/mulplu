# Age compliance: what Google Play demands of an app aimed at children

Research for [#42](https://github.com/ewirch/mulplu/issues/42), part of the map
[#39](https://github.com/ewirch/mulplu/issues/39). Sources retrieved **2026-08-25**.

## Scope

The app under assessment (mulplu, v1):

- Fully offline. No `INTERNET` permission, no network code, no backend.
- No runtime permissions at all. No third-party SDKs. No accounts, no login.
- No ads, no in-app purchases, no monetisation of any kind.
- No user-generated content, no chat, no sharing, no social features.
- Persists exactly one local Jetpack DataStore file (`app_state.json`) holding
  per-item learning progress (which of the 36 multiplication facts sit at which
  level). No name, no identifier, no free-text field, nothing user-supplied.
- `android:allowBackup="true"` in the manifest (Android default).
- German-only UI. Free. Source under GPL-3.0.
- Audience: one primary-school child, 9–11.
- Publisher: an individual (personal Play developer account), based in Germany.

## Note on dates

**Play Console Help policy articles carry no visible "last updated" line.** They
carry a `©2026 Google` footer and, where a change is pending, an in-page "effective
&lt;date&gt;" banner. Rather than invent dates, this document states the retrieval
date (2026-08-25) and reproduces the effective-date banners where they exist. Dated
citations are given for announcement pages, the Federal Register, and German
statutes, where real dates do exist.

Nothing here is legal advice. Section 6 (COPPA / GDPR / TDDDG / Impressum) is
legal-adjacent and flagged as such; a German lawyer should sign off on the privacy
policy before it is published.

## 1. Target audience and content declaration

### What the declaration is

Play Console → **Policy and programs → App content → Target audience and content**.
Four sub-steps
([Manage target audience and app content settings](https://support.google.com/googleplay/android-developer/answer/9867159)):

- **Target age** — *"Select the age group(s) that your app targets. You can make
  multiple selections if appropriate."* Buckets: **Ages 5 & under, 6-8, 9-12,
  13-15, 16-17, 18 and over**.
- **App details** — *"You may be asked for additional details about how your app
  works… Some of the questions are related to legal requirements."*
- **Ads** — *"If your app is serving ads to children, you will be asked about
  Google Play's Families Self-Certified Ads SDK Program or whether your app has a
  neutral age screen."*
- **Store presence** — *"Apps that are primarily for children and apps designed for
  several age groups such as older audiences and children are eligible for
  consideration in the Teacher Approved program."*

There is **no separate "is your app designed for children?" checkbox**. The age
selection *is* the declaration; Google infers everything else from it plus the
store listing.

Prerequisites before this section can be completed: *"you must have declared
whether or not your app contains ads, and provided instructions for app access. You
must also have added a privacy policy."*

### The three outcome branches

Identical wording appears in
[9867159](https://support.google.com/googleplay/android-developer/answer/9867159)
and
[Content rating requirements](https://support.google.com/googleplay/android-developer/answer/9859655):

| Declaration | Stated consequence |
|---|---|
| *"Your app is designed primarily for children under 13"* | *"You must comply with Google Play's Families Policy Requirements, including the requirement to use only Families Self-Certified Ads SDKs to serve ads."* |
| *"Your app is designed for everyone, including children"* | *"Any apps that have at least one target audience age group that includes children must comply with Google Play's Families Policy Requirements, including the requirement to use only Families Self-Certified Ads SDKs to serve ads **to children and users of unknown age**."* |
| *"Your app is not designed for children"* | *"You must still meet the requirements outlined in Google Play Developer Program policies and Developer Distribution Agreement."* |

**The Families trigger is binary.** Ticking *any* child age group pulls the app
fully into Families Policy. Children-only vs. mixed does **not** change *whether*
Families applies.

### Children-only vs. mixed: mixed is looser, not stricter

This is the finding that inverts the naive expectation. The Families policy text
distinguishes *"Apps that solely target children"* from *"Apps that target both
children and older audiences"*, and in every case the mixed variant is the
**relaxed** one — it grants a neutral-age-screen escape hatch that the children-only
variant does not have
([Google Play Families Policies](https://support.google.com/googleplay/android-developer/answer/9893335)):

| | Solely children | Mixed |
|---|---|---|
| AAID, SIM Serial, Build Serial, BSSID, MAC, SSID, IMEI, IMSI | *"must not transmit"* — absolute | *"must not transmit … from children or users of unknown age"* — conditional |
| `AD_ID` permission | *"should not request AD_ID permission when targeting Android API 33 or higher"* | no equivalent statement |
| Location | *"may not request location permission, or collect, use, and transmit precise location"* | no absolute ban |
| Third-party SDKs | *"must not contain any APIs or SDKs that are not approved for use in primarily child-directed services"* — absolute | allowed *"unless they are used behind a neutral age screen or implemented in a way that does not result in the collection of data from children"* |
| Ads SDKs | *"you must use only Families self-certified ads SDK versions"* | *"you must implement age screening measures, such as a neutral age screen, and make sure that ads shown to children come exclusively from Google Play self-certified ads SDK versions"* |
| Ad-format bans | apply unconditionally | *"prohibited when serving ads to children or users of unknown age"* |

Every single one of those deltas concerns **ads, SDKs, identifiers or location**.
mulplu has none of them. So the strictest configuration costs this app **nothing**.

**Recommendation: declare children only — Ages 6-8 and Ages 9-12.**

Rationale:

- The policy says *"If your app is designed for a specific level of school, choose
  the age group that best represents that school level."* The Ages 6-8 suitability
  list explicitly includes *"Relate to early education, like language development,
  early literacy, and basic math"* — mulplu is a textbook fit. Its *unsuitability*
  list includes *"Include a search feature"*, which mulplu does not have. The target
  child is 9–11, so 9-12 belongs alongside it.
- Mixed would buy an escape hatch for machinery the app does not contain.
- Declaring "not designed for children" is not available in practice. From
  9867159: *"If your app is not primarily designed for children under 13 but your
  listing contains marketing elements that suggest otherwise (such as youthful
  animation or young characters in the graphic assets), Google Play may reject your
  app."* The remedies Google offers are to strip the child-appealing marketing, or
  *"Change the target age group(s) of your app to only include children under 13
  (which will require complying with Google Play Families Policy)."* A German
  times-tables trainer for a 9-year-old cannot honestly claim an adult audience.

Do **not** add *Ages 5 & under* (the app requires reading German and entering
digits). Do **not** add any 13+ band.

### Review consequences

- *"Google will review your app to make sure the target audience that you disclose
  is accurate and your app is compliant with all Google Play Developer policies.
  Please note that certain developer accounts and/or categories of apps may be
  subjected to extended reviews, which may result in review times of up to 7 days
  or longer in exceptional cases."* (9867159) — **budget for a 7-day-plus review.**
- *"Google Play reserves the right to conduct its own review of the app information
  that you provide to determine whether the target audience that you disclose is
  accurate."* (9893335)
- *"Misrepresentation of any information about your app in the Play Console,
  including in the Target Audience and Content section, may result in removal or
  suspension of your app."* (9893335)

## 2. Families Policy: what actually bites

Preamble: *"If one of the target audiences for your app is children, you must
comply with the following requirements. Failure to satisfy these requirements may
result in app removal or suspension."*
([Google Play Families Policies](https://support.google.com/googleplay/android-developer/answer/9893335))

| Requirement | Bites? |
|---|---|
| **App content**: *"Your app's content that is accessible to children must be appropriate for children."* | **Bites** — trivially satisfied. Keep German text, art and sounds age-appropriate. |
| **App functionality**: not a mere webview, not primarily an affiliate-traffic driver | Moot — native, offline |
| **Play Console answers**: *"You must accurately answer the questions in the Play Console… providing accurate responses about your app in the Target Audience and Content section, Data safety section, and IARC Content Rating Questionnaire."* | **Bites regardless.** All three are mandatory even for a zero-data app. You answer "no collection", you do not skip. |
| **Disclose collection of personal/sensitive information from children, including via APIs and SDKs** | Moot in substance (nothing collected); the disclosure *mechanism* (Data safety + privacy policy) is still mandatory |
| **No transmission of AAID / SIM Serial / Build Serial / BSSID / MAC / SSID / IMEI / IMSI** | Moot — no network. Satisfied by construction |
| **No `AD_ID` permission at API 33+** | **Bites as a build check** — see §2.1 |
| **No phone number via TelephonyManager** | Moot |
| **No location permission, no precise location** | Moot — no runtime permissions |
| **Companion Device Manager required for Bluetooth** | Moot |
| **APIs and SDKs**: *"must not contain any APIs or SDKs that are not approved for use in primarily child-directed services"* (example given: OAuth-based auth services whose ToS excludes child-directed use) | Moot — zero third-party SDKs, no OAuth. **This is the single biggest advantage of the architecture** |
| **Augmented Reality** safety warning | Moot |
| **Social apps & features** — in-app online-safety reminder, adult action gate, disclosure in the rating questionnaire | Moot; still answer the questionnaire's questions with "no" |
| **Legal compliance**: *"You must ensure that your app, including any APIs or SDKs that your app calls or uses, is compliant with the U.S. Children's Online Privacy and Protection Act (COPPA), E.U. General Data Protection Regulation (GDPR), and any other applicable laws or regulations."* | **Bites formally**, satisfied in substance — see §6 |
| **Privacy policy**: *"Apps must have a privacy policy that accurately reflects data collection and handling practices."* | **Bites** — see §5 |
| **Families Ads and Monetization Policy Requirements** — scope is *"ads, cross-promotions (for your apps and third party apps), offers for in-app purchases, or any other commercial content"* | **Entirely moot.** Note the breadth: do not add a "my other apps" button later without re-reading this |
| **Families Self-Certified Ads SDK Program** | Moot — declare "no ads" and the sub-questions never appear |
| **Neutral age screen** | Moot — only for mixed-audience apps with ads or non-approved SDKs |

**Headline: for an app with no ads, no SDKs and no network, the Families policy
reduces almost entirely to paperwork** — four declarations (target audience,
content rating, data safety, privacy policy) plus an in-app privacy policy link.
Roughly 80% of the Families requirement surface, including the whole Ads and
Monetization block, is sidestepped by construction.

### What bites regardless

1. **IARC content rating** — mandatory for every app (§3).
2. **Privacy policy URL** — mandatory precondition for the target-audience section,
   and independently required by the Data safety form (§5).
3. **Data safety declaration** — named explicitly in the Families "Play Console
   answers" bullet (§4).
4. **Ads declaration** ("no ads") and **app access instructions** — preconditions.
5. **Advertising ID declaration** ("not used").
6. **Accurate target-age declaration**, subject to Google's review and to
   suspension for misrepresentation.

### 2.1 Advertising ID

Families policy, verbatim: *"Apps solely targeted to children should not request
AD_ID permission when targeting Android API 33 or higher."*

Note the wording is **"should not"**, not "must not", although it sits inside the
"must comply" block. That is a real ambiguity in Google's text. Treat it as hard:
reviewers flag it.

Mechanics, from
[Advertising ID](https://support.google.com/googleplay/android-developer/answer/6048248):

> "when apps update their target to Android 13 or above will need to declare a
> Google Play services normal permission in the manifest file as follows:
> `<uses-permission android:name="com.google.android.gms.permission.AD_ID"/>`"

> "Some SDKs, such as the Google Mobile Ads SDK (play-services-ads) may already
> declare this permission in the SDK's library manifest. If your app uses these
> SDKs as dependencies, the AD_ID permission from the SDK's library manifest will
> be merged with your app's main manifest by default, even if you don't explicitly
> declare the permission in your app's main manifest."

mulplu has no such dependencies, so the risk is near zero — but the check is cheap
and the failure mode (a children-only app rejected for requesting an advertising
identifier) is expensive. **Verify the merged manifest**, not the source one:
inspect `app/build/outputs/logs/manifest-merger-release-report.txt`, or Build →
Analyze APK → AndroidManifest.xml. If something ever injects it:

```xml
<uses-permission android:name="com.google.android.gms.permission.AD_ID"
    tools:node="remove" />
```

A mismatch between the Console's Advertising ID declaration and the merged manifest
is a common rejection cause.

### 2.2 "Designed for Families" no longer exists as a separate opt-in

The old help article "Participate in Designed for Families"
(`answer/7018303`) **now redirects to the Help Center index** (verified
2026-08-25). DFF as a standalone programme has been folded into the Families
Policy. The surviving artefacts are the Families Policy itself and the
[Designed for Families Addendum](https://play.google/families/developer-distribution-agreement-addendum.html)
to the DDA. **There is no separate DFF opt-in step to complete.** The only opt-in
surface in today's Console is the "Store presence" step (Teacher Approved
consideration, §7).

### 2.3 2026 changes

- **Effective 2026-08-26** (banner on 9893335): *"We're expanding our Families
  Policy Requirements policy to prohibit developers of anonymous chat apps from
  targeting children."* See
  [Preview: Google Play Families Policies](https://support.google.com/googleplay/android-developer/answer/17122218):
  *"This article previews changes included in our July 2026 policy updates."*
  **Moot** — no chat.
- **Age-Restricted Content and Functionality** policy (announced 2025-10-30),
  [answer/16302250](https://support.google.com/googleplay/android-developer/answer/16302250):
  targets matchmaking, dating, real-money gambling/games/contests. **Moot.**
  Relatedly, the Console's *Restrict Minor Access* feature is only selectable when
  "18 and over" is the *only* target age group — the opposite end from this app.
- **Child Safety Standards** declaration: Social and Dating categories only. **Moot**
  for an Education app.

### 2.4 Play Age Signals API — does not reach a German developer in the EU

[Play Age Signals overview](https://developer.android.com/google/play/age-signals/overview)
(fetched 2026-08-25), in-page banner, verbatim:

> "On March 17, 2026, the Play Age Signals API started returning age signals for
> users in Brazil for requirements under Digital ECA. The API has started returning
> age signals for eligible users in Texas who created their accounts after May 28,
> 2026 as part of our compliance efforts for Texas SB2420. Ongoing updates will be
> provided in advance of age verification bills in other US states."

Its ToS carry *"Last modified: October 2025"*.

It is **beta**, **access must be requested**, and it is scoped to regulated regions
— Brazil (Digital ECA), Texas (SB 2420), with Utah and Louisiana queued. Its stated
purpose is *"to provide age-appropriate experiences within your app"*; it is **not a
Families-policy prerequisite**, and **no EU or German obligation is declared on that
page**. Restricting country availability to Germany/EU removes the question
entirely — which is the right call anyway for a German-only app.

## 3. IARC content rating

### Mandatory

[Content Ratings](https://support.google.com/googleplay/android-developer/answer/9898843):

> "All apps must have a content rating from the IARC to be on Google Play."

> "Apps without a content rating will be removed from the Play Store."

[Prepare your app for review](https://support.google.com/googleplay/android-developer/answer/9859455):

> "To prevent your apps from being listed as 'Unrated,' sign in to Play Console and
> fill out the questionnaire for each of your apps as soon as possible. **'Unrated'
> apps may be removed from Google Play.**"

[Content rating requirements](https://support.google.com/googleplay/android-developer/answer/9859655):

> "You need to complete the content rating questionnaire for both your new and
> existing apps, including: New apps submitted to Google Play Console; Existing apps
> that are active on Google Play but lack a rating; **All app updates where there has
> been a change to your content or features that would affect the responses to the
> questionnaire**."

> "Misrepresentation of your app's content may result in its removal or suspension."

The Content rating card sits on **App content** and blocks the release from being
sent for review until submitted.

### The flow, verbatim

> "Open Google Play Console and go to the App content page (Policy > App content).
> Click **Start**. Review the information about the questionnaire and enter your
> email address. *Your email address will be used for correspondence with
> International Age Rating Coalition (IARC).* **Select a category and click Next.**
> Complete the questionnaire. […] Your responses will generate calculated ratings
> shown on the Summary page."

So there is an explicit **category selection before the content questions**, and it
selects the questionnaire branch.

### Which branch — the one genuinely debatable call

Only **two** category-branch help articles exist:
[Reference, News, or Educational](https://support.google.com/googleplay/android-developer/answer/6159966)
and
[Utility, Productivity, Communication, or Other](https://support.google.com/googleplay/android-developer/answer/6159978).
The remaining options (Games, Social Networking, Entertainment, All other app
types) exist only as radio buttons in the Console questionnaire, with no
documentation. The full option list is therefore only visible on screen after
clicking Start.

The Reference/News/Educational page is short and entirely load-bearing here.
Verbatim, in full:

> "The tone and intent of the app are important considerations. Content should be
> presented clinically and in a neutral way. **Should the goal of the app be to
> entertain, it should be considered an entertainment app, even if it contains
> factual information.**"

> "Examples of apps that should be in this category include Wikipedia, weather apps,
> dictionaries, **language teaching apps**, and medical references like WebMD and
> Medscape."

> "Examples of apps that should NOT be categorized as Reference, News, or
> Educational apps include Maxim, The Onion, Urban Dictionary, and Best Sex Tips.
> Apps with a significant focus on transaction capabilities (finance, travel,
> restaurants) should not be included in this category. Similarly, **apps that
> contain a significant portion of gaming, such as educational games, should be
> categorized as Games and not considered part of this category.**"

**Recommendation: Reference, News, or Educational.** The argument:

- *"Language teaching apps"* is a named positive example. A times-tables trainer is
  the arithmetic analogue of a vocabulary trainer — same shape, same intent.
- The goal is **not** to entertain. This is not a rationalisation after the fact:
  the ADRs deliberately stripped the reward layer
  ([ADR-0005](../adr/0005-no-accumulating-reward-layer-no-streak.md): no points, no
  XP, no badges, no currency, no collection, no streaks) and
  ([ADR-0004](../adr/0004-no-session-day-is-the-unit.md)), and nothing is timed. What
  remains is drill plus correctness feedback.
- "A significant portion of gaming" does not describe it. There is no narrative, no
  characters, no win/lose, no competition, no score beyond a per-item mastery level.
  The spark particles on a correct answer and the companion figure are *feedback and
  framing*, not a game loop.

**But note the counter-argument honestly**: the sentence about educational games is
explicit, and a reviewer who sees sparks and a companion figure could read the app
as one. Two things make this low-risk: **either branch yields the same all-negative
outcome for content-free arithmetic**, and the questionnaire answers — not the
branch — drive the rating. Pick the branch you can justify in one sentence, because
*"Misrepresentation of your app's content may result in its removal or suspension."*
If the app ever grows a genuine game layer, revisit the branch, since a change to
content or features that would affect the answers requires re-filing anyway.

### What it asks

Content questions (authored by IARC, not Google): **violence, sexual content and
nudity, language and profanity, controlled substances (drugs, alcohol, tobacco),
gambling and simulated gambling, fear and horror, crude humour, discrimination**,
plus a miscellaneous bucket. USK describes the questionnaire as capturing
youth-protection-relevant content such as depictions of violence, "as well as
possible usage risks, such as chat or purchase functions"
([USK: Spiele und Apps im IARC-System](https://usk.de/fuer-unternehmen/spiele-und-apps-pruefen-lassen/spiele-und-apps-im-iarc-system/)).

**Interactive elements** are declared separately and — per
[Apps & Games content ratings on Google Play](https://support.google.com/googleplay/answer/6209544)
— **do not change the assigned age rating**; they surface as descriptors:

| Element | mulplu |
|---|---|
| Users Interact | **No** |
| Shares Location | **No** (no permissions at all) |
| Digital Purchases | **No** (no IAP) |
| Unrestricted Internet | **No** (no `INTERNET` permission) |
| Shares Info | **No** |

### Expected outcome

All-negative answers produce the lowest rating in every territory: **USK 0**
(Germany), **PEGI 3** (Europe), **ESRB Everyone**, ClassInd L (Brazil), ACB G
(Australia), GRAC All (Korea), **IARC Generic 3** — with no descriptors.

USK's own definition of the lowest band confirms the fit
([globalratings.com ratings definitions](https://globalratings.com/ratings-definitions/),
German original):

> "USK ab 0 — Bei Spielen und Apps ab 0 Jahren handelt es sich generell um Inhalte
> ohne Beeinträchtigungspotential. Dabei können sich diese sowohl direkt an Kinder
> und Jugendliche, als auch an erwachsene Nutzer richten. Beispielsweise
> **Dienstprogramme, Kataloge oder Tools im Allgemeinen** fallen üblicherweise unter
> diese Kategorie […]"

And note USK's caveat, which is worth internalising because it is often
misunderstood: *"USK age ratings inform about a potential developmental impairment
for children and young persons. Therefore they do not imply if a certain content
deemed 'USK ab 0 Jahren' (all ages) can be understood or controlled by younger
users."* USK 0 is a statement about harm, not about suitability — the
target-audience declaration (§1) is what carries the "designed for 6–12" claim.

Caveat from 9859655: *"Rating authorities participating in IARC may change your
app's rating after a review"*, and *"The calculated rating shown on the Summary page
may not be the rating shown to users on Google Play."*

### Which boards, and why the rating matters in the EEA

Territory of *distribution* drives this, not developer location; one questionnaire
produces all of them. Verbatim (9859655):

> "Rating authorities: **ESRB** – Americas; **PEGI** – Europe and the Middle East;
> **Unterhaltungssoftware Selbstkontrolle (USK)** – Germany; **Australian
> Classification Board** – Australia; **Classificação Indicativa (ClassInd)** –
> Brazil; **GRAC** – South Korea."

Elsewhere: IARC Generic, Google Play Rating (South Korea, apps only; and Russia),
Other, Refused classification. Ratings are issued immediately and free of charge
([IARC FAQ](https://globalratings.com/faq/)); appeals go to the rating authority via
the link in the IARC certificate email.

Why it matters legally here (9859655):

> "In the European Economic Area (EEA), Australia, Brazil, Singapore, Switzerland
> and the United Kingdom only, for users determined to be minors: Blocking the
> acquisition and purchase of mature content […]; Blocking or filtering mature
> content from Google Play search and browse pages."

A USK 0 / PEGI 3 rating with no descriptors is therefore exactly what makes the app
freely acquirable on a Family Link-supervised device in Germany — which is the
map's destination. It is also what a Families reviewer expects from a children-only
educational app, and it must agree with the target-audience and data-safety
declarations (§9).

## 4. Data Safety form

### Mandatory even when nothing is collected

[Provide information for Google Play's Data safety section](https://support.google.com/googleplay/android-developer/answer/10787469),
verbatim:

> "All developers that have an app published on Google Play must complete the Data
> safety form, including apps on closed, open, or production testing tracks."

> "Even developers with apps that do not collect any user data must complete this
> form and provide a link to their privacy policy. In this case, the completed form
> and privacy policy can indicate that no user data is collected or shared."

> "Ensure that you've added a privacy policy; this is required to complete the Data
> safety form and have your data safety information shown to users."

Also: *"Apps that are active on internal testing tracks are exempt from inclusion in
the data safety section."* — relevant only while sideloading/internal-testing as a
stopgap; the closed test track is **not** exempt.

### How the form collapses when nothing is collected

Verbatim step list:

> "4. In the 'Data collection and security' section, review the list of required
> user data types that you need to disclose. If your app collects or shares any of
> the required user data types, select **Yes**. If not, select **No**.
> 5. **If you selected Yes**, confirm the following by answering Yes or No: Whether
> or not all of the user data collected by your app is encrypted in transit. Whether
> or not you provide a way for users to request that their data is deleted."

So the encryption-in-transit and deletion-mechanism questions are **conditional on
"Yes"**. Answering **No** at step 4 skips both, the entire Data types section, and
the Data usage and handling section — straight to preview and Submit. Google's CSV
export marks those questions `MAYBE_REQUIRED`. The privacy policy URL remains
required regardless.

Users then see a **"No data collected"** / "No data shared with third parties" block
on the listing.

### Free win: the Families badge

> "Committed to follow the Play Families Policy" — *"If your app falls in this
> category and you've reviewed its compliance with the Families policy requirements,
> you can choose to display a badge on your Data safety section."*

Opt in via the Data safety "Security practices" section → "Go to Target audience and
content to opt-in". For a children-only app with a genuinely empty data profile this
is a strong, free trust signal on the listing. **Take it.**

### Maintenance

> "You should update your Data safety section when there are relevant changes to the
> data practices of the app. Your Data safety form responses must remain accurate and
> complete at all times."

> "Google Play has one global Data safety form […] per package name that is agnostic
> to usage, app version, region, and user age."

Enforcement framing: *"You alone are responsible for making complete and accurate
declarations […] When Google becomes aware of a discrepancy between your app
behavior and your declaration, we may take appropriate action, including enforcement
action."* Note this cuts **in mulplu's favour**: binary analysis of an APK with no
SDKs, no permissions and no network code will find nothing to contradict a
"no data collected" declaration.

### Google's definitions, verbatim

> "**'Collect' means transmitting data from your app off a user's device.**"

> "**'Sharing' refers to transferring user data collected from your app to a third
> party.**" — and *"'Third party' means any organization other than the first party
> or its service providers."* Note sharing also covers **on-device transfer to
> another app**: *"you must disclose data sharing […] even if your app does not
> transmit the data off the user's device."* Moot here — mulplu talks to nothing.

Two explicit carve-outs, verbatim:

> "**On-device access/processing:** User data accessed by your app that is only
> processed locally on the user's device and not sent off device does not need to be
> disclosed."

> "**End-to-end encryption:** User data that is sent off device, but that is
> unreadable by you or anyone other than the sender and recipient as a result of
> end-to-end encryption does not need to be disclosed. The encrypted data must not be
> readable by any intermediary entity, including the developer, and only sender and
> recipient may have necessary keys."

Restated elsewhere on the same page:

> "**Developers do not have to declare data access as collection if it occurs solely
> on the user's device as long as the data is never transmitted off the user's
> device.**"

Two independent reasons the DataStore file is not a disclosure:

1. It never leaves the device by any code path the app contains. There is no
   `INTERNET` permission; transmission is not merely absent, it is impossible.
2. Its contents are not user data in the taxonomy's sense: 36 integers recording
   which multiplication facts are mastered. No identifier, no name, no contact
   data, no free text. No data type in Google's list is truthfully populated by it.

### The `allowBackup="true"` question

**This is the one genuinely unsettled point in this research.**

`android:allowBackup` defaults to `true`, and a DataStore file **is** in scope by
default: Auto Backup includes *"Files saved to your app's internal storage and
accessed by `getFilesDir()`"*, which is where Jetpack DataStore lives
([Back up user data with Auto Backup](https://developer.android.com/identity/data/autobackup),
last updated **2026-02-26**):

> "Auto Backup for Apps automatically backs up a user's data from apps that target
> and run on Android 6.0 (API level 23) or higher. **Android preserves app data by
> uploading it to the user's Google Drive**, where it's protected by the user's
> Google Account credentials. […] Every app can allocate up to 25 MB of backup data
> per app user."

> "Backup data is stored in a **private folder in the user's Google Drive account**
> […] The saved data does not count toward the user's personal Google Drive quota.
> […] **The backup data can't be read by the user or other apps on the device.**"

So bytes from `app_state.json` can in fact leave the device. Does that make them
"collected"?

#### Google is silent — this is a documentation gap, not a search failure

**No Google page states whether Android Auto Backup counts as collection for the
Data safety form.** Verified by reading the complete text of: the Data safety help
article including all ~30 FAQ entries and its change log; the full Auto Backup
guide; [Declare your app's data use](https://developer.android.com/privacy-and-security/declare-data-use)
(last updated **2026-03-06**); and
[Security recommendations for backups](https://developer.android.com/privacy-and-security/risks/backup-best-practices)
(last updated **2024-10-25**). The strings "Auto Backup", "Android Backup" and
"backup transport" appear in none of the Data-safety-related pages.

The single backup mention on `declare-data-use` is under **Files and docs**, listing
indicators that an app touches that category: *"Provides a workflow for data
backup."* That describes **an app offering its own backup feature** (an export
button), not the OS backing the app up. It does not reach Auto Backup.

`backup-best-practices` is purely security guidance and never mentions the Data
safety form.

And Google explicitly declines to answer questions like this one
(`declare-data-use`):

> "you alone are responsible for making complete and accurate declarations in your
> app's Play store listing. Only you possess all the information required to complete
> the Data safety form. **Google cannot make determinations on behalf of developers**
> regarding how they collect or handle user data based on their particular usage and
> practices."

What follows is therefore reasoned interpretation, not a Google position.

#### The closest thing Google does say — an almost exact analogy

From the Data safety FAQ, verbatim:

> "**My app enables users to upload their data directly to Google Drive or Dropbox
> for backup or storage. My app does not access any of this data. Should that still
> be disclosed as 'collection'?**
> It depends on the particular implementation. **If the user chooses to upload their
> data directly to their own external drive or cloud storage account (such as Google
> Drive, Dropbox, or similar services) and this upload is governed by the external
> drive or cloud storage provider's terms of service and privacy policy, and your app
> never collects or accesses the data in question, then your app does not need to
> declare the collection of this data.**"

Auto Backup matches this on every element: it goes to the **user's own Google
Drive**, it is governed by **Google's** terms rather than the developer's, and the
app never accesses it. The only imperfect fit is *"the user chooses"* — Auto Backup
is a system-level user-controlled setting (Settings → System → Backup) rather than
an in-app button, which is arguably *more* user-controlled, not less.

#### Conclusion: declare "no data collected"

Four independent grounds:

1. **The Drive/Dropbox FAQ analogy** above.
2. **The definition is agent-scoped, not byte-scoped.** Collection is *the
   developer* transmitting data off the device. Auto Backup is performed by the OS
   and the Google Backup Transport, on the user's instruction, into the user's own
   account. The developer neither initiates it, receives it, nor can read it.
3. **The E2EE carve-out** (see below).
4. **The alternative proves too much.** If Auto Backup were collection, nearly every
   Android app would be mis-declared. Google has never enforced on that basis.

Independently, the backed-up content is not personal data and populates no data type
in Google's taxonomy — the nearest, *App activity → Other actions*, is a stretch for
non-identifiable per-item mastery levels.

#### Hardening it: require client-side encryption

The E2EE carve-out is *usually* satisfied but not automatically. Verbatim
(`backup-best-practices`):

> "The Standard Android Backup system **always encrypts backup data in transit and
> at rest**. This encryption is applied regardless of the Android version in use and
> of whether your device has a lock screen. **Starting from Android 9, if the device
> has a lock screen set, then the backup data is not only encrypted, but encrypted
> with a key not known to Google** (the lock screen secret protects the encryption
> key, thus enabling end-to-end encryption)."

So the gap is devices **without a screen lock**. Two facts shrink it to almost
nothing here: `minSdk = 34` means the app only ever runs on Android 14+, well past
the Android 9 threshold; and a Family Link-supervised child device essentially
always has a lock screen. But it can be closed completely:

> "If you can't exclude sensitive data from your backup, then we recommend requiring
> end-to-end encryption which means allowing backups only on Android 9 or higher and
> only when the lock screen is set. You can achieve this by using the
> `requireFlags="clientSideEncryption"` flag, which needs to be renamed to
> `disableIfNoEncryptionCapabilities` and set to true starting from Android 12."

**Recommended: keep `allowBackup="true"` and gate the backup on client-side
encryption.** Because `minSdk = 34`, **only the `dataExtractionRules` path is needed**
— the usual requirement to also ship a legacy `fullBackupContent` file for Android 11
and lower does not apply to this app:

```xml
<!-- res/xml/data_extraction_rules.xml -->
<data-extraction-rules>
  <cloud-backup disableIfNoEncryptionCapabilities="true">
    <include domain="file" path="." />
  </cloud-backup>
  <device-transfer>
    <include domain="file" path="." />
  </device-transfer>
</data-extraction-rules>
```

referenced as `android:dataExtractionRules="@xml/data_extraction_rules"` on
`<application>`.

This preserves the product (the child keeps their progress across a device change),
makes the E2EE carve-out unconditionally true, and costs one small XML file.

#### Options considered and rejected

- **`android:allowBackup="false"`** — superficially the cleanest, but it loses the
  child's progress on any device change *and* does not fully work: per the Auto
  Backup guide, for apps targeting Android 12+ *"on devices from some device
  manufacturers, specifying `android:allowBackup="false"` disables cloud-based backup
  and restore (such as Google Drive backups) **but doesn't disable device-to-device
  transfers** for the app."* It pays the product cost without buying certainty.
- **Excluding the DataStore file from backup** — same product cost as above, no
  compliance advantage over the encryption gate.
- **Declaring the data as collected** — maximally conservative and arguably *worse
  for the user*: it puts a "this app collects App activity" line on the store listing
  of a children's app that genuinely collects nothing, contradicting both the IARC
  answers and the privacy policy. Not recommended.

Whatever is chosen, **state it honestly in the privacy policy**. "Nothing ever
leaves your device" is imprecise while Auto Backup is on; "your progress stays on
the device, and if Android's own backup is switched on, Android may include it in
your personal, encrypted Google backup, which we cannot access" is accurate and
reassuring.

## 5. Privacy policy

### Mandatory — unconditionally, and twice over

[User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311),
verbatim:

> "All apps must post a privacy policy link in the designated field within Play
> Console, **and** a privacy policy link or text within the app itself."

> "Apps that do not access any personal and sensitive user data must still submit a
> privacy policy."

Not conditioned on permissions, not conditioned on collection. The Data safety form
requires it independently (§4), and the Console enforces it procedurally: the URL
must exist before the target-audience section can be completed.

### The in-app requirement is unconditional for child-directed apps

[Prepare your app for review](https://support.google.com/googleplay/android-developer/answer/9859455),
verbatim — **this is the strongest single citation for mulplu**:

> "- For apps that request access to sensitive permissions or data (as defined in
> the User data policy): You must link to a privacy policy on your app's store
> listing page and within your app. …
> - **For apps that target children: You must link to a privacy policy on your
> app's store listing page and within your app, regardless of your app's access to
> sensitive permissions or data.** Make sure your privacy policy is available on an
> active URL, applies to your app, and specifically covers user privacy. Note that
> even apps that do not access any personal or sensitive user data must still
> submit a privacy policy."

### Crucially: "link **or text**" — no `INTERNET` permission needed

The User Data policy accepts *"a privacy policy link **or text** within the app
itself."* So the in-app obligation is satisfied by a **static in-app screen
rendering the policy text** from a bundled asset. **Do not add the `INTERNET`
permission just to open a browser link** — that would undo the app's cleanest
compliance property and contradict the "no unrestricted internet" answer in the
IARC questionnaire. An in-app text screen is compliant, cheaper, and works offline,
which is the whole point of this app. Showing the URL as selectable text alongside
is fine.

This is nonetheless a **real code change**: mulplu today has no such screen. It is
the only Families requirement that touches the app's UI. Keep it off the child's
main path — a small "Datenschutz" affordance on the start screen.

### URL requirements

User Data policy, verbatim:

> "Please make sure your privacy policy is available on an active, publicly
> accessible and non-geofenced URL (no PDFs) and is non-editable."

Plus *"Ensure your privacy policy is globally accessible"*, and from 9859455,
*"available on an active URL, applies to your app, and specifically covers user
privacy."*

**GitHub Pages of this repo satisfies every clause**: HTTPS, stable
`https://ewirch.github.io/mulplu/…` URL, publicly readable, not geofenced, HTML not
PDF, and not publicly editable (only collaborators can push). Watch:

- **Do not use the GitHub Wiki** if wiki editing is open — that violates
  "non-editable".
- **No PDF**, and no Google Drive/Docs file: the "no PDFs" clause and the
  non-editable clause both cut against Drive-hosted documents.
- A raw `PRIVACY.md` rendered on github.com technically qualifies, but Pages is the
  cleaner story — it looks like a policy page, and the `<title>` can carry the
  required labelling.
- **Keep the URL alive forever.** Renaming the repo breaks it and Play flags the
  app.

### Required contents

User Data policy, verbatim:

> "The privacy policy must, together with any in-app disclosures, comprehensively
> disclose how your app accesses, collects, uses, and shares user data, not limited
> by the data disclosed in the Data safety section. This must include:
> - Developer information and a privacy point of contact or a mechanism to submit
>   inquiries.
> - Disclosing the types of personal and sensitive user data your app accesses,
>   collects, uses, and shares; and any parties with which any personal or sensitive
>   user data is shared.
> - Secure data handling procedures for personal and sensitive user data.
> - The developer's data retention and deletion policy.
> - Clear labeling as a privacy policy (for example, listed as 'privacy policy' in
>   title)."

> "The entity (for example, developer, company) named in the app's Google Play store
> listing must appear in the privacy policy or the app must be named in the privacy
> policy."

So even a "we collect nothing" policy must carry a title containing "Privacy
Policy" / "Datenschutzerklärung", the developer name **matching the Play developer
name** and/or the app name, a contact mechanism, and affirmative answers to all
five bullets — answering them with "none, because…" is the truthful form.

### Structure of a minimal, truthful policy

Each section maps to a quoted requirement, not to boilerplate. Write it in
**German** (German UI, German audience); an English section is not required but
removes review friction, since a reviewer may not read German.

1. **Title** — "Datenschutzerklärung / Privacy Policy — mulplu". Names the app;
   satisfies the labelling bullet.
2. **Who we are** — developer name exactly as in the Play listing, plus contact
   e-mail (and postal address if an Impressum is included). Satisfies "developer
   information and a privacy point of contact".
3. **Kurzfassung für Kinder** — two or three very short sentences a
   primary-schooler can read: collects nothing, sends nothing, needs no internet,
   asks for no name. This is the plain-language layer (Art. 12(1) in spirit), placed
   first.
4. **What data we collect** — none. Affirmatively: no personal data, no accounts,
   no names, no e-mail, no device identifiers, no advertising ID, no location, no
   contacts, no analytics, no crash reporting, no third-party SDKs, no ads, no IAP.
5. **What is stored on the device** — the progress file: what it contains
   (non-personal progress values), that it stays in app-private storage, that it is
   never transmitted, that uninstalling deletes it. This doubles as the § 25(2)
   TDDDG justification (§6).
6. **No network access** — the app declares no `INTERNET` permission and requests no
   runtime permissions, and the source is public under GPL-3.0 so this is
   independently verifiable. **Verifiability is the best asset in any review
   dispute — say it explicitly and link the repo.**
7. **Android system backup** — the accurate statement matching the `allowBackup`
   decision from §4.
8. **Sharing** — nothing, because nothing is collected.
9. **Security** — data never leaves the device and is protected by Android's
   per-app storage sandbox; the developer has no access. This is the only truthful
   way to satisfy the "secure data handling procedures" bullet.
10. **Retention and deletion** — no server-side data exists; the local file persists
    until app data is cleared or the app is uninstalled, with instructions.
    Satisfies the retention/deletion bullet. (Play's account-deletion requirement
    applies to apps offering account creation — not this one.)
11. **Children** — the app is aimed at primary-school children; no personal data is
    processed, so no Art. 8 DSGVO parental consent is sought or needed and no COPPA
    notice/consent obligation arises.
12. **Changes** — one line plus a "last updated" date. **Keep the date current** — a
    stale policy is a common reviewer flag.
13. **Impressum** — inline or clearly linked (§6).

The one thing that would invalidate the entire document is a future dependency that
phones home. **Treat the policy as coupled to the manifest.**

## 6. COPPA, GDPR, TDDDG, Impressum — *not legal advice*

### COPPA

**Does it reach a German developer?** In principle yes — COPPA is not limited to US
operators. It applies to foreign-based services **directed to children in the United
States**, or that knowingly collect personal information from children in the US
([FTC COPPA FAQ](https://www.ftc.gov/business-guidance/resources/complying-coppa-frequently-asked-questions);
[Where in the world? Warning letters address geolocation and COPPA coverage, FTC, 2018-04](https://www.ftc.gov/business-guidance/blog/2018/04/where-world-warning-letters-address-geolocation-coppa-coverage)).

**Does anything trigger?** No. The entire machinery hangs on *collection*.

[16 CFR § 312.3](https://www.ecfr.gov/current/title-16/chapter-I/subchapter-C/part-312/section-312.3):

> "It shall be unlawful for any operator … to **collect personal information from a
> child** in a manner that violates the regulations prescribed under this part.
> Generally, under this part, an operator must: (a) Provide notice … of what
> information it collects from children …; (b) Obtain verifiable parental consent
> **prior to any collection, use, and/or disclosure of personal information from
> children** …"

[16 CFR § 312.2](https://www.ecfr.gov/current/title-16/chapter-I/subchapter-C/part-312/section-312.2):

> "**Collects or collection** means the gathering of any personal information from a
> child by any means, including but not limited to: (1) Requesting, prompting, or
> encouraging a child to submit personal information online; (2) Enabling a child to
> make personal information publicly available in identifiable form …"

mulplu transmits nothing, has no identifiers, no accounts, no SDKs, no ads.
Non-personal progress in a local file is not "gathering … personal information from
a child". **No online notice, no direct notice, no verifiable parental consent
obligation arises.** §§ 312.4 (notice), 312.5 (consent), 312.7 (conditioning),
312.8 (security) and 312.10 (retention) are all predicated on an operator that
collects or maintains children's personal information.

**2025 amendments.** Published **2025-04-22**, effective **2025-06-23**, general
compliance deadline **2026-04-22** — so already binding today
([Federal Register](https://www.federalregister.gov/documents/2025/04/22/2025-05904/childrens-online-privacy-protection-rule);
[FTC press release, 2025-01-16](https://www.ftc.gov/news-events/news/press-releases/2025/01/ftc-finalizes-changes-childrens-privacy-rule-limiting-companies-ability-monetize-kids-data)).
Changes: separate verifiable parental consent for disclosure to third parties
(e.g. targeted advertising); "personal information" expanded to include biometric
and government-issued identifiers; new § 312.8(b) written security programme; new
§ 312.10 written data-retention policy that must be published in the online notice,
with a ban on indefinite retention; Safe Harbor programmes must publish membership
lists. Proposed push-notification and ed-tech changes were **not** adopted.

**Does any of it bite?** No — every one of those duties attaches to an operator that
collects children's personal information.

Two caveats: a German-only UI aimed at German primary schoolers is weak evidence of
being "directed to children in the United States", which further reduces exposure;
and COPPA's second prong is a red line to *keep* not crossing — any future
analytics, crash reporting, ad SDK or persistent identifier flips the app into full
COPPA scope instantly. Restricting distribution to Germany/EU removes even the
jurisdictional question.

### GDPR

GDPR engages only if there is *processing of personal data* (Art. 2(1)). Art. 4(1)
defines personal data as information "relating to an identified or identifiable
natural person"; Art. 4(2) defines processing to include storage
([Reg. (EU) 2016/679, in force since 2018-05-25](https://eur-lex.europa.eu/eli/reg/2016/679/oj)).

Two independent reasons it does not engage:

1. **Not personal data.** Learning progress with no identifier, no name, no
   account, no device ID is not information relating to an identifiable person from
   the developer's perspective. Nothing links it to a person and nothing leaves the
   device.
2. **No controller-side processing.** The developer never receives, accesses, or
   determines the means of any read or write beyond shipping the software. The data
   lives only in the user's app-private storage.

This second point is the **one legally arguable link in the chain**. The
counter-argument in the literature is that a developer who retains control over
logical controls — who could change data access in the next update — is hard to
characterise as a non-processor. That has real force for apps with a network stack
that could be switched on. It has much less force here, where enabling exfiltration
would require a new permission, a new manifest, a new review and a user-visible
update, and where the GPL-3.0 source makes the claim publicly verifiable. The
conservative framing for the policy text is **"we process no personal data"**,
not "GDPR does not apply".

**Art. 8 (child's consent).** Consent for information society services is lawful
from age **16**; Member States may lower to not below 13. **Germany did not lower it
— 16 stands** (no derogation in the BDSG). But Art. 8 conditions *consent as a legal
basis for processing*. With no processing and no consent sought, **Art. 8 is not
reached.**

**Art. 13.** Information duties attach to a controller collecting personal data from
the data subject. No collection, no Art. 13 obligation. A "we process no personal
data" statement is therefore **not a GDPR requirement** — but it is required by
Google (§5), and it is the right content. Do not dress it up as a formal Art. 13
notice with Art. 13(1)(a)–(f) headings; that invites the reader to hunt for the
processing the format implies.

**Art. 12(1).** Information must be provided "in a concise, transparent, intelligible
and easily accessible form, using clear and plain language, **in particular for any
information addressed specifically to a child**"; Recital 58 adds that a child should
easily understand it. Formally this too presupposes processing, but the audience is
primary-school children and their parents — write the German at a level an
8–10-year-old can read. Cheap, and the first thing a regulator or reviewer eyeballs.

### § 25 TDDDG — the one German rule that is *not* about personal data

[§ 25 TDDDG](https://www.gesetze-im-internet.de/ttdsg/__25.html) (ex-TTDSG) makes
storing information in, or accessing information already stored in, the user's
terminal equipment lawful only with informed consent — **regardless of whether the
information is personal data**. That squarely describes writing a progress file.

But § 25(2) Nr. 2 exempts storage or access that is *"unbedingt erforderlich, damit
der Anbieter eines digitalen Dienstes einen vom Nutzer ausdrücklich gewünschten
digitalen Dienst zur Verfügung stellen kann."* Saving the learning progress the
child explicitly asked the app to save is the paradigm case of strictly necessary.
**No consent banner is needed.** State it in the policy in one plain sentence — that
sentence is simultaneously the § 25(2) justification and good child-facing
transparency.

### Impressumspflicht (§ 5 DDG)

[§ 5(1) DDG](https://www.gesetze-im-internet.de/ddg/__5.html):

> "Diensteanbieter haben für **geschäftsmäßige, in der Regel gegen Entgelt
> angebotene** digitale Dienste folgende Informationen leicht erkennbar, unmittelbar
> erreichbar und ständig verfügbar zu halten …"

Purely private and family pages fall outside it, and outside § 18 MStV (which
exempts offerings serving *"ausschließlich persönlichen oder familiären Zwecken"*).
**However**, German courts read "geschäftsmäßig" broadly: it does not require profit
intent, only a sustained, planned activity, and a single ad banner or affiliate link
has been enough to lose the private exemption. A free, ad-free, GPL-3.0 hobby app
published under your own name on a commercial app store is a **genuine grey zone**:
no monetisation argues private; publishing at scale via Play under a developer
account argues geschäftsmäßig.

**Recommendation: publish an Impressum anyway** (name, postal address, e-mail). A
handful of lines, it eliminates an Abmahnung surface, and Google's "developer
information and a privacy point of contact" requirement means a name and contact
channel must be published regardless. The real cost is publishing a home address if
there is no business address — that is the reason to get a lawyer's view if
anonymity matters. There is **no clean way to satisfy Google's requirement while
staying anonymous**: an e-mail address alone satisfies Google but not § 5 DDG if
§ 5 applies.

## 7. Teacher Approved

**Alive and operating in 2026. Optional. Opt-in. Not automatic. Not required to
publish.**

Families Policies, verbatim:

> "All apps that comply with Google Play Families policies can opt in to be rated
> for the Teacher Approved program, but we cannot guarantee that your app will be
> included in the Teacher Approved program."

9867159, Store presence step:

> "Apps that are primarily for children and apps designed for several age groups
> such as older audiences and children are eligible for consideration in the Teacher
> Approved program."

So opting in happens **inside the Store presence sub-step of the target-audience
declaration**. It is a request for consideration, not an approval.

What it grants
([Build Teacher Approved apps](https://play.google.com/console/about/programs/teacherapproved/)):

> "Teacher Approved apps: Are eligible to appear and be featured on the Kids tab on
> Google Play; Display the Teacher Approved badge; Include a section on the app's
> Google Play details page that shows what makes the app stand out."

Judged by teachers and children's media specialists on *"design, appeal, enrichment,
age appropriateness, appropriateness of ads, in-app purchases, and cross-promotion"*.

**For mulplu's destination — installable from Play onto one Family Link-supervised
device — Teacher Approved is irrelevant.** It is a discovery mechanism, and this app
does not need to be discovered. It costs nothing, adds no publishing gate, and can
be opted into later. The realistic obstacles would be the polish bar and reviewer
language coverage: the programme states reviewers rate only *"for the age groups
that they teach"* and says nothing about language, so whether German-only apps get
reviewed at all is **not answerable from the primary source**.

**Verdict: skip for v1.** Revisit only if discovery ever becomes a goal.

## 8. Adjacent blockers found while researching

Not age compliance, but they gate the same submission and are sharp:

- **Closed testing — the real blocker.** Personal developer accounts created on or
  after **2023-11-13** must run a closed test with **at least 12 testers opted in
  continuously for 14 days** before production access can be requested. Originally
  20 testers (Nov 2023), reduced to **12 in December 2024**. Organization accounts
  and older personal accounts are exempt.
  ([App testing requirements for new personal developer accounts](https://support.google.com/googleplay/android-developer/answer/14151465))
  For a solo hobbyist this means **recruiting 12 real testers for a fortnight**.
  This belongs to #40/#41, but it sets the calendar for everything here. Secondary
  reports suggest Google now also checks that testers genuinely used the app —
  **unverified against a primary source**.
- **Target API level.** From **2026-08-31**, new apps and updates must target
  **Android 16 (API 36)**. mulplu is at `targetSdk = 35`; today is 2026-08-25. Since
  the closed-testing requirement already pushes the timeline past that date,
  **plan on `targetSdk = 36`.** An extension to 2026-11-01 can be requested.
  ([Target API level requirements](https://support.google.com/googleplay/android-developer/answer/11926878);
  [Meet Google Play's target API level requirement](https://developer.android.com/google/play/requirements/target-sdk))
  `minSdk = 34` is unaffected and fine for the target device.
- **Signing.** `app/build.gradle.kts:47` silently falls back to the debug keystore
  when `app/release.keystore` is absent. Play rejects debug-signed artefacts. No AAB
  path exists either, and Play has required App Bundles for new apps since August
  2021.
- **EU trader / non-trader status (DSA).** Widely reported as required for
  developers distributing in the EU, and it **publishes name and address on the
  store listing** — a real privacy consideration for a solo hobbyist on a personal
  account. **Could not be confirmed from a primary Play page in this pass.** Check
  Play Console → Developer account → Trader status. This interacts directly with the
  Impressum decision in §6.
- **Android developer verification.** A new section in the Play Console Help
  navigation (2026) covering SHA-256 fingerprints and package-name registration.
  Play Console accounts are already verified, so this is likely sideloading-side,
  but **scope not verified**.
- **GPL-3.0.** No Play-specific blocker found. The well-known GPLv3/app-store
  conflict is an Apple matter (its ToS impose distribution restrictions GPLv3
  forbids); Google's DDA does not impose the equivalent, and GPLv3 apps are routinely
  distributed on Play. Play App Signing means Google re-signs the artefact, which
  some read as friction with GPLv3's installation-information clause — in practice
  never an obstacle on Play. Low confidence, no action indicated.

## 9. Checklist for this app

### Code changes required

- [ ] **Add an in-app privacy policy screen.** Unconditional for child-directed
      apps. Render the policy **text from a bundled asset** — the User Data policy
      accepts "link **or** text". **Do not add the `INTERNET` permission.** Keep the
      affordance off the child's main path (a small "Datenschutz" entry).
- [ ] **Bump `targetSdk` to 36** (Android 16). Mandatory for new submissions from
      2026-08-31; the closed-test calendar makes this unavoidable.
- [ ] **Keep `allowBackup="true"` and add `res/xml/data_extraction_rules.xml`** with
      `<cloud-backup disableIfNoEncryptionCapabilities="true">`, referenced via
      `android:dataExtractionRules`. Makes the Data-safety E2EE carve-out
      unconditionally true while preserving the child's progress across a device
      change. **No legacy `fullBackupContent` file needed** — `minSdk = 34`. Do *not*
      use `allowBackup="false"`: it costs the progress and still does not stop
      device-to-device transfer on some OEMs.
- [ ] **Verify the merged manifest declares no `AD_ID` permission.** Inspect
      `app/build/outputs/logs/manifest-merger-release-report.txt`. Add
      `tools:node="remove"` only if something injects it.
- [ ] **Fix release signing** — real `app/release.keystore`, no silent debug
      fallback, produce an AAB. (Signing/release ticket; listed because it blocks
      the same upload.)

### Artefacts to write

- [ ] **Privacy policy in German**, hosted on this repo's GitHub Pages at a stable
      URL. Active, publicly accessible, non-geofenced, HTML not PDF, non-editable.
      Content per §5, all thirteen sections. Title must contain
      "Datenschutzerklärung / Privacy Policy". Must name the developer exactly as in
      the Play listing, and/or the app.
- [ ] **Impressum** on the same Pages site (§6 — recommended, grey zone).
- [ ] **Ship the same policy text inside the app** as a bundled asset.
- [ ] Store assets still missing per the map: 512px icon, screenshots, feature
      graphic, German store listing.

### Play Console declarations

- [ ] **Privacy policy URL** — App content → Privacy policy. Must be entered
      *before* the target-audience section can be completed.
- [ ] **Ads** — declare the app contains **no ads**. (Precondition.)
- [ ] **App access** — all functionality available without special access; no login,
      no reviewer credentials. (Precondition.)
- [ ] **Target audience and content** — select **Ages 6-8 and Ages 9-12 only**. Not
      *5 & under*, not any 13+ band. The app becomes a Families app at this point.
- [ ] **Store presence sub-step** — do **not** opt into Teacher Approved for v1.
- [ ] **Content ratings (IARC)** — enter an email address for IARC correspondence,
      then select the category branch: **Reference, News, or Educational** (§3;
      justification: "language teaching apps" is a named example, the goal is not to
      entertain, and the reward layer was deliberately removed per ADR-0005). Answer
      **no** to every content question and every interactive element: no user
      interaction, no location sharing, no digital purchases, no unrestricted
      internet, no sharing of personal information. Expected: USK 0 / PEGI 3 / ESRB
      Everyone, no descriptors.
- [ ] **Data safety** — declare **no data collected, no data shared** at step 4; the
      encryption-in-transit and deletion-mechanism sub-questions then never render.
      Privacy policy URL still required here. Keep the reasoning in §4 to hand in
      case the Auto Backup point is ever challenged.
- [ ] **Opt into the "Committed to follow the Play Families Policy" badge** on the
      Data safety section. Free trust signal, and honest here.
- [ ] **Advertising ID** — declare the app does **not** use an advertising ID.
- [ ] **Financial features** — none. **Health** — no. **News** — no. **Government
      apps** — no. **Child safety standards** — not applicable (Social/Dating only).
- [ ] **App category: Education** (not Games). This routes the IARC questionnaire to
      the app branch and matches the target-audience declaration.
- [ ] **Country availability** — restrict to **Germany** (optionally EU). Honest for
      a German-only app; also sidesteps the US-state app-store accountability regime
      (Texas SB 2420 et al.), the Play Age Signals question, and the residual COPPA
      jurisdictional question.
- [ ] **Trader status (DSA)** — check Developer account settings; note it publishes
      name and address publicly (§8, unverified).
- [ ] Walk the whole App content page and answer every declaration listed there —
      the set changes over time and could not be exhaustively enumerated here.

### Timeline

Closed test (12 testers × 14 continuous days) → apply for production access →
target-audience review, **which may take 7 days or longer**. Production is at least
three weeks out from the start of a valid closed test. Plan `targetSdk = 36`
accordingly.

### Consistency check before submitting

The declarations must tell one story, because that is what review checks:
children-only target audience + lowest content rating with no interactive
descriptors + no data collected + a privacy policy saying the same thing + a merged
manifest with no `AD_ID` and no `INTERNET`. Any disagreement — a policy mentioning
analytics, a data-safety entry contradicting "nothing leaves the device", an
interactive-element descriptor implying networking — is the most likely rejection
cause for an app that is otherwise this clean.

Re-read the Families policy, the Preview Families policy, and the Data safety page
in the Console immediately before submitting. These pages carry no last-updated
stamp, so the only way to know the current wording is to read it at submission time.

## Sources

All retrieved 2026-08-25.

**Google Play**

- [Google Play Families Policies](https://support.google.com/googleplay/android-developer/answer/9893335)
- [Preview: Google Play Families Policies](https://support.google.com/googleplay/android-developer/answer/17122218)
- [Manage target audience and app content settings](https://support.google.com/googleplay/android-developer/answer/9867159)
- [Content Ratings](https://support.google.com/googleplay/android-developer/answer/9898843)
- [Content rating requirements for apps, games, and the ads served on both](https://support.google.com/googleplay/android-developer/answer/9859655)
- [Ratings questionnaire: Reference, News, or Educational](https://support.google.com/googleplay/android-developer/answer/6159966)
- [Ratings questionnaire: Utility, Productivity, Communication, or Other](https://support.google.com/googleplay/android-developer/answer/6159978)
- [Apps & Games content ratings on Google Play](https://support.google.com/googleplay/answer/6209544)
- [Policy Deadlines](https://support.google.com/googleplay/android-developer/answer/9876714)
- [Policy announcement: July 15, 2026](https://support.google.com/googleplay/android-developer/answer/17134731)
- [Prepare your app for review](https://support.google.com/googleplay/android-developer/answer/9859455)
- [User Data](https://support.google.com/googleplay/android-developer/answer/10144311)
- [Provide information for Google Play's Data safety section](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Understand app privacy & security practices with Google Play's Data safety section](https://support.google.com/googleplay/answer/11416267?hl=en&co=GENIE.Platform%3DAndroid)
- [Advertising ID](https://support.google.com/googleplay/android-developer/answer/6048248)
- [Age-Restricted Content and Functionality](https://support.google.com/googleplay/android-developer/answer/16302250)
- [App testing requirements for new personal developer accounts](https://support.google.com/googleplay/android-developer/answer/14151465)
- [Target API level requirements for Google Play apps](https://support.google.com/googleplay/android-developer/answer/11926878)
- [Build Teacher Approved apps](https://play.google.com/console/about/programs/teacherapproved/)
- [Designed for Families Addendum to the DDA](https://play.google/families/developer-distribution-agreement-addendum.html)

**Android**

- [Back up user data with Auto Backup](https://developer.android.com/identity/data/autobackup) (last updated 2026-02-26)
- [Security recommendations for backups](https://developer.android.com/privacy-and-security/risks/backup-best-practices) (last updated 2024-10-25)
- [Declare your app's data use](https://developer.android.com/privacy-and-security/declare-data-use) (last updated 2026-03-06)
- [Play Age Signals overview](https://developer.android.com/google/play/age-signals/overview)
- [Meet Google Play's target API level requirement](https://developer.android.com/google/play/requirements/target-sdk)

**Ratings**

- [Games and apps in the IARC system — USK](https://usk.de/en/home/age-classification-for-games-and-apps/games-and-apps-in-the-iarc-system/)
- [IARC FAQ](https://globalratings.com/faq/) · [How IARC works](https://globalratings.com/how-iarc-works/) · [Ratings definitions](https://globalratings.com/ratings-definitions/)

**Law**

- [16 CFR § 312.2 — Definitions](https://www.ecfr.gov/current/title-16/chapter-I/subchapter-C/part-312/section-312.2)
- [16 CFR § 312.3 — Regulation of unfair or deceptive acts](https://www.ecfr.gov/current/title-16/chapter-I/subchapter-C/part-312/section-312.3)
- [Children's Online Privacy Protection Rule — Federal Register, 2025-04-22](https://www.federalregister.gov/documents/2025/04/22/2025-05904/childrens-online-privacy-protection-rule)
- [FTC finalizes changes to Children's Privacy Rule — 2025-01-16](https://www.ftc.gov/news-events/news/press-releases/2025/01/ftc-finalizes-changes-childrens-privacy-rule-limiting-companies-ability-monetize-kids-data)
- [Complying with COPPA: Frequently Asked Questions — FTC](https://www.ftc.gov/business-guidance/resources/complying-coppa-frequently-asked-questions)
- [Where in the world? Warning letters address geolocation and COPPA coverage — FTC, 2018-04](https://www.ftc.gov/business-guidance/blog/2018/04/where-world-warning-letters-address-geolocation-coppa-coverage)
- [Regulation (EU) 2016/679 (GDPR), consolidated text](https://eur-lex.europa.eu/eli/reg/2016/679/oj)
- [§ 25 TDDDG](https://www.gesetze-im-internet.de/ttdsg/__25.html)
- [§ 5 DDG](https://www.gesetze-im-internet.de/ddg/__5.html)
