<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String ppid = ParamUtil.getString(request, "p_p_id");
%>

<liferay-ui:success key="displayPagePublished" message="the-display-page-template-was-published-successfully" />

<c:choose>
	<c:when test="<%= (themeDisplay.isStatePopUp() || themeDisplay.isWidget()) && Validator.isNotNull(ppid) %>">

		<%
		String templateContent = LayoutTemplateLocalServiceUtil.getContent("pop_up", true, theme.getThemeId());

		if (Validator.isNotNull(templateContent)) {
			String templateId = theme.getThemeId() + LayoutTemplateConstants.STANDARD_SEPARATOR + "pop_up";

			RuntimePageUtil.processTemplate(request, response, ppid, templateId, templateContent, LayoutTemplateLocalServiceUtil.getLangType("pop_up", true, theme.getThemeId()));
		}
		%>

	</c:when>
	<c:when test="<%= layoutTypePortlet.hasStateMax() && Validator.isNotNull(ppid) %>">
		<liferay-layout:render-state-max-layout-structure />
	</c:when>
	<c:otherwise>

		<%
		DisplayPageLayoutTypeControllerDisplayContext displayPageLayoutTypeControllerDisplayContext = (DisplayPageLayoutTypeControllerDisplayContext)request.getAttribute(DisplayPageLayoutTypeControllerWebKeys.DISPLAY_PAGE_LAYOUT_TYPE_CONTROLLER_DISPLAY_CONTEXT);

		AssetRendererFactory<?> assetRendererFactory = displayPageLayoutTypeControllerDisplayContext.getAssetRendererFactory();
		%>

		<c:if test="<%= assetRendererFactory != null %>">
			<liferay-ui:success key='<%= assetRendererFactory.getPortletId() + "requestProcessed" %>' message="your-request-processed-successfully" />
		</c:if>

		<c:choose>
			<c:when test="<%= displayPageLayoutTypeControllerDisplayContext.isForbidden(response) %>">
				<div class="layout-content" id="main-content" role="main">
					<clay:container-fluid
						cssClass="pt-3"
					>
						<clay:alert
							displayType="danger"
							message="you-do-not-have-the-required-permissions-to-view-the-content-of-this-page"
						/>
					</clay:container-fluid>
				</div>
			</c:when>
			<c:when test="<%= layout.getMasterLayoutPlid() > 0 %>">
				<div id="master-layout-wrapper">
					<liferay-layout:render-fragment-layout
						showPreview="<%= true %>"
					/>
				</div>
			</c:when>
			<c:otherwise>
				<div class="layout-content portlet-layout" id="main-content" role="main">
					<liferay-layout:render-fragment-layout />
				</div>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<liferay-layout:layout-common
	displaySessionMessages="<%= true %>"
/>