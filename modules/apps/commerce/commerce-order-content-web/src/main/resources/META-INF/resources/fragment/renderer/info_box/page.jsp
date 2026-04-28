<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/info_box/init.jsp" %>

<react:component
	module="{InfoBox} from commerce-order-content-web"
	props='<%=
		HashMapBuilder.<String, Object>put(
			"additionalProps", additionalProps
		).put(
			"buttonDisplayType", buttonStyle
		).put(
			"elementId", uuid
		).put(
			"field", field
		).put(
			"fieldValue", fieldValue
		).put(
			"fieldValueType", fieldValueType
		).put(
			"hasManageOrderNotesPermission", hasManageOrderNotesPermission
		).put(
			"hasManageOrderRestrictedNotesPermission", hasManageOrderRestrictedNotesPermission
		).put(
			"hasUpdatePermission", hasUpdatePermission
		).put(
			"hasViewPermission", hasViewPermission
		).put(
			"isOpen", open
		).put(
			"label", label
		).put(
			"namespace", namespace
		).put(
			"orderId", commerceOrderId
		).put(
			"readOnly", readOnly
		).put(
			"spritemap", themeDisplay.getPathThemeSpritemap()
		).build()
	%>'
/>