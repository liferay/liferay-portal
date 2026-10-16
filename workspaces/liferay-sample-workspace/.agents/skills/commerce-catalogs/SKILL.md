---

description: Standards for managing Liferay Commerce catalogs, products, SKUs, and B2B account onboarding via Headless APIs. Use when the user works with commerce products, variants, options, specifications, product images, or B2B accounts and roles.
name: commerce-catalogs

---

# Commerce Catalogs

Standards for managing Liferay Commerce catalogs, products, SKUs, and B2B account onboarding via the Headless APIs.

## When to Invoke

- "Manage commerce products", "add a product variant", "create SKUs"
- "Set product options, specifications, or images"
- "Onboard a B2B account", "assign account roles"

## Product Management via Headless APIs

When programmatically managing products, follow these strict architectural findings:

- **The ERC mandate**: the `externalReferenceCode` (ERC) is the only reliable identifier for a product. Always use the ERC based endpoints (e.g., `/products/by-externalReferenceCode/{ERC}`) for `PATCH`, `DELETE`, and subresource operations to avoid `404 NOT FOUND` errors.
- **Product options**: options (size, color, etc.) are global, prerequisite entities. They must be created via the global `options` endpoint *before* you can link them to a specific product.
- **Specifications**: do not use nested `POST` endpoints for specifications. Instead, `PATCH` the product itself via its ERC and supply a `productSpecifications` array containing the `specificationKey` and `value`.

## REST Gotchas (headless-commerce-admin-catalog)

Verified on 2026.Q2 during a bulk migration of more than 500 products:

- **`externalReferenceCode` and SKU are VARCHAR(75)** — longer values 500 on create. Cap at 75 (hash-suffix long ones).
- **`POST /products` with a `defaultSku` string auto-creates the default SKU at price 0.** Set its price via `PATCH /skus/by-externalReferenceCode/{erc}` — do NOT POST a second SKU.
- **Specification keys are lowercased on create** (`productLine` → `productline`); reference the lowercase key or the attach silently skips.
- **Categories attach by numeric `id` only**: categories must pre-exist (in a site/asset-library vocabulary — you cannot create a vocabulary in the catalog group itself), then `PATCH /products/by-externalReferenceCode/{erc}/categories` with `[{"id": <categoryId>}]`. By-name → 500. Cross-scope attach (site vocabulary → catalog product) works. The PATCH **replaces** the full category set.
- **`relatedProducts` needs the numeric `productId`** of the related product — ERC alone fails "productId must not be null".
- **`productOptions` POST expects an ARRAY** of ProductOption (a single object fails "Unable to map JSON path").
- **Variant `skuOptions` want the option-REL ids, not the global option id**: `optionId` = the CPDefinitionOptionRel id (the `id` returned when the option was attached to the product) and `optionValueId` = the productOptionValue id — or use the key form `{"key": "<optionKey>", "value": "<valueKey>"}`. Passing the global CPOption id is **silently dropped** (200, no `CPInstanceOptionValueRel`) and reads as "variants don't persist over REST". POST a SINGLE Sku object, not an array.
- **`/products` list caps `pageSize` at 500** (silently, even if you ask for more) — paginate for a complete ERC→id map.
- **The product list returns both `id` and `productId` — correlate on `productId`.** `id` is the internal CPDefinition id; SKU, pricing, and attachment operations key on `productId`, and mixing them up produces wrong-target writes that look successful.
- **Order updates are PATCH-only**: `PATCH /o/headless-commerce-admin-order/v1.0/orders/{id}` works; `PUT` returns 405.
- **Single catalog reads are the singular path**: `/catalog/{id}` or `/catalog/by-externalReferenceCode/{erc}`; `/catalogs/{id}` 404s.
- **The batch engine works for bulk loads**: `POST /products/batch` (also `/products/skus/batch`, `/options/batch`, …) → 202 + async ImportTask; poll `/o/headless-batch-engine/v1.0/import-task/{id}` for `COMPLETED` (then check `failedItems` — COMPLETED alone is not success).

## Catalog vs Channel Scoping

Three verified boundaries that decide where things live:

- **The storefront scopes by CHANNEL, not catalog.** A product with the Visibility → Channels filter OFF appears in **every** channel, whatever catalog it's in; the delivery search filters on `commerceChannelGroupId` only. The **admin** Products portlet scopes by catalog — which is why per-brand catalog segregation works for catalog managers but does not fence the storefront.
- **Price lists are catalog-GROUP scoped.** Price resolution walks the SKU's catalog group, so a price list in catalog A is never found for a SKU in catalog B — you cannot move a product between catalogs without orphaning its tier prices (the SKU base price travels; tiers do not).
- **The storefront "Catalog" facet is `aggregationTerms=groupId`** on the delivery `/products` endpoint (the catalog *group* id — `commerceCatalogId` returns empty facet values in the delivery index). The product payload also carries `catalogName` for labels.

## A Site's Channel From a Site Initializer

A site initializer's tree can create the site's channel: `site-initializer/commerce-channel.json`, which the Commerce extender upserts by site. It takes `Channel`'s own fields only, and every re-fire `PUT`s it, which clears the price display type and the discount target (verified on 2026.Q3). The format and every behavior: `rules/site-initializer-format.md` → "`commerce-channel.json`". Without a channel, the account selector fragment renders "This site does not have a channel" and `Liferay.CommerceContext.account` is empty, so fragment JS that reads the selected account gets nothing.

## Storefront, Pricing Visibility, and Checkout

