<#-- ---------- Common variables ---------- -->

<#assign
	theme_display = themeDisplay
	portlet_display = portletDisplay

	layoutSet = layout.getLayoutSet()

	theme_timestamp = themeDisplay.getTheme().getTimestamp()
	theme_settings = themeDisplay.getThemeSettings()

	root_css_class = languageUtil.get(locale, "lang.dir")
	css_class = htmlUtil.escape(bodyCssClass!)

	css_class = css_class + " " + htmlUtil.escape(theme_display.getColorScheme().getCssClass()) + " yui3-skin-sam"

	page_group = layout.getGroup()
/>

<#if layoutTypePortlet.hasStateMax()>
	<#assign css_class = css_class + " page-maximized" />
</#if>

<#assign
	css_folder = theme_display.getPathThemeCss()
	images_folder = theme_display.getPathThemeImages()
	javascript_folder = theme_display.getPathThemeJavaScript()
	templates_folder = theme_display.getPathThemeTemplates()

	full_css_path = fullCssPath
	full_templates_path = fullTemplatesPath

	css_main_file = htmlUtil.escape(portalUtil.getStaticResourceURL(request, "${css_folder}/main.css"))
	js_main_file = htmlUtil.escape(portalUtil.getStaticResourceURL(request, "${javascript_folder}/main.js"))

	company_id = company.getCompanyId()
	company_name = htmlUtil.escape(company.getName())
	company_logo = htmlUtil.escape(theme_display.getCompanyLogo())
	company_logo_height = theme_display.getCompanyLogoHeight()
	company_logo_width = theme_display.getCompanyLogoWidth()
	company_url = theme_display.getURLHome()

	show_control_menu = theme_display.isShowControlMenu()
/>

<#if !request.isRequestedSessionIdFromCookie()>
	<#assign company_url = portalUtil.getURLWithSessionId(company_url, request.getSession().getId()) />
</#if>

<#assign
	time_zone = user.getTimeZoneId()
	is_login_redirect_required = portalUtil.isLoginRedirectRequired(request)
	is_signed_in = theme_display.isSignedIn()
	group_id = theme_display.getScopeGroupId()
/>

<#-- ---------- LPS-66428 ---------- -->

<#if !user_initialized??>
	<#assign
		is_default_user = user.isDefaultUser()
		is_female = user.isFemale()
		is_guest_user = user.isGuestUser()
		is_male = user.isMale()
		is_setup_complete = user.isSetupComplete()
		language = locale.getLanguage()
		language_id = user.getLanguageId()
		user_birthday = user.getBirthday()
		user_comments = user.getComments()
		user_email_address = user.getEmailAddress()
		user_first_name = user.getFirstName()
		user_greeting = htmlUtil.escape(user.getGreeting())
		user_id = user.getUserId()
		user_last_login_ip = user.getLastLoginIP()
		user_last_name = user.getLastName()
		user_login_ip = user.getLoginIP()
		user_middle_name = user.getMiddleName()
		user_name = user.getFullName()
		w3c_language_id = localeUtil.toW3cLanguageId(theme_display.getLanguageId())
	/>
</#if>

<#if !is_setup_complete>
	<#assign is_setup_complete = theme_display.isImpersonated() />
</#if>

<#-- ---------- URLs ---------- -->

<#assign show_control_panel = theme_display.isShowControlPanelIcon() />

<#if show_control_panel>
	<#assign
		control_panel_text = languageUtil.get(locale, "control-panel")
		control_panel_url = htmlUtil.escape(theme_display.getURLControlPanel())
	/>
</#if>

<#assign show_home = theme_display.isShowHomeIcon() />

<#if show_home>
	<#assign
		home_text = languageUtil.get(locale, "home")
		home_url = htmlUtil.escape(theme_display.getURLHome())
	/>

	<#if !request.isRequestedSessionIdFromCookie()>
		<#assign home_url = htmlUtil.escape(portalUtil.getURLWithSessionId(home_url, request.getSession().getId())) />
	</#if>
