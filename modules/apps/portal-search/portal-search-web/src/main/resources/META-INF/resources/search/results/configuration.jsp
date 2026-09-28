<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.search.results.portlet.SearchResultsPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
SearchResultsPortletDisplayContext searchResultsPortletDisplayContext = new SearchResultsPortletDisplayContext(request);

SearchResultsPortletInstanceConfiguration searchResultsPortletInstanceConfiguration = searchResultsPortletDisplayContext.getSearchResultsPortletInstanceConfiguration();

SearchResultsPortletPreferences searchResultsPortletPreferences = new SearchResultsPortletPreferencesImpl(portletPreferences);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />
<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="display-settings"
		>
			<div class="display-template">
				<liferay-template:template-selector
					className="<%= SearchResultSummaryDisplayContext.class.getName() %>"
					displayStyle="<%= searchResultsPortletInstanceConfiguration.displayStyle() %>"
					displayStyleGroupId="<%= searchResultsPortletDisplayContext.getDisplayStyleGroupId() %>"
					refreshURL="<%= configurationRenderURL %>"
				/>
			</div>

			<aui:input helpMessage="show-default-empty-result-message-help" label="show-default-empty-result-message" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_SHOW_EMPTY_RESULT_MESSAGE) %>" type="checkbox" value="<%= searchResultsPortletPreferences.isShowEmptyResultMessage() %>" />

			<aui:input helpMessage="show-default-pagination-help" label="show-default-pagination" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_SHOW_PAGINATION) %>" type="checkbox" value="<%= searchResultsPortletPreferences.isShowPagination() %>" />
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="advanced-configuration"
		>
			<aui:input label="enable-highlighting" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_HIGHLIGHT_ENABLED) %>" type="checkbox" value="<%= searchResultsPortletPreferences.isHighlightEnabled() %>" />

			<aui:input helpMessage="display-selected-result-in-context-help" label="display-selected-result-in-context" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_VIEW_IN_CONTEXT) %>" type="checkbox" value="<%= searchResultsPortletPreferences.isViewInContext() %>" />

			<aui:input helpMessage="display-results-in-document-form-help" label="display-results-in-document-form" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_DISPLAY_IN_DOCUMENT_FORM) %>" type="checkbox" value="<%= searchResultsPortletPreferences.isDisplayInDocumentForm() %>" />

			<%
			boolean limitResultCountAccuracyFeatureFlagEnabled = searchResultsPortletDisplayContext.isLimitResultCountAccuracyFeatureFlagEnabled();

			String limitResultCountAccuracyHelpMessage = LanguageUtil.get(request, "limit-result-count-accuracy-help");
			String limitResultCountAccuracyInputName = SearchResultsPortletPreferences.PREFERENCE_KEY_LIMIT_RESULT_COUNT_ACCURACY;

			if (limitResultCountAccuracyFeatureFlagEnabled) {
				limitResultCountAccuracyInputName = PortletPreferencesJspUtil.getInputName(limitResultCountAccuracyInputName);
			}
			else {
				limitResultCountAccuracyHelpMessage += StringPool.SPACE + LanguageUtil.format(request, "instance-settings-enable-feature-flag-help", LanguageUtil.get(request, "feature.flag.LPD-98858.title"), false);
			}
			%>

			<aui:input disabled="<%= !limitResultCountAccuracyFeatureFlagEnabled %>" helpMessage="<%= limitResultCountAccuracyHelpMessage %>" label="limit-result-count-accuracy" name="<%= limitResultCountAccuracyInputName %>" type="checkbox" value="<%= searchResultsPortletDisplayContext.isLimitResultCountAccuracy() %>" />

			<aui:input label="pagination-start-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_PAGINATION_START_PARAMETER_NAME) %>" value="<%= searchResultsPortletPreferences.getPaginationStartParameterName() %>" />

			<aui:input label="pagination-delta" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_PAGINATION_DELTA) %>" type="text" value="<%= searchResultsPortletPreferences.getPaginationDelta() %>" />

			<aui:input label="pagination-delta-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_PAGINATION_DELTA_PARAMETER_NAME) %>" type="text" value="<%= searchResultsPortletPreferences.getPaginationDeltaParameterName() %>" />

			<aui:input helpMessage="fields-to-display-help" label="fields-to-display" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_FIELDS_TO_DISPLAY) %>" type="text" value="<%= searchResultsPortletPreferences.getFieldsToDisplay() %>" />

			<aui:input helpMessage="enter-the-key-of-an-alternate-search-this-widget-is-participating-on-if-not-set-widget-participates-on-default-search" label="federated-search-key" name="<%= PortletPreferencesJspUtil.getInputName(SearchResultsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY) %>" type="text" value="<%= searchResultsPortletPreferences.getFederatedSearchKey() %>" />
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>