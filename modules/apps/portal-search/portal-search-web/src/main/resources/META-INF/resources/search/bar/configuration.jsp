<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/learn" prefix="liferay-learn" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.learn.LearnMessageUtil" %><%@
page import="com.liferay.petra.string.StringBundler" %><%@
page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.petra.string.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.ReleaseInfo" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortlet" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.SearchBarPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
SearchBarPortletDisplayContext searchBarPortletDisplayContext = (SearchBarPortletDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

SearchBarPortletInstanceConfiguration searchBarPortletInstanceConfiguration = searchBarPortletDisplayContext.getSearchBarPortletInstanceConfiguration();

SearchBarPortletPreferences searchBarPortletPreferences = new SearchBarPortletPreferencesImpl(portletPreferences);

String suggestionsContributorConfiguration = StringBundler.concat(StringPool.OPEN_BRACKET, StringUtil.merge(searchBarPortletInstanceConfiguration.suggestionsContributorConfigurations(), StringPool.COMMA), StringPool.CLOSE_BRACKET);
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
			<liferay-template:template-selector
				className="<%= SearchBarPortlet.class.getName() %>"
				displayStyle="<%= searchBarPortletInstanceConfiguration.displayStyle() %>"
				displayStyleGroupId="<%= searchBarPortletDisplayContext.getDisplayStyleGroupId() %>"
				refreshURL="<%= configurationRenderURL %>"
				showEmptyOption="<%= true %>"
			/>

			<aui:input helpMessage="invisible-help" label="invisible" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_INVISIBLE) %>" type="checkbox" value="<%= searchBarPortletPreferences.isInvisible() %>" />

			<c:if test="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>">
				<div class="alert alert-info text-center">
					<liferay-ui:message key="the-header-search-bar-configuration-takes-precedence" />
				</div>
			</c:if>

			<aui:input disabled="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>" label="keywords-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_KEYWORDS_PARAMETER_NAME) %>" value="<%= searchBarPortletPreferences.getKeywordsParameterName() %>" />

			<aui:select disabled="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>" label="scope" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_SEARCH_SCOPE) %>" value="<%= searchBarPortletPreferences.getSearchScopePreferenceString() %>">
				<aui:option label="this-site" value="this-site" />
				<aui:option label="everything" value="everything" />
				<aui:option label="let-the-user-choose" value="let-the-user-choose" />
			</aui:select>

			<aui:input disabled="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>" label="scope-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_SCOPE_PARAMETER_NAME) %>" value="<%= searchBarPortletPreferences.getScopeParameterName() %>" />

			<aui:input helpMessage="destination-page-help" label="destination-page" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_DESTINATION) %>" value="<%= searchBarPortletPreferences.getDestination() %>" />
		</liferay-frontend:fieldset>

		<c:if test="<%= searchBarPortletDisplayContext.isSuggestionsEndpointEnabled() %>">
			<liferay-frontend:fieldset
				collapsible="<%= true %>"
				label="suggestions-configuration"
			>
				<div class="sheet-text">
					<liferay-ui:message key="suggestions-configuration-description" />

					<liferay-learn:message
						key="search-bar-suggestions"
						resource="portal-search-web"
					/>
				</div>

				<aui:input label="enable-suggestions" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_SUGGESTIONS_ENABLED) %>" type="checkbox" value="<%= searchBarPortletPreferences.isSuggestionsEnabled() %>" />

				<div class="search-bar-configuration-options-container <%= !searchBarPortletPreferences.isSuggestionsEnabled() ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />suggestionsOptionsContainer">
					<aui:input helpMessage="character-threshold-for-displaying-suggestions-help" label="character-threshold-for-displaying-suggestions" min="0" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_SUGGESTIONS_DISPLAY_THRESHOLD) %>" size="10" type="number" value="<%= searchBarPortletInstanceConfiguration.suggestionsDisplayThreshold() %>" wrapperCssClass="c-mb-0">
						<aui:validator name="min">0</aui:validator>
					</aui:input>

					<div>
						<span aria-hidden="true" class="loading-animation loading-animation-sm mt-4"></span>

						<react:component
							module="{SearchBarConfigurationSuggestions} from portal-search-web"
							props='<%=
								HashMapBuilder.<String, Object>put(
									"initialSuggestionsContributorConfiguration", suggestionsContributorConfiguration
								).put(
									"isDXP", ReleaseInfo.isDXP()
								).put(
									"isSearchExperiencesSupported", searchBarPortletDisplayContext.isSearchExperiencesSupported()
								).put(
									"learnResources", LearnMessageUtil.getReactDataJSONObject("portal-search-web")
								).put(
									"namespace", liferayPortletResponse.getNamespace()
								).put(
									"suggestionsContributorConfigurationName", PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_SUGGESTIONS_CONTRIBUTOR_CONFIGURATION)
								).build()
							%>'
						/>
					</div>
				</div>
			</liferay-frontend:fieldset>
		</c:if>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="advanced-configuration"
		>
			<aui:input disabled="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>" helpMessage="use-advanced-search-syntax-help" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_USE_ADVANCED_SEARCH_SYNTAX) %>" type="checkbox" value="<%= searchBarPortletPreferences.isUseAdvancedSearchSyntax() %>" />

			<aui:input disabled="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>" helpMessage="show-results-from-staged-sites-help" label="show-results-from-staged-sites" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_SHOW_STAGED_RESULTS) %>" type="checkbox" value="<%= searchBarPortletPreferences.isShowStagedResults() %>" />

			<c:if test="<%= searchBarPortletDisplayContext.isDisplayIncludeAttachments() %>">
				<aui:input disabled="<%= searchBarPortletDisplayContext.isDisplayWarningIgnoredConfiguration() %>" helpMessage="include-attachments-in-search-help" label="include-attachments-in-search" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_INCLUDE_ATTACHMENTS) %>" type="checkbox" value="<%= searchBarPortletPreferences.isIncludeAttachments() %>" />
			</c:if>

			<aui:input helpMessage="enter-the-key-of-an-alternate-search-this-widget-is-participating-on-if-not-set-widget-participates-on-default-search" label="federated-search-key" name="<%= PortletPreferencesJspUtil.getInputName(SearchBarPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY) %>" type="text" value="<%= searchBarPortletPreferences.getFederatedSearchKey() %>" />
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />suggestionsEnabled',
		'<portlet:namespace />suggestionsOptionsContainer'
	);
</aui:script>