</#if>

<#assign show_my_account = theme_display.isShowMyAccountIcon() />

<#if show_my_account>
	<#assign my_account_text = languageUtil.get(locale, "my-account") />

	<#if theme_display.getURLMyAccount()??>
		<#assign my_account_url = htmlUtil.escape(theme_display.getURLMyAccount().toString()) />
	</#if>
</#if>

<#assign show_sign_in = theme_display.isShowSignInIcon() />

<#if show_sign_in>
	<#assign
		sign_in_text = languageUtil.get(locale, "sign-in")
		sign_in_url = htmlUtil.escape(theme_display.getURLSignIn())
	/>
</#if>

<#assign show_sign_out = theme_display.isShowSignOutIcon() />

<#if show_sign_out>
	<#assign
		sign_out_text = languageUtil.get(locale, "sign-out")
		sign_out_url = htmlUtil.escape(theme_display.getURLSignOut())
	/>
</#if>

<#-- ---------- Page ---------- -->

<#assign
	selectable = theme_display.isTilesSelectable()
	is_maximized = layoutTypePortlet.hasStateMax()

	page_javascript_1 = ""
	page_javascript_2 = ""
	page_javascript_3 = ""

	page = layout

	is_first_child = page.isFirstChild()
	is_first_parent = page.isFirstParent()

	is_portlet_page = false
/>

<#if stringUtil.equals(page.getType(), "portlet")>
	<#assign is_portlet_page = true />
</#if>

<#assign typeSettingsProperties = layout.getTypeSettingsProperties() />

<#if typeSettingsProperties??>
	<#assign page_javascript = typeSettingsProperties["javascript"]! />
</#if>

<#assign
	site_name = htmlUtil.escape(page_group.getDescriptiveName())

	community_name = site_name

	is_guest_group = page_group.isGuest()
/>

<#if is_guest_group>
	<#assign css_class = css_class + " guest-site" />
</#if>

<#if is_signed_in>
	<#assign css_class = css_class + " signed-in" />
<#else>
	<#assign css_class = css_class + " signed-out" />
</#if>

<#if layout.isPublicLayout()>
	<#assign css_class = css_class + " public-page" />
<#else>
	<#assign css_class = css_class + " private-page" />
</#if>

<#if page_group.isLayoutPrototype()>
	<#assign css_class = css_class + " page-template" />
</#if>

<#if page_group.isLayoutSetPrototype()>
	<#assign css_class = css_class + " site-template" />
</#if>

<#if page_group.isCompany()>
	<#assign site_type = "company-site" />
<#elseif page_group.isOrganization()>
	<#assign site_type = "organization-site" />
<#elseif page_group.isUser()>
	<#assign site_type = "user-site" />
<#else>
	<#assign site_type = "site" />
</#if>

<#assign
	css_class = css_class + " " + site_type

	site_default_public_url = htmlUtil.escape(page_group.getDisplayURL(theme_display, false))

	community_default_public_url = site_default_public_url

	site_default_private_url = htmlUtil.escape(page_group.getDisplayURL(theme_display, true))

	community_default_private_url = site_default_private_url

	site_default_url = site_default_public_url

	community_default_url = site_default_url
/>

<#if layout.isPrivateLayout()>
	<#assign
		site_default_url = site_default_private_url

		community_default_url = site_default_url
	/>
</#if>

<#assign the_title = "" />

<#if layout.getHTMLTitle(locale)??>
	<#assign the_title = layout.getHTMLTitle(locale) />
</#if>

<#if pageTitle??>
	<#assign the_title = pageTitle />
</#if>

<#if pageSubtitle??>
	<#assign the_title = pageSubtitle + " - " + the_title />
</#if>

<#if tilesTitle?has_content>
	<#assign the_title = languageUtil.get(locale, tilesTitle) />
</#if>

<#if page_group.isLayoutPrototype()>
	<#assign the_title = page_group.getDescriptiveName(locale) />
