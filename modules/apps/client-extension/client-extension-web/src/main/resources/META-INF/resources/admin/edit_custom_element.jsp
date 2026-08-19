<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
EditClientExtensionEntryDisplayContext<CustomElementCET> editClientExtensionEntryDisplayContext = (EditClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

CustomElementCET customElementCET = editClientExtensionEntryDisplayContext.getCET();
%>

<aui:field-wrapper cssClass="form-group">
	<aui:input label="html-element-name" name="htmlElementName" required="<%= true %>" type="text" value="<%= customElementCET.getHTMLElementName() %>" />

	<div class="form-text">
		<liferay-ui:message key="specify-the-name-for-the-custom-element-thats-declared-in-its-javascript-file" />
	</div>
</aui:field-wrapper>

<aui:input label="use-esm" name="useESM" type="checkbox" value="<%= customElementCET.isUseESM() %>" />

<div class="lfr-form-rows" id="<portlet:namespace />_urls_field">

	<%
	for (String url : editClientExtensionEntryDisplayContext.getStrings(customElementCET.getURLs())) {
	%>

		<div class="lfr-form-row">
			<aui:field-wrapper cssClass="form-group">
				<aui:input ignoreRequestValue="<%= true %>" label="js-url" name="urls" required="<%= true %>" type="text" value="<%= url %>" />

				<div class="form-text form-text-repeat">
					<liferay-ui:message key="enter-individual-urls-for-each-of-your-client-extension-javascript-files" />
				</div>
			</aui:field-wrapper>
		</div>

	<%
	}
	%>

</div>

<div class="lfr-form-rows" id="<portlet:namespace />_cssURLs_field">

	<%
	for (String cssURL : editClientExtensionEntryDisplayContext.getStrings(customElementCET.getCSSURLs())) {
	%>

		<div class="lfr-form-row">
			<aui:field-wrapper cssClass="form-group">
				<aui:input ignoreRequestValue="<%= true %>" label="css-url" name="cssURLs" type="text" value="<%= cssURL %>" />

				<div class="form-text form-text-repeat">
					<liferay-ui:message key="enter-individual-urls-for-each-of-your-client-extension-css-files" />
				</div>
			</aui:field-wrapper>
		</div>

	<%
	}
	%>

</div>

<c:choose>
	<c:when test="<%= editClientExtensionEntryDisplayContext.isAdding() %>">
		<aui:input label="instanceable" name="instanceable" type="checkbox" value="<%= customElementCET.isInstanceable() %>" />
	</c:when>
	<c:otherwise>
		<aui:input disabled="<%= true %>" label="instanceable" name="instanceable-disabled" type="checkbox" value="<%= customElementCET.isInstanceable() %>" />
		<aui:input name="instanceable" type="hidden" value="<%= customElementCET.isInstanceable() %>" />
	</c:otherwise>
</c:choose>

<clay:select
	label="portlet-category-name"
	name="portletCategoryName"
	options="<%= editClientExtensionEntryDisplayContext.getPortletCategoryNameSelectOptions(customElementCET.getPortletCategoryName()) %>"
/>

<aui:field-wrapper cssClass="form-group">
	<aui:input label="friendly-url-mapping" name="friendlyURLMapping" type="text" value="<%= customElementCET.getFriendlyURLMapping() %>" />

	<div class="form-text">
		<liferay-ui:message key="define-the-widgets-friendly-url-mapping-so-you-can-refer-to-it-using-a-more-user-readable-url" />
	</div>
</aui:field-wrapper>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: '#<portlet:namespace />_urls_field',
		minimumRows: 1,
		namespace: '<portlet:namespace />',
	});

	autoFields({
		contentBox: '#<portlet:namespace />_cssURLs_field',
		minimumRows: 1,
		namespace: '<portlet:namespace />',
	});
</aui:script>