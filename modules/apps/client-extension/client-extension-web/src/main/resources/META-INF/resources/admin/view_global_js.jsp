<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
ViewClientExtensionEntryDisplayContext viewClientExtensionEntryDisplayContext = (ViewClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.VIEW_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

Collection<Method> methods = viewClientExtensionEntryDisplayContext.getMethods();

for (Method method : methods) {
	CETProperty cetProperty = method.getAnnotation(CETProperty.class);
	String label = viewClientExtensionEntryDisplayContext.getLabel(method);
	String name = cetProperty.name();
	Object value = viewClientExtensionEntryDisplayContext.getValue(method);
%>

	<c:choose>
		<c:when test='<%= name.equals("scriptElementAttributesJSON") %>'>
			<aui:field-wrapper cssClass="form-group">
				<react:component
					module="{ScriptElementAttributesFormField} from client-extension-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"disabled", true
						).put(
							"scriptElementAttributesJSON", value
						).build()
					%>'
				/>
			</aui:field-wrapper>
		</c:when>
		<c:when test="<%= cetProperty.type() == CETProperty.Type.Boolean %>">
			<aui:input disabled="<%= true %>" label="<%= label %>" name="<%= label %>" type="checkbox" value="<%= value %>" />
		</c:when>
		<c:when test="<%= (cetProperty.type() == CETProperty.Type.StringList) || (cetProperty.type() == CETProperty.Type.URLList) %>">
			<aui:input disabled="<%= true %>" label="<%= label %>" name="<%= label %>" type="textarea" value="<%= value %>" />
		</c:when>
		<c:otherwise>
			<aui:input disabled="<%= true %>" label="<%= label %>" name="<%= label %>" type="text" value="<%= value %>" />
		</c:otherwise>
	</c:choose>

<%
}
%>