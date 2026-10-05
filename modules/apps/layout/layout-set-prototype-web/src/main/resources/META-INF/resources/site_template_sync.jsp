<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SiteTemplateSyncDisplayContext siteTemplateSyncDisplayContext = (SiteTemplateSyncDisplayContext)request.getAttribute(LayoutSetPrototypeWebKeys.SITE_TEMPLATE_SYNC_DISPLAY_CONTEXT);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(siteTemplateSyncDisplayContext.getBackURL());

renderResponse.setTitle(siteTemplateSyncDisplayContext.getTitle());
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<div class="sheet">
		<span aria-hidden="true" class="loading-animation mb-9 mt-8"></span>
	</div>

	<react:component
		module="{NewSiteTemplateSync} from exportimport-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"backURL", siteTemplateSyncDisplayContext.getBackURL()
			).put(
				"commentsAndRatingsEnabled", siteTemplateSyncDisplayContext.isCommentsAndRatingsEnabled()
			).put(
				"pageTreeModalConfiguration", siteTemplateSyncDisplayContext.getPageTreeModalConfiguration()
			).put(
				"siteTemplateName", siteTemplateSyncDisplayContext.getSiteTemplateName()
			).put(
				"syncPreview", siteTemplateSyncDisplayContext.getSyncPreviewJSONObject()
			).put(
				"syncPreviewAPIURL", siteTemplateSyncDisplayContext.getSyncPreviewAPIURL()
			).put(
				"syncProcessAPIURL", siteTemplateSyncDisplayContext.getSyncProcessAPIURL()
			).put(
				"timeZoneId", timeZone.getID()
			).build()
		%>'
	/>
</clay:container-fluid>