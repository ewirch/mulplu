# Play developer account 2026: account types, verification, and the pre-production test requirement

Research note for [#40](https://github.com/ewirch/mulplu/issues/40) (part of map [#39](https://github.com/ewirch/mulplu/issues/39)).
All sources fetched and verified on **2026-08-25**. Google's Help Center pages are undated; where a
rule has a known effective date it is given inline, otherwise the claim is "as documented on 2026-08-25".

---

## Answer in short

- **Personal account is the right choice.** US$25 one-time fee, government ID verification, plus a
  one-minute device verification. An organization account would need a real legal entity **and** a
  D-U-N-S number (up to 30 days to obtain) — not available to a private person. [1][2][3]
- **The closed-testing precondition applies:** ≥ **12 testers, opted in continuously for 14 days**,
  then apply for production access; the application is reviewed in "seven days or less". Only
  **personal** accounts created after 2023-11-13 are subject to it. [4]
- **A free app does not publish the home address.** Google publishes developer name, **legal name**,
  **country** and developer email. The **full address is only published for merchant accounts**
  (apps that monetize via paid apps or in-app purchases). Mulplu is free and has no IAP, so street
  address stays private. The **legal name is published regardless** — a pseudonymous developer name
  does not hide it. [3][5]
- **Realistic end-to-end: ~5–8 weeks**, of which 14 days is a hard, unshortenable floor and tester
  recruitment is the dominant risk.
- **Watch:** Android developer verification goes live 2026-09-30 (BR/ID/SG/TH first, global 2027).
  A verified Play Console account already satisfies it; no extra work for this project. [8][9]

---

## 1. Account types, fee and verification

### Registration

| Step | Detail | Source |
| --- | --- | --- |
| Age | Must be ≥ 18 | [1] |
| Fee | **US$25 one-time**, credit/debit card only (MasterCard, Visa, Amex, Visa Electron outside US). **Prepaid cards are not accepted.** | [1] |
| Agreement | Accept the Google Play Developer Distribution Agreement | [1] |
| Account type | Personal or Organization — chosen at signup | [1][2] |
| Payments profile | The developer account is linked to a Google payments profile **at the point of account creation**; it carries the legal name and address and must stay up to date. | [3] |

The fee is not refunded if the identity information submitted turns out to be invalid. [1]

### Personal vs. organization

Both types have identical Play Console functionality and both can monetize. [2]

**Personal** — "for personal use … student, hobbyist, or amateur developer". [2]
Required at creation: developer name, legal name, legal address, contact email, contact phone,
developer email. [3]
Verification: **official government identity document** (if the linked payments profile is not
already verified). Country/region-specific document lists exist; individuals generally submit a
government photo ID and a proof-of-address document matching the profile exactly. [5][6]

**Organization** — required for commercial/professional activity, and *mandatory* for financial,
health, VPN and government apps. [2]
Required at creation: D-U-N-S number, organization name/address/phone/**website**, contact name,
contact email/phone, developer email, **developer phone number**. [3]
Verification: D-U-N-S number + government photo ID of an authorized representative + official
organization document + website verification (website verification for new organization accounts
was introduced **February 2024**). [3][5]

**D-U-N-S:** issued free by Dun & Bradstreet, but "**This process can take up to 30 days so you
should plan ahead.**" No exemptions are granted except for regions D&B does not cover, and for
recognised government agencies. [3]

> **Conclusion for Mulplu:** an organization account is not a realistic option — it presupposes an
> actual organization or business, and it publishes *more* personal data (full legal address **and**
> developer phone number), not less. [3]

### Device verification (personal accounts only)

New personal accounts must prove access to a real Android device via the Play Console mobile app
before the app can go live. Requirements: **non-rooted physical Android device running Android 10+**;
the same device may verify several accounts; takes "less than a minute". Introduced **early 2024**. [7]

### Verification durations

- Identity verification review: Google reviews the submission and emails the account owner when
  complete; "may take a few days". [5]
- Payment-*method* verification (only if monetizing): "can take up to 5 days". Not applicable to
  a free app with no IAP. [3]
- Production-access application review: "usually takes seven days or less, but can occasionally
  take longer". [4]

---

## 2. The closed-testing precondition

Primary source: *App testing requirements for new personal developer accounts* [4].

**Scope.** Applies to **personal** Play Console accounts **created after 2023-11-13**. Accounts
created before that date, and **organization accounts**, are not covered by the article's scope.
Until the requirement is met, the Play Console **Production** and **Pre-registration** pages stay
disabled. [4]

**The bar.**

> "Developers with personal accounts created after November 13, 2023, must run a closed test for
> their app with a minimum of **12 testers** who have been opted in **continuously for at least 14
> days**." [4]

- The 12 testers must be opted in **at the moment you apply**, and must have been opted in
  continuously for the **preceding 14 days**. [4]
- Testers must be told explicitly that they have to **stay opted in** for the whole 14 days. [4]
- Closed testing requires **completed app setup** first (store listing, content rating, data safety,
  target audience, signed release artefact). Internal testing has no prerequisites and can start
  before app setup. [4]
- **Open testing is only unlocked after production access is granted** — it is not a shortcut. [4]

**History of the number.** Introduced 2023-11-13 at **20 testers**; reduced to **12** on
**2024-12-11** ("starting today, we're requiring 12 instead of 20 testers for personal developer
accounts"). [10] The 12/14 figures are still what the Help Center states on 2026-08-25. [4]

**What counts as a valid tester.** The Help Center does **not** publish a machine-readable
definition. What it does say:
- Recruit from personal/professional networks, communities, social media; recruit a group that
  represents the app's intended audience. [4]
- Applications are rejected for "fewer than 12 opted-in testers" **or** "**insufficient tester
  engagement** during the testing period" — i.e. mere opt-in without use is a documented failure
  mode. [4]
- The application form asks you to **summarize the feedback received** and how it was collected, so
  a test with zero feedback is hard to write up truthfully. [4]

> Widely-repeated claims that testers must be 12 *distinct Google accounts on real devices* and that
> emulators/bots don't count are **not** stated in Google's documentation. They are consistent with
> the "insufficient engagement" rejection reason, but treat them as folklore, not policy.

**Applying for production access.** Play Console → Dashboard → *Apply for production*. Three
sections: (1) *About your closed test* — how hard tester recruitment was, whether testers used all
features, whether usage matched expected production behaviour, summary of feedback; (2) *About your
app/game* — target audience, value proposition, estimated first-year install range (answers are
**not** shown publicly and do not affect visibility or program eligibility); (3) *About your
production readiness* — what you changed based on the test, and how you decided it was ready.
Each part must be submitted with **Next**/**Apply**; leaving the page discards the answers. [4]

Review outcome arrives by email to the account owner; on approval the **Production** and **Open
testing** pages unlock. On rejection, the closed test must continue. [4]

---

## 3. What Google Play publishes about the developer

For a **personal** account:

> "Google will display your **legal name**, your **country** (as per your legal address), and
> **developer email address** on Google Play. **If you decide to monetize on Google Play then Google
> will display your full address.**" [3]

And, independently:

> "To comply with consumer protection laws, **merchant accounts** (developer accounts with apps that
> monetize via paid apps or in-app purchases) must show their **full address** on Google Play." [13]

