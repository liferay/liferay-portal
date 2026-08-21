<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
Date lastPublishDate = null;

Group publishSourceGroup = (stagingGroup != null) ? stagingGroup : group;

LayoutSet layoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(publishSourceGroup.getGroupId(), false);

if (layoutSet != null) {
	lastPublishDate = ExportImportDateUtil.getLastPublishDate(layoutSet);
}

portletDisplay.setShowBackIcon(true);

String backURL = ParamUtil.getString(request, "backURL", themeDisplay.getURLCurrent());

portletDisplay.setURLBack(backURL);

long scheduledPublishProcessId = ParamUtil.getLong(request, "scheduledPublishProcessId");

if (liveGroup == null) {
	liveGroup = group;
}

PublishProcessDisplayContext publishProcessDisplayContext = new PublishProcessDisplayContext(liveGroup, locale);

renderResponse.setTitle(publishProcessDisplayContext.getTitle(scheduledPublishProcessId));
%>

<clay:container-fluid
	cssClass="container-form-lg"
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
				"lastPublishDate", (lastPublishDate == null) ? null : String.valueOf(lastPublishDate.toInstant())
			).put(
				"lookAndFeelEnabled", publishProcessDisplayContext.isLookAndFeelEnabled()
			).put(
				"pageTreeModalConfiguration",
				HashMapBuilder.<String, Object>put(
					"groupId", publishSourceGroup.getGroupId()
				).put(
					"pageSize", PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN
				).put(
					"privateLayoutsAvailable", publishSourceGroup.isPrivateLayoutsEnabled() && publishSourceGroup.hasPrivateLayouts()
				).build()
			).put(
				"publishPreviewAPIURL", publishProcessDisplayContext.getPublishPreviewAPIURL()
			).put(
				"publishProcessAPIURL", publishProcessDisplayContext.getPublishProcessAPIURL()
			).put(
				"scheduledBackURL", HttpComponentsUtil.setParameter(backURL, liferayPortletResponse.getNamespace() + "tabs1", "scheduled")
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