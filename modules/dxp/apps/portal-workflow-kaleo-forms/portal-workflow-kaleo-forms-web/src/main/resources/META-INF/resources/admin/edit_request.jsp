<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%@ include file="/admin/deprecated_warning.jspf" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

KaleoProcess kaleoProcess = (KaleoProcess)request.getAttribute(KaleoFormsWebKeys.KALEO_PROCESS);

long classPK = ParamUtil.getLong(request, "classPK");

long kaleoProcessId = BeanParamUtil.getLong(kaleoProcess, request, "kaleoProcessId", classPK);

long groupId = BeanParamUtil.getLong(kaleoProcess, request, "groupId", scopeGroupId);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(LanguageUtil.format(request, "new-x", kaleoProcess.getName(locale), false));
%>

<clay:container-fluid
	cssClass="sidenav-container sidenav-right"
>
	<portlet:actionURL name="/kaleo_forms_admin/start_workflow_instance" var="startWorkflowInstanceURL" />

	<aui:form action="<%= startWorkflowInstanceURL %>" cssClass="lfr-dynamic-form" enctype="multipart/form-data" method="post" name="fm1">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
		<aui:input name="kaleoProcessId" type="hidden" value="<%= String.valueOf(kaleoProcessId) %>" />
		<aui:input name="ddlRecordSetId" type="hidden" value="<%= String.valueOf(kaleoProcess.getDDLRecordSetId()) %>" />
		<aui:input name="ddmTemplateId" type="hidden" value="<%= String.valueOf(kaleoProcess.getDDMTemplateId()) %>" />
		<aui:input name="defaultLanguageId" type="hidden" value="<%= themeDisplay.getLanguageId() %>" />
		<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_PUBLISH %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>

					<%
					DDMTemplate ddmTemplate = kaleoProcess.getDDMTemplate();
					%>

					<liferay-ddm:html
						classNameId="<%= PortalUtil.getClassNameId(DDMTemplate.class) %>"
						classPK="<%= ddmTemplate.getTemplateId() %>"
						requestedLocale="<%= locale %>"
					/>
				</aui:fieldset>
			</div>
		</div>

		<aui:button-row>
			<aui:button cssClass="btn-lg" name="saveButton" type="submit" value="save" />

			<aui:button cssClass="btn-lg" href="<%= redirect %>" name="cancelButton" type="cancel" />
		</aui:button-row>
	</aui:form>
</clay:container-fluid>

<%
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.format(request, "add-x", kaleoProcess.getName(locale), false), currentURL);
%>