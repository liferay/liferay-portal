---

description: Build a Liferay Form Container — the native page element that creates or edits an Object entry from a Content Page or a Display Page Template — over the live page API or the site initializer tree. Covers field fragment keys, the submit button, multistep forms, success redirects, hidden context fields, edit forms on a DPT, the gated-content recipe (teaser and form, then the full entry), and the permission that makes a form render at all. Use when the user asks for a form that saves to an object, a lead or contact form, gated content behind a form, an edit form on a display page, or a form that renders nothing.
name: manage-form-containers

---

# Manage Form Containers

A **Form Container** binds a form to a published object. Its children are field fragments (`INPUTS-*`) and a submit button, and submission creates the entry, or updates it on an edit form. Submissions post to the classic `/c/portal/edit_info_item` controller, not to `/o/c/...`, so no custom fragment or client extension is needed for the common case.

Everything below was verified live on 2026.Q3.2, mostly against shapes the Page Editor itself produced.

## When to Invoke

- "Add a form that saves to <object>", "a contact / lead / registration form"
- "An edit form on the display page", "a two-step form", "redirect after submit"
- A form that renders nothing, or submits and stores empty values

`manage-pages` owns the page and the whole-page write; `manage-roles-permissions` owns the grants; `scaffold-form-fragment` owns custom input fragments.

## First: Grant `ADD_OBJECT_ENTRY`, or the Form Does Not Exist

**A viewer without `ADD_OBJECT_ENTRY` on the bound object gets no form at all.** Nothing renders: no `<form>`, no fields, not even the element's layout wrapper. There is no error and no log line, so it reads exactly like a broken import. Grant it on `com.liferay.object#<definitionId>` at company scope (`scope` `"1"`), **without** `VIEW` for a public form (`rules/guest-access.md` → "Public Forms Are Write Only"). Then test as the real audience — an admin session always sees the form.

**On a company-scoped object, grant it to a regular role, never a site role.** The form's render check runs in the page's group, but the add itself is checked in group 0: `ObjectEntryInfoItemUtil.getScopeKey` gives a company object no scope, and `ObjectEntryServiceImpl` checks `ADD_OBJECT_ENTRY` there, where no site role is in the user's bag. So a company-scope grant to Site Member (or any site role) shows the form to the site's members, and then **every submission fails**: "An error occurred while sending the form information.", nothing stored, nothing logged, and a REST `POST` as the same user answers `403`. Verified on 2026.Q3.5 with Site Member on a Guest page; the same form saved once the grant named the regular roles the members hold.

## The Element, Over the Live API

Place it with the whole-page PUT (`manage-pages` → "Page Specification Workflow"). This is what the Page Editor produces:

```json
{
	"pageElementDefinition": {
		"formContainerConfig": {
			"formContainerReference": {
				"className": "com.liferay.object.model.ObjectDefinition#<code>",
				"type": "FormContainerClassSubtypeReference"
			},
			"formContainerType": "Simple",
			"numberOfSteps": 0,
			"successFormContainerSubmissionResult": {"type": "EmbeddedMessage", "message": {"value_i18n": {"en-US": "Thank you."}}}
		},
		"type": "FormContainer"
	},
	"pageElements": [ /* FormFragment children, then the submit button */ ]
}
```

- **`className` is the definition's suffixed class name.** Read it from `GET /o/object-admin/v1.0/object-definitions/by-external-reference-code/<erc>` (`className`); never strip the `#<code>`.
- **`numberOfSteps` must be present**, or the PUT returns `500` (NPE on `getNumberOfSteps()`). `0` and `1` both work for one step, whether `formContainerType` is `Simple` or `Multistep`.

### Fields

Each field is a `FormFragment`:

