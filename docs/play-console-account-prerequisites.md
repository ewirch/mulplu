# Play Console prerequisites for a legacy personal developer account

Research notes, gathered 2026-08-31. All statements are general Google policy; nothing here
inspects a specific account. Confidence and sources are given per claim. Flags mark places
where only third-party sources exist or where sources conflict.

## 1. Closed-test precondition (12 testers / 14 days)

**Scope.** The requirement is scoped by *account type* **and** *account creation date*.
Google's help page is literally titled "App testing requirements for **new** personal
developer accounts" and states:

> "Google Play requires personal developer accounts created after November 13, 2023, to test
> their apps before those apps are eligible for distribution on Google Play."

> "Developers with personal accounts created after November 13, 2023, must run a closed test
> for their app with a minimum of 12 testers who have been opted in continuously for at least
> 14 days."

> "Testers who opt in, test for fewer than 14 days, and then opt out do not count toward the
> requirement."

- Source: <https://support.google.com/googleplay/android-developer/answer/14151465?hl=en>
  (no visible last-updated stamp; content current as of 2026-08-31)
- Original announcement (20 testers, 2 weeks, "newly created personal Play Console
  accounts"): Kobi Gluck, Google Play, 2023-11-09 —
  <https://android-developers.googleblog.com/2023/11/ensuring-high-quality-apps-on-google-play.html>
- Reduction 20 -> 12 testers: 2024-12-11 update appended to that same blog post. The 14-day
  duration was **not** changed.

**The 13 November 2023 cutoff is real and still in force in 2026.** Confidence: high.

**Are pre-cutoff personal accounts exempt?** Confidence: medium-high, with a caveat. Google
states the rule *applies to* accounts created after 2023-11-13 and titles the page "new
personal developer accounts"; it never publishes an explicit sentence such as "accounts
created before that date are exempt". The exemption is an inference from the inclusion
wording. Every third-party source agrees older personal accounts and all organization
accounts publish straight to production.
**FLAG:** exemption wording is third-party only; the primary source only carries the
inclusion statement.

**Changes since:** only the 20 -> 12 tester reduction (2024-12-11). No evidence of the rule
being extended to older accounts, nor of the 14-day window changing, nor of the rule being
withdrawn. Confidence: high (checked for 2025/2026 announcements; none found).

**Practical check (not a policy claim):** in Play Console the Dashboard shows a "Production
access" card, and Test and release > Production / Pre-registration stay disabled until the
requirement is met. **FLAG:** this UI description is third-party sourced; Google's page only
documents "apply for production access on the Dashboard in Play Console".

Note that a market exists for buying pre-November-2023 accounts specifically to dodge this
rule. Account transfer/sale outside Google's documented app-transfer process violates the
Play Developer Distribution Agreement; do not go there.

## 2. Identity verification for legacy accounts

**Scope and waves.** All accounts created **before September 2023** (personal and
organization) had to complete identity verification. Accounts created from 2023-07-31 onward
collected it at signup.

- Policy announced 2023-07-12.
- Deadline selection window: 2023-11-08 to 2024-02-29, first come first served. If no
  deadline was chosen by 2024-02-29, Google assigned one automatically, viewable on the
  **Account details** page.
- Verification window: May 2024 – February 2025. One 90-day extension available.
- Personal accounts supplied: legal name and address (via the linked Google payments
  profile), private contact email and phone (each OTP-verified), a public-facing developer
  email, preferred language; a government photo ID where the payments profile was not
  already verified.
- Organization accounts additionally: D-U-N-S number and business registration document.

Source: <https://support.google.com/googleplay/android-developer/answer/14177239> and
<https://support.google.com/googleplay/android-developer/answer/10841920?hl=en>.
Confidence: high.

**Consequence of non-completion**, Google's wording:

> "If you are unable to complete verifications by your chosen deadline, your developer
> profile and apps will be removed from Google Play."

> "If Google can't verify your developer information or your contact information, your
> developer presence and apps may be removed from Google Play, and you won't be able to
> republish your app until you've verified your information."

Confidence: high. Note the second sentence: an unverified account is blocked from
publishing, which is exactly the state a dormant unverified account would be in today.

**Once complete.** Verification does not expire on a documented schedule. Re-verification is
**event-triggered**: a change of legal name or address, or an updated Dun & Bradstreet
profile, prompts re-verification by email
(<https://support.google.com/googleplay/android-developer/answer/13634888?hl=en>).
Confidence: high that there is no fixed periodic cycle; medium on completeness.

**Where verification status is visible.** Documented locations:

- **Play Console home page** — banner while verification is in progress ("Google is
  verifying your identity. This may take a few days.") and, within 60 days of an assigned
  deadline, a notification with a **Get started** button.
- **Account details** page — verification progress, outstanding actions, and the assigned
  deadline.
- **Developer account > About you** and **Developer account > Contact details** — the
  verified identity and contact fields themselves
  (<https://support.google.com/googleplay/android-developer/answer/13634081?hl=en>).
- Completion is confirmed by email: "The account owner will receive an email when
  verification is complete. This may take a few days."

**FLAG:** Google's help pages name sections but never print a full literal breadcrumb
(e.g. "Settings > Developer account > ..."). The terms "Verification Centre" and a "Verify
your identity" task tile appear only in third-party write-ups, not in Google's own text.
Confidence: medium on exact UI labels, high that Account details is the page.

**EU DSA trader status.** Exists as a layer on top (declare whether you act as a trader;
traders supply legal entity, address, email, phone). **FLAG: no primary Google help-center
page dedicated to trader status could be found.** All specifics came from third-party
sources (makaka.org, verasafe.com, appsonair.com). The Google page on EEA access conditions
(<https://support.google.com/googleplay/android-developer/answer/14659200?hl=en>) covers the
DMA, not the DSA, and does not corroborate the trader-status mechanics. Confidence: medium
that the requirement exists, low on mechanics and on whether legacy personal accounts have
any carve-out.

**New for 2026 — Android developer verification (separate program).** Rolling out to all
Android developers, not just Play. Google:

> "If you've completed Play Console's developer verification requirements, your identity is
> already verified and we'll automatically register eligible Play apps for you."

> "In the rare case that we are unable to register your apps for you, you will need to follow
> the manual app claim process."

Enforcement: from **2026-09-30** for certified Android devices in Brazil, Indonesia,
Singapore and Thailand; global from 2027 onward. The guide adds: "By September 30, 2026,
register any remaining apps you want to continue distributing to avoid global removal from
Google Play."

- <https://android-developers.googleblog.com/2026/03/android-developer-verification-rolling-out-to-all-developers.html> (2026-03-30)
- <https://developer.android.com/developer-verification/guides/google-play-console> (last updated 2026-08-18)
- <https://developer.android.com/developer-verification>

Confidence: high. A dormant, never-published account has no apps to auto-register, so the
practical action is: check the Play Console Home page for unregistered apps after the first
publish. **FLAG:** the blog does not address the never-published case explicitly.

No evidence found for a public "verified developer" badge on store listings. Treat as
unconfirmed.

## 3. Account type, developer ID, creation date

**Page:** Play Console > **Developer account > About you** (documented at
<https://support.google.com/googleplay/android-developer/answer/13634081?hl=en>). Fields:

> "Account type (read only): The Google Play developer account type you selected during sign
> up"

> "Account ID (read only): Your unique 19-digit Google Play Developer account ID"

Also on that page: developer name; for organizations the read-only legal name, address and
D-U-N-S. Contact fields live on the adjacent **Contact details** tab.
Confidence: high for account type and account ID.

**Creation date: not exposed in the Play Console UI.** No Google documentation mentions a
"created on", "member since" or equivalent field anywhere in Play Console. Confidence:
medium-high (absence of evidence across the account-information help pages, plus community
threads asking the question with no official answer).
**FLAG:** this is a negative finding; Google does not state "we do not show this".

Proxies, in descending reliability:

1. **The US$25 registration-fee receipt.** The confirmation email sent when the fee was paid,
   or the transaction in the Google payments transaction history at pay.google.com /
   payments.google.com. See section 4.
2. **Whether the account went through the legacy verification flow.** The flow at
   answer/14177239 only ever applied to accounts created **before September 2023**. If the
   account was assigned a verification deadline in 2023/2024, it necessarily predates the
   2023-11-13 closed-testing cutoff.
3. **Whether Production is available in Play Console.** If Test and release > Production is
   not gated behind a "Production access" application, the closed-test rule is not applied to
   the account. This is the operationally decisive signal even though it is not documented as
   a creation-date indicator.
4. Old Google Play / Google Payments email in the account's mailbox.

Google support will not issue receipts or invoices, so route 1 depends on records the
developer still holds.

## 4. Registration fee

**One-time, never re-charged.** Google: "There is a US$25 one-time registration fee that you
can pay with the following credit or debit cards"
(<https://support.google.com/googleplay/android-developer/answer/6112435?hl=en>). No
renewal, no annual fee. Confidence: high.

**Refundability.** Not refundable in the general case: "Your registration fee is not
refundable and will be forfeited" for dormant/closed accounts, and on termination for policy
violations "Any new account that you try to open will be terminated as well (without a refund
of the developer registration fee)"
(<https://support.google.com/googleplay/android-developer/answer/9899234>).
**Documented exception:** after a completed app transfer to another account, "if you want to
close your original account, our support team can refund the registration fee for that
account" (<https://support.google.com/googleplay/android-developer/answer/6230247?hl=en>).
**FLAG:** these two pages appear to conflict; the reconciliation is that the transfer refund
is a narrow, voluntary-closure-after-transfer case. Confidence: high on both quotes, medium
on the reconciliation.

**Receipt location** (<https://support.google.com/googleplay/android-developer/answer/9875040?hl=en>):

> "A confirmation email is sent to you after you pay for your Google Play developer account
> registration fee."

> "You can also view your transaction history in GPay." (payments.google.com — sign in with
> the same Google account used to create the developer account.)

> "Our support team is unable to provide registration receipts or invoices."

There is **no** documented Play Console path to the registration-fee receipt. Confidence:
high.

**Regional variation:** could not be established. Google's pages say only "US$25" and note
that accepted card types vary by location. No primary source on local-currency or regional
pricing. Confidence: low / unresolved.

## 5. Inactivity closure — the biggest risk in this scenario

Source: "Closure of inactive developer accounts",
<https://support.google.com/googleplay/android-developer/answer/11605267?hl=en> (no visible
last-updated stamp). Two criteria sets; meeting either makes the account eligible for
closure.

**Set A — accounts without apps:**

> "The developer account was created more than a year ago"
> "It has never submitted an app for review"

**Set B — accounts with apps:**

> "The developer account was created more than a year ago"
> "All published apps (including live, removed, and suspended apps) in the account have less
> than 1,000 combined lifetime installs"
> "It has not verified their Play Developer account phone number and contact email address"
> "It has not used Play Console in the last 180 days"

**This is the direct answer to "does an old account that never published risk closure": yes.**
Set A has only two conditions and does **not** require Play Console inactivity — an account
older than a year that has never submitted an app for review qualifies on its face.
Confidence: high.

**Warning process.** Email to the account owner plus an in-console message "with steps to
take and a deadline for completion". "Reminder emails are sent with 60, 30, and 7 days
notice." Confidence: high.

**Prevention** — both of:

1. Verify contact email and phone on the Account details page.
2. Create and publish an app, or publish an update to an existing app.

The page mentions Internal app sharing, Internal testing and Closed testing as ways to
upload without going public. **FLAG:** whether a closed-testing-only or draft app satisfies
"submitted an app for review" is **not stated unambiguously**. Confidence: medium. Do not
rely on a draft counting.

**After closure.** Fee forfeited, not refunded. Re-registration is explicitly allowed for
dormancy closures: "Closure of a dormant account will not limit your ability to create a new
account in the future if you decide to publish on Google Play." That is materially different
from **termination** for policy violations, where new accounts are terminated too.
Confidence: high on the quotes.
**Not documented:** whether the developer name, developer ID or package names are released
or permanently burned after closure. Unresolved.

**Google account inactivity (separate policy).** Consumer Google accounts inactive for 2
years may be deleted; earliest deletion date was 2023-12-01; Workspace/school/work accounts
excluded (<https://support.google.com/accounts/answer/12418290?hl=en>). Whether deletion of
the underlying Google account cascades to the Play developer account is **not documented**.
Two separate systems, different thresholds. Unresolved.

## 6. Everything else a returning developer must do that a new registrant would not

### Legacy-specific (confirmed)

1. **Complete the pre-September-2023 account verification** if it was never done
   (section 2). Consequence of not doing it is removal of developer profile plus a block on
   publishing. This is the single clearest legacy-only obligation. Confidence: high.
2. **Verify contact email and phone** and get an app submitted, to clear the dormancy clock
   (section 5). Overlaps heavily with item 1. Confidence: high.
3. **Link / revalidate a Google payments profile.** Google: "The Google Play Console uses a
   Google payments profile to verify the legal name and address of all developer accounts"
   (<https://support.google.com/googleplay/android-developer/answer/16260648?hl=en>, footer
   dated 2026). So a payments profile is needed for verification regardless of monetization,
   not only to sell. Update path: Play Console > **About you** > **Update details** > **Go to
   Google Payments Center** > edit > Save; "To complete the update, you might need to upload
   identity documents." Country, account type (individual vs organization) and D-U-N-S
   **cannot** be changed on an existing payments profile — that needs a new profile.
   Confidence: high for the quote and the path; **FLAG:** whether a free-app-only publisher
   can proceed with no payments profile at all could not be settled from Google's own text —
   the monetization-oriented pages
   (<https://support.google.com/googleplay/android-developer/answer/7161426?hl=en>) read
   ambiguously and appear to conflict with the verification page. Unresolved.
4. **Possible re-acceptance of updated agreements.** Developer Program Policies have rolling
   effective dates (versions found effective 2026-01-01, 2026-05-27, 2026-07-15). **FLAG:**
   the DDA page redirected repeatedly and could not be fetched cleanly, so the current DDA
   effective date could not be established. That a changed-terms screen blocks Play Console
   on next sign-in is third-party/unverified. Confidence: low. Assume there will be something
   to accept; do not rely on a specific date.

### Spared, relative to a new registrant

- **Device verification via the Play Console mobile app** applies to "developers with new
  personal accounts", "Starting in early 2024"
  (<https://support.google.com/googleplay/android-developer/answer/14316361>). **FLAG:** the
  page does not explicitly exempt older accounts; it only scopes itself to new ones.
  Confidence: medium.
- The 12-testers / 14-days closed test, if the account predates 2023-11-13 (section 1).

### Universal in 2026 — same for old and new accounts

- **App content checklist:** privacy policy, ads declaration, app access instructions, target
  audience and content (including Families policy), content ratings (IARC), data safety form
  (required for every app on closed/open/production tracks, even zero-data apps), financial
  features declaration (mandatory since 2023-08-31 for *every* app, including an explicit
  "none"). Official page: "Prepare your app for review",
  <https://support.google.com/googleplay/android-developer/answer/9859455>. Confidence: high.
- **News/magazine self-declaration**, deadline 2026-05-27, non-compliant apps removed.
  Confidence: medium (search-derived, pages answer/16550159 and /16926792 not fetched
  directly).
- **Health & Fitness / Health Connect** permission-category updates in 2025/2026. **FLAG:**
  third-party summary only. Low confidence.
- **Target API level:** new apps and updates must target Android 16 (API 36) by 2026-08-31,
  with an extension to 2026-11-01 available on request; Wear OS/Automotive API 35, TV/XR API
  34 (<https://support.google.com/googleplay/android-developer/answer/11926878>). Confidence:
  high. Note this deadline is essentially now.
- **Android App Bundle** mandatory for new apps (since August 2021) and **Play App Signing**
  enrolment forced for new apps
  (<https://support.google.com/googleplay/android-developer/answer/9842756>). Confidence:
  high / medium.
- **16 KB page size:** since 2025-11-01, new apps and updates targeting API 35+ with native
  code must be 16 KB-aligned; enforced as an upload blocker. Pure Kotlin/Java apps with no
  native libraries are compliant automatically
  (<https://android-developers.googleblog.com/2025/05/prepare-play-apps-for-devices-with-16kb-page-size.html>).
  Confidence: high.
- **64-bit:** the general requirement dates from August 2019, not new. A narrower Wear OS
  64-bit enforcement is new for 2026
  (<https://android-developers.googleblog.com/2026/04/get-your-wear-os-apps-ready-for-64-bit-requirement.html>).
  Confidence: medium.
- **Restricted permissions:** MANAGE_EXTERNAL_STORAGE justification mandatory since
  2025-05-28 (answer/10467955); USE_FULL_SCREEN_INTENT restricted by default since
  2025-01-22 for apps targeting API 34+ (answer/13392821). Confidence: medium (not fetched
  directly). QUERY_ALL_PACKAGES and foreground-service-type declaration mechanics: **not
  established**.
- **2-Step Verification:** required for new Play Console users from Q3 2020 and rolled out to
  existing users with high-risk permissions later that year
  (2020 Android Developers Blog). The current help page "Protect your developer account"
  (<https://support.google.com/googleplay/android-developer/answer/2543765>) frames 2SV as
  recommended rather than mandatory. **FLAG: sources conflict.** Whether 2SV is a hard
  universal gate in 2026 could not be settled. Confidence: medium on history, low on current
  universality. Turn it on regardless.
- **Store listing contact details:** an email address is mandatory; phone and website
  optional. For a personal account, only the email is shown publicly **unless** the account
  monetizes (paid apps or IAP), in which case the full legal address from the payments
  profile is displayed. **FLAG:** the trigger conditions are partly third-party sourced
  (PhoneArena, XDA) and answer/13634081, /13634888, /13628312 were not all fetched directly.
  Confidence: medium. Regional rules may require a public phone or address regardless.

## Could not be established

- Explicit Google wording that pre-2023-11-13 personal accounts are exempt from the closed
  test (only the inclusion statement exists).
- Any Play Console UI surface showing the account creation date.
- Current DDA effective date and the re-acceptance UX.
- Whether a payments profile is strictly required to publish a free-only app.
- Whether 2SV is a hard universal requirement in 2026.
- Whether closed-testing-only or draft apps satisfy "submitted an app for review" for
  dormancy purposes.
- Regional variation of the US$25 fee.
- Fate of the developer name / developer ID / package names after account closure.
- Interaction between the 2-year Google account inactivity policy and Play developer accounts.
- A primary Google page for DSA trader status.
- Whether a public "verified developer" badge exists.
