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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.frontend.esm.FrontendESMUtil" %><%@
page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.configuration.ModifiedFacetPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.portlet.ModifiedFacetPortlet" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.portlet.ModifiedFacetPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.modified.facet.portlet.ModifiedFacetPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
ModifiedFacetDisplayContext modifiedFacetDisplayContext = (ModifiedFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

ModifiedFacetPortletInstanceConfiguration modifiedFacetPortletInstanceConfiguration = modifiedFacetDisplayContext.getModifiedFacetPortletInstanceConfiguration();

ModifiedFacetPortletPreferences modifiedFacetPortletPreferences = new ModifiedFacetPortletPreferencesImpl(portletPreferences);

JSONArray rangesJSONArray = modifiedFacetPortletPreferences.getRangesJSONArray();
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
		<liferay-ui:error key="unparsableDate" message="unparsable-date" />

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="display-settings"
		>
			<div class="display-template">
				<liferay-template:template-selector
					className="<%= ModifiedFacetPortlet.class.getName() %>"
					displayStyle="<%= modifiedFacetPortletInstanceConfiguration.displayStyle() %>"
					displayStyleGroupId="<%= modifiedFacetDisplayContext.getDisplayStyleGroupId() %>"
					refreshURL="<%= configurationRenderURL %>"
					showEmptyOption="<%= true %>"
				/>
			</div>
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="advanced-configuration"
		>
			<aui:input label="frequency-threshold" name="<%= PortletPreferencesJspUtil.getInputName(ModifiedFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD) %>" value="<%= modifiedFacetPortletPreferences.getFrequencyThreshold() %>" />

			<aui:select label="order-terms-by" name="<%= PortletPreferencesJspUtil.getInputName(ModifiedFacetPortletPreferences.PREFERENCE_KEY_ORDER) %>" value="<%= modifiedFacetPortletPreferences.getOrder() %>">
				<aui:option label="ranges-configuration" value="" />
				<aui:option label="term-frequency-descending" value="count:desc" />
				<aui:option label="term-frequency-ascending" value="count:asc" />
			</aui:select>

			<liferay-frontend:fieldset
				collapsible="<%= true %>"
				label="ranges-configuration"
			>
				<aui:fieldset id='<%= liferayPortletResponse.getNamespace() + "rangesId" %>'>

					<%
					int[] rangesIndexes = new int[rangesJSONArray.length()];

					for (int i = 0; i < rangesJSONArray.length(); i++) {
						rangesIndexes[i] = i;

						JSONObject jsonObject = rangesJSONArray.getJSONObject(i);
					%>

						<div class="lfr-form-row lfr-form-row-inline range-form-row">
							<div class="row-fields">
								<aui:input cssClass="label-input" label="label" name='<%= "label_" + i %>' required="<%= true %>" value='<%= jsonObject.getString("label") %>' wrapperCssClass="c-mb-2" />

								<aui:input cssClass="range-input" label="range" name='<%= "range_" + i %>' required="<%= true %>" value='<%= jsonObject.getString("range") %>' wrapperCssClass="c-mb-3" />
							</div>
						</div>

					<%
					}
					%>

					<aui:input cssClass="ranges-input" name="<%= PortletPreferencesJspUtil.getInputName(ModifiedFacetPortletPreferences.PREFERENCE_KEY_RANGES) %>" type="hidden" value="<%= modifiedFacetPortletPreferences.getRangesString() %>" />

					<aui:input name="rangesIndexes" type="hidden" value="<%= StringUtil.merge(rangesIndexes) %>" />
				</aui:fieldset>

				<aui:input label="display-frequencies" name="<%= PortletPreferencesJspUtil.getInputName(ModifiedFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE) %>" type="checkbox" value="<%= modifiedFacetPortletPreferences.isFrequenciesVisible() %>" />
			</liferay-frontend:fieldset>
		</liferay-frontend:fieldset>

		<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-71164") %>'>
			<liferay-frontend:fieldset
				collapsible="<%= true %>"
				label="seo-configuration"
			>
				<aui:input helpMessage="enable-web-crawler-indexing-help" label="enable-web-crawler-indexing" name="<%= PortletPreferencesJspUtil.getInputName(ModifiedFacetPortletPreferences.PREFERENCE_KEY_WEB_CRAWLER_INDEXING_ENABLED) %>" type="checkbox" value="<%= modifiedFacetPortletPreferences.isWebCrawlerIndexingEnabled() %>" />
			</liferay-frontend:fieldset>
		</c:if>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: 'fieldset#<portlet:namespace />rangesId',
		fieldIndexes: '<portlet:namespace />rangesIndexes',
		namespace: '<portlet:namespace />',
	});
</aui:script>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"namespace", liferayPortletResponse.getNamespace()
		).build()
	%>'
	module="{RangeInputs} from portal-search-web"
/>