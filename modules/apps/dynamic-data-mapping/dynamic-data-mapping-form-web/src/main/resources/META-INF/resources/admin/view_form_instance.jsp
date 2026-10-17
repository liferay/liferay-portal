<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
PortletURL portletURL = ddmFormAdminDisplayContext.getPortletURL();

FormInstancePermissionCheckerHelper formInstancePermissionCheckerHelper = ddmFormAdminDisplayContext.getPermissionCheckerHelper();
%>

<div class="lfr-alert-container">
	<clay:container-fluid className="lfr-alert-wrapper"></clay:container-fluid>
</div>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= portletURL %>" method="post" name="searchContainerForm">
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="deleteFormInstanceIds" type="hidden" />

		<%@ include file="/admin/error_model_listener_exception.jspf" %>

		<c:choose>
			<c:when test="<%= ddmFormAdminDisplayContext.hasResults() %>">
				<liferay-ui:search-container
					id="<%= ddmFormAdminDisplayContext.getSearchContainerId() %>"
					rowChecker="<%= new DDMFormInstanceRowChecker(renderResponse) %>"
					searchContainer="<%= ddmFormAdminDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.dynamic.data.mapping.model.DDMFormInstance"
						keyProperty="formInstanceId"
						modelVar="formInstance"
					>
						<portlet:renderURL var="rowURL">
							<portlet:param name="mvcRenderCommandName" value="/admin/edit_form_instance" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="formInstanceId" value="<%= String.valueOf(formInstance.getFormInstanceId()) %>" />
							<portlet:param name="displayStyle" value="<%= displayStyle %>" />
						</portlet:renderURL>

						<%
						if (!formInstancePermissionCheckerHelper.isShowEditIcon(formInstance)) {
							rowURL = null;
						}
						%>

						<c:choose>
							<c:when test='<%= displayStyle.equals("descriptive") %>'>
								<liferay-ui:search-container-column-icon
									cssClass="asset-icon"
									icon="forms"
								/>

								<liferay-ui:search-container-column-jsp
									colspan="<%= 2 %>"
									href="<%= rowURL %>"
									path="/admin/view_form_instance_descriptive.jsp"
								/>

								<liferay-ui:search-container-column-text>
									<clay:dropdown-actions
										aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
										dropdownItems="<%= ddmFormAdminDisplayContext.getActionDropdownItems(formInstance) %>"
										propsTransformer="{DDMFormAdminActionDropdownPropsTransformer} from dynamic-data-mapping-form-web"
									/>
								</liferay-ui:search-container-column-text>
							</c:when>
							<c:otherwise>

								<%
								boolean hasValidDDMFormFields = ddmFormAdminDisplayContext.hasValidDDMFormFields(formInstance);

								ObjectDefinition objectDefinition = ddmFormAdminDisplayContext.getObjectDefinition(formInstance);

								boolean hasValidMappedObject = ddmFormAdminDisplayContext.hasValidMappedObject(formInstance, objectDefinition);

								boolean hasValidStorageType = ddmFormAdminDisplayContext.hasValidStorageType(formInstance);
								%>

								<c:choose>
									<c:when test="<%= hasValidDDMFormFields && hasValidMappedObject && hasValidStorageType %>">
										<liferay-ui:search-container-column-text
											cssClass="table-cell-expand table-title"
											href="<%= rowURL %>"
											name="name"
											value="<%= HtmlUtil.replaceNewLine(HtmlUtil.escape(formInstance.getName(locale))) %>"
										/>
									</c:when>
									<c:otherwise>
										<liferay-ui:search-container-column-text
											cssClass="table-cell-expand table-title"
											name="name"
										>

											<%
											String errorMessage = StringPool.BLANK;

											if (!hasValidDDMFormFields) {
												errorMessage = LanguageUtil.format(request, "this-form-was-created-using-a-custom-field-type-x-that-is-not-available-for-this-liferay-dxp-installation.-instal-x-to-make-it-available-for-editing", ddmFormAdminDisplayContext.getInvalidDDMFormFieldType(formInstance));
											}
											else if (!hasValidMappedObject) {
												if (objectDefinition == null) {
													errorMessage = "this-form-was-created-using-a-deleted-object-as-storage-type";
												}
												else {
													errorMessage = LanguageUtil.format(request, "this-form-was-created-using-an-inactive-object-as-storage-type.-activate-x-object-to-make-it-available-for-editing", ddmFormAdminDisplayContext.getObjectDefinitionLabel(formInstance, locale));
												}
											}
											else if (!hasValidStorageType) {
												errorMessage = LanguageUtil.format(request, "this-form-was-created-using-a-storage-type-x-that-is-not-available-for-this-liferay-dxp-installation.-install-x-to-make-it-available-for-editing", formInstance.getStorageType());
											}
											%>

											<span class="error-icon">
												<liferay-ui:icon
													icon="exclamation-full"
													markupView="lexicon"
													message="<%= errorMessage %>"
													toolTip="<%= true %>"
												/>
											</span>
											<span class="invalid-form-instance">
												<%= HtmlUtil.replaceNewLine(HtmlUtil.escape(formInstance.getName(locale))) %>
											</span>
										</liferay-ui:search-container-column-text>
									</c:otherwise>
								</c:choose>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									name="description"
									value="<%= HtmlUtil.replaceNewLine(HtmlUtil.escape(formInstance.getDescription(locale))) %>"
								/>

								<liferay-ui:search-container-column-text
									cssClass="text-nowrap"
									name="status"
								>
									<c:choose>
										<c:when test="<%= DDMFormInstanceExpirationStatusUtil.isFormExpired(formInstance, timeZone) %>">
											<clay:label
												displayType="danger"
												label="expired"
											/>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="<%= ddmFormAdminDisplayContext.isFormPublished(formInstance) %>">
													<clay:label
														displayType="success"
														label="available"
													/>
												</c:when>
												<c:otherwise>
													<clay:label
														displayType="info"
														label="unpublished"
													/>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</liferay-ui:search-container-column-text>

								<liferay-ui:search-container-column-date
									cssClass="table-cell-expand-smaller"
									name="modified-date"
									value="<%= formInstance.getModifiedDate() %>"
								/>

								<liferay-ui:search-container-column-text>
									<clay:dropdown-actions
										aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
										dropdownItems="<%= ddmFormAdminDisplayContext.getActionDropdownItems(formInstance) %>"
										propsTransformer="{DDMFormAdminActionDropdownPropsTransformer} from dynamic-data-mapping-form-web"
									/>
								</liferay-ui:search-container-column-text>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="<%= displayStyle %>"
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</c:when>
			<c:otherwise>
				<liferay-frontend:empty-result-message
					actionDropdownItems="<%= ddmFormAdminDisplayContext.getEmptyResultMessageActionItemsDropdownItems() %>"
					animationType="<%= ddmFormAdminDisplayContext.getEmptyResultMessageAnimationType() %>"
					buttonCssClass="secondary"
					description="<%= ddmFormAdminDisplayContext.getEmptyResultMessageDescription() %>"
					title="<%= ddmFormAdminDisplayContext.getEmptyResultsMessage() %>"
				/>
			</c:otherwise>
		</c:choose>
	</aui:form>
</clay:container-fluid>

<%@ include file="/admin/copy_form_publish_url.jspf" %>

<%@ include file="/admin/export_form_instance.jspf" %>