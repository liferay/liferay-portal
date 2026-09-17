<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/display/init.jsp" %>

<%
String redirect = PortalUtil.escapeRedirect(ParamUtil.getString(request, "redirect"));

DDMFormInstance formInstance = ddmFormDisplayContext.getFormInstance();

DDMFormInstanceRecord formInstanceRecord = ddmFormDisplayContext.getFormInstanceRecord();

DDMFormInstanceRecordVersion formInstanceRecordVersion = null;

if (formInstanceRecord != null) {
	formInstanceRecordVersion = formInstanceRecord.getLatestFormInstanceRecordVersion();
}

portletDisplay.setURLBack(redirect);
portletDisplay.setShowBackIcon(true);

String title = ParamUtil.getString(request, "title");

renderResponse.setTitle(GetterUtil.get(title, LanguageUtil.get(request, "view-form")));
%>

<clay:container-fluid>
	<c:if test="<%= formInstanceRecordVersion != null %>">
		<aui:model-context bean="<%= formInstanceRecordVersion %>" model="<%= DDMFormInstanceRecordVersion.class %>" />

		<div class="panel text-center">
			<aui:workflow-status markupView="lexicon" model="<%= DDMFormInstanceRecord.class %>" showHelpMessage="<%= false %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= formInstanceRecordVersion.getStatus() %>" version="<%= formInstanceRecordVersion.getVersion() %>" />
		</div>
	</c:if>
</clay:container-fluid>

<clay:container-fluid
	cssClass="ddm-form-builder-app editing-form-entry"
>
	<portlet:actionURL name="/dynamic_data_mapping_form/add_form_instance_record" var="editFormInstanceRecordActionURL" />

	<aui:form action="<%= editFormInstanceRecordActionURL %>" data-DDMFormInstanceId="<%= ddmFormDisplayContext.getFormInstanceId() %>" data-senna-off="true" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="formInstanceRecordId" type="hidden" value="<%= ddmFormDisplayContext.getFormInstanceRecordId() %>" />
		<aui:input name="formInstanceId" type="hidden" value="<%= ddmFormDisplayContext.getFormInstanceId() %>" />
		<aui:input name="defaultLanguageId" type="hidden" value='<%= ParamUtil.getString(request, "defaultLanguageId") %>' />

		<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/dynamic_data_mapping_form/validate_csrf_token" var="validateCSRFTokenURL" />

		<div id=<%= ddmFormDisplayContext.getContainerId() %>>

			<%
			String languageId = ddmFormDisplayContext.getDefaultLanguageId();

			Locale displayLocale = LocaleUtil.fromLanguageId(languageId);
			%>

			<react:component
				module="{FormView} from dynamic-data-mapping-form-web"
				props='<%=
					HashMapBuilder.<String, Object>put(
						"description", formInstance.getDescription(displayLocale)
					).put(
						"title", formInstance.getName(displayLocale)
					).put(
						"validateCSRFTokenURL", validateCSRFTokenURL.toString()
					).putAll(
						ddmFormDisplayContext.getDDMFormContext()
					).build()
				%>'
			/>
		</div>
	</aui:form>
</clay:container-fluid>