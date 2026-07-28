<!DOCTYPE html>

<#include init />

<html dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
	<meta content="initial-scale=1.0, width=device-width" name="viewport" />

	<link href="/o/osb-faro-theme/css/fonts.css" ${nonceAttribute} rel="stylesheet" type = "text/css" />

	<#if is_signed_in>
		<link href="${htmlUtil.escape(portalUtil.getStaticResourceURL(request, "/o/osb-faro-web/dist/main.css"))}" ${nonceAttribute} rel="stylesheet" type = "text/css" />
	</#if>

	<@liferay_util["include"] page=top_head_include />
</head>

<body class="dxp">
	<#if is_signed_in>
		<@liferay_portlet["runtime"] portletName="faro_portlet" />

		<script defer ${nonceAttribute} src="${htmlUtil.escape(portalUtil.getStaticResourceURL(request, "/o/osb-faro-web/dist/main.js"))}" type="module"></script>
	<#else>

		<@liferay_util["include"] page=body_top_include />

		<@liferay_util["include"] page=content_include />

		<@liferay_util["include"] page=bottom_include />

		<@liferay_util["include"] page=body_bottom_include />
	</#if>

	<#if !is_signed_in>
		<script ${nonceAttribute}>
			const parsedUrl = new URL(window.location.href);
			const params = new URLSearchParams(parsedUrl.search);
			const paramName = params.get('_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName')?.replace('/login/', '') ?? null;

			if (paramName) {
				document.querySelector('.portlet-login').classList.add(paramName + '-screen')
			}
		</script>
	<#else>
		<script ${nonceAttribute}>
			document.addEventListener('DOMContentLoaded', () => {
				const liferayAUICSS = document.querySelector('#liferayAUICSS');
				const liferayThemeCSS = document.querySelector('#liferayThemeCSS');

				if (liferayAUICSS) {
					liferayAUICSS.remove();
				}

				if (liferayThemeCSS) {
					liferayThemeCSS.remove();
				}
			});
		</script>
	</#if>
</body>
</html>