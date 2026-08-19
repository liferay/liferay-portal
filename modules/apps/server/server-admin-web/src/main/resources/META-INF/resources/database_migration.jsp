<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
<liferay-ui:error key="databaseSchemaExportFailed" message="unable-to-export-the-database-schema" />

<%
String anchorCloseTag = StringPool.BLANK;
String anchorOpenTag = StringPool.BLANK;

LearnMessage learnMessage = LearnMessageUtil.getLearnMessage("database-migration", themeDisplay.getLanguageId(), "server-admin-web");

if (Validator.isNotNull(learnMessage.getURL())) {
	anchorCloseTag = "</a>";
	anchorOpenTag = StringBundler.concat("<a class=\"text-underline\" href=\"", learnMessage.getURL(), "\" target=\"_blank\">");
}
%>

<div class="sheet">
	<div class="panel-group panel-group-flush">
		<p class="sheet-text">
			<%= LanguageUtil.format(request, "exports-the-current-database-schema-as-sql-files-for-use-with-the-db-migration-importer-tool", new Object[] {anchorOpenTag, anchorCloseTag}, false) %>
		</p>

		<aui:input cssClass="lfr-input-text-container" label="export-files-path" name="exportFilesPath" required="<%= true %>" type="text" />

		<liferay-captcha:captcha />

		<aui:button-row>
			<aui:button onClick='<%= "javascript:" + liferayPortletResponse.getNamespace() + "exportDatabaseSchema(event)" %>' primary="<%= true %>" type="submit" value="export" />
		</aui:button-row>
	</div>
</div>

<aui:script>
	function <portlet:namespace />exportDatabaseSchema(event) {
		event.preventDefault();

		var form = document.getElementById('<portlet:namespace />fm');

		if (form) {
			form.action =
				'<portlet:actionURL name="/server_admin/export_database_schema" />';

			submitForm(form);
		}
	}
</aui:script>