| Field | Personal, free app | Personal, monetizing | Organization |
| --- | --- | --- | --- |
| Developer name (free text, changeable) | public | public | public |
| Legal name | **public** | public | public (org name) |
| Country | public | public | public |
| Full street address | **not public** | public | **public** |
| Developer email address | public (mandatory, OTP-verified) | public | public |
| Developer phone number | not required (except Korea) | — | **public, mandatory** |
| Contact email / contact phone | never public — Google-internal only | | |
| Store-listing contact details (email required, phone/website optional) | public on the listing | | |

Caveats:
- "In certain regions, developers are required to provide additional information which may be
  displayed on Google Play, like their phone number or full address." [3] The regions named in the
  country-specific requirements article are **Japan** (paid apps/IAP only), **Korea** (individuals
  must give a contact phone number; Korean personal accounts are blocked from publishing without a
  developer phone number) and **Brazil** (merchants). **Germany / the EU is not among them**; the
  only EU-specific requirement listed is the geo-blocking prohibition (Regulation (EU) 2018/302). [11]
- The **developer name may be a pseudonym**, but it does **not** replace the legal name — the legal
  name is published separately. [3]

### EU Digital Services Act / trader status

- DSA Art. 30/31 oblige online marketplaces to collect, verify and **publish** trader contact
  details (address, phone, email) for traders offering to EU consumers.
