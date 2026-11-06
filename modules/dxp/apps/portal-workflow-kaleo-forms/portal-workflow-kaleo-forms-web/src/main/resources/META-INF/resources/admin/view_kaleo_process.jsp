<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	redirect = PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCPath(
		"/admin/view.jsp"
	).buildString();
}

KaleoFormsViewRecordsDisplayContext kaleoFormsViewRecordsDisplayContext = kaleoFormsAdminDisplayContext.getKaleoFormsViewRecordsDisplayContext();

KaleoProcess kaleoProcess = kaleoFormsViewRecordsDisplayContext.getKaleoProcess();
%>

<clay:navigation-bar
	navigationItems="<%= kaleoFormsViewRecordsDisplayContext.getNavigationItems() %>"
/>

<portlet:actionURL name="/kaleo_forms_admin/delete_record" var="deleteDDLRecordURL">
	<portlet:param name="mvcPath" value="/admin/view_kaleo_process.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="kaleoProcessId" value="<%= String.valueOf(kaleoProcess.getKaleoProcessId()) %>" />
</portlet:actionURL>

<clay:management-toolbar
	actionDropdownItems="<%= kaleoFormsViewRecordsDisplayContext.getActionItemsDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"deleteDDLRecordURL", deleteDDLRecordURL.toString()
		).build()
	%>'
	clearResultsURL="<%= kaleoFormsViewRecordsDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= kaleoFormsViewRecordsDisplayContext.getCreationMenu() %>"
	itemsTotal="<%= kaleoFormsViewRecordsDisplayContext.getTotalItems() %>"
	orderDropdownItems="<%= kaleoFormsViewRecordsDisplayContext.getOrderItemsDropdownItems() %>"
	propsTransformer="{KaleoFormsViewRecordsManagementToolbarPropsTransformer} from portal-workflow-kaleo-forms-web"
	searchActionURL="<%= kaleoFormsViewRecordsDisplayContext.getSearchActionURL() %>"
	searchContainerId="<%= kaleoFormsViewRecordsDisplayContext.getSearchContainerId() %>"
	searchFormName="fm"
	sortingOrder="<%= kaleoFormsViewRecordsDisplayContext.getOrderByType() %>"
	sortingURL="<%= kaleoFormsViewRecordsDisplayContext.getSortingURL() %>"
/>

<%@ include file="/admin/deprecated_warning.jspf" %>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form
		action='<%=
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCPath(
				"/admin/view_kaleo_process.jsp"
			).setRedirect(
				redirect
			).setParameter(
				"kaleoProcessId", kaleoProcess.getKaleoProcessId()
			).buildString()
		%>'
		method="post"
		name="searchContainerForm"
	>
		<aui:input name="ddlRecordIds" type="hidden" />

		<liferay-ui:search-container
			id="<%= kaleoFormsViewRecordsDisplayContext.getSearchContainerId() %>"
			searchContainer="<%= kaleoFormsViewRecordsDisplayContext.getSearch() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.lists.model.DDLRecord"
				keyPropertyName="recordId"
				modelVar="record"
			>

				<%
				row.setParameter("kaleoProcessId", String.valueOf(kaleoProcess.getKaleoProcessId()));

				DDLRecordVersion recordVersion = record.getRecordVersion();

				DDMFormValues ddmFormValues = kaleoFormsAdminDisplayContext.getDDMFormValues(recordVersion.getDDMStorageId());

				Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap = ddmFormValues.getDDMFormFieldValuesMap();

				PortletURL rowURL = PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCPath(
					"/admin/view_record.jsp"
				).setRedirect(
					currentURL
				).setParameter(
					"ddlRecordId", record.getRecordId()
				).setParameter(
					"kaleoProcessId", kaleoProcess.getKaleoProcessId()
				).setParameter(
					"version", recordVersion.getVersion()
				).buildPortletURL();

				// Columns

				for (DDMFormField ddmFormField : kaleoFormsViewRecordsDisplayContext.getDDMFormFields()) {
					String value = StringPool.BLANK;

					List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(ddmFormField.getName());

					if (ddmFormFieldValues != null) {
						DDMFormFieldValueRenderer ddmFormFieldValueRenderer = DDMFormFieldValueRendererRegistryUtil.getDDMFormFieldValueRenderer(ddmFormField.getType());

						value = ddmFormFieldValueRenderer.render(ddmFormFieldValues, themeDisplay.getLocale());
					}
				%>

					<liferay-ui:search-container-column-text
						name="<%= HtmlUtil.escape(kaleoFormsViewRecordsDisplayContext.getColumnName(ddmFormField)) %>"
						truncate="<%= true %>"
					>
						<span class="lfr-portal-tooltip text-truncate-inline" title="<%= value %>">
							<span class="text-truncate">
								<%= value %>
							</span>
						</span>
					</liferay-ui:search-container-column-text>

				<%
				}
				%>

				<c:if test="<%= KaleoProcessPermission.contains(permissionChecker, kaleoProcess, ActionKeys.SUBMIT) %>">
					<liferay-ui:search-container-column-status
						name="status"
						status="<%= recordVersion.getStatus() %>"
						statusByUserId="<%= recordVersion.getStatusByUserId() %>"
						statusDate="<%= recordVersion.getStatusDate() %>"
					/>

					<liferay-ui:search-container-column-date
						name="modified-date"
						value="<%= record.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-text
						name="author"
						value="<%= HtmlUtil.escape(PortalUtil.getUserName(recordVersion)) %>"
					/>
				</c:if>

				<liferay-ui:search-container-column-jsp
					path="/admin/record_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= kaleoFormsViewRecordsDisplayContext.getDisplayStyle() %>"
				markupView="lexicon"
				paginate="<%= false %>"
				searchContainer="<%= kaleoFormsViewRecordsDisplayContext.getSearch() %>"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<clay:container-fluid>
	<liferay-ui:search-paginator
		markupView="lexicon"
		searchContainer="<%= kaleoFormsViewRecordsDisplayContext.getSearch() %>"
		type="article"
	/>
</clay:container-fluid>

<%@ include file="/admin/export_kaleo_process.jspf" %>

<%
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(kaleoProcess.getName(locale));

PortalUtil.setPageSubtitle(kaleoProcess.getName(locale), request);
PortalUtil.setPageDescription(kaleoProcess.getDescription(locale), request);

PortalUtil.addPortletBreadcrumbEntry(request, kaleoProcess.getName(locale), currentURL);
%>