<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
%>

<portlet:actionURL name="updateArchivedSetup" var="updateArchivedSetupURL">
	<portlet:param name="mvcPath" value="/edit_configuration_templates.jsp" />
	<portlet:param name="portletConfiguration" value="<%= Boolean.TRUE.toString() %>" />
</portlet:actionURL>

<div class="cadmin portlet-configuration-add-template">
	<aui:form action="<%= updateArchivedSetupURL %>" cssClass="form" id="fm" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="portletResource" type="hidden" value="<%= portletResource %>" />

		<div class="portlet-configuration-body-content">
			<clay:container-fluid>
				<liferay-ui:error exception="<%= PortletItemNameException.class %>" message="please-enter-a-valid-setup-name" />

				<div class="sheet">
					<div class="panel-group panel-group-flush">
						<aui:fieldset>

							<%
							String name = StringPool.BLANK;

							boolean useCustomTitle = GetterUtil.getBoolean(portletPreferences.getValue("portletSetupUseCustomTitle", null));

							if (useCustomTitle) {
								name = PortletConfigurationUtil.getPortletTitle(portletDisplay.getId(), portletPreferences, LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()));
							}
							%>

							<aui:input name="name" placeholder="name" required="<%= true %>" type="text" value="<%= name %>">
								<aui:validator name="maxLength">75</aui:validator>
							</aui:input>
						</aui:fieldset>
					</div>
				</div>
			</clay:container-fluid>
		</div>

		<aui:button-row>
			<liferay-frontend:edit-form-buttons
				redirect="<%= redirect %>"
			/>
		</aui:button-row>
	</aui:form>
</div>

<liferay-frontend:component
	module="{AddConfigurationTemplateEventHandler} from portlet-configuration-web"
/>