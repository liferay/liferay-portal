<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionDisplayLayoutDisplayContext cpDefinitionDisplayLayoutDisplayContext = (CPDefinitionDisplayLayoutDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDisplayLayout cpDisplayLayout = cpDefinitionDisplayLayoutDisplayContext.getCPDisplayLayout();

String layoutPageTemplateEntryName = cpDefinitionDisplayLayoutDisplayContext.getLayoutPageTemplateEntryName(cpDisplayLayout);
%>

<aui:input id="pagesContainerInput" ignoreRequestValue="<%= true %>" name="layoutPageTemplateEntryUuid" type="hidden" value="<%= (cpDisplayLayout == null) ? StringPool.BLANK : cpDisplayLayout.getLayoutPageTemplateEntryUuid() %>" />

<aui:field-wrapper cssClass="mt-3" helpMessage="product-display-page-help" label="product-display-page">
	<p class="text-default">
		<span class="<%= Validator.isNull(layoutPageTemplateEntryName) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />displayPageItemRemove" role="button">
			<clay:button
				aria-label='<%= LanguageUtil.format(locale, "remove-x", "product-display-page") %>'
				cssClass="lfr-portal-tooltip"
				displayType="unstyled"
				icon="times"
				title="remove"
			/>
		</span>
		<span id="<portlet:namespace />displayPageNameInput">
			<c:choose>
				<c:when test="<%= Validator.isNull(layoutPageTemplateEntryName) %>">
					<span class="text-muted"><liferay-ui:message key="none" /></span>
				</c:when>
				<c:otherwise>
					<%= HtmlUtil.escape(layoutPageTemplateEntryName) %>
				</c:otherwise>
			</c:choose>
		</span>
	</p>
</aui:field-wrapper>

<aui:button name="chooseLayoutPageTemplateEntry" value="choose" />