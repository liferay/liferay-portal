<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:renderURL var="viewURL">
	<portlet:param name="mvcPath" value="/view.jsp" />
</portlet:renderURL>

<a class="bg-light d-inline-flex my-2 p-2 rounded text-decoration-none" href="<%= viewURL %>">Back</a>

<%
PortletRequest portletRequest = (PortletRequest)request.getAttribute(JavaConstants.JAKARTA_PORTLET_REQUEST);

if (portletRequest != null) {
	portletRequest.setAttribute("aui:form:portletNamespace", "<\u002fscript><scrIpt>alert(12451);<\u002fscRipt><script>");
}
%>

<liferay-util:buffer
	var="contents"
>
	<h1>XSS Editor</h1>

	<p>
		Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nunc id cursus metus aliquam eleifend mi in nulla. Quam adipiscing vitae proin sagittis nisl rhoncus. Suspendisse faucibus interdum posuere lorem. Nullam ac tortor vitae purus faucibus ornare. Ac felis donec et odio pellentesque diam. Nulla at volutpat diam ut. Posuere urna nec tincidunt praesent semper feugiat nibh. Gravida quis blandit turpis cursus. Proin libero nunc consequat interdum varius. Sollicitudin ac orci phasellus egestas tellus rutrum tellus pellentesque. Neque volutpat ac tincidunt vitae semper quis lectus nulla at. Odio euismod lacinia at quis risus sed vulputate odio ut. Augue lacus viverra vitae congue eu consequat ac. Elementum sagittis vitae et leo duis ut diam. Diam quis enim lobortis scelerisque fermentum dui faucibus.
	</p>
</liferay-util:buffer>

<liferay-editor:editor
	contents="<%= contents %>"
	editorName="ckeditor"
	name="sampleXSSEditor"
	placeholder="content"
/>

<%
if (portletRequest != null) {
	portletRequest.removeAttribute("aui:form:portletNamespace");
}
%>