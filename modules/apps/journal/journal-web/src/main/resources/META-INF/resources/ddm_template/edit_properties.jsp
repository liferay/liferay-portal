<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalEditDDMTemplateDisplayContext journalEditDDMTemplateDisplayContext = (JournalEditDDMTemplateDisplayContext)request.getAttribute(JournalEditDDMTemplateDisplayContext.class.getName());
%>

<div class="journal-ddm-template-properties">
	<liferay-frontend:form-navigator
		fieldSetCssClass="form-group-sm mb-0 panel-group-flush"
		formModelBean="<%= journalEditDDMTemplateDisplayContext.getDDMTemplate() %>"
		id="<%= JournalWebConstants.FORM_NAVIGATOR_ID_JOURNAL_DDM_TEMPLATE %>"
		showButtons="<%= false %>"
	/>
</div>