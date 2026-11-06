<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long formDDMTemplateId = ParamUtil.getLong(request, "formDDMTemplateId");

DDLViewRecordsDisplayContext ddlViewRecordsDisplayContext = new DDLViewRecordsDisplayContext(liferayPortletRequest, liferayPortletResponse, formDDMTemplateId);

PortletURL portletURL = ddlViewRecordsDisplayContext.getPortletURL();

if (!ddlDisplayContext.isAdminPortlet()) {
	DDLRecordSet recordSet = ddlDisplayContext.getRecordSet();

	renderResponse.setTitle(recordSet.getName(locale));
}
%>

<c:if test="<%= ddlViewRecordsDisplayContext.isAdminPortlet() %>">
	<clay:navigation-bar
		navigationItems="<%= ddlViewRecordsDisplayContext.getNavigationItems() %>"
	/>
</c:if>

<portlet:actionURL name="/dynamic_data_lists/delete_record" var="deleteRecordURL">
	<portlet:param name="mvcPath" value="/view_records.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:management-toolbar
	actionDropdownItems="<%= ddlViewRecordsDisplayContext.getActionItemsDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"deleteRecordURL", deleteRecordURL.toString()
		).build()
	%>'
	clearResultsURL="<%= ddlViewRecordsDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= ddlViewRecordsDisplayContext.getCreationMenu() %>"
	disabled="<%= ddlViewRecordsDisplayContext.isDisabledManagementBar() %>"
	itemsTotal="<%= ddlViewRecordsDisplayContext.getTotalItems() %>"
	orderDropdownItems="<%= ddlViewRecordsDisplayContext.getOrderItemsDropdownItems() %>"
	propsTransformer="{ViewRecordsManagementToolbarPropsTransformer} from dynamic-data-lists-web"
	searchActionURL="<%= ddlViewRecordsDisplayContext.getSearchActionURL() %>"
	searchContainerId="<%= ddlViewRecordsDisplayContext.getSearchContainerId() %>"
	searchFormName="fm1"
	selectable="<%= ddlViewRecordsDisplayContext.isSelectable() %>"
	sortingOrder="<%= ddlViewRecordsDisplayContext.getOrderByType() %>"
	sortingURL="<%= ddlViewRecordsDisplayContext.getSortingURL() %>"
/>

<%@ include file="/deprecated_warning.jspf" %>

<clay:container-fluid
	cssClass="view-records-container"
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= portletURL %>" method="post" name="fm">
		<aui:input name="recordIds" type="hidden" />

		<liferay-ui:search-container
			id="<%= ddlViewRecordsDisplayContext.getSearchContainerId() %>"
			rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
			searchContainer="<%= ddlViewRecordsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.lists.model.DDLRecord"
				keyPropertyName="recordId"
				modelVar="record"
			>

				<%
				DDLRecordVersion recordVersion = record.getRecordVersion();

				if (ddlViewRecordsDisplayContext.isEditable()) {
					recordVersion = record.getLatestRecordVersion();
				}

				row.setParameter("editable", String.valueOf(ddlViewRecordsDisplayContext.isEditable()));
				row.setParameter("formDDMTemplateId", String.valueOf(formDDMTemplateId));
				row.setParameter("hasDeletePermission", String.valueOf(ddlViewRecordsDisplayContext.hasDeletePermission()));
				row.setParameter("hasUpdatePermission", String.valueOf(ddlViewRecordsDisplayContext.hasUpdatePermission()));

				String href = StringPool.BLANK;

				if (ddlViewRecordsDisplayContext.isEditable()) {
					PortletURL rowURL = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCPath(
						"/view_record.jsp"
					).setRedirect(
						currentURL
					).setParameter(
						"editable", ddlViewRecordsDisplayContext.isEditable()
					).setParameter(
						"formDDMTemplateId", formDDMTemplateId
					).setParameter(
						"recordId", record.getRecordId()
					).setParameter(
						"version", recordVersion.getVersion()
					).buildPortletURL();

					href = rowURL.toString();
				}

				Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap = ddlViewRecordsDisplayContext.getDDMFormFieldValuesMap(recordVersion);

				for (DDMFormField ddmFormField : ddlViewRecordsDisplayContext.getDDMFormFields()) {
					LocalizedValue label = ddmFormField.getLabel();

					String value = StringPool.BLANK;

					List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(ddmFormField.getName());

					if (ddmFormFieldValues != null) {
						DDMFormFieldValueRenderer ddmFormFieldValueRenderer = DDMFormFieldValueRendererRegistryUtil.getDDMFormFieldValueRenderer(ddmFormField.getType());

						value = ddmFormFieldValueRenderer.render(ddmFormFieldValues, themeDisplay.getLocale());
					}
				%>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						href="<%= href %>"
						name="<%= label.getString(themeDisplay.getLocale()) %>"
						value="<%= value %>"
					/>

				<%
				}
				%>

				<c:if test="<%= ddlViewRecordsDisplayContext.hasUpdatePermission() %>">
					<liferay-ui:search-container-column-status
						href="<%= href %>"
						name="status"
						status="<%= recordVersion.getStatus() %>"
						statusByUserId="<%= recordVersion.getStatusByUserId() %>"
						statusDate="<%= recordVersion.getStatusDate() %>"
					/>

					<liferay-ui:search-container-column-date
						href="<%= href %>"
						name="modified-date"
						value="<%= record.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-text
						href="<%= href %>"
						name="author"
						value="<%= HtmlUtil.escape(PortalUtil.getUserName(recordVersion)) %>"
					/>
				</c:if>

				<liferay-ui:search-container-column-jsp
					path="/record_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= ddlViewRecordsDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<%@ include file="/export_record_set.jspf" %>

<aui:script use="liferay-portlet-dynamic-data-lists"></aui:script>