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

long ddlRecordId = ParamUtil.getLong(request, "ddlRecordId");

DDLRecord ddlRecord = DDLRecordServiceUtil.getRecord(ddlRecordId);

DDLRecordSet ddlRecordSet = ddlRecord.getRecordSet();

KaleoProcess kaleoProcess = (KaleoProcess)request.getAttribute(KaleoFormsWebKeys.KALEO_PROCESS);

String version = ParamUtil.getString(request, "version", DDLRecordConstants.VERSION_DEFAULT);

DDLRecordVersion ddlRecordVersion = ddlRecord.getRecordVersion(version);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(LanguageUtil.format(request, "view-x", kaleoProcess.getName(locale), false));
%>

<clay:container-fluid>
	<c:if test="<%= ddlRecordVersion != null %>">
		<aui:model-context bean="<%= ddlRecordVersion %>" model="<%= DDLRecordVersion.class %>" />

		<div class="panel text-center">
			<liferay-portal-workflow:status
				bean="<%= ddlRecord %>"
				modelClass="<%= KaleoProcess.class %>"
				showInstanceTracker="<%= true %>"
				showStatusLabel="<%= false %>"
				status="<%= ddlRecord.getStatus() %>"
				version="<%= ddlRecordVersion.getVersion() %>"
			/>
		</div>
	</c:if>

	<aui:fieldset>

		<%
		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		DDMFormValues ddmFormValues = null;

		if (ddlRecordVersion != null) {
			ddmFormValues = kaleoFormsAdminDisplayContext.getDDMFormValues(ddlRecordVersion.getDDMStorageId());
		}
		%>

		<div class="panel">
			<liferay-ddm:html
				classNameId="<%= PortalUtil.getClassNameId(DDMStructure.class) %>"
				classPK="<%= ddmStructure.getStructureId() %>"
				ddmFormValues="<%= ddmFormValues %>"
				readOnly="<%= true %>"
				requestedLocale="<%= locale %>"
			/>
		</div>

		<aui:button-row>
			<aui:button cssClass="btn-lg" href="<%= redirect %>" name="cancelButton" type="cancel" />
		</aui:button-row>
	</aui:fieldset>
</clay:container-fluid>