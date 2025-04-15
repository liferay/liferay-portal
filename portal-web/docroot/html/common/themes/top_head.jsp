<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/html/common/themes/init.jsp" %>
<%@ include file="/html/common/themes/top_meta.jspf" %>
<%@ include file="/html/common/themes/top_meta-ext.jsp" %>

<liferay-util:dynamic-include key="/html/common/themes/top_head.jsp#pre" />

<link href="<%= themeDisplay.getFaviconURL() %>" rel="apple-touch-icon" />
<link href="<%= themeDisplay.getFaviconURL() %>" rel="icon" />

<%-- Portal CSS --%>

<aui:link cssClass="lfr-css-file" href="<%= HtmlUtil.escapeAttribute(themeDisplay.getClayCSSURL()) %>" id="liferayAUICSS" rel="stylesheet" senna="temporary" type="text/css" />

<%
PortletTreeSet portletTreeSet = null;

if (layoutTypePortlet != null) {
	portletTreeSet = new PortletTreeSet(layoutTypePortlet.getAllPortlets());
}

if (layout != null) {
	String ppid = ParamUtil.getString(request, "p_p_id");

	if (layout.isTypeAssetDisplay() || layout.isTypeContent()) {
		List<com.liferay.portal.kernel.model.PortletPreferences> portletPreferencesList = PortletPreferencesLocalServiceUtil.getPortletPreferences(PortletKeys.PREFS_OWNER_ID_DEFAULT, PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid());

		for (com.liferay.portal.kernel.model.PortletPreferences portletPreferences : portletPreferencesList) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletPreferences.getPortletId());

			if ((portlet == null) || !portlet.isActive() || portlet.isUndeployedPortlet()) {
				continue;
			}

			portletTreeSet.add(portlet);
		}
	}
	else if ((layout.isTypeEmbedded() || layout.isTypePortlet()) && (themeDisplay.isStateMaximized() || themeDisplay.isStatePopUp() || (layout.isSystem() && Objects.equals(layout.getFriendlyURL(), PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL)))) {
		if (Validator.isNotNull(ppid)) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), ppid);

			if ((portlet != null) && !portletTreeSet.contains(portlet)) {
				portletTreeSet.add(portlet);
			}
		}
	}
	else if (layout.isTypeControlPanel() || layout.isTypePanel()) {
		portletTreeSet = new PortletTreeSet(layout.getEmbeddedPortlets());

		if (Validator.isNotNull(ppid)) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), ppid);

			if ((portlet != null) && !portletTreeSet.contains(portlet)) {
				portletTreeSet.add(portlet);
			}
		}
	}

	String portletResource = ParamUtil.getString(request, PortalUtil.getPortletNamespace(ppid) + "portletResource");

	if (Validator.isNotNull(portletResource)) {
		Portlet portlet = PortletLocalServiceUtil.getPortletById(company.getCompanyId(), portletResource);

		if ((portlet != null) && !portletTreeSet.contains(portlet)) {
			portletTreeSet.add(portlet);
		}
	}

	Iterator<Portlet> portletsIterator = portletTreeSet.iterator();

	LayoutTypeAccessPolicy layoutTypeAccessPolicy = LayoutTypeAccessPolicyTracker.getLayoutTypeAccessPolicy(layout);

	while (portletsIterator.hasNext()) {
		Portlet portlet = portletsIterator.next();

		try {
			layoutTypeAccessPolicy.checkAccessAllowedToPortlet(request, layout, portlet);
		}
		catch (PrincipalException pe) {
			portletsIterator.remove();
		}
	}

	request.setAttribute(WebKeys.LAYOUT_PORTLETS, portletTreeSet);
}
%>

<%-- Portlet CSS References --%>

<%@ include file="/html/common/themes/top_portlet_resources_css.jspf" %>

<%-- Portal JavaScript References --%>

<%@ include file="/html/common/themes/top_js.jspf" %>
<%@ include file="/html/common/themes/top_js-ext.jspf" %>

<%-- Portlet JavaScript References --%>

<%@ include file="/html/common/themes/top_portlet_resources_js.jspf" %>

<%-- Raw Text --%>

<%
List<String> markupHeaders = (List<String>)request.getAttribute(MimeResponse.MARKUP_HEAD_ELEMENT);
%>

<c:if test="<%= markupHeaders != null %>">

	<%
	for (String markupHeader : markupHeaders) {
	%>

		<%= markupHeader %>

	<%
	}
	%>

</c:if>

<%
com.liferay.petra.string.StringBundler pageTopSB = OutputTag.getDataSB(request, WebKeys.PAGE_TOP);
%>

<c:if test="<%= pageTopSB != null %>">

	<%
	pageTopSB.writeTo(out);
	%>

</c:if>

<%
boolean portletHubRequired = false;

for (Portlet portlet : portletTreeSet) {
	for (PortletDependency portletDependency : portlet.getPortletDependencies()) {
		if (Objects.equals(portletDependency.getName(), "PortletHub") && Objects.equals(portletDependency.getScope(), "jakarta.portlet")) {
			portletHubRequired = true;

			break;
		}
	}

	if (portletHubRequired) {
		break;
	}
}
%>

<c:if test="<%= portletHubRequired %>">
	<aui:script type="text/javascript">
		var portlet = portlet || {};

		portlet.data = portlet.data || {};

		portlet.data.pageRenderState = <%= RenderStateUtil.generateJSON(request, themeDisplay) %>;
	</aui:script>
</c:if>

<%-- Theme CSS --%>

<aui:link cssClass="lfr-css-file" href="<%= HtmlUtil.escapeAttribute(themeDisplay.getMainCSSURL()) %>" id="liferayThemeCSS" rel="stylesheet" senna="temporary" type="text/css" />

<%-- User Inputted Layout CSS --%>

<c:if test="<%= (layout != null) && Validator.isNotNull(layout.getCssText()) %>">
	<aui:style senna="temporary" type="text/css">
		<%= _escapeCssBlock(layout.getCssText()) %>
	</aui:style>
</c:if>

<%-- User Inputted Portlet CSS --%>

<c:if test="<%= portletTreeSet != null %>">
	<aui:style senna="temporary" type="text/css">

		<%
		for (Portlet portlet : portletTreeSet) {
			PortletPreferences portletPreferences = themeDisplay.getStrictLayoutPortletSetup(layout, portlet.getPortletId());

			String portletSetupCss = portletPreferences.getValue("portletSetupCss", StringPool.BLANK);
		%>

			<c:if test="<%= Validator.isNotNull(portletSetupCss) %>">

				<%
				try {
				%>

					<%@ include file="/html/common/themes/portlet_css.jspf" %>

				<%
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(e.getMessage());
					}
				}
				%>

			</c:if>

		<%
		}
		%>

	</aui:style>
</c:if>

<liferay-util:dynamic-include key="/html/common/themes/top_head.jsp#post" />

<%!
private String _escapeCssBlock(String css) {
	return StringUtil.replace(css, new String[] {"<", "expression("}, new String[] {"\\3c", ""});
}

private static final Log _log = LogFactoryUtil.getLog("portal_web.docroot.html.common.themes.top_head_jsp");
%>