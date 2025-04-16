# 7.4 Breaking Changes

This document presents a chronological list of changes that break existing functionality, APIs, or contracts with third party Liferay developers or users. We try our best to minimize these disruptions, but sometimes they are unavoidable.

Here are some of the types of changes documented in this file:

* Functionality that is removed or replaced
* API incompatibilities: Changes to public Java or JavaScript APIs
* Changes to context variables available to templates
* Changes in CSS classes available to Liferay themes and portlets
* Configuration changes: Changes in configuration files, like `portal.properties`, `system.properties`, etc.
* Execution requirements: Java version, J2EE Version, browser versions, etc.
* Deprecations or end of support: For example, warning that a certain feature or API will be dropped in an upcoming version.

*This document has been reviewed through the breaking change entry at commit `90a08686f0a880cebbedbfb27328fea50b2f9991`.*

Each change must have a brief descriptive title and contain the following information:

* **[Title]** Provide a brief descriptive title. Use past tense and follow the capitalization rules from <http://en.wikibooks.org/wiki/Basic_Book_Design/Capitalizing_Words_in_Titles>.
* **Date:** Specify the date you submitted the change. Format the date as *YYYY-MMM-DD* (e.g., 2014-Feb-03).
* **JIRA Ticket:** Reference the related JIRA ticket (e.g., LPS-12345) (Optional).
* **What changed?** Identify the affected component and the type of change that was made.
* **Who is affected?** Are end-users affected? Are developers affected? If the only affected people are those using a certain feature or API, say so.
* **How should I update my code?** Explain any client code changes required.
* **Why was this change made?** Explain the reason for the change. If applicable, justify why the breaking change was made instead of following a deprecation process.

Here is the template to use for each breaking change (note how it ends with a horizontal rule):

```
## Title
- **Date:** YYYY-MMM-DD
- **JIRA Ticket:** [LPS-1234](https://issues.liferay.com/browse/LPS-1234)

### What changed?

### Who is affected?

### How should I update my code?

### Why was this change made?

---------------------------------------

```

The remaining content of this document consists of the breaking changes listed in ascending chronological order.

## Removed the liferay-ui:flash Tag
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121732](https://issues.liferay.com/browse/LPS-121732)

### What changed?

The tag `liferay-ui:flash` has been deleted and is no longer available.

### Who is affected?

This affects any development that uses the `liferay-ui:flash` tag.

### How should I update my code?

If you still need to embed Adobe Flash content in a page, write your own code using a standard mechanism such as `SWFObject`.

### Why was this change made?

This change aligns with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html) in December 31, 2020 and with upcoming versions browsers removing Flash support.

---------------------------------------

## Removed the /portal/flash Path
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121733](https://issues.liferay.com/browse/LPS-121733)

### What changed?

Previously you could play an Adobe Flash movie by passing a movie URL as a parameter to the `/portal/flash` public path. The `/portal/flash path` path has been removed.

Additionally, the property and accessors have been removed from `ThemeDisplay` and are no longer available.

### Who is affected?

This affects you if you are using the `/c/portal/flash` path directly to show pages with Adobe Flash content.

### How should I update my code?

A direct code update is impossible. However, you can create a custom page that simulates the old behavior. The page could parse movie parameters from the URL and instantiate the movie using the common means for Adobe Flash reproduction.

### Why was this change made?

This change aligns with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html) in December 31, 2020 and with upcoming versions browsers removing Flash support.

---------------------------------------