</#if>

<#if !tilesTitle?has_content>
	<#assign the_title = htmlUtil.escape(the_title) />
</#if>

<#assign
	layout_friendly_url = layout.getFriendlyURL()

	portlet_id = paramUtil.getString(request, "p_p_id")
/>

<#if validator.isNotNull(portlet_id) && layout.isSystem() && !layout.isTypeControlPanel() && stringUtil.equals(layout_friendly_url, "/manage")>
	<#assign the_title = htmlUtil.escape(portalUtil.getPortletTitle(portlet_id, locale)) />
</#if>

<#if the_title ?has_content && !stringUtil.equals(company_name, site_name) && !page_group.isLayoutPrototype()>
	<#assign the_title = the_title + " - " + site_name />
</#if>

<#if htmlTitle??>
	<#assign html_title = htmlUtil.escape(htmlTitle) />
<#else>
	<#assign html_title = the_title + " - " + company_name />
</#if>

<#if layouts??>
	<#assign pages = layouts />
</#if>

<#-- ---------- Logo ---------- -->

<#assign
	logo_css_class = "logo"
	use_company_logo = !layoutSet.isLogo()
	site_logo_height = company_logo_height
	site_logo_width = company_logo_width
/>

<#if (company.getLogoId() == 0) && use_company_logo>
	<#assign logo_css_class = logo_css_class + " default-logo" />
<#else>
	<#assign logo_css_class = logo_css_class + " custom-logo" />
</#if>

<#if theme_settings["show-site-name-supported"]??>
	<#assign show_site_name_supported = getterUtil.getBoolean(theme_settings["show-site-name-supported"]!"", true) />
<#else>
	<#assign show_site_name_supported = true />
</#if>

<#if theme_settings["show-site-name-default"]??>
	<#assign show_site_name_default = getterUtil.getBoolean(theme_settings["show-site-name-default"]!"", show_site_name_supported) />
<#else>
	<#assign show_site_name_default = show_site_name_supported />
</#if>

<#assign
	show_site_name = getterUtil.getBoolean(layoutSet.getSettingsProperty("showSiteName"), show_site_name_default)

	site_logo = company_logo

	logo_description = ""
/>

<#if !show_site_name>
	<#assign logo_description = htmlUtil.escape(site_name) />
</#if>

<#-- ---------- Navigation ---------- -->

<#assign has_navigation = false />

<#if navItems??>
	<#assign
		nav_items = navItems

		has_navigation = (nav_items?size > 0)
	/>
</#if>

<#assign nav_css_class = "sort-pages modify-pages" />

<#if !has_navigation>
	<#assign nav_css_class = nav_css_class + " hide" />
</#if>

<#-- ---------- My sites ---------- -->

<#assign
	show_my_sites = user.hasMySites()

	show_my_places = show_my_sites
/>

<#if show_my_sites>
	<#assign
		my_sites_text = languageUtil.get(locale, "my-sites")

		my_places_text = my_sites_text
	/>
</#if>

<#-- ---------- Includes ---------- -->

<#assign
	dir_include = "/html"
	body_bottom_include = "${dir_include}/common/themes/body_bottom.jsp"
	body_top_include = "${dir_include}/common/themes/body_top.jsp"
	bottom_include = "${dir_include}/common/themes/bottom.jsp"
	bottom_ext_include = bottom_include
/>

<#if tilesContent?has_content>
	<#assign content_include = "${dir_include}${tilesContent}" />
</#if>

<#assign
	top_head_include = "${dir_include}/common/themes/top_head.jsp"
	top_messages_include = "${dir_include}/common/themes/top_messages.jsp"
/>

<#-- ---------- Date ---------- -->

<#assign
	date = dateUtil

	current_time = date.newDate()
	the_year = current_time?date?string("yyyy")
/>

<#-- ---------- Custom init ---------- -->

<#include "${full_templates_path}/init_custom.ftl" />