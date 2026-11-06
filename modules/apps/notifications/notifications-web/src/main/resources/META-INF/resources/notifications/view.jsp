<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

String navigation = ParamUtil.getString(request, "navigation", "all");

boolean actionRequired = ParamUtil.getBoolean(request, "actionRequired");

if (actionRequired) {
	navigation = "unread";
}

SearchContainer<UserNotificationEvent> notificationsSearchContainer = new SearchContainer(renderRequest, currentURLObj, null, actionRequired ? "you-do-not-have-any-requests" : "you-do-not-have-any-notifications");

String searchContainerId = "userNotificationEvents";

if (actionRequired) {
	searchContainerId = "actionableUserNotificationEvents";
}

notificationsSearchContainer.setId(searchContainerId);

NotificationsManagementToolbarDisplayContext notificationsManagementToolbarDisplayContext = new NotificationsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, currentURLObj);

NotificationsUtil.populateResults(themeDisplay.getUserId(), actionRequired, navigation, notificationsManagementToolbarDisplayContext.getOrderByType(), notificationsSearchContainer);

if (Validator.isNotNull(backURL)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);
}
%>

<%@ include file="/notifications/view-ext-pre.jsp" %>

<clay:navigation-bar
	inverted="<%= layout.isTypeControlPanel() %>"
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(!actionRequired);
						navigationItem.setHref(renderResponse.createRenderURL(), "actionRequired", StringPool.FALSE, "backURL", currentURL);
						navigationItem.setLabel(LanguageUtil.format(httpServletRequest, "notifications-list-x", UserNotificationEventLocalServiceUtil.getDeliveredUserNotificationEventsCount(themeDisplay.getUserId(), UserNotificationDeliveryConstants.TYPE_WEBSITE, true, false)));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(actionRequired);
						navigationItem.setHref(renderResponse.createRenderURL(), "actionRequired", StringPool.TRUE, "backURL", currentURL);
						navigationItem.setLabel(LanguageUtil.format(httpServletRequest, "requests-list-x", String.valueOf(UserNotificationEventLocalServiceUtil.getArchivedUserNotificationEventsCount(themeDisplay.getUserId(), UserNotificationDeliveryConstants.TYPE_WEBSITE, true, true, false))));
					});
			}
		}
	%>'
/>

<portlet:actionURL name="deleteNotifications" var="deleteNotificationsURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<portlet:actionURL name="markNotificationsAsRead" var="markNotificationsAsReadURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<portlet:actionURL name="markNotificationsAsUnread" var="markNotificationsAsUnreadURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<clay:management-toolbar
	actionDropdownItems="<%= notificationsManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"deleteNotificationsURL", deleteNotificationsURL.toString()
		).put(
			"markNotificationsAsReadURL", markNotificationsAsReadURL.toString()
		).put(
			"markNotificationsAsUnreadURL", markNotificationsAsUnreadURL.toString()
		).put(
			"searchContainerId", searchContainerId
		).build()
	%>'
	clearResultsURL="<%= notificationsManagementToolbarDisplayContext.getClearResultsURL() %>"
	disabled="<%= NotificationsUtil.getAllNotificationsCount(themeDisplay.getUserId(), actionRequired) == 0 %>"
	filterDropdownItems="<%= notificationsManagementToolbarDisplayContext.getFilterDropdownItems() %>"
	filterLabelItems="<%= notificationsManagementToolbarDisplayContext.getFilterLabelItems() %>"
	itemsTotal="<%= notificationsSearchContainer.getTotal() %>"
	orderDropdownItems="<%= notificationsManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
	propsTransformer="{NotificationsManagementToolbarPropsTransformer} from notifications-web"
	searchContainerId="<%= searchContainerId %>"
	selectable="<%= actionRequired ? false : true %>"
	showCreationMenu="<%= false %>"
	showInfoButton="<%= false %>"
	showSearch="<%= false %>"
	sortingOrder="<%= notificationsManagementToolbarDisplayContext.getOrderByType() %>"
	sortingURL="<%= String.valueOf(notificationsManagementToolbarDisplayContext.getSortingURL()) %>"
/>

<clay:container-fluid>
	<aui:form action="<%= currentURL %>" method="get" name="fm">
		<aui:input name="selectedEntryIds" type="hidden" />
		<aui:input name="selectAll" type="hidden" value="<%= false %>" />

		<div class="user-notifications">
			<liferay-ui:search-container
				rowChecker="<%= actionRequired ? null : new UserNotificationEventRowChecker(renderResponse) %>"
				searchContainer="<%= notificationsSearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.UserNotificationEvent"
					keyPropertyName="userNotificationEventId"
					modelVar="userNotificationEvent"
				>

					<%
					UserNotificationFeedEntry userNotificationFeedEntry = UserNotificationManagerUtil.interpret(StringPool.BLANK, userNotificationEvent, ServiceContextFactory.getInstance(request));

					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", StringUtil.merge(notificationsManagementToolbarDisplayContext.getAvailableActions(userNotificationEvent, userNotificationFeedEntry))
						).put(
							"userNotificationFeedEntry", userNotificationFeedEntry
						).build());
					%>

					<%@ include file="/notifications/user_notification_entry.jspf" %>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="descriptive"
					markupView="lexicon"
					searchResultCssClass="list-group list-group-notification"
				/>
			</liferay-ui:search-container>
		</div>
	</aui:form>
</clay:container-fluid>

<aui:script use="aui-base">
	var form = A.one('#<portlet:namespace />fm');

	form.delegate(
		'click',
		(event) => {
			event.preventDefault();

			var currentTarget = event.currentTarget;

			Liferay.Util.fetch(currentTarget.attr('href'), {
				method: 'POST',
			})
				.then((response) => {
					return response.json();
				})
				.then((response) => {
					if (response.success) {
						var notificationContainer =
							currentTarget.ancestor('li.list-group-item');

						if (notificationContainer) {
							var markAsReadURL = notificationContainer
								.one('a')
								.attr('href');

							form.attr('method', 'post');

							submitForm(form, markAsReadURL);

							notificationContainer.remove();
						}

						if (currentTarget.siblings()) {
							currentTarget.siblings().remove();
						}

						currentTarget.remove();
					}
					else {
						Liferay.Util.openToast({
							message:
								'<liferay-ui:message key="an-unexpected-error-occurred" />',
							toastProps: {
								autoClose: 5000,
							},
							type: 'warning',
						});
					}
				});
		},
		'.user-notification-action'
	);
</aui:script>

<%@ include file="/notifications/view-ext-post.jsp" %>