---

description: Create a Liferay input fragment that lives inside a Form Container and binds to an object field. Use when the user asks to build a form field, add a custom input to a form, create a picker or stepper for a form, or make a fragment that submits to an object.
name: scaffold-form-fragment

---

# Scaffold Form Fragment

Build a fragment of type `input` — a form field an author drops into a Form Container, which binds it to a field on the container's object.

## When to Invoke

- "Build a form field", "add a custom input to a form", "make a date picker for this form"
- "Create a fragment that submits to an object"
- Called by `scaffold-fragment` when the fragment being built is a form field rather than a display component

For a display fragment — hero, card, section — use `scaffold-fragment` instead. Everything about collections, naming, deploy, and reprovision lives there and is not repeated here.

## Prerequisites

- A **published** object with the target field. Load `manage-objects`; an unpublished definition exposes no fields to bind against.
- A page carrying a **Form Container** bound to that object. Load `manage-pages`.

No feature flag gates input fragments.

## Workflow

### Declare the Fragment as an Input

`fragment.json` needs two things beyond a normal fragment: `"type": "input"`, and a `typeOptions` block naming the field types the fragment can bind to.

```json
{
	"cacheable": false,
	"fragmentEntryKey": "brand-email-input",
	"icon": "envelope-closed",
	"name": "Brand Email Input",
	"type": "input",
	"typeOptions": {
		"fieldTypes": [
			"email"
		]
	}
}
```

`fieldTypes` is nested **inside `typeOptions`** — not a top level key. `typeOptions` is schema validated with `additionalProperties: false`, so `fieldTypes` is the only key it accepts.

Declare only the types the fragment actually renders. The Form Container offers the fragment for a field whose type appears in this list, so an overly broad list surfaces the fragment where it cannot work.

| Field Type | Object Field It Binds To |
| --- | --- |
| `boolean` | Boolean |
| `captcha` | None — renders a CAPTCHA, not a field |
| `date` | Date |
| `date-time` | Date and Time |
| `email` | Text constrained to email |
| `file` | Attachment |
| `formButton` | None — renders the submit control |
| `friendly-url` | Text constrained to a friendly URL |
| `html` | Rich Text |
| `long-text` | Long Text |
| `multiselect` | Picklist, multiple values |
| `number` | Integer, Decimal, Precision Decimal |
| `phone-number` | Text constrained to a phone number |
| `relationship` | Relationship |
| `select` | Picklist, single value |
| `stepper` | None — renders form step navigation |
| `text` | Text |

Set `"cacheable": false`. Liferay refuses to cache an input fragment regardless, and stating it keeps the file honest.

Source: `FragmentEntryValidatorImpl` and the built in input fragments under `modules/apps/fragment/fragment-collection-contributor/fragment-collection-contributor-inputs`.

### Render the Field

The container injects an `input` object into the template. Read the field's label, value, and state from it — never hardcode them, and never invent a `name`.

| Property | Type | Holds |
| --- | --- | --- |
| `input.attributes` | map | Type specific metadata, plus `readOnly`. Contents vary by field type |
| `input.errorMessage` | string | Server side validation message; empty when valid |
| `input.helpText` | string | Author supplied help text |
| `input.label` | string | The field label |
| `input.localizable` | boolean | Whether the field stores a value per locale |
| `input.name` | string | The `name` attribute the container reads on submit |
| `input.readOnly` | boolean | Whether the field rejects edits |
| `input.required` | boolean | Whether the field is mandatory |
| `input.showHelpText` | boolean | Whether the author chose to show help text |
| `input.showLabel` | boolean | Whether the author chose to show the label |
| `input.type` | string | The bound field's type |
| `input.value` | string | Current value, populated when editing an existing entry |
| `input.valueI18n` | map | Per locale values, keyed by language ID |

`${input.name}` is the whole binding. The container maps the submitted value to the object field by that name, so a hardcoded or misspelled `name` submits nothing and reports no error.

Namespace every `id` with `${fragmentElementId}` so the fragment survives being placed twice on one page.

```html
<div class="form-group [#if input.errorMessage?has_content]has-error[/#if] mb-0" id="${fragmentElementId}-form-group">
	<label class="[#if !input.showLabel || !input.label?has_content]sr-only[/#if]" for="${fragmentElementId}-input" id="${fragmentElementId}-input-label">${htmlUtil.escape(input.label)}

		[#if input.required][@clay["icon"] className="reference-mark" symbol="asterisk" /][/#if]</label>

	<input class="form-control" id="${fragmentElementId}-input" name="${input.name}" [#if input.readOnly]readonly[/#if] ${input.required?then('required', '')} type="email" [#if input.value??]value="${input.value}"[/#if] />

	[#if input.showHelpText && input.helpText?has_content]
		<p class="mb-0 mt-1 text-secondary" id="${fragmentElementId}-input-help-text">${htmlUtil.escape(input.helpText)}</p>
	[/#if]
</div>
```

