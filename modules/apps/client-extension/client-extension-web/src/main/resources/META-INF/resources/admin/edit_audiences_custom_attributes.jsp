<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
EditClientExtensionEntryDisplayContext<AudiencesCustomAttributesCET> editClientExtensionEntryDisplayContext = (EditClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

AudiencesCustomAttributesCET audiencesCustomAttributesCET = editClientExtensionEntryDisplayContext.getCET();
%>

<aui:field-wrapper cssClass="form-group">
	<aui:input ignoreRequestValue="<%= true %>" label="js-url" name="url" required="<%= true %>" type="text" value="<%= audiencesCustomAttributesCET.getURL() %>" />

	<div class="form-text">
		<liferay-ui:message key="specify-the-url-of-the-audiences-custom-attributes-javascript-file" />
	</div>
</aui:field-wrapper>

<div class="lfr-form-rows" id="<portlet:namespace />_symbols_field">

	<%
	String[] names = editClientExtensionEntryDisplayContext.getStrings(audiencesCustomAttributesCET.getNames());
	String[] symbols = editClientExtensionEntryDisplayContext.getStrings(audiencesCustomAttributesCET.getSymbols());
	String[] types = editClientExtensionEntryDisplayContext.getStrings(audiencesCustomAttributesCET.getTypes());

	for (int i = 0; i < symbols.length; i++) {
	%>

		<div class="lfr-form-row">
			<aui:field-wrapper cssClass="form-group">
				<aui:input ignoreRequestValue="<%= true %>" label="name" name="names" required="<%= true %>" type="text" value="<%= names[i] %>" />

				<aui:select ignoreRequestValue="<%= true %>" label="type" name="types" required="<%= true %>" value="<%= types[i] %>">
					<aui:option label="boolean" value="boolean" />
					<aui:option label="number" value="number" />
					<aui:option label="set" value="set" />
					<aui:option label="string" value="string" />
				</aui:select>

				<aui:input ignoreRequestValue="<%= true %>" label="symbol" name="symbols" required="<%= true %>" type="text" value="<%= symbols[i] %>" />

				<div class="form-text form-text-repeat">
					<liferay-ui:message key="enter-the-names-of-the-exported-functions-that-implement-custom-attributes" />
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
		contentBox: '#<portlet:namespace />_symbols_field',
		minimumRows: 1,
		namespace: '<portlet:namespace />',
	});
</aui:script>