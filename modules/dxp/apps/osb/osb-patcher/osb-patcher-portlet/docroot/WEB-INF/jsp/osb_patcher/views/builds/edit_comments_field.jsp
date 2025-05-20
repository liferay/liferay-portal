<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/WEB-INF/jsp/osb_patcher/views/init.jsp" %>

<aui:model-context bean="${patcherBuild}" model="<%= PatcherBuild.class %>" />

<portlet:actionURL var="updatePatcherBuildCommentsFieldURL">
	<portlet:param name="controller" value="builds" />
	<portlet:param name="action" value="updateCommentsField" />
</portlet:actionURL>

<aui:form action="${updatePatcherBuildCommentsFieldURL}" method="post">
	<aui:input name="id" type="hidden" value="${patcherBuild.patcherBuildId}" />

	<aui:input name="comments" />

	<aui:button-row>
		<aui:button type="submit" />

		<aui:button onClick="Liferay.Patcher.closeWindow();" value="cancel" />
	</aui:button-row>
</aui:form>