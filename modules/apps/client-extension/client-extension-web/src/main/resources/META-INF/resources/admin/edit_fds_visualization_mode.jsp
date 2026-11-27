<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
EditClientExtensionEntryDisplayContext<FDSVisualizationModeCET> editClientExtensionEntryDisplayContext = (EditClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

FDSVisualizationModeCET fdsVisualizationModeCET = editClientExtensionEntryDisplayContext.getCET();
%>

<aui:field-wrapper cssClass="form-group">
	<aui:input label="js-url" name="url" required="<%= true %>" type="text" value="<%= fdsVisualizationModeCET.getURL() %>" />

	<div class="form-text">
		<liferay-ui:message key="enter-the-url-of-the-javascript-file-to-customize-a-frontend-data-set-visualization-mode" />
	</div>
</aui:field-wrapper>

<aui:field-wrapper cssClass="form-group">
	<react:component
		module="{IconPickerFormField} from client-extension-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"icon", fdsVisualizationModeCET.getIcon()
			).put(
				"spritemap", themeDisplay.getPathThemeSpritemap()
			).build()
		%>'
	/>
</aui:field-wrapper>

<%
String sectionsPlaceholder = "[{\"label\": \"Title\", \"name\": \"title\", \"required\": true}]";
%>

<aui:input aria-describedby='<%= liferayPortletResponse.getNamespace() + "sectionsHelp" %>' label="item-elements" name="sections" placeholder="<%= HtmlUtil.escapeAttribute(sectionsPlaceholder) %>" rows="6" type="textarea" value="<%= fdsVisualizationModeCET.getSections() %>" />

<div class="form-text mb-3" id="<portlet:namespace />sectionsHelp">
	<liferay-ui:message key="fds-visualization-mode-item-elements-help" />
</div>