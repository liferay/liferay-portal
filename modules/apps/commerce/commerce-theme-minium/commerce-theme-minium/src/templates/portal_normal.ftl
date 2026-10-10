<!DOCTYPE html>
<#include init />
<#assign
	colorScheme = sessionClicks.get(request, "com_liferay_application_list_taglib_SideNavigationColorScheme", "light")
	prefersContrast = (sessionClicks.get(request, "ACCESSIBILITY_SETTING_HIGH_CONTRAST", "false") == "true")?then("more", "")
/>

<html class="${root_css_class}" data-color-scheme="${colorScheme}" data-prefers-contrast="${prefersContrast}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">
<head>
	<title>${the_title} - ${company_name}</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />

	<@liferay_util["include"] page=top_head_include />
</head>

<#if validator.isNotNull(redirect_url) && is_login_page && themeDisplay.isSignedIn()>

	<#-- Instant redirect, when the page is hit directly or refreshed -->

	<script ${nonceAttribute}>
		window.location.replace("${redirect_url}");
	</script>

	<#-- Redirect for Senna (I.E. when you press "Go to Site"). This will cause a flash as the page has to fully load -->

	<@liferay_aui.script>
		window.location.replace("${redirect_url}");
	</@>
</#if>

<#if is_login_page && !themeDisplay.isSignedIn()>
	<#assign css_class = css_class + " minium-login" />
</#if>

<body class="${css_class}">
	<@liferay_ui["quick-access"] contentId="#minium" />

	<@liferay.control_menu />

	<div class="liferay-top">
		<@liferay_util["include"] page=body_top_include />
	</div>

	<div class="position-relative" id="wrapper">
		<main class="minium minium-frame" id="minium">
			<#if show_sidebar>
				<div class="minium-frame__sidebar">
					<#include "${full_templates_path}/sidebar.ftl" />
				</div>
			</#if>

			<#if show_topbar>
				<div class="minium-frame__topbar">
					<#include "${full_templates_path}/topbar.ftl" />
				</div>
			</#if>

			<div class="minium-frame__content js-scroll-area">
				<a name="minium-top"></a>

				<div class="${minium_content_css_class}" id="content">
					<#if selectable>
						<@liferay_util["include"] page=content_include />
					<#else>
						${portletDisplay.recycle()}
						${portletDisplay.setTitle(the_title)}

						<@liferay_theme["wrap-portlet"] page="portlet.ftl">
							<@liferay_util["include"] page=content_include />
						</@>
					</#if>
				</div>
			</div>

			<#if show_topbar>

				<#-- The toolbar is needed to create the shadow when scrolling -->

				<div class="minium-frame__toolbar"></div>
			</#if>

			<div class="minium-frame__overlay">
				<@liferay_commerce_ui["search-results"] />
			</div>
		</main>
	</div>

	<div class="liferay-bottom">
			<@liferay_util["include"] page=body_bottom_include />
			<@liferay_util["include"] page=bottom_include />
	</div>
</body>
</html>