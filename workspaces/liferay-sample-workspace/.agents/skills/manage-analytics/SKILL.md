---

description: Connect DXP to Liferay Analytics Cloud / Liferay Data Platform (LDP) and track custom events from fragments and client extensions with the Analytics SDK — `Analytics.track`, real-time segments, and checking that an event reaches LDP. Use when the user asks to connect DXP to Analytics Cloud or LDP, track a custom event from a fragment or a client extension, read the visitor's real-time segments, verify that an event arrives in LDP, or build an Event Analysis. Deciding when an event should fire belongs to other skills.
name: manage-analytics

---

# Manage Analytics

Liferay Analytics Cloud, now Liferay Data Platform (LDP), receives events from the Analytics SDK that DXP loads on the pages of every site it tracks. This skill covers the DXP side: connecting the instance, calling the SDK safely from browser code, and proving that an event arrived.

The SDK behavior below was verified on 2026.Q3, and the connection procedure is the GUI path on 2026.Q3. **LDP is a separately licensed product:** without an LDP workspace there is nothing to connect. On 2026.Q1, analytics attributes on object entries need flag `LPD-39437` (release, off by default), and the real-time segment call below does not exist.

## When to Invoke

- "Connect DXP to Analytics Cloud / LDP", "track this site"
- "Track a custom event" from a fragment or a client extension
- "Show content by the visitor's real-time segment"
- "Is my event arriving?", "build an Event Analysis"

This skill says **how** to track. When to fire belongs elsewhere: whether a Form Container was really submitted is `manage-form-containers` (the `innerText` watcher).

## Prerequisites — Connect the Instance

DXP must be reachable on a public URL; on a local bundle, put a tunnel in front of it.

1. In LDP, add this instance as a **Data Source** and copy its token.

1. In DXP, Control Panel → Instance Settings → **Analytics Cloud**: paste the token and save.

1. In LDP, select the sites to track.

1. Rename the Data Source to something specific. In a shared LDP workspace, a generic name cannot be told apart from everyone else's.

## Workflow

### 1. Detect the SDK Before Every Call

Until `Analytics.create()` has run during the site's bootstrap, `window.Analytics` is only `{create}`; it is then replaced by the SDK instance. On a site that is not connected, it never is, and the official documentation warns that a fragment using Analytics breaks there. Test before any call:

```javascript
if (typeof window.Analytics?.track === 'function') {
	// safe to call
}
```

### 2. `Analytics.track(eventName, properties)`

`track` is the documented API (learn.liferay.com → Analytics Cloud → Touchpoints → Tracking Events). `Analytics.send(id, appId, props)` is only a wrapper that calls `track`; use it only when its categorization argument is actually needed.

**Values must be a string, number, or boolean, at most 1024 characters.** Anything else makes the SDK throw an uncaught exception into the caller (`Attribute must be a String, Number, or Boolean.`, or `… exceeds maximum length of 1024`), and nothing is sent. Normalize every value, and wrap the call so that analytics never breaks the main flow:

```javascript
function trackSafely(eventName, properties) {
	if (typeof window.Analytics?.track !== 'function') {
		return;
	}

	const clean = {};

	for (const [key, value] of Object.entries(properties || {})) {
		if (value === null || value === undefined) {
			continue;
		}

		let text = Array.isArray(value) ? value.join(',') : value;

		if (typeof text === 'object') {
			text = JSON.stringify(text);
		}

		clean[key] = typeof text === 'string' ? text.slice(0, 1024) : text;
	}

	try {
		window.Analytics.track(eventName, clean);
	}
	catch (error) {
		console.warn('Analytics.track failed', error);
	}
}
```

**Read property values from the DOM with `textContent`, not `innerText`.** `innerText` applies CSS, so a mapped editable styled `text-transform: uppercase` sends `ACME` instead of the stored `Acme`, and the LDP breakdown no longer matches the data (verified on 2026.Q3.2). Better still, publish raw values as data attributes from FreeMarker (`INFO_ITEM.externalReferenceCode`, `objectEntryId`). The success watcher's `document.body.innerText` (`manage-form-containers`) is the opposite case: there `textContent` would match the script's own source.

**A redirect right after `track` does not lose the event. Add no delay.** `track` writes the event, already timestamped, to `localStorage` (`ac_message_queue`) synchronously. `QueueFlushService` sends the queue about every 2 s and removes a message only after a `200`. There is no `sendBeacon` and no `keepalive`, so a navigation aborts a send in progress, but the message stays queued and leaves from the next page on the same domain.

### 3. Name Events per Participant

In a shared LDP workspace or instance, suffix every custom event with an identifier of its author (`whitePaperDownloaded_jdoe`). Otherwise the reports mix everyone's events. Name saved analyses the same way.

### 4. Real-Time Segments

```javascript
const ercs = await window.Analytics.segment.getRealTimeSegmentExternalReferenceCodes();
```

