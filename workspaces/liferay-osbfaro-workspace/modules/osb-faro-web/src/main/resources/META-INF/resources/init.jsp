<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.osb.faro.constants.DocumentationConstants" %><%@
page import="com.liferay.osb.faro.constants.FaroChannelConstants" %><%@
page import="com.liferay.osb.faro.constants.FaroProjectConstants" %><%@
page import="com.liferay.osb.faro.contacts.model.constants.ContactsCardTemplateConstants" %><%@
page import="com.liferay.osb.faro.engine.client.constants.ActivityConstants" %><%@
page import="com.liferay.osb.faro.engine.client.constants.DataSourceConstants" %><%@
page import="com.liferay.osb.faro.engine.client.constants.FieldMappingConstants" %><%@
page import="com.liferay.osb.faro.engine.client.constants.LCPProjectConstants" %><%@
page import="com.liferay.osb.faro.engine.client.constants.SegmentConstants" %><%@
page import="com.liferay.osb.faro.engine.client.constants.TimeConstants" %><%@
page import="com.liferay.osb.faro.provisioning.client.constants.FaroSubscriptionConstants" %><%@
page import="com.liferay.osb.faro.util.FaroPropsValues" %><%@
page import="com.liferay.osb.faro.web.internal.constants.FaroConstants" %><%@
page import="com.liferay.osb.faro.web.internal.constants.FaroPaginationConstants" %><%@
page import="com.liferay.osb.faro.web.internal.constants.FaroPortletKeys" %><%@
page import="com.liferay.osb.faro.web.internal.constants.FaroPreferencesConstants" %><%@
page import="com.liferay.osb.faro.web.internal.constants.FaroWebKeys" %><%@
page import="com.liferay.osb.faro.web.internal.constants.UserConstants" %><%@
page import="com.liferay.osb.faro.web.internal.util.JSONUtil" %><%@
page import="com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil" %><%@
page import="com.liferay.portal.kernel.model.User" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.PortalUtil" %><%@
page import="com.liferay.portal.kernel.util.URLCodec" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<aui:script position="inline">

	<%
	User currentUser = themeDisplay.getUser();
	%>

	window.faroConstants =
		<%=
	JSONUtil.writeValueAsString(
		HashMapBuilder.<String, Object>put(
		"activityActions", ActivityConstants.getActions())
		.put("applications", FaroConstants.getApplications())
		.put("cerebroAssetsURL", (String)request.getAttribute(FaroWebKeys.CEREBRO_ASSETS_URL))
		.put("cerebroTouchpointsURL", (String)request.getAttribute(FaroWebKeys.CEREBRO_TOUCHPOINTS_URL))
		.put("channelPermissionTypes", FaroChannelConstants.getPermissionTypes())
		.put("contactsCardTemplateTypes", ContactsCardTemplateConstants.getConstants())
		.put("dataSourceDisplayStatuses", DataSourceConstants.getDisplayStatuses())
		.put("dataSourceProgressStatuses", DataSourceConstants.getProgressStatuses())
		.put("dataSourceStatuses", DataSourceConstants.getStatuses())
		.put("dataSourceTypes", DataSourceConstants.getTypes())
		.put("documentationURLs", DocumentationConstants.getURLs())
		.put("entityTypes", FaroConstants.getTypes())
		.put("faroURL", FaroPropsValues.FARO_URL)
		.put("fieldContexts", FieldMappingConstants.getContexts())
		.put("fieldOwnerTypes", FieldMappingConstants.getOwnerTypes())
		.put("fieldTypes", FieldMappingConstants.getFieldTypes())
		.put("locale", locale.toString())
		.put("pagination", FaroPaginationConstants.getConstants())
		.put("portletNamespace", PortalUtil.getPortletNamespace(FaroPortletKeys.FARO))
		.put("preferencesScopes", FaroPreferencesConstants.getScopes())
		.put("projectLocations", LCPProjectConstants.getLocations())
		.put("projectStates", FaroProjectConstants.getStates())
		.put("segmentStates", SegmentConstants.getSegmentStates())
		.put("segmentTypes", SegmentConstants.getSegmentTypes())
		.put("subscriptionPlans", FaroSubscriptionConstants.getFaroSubscriptionPlans())
		.put("subscriptionStatuses", FaroSubscriptionConstants.getStatuses())
		.put("timeIntervals", TimeConstants.getIntervals())
		.put("timeSpans", TimeConstants.getTimeSpans())
		.put("userName", currentUser.getFullName())
		.put("userRoleNames", UserConstants.getRoleNames())
		.put("userStatuses", UserConstants.getStatuses()).build()) %>;
</aui:script>