<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/import/init.jsp" %>

<liferay-portlet:renderURL var="backURL" />

<%
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

long backgroundTaskId = GetterUtil.getLong(request.getParameter("backgroundTaskId"));

BackgroundTask backgroundTask = BackgroundTaskManagerUtil.fetchBackgroundTask(backgroundTaskId);

renderResponse.setTitle(backgroundTask.getName());

GroupDisplayContextHelper groupDisplayContextHelper = new GroupDisplayContextHelper(request);
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(true);
						navigationItem.setHref(currentURL);
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "report-entries"));
					});
			}
		}
	%>'
/>

<aui:form method="post" name="fm">
	<frontend-data-set:headless-display
		apiURL="<%= reportEntriesDisplayContext.getImportProcessReportEntriesAPIURL(String.valueOf(backgroundTaskId)) %>"
		fdsActionDropdownItems="<%= reportEntriesDisplayContext.getFDSActionDropdownItems() %>"
		id="<%= stagingGroupHelper.isCompanyGroup(groupDisplayContextHelper.getGroup()) ? ExportImportFDSNames.COMPANY_IMPORT_REPORT_ENTRIES : ExportImportFDSNames.IMPORT_REPORT_ENTRIES %>"
		propsTransformer="{ReportEntriesFDSPropsTransformer} from exportimport-web"
		style="fluid"
	/>
</aui:form>