- **Finding:** as of 2026-08-25 I could **not** locate a Play Console Help article that asks Play
  developers to declare a trader/non-trader status — unlike Apple's App Store Connect and Google's
  own Chrome Web Store, both of which have an explicit trader-declaration flow. Google Play's
  implementation appears to run through the **merchant-account** distinction instead: developers who
  monetize (and are therefore traders) get their full address published; developers who do not, do
  not. This is an inference from [3] and [13], **not** a documented Google statement.
- **Practical read for Mulplu:** a free, non-commercial, ad-free hobby app is not "offering products
  or services to consumers" in the trader sense, and Play does not publish the address for
  non-monetizing accounts. **Risk if this is wrong:** adding any monetization later flips the
  account to merchant status and publishes the home address. Treat "stay free, no IAP" as a
  privacy-relevant decision, not just a product one.

### Address-hiding options, ranked

1. **Stay free / no IAP** — the address is simply not published. Cheapest and effective. Legal name
   is still published; that cannot be avoided on a personal account.
2. **Organization account** — publishes a *business* address instead of a home one, but requires a
   real legal entity, a D-U-N-S number, a verified website, and additionally publishes a **developer
   phone number**. Net worse for a hobby project.
3. **Mail-forwarding / c/o address** — **not recommended.** The legal address comes from the Google
   payments profile and must match the proof-of-address document submitted during identity
   verification; a mismatch is the primary documented cause of verification failure, and stale or
   unverifiable identity data can get the account restricted and all apps removed. [3][5][6]

---

## 4. Realistic end-to-end timeline

Hard floors from the documentation, plus estimates (marked *est.*) for the parts Google does not
quantify.

