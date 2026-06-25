<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/share/init.jsp" %>

<react:component
	module="{RoomShareButton} from site-dsr-site-initializer"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"canAssignAllRoles", canAssignAllRoles
		).put(
			"readOnly", readOnly
		).put(
			"roomId", roomId
		).build()
	%>'
/>