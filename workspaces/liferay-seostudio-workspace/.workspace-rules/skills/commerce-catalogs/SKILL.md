---

alwaysApply: false
description: Standards for managing Liferay Commerce catalogs, products, SKUs, and B2B account onboarding via Headless APIs. Use when the user works with commerce products, variants, options, specifications, product images, or B2B accounts and roles.
globs: {commerce/**/*,**/*commerce*}
name: commerce-catalogs

---

# Liferay Commerce Engineering Standards

## Product Management via Headless APIs

When programmatically managing products, follow these strict architectural findings:

- **The ERC mandate**: the `externalReferenceCode` (ERC) is the only reliable identifier for a product. Always use the ERC based endpoints (e.g., `/products/by-externalReferenceCode/{ERC}`) for `PATCH`, `DELETE`, and subresource operations to avoid `404 NOT FOUND` errors.
- **Product options**: options (size, color, etc.) are global, prerequisite entities. They must be created via the global `options` endpoint *before* you can link them to a specific product.
- **Specifications**: do not use nested `POST` endpoints for specifications. Instead, `PATCH` the product itself via its ERC and supply a `productSpecifications` array containing the `specificationKey` and `value`.

## Image Management

- **Direct upload**: for robust image handling, Base64 encode the image content and use the `/products/by-externalReferenceCode/{ERC}/images/by-base64` endpoint.
- **Persistence**: always include `"neverExpire": true` in the image payload to prevent the image from disappearing over time.
- **Update workflow**: to replace an image, first `GET` the existing images, `DELETE` them via the `attachment` endpoint, then perform a new `POST` with the updated content.

## SKU Engineering

Creating variant SKUs (e.g., different pack sizes) is a multistep process:

1. **Attach option**: `PATCH` the product via its ERC to attach a global Option ID.

1. **Add option values**: `POST` to the `productOptionValues` endpoint to define the specific variants (e.g., "10 pack", "20 pack").

1. **Create SKU**: `POST` the SKU directly to the product's SKUs endpoint, including the `skuOptions` block to map the specific `optionValueId`.

## B2B Account Onboarding

When onboarding accounts and users via `headless-admin-user` APIs, adhere to these validation rules:

- **Postal addresses**:
  - `addressCountry`: must match Liferay's internal name exactly (e.g., "United Kingdom", not "GB").
  - `addressRegion`: must perfectly match Liferay's region dictionary (e.g., "London, City of").
  - `addressType`: must be lowercase (e.g., "billing", "shipping").
- **User association**: use the `by-email-address` endpoint to associate an existing user with an account: `POST /accounts/{accountId}/user-accounts/by-email-address/{email}`.
- **Role assignment**: assign account roles by their ERC: `POST /accounts/{accountId}/account-roles/by-external-reference-code/{ROLE_ERC}/user-accounts/{userId}`.