The Promise resolves to an array of the ERCs of the visitor's real-time segments. The call exists from 2026.Q2; 2026.Q1 has no equivalent. **Confirmed live on 2026.Q3.2 but absent from the official documentation**, so detect `window.Analytics?.segment` before calling it, and say so to the user.

In LDP, a segment's ERC is editable on its detail screen and accepts only lowercase letters and dashes. Set a readable one (`white-paper-readers`) before writing code against it, rather than keeping the generated value.

### 5. Asset Events for Object Entries (No Code)

DXP tags object entries for Analytics by itself, so most asset tracking needs no `track` call. Verified on 2026.Q2.7 from source and with a Guest in headless Chrome:

- **In View mode, with the instance connected and no feature flag** (LPD-39437 is gone; the code is unconditional), every fragment editable mapped to an object entry gets `data-analytics-asset-type="object-entry"`, `-asset-id`, `-asset-title`, `-object-definition-name` (the stored name: `CMSBasicDocument`, or `C_<Name>` for a custom object), `-external-reference-code`, `-asset-field`, and tags and categories as JSON (`AnalyticsAttributesUtil`). The attributes go on the editable element itself; no wrapper is added.
- **`asset-action` is `view` for an HTML or long text field and `impression` for every other**, so the SDK (`analytics-client-js` `plugins/object-entry.ts`, `applicationId` `ObjectEntry`) sends `objectEntryViewed` or `objectEntryImpressionMade` once the element enters the viewport. Its payload is `externalReferenceCode`, `objectDefinitionName`, `title` and the tags, categories and MIME type: no numeric id.
- **A type with neither field type (a CMS Basic Document: title and file) gets impressions only.** An information template mapped into an HTML editable adds the view: a `TemplateEntry` field is an `HTMLInfoFieldType` (`TemplateInfoItemFieldSetProviderImpl`), so `ddmTemplate_<id>` is tagged `view`.
- **Nothing out of the box emits `objectEntryDownloaded`.** `download` is set only for the legacy `FileEntry_downloadURL` mapping, typed as a FileEntry. The SDK's click listener on `document` sends it when the **clicked element itself** has `asset-action="download"`, `asset-type="object-entry"`, the definition name and the ERC, with no `closest()`, so put them on the `<a>` (an information template or fragment JS can) and give any icon inside it `pointer-events: none`, or clicks on the icon send nothing.
- **A plain link to a file sends no asset event**, and opening `/documents/…` loads no page. For a visit to count, link to the entry's display page.
- **Every display page also sends a custom event** `model.resource.<className> Viewed` with `classPK` and `title` (`AnalyticsRenderFragmentLayoutPostDynamicInclude`); for a custom object `<className>` carries the per-database `#XXXX` suffix.

To check without sending anything to LDP, intercept the page's `POST`s to the Analytics host and `abort()` them after reading `postData()`: the events stay queued and the body shows each `eventId` (the spy in "Without a Human at the Browser" covers `send` too, which these events use).

## Not Covered

The LDP REST API (reports, segment creation) is not covered. Use the LDP interface for both.

## Success Signal

1. In DevTools → Network, enable **Preserve log** (a redirect clears the panel) and filter on the Analytics Cloud domain.

1. Trigger the event. Expect the request within about 2 s of the **next** page loading, not before the redirect.

1. In LDP, Events → **Event Analysis**: Analyze = the suffixed event name, Breakdown = a useful property (such as `pageTitle`), Save Analysis, range "Last 24 hours", then **Download Reports**. Check the row in the downloaded file, not only on screen.

An empty report proves nothing. Generate at least one real event first.

### Without a Human at the Browser

An agent can prove steps 1–2 itself with a headless Chrome (`rules/guest-access.md` → "Verify as the Visitor" for the setup):

- **Spy on `track` before the SDK exists.** `Analytics.create()` replaces `window.Analytics`, so wrap it from `evaluateOnNewDocument` with an `Object.defineProperty(window, 'Analytics', {set(value) {…}})` setter that wraps `value.track` when it appears and records each `(name, properties)`.
- **Assert on the response, not only the call.** Record the `POST`s to the Analytics Cloud host (`*.lfr.cloud` on 2026.Q3.2) whose `postData()` contains the event name, and check that each answered `200`. Each carries the event with `"applicationId": "CustomEvent"`, `eventId` = the name and your `properties`.
- **A residual `ac_message_queue` in `localStorage` after ~6 s means the sends are failing**: look at the console for a CORS error before suspecting the SDK.

Verified on 2026.Q3.2. This proves the event left the browser and was accepted; it does not replace step 3 in LDP.

## See Also

- `skills/manage-form-containers/SKILL.md` — when a form submission has really happened.
- `skills/scaffold-fragment/SKILL.md` and `skills/scaffold-client-extension/SKILL.md` — where the tracking code lives.
- Tracking events: learn.liferay.com → Analytics Cloud → Touchpoints → Tracking Events (search `site:learn.liferay.com analytics tracking events`).