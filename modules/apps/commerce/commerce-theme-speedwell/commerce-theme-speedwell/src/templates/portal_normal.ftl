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

	<body class="speedwell ${css_class}">
		<@liferay.control_menu />

		<div class="liferay-top">
			<@liferay_ui["quick-access"] contentId="#main-content" />
			<@liferay_util["include"] page=body_top_include />
		</div>

		<div class="position-relative" id="wrapper">
			<main class="speedwell-frame" id="speedwell">
				<div class="speedwell-frame__topbar">
					<#include "${full_templates_path}/topbar.ftl" />
				</div>

				<#if speedwell_content_css_class?contains("wide")>
					<div class="speedwell-frame speedwell-frame__content--wide">
				<#else>
					<div class="speedwell-frame speedwell-frame__content">
				</#if>
					<a name="speedwell-top"></a>

					<div class="container-fluid ${speedwell_content_css_class}" id="content">
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

				<footer class="speedwell-footer">
					<div class="speedwell-footer__closing">
						<img alt="${logo_description}" class="logo" src="${site_logo}" />

						<nav>
							<a href="#">Privacy Policy</a>
							<a href="#">Terms and Conditions</a>
							<a href="#">Legal Notice Patents</a>
						</nav>
					</div>
				</footer>
			</main>
		</div>

		<div class="liferay-bottom">
			<@liferay_util["include"] page=body_bottom_include />
			<@liferay_util["include"] page=bottom_include />
		</div>
	</body>
</html>