<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<aui:script>
	Liferay.Util.setPortletConfigurationIconAction(
		'<portlet:namespace />import',
		() => {
			Liferay.Util.openModal({
				onClose: function (event) {
					window.location.reload();
				},
				title: '<liferay-ui:message key="import" />',
				url: '<%=
					PortletURLBuilder.create(
						PortalUtil.getControlPanelPortletURL(liferayPortletRequest, LayoutAdminPortletKeys.GROUP_PAGES, PortletRequest.RENDER_PHASE)
					).setMVCPath(
						"/view_import.jsp"
					).setWindowState(
						LiferayWindowState.POP_UP
					).buildString()
			%>',
			});
		}
	);
</aui:script>