## Removed the swfobject AUI Module
- **Date:** 2020-Oct-13
- **JIRA Ticket:** [LPS-121736](https://issues.liferay.com/browse/LPS-121736)

### What changed?

The AUI module `swfobject` has been removed. It provided a way to load the SWFObject library, commonly used to embed Adobe Flash content.

### Who is affected?

This affects you if you are making the SWFObject library available globally via the AUI `swfobject` module.

### How should I update my code?

If you still need to embed Adobe Flash content, you can inject the SWFObject library directly in your application using other available mechanisms.

### Why was this change made?

This change aligns with [Adobe dropping support for Flash](https://www.adobe.com/products/flashplayer/end-of-life.html) in December 31, 2020 and with upcoming versions browsers removing Flash support.

---------------------------------------

## Removed the AssetEntries_AssetCategories Table and Corresponding Code
- **Date:** 2020-Oct-16
- **JIRA Ticket:** [LPS-89065](https://issues.liferay.com/browse/LPS-89065)

### What changed?

The `AssetEntries_AssetCategories` mapping table and its corresponding code have been removed.

### Who is affected?

This affects you if you use `AssetEntryLocalService` and `AssetCategoryLocalService` to operate on relationships between asset entries and asset categories.

### How should I update my code?

Use the new methods in `AssetEntryAssetCategoryRelLocalService`. Note, that the method signatures are the same as they were in `AssetEntryAssetCategoryRelLocalService`.

### Why was this change made?

This change removes an unnecessary table and corresponding code.

It is a followup step to the [Liferay AssetEntries_AssetCategories Is No Longer Used](https://learn.liferay.com/dxp/latest/en/liferay-internals/reference/7-2-breaking-changes.html#liferay-assetentries-assetcategories-is-no-longer-used) 7.2 breaking change where the table was replaced by the `AssetEntryAssetCategoryRel` table and the corresponding interfaces in `AssetEntryLocalService` and `AssetCategoryLocalService` were moved into `AssetEntryAssetCategoryRelLocalService`.

---------------------------------------

## Refactored AntivirusScanner Support and Clamd Integration
- **Date:** 2020-Oct-21
- **JIRA Ticket:** [LPS-122280](https://issues.liferay.com/browse/LPS-122280)

### What changed?

The portal's Clamd integration implementation has been replaced by an OSGi service that uses a Clamd remote service. The AntivirusScanner OSGi integration has replaced the AntivirusScanner implementation selection portal properties and AntivirusScanner implementation hook registration portal properties.

### Who is affected?

This affects you if you are using the portal's Clamd integration implementation or if you are providing your own AntivirusScanner implementation via a hook.

### How should I update my code?

Enable the new Clamd integration and AntivirusScanner support. See [Enabling Antivirus Scanning for Uploaded Files](https://learn.liferay.com/dxp/latest/en/system-administration/file-storage/enabling-antivirus-scanning-for-uploaded-files.html).

If you are providing your own AntivirusScanner implementation via a hook, convert it to an OSGi service that has a service ranking higher greater than zero. The Clamd remote service AntivirusScanner implementation service ranking is zero.

### Why was this change made?

This change supports container environments better and unifies the API to do OSGi integration.

---------------------------------------

## Changed Entity Display Page Registration Tracking Logic
- **Date:** 2020-Oct-27
- **JIRA Ticket:** [LPS-122275](https://issues.liferay.com/browse/LPS-122275)

### What changed?

The persisting (tracking) of entity associations with display pages has changed. In Liferay DXP/Portal versions 7.1 through 7.3, only entity associations with default display pages were persisted; entities with no display page associations and entity associations with specific, non-default display pages were not persisted. This change switches the behavior.

Here is the new behavior:

- Entities linked to a default display page are not persisted.
- Entities that do not have a display page are persisted.
- Entities that have a specific, non-default display page are persisted.

### Who is affected?

This affects you if you have custom entities for which display pages can be created.

### How should I update my code?

If you have custom entities with display pages, swap the display page logic by adding the `BaseUpgradeAssetDisplayPageEntry` upgrade process to your application. The process receives a table, primary key column name, and a class name.

### Why was this change made?

This change makes the display page logic more consistent with the overall display page concept.

---------------------------------------

## Removed Deprecated and Unused JSP Tags
- **Date:** 2020-Nov-24
- **JIRA Ticket:** [LPS-112476](https://issues.liferay.com/browse/LPS-112476)

### What changed?

A series of deprecated and unused JSP tags have been removed and are no longer available. These tags are included:

- `clay:table`
- `liferay-ui:alert`
- `liferay-ui:input-scheduler`
- `liferay-ui:organization-search-container-results`
- `liferay-ui:organization-search-form`
- `liferay-ui:ratings`
- `liferay-ui:search-speed`
- `liferay-ui:table-iterator`
- `liferay-ui:toggle-area`
- `liferay-ui:toggle`
- `liferay-ui:user-search-container-results`
- `liferay-ui:user-search-form`

### Who is affected?

This affects you if you are using any of the removed tags.

### How should I update my code?

See the 7.3 [`liferay-ui.tld`](https://github.com/liferay/liferay-portal/blob/7.3.x/util-taglib/src/META-INF/liferay-ui.tld) for removed tags that have replacements. However, many of the tags have no direct replacement. If you still need to use a tag that does not have a direct replacement, you can copy the old implementation and serve it directly from your project.

### Why was this change made?

These tags were removed in an attempt to clarify the default JSP component offering and to focus on providing a smaller but higher quality component set.

---------------------------------------

## Replaced the .container-fluid-1280 CSS Class
- **Date:** 2020-Nov-24
- **JIRA Ticket:** [LPS-123894](https://issues.liferay.com/browse/LPS-123894)

### What changed?

The `.container-fluid-1280` CSS class has been replaced with `.container-fluid.container-fluid-max-xl`. The compatibility layer that had `.container-fluid-1280`'s style has been removed too.

### Who is affected?

This affects you if your container elements have the `.container-fluid-1280` CSS class.

### How should I update my code?

Use the updated CSS classes from Clay `.container-fluid.container-fluid-max-xl` instead of `.container-fluid-1280`. Alternatively, use ClayLayout [Components](https://clayui.com/docs/components/layout.html) and [TagLibs](https://clayui.com/docs/get-started/using-clay-in-jsps.html#clay-sidebar).

### Why was this change made?

The change removes deprecated legacy code and improves code consistency and performance.

---------------------------------------

## Disabled Runtime Minification of CSS and JavaScript Resources by Default
- **Date:** 2020-Nov-27
- **JIRA Ticket:** [LPS-123550](https://issues.liferay.com/browse/LPS-123550)

### What changed?

The `minifier.enable` portal property now defaults to `false`. Instead of performing minification of CSS and JS resources at run time, we prepare pre-minified resources at build time. There should be no user-visible changes in page styles or logic.

### Who is affected?

This affects you if your implementations depend on the runtime minifier (usually the Google Closure Compiler).

### How should I update my code?

If you want to maintain the former runtime minification behavior, set the `minifier.enable` portal property to `true`.

### Why was this change made?

Moving frontend resource minification from run time to build time reduces server load and facilitates using the latest minification technologies available within the frontend ecosystem.

---------------------------------------

## Removed the SoyPortlet Class
- **Date:** 2020-Dec-09
- **JIRA Ticket:** [LPS-122955](https://issues.liferay.com/browse/LPS-122955)

### What changed?

The `SoyPortlet` class has been removed. It used to implement a portlet whose views were backed by Closure Templates (Soy).

### Who is affected?

This affects you if you are using `SoyPortlet` as a base for your portlet developments.

### How should I update my code?

We heavily recommend re-writing your Soy portlets using either a well established architecture such as `MVCPortlet` using JSPs or a particular frontend framework of your choice.

### Why was this change made?

This was done as a way to simplify our frontend technical offering and better focus on proven technologies with high demand in the market.

A further exploration and analysis of the different front-end options available can be found in [The State of Frontend Infrastructure](https://liferay.dev/blogs/-/blogs/the-state-of-frontend-infrastructure) including a rationale on why we are moving away from Soy:

> Liferay invested several years into Soy believing it was "the Holy Grail". We believed the ability to compile Closure Templates would provide us with performance comparable to JSP and accommodate reusable components from other JavaScript frameworks. While Soy came close to achieving some of our goals, we never hit the performance we wanted and more importantly, we felt like we were the only people using this technology.

---------------------------------------

## Removed Server-side Closure Templates (Soy) Support
- **Date:** 2020-Dec-14
- **JIRA Ticket:** [LPS-122956](https://issues.liferay.com/browse/LPS-122956)

### What changed?

The following modules and the classes they exported to allow Soy rendering server-side have been removed:
- `portal-template-soy-api`
- `portal-template-soy-impl`
- `portal-template-soy-context-contributor`

To simplify the migration, the following modules are deprected but available to provide only client-side initialization of previous Soy components:
- `portal-template-soy-renderer-api`
- `portal-template-soy-renderer-impl`

### Who is affected?

This affects you if you are using removed classes like `SoyContext`, `SoyHTMLData`, etc. and declaring `TemplateContextContributor` using `LANG_TYPE_SOY` as the value for the `lang.type` attribute.

This affects you if you are initializing Soy components using our Soy `ComponentRenderer`.

### How should I update my code?

There is no replacement for the removed Soy support. If the first scenario describes you, switch to a different supported template language and rewrite your templates and components.

If you are using `ComponentRenderer`, the only difference should be that your components no longer produce markup server-side. If this is important to you, a temporary workaround has been added. You can manually generate a version of the markup you want to render server-side and pass the markup as a `__placeholder__` property in your `context` parameter. Remember, `ComponentRenderer` is deprecated and will eventually be removed; so we kindly recommend rewriting your component using a different technology.

### Why was this change made?

This is done as a way to simplify our frontend technical offering and better focus on proven technologies with high demand in the market.

A further exploration and analysis of the different front-end options available can be found in [The State of Frontend Infrastructure](https://liferay.dev/blogs/-/blogs/the-state-of-frontend-infrastructure) including a rationale on why we are moving away from Soy:

> Liferay invested several years into Soy believing it was "the Holy Grail". We believed the ability to compile Closure Templates would provide us with performance comparable to JSP and accommodate reusable components from other JavaScript frameworks. While Soy came close to achieving some of our goals, we never hit the performance we wanted and more importantly, we felt like we were the only people using this technology.

---------------------------------------

## Removed com.liferay.portal.kernel.model.PortletPreferences Methods getPreferences and setPreferences
- **Date:** 2020-Dec-20
- **JIRA Ticket:** [LPS-122562](https://issues.liferay.com/browse/LPS-122562)

### What changed?

Portlet preferences are no longer stored and retrieved as XML. They are now stored in a table called `PortletPreferenceValue` that has separate columns for preference names and preference values.

### Who is affected?

This affects you if you are directly getting or setting portlet preferences via `com.liferay.portal.kernel.model.PortletPreferences` methods `getPreferences` or `setPreferences`.

### How should I update my code?

Access `javax.portlet.PortletPreferences` instances via `PortletPreferencesLocalService`. Get and set preferences using the `javax.portlet.PortletPreferences` API.

### Why was this change made?

This change simplifies upgrades, reduces storage requirements, and supports querying preferences without using the `like` operator.

---------------------------------------

## Removed CSS Compatibility Layer
- **Date:** 2021-Jan-02
- **JIRA Ticket:** [LPS-123359](https://issues.liferay.com/browse/LPS-123359)

### What changed?

The support for Boostrap 3 markup has been deleted and is no longer available.

### Who is affected?

This affects you if you are using Boostrap 3 markup or if you have not correctly migrated to Boostrap 4 markup.

### How should I update my code?

If you are using Clay markup you can update it by following the last [Clay components](https://clayui.com/docs/components/index.html) version. If your markup is based on Boostrap 3, you can update the markup with Boostrap 4 markup following the [Bootstrap migration guidelines](https://getbootstrap.com/docs/4.4/migration/).

### Why was this change made?

The configurable CSS compatibility layer simplified migrating from Liferay 7.0 to 7.1 but removing the layer resolves conflicts with new styles and improves general CSS weight.

---------------------------------------

## Removed the spi.id Property From the Log4j XML Definition File
- **Date:** 2021-Jan-19
- **JIRA Ticket:** [LPS-125998](https://issues.liferay.com/browse/LPS-125998)

### What changed?

The `spi.id` property in the Log4j XML definition file has been removed.

### Who is affected?

This affects you if you are using `@spi.id@` in its custom Log4j XML definition file.

### How should I update my code?

Remove `@spi.id@` from your Log4j XML definition file.

### Why was this change made?

SPI was removed by [LPS-110758](https://issues.liferay.com/browse/LPS-110758).

---------------------------------------

## Removed Deprecated Attributes From the frontend-taglib-clay Tags
- **Date:** 2021-Jan-26
- **JIRA Ticket:** [LPS-125256](https://issues.liferay.com/browse/LPS-125256)

### What changed?

The deprecated attributes have been removed from the `frontend-taglib-clay` TagLib.

### Who is affected?

This affects you if you use deprecated attributes in `<clay:*>` tags.

### Why was this change made?

The `frontend-taglib-clay` module is now using components from [`Clay v3`](https://github.com/liferay/clay), which does not support the removed attributes.

---------------------------------------

## Changed Handling of HTML Tag Boolean Attributes
- **Date:** 2021-Feb-18
- **JIRA Ticket:** [LPS-127832](https://issues.liferay.com/browse/LPS-127832)

### What changed?

Boolean HTML attributes will only be rendered if passed a value of `true`. The value for such attributes will be their canonical name.

Previously, a value such as `false` for a `disabled` attribute would be rendered into the DOM as `disabled="false"`; now, it the attribute is omitted. Likewise, a `true` value for a `disabled` attribute was formerly rendered into the DOM as `disabled="true"`; now it is rendered as `disabled="disabled"`.

### Who is affected?

This affects you if you are passing the following boolean attributes to tag libraries:

- `"allowfullscreen"`
- `"allowpaymentrequest"`
- `"async"`
- `"autofocus"`
- `"autoplay"`
- `"checked"`
- `"controls"`
- `"default"`
- `"disabled"`
- `"formnovalidate"`
- `"hidden"`
- `"ismap"`
- `"itemscope"`
- `"loop"`
- `"multiple"`
- `"muted"`
- `"nomodule"`
- `"novalidate"`
- `"open"`
- `"playsinline"`
- `"readonly"`
- `"required"`
- `"reversed"`
- `"selected"`
- `"truespeed"`

### How should I update my code?

Make sure to pass a `true` value to boolean attributes you want present in the DOM. Update CSS selectors that target a `true` value (e.g., `[disabled="true"]`) to target presence of the attribute (e.g., `[disabled]`) or its canonical name (e.g., `[disabled="disabled"]`).

### Why was this change made?

This change was made for better compliance with [the HTML Standard](https://html.spec.whatwg.org/#boolean-attribute), which says that "The presence of a boolean attribute on an element represents the true value, and the absence of the attribute represents the false value. If the attribute is present, its value must either be the empty string or a value that is an ASCII case-insensitive match for the attribute's canonical name."

---------------------------------------

## Removed com.liferay.portal.kernel.model.PortalPreferences Methods getPreferences and setPreferences
- **Date:** 2020-Mar-31
- **JIRA Ticket:** [LPS-124338](https://issues.liferay.com/browse/LPS-124338)

### What changed?

`PortalPreferences` preferences are now stored in the `PortalPreferenceValue` table.

### Who is affected?

This affects you if you are directly getting or setting portal preferences via `com.liferay.portal.kernel.model.PortalPreferences` methods `getPreferences` or `setPreferences`.

### How should I update my code?

Access `javax.portlet.PortalPreferences` instances via `PortalPreferencesLocalService`. Get and set preferences using the `javax.portlet.PortalPreferences` API.

### Why was this change made?

This change simplifies upgrades, reduces storage requirements, and supports querying preferences without using the `like` operator.

---------------------------------------

## item-selector-taglib No Longer fires coverImage-related Events
- **Date:** 2021-Apr-15
- **JIRA Ticket:** [LPS-130359](https://issues.liferay.com/browse/LPS-130359)

### What changed?

The `ImageSelector` JavaScript module no longer fires the `coverImageDeleted`, `coverImageSelected`, and `coverImageUploaded` events using the `Liferay.fire()` API. These events facilitated communication between the `item-selector-taglib` module and `blogs-web` module. Now `Liferay.State` synchronizes the communication using `imageSelectorCoverImageAtom`.

### Who is affected?

This affects you if you are listening for the removed events with `Liferay.on()` or similar functions.

### How should I update my code?

In practice, you should not observe interaction between these two modules, but if you must, you could subscribe to `imageSelectorCoverImageAtom` using the `Liferay.State.subscribe()` API.

### Why was this change made?

`Liferay.fire()` and `Liferay.on()` publish globally visible events on a shared channel. The `Liferay.State` API is a better fit for modules that wish to coordinate at a distance in this way, and it does so in a type-safe manner.

---------------------------------------

## Changed the OAuth 2.0 Token Instrospection Feature Identifier
- **Date:** 2021-May-04
- **JIRA Ticket:** [LPS-131573](https://issues.liferay.com/browse/LPS-131573)

### What changed?

The OAuth 2.0 Token Instrospection Feature Identifier was changed from `token_introspection` to `token.introspection`.

### Who is affected?

This affects you if you are using the Token Introspection feature identifier. Here are a couple use cases:

- Adding an OAuth 2.0 application programatically with Token Introspection feature identifier enabled.
- Checking if Token Introspection feature identifier is enabled for an OAuth 2.0 application.

### How should I update my code?

Change the token from `token_introspection` to `token.introspection`.

### Why was this change made?

This change was made to align and standarize all OAuth 2.0 constants in our code. We recommend using a dot to separate words in feature identifiers.

---------------------------------------

## Removed JournalArticle's Content Field
- **Date:** 2021-May-21
- **JIRA Ticket:** [LPS-129058](https://issues.liferay.com/browse/LPS-129058)

### What changed?

`JournalArticle` content is now stored by Dynamic Data Mapping (DDM) Field services.

### Who is affected?

This affects you if you are directly setting the `JournalArticle` content field.

### How should I update my code?

Use `JournalArticleLocalService`'s update methods instead of directly setting the content field.

### Why was this change made?

This change facilitates referencing file, page, and web content DDM fields in the database without fetching and parsing the content.

---------------------------------------

## Replaced com.liferay.portal.kernel.util.StringBundler with com.liferay.petra.string.StringBundler
- **Date:** 2021-Jun-25
- **JIRA Ticket:** [LPS-133200](https://issues.liferay.com/browse/LPS-133200)

### What changed?

The `com.liferay.petra.string.StringBundler` class has been deprecated. The `com.liferay.portal.kernel.util.StringBundler` class has replaced it.

Here are some methods that now return `com.liferay.petra.string.StringBundler` instead of `com.liferay.portal.kernel.util.StringBundler`:

- `com.liferay.frontend.taglib.dynamic.section.BaseJSPDynamicSection.java#modify`
- `com.liferay.frontend.taglib.dynamic.section.DynamicSection#modify`
- `com.liferay.portal.kernel.io.unsync.UnsyncStringWriter#getStringBundler`
- `com.liferay.portal.kernel.layoutconfiguration.util.RuntimePage#getProcessedTemplate`
- `com.liferay.portal.kernel.layoutconfiguration.util.RuntimePageUtil#getProcessedTemplate`
- `com.liferay.portal.kernel.servlet.BufferCacheServletResponse#getStringBundler`
- `com.liferay.portal.kernel.servlet.taglib.BodyContentWrapper.java#getStringBundler`
- `com.liferay.portal.kernel.theme.PortletDisplay#getContent`
- `com.liferay.portal.kernel.util.StringUtil#replaceToStringBundler`
- `com.liferay.portal.kernel.util.StringUtil#replaceWithStringBundler`
- `com.liferay.portal.layoutconfiguration.util.PortletRenderer#render`
- `com.liferay.portal.layoutconfiguration.util.PortletRenderer#renderAjax`
- `com.liferay.portal.layoutconfiguration.util.RuntimePageImpl#getProcessedTemplate`
- `com.liferay.taglib.BaseBodyTagSupport#getBodyContentAsStringBundler`
- `com.liferay.taglib.BodyContentWrapper#getStringBundler`
- `com.liferay.taglib.aui.NavBarTag#getResponsiveButtonsSB`

### Who is affected?

This affects you if you call one of these methods.

### How should I update my code?

Import `com.liferay.petra.string.StringBundler` instead of
`com.liferay.portal.kernel.util.StringBundler`.

### Why was this change made?

The `com.liferay.petra.string.StringBundler` class has been deprecated.

---------------------------------------

## UserLocalService related classes have modified public API
- **Date:** 2021-Jul-07
- **JIRA Ticket:** [LPS-134096](https://issues.liferay.com/browse/LPS-134096)

### What changed?

A number of methods which normally return `void` now return a `boolean` value instead. This list includes:

- com.liferay.portal.kernel.service.UserLocalService#addDefaultGroups
- com.liferay.portal.kernel.service.UserLocalService#addDefaultRoles
- com.liferay.portal.kernel.service.UserLocalService#addDefaultUserGroups
- com.liferay.portal.kernel.service.UserLocalServiceUtil#addDefaultGroups
- com.liferay.portal.kernel.service.UserLocalServiceUtil#addDefaultRoles
- com.liferay.portal.kernel.service.UserLocalServiceUtil#addDefaultUserGroups
- com.liferay.portal.kernel.service.UserLocalServiceWrapper#addDefaultGroups
- com.liferay.portal.kernel.service.UserLocalServiceWrapper#addDefaultRoles
- com.liferay.portal.kernel.service.UserLocalServiceWrapper#addDefaultUserGroups
- com.liferay.portal.service.impl.UserLocalServiceImpl#addDefaultGroups
- com.liferay.portal.service.impl.UserLocalServiceImpl#addDefaultRoles
- com.liferay.portal.service.impl.UserLocalServiceImpl#addDefaultUserGroups

### Who is affected?

Everyone calling one of these methods

### How should I update my code?

No immediate action is needed, but it's important to note the return type has changed.

### Why was this change made?

This change was made in order to check if default groups, roles, and/or user groups were added to the given user, or if the user already had these associations.

---------------------------------------

## Removed the frontend-css-web CSS module
- **Date:** 2021-Aug-02
- **JIRA Ticket:** [LPS-127085](https://issues.liferay.com/browse/LPS-127085)

### What changed?

The `frontend-css-web` module has been removed and its CSS files have been upgraded.

### Who is affected?

This change affects the following modules:

- `modules/apps/asset/asset-taglib/`
- `modules/apps/asset/asset-tags-navigation-web/`
- `modules/apps/captcha/captcha-taglib/`
- `modules/apps/comment/comment-web/`
- `modules/apps/commerce/commerce-product-content-web/`
- `modules/apps/document-library/document-library-web/`
- `modules/apps/dynamic-data-lists/dynamic-data-lists-web/`
- `modules/apps/dynamic-data-mapping/dynamic-data-mapping-form-web/`
- `modules/apps/dynamic-data-mapping/dynamic-data-mapping-web/`
- `modules/apps/flags/flags-taglib/`
- `modules/apps/frontend-css/frontend-css-web/`
- `modules/apps/frontend-editor/frontend-editor-ckeditor-web/`
- `modules/apps/frontend-js/frontend-js-aui-web/`
- `modules/apps/frontend-js/frontend-js-components-web/`
- `modules/apps/frontend-taglib/frontend-taglib/`
- `modules/apps/frontend-theme/frontend-theme-styled/`
- `modules/apps/item-selector/item-selector-taglib/`
- `modules/apps/knowledge-base/knowledge-base-web/`
- `modules/apps/mobile-device-rules/mobile-device-rules-web/`
- `modules/apps/polls/polls-web/`
- `modules/apps/portal-settings/portal-settings-authentication-cas-web/`
- `modules/apps/product-navigation/product-navigation-control-menu-web/`
- `modules/apps/site-navigation/site-navigation-directory-web/`
- `modules/apps/social/social-bookmarks-taglib/`
- `modules/apps/staging/staging-taglib/`
- `modules/apps/wiki/wiki-web/`
- `modules/dxp/apps/portal-search-tuning/portal-search-tuning-rankings-web/`
- `portal-kernel/`
- `portal-web/`

### How should I update my code?

There are no required code updates.

### Why was this change made?

This change removes deprecated legacy code from DXP/Portal and improves code performance and code consistency.

---------------------------------------

## Removed Some SanitizedServletResponse Static Methods, the HttpHeaders X_XSS_PROTECTION Constant, and http.header.secure.x.xss.protection Portal Property
- **Date:** 2021-Aug-05
- **JIRA Ticket:** [LPS-134188](https://issues.liferay.com/browse/LPS-134188)

### What changed?

The following methods, constant, and portal property have been removed.

Methods:

- `com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditor`
- `com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditor`
- `com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditorOnNextRequest`
- `com.liferay.portal.kernel.servlet.SanitizedServletResponse#disableXSSAuditorOnNextRequest`

Constant:

- `com.liferay.portal.kernel.servlet.HttpHeaders#X_XSS_PROTECTION`

Portal Property:

- `http.header.secure.x.xss.protection`

### Who is affected?

This affects you if you call these methods, use the constant, or use the portal property.

### How should I update my code?

Remove your code that calls these methods or uses the constant. The X-Xss-Protection header has no effect on modern browsers and may give a false sense of security.

Remove the `http.header.secure.x.xss.protection` property from your portal property extension files (e.g., `portal-ext.properties`).

### Why was this change made?

The X-Xss-Protection header is no longer supported by modern browsers. These static methods, constant, and portal property relate to the X-Xss-Protection header.

---------------------------------------

## Replaced the OpenIdConnectServiceHandler Interface With the OpenIdConnectAuthenticationHandler
- **Date:** 2021-Aug-09
- **JIRA Ticket:** [LPS-124898](https://issues.liferay.com/browse/LPS-124898)

### What changed?

The `OpenIdConnectServiceHandler` interface has been removed and replaced by the `OpenIdConnectAuthenticationHandler` interface.

Old interface:

```
portal.security.sso.openid.connect.OpenIdConnectServiceHandler
```

New interface:

```
portal.security.sso.openid.connect.OpenIdConnectAuthenticationHandler
```

### Who is affected?

This affects you if you are implementing or using the `OpenIdConnectServiceHandler` interface.

### How should I update my code?

If your code invokes the `OpenIdConnectServiceHandler` interface, change it to invoke the `OpenIdConnectAuthenticationHandler` interface. This requires providing an `UnsafeConsumer` for signing in the DXP/Portal user.

If you have implemented the `OpenIdConnectServiceHandler` interface, implement the `OpenIdConnectAuthenticationHandler` interface and provide a way to refresh the user's OIDC access tokens using the provided refresh tokens. If you don't make this provision, sessions will invalidate when the initial access tokens expire.

### Why was this change made?

This change improves OIDC refresh token handling. The change was made for these reasons:

- To detach the access token refresh process from HTTP request handling. Without this detachment, there can be problems maintaining OIDC sessions with providers that only allow refresh tokens to be used once. Premature portal session invalidation can occur.

- To avoid premature portal session invalidation for OIDC providers that provide refresh tokens that expire at the same time as their corresponding access tokens.

---------------------------------------

## Renamed Language Keys

- **Date:** 2021-Sep-09
- **JIRA Ticket:** LPS-135504

### What changed?

All module language keys were moved to a module called `portal-language-lang` at `liferay-[dxp|portal]/modules/apps/portal-language/portal-language-lang`. In some cases where modules used language keys with the same name but different values, language keys were added to accommodate the different values. From an affected module's perspective, the language key has been renamed.

### Who is affected?

This affects you if you are using or overriding a language key that has been renamed. [Renamed Language Keys](https://learn.liferay.com/dxp/latest/en/installation-and-upgrades/upgrading-liferay/reference/renamed-language-keys.html) maps the old language key names to the new names.

### How should I update my code?

Rename all instances of the renamed language keys to their new names based on the mappings in [Renamed Language Keys](https://learn.liferay.com/dxp/latest/en/installation-and-upgrades/upgrading-liferay/reference/renamed-language-keys.html).

### Why was this change made?

Centralized language keys are easier to manage.

---------------------------------------

## Moved the CAS SSO Modules to the portal-security-sso-cas Project
- **Date:** 2021-Sep-15
- **JIRA Ticket:** [LPS-88905](https://issues.liferay.com/browse/LPS-88905)

### What changed?

The CAS SSO modules were moved from the `portal-security-sso` project to a new project named `portal-security-sso-cas`. The new project is deprecated but is available to download from Liferay Marketplace.

### Who is affected?

This affects anyone using CAS SSO as an authentication system.

### How should I update my code?

If you want to continue using CAS SSO as an authentication system, you must download the corresponding app from Liferay Marketplace.

### Why was this change made?

This is part of an ongoing effort to consolidate SSO support and increase focus on open standards.

---------------------------------------

## Namespaced the clay:select Tag's name Attribute for Printing
- **Date:** 2021-Sep-15
- **JIRA Ticket:** [LPS-139131](https://issues.liferay.com/browse/LPS-139131)

### What changed?

The attribute `name` of `clay:select` now includes the portlet namespace (if available) by default when printed.

### Who is affected?

This affects everyone who uses `clay:select` and its `name` attribute to handle data server-side.

### How should I update my code?

If you have been namespacing the attribute on your own by prefixing `liferayPortletResponse.getNamespace() + NAME_VALUE`
to the value, drop that prefix and use `name="NAME_VALUE"` directly.

If you want full control (or the namespaced version is insufficient), you can revert back to the old
behavior by also passing `useNamespace="<%= Boolean.FALSE %>"` to the tag.

### Why was this change made?

This change was made to better match the current `aui:select` tag behavior, facilitate a future migration to the `clay:select` tag, and simplify current `clay:select` tag usage.

---------------------------------------

## Removed the Core Registry API and Registry Implementation modules
- **Date:** 2021-Sep-28
- **JIRA Ticket:** [LPS-138126](https://issues.liferay.com/browse/LPS-138126)

### What changed?

The core Registry API (`registry-api`) and Registry Implementation (`registry-impl`) modules have been removed.

### Who is affected?

This affects anyone using the Registry API.

### How should I update my code?

Access the native OSGi API using the system bundle's `org.osgi.framework.BundleContext`. Get the context by calling the `com.liferay.portal.kernel.module.util.SystemBundleUtil.getBundleContext()` method.

### Why was this change made?

The native OSGi API makes the bridged API unnecessary.

---------------------------------------

## Removed Support for Web Content in Document Types
- ** Date:** 2021-Sep-30
- ** JIRA Ticket:** [LPS-139710](https://issues.liferay.com/browse/LPS-139710)

### What changed?

Support for Web Content fields in Document Types has been removed.

### Who is affected?

This affects anyone using a Web Content field Document Type.

### How should I update my code?

Remove all references to Web Content field Document Types.

### Why was this change made?

The Web Content field Document Type was an accidental feature included in Documents and Media. Its use case was unclear. When staging was enabled, the feature was causing circularity problems between Web Content articles linked to Documents and Media documents.

---------------------------------------

## OpenID Connect Provider Signing Algorithm Must Be Configured If Different From RS256

- **Date:** 2021-Sep-30
- **JIRA Ticket:** [LPS-138756](https://issues.liferay.com/browse/LPS-138756)

#### What changed?

The portal's OpenID Connect client now requires explicitly stating the ID Token signing algorithm agreed with the provider.

#### Who is affected?

This affects anyone integrating OpenID Connect providers that sign ID Tokens using a signing algorithm other than the **first** supported signing algorithm listed in the UI. The list is served by the provider's Discovery Endpoint, or configured offline in the UI.

#### How should I update my code?

In the UI, review the OpenID Connect Provider Connection configuration(s). Specify the agreed algorithm as the Registered ID Token Signing Algorithm.

#### Why was this change made?

This improves using all signing algorithms supported by OpenID Connect providers.

---------------------------------------

## Restricted the Service Builder Task to Service Module Folders that have a Service XML File

- **Date:** 2021-Nov-02
- **JIRA Ticket:** [LPS-129696](https://issues.liferay.com/browse/LPS-129696)

#### What changed?

Automatically applied the Service Builder plugin to all OSGi projects but restricted the `buildService` task target to `*-service` module folders that contain a `service.xml` file.

#### Who is affected?

This affects you if you are running the `buildService` task in a the Liferay Workspace.

#### How should I update my code?

This requires no code changes. When you run the `buildService` task, you must target a `*-service` module folder that contains a `service.xml` file.

#### Why was this change made?

This was done to clarify the folder that `buildService` should target.

---------------------------------------

## Updated addFragmentEntry methods in FragmentEntryLocalService and FragmentEntryService

- **Date:** 2021-Dec-16
- **JIRA Ticket:** [LPS-125034](https://issues.liferay.com/browse/LPS-125034)

### What changed?

The `addFragmentEntry` methods in `FragmentEntryLocalService` and `FragmentEntryService` have a new parameter for the icon of a `FragmentEntry`.

### Who is affected?

This affects anyone using `FragmentEntryLocalService` and `FragmentEntryService` to add fragment entries.

### How should I update my code?

When you use the new `addFragmentEntry` methods in `FragmentEntryLocalService` and `FragmentEntryService`, you must include a new `String` parameter for the `icon` of a `FragmentEntry`.

### Why was this change made?

This change was made so that a new icon can be used when importing a fragment.

---------------------------------------

## The getSegmentsExperienceIds methods in FragmentEntryProcessorContext, DefaultFragmentEntryProcessorContext, FragmentRendererContext, DefaultFragmentRendererContext have been removed.
- **Date:** 2021-Dec-17
- **JIRA Ticket:** [LPS-141471](https://issues.liferay.com/browse/LPS-141471)

### What changed?

The `getSegmentsExperienceIds` methods from `FragmentEntryProcessorContext`, `DefaultFragmentEntryProcessorContext`, `FragmentRendererContext` and `DefaultFragmentRendererContext` have been removed. The method with signature `getContextObjects(JSONObject, String)` from `FragmentEntryConfigurationParser` has been removed.

### Who is affected?

This affects anyone using `FragmentEntryProcessorContext`, `DefaultFragmentEntryProcessorContext`, `FragmentRendererContext` or `DefaultFragmentRendererContext` to get the `long array` of `segmentsExperienceIds`, or if you use the method with signature `getContextObjects(JSONObject, String)` from `FragmentEntryConfigurationParser`.

### How should I update my code?

Use `getSegmentsEntryIds` in `FragmentEntryProcessorContext`, `DefaultFragmentEntryProcessorContext`, `FragmentRendererContext` and `DefaultFragmentRendererContext`. Use the method with signature `getContextObjects(JSONObject, String, long[])` from `FragmentEntryConfigurationParser`, where the third parameter represents the `long array` of `segmentsEntryIds`.

### Why was this change made?

This change was made so that a collection's variations can be retrieved when using collections in a fragment.

---------------------------------------

## Renamed Google Cloud autotranslation module

- **Date:** 2022-Jan-17
- **JIRA Ticket:** [LPS-145450](http://issues.liferay.com/browse/LPS-145450)

### What changed?

The `translation-google-cloud-translator` module and its packages were renamed. Although these packages are internal, the rename is not backwards compatible, as it changes the PID of the service configuration.

### Who is affected?

Any installation that has configured the Google Cloud autotranslator service.

### How should I update my code?

No code changes are necessary. Administrators may need to reconfigure the Google Could autotranslator service.

---------------------------------------

## Redirect URLs removed from Portal Properties
- **Date:** 2022-May-24
- **JIRA Ticket:** [LPS-128837](https://issues.liferay.com/browse/LPS-128837)

### What changed?

Redirect URL configuration is no longer configurable via portal properties. It can be configured in the UI, under [Redirect URLs](../../system-administration/configuring-liferay/virtual-instances/redirect-urls.md) in Instance Settings.

### Who is affected?

This affects anyone using portal properties to configure redirect URLs.

### How should I update my code?

Set your necessary configurations in the UI, under [Redirect URLs](../../system-administration/configuring-liferay/virtual-instances/redirect-urls.md) in Instance Settings.

### Why was this change made?

This change was made so that administrators can set separate redirect URL configurations for each Liferay instance.

---------------------------------------

## Portal Libs Cleanup

- **Date** 2022-May-26
- **JIRA Ticket** [LPS-142130](https://issues.liferay.com/browse/LPS-142130)

### Removed

- abdera.jar, axiom-api.jar and axiom-impl.jar - [LPS-142131](https://issues.liferay.com/browse/LPS-142131)
- xuggle-xuggler-noarch.jar - [LPS-143939](https://issues.liferay.com/browse/LPS-143939) Note, FFmpeg replaced Xuggler. See [Enabling FFmpeg for Audio and View Previews](../../content-authoring-and-management/documents-and-media/devops/enabling-ffmpeg-for-audio-and-video-previews.md) to learn more.
- bcmail.jar and bcprov.jar - [LPS-143945](https://issues.liferay.com/browse/LPS-143945)
- ant.jar - [LPS-143953](https://issues.liferay.com/browse/LPS-143953)
- aspectj-rt.jar and aspectj-weaver.jar - [LPS-143999](https://issues.liferay.com/browse/LPS-143999)
- jfreechart.jar and jcommon.jar - [LPS-144001](https://issues.liferay.com/browse/LPS-144001)
- boilerpipe.jar - [LPS-144005](https://issues.liferay.com/browse/LPS-144005)
- ecs.jar - [LPS-144081](https://issues.liferay.com/browse/LPS-144081)
- chardet.jar - [LPS-144084](https://issues.liferay.com/browse/LPS-144084)
- ical4j.jar - [LPS-144119](https://issues.liferay.com/browse/LPS-144119)
- jrcs-diff.jar - [LPS-144476](https://issues.liferay.com/browse/LPS-144476)
- curvesapi.jar - [LPS-144549](https://issues.liferay.com/browse/LPS-144549)
- concurrent.jar - [LPS-144640](https://issues.liferay.com/browse/LPS-144640)
- gif89.jar - [LPS-144861](https://issues.liferay.com/browse/LPS-144861)
- antlr2.jar and antlr3.jar - [LPS-144863](https://issues.liferay.com/browse/LPS-144863)
- bsf.jar - [LPS-145153](https://issues.liferay.com/browse/LPS-145153)
- commons-chain.jar - [LPS-145154](https://issues.liferay.com/browse/LPS-145154)
- freshcookies-security.jar - [LPS-145155](https://issues.liferay.com/browse/LPS-145155)
- htmlparser.jar - [LPS-145367](https://issues.liferay.com/browse/LPS-145367)
- jakarta-regexp.jar - [LPS-145500](https://issues.liferay.com/browse/LPS-145500)
- xmpcore.jar - [LPS-145541](https://issues.liferay.com/browse/LPS-145541)
- jcifs.jar - [LPS-145556](https://issues.liferay.com/browse/LPS-145556)
- juh.jar, jurt.jar, ridl.jar and unoil.jar - [LPS-145918](https://issues.liferay.com/browse/LPS-145918)
- xalan.jar - [LPS-145946](https://issues.liferay.com/browse/LPS-145946)
- wsdl4j.jar - [LPS-145991](https://issues.liferay.com/browse/LPS-145991)
- jsr107cache.jar - [LPS-146007](https://issues.liferay.com/browse/LPS-146007)
- xstream.jar - [LPS-146069](https://issues.liferay.com/browse/LPS-146069)
- liferay-icu.jar - [LPS-146089](https://issues.liferay.com/browse/LPS-146089)
- stringtemplate.jar - [LPS-146169](https://issues.liferay.com/browse/LPS-146169)
- rhino.jar - [LPS-146440](https://issues.liferay.com/browse/LPS-146440)
- odmg.jar - [LPS-146547](https://issues.liferay.com/browse/LPS-146547)
- closure-compiler.jar - [LPS-147006](https://issues.liferay.com/browse/LPS-147006)
- nekohtml.jar - [LPS-147042](https://issues.liferay.com/browse/LPS-147042)
- hessian.jar - [LPS-147424](https://issues.liferay.com/browse/LPS-147424)
- jericho-html.jar - [LPS-147656](https://issues.liferay.com/browse/LPS-147656)
- rmi-api.jar - [LPS-148863](https://issues.liferay.com/browse/LPS-148863)
- commons-beanutils.jar - [LPS-149082](https://issues.liferay.com/browse/LPS-149082)
- soap.jar - [LPS-149611](https://issues.liferay.com/browse/LPS-149611)
- serializer.jar - [LPS-150261](https://issues.liferay.com/browse/LPS-150261)
- jaxws-rt.jar - [LPS-150410](https://issues.liferay.com/browse/LPS-150410)
- xbean-spring.jar - [LPS-150448](https://issues.liferay.com/browse/LPS-150448)
- commons-math.jar - [LPS-150548](https://issues.liferay.com/browse/LPS-150548)
- streambuffer.jar, mimepull.jar, saaj-api.jar and saaj-impl.jar - [LPS-150781](https://issues.liferay.com/browse/LPS-150781)
- DBCP, c3p0 and Tomcat pool - [LPS-151028](https://issues.liferay.com/browse/LPS-151028)
- stax.jar - [LPS-151308](https://issues.liferay.com/browse/LPS-151308)

### Moved to Modules

- im4java.jar and monte-cc.jar - [LPS-144170](https://issues.liferay.com/browse/LPS-144170)
- java-diff.jar, daisydiff.jar and eclipse-core-runtime.jar - [LPS-144201](https://issues.liferay.com/browse/LPS-144201)
- urlrewritefilter.jar - [LPS-145186](https://issues.liferay.com/browse/LPS-145186)
- jai_core.jar and jai-codec.jar - [LPS-145778](https://issues.liferay.com/browse/LPS-145778)
- ccpp.jar, ccpp-ri.jar, jena.jar, oro.jar and reffilter.jar - [LPS-145917](https://issues.liferay.com/browse/LPS-145917)
- netty-buffer.jar, netty-codec.jar, netty-common.jar, netty-handler.jar, netty-resolver.jar and netty-transport.jar - [LPS-146451](https://issues.liferay.com/browse/LPS-146451)
- jazzy.jar - [LPS-146894](https://issues.liferay.com/browse/LPS-146894)
- commons-discovery.jar - [LPS-147205](https://issues.liferay.com/browse/LPS-147205)
- scribe.jar - [LPS-147542](https://issues.liferay.com/browse/LPS-147542)
- tika-core.jar, tika-parsers.jar, vorbis-java-core.jar and vorbis-java-tika.jar - [LPS-147938](https://issues.liferay.com/browse/LPS-147938)
- commons-lang3.jar - [LPS-148100](https://issues.liferay.com/browse/LPS-148100)
- commons-digester.jar and commons-validator.jar - [LPS-148191](https://issues.liferay.com/browse/LPS-148191)
- jmatio.jar - [LPS-148218](https://issues.liferay.com/browse/LPS-148218)
- mime4j.jar - [LPS-148287](https://issues.liferay.com/browse/LPS-148287)
- poi.jar - [LPS-148302](https://issues.liferay.com/browse/LPS-148302)
- metadata-extractor.jar and xmpcore.jar - [LPS-148460](https://issues.liferay.com/browse/LPS-148460)
- commons-compress.jar - [LPS-148461](https://issues.liferay.com/browse/LPS-148461)
- tagsoup.jar - [LPS-148497](https://issues.liferay.com/browse/LPS-148497)
- java-libpstjar - [LPS-148577](https://issues.liferay.com/browse/LPS-148577)
- mp4parser.jar - [LPS-148582](https://issues.liferay.com/browse/LPS-148582)
- juniversalchardet.jar - [LPS-148666](https://issues.liferay.com/browse/LPS-148666)
- jhighlight.jar - [LPS-148670](https://issues.liferay.com/browse/LPS-148670)
- jna.jar - [LPS-148671](https://issues.liferay.com/browse/LPS-148671)
- sparse-bit-set.jar - [LPS-148757](https://issues.liferay.com/browse/LPS-148757)
- netcdf.jar - [LPS-148925](https://issues.liferay.com/browse/LPS-148925)
- jaxb-runtime.jar and istack-commons-runtime.jar - [LPS-148926](https://issues.liferay.com/browse/LPS-148926)
- commons-exec.jar - [LPS-149097](https://issues.liferay.com/browse/LPS-149097)
- commons-collections4.jar - [LPS-149099](https://issues.liferay.com/browse/LPS-149099)
- commons-math3.jar - [LPS-149151](https://issues.liferay.com/browse/LPS-149151)
- pdfbox.jar - [LPS-149426](https://issues.liferay.com/browse/LPS-149426)
- rometools.jar - [LPS-150149](https://issues.liferay.com/browse/LPS-150149)
- passwordencryptor.jar - [LPS-150150](https://issues.liferay.com/browse/LPS-150150)
- jdom2.jar - [LPS-150423](https://issues.liferay.com/browse/LPS-150423)
- xbean.jar - [LPS-150447](https://issues.liferay.com/browse/LPS-150447)
- asm.jar - [LPS-151419](https://issues.liferay.com/browse/LPS-151419)

### Why was this change made ?

This change was made to address vulnerabilities in outdated libraries that are no longer maintained.

---------------------------------------

## Elasticsearch Sortable Type Mappings Were Changed from keyword to icu_collation_keyword

- **Date:** 2022-May-12
- **JIRA Ticket:** [LPS-152937](https://issues.liferay.com/browse/LPS-152937)

### What changed?

The Elasticsearch type mapping of localized sortable `*_<languageId>_sortable` and nested `ddmFieldArray.ddmFieldValueText_<languageId>_String_sortable` fields were changed from `keyword` to `icu_collation_keyword`.

These fields' indexed information is now stored in an encoded format. For example, the `entity title` text is now stored as `MkRQOlBaBFA6UEAyARABEAA=`.

This new `icu_collation_keyword` type allows sorting using the correct collation rules of each language. For more information see <https://www.elastic.co/guide/en/elasticsearch/plugins/7.17/analysis-icu-collation-keyword-field.html>.

When updating an existing Liferay installation, perform a full reindex to create the new mappings.

### Who is affected?

If you use the `*_<languageId>_sortable` and `ddmFieldArray.ddmFieldValueText_<languageId>_String_sortable` fields,

- Results are now sorted using each language's correct collation rules

- Indexed information is now encoded when returned

### How should I update my code?

If you want to maintain the old sort behavior, you must customize the Elasticsearch mappings, removing the `icu_collation_keyword`. For more information about how to configure mappings, see https://learn.liferay.com/dxp/latest/en/using-search/installing-and-upgrading-a-search-engine/elasticsearch/advanced-configuration-of-the-liferay-elasticsearch-connector.html

You can retrieve data from these fields from Elasticsearch's `_source` field (<https://www.elastic.co/guide/en/elasticsearch/reference/7.17/mapping-source-field.html>). Alternatively, remove the `icu_collation_keyword` as explained above.

---------------------------------------

## Upgraded MySQL Connector to 8.0.29 and Forced to Use Protocol TLSv1.2 for MySQL

- **Date:** 2022-Jul-20
- **JIRA Ticket:** [LPS-157036](https://issues.liferay.com/browse/LPS-157036), [LPS-157039](https://issues.liferay.com/browse/LPS-157039)

### What changed?

TLS 1.2 requires updating MySQL to 5.7.28 with MySQL Connector 8.0.29 or above.

### Who is affected?

Anyone using a MySQL version lower than 5.7.28, especially if they use the auto-downloaded MySQL connector on DXP U37 or higher.

### How should I update my code?

Upgrade MySQL to version 5.7.28 or higher, or set the protocol to TLSv1.2 manually (See https://dev.mysql.com/doc/refman/5.7/en/encrypted-connection-protocols-ciphers.html#encrypted-connection-supported-protocols).

### Why was this change made?

This change was made to address security vulnerabilities.

---------------------------------------

## Removed UtilLocator

- **Date:** 2022-Nov-2
- **JIRA Ticket:** [LPS-165363](https://issues.liferay.com/browse/LPS-165363)

### What changed?

`UtilLocator` is removed. Templates can no longer use `utilLocator.findUtil()` to access `*Util` classes.

### Who is affected?

Anyone currently using `utilLocator` within templates to access `*Util` classes.

### How should I update my code?

There is no replacement for this removal.

The workaround is to set the environment variable `LIFERAY_TEMPLATE_PERIOD_ENGINE_PERIOD_SERVICE_PERIOD_LOCATOR_PERIOD_RESTRICT` to false and access the desired util class using `ServiceLocator` instead. However, `ServiceLocator` opens unrestricted access to all published OSGi services within templates. Proceed with caution.

### Why was this change made?

This change was made to address a security vulnerability.

`UtilLocator` currently gives unrestricted access to all `*Util` classes in templates, exposing these classes for misuse or a malicious attack. Commonly used `Util` classes such as `StringUtil` are already included in the template context and ready to be used.

---------------------------------------

## Removed ConfigurationBeanDeclaration

- **Date:** 2022-Nov-11
- **JIRA Ticket:** [LPS-162830](https://issues.liferay.com/browse/LPS-162830)

### What changed?

Because `ConfigurationProvider` is no longer required to retrieve an annotated `*Configuration.java` interface, `ConfigurationBeanDeclaration` is removed.

### Who is affected?

Anyone who has implemented `ConfigurationBeanDeclaration` or references the class in their code.

### How should I update my code?

Delete implementations of `ConfigurationBeanDeclaration` and remove references to the class.

### Why was this change made?

`ConfigurationBeanDeclaration` was used to register configuration classes with the `ConfigurationProvider` framework, to be tracked by OSGi managed services. Liferay now analyzes a bundle's metatype information and registers configuration classes automatically at container registration. Now developers don't have to register configuration classes manually, because it happens in the background.

---------------------------------------

## Removed FieldSetGroupTag

- **Date:** 2022-Nov-22
- **JIRA Ticket:** [LPS-168309](https://issues.liferay.com/browse/LPS-168309)

### What changed?

`FieldSetGroupTag` was removed because `<aui:fieldset-group>` and `<liferay-frontend:fieldset-group>` are no longer needed.

### Who is affected?

Anyone using `<aui:fieldset-group>` or `<liferay-frontend:fieldset-group>`.

### How should I update my code?

Delete usages of `<liferay-frontend:fieldset-group>` and replace usages of `<aui:fieldset-group>` with `<div class="sheet"><div class="panel-group panel-group-flush">`.

### Why was this change made?

The tags `<aui:fieldset-group>` and `<liferay-frontend:fieldset-group>` added unnecessary markup to the page and caused accessibility issues.

---------------------------------------

## Removed ContainerTag

- **Date:** 2022-Dec-22
- **JIRA Ticket:** [LPS-166546](https://issues.liferay.com/browse/LPS-166546)

### What changed?

`ContainerTag` was removed, and with it the `<aui:container>` tag. The tag was also removed from the [AUI tld](https://learn.liferay.com/reference/latest/en/dxp/taglibs/util-taglib/aui/tld-summary.html).

### Who is affected?

Anyone using `<aui:container>`.

### How should I update my code?

Replace usages of `<aui:container>` with `<clay:container>`.

### Why was this change made?

The tag `<aui:container>` was deprecated in a previous version.

---------------------------------------

## Removed Properties for Tika Configuration and Text Extraction

- **Date:** 2022-Dec-13
- **JIRA Ticket:** [LPS-147938](https://issues.liferay.com/browse/LPS-147938) and [LPS-169760](https://issues.liferay.com/browse/LPS-169760)

### What changed?

The Tika library was extracted from Liferay's core to the `com.liferay.portal.tika` module, changing the way it must be configured. The system property `tika.config` was removed, as were the portal properties `text.extraction.fork.process.enabled` and `text.extraction.fork.process.mime.types`. The Tika library and text extraction are no longer configurable through properties files.

As part of the extraction to a module, the removed properties are added to the module's configuration interface and are configurable using System Settings or a `.config` configuration file.

### Who is affected?

This affects anyone using the removed system or portal properties.

### How should I update my code?

No code changes are necessary.

Configure the same properties in System Settings &rarr; Infrastructure &rarr; Tika Configuration.

### Why was this change made?

These configuration changes were made because the Tika library was extracted to the `com.liferay.portal.tika` module.

---------------------------------------

## Moved CTSQLModeThreadLocal to portal-kernel and Changed Package

- **Date:** 2023-Apr-11
- **JIRA Ticket:** [LPS-181233](https://issues.liferay.com/browse/LPS-181233)

### What changed?

The `CTSQLModeThreadLocal` class was moved from the `portal-impl` module into the `portal-kernel` module. Consequently, its package was changed from `com.liferay.portal.change.tracking.sql` to `com.liferay.portal.kernel.change.tracking.sql` to be consistent with the `portal-kernel` module's package naming scheme for the change tracking classes.

### Who is affected?

This affects anyone calling the `CTSQLModeThreadLocal` class from their code.

### How should I update my code?

1. Declare a dependency on the `portal-kernel` module.

1. Modify `import` statements for the `CTSQLModeThreadLocal` class to use the new package:

	```
	import com.liferay.portal.kernel.change.tracking.sql.CTSQLModeThreadLocal;
	```

### Why was this change made?

To resolve [LPS-181233](https://issues.liferay.com/browse/LPS-181233), the value of the `CTSQLModeThreadLocal` must be set from the `portal-kernel` module. Moving the class into the `portal-kernel` module allows it to be referenced as required.

---------------------------------------

## Removed Log4j1 Compatibility

- **Date:** 2023-May-9
- **JIRA Ticket:** [LPS-181002](https://issues.liferay.com/browse/LPS-181002)

### What changed?

Support for Log4j1 XML configuration syntax is removed.

### Who is affected?

This affects any code using Log4j1 configuration files.

### How should I update my code?

[Convert](https://logging.apache.org/log4j/2.x/manual/migration.html#Log4j2ConfigurationFormat) Log4j1 configuration files to use Log4j2 XML syntax.

### Why was this change made?

Liferay's source code has been using Log4j2 for some time, and Log4j1 reached [end of life in 2015](https://news.apache.org/foundation/entry/apache_logging_services_project_announces). After this change, all log4j configuration files must use log4j2 syntax.

---------------------------------------

## Removed the `verifyDB` function from Server Administration and its services

- **Date:** 2023-May-10
- **JIRA Ticket:** [LPS-184192](https://issues.liferay.com/browse/LPS-184192)

### What changed?

The `verifyDB()` method was removed from `ServiceComponentLocalService`. The corresponding _Verify database tables of all plugins_ functionality was removed from the Server Administration console's Verification Actions.

### Who is affected?

This affects anyone calling the `ServiceComponentLocalService.verifyDB()` method from their code or using the Server Administration functionality.

### How should I update my code?

Remove all usages of `ServiceComponentLocalService.verifyDB()`.

### Why was this change made?

The upgrade framework manages all modules' tables and `Release` record creation. The `verifyDB` method is non-functional.

---------------------------------------

## Removed 7.1 methods in PortletSharedSearchSettings from portal-search-web-api module

- **Date:** 2023-May-10
- **JIRA Ticket:** [LPS-183921](https://issues.liferay.com/browse/LPS-183921)

### What changed?

`PortalSharedSearchSettings` methods related to 7.1 compatibility were removed.

### Who is affected?

This affects anyone calling these methods: `getParameter71()`, `getParameterValues71()`, and `getPortletPreferences71()`.

### How should I update my code?

Replace `getParameter71()` with `getParameterOptional()`, `getParameterValues71()` with `getParameterValues()`, and `getPortletPreferences71()` with `getPortletPreferencesOptional()`.

### Why was this change made?

These methods were added in 7.2 for forward compatibility: see [LPS-101007](https://issues.liferay.com/browse/LPS-101007). In 7.4 they are redundant to the `Optional` and `String[]` variations.

---------------------------------------

## Removed S3FileCache
- **Date:** 2023-June-1
- **JIRA Ticket:** [LPS-176640](https://issues.liferay.com/browse/LPS-176640)

### What changed?

`S3FileCache` was removed. In addition, `cacheDirCleanUpExpunge` and `cacheDirCleanUpFrequency` were removed from `com.liferay.portal.store.s3.configuration.S3StoreConfiguration`.

### Who is affected?

This affects anyone using the S3 file store. When downloading files from S3, the data is directly forwarded to the client, and no longer cached on the Liferay server.

### How should I update my code?

No code changes are necessary.

If using a `com.liferay.portal.store.s3.configuration.S3StoreConfiguration.config` file to configure S3 in Liferay, remove the properties `cacheDirCleanUpExpunge` and `cacheDirCleanUpFrequency`.

### Why was this change made?

`S3FileCache` has various design flaws, and no other cloud-based store implementation in Liferay provides caching.

---------------------------------------

## Removed unsupported scripting language types from file liferay-workflow-definition_7_4_0.xsd
- **Date:** 2023-June-14
- **JIRA Ticket:** [LPS-187594](https://issues.liferay.com/browse/LPS-187594)

### What changed?

These scripting languages are removed: `beanshell`, `javascript`, `python` and `ruby`. Workflow XML files cannot contain these scripting language types.

### Who is affected?

This affects anyone with workflow definitions containing `beanshell`, `javascript`, `python` or `ruby` scripting language types.

### How should I update my code?

Use `drl`, `groovy` or `java` as the scripting language type, and rewrite the script logic in your workflow definition XML files.

### Why was this change made?

Liferay no longer supports these scripting language types.

---------------------------------------

## Removed ScriptingExecutorExtender and ScriptBundleProvider

- **Date:** 2023-June-19
- **JIRA Ticket:** [LPS-169777](https://issues.liferay.com/browse/LPS-169777)

### What changed?

The `ScriptingExecutorExtender` class and the `ScriptBundleProvider` interface were removed.

### Who is affected?

This affects anyone implementing `ScriptBundleProvider` with script files in this path in the same module: `/META-INF/resources/scripts/`.

### How should I update my code?

Delete all implementations of `ScriptBundleProvider`, remove script files from `/META-INF/resources/scripts/`, and rewrite the logic in the script files using an immediate component.

### Why was this change made?

This change was made to address security vulnerabilities.

---------------------------------------

## Removed IndexStatusManagerInternalConfiguration
- **Date:** 2023-June-21
- **JIRA Ticket:** [LPS-185105](https://issues.liferay.com/browse/LPS-185105)

### What changed?

`IndexStatusManagerInternalConfiguration` was removed.

### Who is affected?

This affects anyone using this configuration.

### How should I update my code?

This configuration has no replacement, as it was meant for testing purposes only. Setting the index to read only should be accomplished using `IndexStatusManagerConfiguration`.

### Why was this change made?

The configuration was no longer required for internal testing, and the functionality is still available in `IndexStatusManagerConfiguration`.

---------------------------------------

## Removed support for custom SoapDescriptorBuilder
- **Date:** 2023-June-30
- **JIRA Ticket:** [LPS-173756](https://liferay.atlassian.net/browse/LPS-173756)

### What changed?

Implementing a custom `SoapDescriptorBuilder` is no longer supported.

### Who is affected?

This affects anyone implementing the interface `SoapDescriptorBuilder`.

### How should I update my code?

This removed extension point has no direct replacement.

### Why was this change made?

`SOAP` was deprecated in 7.3, and Liferay decided to not support this extension point.

---------------------------------------

## Removed `AMImageConfigurationProvider` and the `AMImageConfiguration` property `imageMaxSize`
- **Date:** 2023-June-29
- **JIRA Ticket:** [LPS-185768](https://issues.liferay.com/browse/LPS-185768)

### What changed?

The `AMImageConfiguration` property `imageMaxSize` and its provider `AMImageConfigurationProvider` were removed.

### Who is affected?

This affects anyone using this configuration.

### How should I update my code?

Replace the configuration with the `DLFileEntryConfiguration` property called `previewableProcessorMaxSize`.

### Why was this change made?

The `AMImageConfiguration` property `imageMaxSize` has been deprecated since 7.2.x in favor of using `DLFileEntryConfiguration.previewableProcessorMaxSize()`.

---------------------------------------

## Changed default value of `virtual.hosts.valid.hosts` portal property

- **Date:** 2023-June-2
- **JIRA Ticket:** [LPE-17758](https://liferay.atlassian.net/browse/LPE-17758)

### What changed?

The default value of the `virtual.hosts.valid.hosts` changed from `*` to `localhost,127.0.0.1,[::1],[0:0:0:0:0:0:0:1]` in `portal.properties`.

### Who is affected?

Anyone setting `virtual.hosts.valid.hosts` besides `localhost`, `127.0.0.1`, `[::1]`, or `[0:0:0:0:0:0:0:1]`.

### How should I update my code?

Upgrade the default value of `virtual.hosts.valid.hosts` in `portal-impl/src/portal.properties` to match the value used in your current configuration.

### Why was this change made?

This change was made to address security vulnerabilities.

---------------------------------------

## Removed extension points in `SolrClientManager`
- **Date:** 2023-July-4
- **JIRA Ticket:** [LPS-180691](https://liferay.atlassian.net/browse/LPS-180691)

### What changed?

Extension points in `SolrClientManager` were removed. For the `SolrClientFactory`, extension points for the types `CLOUD` and `REPLICATED` were removed. For `HttpClientFactory`, extension points for the types `BASIC` and `CERT` were removed.

### Who is affected?

This affects anyone overriding the `SolrClientFactory` and `HttpClientFactory` with the types Liferay provided.

### How should I update my code?

This removed extension point has no direct replacement.

### Why was this change made?

Liferay decided to not support these extension points.

---------------------------------------

## Deprecated methods from the portal-kernel interface `com.liferay.portal.kernel.search.Document`
- **Date:** 2023-July-7
- **JIRA Ticket:** [LPS-188914](https://liferay.atlassian.net/browse/LPS-188914)

### What changed?

These API methods in `com.liferay.portal.kernel.search.Document` and their implementations in `com.liferay.portal.kernel.search.DocumentImpl` are deprecated:

- `addFile(String name, byte[] bytes, String fileExt)`
- `addFile(String name, File file, String fileExt)`
- `addFile(String name, InputStream inputStream, String fileExt)`
- `addFile(String name, InputStream inputStream, String fileExt,int maxStringLength)`

### Who is affected?

This affects anyone using these API methods.

### How should I update my code?

Instead of using `addFile(String name, byte[] bytes, String fileExt)`,

1. Get an `InputStream` from the `bytes`.

1. Call `com.liferay.portal.kernel.util.TextExtractor.extractText(InputStream inputStream, int maxStringLength)` with the `inputStream` and `-1` and store its return value.

1. Call `com.liferay.portal.kernel.search.Document.addText(String name, String value)` with `name` and the previous return value.

Instead of using `addFile(String name, File file, String fileExt)`,

1. Get an InputStream from the `file`.

1. Call `com.liferay.portal.kernel.util.TextExtractor.extractText(InputStream inputStream, int maxStringLength)` with the `inputStream` and `-1` and store its return value.

1. Call `com.liferay.portal.kernel.search.Document.addText(String name, String value)` with `name` and the previous return value.

Instead of using `addFile(String name, InputStream inputStream, String fileExt)`,

1. Call `com.liferay.portal.kernel.util.TextExtractor.extractText(InputStream inputStream, int maxStringLength)` with `inputStream` and `-1` and store its return value.

1. Call `com.liferay.portal.kernel.search.Document.addText(String name, String value)` with `name` and the previous return value.

Instead of using `addFile(String name, InputStream inputStream, String fileExt,int maxStringLength)`,

1. Call `com.liferay.portal.kernel.util.TextExtractor.extractText(InputStream inputStream, int maxStringLength)` with `inputStream` and `maxStringLength` and store its return value.

1. Call `com.liferay.portal.kernel.search.Document.addText(String name, String value)` with `name` and the previous return value.

### Why was this change made?

These methods are no longer called by Liferay internally.

---------------------------------------

## Remove interface `com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker` under portal-kernel
- **Date:** 2023-August-11
- **JIRA Ticket:** [LPS-182671](https://liferay.atlassian.net/browse/LPS-182671)

### What changed?

The interface `com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker` and related support logic was removed.

### Who is affected?

This affects anyone implementing this interface class.

### How should I update my code?

Implement `com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission` instead.

### Why was this change made?

The interface `com.liferay.portal.kernel.security.permission.BaseModelPermissionChecker` was deprecated in 7.1 and is no longer used by Liferay internally.

---------------------------------------

## Removed destination `liferay/hot_deploy`
- **Date:** 2023-August-4
- **JIRA Ticket:** [LPS-192680](https://liferay.atlassian.net/browse/LPS-192680)

### What changed?

The Message bus destination `liferay/hot_deploy` and test rule `DestinationAwaitClassTestRule` are removed.

### Who is affected?

Anyone who is listening to hot deploy events by registering a `com.liferay.portal.kernel.messaging.MessageListener`, and anyone who is using a custom instance of `DestinationAwaitClassTestRule`.

### How should I update my code?

Register a `HotDeployListener` to listen to hot deploy events. Manually implement the logic to sync with any destination.

### Why was this change made?

This destination is no longer used in Liferay.

---------------------------------------

## Removed the `unschedule` API from the scheduler engine platform
- **Date:** 2023-August-24
- **JIRA Ticket:** [LPS-194314](https://liferay.atlassian.net/browse/LPS-194314)

### What changed?

The `unschedule` method was removed from the classes `com.liferay.portal.kernel.scheduler.SchedulerEngine`, `com.liferay.portal.kernel.scheduler.SchedulerEngineHelper` and `com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil`.

### Who is affected?

Anyone who is using the removed `unschedule` API method.

### How should I update my code?

Use the `delete` method from the same class.

### Why was this change made?

The `unschedule` method is not needed. Liferay always adds a new job and trigger together. Unscheduled jobs are no longer needed and should be deleted.

---------------------------------------

## Removal of `com.liferay.document.library.kernel.util.DLProcessor` registration support from `com.liferay.portal.deploy.hot.HookHotDeployListener`
- **Date:** 2023-August-17
- **JIRA Ticket:** [LPS-193926](https://liferay.atlassian.net/browse/LPS-193926)

### What changed?

Support for deploying a `com.liferay.document.library.kernel.util.DLProcessor` via hook was removed from `com.liferay.portal.deploy.hot.HookHotDeployListener`.

### Who is affected?

This affects anyone providing a `DLProcessor` implementation via hook.

### How should I update my code?

If you are providing your own `DLProcessor` implementation via hook, convert it to an OSGi service.

### Why was this change made?

This change was made to remove duplicated `DLProcessor` registration logic between `DLProcessorRegistryImpl` and `HookHotDeployListener`.

---------------------------------------

## Removed support for `DestinationEventListener` and `MessageBusEventListener`
- **Date:** 2023-Sep-1
- **JIRA Ticket:** [LPS-195116](https://liferay.atlassian.net/browse/LPS-195116)

### What changed?

The interfaces `DestinationEventListener` and `MessageBusEventListener` are removed, along with the support to register a listener for `MessageListener` and `Destination` registration and unregistration events.

### Who is affected?

This affects anyone registering such listeners to listen to these events.

### How should I update my code?

The removal of this extension point has no direct replacement.

### Why was this change made?

These listeners are not used in Liferay. Liferay decided to not support these extension points.

---------------------------------------

## Removed API to register/unregister `MessageListener` from `Destination`
- **Date:** 2023-Sep-1
- **JIRA Ticket:** [LPS-194337](https://liferay.atlassian.net/browse/LPS-194337)

### What changed?

The following API methods related to `MessageListener` registration have been removed from the `Destination` interface:
- `copyMessageListeners`
- `getMessageListenerCount`
- `isRegistered`
- `register`
- `unregister`

A new interface, `MessageListenerRegistry`, was added with an API to get message listeners associated with the provided destination name.

### Who is affected?

This affects anyone registering/unregistering such listeners directly on the `Destination` interface.

### How should I update my code?

Register a `MessageListener` as an OSGi service, with the property `destination.name` mapped to the corresponding destination name.

### Why was this change made?

This change simplifies the message bus infrastructure and usage.

---------------------------------------

## Moved portal property `discussion.subscribe` to instance settings
- **Date:** 2023-September-4
- **JIRA Ticket:** [LPS-194379](https://liferay.atlassian.net/browse/LPS-194379)

### What changed?

The portal property `discussion.subscribe` was moved to instance settings and removed from the `portal.properties` file.

### Who is affected?

This affects anyone using `discussion.subscribe` with a value different than the default.

### How should I update my code?

No code updates are required. Set the property in the instance settings UI.

### Why was this change made?

Portal properties are global and require a server restart, while instance settings can differ between instances and are made in the running portal. Moving to instance settings provides more flexibility in the system.

---------------------------------------

## Removed repository registration support from `com.liferay.portal.deploy.hot.HookHotDeployListener`
- **Date:** 2023-September-4
- **JIRA Ticket:** [LPS-194350](https://liferay.atlassian.net/browse/LPS-194350)

### What changed?

The support for deploying an external repository via hook was removed from the `com.liferay.portal.deploy.hot.HookHotDeployListener` class.

### Who is affected?

This affects anyone providing a repository implementation via hook.

### How should I update my code?

If you are providing your own repository implementation hook, convert it to an OSGi service.

### Why was this change made?

External repositories deployed via hook don't support the same feature set as those implemented as OSGi serivces.

---------------------------------------

## Moved portal property `discussion.comments.always.editable.by.owner` to instance settings
- **Date:** 2023-September-13
- **JIRA Ticket:** [LPS-195006](https://liferay.atlassian.net/browse/LPS-195006)

### What changed?

The portal property `discussion.comments.always.editable.by.owner` was moved to instance settings and removed from the `portal.properties` file.

### Who is affected?

This affects anyone using `discussion.comments.always.editable.by.owner` with a value different than the default.

### How should I update my code?

No code updates are required. Set the property in the instance settings UI.

### Why was this change made?

Portal properties are global and require a server restart, while instance settings can differ between instances and are made in the running portal. Moving to instance settings provides more flexibility in the system.

--------------------------------------

## Removed portal property `sql.data.max.parameters`
- **Date:** 2023-October-2
- **JIRA Ticket:** [LPS-189621](https://liferay.atlassian.net/browse/LPS-189621)

### What changed?

The property `sql.data.max.parameters` was removed from `portal.properties`.

### Who is affected?

This affects anyone using the `sql.data.max.parameters` property.

### How should I update my code?

Use the database-specific property `database.max.parameters[databse]` instead.

### Why was this change made?

The total parameters limit varies for each database. The new portal property allows for database-specific configuration.

---------------------------------------

## Removed support to disable a scheduler job by setting interval to 0 or cron expression to empty string
- **Date:** 2023-Sep-11
- **JIRA Ticket:** [LPS-190994](https://liferay.atlassian.net/browse/LPS-190994)

### What changed?

Setting an interval of `0` or an empty cron expression are no longer accepted by the scheduler framework. When these values are used, an error message will be displayed.

### Who is affected?

This affects anyone using the interval `0` or empty cron expression.

### How should I update my code?

Instead use the Component Blacklist to disable certain scheduler components. The required class names are shown in the error messages thrown when setting the interval to `0` or an empty cron expression.

### Why was this change made?

Now Scheduler jobs are bootstrapped by `SchedulerJobConfiguration` objects registered as OSGi services. If a job should not be bootstrapped, the configuration object must not be registered at all.

---------------------------------------

## Removed RemotePreference API
- **Date:** 2024-Mar-14
- **JIRA Ticket:** [LPD-20659](https://liferay.atlassian.net/browse/LPD-20659)

### What changed?

com.liferay.portal.kernel.util.RemotePreference API is removed.

### Who is affected?

This affects anyone using the User.getRemotePreference(String) and User.getRemotePreferences().

### How should I update my code?

User.getRemotePreference(String) and User.getRemotePreferences() were just a convenient shortcut to get the user's current request's cookies with "REMOTE_PREFERENCE_" name prefix. The same logic can be done by directly getting necessary cookies from request.

### Why was this change made?

RemotePreference API has never been used. But the api supporting logic has to collect and hold cookies in User object, causing unnecessary CPU and memory overhead.