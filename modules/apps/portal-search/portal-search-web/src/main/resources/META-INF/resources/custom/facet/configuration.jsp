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
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortlet" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<%@ page import="java.util.Objects" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
CustomFacetDisplayContext customFacetDisplayContext = (CustomFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

CustomFacetPortletInstanceConfiguration customFacetPortletInstanceConfiguration = customFacetDisplayContext.getCustomFacetPortletInstanceConfiguration();

CustomFacetPortletPreferences customFacetPortletPreferences = new CustomFacetPortletPreferencesImpl(portletPreferences);

JSONArray rangesJSONArray = customFacetPortletPreferences.getRangesJSONArray();
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
			label="aggregation-settings"
		>
			<div class="form-group">
				<aui:select label="aggregation-type" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_AGGREGATION_TYPE) %>" value="<%= customFacetPortletPreferences.getAggregationType() %>">
					<aui:option label="terms" value="terms" />
					<aui:option label="date-range" value="dateRange" />
					<aui:option label="range" value="range" />
				</aui:select>

				<aui:input helpMessage="aggregation-field-help" label="aggregation-field" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_AGGREGATION_FIELD) %>" required="<%= true %>" value="<%= customFacetPortletPreferences.getAggregationField() %>" wrapperCssClass="c-mb-0" />

				<div class="form-feedback-group">
					<div class="form-text">
						<liferay-ui:message key="aggregation-field-input-help" />

						<liferay-learn:message
							key="custom-facet"
							resource="portal-search-web"
						/>
					</div>
				</div>
			</div>
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="display-settings"
		>
			<div class="display-template">
				<liferay-template:template-selector
					className="<%= CustomFacetPortlet.class.getName() %>"
					displayStyle="<%= customFacetPortletInstanceConfiguration.displayStyle() %>"
					displayStyleGroupId="<%= customFacetDisplayContext.getDisplayStyleGroupId() %>"
					refreshURL="<%= configurationRenderURL %>"
					showEmptyOption="<%= true %>"
				/>
			</div>

			<aui:input helpMessage="custom-heading-help" label="custom-heading" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING) %>" value="<%= customFacetPortletPreferences.getCustomHeading() %>" />

			<aui:input helpMessage="custom-parameter-name-help" label="custom-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME) %>" value="<%= customFacetPortletPreferences.getParameterName() %>" />

			<div class="<%= StringUtil.equals(customFacetPortletPreferences.getAggregationType(), "terms") ? StringPool.BLANK : "hide" %>" id="<portlet:namespace />maxTermsContainer">
				<aui:input helpMessage="max-terms-help" label="max-terms" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS) %>" value="<%= customFacetPortletPreferences.getMaxTerms() %>" />
			</div>

			<aui:input label="frequency-threshold" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD) %>" value="<%= customFacetPortletPreferences.getFrequencyThreshold() %>" />

			<aui:select helpMessage="order-by-help" id="preferenceKeyOrder" label="order-by" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_ORDER) %>" value="<%= customFacetPortletPreferences.getOrder() %>">
				<aui:option label="frequency-descending" value="count:desc" />
				<aui:option label="frequency-ascending" value="count:asc" />
				<aui:option label="value-ascending" value="key:asc" />
				<aui:option label="value-descending" value="key:desc" />

				<c:if test='<%= Objects.equals(customFacetPortletPreferences.getAggregationType(), "dateRange") || Objects.equals(customFacetPortletPreferences.getAggregationType(), "range") %>'>
					<aui:option id='<%= liferayPortletResponse.getNamespace() + "rangesConfigurationOption" %>' label="ranges-configuration" value="rangesConfiguration" />
				</c:if>
			</aui:select>

			<aui:input label="display-frequencies" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE) %>" type="checkbox" value="<%= customFacetPortletPreferences.isFrequenciesVisible() %>" />
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			cssClass='<%= StringUtil.equals(customFacetPortletPreferences.getAggregationType(), "terms") ? "hide" : StringPool.BLANK %>'
			label="ranges-configuration"
		>
			<div class="form-text text-weight-normal">
				<liferay-ui:message arguments='<%= new String[] {"past-hour", "past-24-hours", "past-week", "past-month", "past-year", "*", "now"} %>' key="ranges-configuration-description" translateArguments="<%= false %>" />

				<liferay-learn:message
					key="custom-facet"
					resource="portal-search-web"
				/>
			</div>

			<div class="form-group">
				<span aria-hidden="true" class="loading-animation loading-animation-sm mt-4"></span>

				<react:component
					module="{CustomConfigurationRangeOptions} from portal-search-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"namespace", liferayPortletResponse.getNamespace()
						).put(
							"rangesIndexesInputName", "rangesIndexes"
						).put(
							"rangesInputName", PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_RANGES)
						).put(
							"rangesJSONArray", rangesJSONArray
						).build()
					%>'
				/>
			</div>

			<div class="form-group">
				<aui:input helpMessage="show-input-range-help" label="show-input-range" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_SHOW_INPUT_RANGE) %>" type="checkbox" value="<%= customFacetPortletPreferences.isShowInputRange() %>" />
			</div>
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="advanced-configuration"
		>
			<aui:input helpMessage="enter-the-key-of-an-alternate-search-this-widget-is-participating-on-if-not-set-widget-participates-on-default-search" label="federated-search-key" name="<%= PortletPreferencesJspUtil.getInputName(CustomFacetPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY) %>" type="text" value="<%= customFacetPortletPreferences.getFederatedSearchKey() %>" />
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>