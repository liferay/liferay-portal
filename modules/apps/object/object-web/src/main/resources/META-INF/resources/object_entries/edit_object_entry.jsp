<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String externalReferenceCode = ParamUtil.getString(request, "externalReferenceCode");

ObjectEntryDisplayContext objectEntryDisplayContext = (ObjectEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

ObjectDefinition objectDefinition = objectEntryDisplayContext.getObjectDefinition1();
ObjectEntry objectEntry = objectEntryDisplayContext.getObjectEntry();
ObjectRelationship objectRelationship = objectEntryDisplayContext.getObjectRelationship();
%>

<c:choose>
	<c:when test="<%= !Validator.isBlank(externalReferenceCode) && (objectEntry == null) %>">
		<clay:alert
			displayType="danger"
			message="the-object-entry-could-not-be-found"
		/>
	</c:when>
	<c:otherwise>
		<c:if test="<%= objectEntryDisplayContext.isShowScreenNavigation() %>">
			<liferay-frontend:screen-navigation
				key="<%= objectDefinition.getClassName() %>"
				navBarCssClass="container-fluid-max-xxxl"
				portletURL="<%= currentURLObj %>"
			/>
		</c:if>

		<c:choose>
			<c:when test="<%= objectRelationship != null %>">
				<liferay-util:include page="/object_entries/object_entry/relationship.jsp" servletContext="<%= application %>">
					<liferay-util:param name="externalReferenceCode" value="<%= externalReferenceCode %>" />
				</liferay-util:include>
			</c:when>
			<c:when test="<%= objectEntryDisplayContext.isShowObjectEntryForm() %>">
				<liferay-util:include page="/object_entries/object_entry/form.jsp" servletContext="<%= application %>">
					<liferay-util:param name="externalReferenceCode" value="<%= externalReferenceCode %>" />
				</liferay-util:include>
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>