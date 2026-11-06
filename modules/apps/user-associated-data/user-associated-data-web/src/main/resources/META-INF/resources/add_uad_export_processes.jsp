<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String backURL = ParamUtil.getString(request, "backURL");

List<UADApplicationExportDisplay> uadApplicationExportDisplayList = (List<UADApplicationExportDisplay>)request.getAttribute(UADWebKeys.UAD_APPLICATION_EXPORT_DISPLAY_LIST);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);

renderResponse.setTitle(StringBundler.concat(selectedUser.getFullName(), " - ", LanguageUtil.get(request, "new-data-export")));
%>

<portlet:renderURL var="viewUADExportProcesses">
	<portlet:param name="p_u_i_d" value="<%= String.valueOf(selectedUser.getUserId()) %>" />
	<portlet:param name="mvcRenderCommandName" value="/user_associated_data/view_uad_export_processes" />
</portlet:renderURL>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<aui:form method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "exportApplicationData();" %>'>
		<aui:input name="p_u_i_d" type="hidden" value="<%= String.valueOf(selectedUser.getUserId()) %>" />
		<aui:input name="redirect" type="hidden" value="<%= viewUADExportProcesses.toString() %>" />
		<aui:input name="applicationKeys" type="hidden" />

		<clay:sheet>
			<clay:sheet-section>
				<h2 class="sheet-title"><liferay-ui:message key="export-personal-data" /></h2>

				<div class="sheet-text">
					<liferay-ui:message key="please-select-the-applications-for-which-you-want-to-start-an-export-process" />
				</div>

				<%
				boolean disableManagementBar = true;

				for (UADApplicationExportDisplay uadApplicationExportDisplay : uadApplicationExportDisplayList) {
					if (uadApplicationExportDisplay.getDataCount() > 0) {
						disableManagementBar = false;

						break;
					}
				}
				%>

				<clay:management-toolbar
					disabled="<%= disableManagementBar %>"
					itemsTotal="<%= uadApplicationExportDisplayList.size() %>"
					searchContainerId="uadApplicationExportDisplay"
					selectable="<%= true %>"
					showSearch="<%= false %>"
				/>

				<liferay-ui:search-container
					id="uadApplicationExportDisplay"
					iteratorURL="<%= currentURLObj %>"
					rowChecker="<%= new UADApplicationExportDisplayChecker(renderResponse) %>"
					total="<%= uadApplicationExportDisplayList.size() %>"
				>
					<liferay-ui:search-container-results
						calculateStartAndEnd="<%= true %>"
						results="<%= uadApplicationExportDisplayList %>"
					/>

					<liferay-ui:search-container-row
						className="com.liferay.user.associated.data.web.internal.display.UADApplicationExportDisplay"
						keyPropertyName="applicationKey"
						modelVar="uadApplicationExportDisplay"
					>

						<%
						if (uadApplicationExportDisplay.getDataCount() == 0) {
							row.setCssClass(row.getCssClass() + " table-disabled");
						}
						%>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand table-list-title"
							name="application"
							value="<%= UADLanguageUtil.getApplicationName(uadApplicationExportDisplay.getApplicationKey(), locale) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="items"
							value="<%= String.valueOf(uadApplicationExportDisplay.getDataCount()) %>"
						/>

						<%
						Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat("yyyy.MM.dd - hh:mm a", locale, themeDisplay.getTimeZone());

						Date lastExportDate = uadApplicationExportDisplay.getLastExportDate();
						%>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="last-available-export"
						>
							<%= (lastExportDate != null) ? dateFormat.format(lastExportDate) : StringPool.DASH %>
						</liferay-ui:search-container-column-text>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</clay:sheet-section>

			<clay:sheet-footer>
				<aui:button primary="<%= true %>" type="submit" value="export" />

				<aui:button href="<%= backURL %>" type="cancel" />
			</clay:sheet-footer>
		</clay:sheet>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />exportApplicationData() {
		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			var applicationKeys = form.querySelector(
				'#<portlet:namespace />applicationKeys'
			);

			if (applicationKeys) {
				applicationKeys.setAttribute(
					'value',
					Liferay.Util.getCheckedCheckboxes(
						form,
						'<portlet:namespace />allRowIds',
						'<portlet:namespace />rowIds'
					)
				);
			}

			submitForm(
				form,
				'<portlet:actionURL name="/user_associated_data/export_application_data" />'
			);
		}
	}
</aui:script>