```json
{
	"pageElementDefinition": {
		"fieldKey": "ObjectField_<fieldName>",
		"fragmentInstance": {
			"fragmentConfigurationFieldValues": {},
			"fragmentEditableElements": [],
			"fragmentReference": {
				"defaultFragmentKey": "INPUTS-text-input",
				"fragmentReferenceType": "DefaultFragmentReference"
			}
		},
		"type": "FormFragment"
	},
	"pageElements": []
}
```

- **`fieldKey` binds the input to the object field.** The platform writes `ObjectField_<name>`, and a bare `<name>` binds too. The rendered inputs are named `ObjectField_<name>` as well (a 2025.Q3 page's script selecting `input[name="<name>"]` matches nothing on 2026.Q3.5). The system ERC field is `ObjectEntry_externalReferenceCode`. **A field without `fieldKey` still submits and shows the success message, but stores an empty value.**
- **Keys produced by the Page Editor, per field type:**

| Object field type | `defaultFragmentKey` | Generated `fragmentConfigurationFieldValues` |
| --- | --- | --- |
| Text | `INPUTS-text-input` | `showCharactersCount: {"type": "Checkbox", "value": false}` |
| LongText | `INPUTS-textarea` | `numberOfLines`, `showCharactersCount` (both optional) |
| RichText | `INPUTS-rich-text-input` | none |
| Integer, Decimal, PrecisionDecimal | `INPUTS-numeric-input` (not `INPUTS-numeric-upload`) | none |
| Date | `INPUTS-date-input` | none |
| DateTime | `INPUTS-date-time-input` (renders `datetime-local`) | none |
| Picklist | `INPUTS-select-from-list` | none |
| MultiselectPicklist | `INPUTS-multiselect-list` (one checkbox per entry, each named `ObjectField_<field>`, plus a `checkboxNames` entry); `INPUTS-multiselector-dropdown` is the dropdown variant | none |
| Relationship (child side, `fieldKey` `ObjectField_r_<rel>_c_<parent>Id`) | `INPUTS-select-from-list` | none |
| Boolean | `INPUTS-checkbox` | none |
| Attachment | `INPUTS-file-upload` | `showSupportedFileInfo: {"type": "Checkbox", "value": true}` |

An Attachment field whose `fileSource` is `CMSBasicDocument` renders the same `INPUTS-file-upload` as a **picker** (`selectFromDocumentLibrary: true`, a "Select File" button opening the item selector, no `<input type="file">`): `ObjectFieldInfoFieldConverter` maps both `CMSBasicDocument` and `documentsAndMedia` to the document-library source (verified on 2026.Q3.5).

**A fragment's key is its `fragmentEntryKey`, not its directory in the inputs jar.** The multiselect lives in `multiple-select-from-list/` but is keyed `INPUTS-multiselect-list`. A tree element placed by the directory name is dropped at import with no log line, and the page imports one link short (verified on 2026.Q3.5). Count the links after an import.

**An upload source with `showFilesInLibrary` off works on a company-scoped object.** `userComputerToCMSBasicDocument` (and `userComputerToDocumentsAndMedia`) renders a plain file input. The uploaded file is stored hidden in the object's own repository (`…ObjectDefinitionsPortlet_<code>`) **in the form page's site**, owned by the entry, and deleted with it. Its `link.href` downloads for the owner and for a holder of `DOWNLOAD_<FIELD>`, answers `401` to another signed-in user and `404` to Guest. A `/documents/…` URL ignores Basic auth (every caller, administrator included, got `404`), so test downloads in a signed-in browser session. Verified on 2026.Q3.5.

- **Which selector opens.** The input opens DXP's CMS file selector (`openCMSFileSelectorModal`) only when the page's own scope group is the CMS group or a Space (`input.attributes.isCMS`, `FragmentEntryInputTemplateNodeContextHelperImpl._isCMSGroup`). On a site page it opens the Documents and Media selector, which cannot browse a Space's files.
- **What it posts.** The input posts the picked file's **DLFileEntry id** under `ObjectField_<field>`, and an edit form pre-fills it with that id. Posting a CMS file's DLFileEntry id saves through a Form Container. A non-existent id fails with the generic error.

Generating the form in the Page Editor also adds an `INPUTS-text-input` bound to `ObjectEntry_externalReferenceCode`; drop it unless visitors should set the ERC. A full submission of every type round-tripped correctly, the file included. Picklists and relationships render an autocomplete text box plus two hidden inputs, `ObjectField_<field>` (the key or related id) and `ObjectField_<field>-label`. A rich text field is a hidden `ObjectField_<field>` fed by CKEditor, so do not read or set it as a plain `<input>`.

### The Submit Button

```json
{
	"pageElementDefinition": {
		"fragmentInstance": {
			"fragmentConfigurationFieldValues": {
				"buttonSize": {"type": "Select", "value": "nm"},
				"buttonType": {"type": "Select", "value": "primary"},
				"submittedEntryStatus": {"type": "Select", "value": "0"},
				"type": {"type": "Select", "value": "submit"}
			},
			"fragmentEditableElements": [],
			"fragmentReference": {
				"defaultFragmentKey": "INPUTS-submit-button",
				"fragmentReferenceType": "DefaultFragmentReference"
			}
		},
		"type": "FormFragment"
	},
	"pageElements": []
}
```

- **No `fieldKey`.**
- **`submittedEntryStatus`:** `"0"` stores approved entries, `"2"` stores drafts. A page exported from 2025.Q3 may carry the label `"approved"` instead; 2026.Q3.5's button declares only `"0"` and `"2"` and posts the stored value as the `status` parameter, so rewrite it to `"0"` (verified: the submission stored an approved entry). `"2"` **requires `enableObjectEntryDraft: true` on the object**. Without it, every submission shows "An error occurred while sending the form information.", stores nothing, and logs nothing.
- **`type`:** `submit`, `next`, or `previous`.

### Laying Out Fields — Rows, Columns, Spacers

Do not stack every field full width. A `FormContainer`'s children may mix `FormFragment` fields with a `Grid` of `Module` columns and plain `BasicFragment` elements (headings, spacers), at any depth, and a `Module` may hold several fields. Field discovery (`data-field-name="ObjectField_<field>"`) and submission are unaffected, so scripts reading the form keep working. Verified on 2026.Q3.2: a Page-Editor-built form read back over the API, and the tree shape below imported and rendered anonymously.

A row, inside the `FormContainer`'s `pageElements` (give every element its `externalReferenceCode`, `parentExternalReferenceCode`, and `position`, as for any page element):

```json
{
	"pageElementDefinition": {
		"gridViewports": [
			{"gridViewportDefinition": {"modulesPerRow": 2}, "id": "Desktop"},
			{"gridViewportDefinition": {"modulesPerRow": 1}, "id": "LandscapeMobile"},
			{"gridViewportDefinition": {}, "id": "PortraitMobile"},
			{"gridViewportDefinition": {}, "id": "Tablet"}
		],
		"gutters": true,
		"numberOfModules": 2,
		"reverseOrder": false,
		"type": "Grid"
	},
	"pageElements": [
		{
			"pageElementDefinition": {
				"moduleViewports": [
					{"id": "Desktop", "moduleViewportDefinition": {"size": 6}},
					{"id": "LandscapeMobile", "moduleViewportDefinition": {"size": 12}}
				],
				"type": "Module"
			},
			"pageElements": []
		}
	]
}
```

A spacer, anywhere a field can go — `height` takes `py-1`, `py-2`, or `py-3`:

```json
{
	"pageElementDefinition": {
		"fragmentInstance": {
			"fragmentConfigurationFieldValues": {"height": {"type": "Select", "value": "py-2"}},
			"fragmentReference": {
				"defaultFragmentKey": "BASIC_COMPONENT-spacer",
				"fragmentReferenceType": "DefaultFragmentReference"
			}
		},
		"type": "BasicFragment"
	}
}
```

Layout rules, native elements only and no custom CSS:

- **Sections:** group fields by meaning (what happened, who, how much, attachments) under an `h3` heading each.
- **Side by side:** short fields — picklists, booleans, phone and email, city and postal code, numbers, file uploads — in rows of 2 to 4 columns summing to 12 (`6+6`, `4+4+4`, `3+3+3+3`). Pair fields read together.
- **Full width:** long-text and rich-text fields, a free-text title or summary, a street address, and a field that opens a section on its own.
- **Stacked in one column:** a field and the one depending on it (a checkbox and its follow-up), separated by a `py-1` spacer.
- **Mobile:** every column `12` and every row one module per row on landscape and portrait mobile.
- **Spacing:** `py-3` before each section heading, before the submit button, and before the form when it follows other content; `py-2` after each heading and between rows of a section; `py-1` between two fields stacked in one column. Never two spacers in a row.

### Multistep Forms

`formContainerType: "Multistep"` with `numberOfSteps: <N>`. The first child is an `INPUTS-stepper` fragment (`fragmentConfigurationFieldValues.numberOfSteps: {"type": "Text", "value": "<N>"}`), then a `FormStepContainer` holding one `FormStep` per step. Each `FormStep` holds a `Container` with that step's fields and its `INPUTS-submit-button`s typed `next`, `previous`, and on the last step `submit`. A two-step submission stored all values.

### After Submission — `successFormContainerSubmissionResult`

| `type` | Extra keys | Effect |
| --- | --- | --- |
| `EmbeddedMessage` | `message.value_i18n` | Stays on the page and shows the message. The default the platform reads back when none is set |
| `StayInPage` | — | Reloads the page with an empty form and **no message** |
| `Url` | `url.value_i18n` | Redirects. **A URL outside the portal's allowed redirect domains stores the entry, then fails with a hidden `500`** (`WARN [PortalImpl] Redirect URL … is not allowed`); the visitor sees the page reload with no feedback. Same-domain URLs work |
| `SitePage` | `itemExternalReference: {"className": "com.liferay.portal.kernel.model.Layout", "externalReferenceCode": "<page-erc>"}` | Redirects to that page |
| `DisplayPage` | `itemExternalReference: {"className": "com.liferay.layout.page.template.model.LayoutPageTemplateEntry", "externalReferenceCode": "<dpt-erc>"}` | Redirects to that DPT, showing the entry just created (`/web/<site>/e/<dpt-url>/<classNameId>/<entryId>`) |

## The Element, in the Site Initializer Tree

The tree uses a different vocabulary: a `Form` element with `formConfig.formReference`, not `FormContainer`. See `manage-pages` → "Links, Forms, and Menus in the Tree". Its field children are `INPUTS-*` fragments with `fragmentConfig.inputFieldId: "ObjectField_<field>"`. For a form bound to another object than the DPT's, use `formReference.className` with the suffixed class name; an `ObjectEntry` plus `classType` renders an empty drop zone. The success redirect is `formConfig.formSuccessSubmissionResult`, not the live key above: `manage-pages` → "Links, Forms, and Menus in the Tree".

**The success result in the tree** is `formConfig.formSuccessSubmissionResult` (`FormLayoutStructureItemImporter._getSuccessMessageJSONObject`; verified on 2026.Q3.5):

| Shape | Effect |
| --- | --- |
| `{"messageType": "Embedded", "message": {"value_i18n": {"en_US": "…"}}}` | The embedded message |
| `{"itemReference": {"className": "com.liferay.portal.kernel.model.Layout", "fields": [{"fieldName": "friendlyURL", …}, {"fieldName": "privatePage", …}, {"fieldName": "siteKey", "fieldValue": "[$GROUP_KEY$]"}]}}` | A page |
| `{"defaultDisplayPage": true}` | The new entry on the **default** DPT, by its id-free URL |
| `{"defaultDisplayPage": false, "mapping": {"itemReference": {"fields": [{"fieldName": "externalReferenceCode", "fieldValue": "<dpt erc>"}]}}}` | The new entry on **that** DPT, `/e/<dpt-url>/<classNameId>/<id>`. The ERC is looked up in the page's group. An unknown one is stored as the default DPT, with no warning |

A tree-made DPT's ERC is generated per database (`rules/page-types.md`), so the last shape works only after that database's ERC has been written into the tree.

**A multistep form in the tree** (verified on 2026.Q3.5 with real two-step submissions; the same shape as the platform's own import-export fixture, `layout-page-template-admin-web-test`, `form/steps`):

```text
Form  {"formConfig": {"formReference": {...}, "formType": "multistep", "numberOfSteps": 2}}
├─ Fragment INPUTS-stepper  fragmentConfig {"numberOfSteps": "2", "showNumbers": true, "showTitles": true}
│                           fragmentFields step-1-text, step-2-text: the step titles (text editables)
└─ FormStepContainer  {}
   ├─ FormStep {} → Section → step 1's fields, INPUTS-submit-button {"type": "next"}
   └─ FormStep {} → Section → step 2's fields, {"type": "previous"}, {"type": "submit"}
```

`formType` and `numberOfSteps` sit inside `formConfig` (`FormLayoutStructureItemImporter`). The stepper's `numberOfSteps` is a string.

Rows and spacers in the tree are `Row` → `Column` → `Fragment`, inside the `Form` element:

```json
{
	"definition": {
		"gutters": true,
		"indexed": true,
		"modulesPerRow": 3,
		"numberOfColumns": 3,
		"reverseOrder": false,
		"rowViewports": [
			{"id": "landscapeMobile", "rowViewportDefinition": {"modulesPerRow": 1}},
			{"id": "portraitMobile", "rowViewportDefinition": {"modulesPerRow": 1}}
		],
		"verticalAlignment": "top"
	},
	"pageElements": [
		{
			"definition": {
				"columnViewports": [
					{"columnViewportDefinition": {"size": 12}, "id": "landscapeMobile"},
					{"columnViewportDefinition": {"size": 12}, "id": "portraitMobile"}
				],
				"size": 4
			},
			"pageElements": [
				{"definition": {"fragment": {"key": "INPUTS-select-from-list"}, "fragmentConfig": {"inputFieldId": "ObjectField_<field>"}}, "type": "Fragment"}
			],
			"type": "Column"
		}
	],
	"type": "Row"
}
```

- The importer reads `rowViewports[].rowViewportDefinition` and `columnViewports[].columnViewportDefinition` with **lowerCamel** viewport ids (`landscapeMobile`, `portraitMobile`, `tablet`); the flat `modulesPerRow` and `size` are the Desktop values (`RowLayoutStructureItemImporter`, `ColumnLayoutStructureItemImporter`, from source).
- A spacer is `{"definition": {"fragment": {"key": "BASIC_COMPONENT-spacer"}, "fragmentConfig": {"height": "py-2"}}, "type": "Fragment"}`.
- Read back over the live API, `Row` becomes `Grid` and `Column` becomes `Module`, and **the `Form` reads back as `formContainerType: "Multistep"` with `numberOfSteps: 0`** even when the tree says nothing about steps. It still renders and submits as one step — not a manual change when comparing a live page with its tree.

## Custom Input Fragments as Fields

A custom `"type": "input"` fragment (`scaffold-form-fragment`) placed through the live API is a `FormFragment` with `"fragmentReferenceType": "FragmentItemExternalReference"` and the fragment's ERC. **Send its real `html`, `css`, and `js` on `fragmentInstance`.** With an empty `html`, the field wrapper renders but its content is empty, and the field submits nothing — no error anywhere. Only `DefaultFragmentReference` (the built-in `INPUTS-*`) is backfilled from an empty `html`. To change such a field's code later, re-add it under a **new** element ERC (`manage-pages`).

## On a Display Page Template

### Hidden Field From the Displayed Entry

A form for object B placed on object A's DPT can carry A's id in a relationship field, through a custom hidden input fragment:

```html
[#assign infoItem = (request.getAttribute("INFO_ITEM"))! /]
<input
	name="${input.name}"
	type="hidden"
	[#if infoItem?? && infoItem.objectEntryId??]
		value="${infoItem.objectEntryId}"
	[/#if] />
```

Submitting from entry A's display page stored a B entry whose FK is A's id, and the same held for entry B.

**`INFO_ITEM` also exposes the entry's custom fields, through `getValues()`:**

| Expression | Result |
| --- | --- |
| `infoItem.objectEntryId`, `.externalReferenceCode`, `.status` | Direct values |
| `infoItem.getValues()["<fieldName>"]` | The field's value (e.g. `10.5`) |
| `infoItem.price`, `.getInfoFieldValue("price")`, `.getMap(locale)` | "evaluated to null or missing" |
| `infoItem.createDate?string` | Error — dates need `?datetime` |

So a form, or any fragment on a DPT, can pre-fill or display another object's values without a client-side fetch.

The same works inside a `"type": "input"` fragment, so a hidden field can carry a value **computed** from the displayed entry, such as a source tag derived from one of its fields (verified on 2026.Q3.2):

```html
[#assign infoItem = (request.getAttribute("INFO_ITEM"))! /]
[#assign tag = "<prefix>" /]
[#if infoItem?? && infoItem.objectEntryId??]
	[#assign raw = (infoItem.getValues()["<fieldName>"])!"" /]
	[#if raw?has_content]
		[#assign tag = "<prefix>-" + raw?lower_case?replace("[^a-z0-9]+", "-", "r")?remove_beginning("-")?remove_ending("-") /]
	[/#if]
[/#if]
<input id="${fragmentElementId}-input" name="${input.name}" type="hidden" value="${htmlUtil.escapeAttribute(tag)}" />
```

Declare it with `typeOptions.fieldTypes` matching the target field (`["text"]` for a Text field) and bind it with `fragmentConfig.inputFieldId`, like any `INPUTS-*` child.

### Edit Forms

**A Form Container bound to the same object as its DPT is an edit form.** Binding by `formReference.className` or by `contextSource: "DisplayPageItem"` gives the same result: the fields are pre-filled from the displayed entry, and submission updates it without creating an entry.

**An update re-validates every stored relationship, posted or not.** A user holding `UPDATE` on the entry whose form omits a relationship field (a script disabled that section, or the field is not on the form) still fails when the entry points at a related entry the user cannot `VIEW`: the page reloads with "An error occurred while sending the form information.", a REST `PATCH` of any other field answers `403`, and the entry's `actions` still list `update`. Verified on 2026.Q3.5 with a child of two `AccountEntry` relationships: the agent could update the entry only while its coach account was one she could view. Grant `VIEW` on the related object (or the related entries) to the editing role, or expect the edit form to fail for that role.

**Scripting a submission** (probes, not production): post `multipart/form-data` (the form's `enctype`; urlencoded is ignored and stores an empty entry), include the hidden inputs and a `Referer` header. On an error `EditInfoItemStrutsAction` redirects to the `Referer`, so a request without one answers `500` (`url is null`) instead of showing the message. Picklist and relationship inputs post the key or id plus their `-label` twin; a checkbox posts its name with `checkboxNames`; the rich-text input is added by CKEditor, so post `ObjectField_<field>` yourself.

## Reacting to Success in the Browser

There is no submission event to hook. Liferay swaps the form's markup for the success message, and anything inside the Form Container is replaced with it. So put the watcher on the **wrapping** fragment, and look for the success text in `document.body.innerText`, never in `textContent`, which includes the fragment's own inline `<script>` and matches itself on load (verified 2026.Q3.2):

```javascript
(function () {
	var MARKER = 'Your information was successfully received';
	var fired = false;

	function check() {
		if (fired || document.body.innerText.indexOf(MARKER) === -1) {
			return;
		}

		fired = true;

		// track, reveal content, or redirect here
	}

	check();

	new MutationObserver(check).observe(document.body, {childList: true, subtree: true});
})();
```

Use a native `successFormContainerSubmissionResult` whenever one fits. To track the submission in Analytics Cloud, see `manage-analytics`.

## Recipe: Gated Content on a Display Page

"A teaser and a form, then the full content of the same entry", built in the site initializer tree only. Assembled and verified end to end as an anonymous visitor on 2026.Q3.2:

1. **Two DPTs for the content type**, both with `"contentType": {"className": "[$OBJECT_DEFINITION_CLASS_NAME:<Content>$]"}` in `display-page-template.json`: the gated one `"defaultTemplate": true` (listing links with `displayPageURL` land on it), the full one `false`.
1. **The gated DPT** holds one wrapper fragment (teaser editables mapped with `contextSource: "DisplayPageItem"`, plus `<lfr-drop-zone id="<zone>">` in its HTML). In the tree the drop zone's content must be explicit:

	```text
	Fragment (wrapper)
	└─ FragmentDropZone  {"fragmentDropZoneId": "<zone>", "fragmentSettings": {"unallowedFragments": []}}
	   └─ Form  {"formConfig": {"formReference": {"className": "[$OBJECT_DEFINITION_CLASS_NAME:<Target>$]"}}}
	      ├─ INPUTS-text-input  {"inputFieldId": "ObjectField_<field>"}  … one per field
	      ├─ <custom hidden input>  (see "Hidden Field From the Displayed Entry")
	      └─ INPUTS-submit-button
	```

	Leave `formSuccessSubmissionResult` out: the default embedded message is the marker the watcher waits for.
1. **The wrapper's `index.js` is the watcher** ("Reacting to Success in the Browser"). It reads the entry id (`INFO_ITEM.objectEntryId`, written as a data attribute), the site path (`${themeDisplay.getPathFriendlyURLPublic()}${themeDisplay.getScopeGroup().getFriendlyURL()}`) and the environment-specific `classNameId` and DPT URL (`fragmentConfig`, `manage-pages` → "Links, Forms, and Menus in the Tree"), then opens `<site>/e/<dpt-url>/<classNameId>/<entryId>`. To let the next page tell a gated arrival from a direct one, set a `sessionStorage` flag keyed by the entry id before redirecting.
1. **The full DPT** maps the remaining fields. Any tracking runs in its own fragment JS on load (`manage-analytics`).
1. **Guest grants** in `resource-permissions.json`, scope `"1"`: `VIEW` on the content's `[$OBJECT_DEFINITION_CLASS_NAME:…$]`, and `ADD_OBJECT_ENTRY` (no `VIEW`) on `com.liferay.object#[$OBJECT_DEFINITION_ID:<Target>$]`.

The full DPT stays reachable by URL without the form. Say so to the user: this is a soft gate.

## Testing a Submission Without a Browser

A `curl` replay of the form is a quick check that the binding and the Guest grant work: GET the page with a cookie jar, read `action` and every `<input>` of the `<form>`, read `p_auth` from `authToken: '…'` in the page, and POST them multipart to `/c/portal/edit_info_item?…&p_auth=<token>`. **The replay answers `500` (`code_jsp` WARN) although the entry is stored**, so judge it only by reading the entry back as admin. Strip `\r` from values extracted on Windows, or they are stored with it. Delete the entries the test created. The redirect and anything else client-side still need a real browser (`rules/guest-access.md` → "Verify as the Visitor").

Two replay traps, measured on 2026.Q2.7:

- **Post multipart even when no field is a file.** Python `requests` with `data=` and no `files=` sends `application/x-www-form-urlencoded`. The servlet logs `WARN [UploadServletRequestImpl] Unable to parse upload request`, answers `302` back to the page, and stores nothing: it reads exactly like a missing grant. Send every field as a multipart part (`files=[(name, (None, value)), …]`).
- **A file field's `<input type="file">` has no `name` in the served HTML.** `INPUTS-file-upload`'s script sets it to `ObjectField_<field>` when a file is chosen (and clears its hidden twin's name), so a replay posts the file under `ObjectField_<field>` in the same multipart body, as the browser does.

**A Guest file upload works on 2026.Q2.7** with a company-scoped object, `fileSource` `userComputerToDocumentsAndMedia` and `showFilesInLibrary: false`, with Guest holding only `ADD_OBJECT_ENTRY`: the entry is stored, each file downloads byte-identical in an admin's signed-in session (from the entry's `<field>.link.href`), Guest still reads no entry, and deleting the entry deletes its files (the URL stops downloading). Measured with three upload fields on one form.

## A Form in a Modal

Opening a form page or an edit DPT in `Liferay.Util.openModal` works on 2026.Q3.5, with three conditions (verified in a browser, Add, Edit and Delete each landing in the database):

- **Pass an absolute URL** (`location.origin + path`). A relative `url` throws `TypeError: Failed to construct 'URL': Invalid URL` in `frontend-js-components-web`, and no modal opens.
- **Leave `size` out.** A modal with a `url` defaults to `full-screen` (`Modal.tsx:315`). `size: 'lg'` gives an iframe about 160 px tall on a desktop viewport: it cut off a two-field form's submit button, and a picker overlay opened inside the form was confined to a strip of that height (verified on 2026.Q3.5).
- **Give the page a bare master.** In `p_p_state=pop_up` a content page or DPT still renders its master's header, menus and footer, inside the modal and over the form. Use a master with only a `DropZone` and the full master's `settings` (theme CSS client extension, style book, global CSS and JS), so the form keeps the site's styling.
- **Pass context on the URL.** A URL parameter reaches a custom hidden input: `request.getParameter("<name>")` in its FreeMarker, read on the server.

The embedded success message shows inside the modal. `onClose` fires when the user closes it, which is where the opener refreshes its list. An edit form's success reloads the DPT under the entry's new friendly URL, since that URL follows the title field.

## Account-Restricted Objects

**A Form Container does not bypass `accountEntryRestricted`.** A Guest submission that sets the account relationship to an account the visitor does not belong to fails ("An error occurred while sending the form information."), stores nothing, and logs nothing, exactly as the REST `POST` returns `403`. For a signed-in user with `ADD_OBJECT_ENTRY` on the User role, the form did not render on a restricted object at all, and the cause was not established. **With regular roles holding a company-scope `ADD_OBJECT_ENTRY`, it renders** (2026.Q3.5): an add form and display-page edit forms rendered on restricted objects for both roles' members, and a coach's submission saved, its account relationship posted by a hidden input from a URL parameter (`manage-objects` → "Account Restriction").

## Page Editor Pitfalls

- **Duplicating a Form Container in the Page Editor drops every field's `fieldKey`.** The copy keeps the object binding and the success settings, and its fields then store empty values. Re-map each field on the copy.
- **LongText can end up as a plain text input.** One LongText field sat on an `INPUTS-text-input` in a multistep form; it is unclear whether that was a manual choice. Check the generated keys before copying a form's JSON.

## Not Verified

- `COMMERCE_CONTEXT` in a hidden field (no Commerce channel was available).
- Master-detail patterns (a Data Set row loading an entry into a form).
- The signed-in case on an account-restricted object.

## Success Signal

- The form renders for the real audience: an unauthenticated browser for a public form.
- A real submission stores every field with the right value, read back over REST as admin.
- The chosen success behavior happens.
- The audience still cannot read other entries if the form is public.