<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
String redirect = PortalUtil.escapeRedirect(ParamUtil.getString(request, "redirect"));

DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion = ddmFormAdminDisplayContext.getDDMFormInstanceRecordVersion();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(LanguageUtil.get(request, "view-form"));
%>

<clay:container-fluid>
	<c:if test="<%= ddmFormInstanceRecordVersion != null %>">
		<aui:model-context bean="<%= ddmFormInstanceRecordVersion %>" model="<%= DDMFormInstanceRecordVersion.class %>" />

		<div class="panel text-center">
			<aui:workflow-status markupView="lexicon" model="<%= DDMFormInstanceRecord.class %>" showHelpMessage="<%= false %>" showIcon="<%= false %>" showLabel="<%= false %>" status="<%= ddmFormInstanceRecordVersion.getStatus() %>" version="<%= ddmFormInstanceRecordVersion.getVersion() %>" />
		</div>
	</c:if>
</clay:container-fluid>

<clay:container-fluid
	cssClass="editing-form-entry-admin"
>
	<portlet:actionURL name="/dynamic_data_mapping_form/add_form_instance_record" var="editFormInstanceRecordActionURL" />

	<aui:form action="<%= editFormInstanceRecordActionURL %>" data-DDMFormInstanceId="<%= ddmFormInstanceRecordVersion.getFormInstanceId() %>" data-senna-off="true" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="formInstanceRecordId" type="hidden" value="<%= ddmFormInstanceRecordVersion.getFormInstanceRecordId() %>" />
		<aui:input name="formInstanceId" type="hidden" value="<%= ddmFormInstanceRecordVersion.getFormInstanceId() %>" />

		<%
		DDMFormValues ddmFormValues = ddmFormInstanceRecordVersion.getDDMFormValues();
		%>

		<aui:input name="defaultLanguageId" type="hidden" value="<%= LocaleUtil.toLanguageId(ddmFormValues.getDefaultLocale()) %>" />

		<div class="ddm-form-basic-info">
			<h1 class="ddm-form-name"><%= HtmlUtil.replaceNewLine(HtmlUtil.escape(ddmFormAdminDisplayContext.getFormName())) %></h1>

			<%
			String description = ddmFormAdminDisplayContext.getFormDescription();
			%>

			<c:if test="<%= Validator.isNotNull(description) %>">
				<div class="ddm-form-description h5"><%= HtmlUtil.replaceNewLine(HtmlUtil.escape(description)) %></div>
			</c:if>
		</div>

		<%
		String containerId = StringPool.BLANK;

		Map<String, Object> ddmFormContext = ddmFormAdminDisplayContext.getDDMFormContext(renderRequest, false);

		if (ddmFormContext.containsKey("containerId")) {
			containerId = (String)ddmFormContext.get("containerId");
		}
		%>

		<div id="<%= containerId %>">
			<react:component
				module="{FormView} from dynamic-data-mapping-form-web"
				props="<%= ddmFormContext %>"
			/>
		</div>
	</aui:form>
</clay:container-fluid>