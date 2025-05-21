<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

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