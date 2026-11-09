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

if (Validator.isNull(redirect)) {
	redirect = PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCPath(
		"/admin/view.jsp"
	).buildString();
}

String historyKey = ParamUtil.getString(request, "historyKey");

KaleoProcess kaleoProcess = (KaleoProcess)request.getAttribute(KaleoFormsWebKeys.KALEO_PROCESS);

long kaleoProcessId = BeanParamUtil.getLong(kaleoProcess, request, "kaleoProcessId");

long groupId = BeanParamUtil.getLong(kaleoProcess, request, "groupId", scopeGroupId);

String kaleoProcessName = KaleoFormsUtil.getKaleoProcessName(kaleoProcess, portletSession, locale);

boolean kaleoProcessStarted = false;

if (kaleoProcess != null) {
	kaleoProcessStarted = DDLRecordLocalServiceUtil.getRecordsCount(kaleoProcess.getDDLRecordSetId(), WorkflowConstants.STATUS_ANY) > 0;
}

portletDisplay.setShowBackIcon(true);

PortletURL backPortletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/admin/view.jsp"
).buildPortletURL();

portletDisplay.setURLBack(backPortletURL.toString());

String title = null;

if (Validator.isNull(kaleoProcessName)) {
	title = LanguageUtil.get(request, "new-process");
}
else {
	title = kaleoProcessName;
}

renderResponse.setTitle(title);
%>

<c:if test="<%= kaleoProcessStarted %>">
	<clay:container-fluid
		cssClass="alert alert-info"
	>
		<liferay-ui:message key="updating-the-field-set-or-workflow-will-cause-loss-of-data" />
	</clay:container-fluid>
</c:if>

<portlet:actionURL name="/kaleo_forms_admin/update_kaleo_process" var="editKaleoProcessURL">
	<portlet:param name="mvcPath" value="/admin/edit_kaleo_process.jsp" />
	<portlet:param name="redirect" value="<%= backPortletURL.toString() %>" />
</portlet:actionURL>

<aui:form action="<%= editKaleoProcessURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="kaleoProcessId" type="hidden" value="<%= kaleoProcessId %>" />
	<aui:input name="groupId" type="hidden" value="<%= groupId %>" />
	<aui:input name="scope" type="hidden" value="1" />

	<liferay-ui:error exception="<%= KaleoProcessDDMTemplateIdException.class %>" message="please-enter-a-valid-initial-form" />
	<liferay-ui:error exception="<%= RequiredStructureException.class %>" message="unable-to-delete-field-set-with-forms-associated-with-it" />
	<liferay-ui:error exception="<%= RequiredWorkflowDefinitionException.class %>" message="unable-to-delete-the-workflow-definition-that-is-in-use" />

	<liferay-util:buffer
		var="htmlBottom"
	>
		<aui:button-row cssClass="kaleo-process-buttons mt-4">
			<aui:button cssClass='<%= (historyKey.equals("forms") ? StringPool.BLANK : "hide") + " float-left kaleo-process-previous" %>' icon="icon-circle-arrow-left" value="previous" />

			<aui:button cssClass='<%= (historyKey.equals("forms") ? StringPool.BLANK : "hide") + " float-right kaleo-process-submit" %>' disabled="<%= true %>" primary="<%= true %>" type="submit" />

			<aui:button cssClass='<%= (historyKey.equals("forms") ? "hide" : StringPool.BLANK) + " float-right kaleo-process-next" %>' disabled="<%= true %>" icon="icon-circle-arrow-right" iconAlign="right" primary="<%= true %>" value="next" />

			<aui:button cssClass="float-right kaleo-process-cancel" href="<%= redirect %>" value="cancel" />
		</aui:button-row>
	</liferay-util:buffer>

	<liferay-frontend:form-navigator-steps
		formName="fm"
		htmlBottom="<%= htmlBottom %>"
		id="kaleo.form"
		showButtons="<%= false %>"
	/>

	<aui:script use="liferay-component,liferay-form,liferay-kaleo-forms-admin">
		var afterFormRegistered = function (event) {
			var form = Liferay.Form.get('<portlet:namespace />fm');

			if (form === event.form) {
				Liferay.component('<portlet:namespace />KaleoFormsAdmin', () => {
					return new Liferay.KaleoFormsAdmin({
						currentURL: '<%= HtmlUtil.escapeJS(currentURL) %>',
						form: form,
						kaleoProcessId: <%= kaleoProcessId %>,
						namespace: '<portlet:namespace />',
						portletId: '<%= PortalUtil.getPortletId(request) %>',
						saveInPortletSessionURL:
							'<portlet:resourceURL id="saveInPortletSession" />',
						tabView: Liferay.component('<portlet:namespace />fmTabview'),
					});
				});

				Liferay.component('<portlet:namespace />KaleoFormsAdmin').syncUI();
			}
		};

		Liferay.after('form:registered', afterFormRegistered);

		var clearAfterFormRegistered = function (event) {
			Liferay.detach('form:registered', afterFormRegistered);
		};

		Liferay.on('destroyPortlet', clearAfterFormRegistered);
	</aui:script>
</aui:form>