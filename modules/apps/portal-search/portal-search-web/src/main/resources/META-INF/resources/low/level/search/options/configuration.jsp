<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.kernel.json.JSONArray" %><%@
page import="com.liferay.portal.kernel.json.JSONObject" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.low.level.search.options.portlet.action.ConfigurationDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.low.level.search.options.portlet.preferences.LowLevelSearchOptionsPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.low.level.search.options.portlet.preferences.LowLevelSearchOptionsPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<%@ page import="java.util.List" %>

<portlet:defineObjects />

<%
ConfigurationDisplayContext configurationDisplayContext = (ConfigurationDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));

LowLevelSearchOptionsPortletPreferences lowLevelSearchOptionsPortletPreferences = new LowLevelSearchOptionsPortletPreferencesImpl(portletPreferences);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
	onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveConfiguration();" %>'
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<aui:fieldset>

			<%
			List<String> connectionIds = configurationDisplayContext.getConnectionIds();
			String selectedConnectionId = lowLevelSearchOptionsPortletPreferences.getConnectionId();
			%>

			<c:if test="<%= !Validator.isBlank(selectedConnectionId) && !connectionIds.contains(selectedConnectionId) %>">
				<div class="alert alert-inline alert-warning">
					<div class="autofit-row">
						<div class="autofit-col">
							<div class="autofit-section c-mr-2">
								<clay:icon
									class="text-warning"
									symbol="warning-full"
								/>
							</div>
						</div>

						<div class="autofit-col autofit-col-expand">
							<div class="autofit-section">
								<liferay-ui:message arguments="<%= selectedConnectionId %>" key="the-previously-selected-connection-id-x-is-no-longer-available" translateArguments="<%= false %>" />
							</div>
						</div>
					</div>
				</div>
			</c:if>

			<aui:select helpMessage="connection-id-help[web]" label="connection-id" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_CONNECTION_ID) %>">
				<aui:option value="" />

				<%
				for (String connectionId : connectionIds) {
				%>

					<aui:option label="<%= HtmlUtil.escape(connectionId) %>" selected="<%= connectionId.equals(selectedConnectionId) %>" value="<%= connectionId %>" />

				<%
				}
				%>

			</aui:select>

			<aui:input helpMessage="indexes-help" label="indexes" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_INDEXES) %>" type="text" value="<%= lowLevelSearchOptionsPortletPreferences.getIndexes() %>" />

			<aui:input helpMessage="fields-to-return-help" label="fields-to-return" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_FIELDS_TO_RETURN) %>" type="text" value="<%= lowLevelSearchOptionsPortletPreferences.getFieldsToReturn() %>" />

			<aui:input helpMessage="contributors-to-include-help" label="contributors-to-include" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_CONTRIBUTORS_TO_INCLUDE) %>" type="text" value="<%= lowLevelSearchOptionsPortletPreferences.getContributorsToInclude() %>" />

			<aui:input helpMessage="contributors-to-exclude-help" label="contributors-to-exclude" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_CONTRIBUTORS_TO_EXCLUDE) %>" type="text" value="<%= lowLevelSearchOptionsPortletPreferences.getContributorsToExclude() %>" />

			<aui:input helpMessage="enter-the-key-of-an-alternate-search-this-widget-is-participating-on-if-not-set-widget-participates-on-default-search" label="federated-search-key" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY) %>" type="text" value="<%= lowLevelSearchOptionsPortletPreferences.getFederatedSearchKey() %>" />
		</aui:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			id='<%= liferayPortletResponse.getNamespace() + "attributesId" %>'
			label="attributes"
		>

			<%
			JSONArray attributesJSONArray = lowLevelSearchOptionsPortletPreferences.getAttributesJSONArray();

			for (int i = 0; i < attributesJSONArray.length(); i++) {
				JSONObject jsonObject = attributesJSONArray.getJSONObject(i);
			%>

				<div class="field-form-row lfr-form-row lfr-form-row-inline">
					<div class="autofit-row row-fields">
						<div class="autofit-col-expand">
							<aui:input cssClass="flex-fill key-input" label="key" name='<%= "key_" + i %>' value='<%= jsonObject.getString("key") %>' wrapperCssClass="c-mb-3 c-mr-2" />
						</div>

						<div class="autofit-col-expand">
							<aui:input cssClass="flex-fill value-input" label="value" name='<%= "value_" + i %>' value='<%= jsonObject.getString("value") %>' wrapperCssClass="c-mb-3" />
						</div>
					</div>
				</div>

			<%
			}
			%>

			<aui:input cssClass="fields-input" name="<%= PortletPreferencesJspUtil.getInputName(LowLevelSearchOptionsPortletPreferences.PREFERENCE_ATTRIBUTES) %>" type="hidden" value="<%= lowLevelSearchOptionsPortletPreferences.getAttributesString() %>" />
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script use="liferay-auto-fields">
	new Liferay.AutoFields({
		contentBox: 'fieldset#<portlet:namespace />attributesId',
		namespace: '<portlet:namespace />',
	}).render();
</aui:script>

<aui:script>
	function <portlet:namespace />saveConfiguration() {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var fields = [];

			var fieldFormRows = Array.prototype.filter.call(
				document.getElementsByClassName('field-form-row'),
				(item) => {
					return !item.getAttribute('hidden');
				}
			);

			fieldFormRows.forEach((item) => {
				fields.push({
					key: item.querySelector('.key-input').value,
					value: item.querySelector('.value-input').value,
				});
			});

			document.getElementById(
				'<%= liferayPortletResponse.getNamespace() + LowLevelSearchOptionsPortletPreferences.PREFERENCE_ATTRIBUTES %>'
			).value = JSON.stringify(fields);

			submitForm(form);
		}
	}
</aui:script>