<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPConfigurationListDisplayContext cpConfigurationListDisplayContext = (CPConfigurationListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPConfigurationEntry cpConfigurationEntry = cpConfigurationListDisplayContext.getCPConfigurationEntry();
%>

<aui:form method="post" name="fm" onSubmit='<%= liferayPortletResponse.getNamespace() + "saveCPConfigurationEntry(event, this.form)" %>'>
	<liferay-frontend:side-panel-content
		title='<%= LanguageUtil.format(request, "edit-x", cpConfigurationListDisplayContext.getProductName(), false) %>'
	>
		<div class="row">
			<div class="col-12">
				<clay:sheet
					cssClass="product-configuration sheet-multiple-form"
					size="md"
				>
					<clay:sheet-header>
						<h3 class="sheet-title" data-qa-id="sidePanelTitle"><liferay-ui:message key="configuration-settings" /></h3>
					</clay:sheet-header>

					<clay:sheet-section>
						<liferay-util:include page="/configuration_list/edit_cp_configuration_entry_form.jsp" servletContext="<%= application %>" />
					</clay:sheet-section>
				</clay:sheet>
			</div>
		</div>

		<div class="dialog-footer">
			<clay:button
				data-qa-id="saveButton"
				displayType="primary"
				label="save"
				type="submit"
			/>
		</div>
	</liferay-frontend:side-panel-content>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"cpConfigurationEntryId", (cpConfigurationEntry != null) ? cpConfigurationEntry.getCPConfigurationEntryId() : StringPool.BLANK
		).put(
			"cpConfigurationListId", cpConfigurationListDisplayContext.getCPConfigurationListId()
		).put(
			"entityId", (cpConfigurationEntry != null) ? cpConfigurationEntry.getClassPK() : 0
		).put(
			"mode", ((cpConfigurationEntry != null) && (cpConfigurationEntry.getCPConfigurationListId() == cpConfigurationListDisplayContext.getCPConfigurationListId())) ? "edit" : "add"
		).put(
			"namespace", liferayPortletResponse.getNamespace()
		).build()
	%>'
	module="{editCpConfigurationEntry} from commerce-product-definitions-web"
/>