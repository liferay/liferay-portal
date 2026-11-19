<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
portletDisplay.setShowBackIcon(true);

String backURL = ParamUtil.getString(request, "backURL", themeDisplay.getURLCurrent());

portletDisplay.setURLBack(backURL);

long scheduledPublishProcessId = ParamUtil.getLong(request, "scheduledPublishProcessId");

if (liveGroup == null) {
	liveGroup = group;
}

String portletId = ParamUtil.getString(request, "portletId");
Group publishSourceGroup = (stagingGroup != null) ? stagingGroup : group;

PublishProcessDisplayContext publishProcessDisplayContext = new PublishProcessDisplayContext(liveGroup, locale, ParamUtil.getLong(request, "plid"), portletId, publishSourceGroup);

renderResponse.setTitle(publishProcessDisplayContext.getTitle(scheduledPublishProcessId));
%>

<clay:container-fluid
	cssClass="container-form-lg"
	size="lg"
>
	<div class="sheet">
		<span aria-hidden="true" class="loading-animation mb-9 mt-8"></span>
	</div>

	<react:component
		module="{NewPublish} from exportimport-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"backURL", backURL
			).put(
				"commentsAndRatingsEnabled", publishProcessDisplayContext.isCommentsAndRatingsEnabled()
			).put(
				"defaultScheduled", ParamUtil.getBoolean(request, "scheduled")
			).put(
				"pageTreeModalConfiguration", publishProcessDisplayContext.getPageTreeModalConfiguration()
			).put(
				"processesBackURL",
				Validator.isNull(portletId) ?
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setTabs1(
						"processes"
					).buildString() : backURL
			).put(
				"publishPreviewAPIURL", publishProcessDisplayContext.getPublishPreviewAPIURL()
			).put(
				"publishProcessAPIURL", publishProcessDisplayContext.getPublishProcessAPIURL()
			).put(
				"remote", liveGroup.isStagedRemotely()
			).put(
				"remoteConnection", publishProcessDisplayContext.getRemoteConnectionJSONObject()
			).put(
				"scheduledBackURL",
				PortletURLBuilder.createRenderURL(
					liferayPortletResponse
				).setTabs1(
					"scheduled"
				).buildString()
			).put(
				"scheduledPublishProcessAPIURL", publishProcessDisplayContext.getScheduledPublishProcessAPIURL()
			).put(
				"scheduledPublishProcessId", (scheduledPublishProcessId > 0) ? scheduledPublishProcessId : null
			).put(
				"timeZoneId", timeZone.getID()
			).put(
				"timeZones", publishProcessDisplayContext.getTimeZonesJSONArray()
			).build()
		%>'
	/>
</clay:container-fluid>