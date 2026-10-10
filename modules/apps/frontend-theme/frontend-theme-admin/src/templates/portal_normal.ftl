<!DOCTYPE html>

<#include init />

<#assign
	colorScheme = sessionClicks.get(request, "com_liferay_application_list_taglib_SideNavigationColorScheme", "light")
	prefersContrast = (sessionClicks.get(request, "ACCESSIBILITY_SETTING_HIGH_CONTRAST", "false") == "true")?then("more", "")
/>

<html class="${root_css_class}" data-color-scheme="${colorScheme}" data-prefers-contrast="${prefersContrast}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<title>${html_title}</title>

	<meta content="initial-scale=1.0, width=device-width" name="viewport" />

	<@liferay_util["include"] page=top_head_include />
</head>

<body class="${css_class}">

<@liferay_ui["quick-access"] contentId="#main-content" />

<@liferay_util["include"] page=body_top_include />

<#assign scope_group = theme_display.getScopeGroup() />

<div class="position-relative" id="wrapper">
	<div id="content-wrapper">
		<div id="content">
			<#if selectable>
				<@liferay_util["include"] page=content_include />
			<#else>
				${portletDisplay.recycle()}

				${portletDisplay.setTitle(the_title)}

				<@liferay_theme["wrap-portlet"] page="portlet.ftl">
					<@liferay_util["include"] page=content_include />
				</@>
			</#if>

			<div class="clear"></div>
		</div>
	</div>
</div>

<@liferay_util["include"] page=body_bottom_include />

<@liferay_util["include"] page=bottom_include />

</body>

</html>