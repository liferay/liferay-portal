<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
DDMFormViewFormInstanceRecordsDisplayContext ddmFormViewFormInstanceRecordsDisplayContext = ddmFormAdminDisplayContext.getDDMFormViewFormInstanceRecordsDisplayContext();

PortletURL portletURL = ddmFormViewFormInstanceRecordsDisplayContext.getPortletURL();
%>

<portlet:actionURL name="/dynamic_data_mapping_form/delete_form_instance_record" var="deleteFormInstanceRecordURL">
	<portlet:param name="mvcPath" value="/admin/view_form_instance_records.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<%
BulkSelectionRunner bulkSelectionRunner = BulkSelectionRunnerUtil.getBulkSelectionRunner();
%>

<div>
	<react:component
		module="{BulkStatus} from dynamic-data-mapping-form-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"bulkInProgress", bulkSelectionRunner.isBusy(user)
			).put(
				"deletionStarted", SessionMessages.contains(renderRequest, "deleteFormInstanceRecordsInBackground")
			).put(
				"pathModule", PortalUtil.getPathModule()
			).build()
		%>'
	/>
</div>

<clay:management-toolbar
	actionDropdownItems="<%= ddmFormViewFormInstanceRecordsDisplayContext.getActionItemsDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"deleteFormInstanceRecordURL", deleteFormInstanceRecordURL.toString()
		).build()
	%>'
	clearResultsURL="<%= ddmFormViewFormInstanceRecordsDisplayContext.getClearResultsURL() %>"
	disabled="<%= ddmFormViewFormInstanceRecordsDisplayContext.isDisabledManagementBar() %>"
	itemsTotal="<%= ddmFormViewFormInstanceRecordsDisplayContext.getTotalItems() %>"
	orderDropdownItems="<%= ddmFormViewFormInstanceRecordsDisplayContext.getOrderItemsDropdownItems() %>"
	propsTransformer="{DDMFormViewFormInstanceRecordsManagementToolbarPropsTransformer} from dynamic-data-mapping-form-web"
	searchActionURL="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSearchActionURL() %>"
	searchContainerId="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSearchContainerId() %>"
	searchFormName="fm"
	sortingOrder="<%= HtmlUtil.escape(ddmFormViewFormInstanceRecordsDisplayContext.getOrderByType()) %>"
	sortingURL="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSortingURL() %>"
/>

<%
DDMFormInstance ddmFormInstance = ddmFormViewFormInstanceRecordsDisplayContext.getDDMFormInstance();
%>

<c:if test="<%= DDMFormInstanceExpirationStatusUtil.isFormExpired(ddmFormInstance, timeZone) %>">
	<clay:stripe
		dismissible="<%= true %>"
		displayType="warning"
		message="the-form-is-expired-and-is-no-longer-available-for-editing-or-submitting-new-answers"
	/>
</c:if>

<clay:stripe
	displayType="info"
	message="view-current-fields-warning-message"
