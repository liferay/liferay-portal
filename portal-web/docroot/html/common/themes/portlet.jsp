<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/common/themes/init.jsp" %>

<portlet:defineObjects />

<%
String tilesPortletContent = GetterUtil.getString(request.getAttribute(WebKeys.PORTLET_CONTENT_JSP));

Portlet portlet = (Portlet)request.getAttribute(WebKeys.RENDER_PORTLET);

LiferayRenderResponse liferayRenderResponse = (LiferayRenderResponse)LiferayPortletUtil.getLiferayPortletResponse(renderResponse);

// Portlet title

String portletTitle = PortletConfigurationUtil.getPortletTitle(portletDisplay.getId(), portletDisplay.getPortletPreferences(), themeDisplay.getLanguageId());

if (portletDisplay.isActive() && Validator.isNull(portletTitle)) {
	portletTitle = liferayRenderResponse.getTitle();
}

if (Validator.isNull(portletTitle)) {
	portletTitle = PortalUtil.getPortletTitle(portlet, application, locale);
}

portletDisplay.setTitle(portletTitle);

// Portlet description

if (Validator.isNull(portletDisplay.getDescription())) {
	portletDisplay.setDescription(PortalUtil.getPortletDescription(portlet, application, locale));
}

Group group = layout.getGroup();
%>

<c:choose>
	<c:when test="<%= themeDisplay.isStateExclusive() %>">
		<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
	</c:when>
	<c:when test="<%= themeDisplay.isStatePopUp() %>">
		<div class="portlet-body">
			<c:if test='<%= !tilesPortletContent.endsWith("/error.jsp") %>'>
				<liferay-theme:portlet-messages
					group="<%= group %>"
					portlet="<%= portlet %>"
				/>
			</c:if>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(tilesPortletContent) %>">
					<liferay-util:include page="<%= StrutsUtil.TEXT_HTML_DIR + tilesPortletContent %>" />
				</c:when>
				<c:otherwise>

					<%
					JspWriter jspWriter = pageContext.getOut();

					jspWriter.print(renderRequest.getAttribute(WebKeys.PORTLET_CONTENT));
					%>

				</c:otherwise>
			</c:choose>
		</div>
	</c:when>
	<c:otherwise>
		<liferay-theme:wrap-portlet
			page="portlet.jsp"
		>
			<div class="<%= portletDisplay.isStateMin() ? "hide" : "" %> portlet-content-container">
				<%@ include file="/html/common/themes/portlet_content_wrapper.jspf" %>
			</div>
		</liferay-theme:wrap-portlet>
	</c:otherwise>
</c:choose>