| Phase | Duration | Blocking? |
| --- | --- | --- |
| 1. Sign up, pay US$25, accept DDA, create payments profile | same day | — |
| 2. Identity verification (gov ID + proof of address), Google review | "a few days" → **2–7 days** [5] | blocks publishing |
| 3. Device verification via Play Console mobile app (Android 10+) | < 1 minute [7] | blocks publishing |
| 4. App setup: release signing key + AAB, store listing, screenshots, icon, privacy policy, content rating (IARC), Data Safety, target-audience declaration | *est.* **3–10 days** for this repo (see #39: no keystore, no AAB path, no store assets, no privacy policy) | blocks closed testing |
| 5. Recruit ≥ 12 testers and get them opted in | *est.* **2–14 days**, highest-variance step | blocks the 14-day clock |
| 6. Closed test running, 12 testers opted in continuously | **≥ 14 days, hard floor** [4] | — |
| 7. Apply for production access; Google review | "seven days or less, occasionally longer" → **3–10 days** [4] | — |
| 8. First production release review (new account, children's app) | *est.* **1–7 days**, often longer for family-policy apps | — |

**Optimistic:** ~4 weeks. **Realistic:** **5–8 weeks**. **If tester recruitment stalls or the
production application is rejected for insufficient engagement, add another 14+ days per attempt.**

Note that phases 2–4 can run in parallel with each other, but **phase 6 cannot start before phase 4
is complete** ("You can start a closed test after completing your app setup" [4]), so app setup is
on the critical path.

---

## 5. Announced / recent changes, and open points

**In force or imminent**

- **Android developer verification** — announced 2025-08-25 [12], rolled out to all developers in
  Play Console and the new Android Developer Console in **March 2026** [8]. User-facing enforcement
  starts **2026-09-30** in **Brazil, Indonesia, Singapore, Thailand** across seven stores (Play,
  HONOR, OPPO, Galaxy Store, Palm Store, V-Appstore, GetApps); **global rollout in 2027**. From that
  date, unregistered apps on certified devices can only be installed via **ADB or the "advanced
  flow"**. [8][9]
  - **Impact on Mulplu: none extra.** "If you've completed Play Console's developer verification
    requirements, your identity is already verified and we'll automatically register eligible Play
    apps for you." Over 99% of Play apps were auto-registered. Play developers must still confirm on
    the Play Console Home page that each app is registered **by 2026-09-30** to avoid removal
    (Play Console Requirements update of **2026-07-15**). [9]
- **Limited distribution accounts** (Android Developer Console) — free, **no fee, no government ID**,
  up to **20 devices**, for students/hobbyists. Early access **July 2026**, **global launch August
  2026**. [8][9]
  - **Relevant as a fallback, not a solution:** it distributes *outside* Play, so it cannot satisfy
    map #39's destination ("installed from the Play Store" on a Family Link device). But it is a
    legitimate stopgap channel for the sideloading phase, and it removes the ID requirement.
- **Advanced flow** for sideloading unverified apps — global **August 2026**. [9]
- Tester minimum reduced **20 → 12** on **2024-12-11**. [10]
- Website verification for new **organization** accounts — **February 2024**. [5]

**Not confirmed / uncertain**

- No documented definition of a "valid" tester (unique Google account, real device, minimum session
  count). Secondary sources assert stricter rules than Google publishes.
- No Play-specific DSA trader-status declaration flow found (see §3). If one exists it is inside the
  Console UI and not in the public Help Center.
- Whether a 2026 Play policy change tightened *tester engagement* checks beyond the existing
  "insufficient tester engagement" rejection reason — asserted by secondary blogs, **not confirmed**
  in Google documentation.
- Identity-verification turnaround is documented only as "a few days"; the 2–7 day figure is an
  estimate.

---

## 6. Sources

All URLs fetched 2026-08-25.

1. *Get started with Play Console* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/6112435
2. *Choose a developer account type* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/13634885
3. *Required information to create a Play Console developer account* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/13628312
4. *App testing requirements for new personal developer accounts* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/14151465
5. *Verify your developer identity information* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/10841920
6. *Google Play Developer Verification: Required documents by country and region* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/15633622
7. *Device verification requirements for new developer accounts* — Play Console Help.
   https://support.google.com/googleplay/android-developer/answer/14316361
8. *Android developer verification: Rolling out to all developers on Play Console and Android
   Developer Console* — Android Developers Blog, March 2026.
   https://android-developers.googleblog.com/2026/03/android-developer-verification-rolling-out-to-all-developers.html
9. *Android developer verification: Building a safer ecosystem together* — Android Developers Blog,
   June 2026, updated 2026-07-15.
   https://android-developers.googleblog.com/2026/06/android-developer-verification.html
10. *Ensuring high-quality apps on Google Play* — Android Developers Blog, 2023-11-13, with the
    **2024-12-11 update** reducing 20 testers to 12.
    https://android-developers.googleblog.com/2023/11/ensuring-high-quality-apps-on-google-play.html
11. *Requirements for distributing apps in specific countries/regions* — Play Console Help.
    https://support.google.com/googleplay/android-developer/answer/6223646
12. *A new layer of security for certified Android devices* — Android Developers Blog, 2025-08-25.
    https://android-developers.googleblog.com/2025/08/elevating-android-security.html
13. *View and manage your developer account information (for Play Console Requirements-verified
    accounts)* — Play Console Help.
    https://support.google.com/googleplay/android-developer/answer/13634081