/>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "viewEntriesContainer" %>'
>
	<aui:form action="<%= portletURL %>" method="post" name="searchContainerForm">
		<aui:input name="deleteFormInstanceRecordIds" type="hidden" />
		<aui:input name="formInstanceId" type="hidden" value="<%= ddmFormInstance.getFormInstanceId() %>" />
		<aui:input name="selectAll" type="hidden" />

		<liferay-ui:search-container
			id="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSearchContainerId() %>"
			rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
			searchContainer="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord"
				keyProperty="formInstanceRecordId"
				modelVar="formInstanceRecord"
			>
				<c:if test="<%= ddmFormViewFormInstanceRecordsDisplayContext.getAvailableLocalesCount() > 1 %>">
					<liferay-ui:search-container-column-text
						name="language"
					>

						<%
						String w3cLanguageId = StringUtil.toLowerCase(LocaleUtil.toW3cLanguageId(ddmFormViewFormInstanceRecordsDisplayContext.getDefaultLocale(formInstanceRecord)));
						%>

						<div class="search-container-column-language">
							<svg class="h4 lexicon-icon lexicon-icon-<%= w3cLanguageId %> reference-mark">
								<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#<%= w3cLanguageId %>" />
							</svg>
						</div>
					</liferay-ui:search-container-column-text>
				</c:if>

				<%
				Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap = ddmFormViewFormInstanceRecordsDisplayContext.getDDMFormFieldValues(formInstanceRecord);

				DDMForm ddmForm = ddmFormViewFormInstanceRecordsDisplayContext.getDDMForm(formInstanceRecord);

				Map<String, DDMFormField> ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(true);

				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(ddmFormViewFormInstanceRecordsDisplayContext.getAvailableActions(permissionChecker))
					).build());

				for (DDMFormField ddmFormField : ddmFormViewFormInstanceRecordsDisplayContext.getDDMFormFields()) {
				%>

					<c:choose>
						<c:when test="<%= StringUtil.equals(ddmFormField.getType(), DDMFormFieldTypeConstants.IMAGE) %>">
							<liferay-ui:search-container-column-image
								name="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnName(ddmFormField) %>"
								src="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnValue(ddmFormFieldsMap.get(ddmFormField.getName()), StringPool.BLANK, ddmFormFieldValuesMap.get(ddmFormField.getName())) %>"
							/>
						</c:when>
						<c:when test="<%= StringUtil.equals(ddmFormField.getType(), DDMFormFieldTypeConstants.SEARCH_LOCATION) %>">
							<liferay-ui:search-container-column-text
								name="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnName(ddmFormField) %>"
								truncate="<%= true %>"
								value='<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnValue(ddmFormFieldsMap.get(ddmFormField.getName()), "place", ddmFormFieldValuesMap.get(ddmFormField.getName())) %>'
							/>

							<%
							for (String visibleField : ddmFormViewFormInstanceRecordsDisplayContext.getVisibleFields(ddmFormField)) {
							%>

								<liferay-ui:search-container-column-text
									name="<%= visibleField %>"
									truncate="<%= true %>"
									value="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnValue(ddmFormFieldsMap.get(ddmFormField.getName()), visibleField, ddmFormFieldValuesMap.get(ddmFormField.getName())) %>"
								/>

							<%
							}
							%>

						</c:when>
						<c:when test="<%= StringUtil.equals(ddmFormField.getType(), DDMFormFieldTypeConstants.CHECKBOX) %>">
							<liferay-ui:search-container-column-text
								name="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnName(ddmFormField) %>"
								truncate="<%= true %>"
								value="<%= ddmFormViewFormInstanceRecordsDisplayContext.getLocalizedColumnValues(ddmFormViewFormInstanceRecordsDisplayContext.getColumnValue(ddmFormFieldsMap.get(ddmFormField.getName()), StringPool.BLANK, ddmFormFieldValuesMap.get(ddmFormField.getName()))) %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								name="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnName(ddmFormField) %>"
								truncate="<%= true %>"
								value="<%= ddmFormViewFormInstanceRecordsDisplayContext.getColumnValue(ddmFormFieldsMap.get(ddmFormField.getName()), StringPool.BLANK, ddmFormFieldValuesMap.get(ddmFormField.getName())) %>"
							/>
						</c:otherwise>
					</c:choose>

				<%
				}
				%>

				<liferay-ui:search-container-column-status
					name="status"
					property="status"
				/>

				<liferay-ui:search-container-column-date
					name="modified-date"
					value="<%= formInstanceRecord.getModifiedDate() %>"
				/>

				<liferay-ui:search-container-column-text
					name="author"
					value="<%= HtmlUtil.escape(PortalUtil.getUserName(formInstanceRecord)) %>"
				/>

				<liferay-ui:search-container-column-jsp
					path="/admin/form_instance_record_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= ddmFormViewFormInstanceRecordsDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
				paginate="<%= false %>"
				searchContainer="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSearchContainer() %>"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<clay:container-fluid>
	<liferay-ui:search-paginator
		markupView="lexicon"
		searchContainer="<%= ddmFormViewFormInstanceRecordsDisplayContext.getSearchContainer() %>"
	/>
</clay:container-fluid>

<%@ include file="/admin/export_form_instance.jspf" %>