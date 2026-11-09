<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.search.results.portlet.shared.search.SearchResultsPortletSharedSearchContributor" %>

<%
SearchResultsPortletInstanceConfiguration searchResultsPortletInstanceConfiguration = (SearchResultsPortletInstanceConfiguration)request.getAttribute(SearchResultsPortletInstanceConfiguration.class.getName());

boolean limitResultCountAccuracyAvailable = FeatureFlagManagerUtil.isEnabled("LPD-98858");
%>

<aui:input name="displayStyle" type="textarea" value="<%= searchResultsPortletInstanceConfiguration.displayStyle() %>" />

<aui:input name="displayStyleGroupExternalReferenceCode" type="textarea" value="<%= searchResultsPortletInstanceConfiguration.displayStyleGroupExternalReferenceCode() %>" />

<aui:input name="displayStyleGroupId" value="<%= searchResultsPortletInstanceConfiguration.displayStyleGroupId() %>" wrapperCssClass="mb-0" />

<div class="form-feedback-group mb-4">
	<span class="form-help-text">
		<liferay-ui:message key="display-style-group-id-description" />
	</span>
</div>

<aui:input disabled="<%= !limitResultCountAccuracyAvailable %>" label="limit-result-count-accuracy" name="limitResultCountAccuracy" type="checkbox" value="<%= limitResultCountAccuracyAvailable && searchResultsPortletInstanceConfiguration.limitResultCountAccuracy() %>" wrapperCssClass="mb-0" />

<div class="form-feedback-group mb-4">
	<span class="form-help-text">
		<liferay-ui:message arguments="<%= SearchResultsPortletSharedSearchContributor.RESULT_COUNT_ACCURACY_LIMIT %>" key="limit-result-count-accuracy-help" translateArguments="<%= false %>" />

		<c:if test="<%= !limitResultCountAccuracyAvailable %>">
			<liferay-ui:message arguments='<%= LanguageUtil.get(request, "feature.flag.LPD-98858.title") %>' key="instance-settings-enable-feature-flag-help" translateArguments="<%= false %>" />
		</c:if>
	</span>
</div>

<%
boolean maximumSearchDepthEnabled = FeatureFlagManagerUtil.isEnabled("LPD-114635");
%>

<aui:input disabled="<%= !maximumSearchDepthEnabled %>" label="maximum-search-depth" name="maximumSearchDepth" type="number" value="<%= searchResultsPortletInstanceConfiguration.maximumSearchDepth() %>" wrapperCssClass="mb-0">
	<aui:validator name="digits" />
	<aui:validator name="min">1</aui:validator>
</aui:input>

<div class="form-feedback-group mb-4">
	<span class="form-help-text">
		<liferay-ui:message key="maximum-search-depth-help" />

		<c:if test="<%= !maximumSearchDepthEnabled %>">
			<liferay-ui:message arguments='<%= LanguageUtil.get(request, "feature.flag.LPD-114635.title") %>' key="instance-settings-enable-feature-flag-help" translateArguments="<%= false %>" />
		</c:if>
	</span>
</div>