- **Hide prices from guests = revoke Guest `VIEW_PRICE` on the `com.liferay.commerce.product` root resource (company scope).** It is a permission, not a channel or OSGi config, and survives site reprovision.
- **A buyer persona needs the "Buyer" account role** (grants `CHECKOUT_OPEN` / `VIEW_OPEN_COMMERCE_ORDERS`) — "Account Administrator" alone is not enough, and portal admins bypass account-role checks entirely, so "works as admin" proves nothing about the buyer flow.
- **An order-LEVEL validator failure (e.g. minimum-order-amount) 302s checkout back to the cart with NO message** — the mini-cart submit stays enabled because its `canSubmit` only inspects per-item errors. Diagnose via the `CommerceOrderValidatorRegistry` (Groovy). The min-order rule type is `minimum-order-amount` (not "order-limit"), one rule per currency.
- **Checkout cannot be fully driven headlessly.** Payment methods, shipping methods, and shipping rate rows have no REST create (Control Panel or a `commercePaymentIntegration`/`commerceShippingEngine` CET). delivery-cart `POST /carts/{id}/checkout` is a light transition that **skips order-level validators** — for enforced placement use `CommerceOrderEngine` server-side. `PATCH /carts/{id}` silently no-ops `shippingOption`/`shippingMethod` (`paymentMethod` and inline addresses DO persist) — always verify with a re-GET. Cart create = `POST /channels/{channelId}/carts` with `accountId` in the body; add-item `options` is a JSON **string** (`[{"key":"finish","value":["black"]}]`), required for option products.
- **Delivery-catalog `/products` 404s for an authenticated session with no account context** (guests get the anonymous fallback). Resolve the current account via `/channels/{id}/accounts` first and pass `accountId`. In storefront fragment JS the active account is also available client-side as `Liferay.CommerceContext.account.accountId` (guard for null when no account is selected).
- **`headless-commerce-admin-order` account filtering can silently no-op** — a `filter=accountId eq ...` has been observed returning unfiltered results on some versions. When correctness matters, fetch with `pageSize=100` + pagination and filter on `order.accountId` client-side, or verify the filter against your version before trusting it.

## Image Management

- **Direct upload**: for robust image handling, Base64 encode the image content and use the `/products/by-externalReferenceCode/{ERC}/images/by-base64` endpoint.
- **Persistence**: always include `"neverExpire": true` in the image payload to prevent the image from disappearing over time.
- **Update workflow**: to replace an image, first `GET` the existing images, `DELETE` them via the `attachment` endpoint, then perform a new `POST` with the updated content.

## SKU Engineering

Creating variant SKUs (e.g., different pack sizes) is a multistep process:

1. **Attach option**: `PATCH` the product via its ERC to attach a global Option ID.

1. **Add option values**: `POST` to the `productOptionValues` endpoint to define the specific variants (e.g., "10 pack", "20 pack").

1. **Create SKU**: `POST` the SKU directly to the product's SKUs endpoint, including the `skuOptions` block to map the specific `optionValueId`.

## Price Lists — Use v2.0, Not v1.0

The `headless-commerce-admin-pricing` **v1.0** price-list create endpoint is defective: it 403s **every** caller, including full admins (transposed group-id arguments route the permission check against group 0). Use **v2.0** instead, and prefer `PUT` by external reference code, which upserts idempotently. Two v2.0 quirks: numeric `id` fields are bean-validated even when addressing by ERC (send them anyway), and the catalog read is the **singular** `/catalog/{id}` path. Reading an account's price-list-relevant groups additionally requires the `VIEW_ACCOUNT_GROUPS` permission.

## B2B Account Onboarding

When onboarding accounts and users via `headless-admin-user` APIs, adhere to these validation rules:

- **Postal addresses**:
  - `addressCountry`: must match Liferay's internal name exactly (e.g., "United Kingdom", not "GB").
  - `addressRegion`: must perfectly match Liferay's region dictionary (e.g., "London, City of").
  - `addressType`: must be lowercase (e.g., "billing", "shipping").
- **User association**: use the `by-email-address` endpoint to associate an existing user with an account: `POST /accounts/{accountId}/user-accounts/by-email-address/{email}`.
- **Role assignment**: assign account roles by their ERC: `POST /accounts/{accountId}/account-roles/by-external-reference-code/{ROLE_ERC}/user-accounts/{userId}`.

## Account Membership and Logos

- **Membership by ERC**: `POST /accounts/by-external-reference-code/{accountErc}/user-accounts/by-external-reference-code/{userErc}` answers `204`, and `204` again when the user is already a member, so it is safe to repeat. A user's own `GET /user-accounts/{id}` lists the result in `accountBriefs[].externalReferenceCode`. An account PUT that carries no `accountUserAccounts` keeps the members (verified on 2026.Q3).
- **Logos: `logoBase64` alone sets nothing on an account that has no logo.** A `PATCH` with only `logoBase64` answers `200` and leaves `logoId` at `0`, because the resource treats a missing `logoId` and `logoExternalReferenceCode` as a request to delete the logo. Set the logo at creation (`POST` takes `logoBase64` directly), or name a Documents and Media file in the company group with `logoExternalReferenceCode`. Read `logoId` back to confirm. A later `PATCH` without logo fields keeps the logo. A full `PUT` (including the site initializer's `accounts.json` upsert) deletes it unless the body names a logo file.

## Success Signal

TODO / inferred — verify against a running bundle. Confirm the created catalog, products, and SKUs list via the Commerce headless APIs and appear under Commerce → Catalogs in the Control Panel.