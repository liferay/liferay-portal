<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPConfigurationListDisplayContext cpConfigurationListDisplayContext = (CPConfigurationListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPConfigurationList cpConfigurationList = cpConfigurationListDisplayContext.getCPConfigurationList();
long templateCPConfigurationEntryId = cpConfigurationListDisplayContext.getTemplateCPConfigurationEntryId();
%>

<aui:form method="post" name="fm" onSubmit='<%= liferayPortletResponse.getNamespace() + "saveCPConfigurationList(event, this.form)" %>'>
	<div class="row">
		<aui:model-context bean="<%= cpConfigurationList %>" model="<%= CPConfigurationList.class %>" />

		<div class="<%= (templateCPConfigurationEntryId > 0) ? "col-12" : "col-8" %>">
			<commerce-ui:panel
				elementClasses="mt-4"
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<div class="row">
					<div class="col-12">
						<aui:input data-qa-id="nameInput" formName="fm" name="name" type="text">
							<aui:validator name="required" />
						</aui:input>
					</div>

					<div class="col-12">
						<aui:input data-qa-id="catalogNameInput" disabled="<%= true %>" formName="fm" name="catalogName" type="text" value="<%= cpConfigurationListDisplayContext.getCommerceCatalogName() %>" />
					</div>

					<c:if test="<%= cpConfigurationList.getParentCPConfigurationListId() > 0 %>">
						<div class="col-12">
							<aui:input data-qa-id="parentCPConfigurationListNameInput" disabled="<%= true %>" formName="fm" label="parent-configuration" name="parentCPConfigurationListName" type="text" value="<%= cpConfigurationListDisplayContext.getParentCPConfigurationListName() %>" />
						</div>
					</c:if>

					<div class="col-12">
						<aui:input data-qa-id="priorityInput" formName="fm" name="priority" type="text">
							<aui:validator name="number" />
						</aui:input>
					</div>
				</div>
			</commerce-ui:panel>
		</div>

		<c:if test="<%= templateCPConfigurationEntryId <= 0 %>">
			<div class="col-4">
				<commerce-ui:panel
					elementClasses="mt-4"
					title='<%= LanguageUtil.get(request, "schedule") %>'
				>
					<div class="row">
						<div class="col-12" data-qa-id="displayDate">
							<aui:input formName="fm" name="displayDate" />
						</div>

						<div class="col-12" data-qa-id="expirationDate">
							<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= cpConfigurationList.getExpirationDate() == null %>" formName="fm" name="expirationDate" />
						</div>
					</div>
				</commerce-ui:panel>

				<c:if test="<%= cpConfigurationListDisplayContext.hasCustomAttributesAvailable() %>">
					<commerce-ui:panel
						title='<%= LanguageUtil.get(request, "custom-attribute") %>'
					>
						<liferay-expando:custom-attribute-list
							className="<%= CPConfigurationList.class.getName() %>"
							classPK="<%= cpConfigurationList.getCPConfigurationListId() %>"
							editable="<%= true %>"
							label="<%= true %>"
						/>
					</commerce-ui:panel>
				</c:if>
			</div>
		</c:if>

		<c:if test="<%= templateCPConfigurationEntryId > 0 %>">
			<div class="col-12">
				<commerce-ui:panel
					title='<%= LanguageUtil.get(request, "default-product-configuration-settings") %>'
				>
					<liferay-util:include page="/configuration_list/edit_cp_configuration_entry_form.jsp" servletContext="<%= application %>" />
				</commerce-ui:panel>

				<c:if test="<%= cpConfigurationListDisplayContext.hasCustomAttributesAvailable() %>">
					<commerce-ui:panel
						title='<%= LanguageUtil.get(request, "custom-attribute") %>'
					>
						<liferay-expando:custom-attribute-list
							className="<%= CPConfigurationList.class.getName() %>"
							classPK="<%= cpConfigurationList.getCPConfigurationListId() %>"
							editable="<%= true %>"
							label="<%= true %>"
						/>
					</commerce-ui:panel>
				</c:if>
			</div>
		</c:if>
	</div>

	<clay:button
		cssClass="hide"
		id='<%= liferayPortletResponse.getNamespace() + "saveButton" %>'
		type="submit"
	/>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"cpConfigurationEntryId", templateCPConfigurationEntryId
		).put(
			"cpConfigurationListId", cpConfigurationListDisplayContext.getCPConfigurationListId()
		).put(
			"namespace", liferayPortletResponse.getNamespace()
		).build()
	%>'
	module="{editCpConfigurationList} from commerce-product-definitions-web"
/>