Escape `input.label` and `input.helpText` with `htmlUtil.escape` in the template. The JSON handed to `index.js` is already escaped; the FreeMarker object is not.

Use Lexicon classes (`form-group`, `form-control`, `has-error`, `sr-only`) and the Clay icon macro so the field matches every other field in the container.

### Wire the Value

Three things belong in `index.js`.

**Disable the control in edit mode.** `layoutMode` is `'edit'` while an author composes the page. Leaving the field live lets an author type into a form they are only arranging.

```javascript
const inputElement = document.getElementById(`${fragmentElementId}-input`);

if (layoutMode === 'edit') {
	inputElement.setAttribute('disabled', true);
}
```

**Register the field** so error rendering and localization behave like the built in inputs. `@liferay/fragment-impl/api` exposes `registerInputFeedback` for error display, and `registerLocalizedInput` / `registerUnlocalizedInput` for value handling. Branch on `input.localizable` — a localizable field needs the per locale inputs the helper creates, and skipping it silently drops every locale but the default.

**Sync custom UI to a hidden input.** A slider, a map picker, or a multi select built from `div`s submits nothing on its own. Carry a hidden input named `${input.name}`, write to it, and dispatch a bubbling `change` so the container notices.

```javascript
customControl.addEventListener('change', (event) => {
	hiddenInput.value = event.target.value;

	hiddenInput.dispatchEvent(new Event('change', {bubbles: true}));
});
```

Without the dispatched event the value sits in the DOM and the container reads the previous one.

### Deploy and Place

Deploy exactly as a display fragment — the fragment lives in the site initializer tree and appears when the site is provisioned. See `scaffold-fragment` for the file layout and `rules/site-initializer-format.md` for the reprovision recipe.

Then bind it: open the page, select the Form Container's field, and choose the fragment from the field's input list. It appears only for fields whose type is in `typeOptions.fieldTypes`.

## Patterns and Gotchas

Four `fieldTypes` mistakes throw and **abort the fragment import** — the collection fails to load and the fragment never appears:

| Condition | Message |
| --- | --- |
| `captcha` listed alongside any other field type | Captcha field type cannot be mixed with other field types |
| `fieldTypes` present on any `type` other than `input` | Only fragment type input can have field types |
| `stepper` listed alongside any other field type | Stepper field type cannot be mixed with other field types |
| `type: "input"` with an empty or absent `fieldTypes` | Fragment type input must have at least one field type |

**`input` exists only for `type: "input"`.** The renderer injects it behind an `isTypeInput()` check, so referencing `input` from a `component` or `section` fragment throws a reference error at render. `configuration`, `fragmentElement`, `fragmentElementId`, and `fragmentEntryLinkNamespace` are injected for every fragment type.

**Use `fragmentElementId`, not `fragmentEntryLinkNamespace`, for element ids.** Both are injected, and the built in input fragments use `fragmentElementId` throughout — matching them keeps ids consistent with the helpers in `@liferay/fragment-impl/api`, which take it as their `namespace`.

**A hidden field still submits.** CSS `display: none` removes a conditionally shown field from view and leaves it in the form, so it posts a value the visitor never entered. Remove it from the DOM in `index.js` instead. Conditional visibility built with `<lfr-drop-zone>` has the same trap — see `scaffold-fragment` → "Drop Zones".

**Public forms are write only.** A form open to anonymous visitors needs `ADD_OBJECT_ENTRY` and **not** `VIEW` on the object; company scope `VIEW` publishes every submission to the world. The visitor's own `POST` still returns the created entry, so the fragment can confirm back what was recorded. Read `rules/guest-access.md` before exposing a form on a public page.

**Edit mode styling.** An input fragment with scroll animations or collapsed sections needs the `has-edit-mode-menu` treatment so an author can reach the field. See `scaffold-fragment` → "Edit Mode Awareness".

## Success Signal

The fragment appears in the input list for a field of a declared type, and a submission through it lands as an object entry:

```bash
curl \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>" \
	--user "test@liferay.com:test" \
	| jq '.items[-1]'
```

The submitted value appears on the field named by `input.name`. A `200` with the field empty or absent means the `name` attribute did not reach the container — check for a hardcoded `name` or a missing dispatched `change`.