<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
EditClientExtensionEntryDisplayContext<EditorConfigContributorCET> editClientExtensionEntryDisplayContext = (EditClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

EditorConfigContributorCET editorConfigContributorCET = editClientExtensionEntryDisplayContext.getCET();
%>

<aui:field-wrapper cssClass="form-group">
	<aui:input label="js-url" name="url" required="<%= true %>" type="text" value="<%= editorConfigContributorCET.getURL() %>" />

	<div class="form-text">
		<liferay-ui:message key="enter-the-url-of-the-javascript-file-to-customize-an-editor-configuration" />
	</div>
</aui:field-wrapper>

<div class="lfr-form-rows" id="<portlet:namespace />_portletNames_field">

	<%
	for (String editorConfigContributorCETPortletName : editClientExtensionEntryDisplayContext.getStrings(editorConfigContributorCET.getPortletNames())) {
	%>

		<div class="lfr-form-row">
			<aui:field-wrapper cssClass="form-group">
				<aui:input ignoreRequestValue="<%= true %>" label="portlet-names" name="portletNames" type="text" value="<%= editorConfigContributorCETPortletName %>" />

				<div class="form-text form-text-repeat">
					<liferay-ui:message key="enter-portlet-name-property-of-this-editor-config-contributor" />
				</div>
			</aui:field-wrapper>
		</div>

	<%
	}
	%>

</div>

<div class="lfr-form-rows" id="<portlet:namespace />_editorNames_field">

	<%
	for (String editorName : editClientExtensionEntryDisplayContext.getStrings(editorConfigContributorCET.getEditorNames())) {
	%>

		<div class="lfr-form-row">
			<aui:field-wrapper cssClass="form-group">
				<aui:input ignoreRequestValue="<%= true %>" label="editor-name" name="editorNames" type="text" value="<%= editorName %>" />

				<div class="form-text form-text-repeat">
					<liferay-ui:message key="enter-editor-name-property-of-this-editor-config-contributor" />
				</div>
			</aui:field-wrapper>
		</div>

	<%
	}
	%>

</div>

<div class="lfr-form-rows" id="<portlet:namespace />_editorConfigKeys_field">

	<%
	for (String editorConfigKey : editClientExtensionEntryDisplayContext.getStrings(editorConfigContributorCET.getEditorConfigKeys())) {
	%>

		<div class="lfr-form-row">
			<aui:field-wrapper cssClass="form-group">
				<aui:input ignoreRequestValue="<%= true %>" label="editor-config-key" name="editorConfigKeys" type="text" value="<%= editorConfigKey %>" />

				<div class="form-text form-text-repeat">
					<liferay-ui:message key="enter-editor-config-key-property-of-this-editor-config-contributor" />
				</div>
			</aui:field-wrapper>
		</div>

	<%
	}
	%>

</div>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: '#<portlet:namespace />_portletNames_field',
		minimumRows: 1,
		namespace: '<portlet:namespace />',
	});

	autoFields({
		contentBox: '#<portlet:namespace />_editorNames_field',
		minimumRows: 1,
		namespace: '<portlet:namespace />',
	});

	autoFields({
		contentBox: '#<portlet:namespace />_editorConfigKeys_field',
		minimumRows: 1,
		namespace: '<portlet:namespace />',
	});
</aui:script>