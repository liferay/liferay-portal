<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<aui:style type="text/css">
	#impersonate-user-icon {
		color: #272833;
	}

	#impersonate-user-icon .lexicon-icon {
		margin-top: -.125rem;
	}

	#impersonate-user-sticker {
		bottom: -.4rem;
		color: #000;
		font-size: .6rem;
		height: 1.2rem;
		right: -0.4rem;
		width: 1.2rem;
	}
</aui:style>

<li class="control-menu-nav-item">
	<span class="user-avatar-link">
		<liferay-product-navigation:personal-menu
			user="<%= user %>"
		/>

		<%
		int notificationsCount = GetterUtil.getInteger(request.getAttribute(PersonalMenuWebKeys.NOTIFICATIONS_COUNT));
		%>

		<c:if test="<%= notificationsCount > 0 %>">

			<%
			String notificationsURL = PersonalApplicationURLUtil.getPersonalApplicationURL(request, PortletProviderUtil.getPortletId(UserNotificationEvent.class.getName(), PortletProvider.Action.VIEW));
			%>

			<a aria-label="<%= notificationsCount + StringPool.SPACE + LanguageUtil.get(request, "new-notification") %>" class="panel-notifications-count" href="<%= (notificationsURL != null) ? notificationsURL : null %>" title="<%= notificationsCount + StringPool.SPACE + LanguageUtil.get(request, "new-notification") %>">
				<clay:badge
					displayType="danger"
					label="<%= String.valueOf(notificationsCount) %>"
				/>
			</a>
		</c:if>
	</span>
</li>