<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long layoutPageTemplateCollectionId = GetterUtil.getLong(request.getAttribute(LayoutPageTemplateAdminWebKeys.LAYOUT_PAGE_TEMPLATE_COLLECTION_ID));

LayoutPageTemplateCollection layoutPageTemplateCollection = LayoutPageTemplateCollectionLocalServiceUtil.fetchLayoutPageTemplateCollection(layoutPageTemplateCollectionId);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle((layoutPageTemplateCollection != null) ? layoutPageTemplateCollection.getName() : LanguageUtil.get(request, "add-page-template-set"));
%>

<portlet:actionURL name="/layout_page_template_admin/edit_layout_page_template_collection" var="editLayoutPageTemplateCollectionURL">
	<portlet:param name="mvcRenderCommandName" value="/layout_page_template_admin/edit_layout_page_template_collection" />
	<portlet:param name="tabs1" value='<%= ParamUtil.getString(request, "tabs1", "page-templates") %>' />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editLayoutPageTemplateCollectionURL %>"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="layoutPageTemplateCollectionId" type="hidden" value="<%= layoutPageTemplateCollectionId %>" />

	<liferay-frontend:edit-form-body>
		<liferay-ui:error exception="<%= DuplicateLayoutPageTemplateCollectionException.class %>" message="please-enter-a-unique-name" />
		<liferay-ui:error exception="<%= LayoutPageTemplateCollectionNameException.class %>" message="please-enter-a-valid-name" />

		<aui:model-context bean="<%= layoutPageTemplateCollection %>" model="<%= LayoutPageTemplateCollection.class %>" />

		<liferay-frontend:fieldset>
			<aui:input label="name" name="name" placeholder="name" />

			<aui:input name="description" placeholder="description" />
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>