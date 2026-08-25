# Family Link: what a supervised child account is allowed to install

Research for [#41](https://github.com/ewirch/mulplu/issues/41), part of the map
[#39](https://github.com/ewirch/mulplu/issues/39).

Researched 2026-08-25. All Google support pages were accessed on 2026-08-25; where a page
carries a copyright year it is noted. Google's help pages are undated by design, so
"accessed 2026-08-25" is the only date available for most primary claims.

Every claim below is tagged:

- **[documented]** — stated on an official Google page (support.google.com, developer.android.com).
- **[inferred]** — follows from documented mechanics, but no Google page says it.
- **[community]** — forum, vendor blog, or press report. Not authoritative.

---

## 0. Answer in one paragraph

The Play Store is a working install channel for a Family Link-supervised child account,
and the parent is the gate: with "Require approval for: All content" the child's install
attempt raises a request that the parent approves either remotely (push notification to
the Family Link app) or on the child's device with the parent's Google password. Approval
is once per app — reinstalls and normal updates do not re-ask. The second gate is the
content-rating filter: the parent picks a maximum maturity level per content type, and an
app above it cannot be installed and will not be offered as an update. Neither gate needs
anything special from the app beyond a completed IARC content rating and a target-audience
declaration that includes children. Sideloading is blocked by default
(`DISALLOW_INSTALL_UNKNOWN_SOURCES`) but the parent can lift it per device; `adb install`
is documented to be *outside* that restriction, so it remains the reliable stopgap as long
as the parent can enable developer options. Test tracks are the weak spot: the Google
Groups tester path is **documented to be impossible** for child accounts, the email-list
path is undocumented either way, and a second user profile is not a viable side channel
because supervision blocks adding users.

---

## 1. The Play install path and parental approval

**[documented]** Approval is controlled per child by "Purchases & download approvals":
Family Link app → child → Controls → Google Play → **"Require approval for"**, with the
options *All content* / *Paid content only* / *In-app purchases only* / *Never*.
Source: [Purchase approvals on Google Play](https://support.google.com/families/answer/7039872?hl=en&co=GENIE.Platform%3DAndroid),
accessed 2026-08-25.

**[documented]** *All content* covers free downloads, not only purchases — the child needs
approval to "download or access content" at no charge. (Same source.)

**[documented]** When approval is required, the child sees a screen with two options:

- **"Ask in a message"** — asynchronous. The parent gets a push notification, opens Family
  Link (or `familylink.google.com/notifications`) and taps Approve or Deny.
- **"Ask now"** — in person. The parent enters their **Google Account password on the
  child's device** and taps Approve.

(Same source.) The same source warns that disabling notifications for the Family Link app
or the Play Store app means the parent is not notified of requests.

**[not documented]** Whether approval-required is *on by default* for a newly supervised
account is not stated on any Google page found. **[community]** Play Help community threads
describe approval-for-all-content as the out-of-the-box behaviour. Treat the default as
unknown; the parent should check the setting explicitly.

### Updates, reinstalls, pre-existing apps

**[documented]** "Apps that you previously installed or approved, or those available through
your Play Family Library will not require additional approval to download." — so approval
is once per app; reinstall does not re-ask.
Source: [Manage your child's Google Play apps](https://support.google.com/families/answer/7103028?hl=en),
accessed 2026-08-25.

**[documented]** Updates are not re-approved as such, but the rating filter still applies:
"If the new version of the app has a higher rating than your content restrictions allow,
you won't be prompted to update the app."
Source: [How to set up content restrictions on Google Play](https://support.google.com/families/answer/1075738?hl=en),
accessed 2026-08-25.

**[documented]** The rating filter is **not retroactive**: apps installed before the
restriction was set stay on the device even if they now exceed it; the parent must block
them manually via App limits. (Same source.)

---

## 2. Content rating and the age setting

**[documented]** Play content ratings come from one Play Console IARC questionnaire, mapped
to region authorities: **ESRB** (Americas), **PEGI** (Europe/Middle East), **USK**
(Germany), ACB, ClassInd, GRAC, plus a generic IARC rating elsewhere.
Source: [Content rating requirements for apps, games, and the ads served on both](https://support.google.com/googleplay/android-developer/answer/9859655?hl=en),
accessed 2026-08-25.

For Mulplu in Germany this means the **USK** rating is the one the parent's filter compares
against. A no-content, no-network arithmetic trainer should classify at the lowest tier, so
the rating filter is not expected to be a practical obstacle.

**[documented]** The parent sets the filter at Family Link app → child → Controls → Google
Play → "Apps & games" (also Movies, TV, Books) and picks "the highest maturity level of
content you want to allow for download or purchase". Changing it requires the parent's
Google Account password.
Source: [How to set up content restrictions on Google Play](https://support.google.com/families/answer/1075738?hl=en),
accessed 2026-08-25.

**[documented]** The filter blocks the *install/purchase action*, not discovery: "Content
restrictions don't prevent restricted content as a search result or through a direct
link." (Same source.) So a direct Play link to the app still opens for the child even if
the app were above the filter — the block only fires on install.

**[documented]** Exception in the EEA (which includes Germany), Australia, Brazil,
Singapore, Switzerland and the UK: for users determined to be minors, Play additionally
blocks acquisition of mature content and filters mature content from search and browse
pages, "except through deep linking".
Source: [Content rating requirements…](https://support.google.com/googleplay/android-developer/answer/9859655?hl=en),
accessed 2026-08-25.

**[not documented]** How an app that is temporarily *unrated* (questionnaire not yet
completed) behaves against a child's rating filter. Practical consequence: complete the
IARC questionnaire before expecting a supervised install to work. An app that ends up
"Refused classification" is removed from Play in the affected territory. (Same source.)

### Hard age gates independent of the parent's settings

**[documented]** Two failure messages a supervised child can hit regardless of parental
configuration:

- "This app isn't available for kids at this time" — for Google apps not available to
  children under 13 (or the local age).
- "The developer has restricted access to this app for accounts of anyone under 18 years of
  age" — when the *developer* opted the app out of under-18 availability via the Play
  Console target-audience settings.

Source: [Manage your child's Google Play apps](https://support.google.com/families/answer/7103028?hl=en),
accessed 2026-08-25.

**Direct consequence for Mulplu:** the Play Console "Target audience and content"
declaration must include a children's age bracket. Declaring an 18+ target audience would
make the app uninstallable for the child regardless of what the parent approves. Declaring
children as target audience in turn pulls the app into **Play Families policy** (certified
ad SDKs, no personalised ads to children, privacy requirements) — for a no-ads, no-network,
no-SDK app this is compliance paperwork, not code.
Source: [Content rating requirements…](https://support.google.com/googleplay/android-developer/answer/9859655?hl=en),
accessed 2026-08-25.

**[documented]** "Teacher Approved" is a curation badge on top of Play Families policy
compliance, not an installability gate. Nothing requires it for a supervised account to
install an app.
Sources: [Manage your child's Google Play apps](https://support.google.com/families/answer/7103028?hl=en);
[Android Developers Blog, 2020-04](https://android-developers.googleblog.com/2020/04/promoting-high-quality-teacher-approved.html).

---

## 3. Test tracks: internal, closed, open

**[documented]** All tracks: "Users need a Google Account or a Google Workspace account to
join a test." No age or adulthood qualifier is stated anywhere on the page. Internal
testing: up to 100 testers, email list only, live within minutes, no policy review. Closed
testing: email lists (up to 200 lists × 2,000 users) **or** Google Groups. Open testing:
visible on the store listing / via a public opt-in URL.
Source: [Set up an open, closed, or internal test](https://support.google.com/googleplay/android-developer/answer/9845334?hl=en),
accessed 2026-08-25 (page footer: ©2026 Google).

**[documented — the one hard restriction found]** "Child accounts — You can't add a user
with a child Google Account to a group through any method."
Source: [Add people to your group – Google Groups Help](https://support.google.com/groups/answer/2465464?hl=en),
accessed 2026-08-25.

Consequence: a Family Link-supervised child account **cannot** be a tester via the Google
Groups path of closed testing. This is documented and unambiguous.

**[not documented]** Whether a child account can be added to a Play Console tester
**email list** (internal, closed, or open testing) or to Internal App Sharing. Play Console
Help is silent on account type. The Google Groups restriction is account-type-based and
does not, on its face, extend to email lists — but no page confirms the email-list path
works either. **This is the single largest open question in this research and needs an
empirical test on the actual account.**

**[inferred]** A test-track install still happens through the Play Store app on the child's
device, so the "Require approval for: All content" gate should fire the same way for the
first install of a test build. No Google page confirms or denies this for test tracks,
opt-in links, or Internal App Sharing.

**[documented]** The closed-testing requirement for personal developer accounts created
after 2023-11-13 is still in force in 2026: "At least 12 testers must be opted in to your
closed test when you apply for production access, and they must have been opted in
continuously for the preceding 14 days."
Source: [App testing requirements for new personal developer accounts](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en),
accessed 2026-08-25 (©2026 Google). **[community]** The figure was reduced from 20 to 12 in
December 2024 ([RevenueCat blog](https://www.revenuecat.com/blog/engineering/google-play-14-day)) —
several tester-recruitment sites still quote the stale "20", e.g.
[HappyTestr](https://happytestr.com/blog/google-play-closed-testing-requirements), accessed
2026-08-25. Trust the Play Console page, not the blogs.

**[not documented]** No Google page states eligibility criteria for who counts toward the
12 testers — no exclusion of family members, no requirement that testers be adults.

**[documented]** Internal App Sharing is a separate channel: upload → shareable link (60-day
expiry, max 100 downloaders), testers managed as "anyone with the link" or via an authorised
email list; there is no Google Groups option, so the child-account Groups block does not
apply. The tester must enable internal app sharing in their Play Store app settings first.
Source: [Share app bundles and APKs internally](https://support.google.com/googleplay/android-developer/answer/9844679?hl=en),
accessed 2026-08-25. Whether a supervised account can flip that Play Store setting is
**not documented**.

---

## 4. What supervision blocks locally

**[documented]** `DISALLOW_INSTALL_UNKNOWN_SOURCES` — "Specifies if a user is disallowed
from enabling the 'Unknown Sources' setting, that allows installation of apps from unknown
sources. **Unknown sources exclude adb** and special apps such as trusted app stores."
Source: Android `UserManager` reference, developer.android.com, accessed 2026-08-25 (via
search snippet; direct fetch of developer.android.com was blocked in this environment).

The javadoc's explicit adb carve-out is the key technical fact: the unknown-sources
restriction is a Settings-UI gate on the `PackageInstaller` path, and does **not** cover
`adb install` / `pm install`.

**[documented]** `DISALLOW_DEBUGGING_FEATURES` — "Specifies if a user is disallowed from
enabling or accessing debugging features, that are available under Developer options."
Same source, same caveat.

**[community]** That Family Link actually sets these on the child's primary user is
corroborated by a live Android 14 / Pixel report
([Google Pixel Community thread 245464682](https://support.google.com/pixelphone/thread/245464682/family-link-not-allowing-installation-of-apps-from-unknown-sources-android-14-pixel-5?hl=en),
accessed 2026-08-25) and by vendor write-ups
([Bitdefender, 2025](https://www.bitdefender.com/en-us/blog/hotforsecurity/family-link-bypass-android-2025)).
**No official Google page confirming "Family Link blocks Developer Options" was located.**

**[community]** The parent can lift the unknown-sources restriction per device: Family Link
parent app → child → the device entry → toggle "Apps from unknown sources". Reported
consistently across guides and vendor support docs; the exact UI label was **not** verified
against a support.google.com page in this session (support.google.com WebFetch was blocked
for the verification pass).

**[inferred, medium-high confidence]** `adb install` on a supervised device is not blocked
by Family Link: the unknown-sources restriction explicitly excludes adb, and the Play
parental-approval gate lives in the Play Store app, not in `PackageInstaller`. The real
barrier is `DISALLOW_DEBUGGING_FEATURES` preventing developer options from being turned on
in the first place — which the parent controls. **[community]** Bypass tooling such as
[rifting/chronolink](https://github.com/rifting/chronolink) operates on this premise: once
USB debugging is on, adb has full shell access to the device including
`pm uninstall --user 0` of Family Link components.

**No documented statement exists, official or otherwise, that Android's package installer
inspects for a supervised account and rejects adb-installed APKs.**

---

## 5. Second user profile / work profile as a side channel

**[documented]** `DISALLOW_ADD_USER` — "Specifies if a user is disallowed from adding new
users and profiles… This can only be set by device owners and profile owners on the primary
user." Android `UserManager` reference, accessed 2026-08-25 (search snippet).

**[community / Google-adjacent]** On a supervised device the child cannot add a second user
profile by default; a parent-facing toggle to allow adding/removing users exists in Family
Link. Also: "If you add other user profiles to which your child's account isn't signed in,
your Family Link settings for your child's account won't apply to the new user profiles" —
i.e. Google acknowledges a second profile is *outside* supervision, which is exactly why
adding one is restricted.

**[community]** Family Link restricts adding additional Google accounts to the child's
profile; Google Workspace for Education is the documented exception.
([Google Account Community thread](https://support.google.com/accounts/thread/36064021/how-do-i-add-enable-the-addition-of-another-account-under-family-link?hl=en),
accessed 2026-08-25.)

**[inferred]** Work profile (Android Enterprise Profile Owner) is unrelated to Family Link
and there is no evidence Family Link uses or interacts with it. Setting one up on the child
device would require a real EMM and is a disproportionate side channel for shipping one
app.

**Verdict: a second profile is not a viable channel.** It is restricted by default, and even
if the parent lifted the restriction, the app would then live in a profile the child does
not use.

---

## 6. What Family Link is, technically

**[inferred, medium confidence]** Family Link is not an Android Enterprise Device Owner or
Profile Owner in the classical MDM sense: there is no enterprise enrolment, no EMM console,
no Android Management API policy. It is a **supervised Google Account flag** enforced
device-side by Google Play services plus the Family Link helper app
(`com.google.android.apps.kids.familylinkhelper`), which applies `UserManager` `DISALLOW_*`
restrictions to the primary user once the supervised account is the device's main account.
No primary Google source confirming or denying the DPC framing was found; consumer docs
never use Android Enterprise terminology, and enterprise docs treat Family Link as a
separate consumer product.

**[documented]** Full supervision (app approval, screen time, device restrictions) requires
the child's account to be the Android device's signed-in account; on iOS and the web only
account-level Google services are supervised.
Source: [Learn which devices can be supervised](https://support.google.com/families/answer/9116646?hl=en),
accessed 2026-08-25 (via search snippet).

---

## 7. Age thresholds

**[documented]** At 13 (or the local age of consent) Google emails parent and child; the
parent may keep supervision unchanged or move to the "supervision for children over 13"
flow. Supervision does **not** switch off automatically. "Children need parent approval to
stop supervision until they turn 18."
Source: [How Google Accounts work when children turn 13 (or the applicable age in your country)](https://support.google.com/families/answer/7106787?hl=en),
accessed 2026-08-25.

**[documented]** The local age of consent for self-managing a Google Account in **Germany is
16**; the default elsewhere is 13 (Austria 14, France 15, Netherlands 16, Poland 16, Italy
14, Spain 14).
Source: [Age requirements on Google Accounts](https://support.google.com/accounts/answer/1350409?hl=en),
accessed 2026-08-25.

Relevant only in that supervision will persist for years — the Play channel needs to keep
working under supervision, not be a temporary arrangement.

---

## 8. Consequences for the map (#39)

1. **Play production is a viable destination.** The parent (= the dev) approves the install
   once, in person via "Ask now" with their own Google password. Nothing about supervision
   blocks a low-rated, child-targeted app.
2. **Two Play Console declarations are load-bearing**: complete the IARC content rating
   (USK for Germany), and declare a children's age bracket as target audience. An 18+
   target-audience declaration would make the app uninstallable for the child no matter what
   the parent does. Declaring children pulls in Play Families policy.
3. **Do not plan the closed-testing phase around the child's account as a tester.** The
   Google Groups path is documented to be impossible; the email-list path is undocumented.
   Recruit the 12 testers from adult accounts.
4. **Sideloading as a stopgap: prefer `adb install`.** It is documented to sit outside
   `DISALLOW_INSTALL_UNKNOWN_SOURCES`, and the dev has physical access. It only requires the
   parent to permit developer options / USB debugging. Browser-download sideloading requires
   the parent to lift the unknown-sources restriction per device and is the messier path.
5. **A second user profile is a dead end** and should be dropped from consideration.

## 9. Open questions worth an empirical check

- Is "Require approval for" set to *All content* by default on this specific child account?
  (Check in the Family Link app.)
- Can the child's account be added to a Play Console **tester email list** and complete an
  opt-in — internal test track is the cheapest experiment.
- Does the parental-approval prompt fire for a test-track install?
- Can the supervised account enable "Internal app sharing" in its Play Store settings?
- Does the parent app actually expose a Developer options / USB debugging toggle for the
  child device, or only unknown sources?

## 10. Method and reliability caveat

Findings were gathered by three parallel research passes on 2026-08-25. The Play/Family Link
and Play Console passes fetched support.google.com pages directly and quote them verbatim.
The device-restrictions pass could not fetch pages (WebFetch blocked for
support.google.com, developer.android.com and cs.android.com in that environment) and rests
on search-result snippets of primary pages plus community sources — which is why section 4
carries weaker confidence tags than sections 1–3. A verification pass from this session also
hit the same fetch block. Anything in section 4 tagged [community] or [inferred] should be
confirmed on the actual device before it is relied on.
