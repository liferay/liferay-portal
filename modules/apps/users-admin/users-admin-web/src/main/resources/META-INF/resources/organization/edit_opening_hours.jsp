<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditContactInformationDisplayContext editContactInformationDisplayContext = new EditContactInformationDisplayContext("opening-hours", request, renderResponse);

editContactInformationDisplayContext.setPortletDisplay(portletDisplay, portletName);

OrgLabor orgLabor = null;

if (editContactInformationDisplayContext.getPrimaryKey() > 0) {
	orgLabor = OrgLaborServiceUtil.getOrgLabor(editContactInformationDisplayContext.getPrimaryKey());
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "opening-hours"), editContactInformationDisplayContext.getRedirect());
PortalUtil.addPortletBreadcrumbEntry(request, editContactInformationDisplayContext.getSheetTitle(), null);
%>

<portlet:actionURL name="/users_admin/update_contact_information" var="actionURL" />

<aui:form action="<%= actionURL %>" method="post" name="fm">
	<aui:input name="errorMVCPath" type="hidden" value="/common/edit_opening_hours.jsp" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.EDIT %>" />
	<aui:input name="redirect" type="hidden" value="<%= editContactInformationDisplayContext.getRedirect() %>" />
	<aui:input name="className" type="hidden" value="<%= editContactInformationDisplayContext.getClassName() %>" />
	<aui:input name="classPK" type="hidden" value="<%= String.valueOf(editContactInformationDisplayContext.getClassPK()) %>" />
	<aui:input name="listType" type="hidden" value="<%= ListTypeConstants.ORGANIZATION_SERVICE %>" />
	<aui:input name="primaryKey" type="hidden" value="<%= String.valueOf(editContactInformationDisplayContext.getPrimaryKey()) %>" />

	<clay:container-fluid>
		<div class="sheet-lg" id="breadcrumb">
			<liferay-site-navigation:breadcrumb
				breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
			/>
		</div>

		<clay:sheet>
			<clay:sheet-header>
				<h2 class="sheet-title"><%= editContactInformationDisplayContext.getSheetTitle() %></h2>
			</clay:sheet-header>

			<clay:sheet-section>
				<aui:model-context bean="<%= orgLabor %>" model="<%= OrgLabor.class %>" />

				<liferay-ui:error key="<%= NoSuchListTypeException.class.getName() + editContactInformationDisplayContext.getClassName() + ListTypeConstants.ORGANIZATION_SERVICE %>" message="please-select-a-type" />

				<aui:select label="type-of-service" listType="<%= ListTypeConstants.ORGANIZATION_SERVICE %>" listTypeFieldName="listTypeId" name="orgLaborListTypeId" />

				<table border="0">

					<%
					OrgLaborFormDisplay orgLaborFormDisplay = new OrgLaborFormDisplay(locale, orgLabor);

					for (OrgLaborFormDisplay.DayRowDisplay dayRowDisplay : orgLaborFormDisplay.getDayRowDisplays()) {
					%>

						<tr>
							<td>
								<div class="h5"><%= dayRowDisplay.getLongDayName() %></div>
							</td>
							<td>
								<aui:select cssClass="input-container" label="" name='<%= dayRowDisplay.getShortDayName() + "Open" %>'>
									<aui:option value="-1" />

									<%
									for (OrgLaborFormDisplay.SelectOptionDisplay selectOptionDisplay : dayRowDisplay.getOpenSelectOptionDisplays()) {
									%>

										<aui:option label="<%= selectOptionDisplay.getLabel() %>" selected="<%= selectOptionDisplay.isSelected() %>" value="<%= selectOptionDisplay.getValue() %>" />

									<%
									}
									%>

								</aui:select>
							</td>
							<td>
								<div class="h5"><%= StringUtil.lowerCase(LanguageUtil.get(request, "to[date-time]")) %></div>
							</td>
							<td>
								<aui:select cssClass="input-container" label="" name='<%= dayRowDisplay.getShortDayName() + "Close" %>'>
									<aui:option value="-1" />

									<%
									for (OrgLaborFormDisplay.SelectOptionDisplay selectOptionDisplay : dayRowDisplay.getCloseSelectOptionDisplays()) {
									%>

										<aui:option label="<%= selectOptionDisplay.getLabel() %>" selected="<%= selectOptionDisplay.isSelected() %>" value="<%= selectOptionDisplay.getValue() %>" />

									<%
									}
									%>

								</aui:select>
							</td>
						</tr>

					<%
					}
					%>

				</table>
			</clay:sheet-section>

			<clay:sheet-footer>
				<aui:button primary="<%= true %>" type="submit" />

				<aui:button href="<%= editContactInformationDisplayContext.getRedirect() %>" type="cancel" />
			</clay:sheet-footer>
		</clay:sheet>
	</clay:container-fluid>
</aui:form>