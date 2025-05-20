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

<c:forEach items="${StringUtil.split(tickets)}" var="token" varStatus="tokenStatus">
	<c:choose>
		<c:when test="${PatcherFixPackUtil.containsPatcherFixPackName(token)}">
			<c:set value="${PatcherFixPackUtil.getPatcherFixPack(token, GetterUtil.getLong(patcherProjectVersionId))}" var="patcherFixPack" />

			<portlet:renderURL var="viewPatcherFixPackURL">
				<portlet:param name="controller" value="fix_packs" />
				<portlet:param name="action" value="view" />
				<portlet:param name="id" value="${patcherFixPack.patcherFixPackId}" />
			</portlet:renderURL>

			<a class="nobr" href="${viewPatcherFixPackURL}">${token}</a>${(!tokenStatus.last) ? StringPool.COMMA : StringPool.BLANK}
		</c:when>
		<c:otherwise>
			<a class="nobr" href="${PortletPropsValues.JIRA_URL}/${token}" target="_blank">${token}</a>${(!tokenStatus.last) ? StringPool.COMMA : StringPool.BLANK}
		</c:otherwise>
	</c:choose>
</c:forEach>