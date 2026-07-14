<#ftl strip_whitespace=true>

<#assign
	css_main_file = ""
	is_signed_in = false
	js_main_file = ""
	is_setup_complete = false
/>

<#if user??>
	<#assign is_setup_complete = user.isSetupComplete() />
</#if>

<#if themeDisplay??>
	<#assign
		css_main_file = htmlUtil.escape(themeDisplay.getMainCSSURL())
		is_signed_in = themeDisplay.isSignedIn()
		js_main_file = htmlUtil.escape(themeDisplay.getMainJSURL())
	/>

	<#if !is_setup_complete>
		<#assign is_setup_complete = themeDisplay.isImpersonated() />
	</#if>
</#if>

<#function max x y>
	<#if x < y>
		<#return y>
	<#else>
		<#return x>
	</#if>
</#function>

<#function min x y>
	<#if x <= y>
		<#return x>
	<#else>
		<#return y>
	</#if>
</#function>

<#macro breadcrumbs
	default_preferences = ""
>
	<@liferay_portlet["runtime"]
		defaultPreferences=default_preferences
		portletProviderAction=portletProviderAction.VIEW
		portletProviderClassName="com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry"
	/>
</#macro>

<#macro control_menu>
	<#if is_setup_complete && is_signed_in>
		<@liferay_product_navigation["control-menu"] />
	</#if>
</#macro>

<#macro css
	file_name
>
	<#if file_name == css_main_file>
		<link class="lfr-css-file" href="${htmlUtil.escape(portalUtil.getStaticResourceURL(request, file_name))}" id="mainLiferayThemeCSS" ${nonceAttribute} rel="stylesheet" type="text/css" />
	<#else>
		<link class="lfr-css-file" href="${htmlUtil.escape(portalUtil.getStaticResourceURL(request, file_name))}" ${nonceAttribute} rel="stylesheet" type="text/css" />
	</#if>
</#macro>

<#macro date
	format
>
${dateUtil.getCurrentDate(format, locale)}</#macro>

<#macro js
	file_name
>
	<#if file_name == js_main_file>
		<script ${nonceAttribute} id="mainLiferayThemeJavaScript" src="${htmlUtil.escape(portalUtil.getStaticResourceURL(request, file_name))}" type="text/javascript"></script>
	<#else>
		<script ${nonceAttribute} src="${htmlUtil.escape(portalUtil.getStaticResourceURL(request, file_name))}" type="text/javascript"></script>
	</#if>
</#macro>

<#macro language
	key
>
${languageUtil.get(locale, key)}</#macro>

<#macro language_format
	arguments
	key
>
${languageUtil.format(locale, key, arguments)}</#macro>

<#macro languages
	default_preferences = ""
>
	<@liferay_portlet["runtime"]
		defaultPreferences=default_preferences
		portletProviderAction=portletProviderAction.VIEW
		portletProviderClassName="com.liferay.portal.kernel.servlet.taglib.ui.LanguageEntry"
	/>
</#macro>

<#macro navigation_menu
	default_preferences = ""
	instance_id = ""
>
	<@liferay_portlet["runtime"]
		defaultPreferences=default_preferences
		instanceId=instance_id
		portletProviderAction=portletProviderAction.VIEW
		portletProviderClassName="com.liferay.portal.kernel.theme.NavItem"
	/>
</#macro>

<#macro search
	default_preferences = ""
>
	<#if is_setup_complete>
		<@liferay_portlet["runtime"]
			defaultPreferences=default_preferences
			portletProviderAction=portletProviderAction.VIEW
			portletProviderClassName="com.liferay.admin.kernel.util.PortalSearchApplicationType$Search"
		/>
	</#if>
</#macro>

<#macro search_bar
	default_preferences = ""
>
	<#if is_setup_complete>
		<@liferay_portlet["runtime"]
			defaultPreferences=default_preferences
			instanceId="templateSearch"
			portletProviderAction=portletProviderAction.VIEW
			portletProviderClassName="com_liferay_portal_search_web_search_bar_portlet_SearchBarPortlet"
		/>
	</#if>
</#macro>

<#macro silently
	foo
>
	<#assign foo = foo />
</#macro>

<#macro user_personal_bar>
	<#if is_setup_complete || !is_signed_in>
		<@liferay_portlet["runtime"]
			portletProviderAction=portletProviderAction.VIEW
			portletProviderClassName="com.liferay.admin.kernel.util.PortalUserPersonalBarApplicationType$UserPersonalBar"
		/>
	</#if>
</#macro>