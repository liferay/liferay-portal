<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	PortletURL portletURL = renderResponse.createRenderURL();

	redirect = portletURL.toString();
}

LayoutSetPrototype layoutSetPrototype = null;

long layoutSetPrototypeId = ParamUtil.getLong(request, "layoutSetPrototypeId");

if (layoutSetPrototypeId > 0) {
	layoutSetPrototype = LayoutSetPrototypeServiceUtil.fetchLayoutSetPrototype(layoutSetPrototypeId);
}
else {
	Group group = themeDisplay.getScopeGroup();

	if (group.isLayoutSetPrototype()) {
		layoutSetPrototype = LayoutSetPrototypeLocalServiceUtil.fetchLayoutSetPrototype(group.getClassPK());

		layoutSetPrototypeId = layoutSetPrototype.getLayoutSetPrototypeId();
	}
}

if (layoutSetPrototype == null) {
	layoutSetPrototype = new LayoutSetPrototypeImpl();

	layoutSetPrototype.setNew(true);
	layoutSetPrototype.setActive(true);
}

boolean layoutsUpdateable = GetterUtil.getBoolean(layoutSetPrototype.getSettingsProperty("layoutsUpdateable"), true);

Group group = themeDisplay.getSiteGroup();

if (!group.isLayoutSetPrototype()) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);
	portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

	renderResponse.setTitle(layoutSetPrototype.isNew() ? LanguageUtil.get(request, "new-site-template") : layoutSetPrototype.getName(locale));
}

request.setAttribute("edit_layout_set_prototype.jsp-layoutSetPrototype", layoutSetPrototype);
request.setAttribute("edit_layout_set_prototype.jsp-redirect", currentURL);
%>

<liferay-ui:success key='<%= LayoutSetPrototypePortletKeys.SITE_TEMPLATE_SETTINGS + "requestProcessed" %>' message="site-template-was-added" />

<liferay-util:include page="/merge_alert.jsp" servletContext="<%= application %>" />

<portlet:actionURL name="updateLayoutSetPrototype" var="updateLayoutSetPrototypeURL" />

<liferay-frontend:edit-form
	action="<%= updateLayoutSetPrototypeURL %>"
	method="post"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="layoutSetPrototypeId" type="hidden" value="<%= layoutSetPrototypeId %>" />

	<aui:model-context bean="<%= layoutSetPrototype %>" model="<%= LayoutSetPrototype.class %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset>
			<aui:input name="name" placeholder="name" />

			<aui:input name="description" placeholder="description" />

			<aui:input helpMessage="allow-site-administrators-to-create-sites-from-this-site-template" inlineLabel="right" label="active" labelCssClass="simple-toggle-switch" name="active" type="toggle-switch" value="<%= layoutSetPrototype.isActive() %>" />

			<aui:input helpMessage="allow-site-administrators-to-modify-pages-associated-with-this-site-template-help" inlineLabel="right" label="allow-site-administrators-to-modify-pages-associated-with-this-site-template" labelCssClass="simple-toggle-switch" name="layoutsUpdateable" type="toggle-switch" value="<%= layoutsUpdateable %>" />

			<%
			Set<String> servletContextNames = CustomJspRegistryUtil.getServletContextNames();

			String customJspServletContextName = StringPool.BLANK;

			if (layoutSetPrototype != null) {
				UnicodeProperties settingsUnicodeProperties = layoutSetPrototype.getSettingsProperties();

				customJspServletContextName = GetterUtil.getString(settingsUnicodeProperties.get("customJspServletContextName"));
			}
			%>

			<c:if test="<%= !servletContextNames.isEmpty() %>">
				<aui:select label="application-adapter" name="customJspServletContextName">
					<aui:option label="none" />

					<%
					for (String servletContextName : servletContextNames) {
					%>

						<aui:option selected="<%= customJspServletContextName.equals(servletContextName) %>" value="<%= servletContextName %>"><%= CustomJspRegistryUtil.getDisplayName(servletContextName) %></aui:option>

					<%
					}
					%>

				</aui:select>
			</c:if>
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<%
if (!layoutSetPrototype.isNew()) {
	PortalUtil.addPortletBreadcrumbEntry(request, layoutSetPrototype.getName(locale), null);
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-page"), currentURL);
}
%>