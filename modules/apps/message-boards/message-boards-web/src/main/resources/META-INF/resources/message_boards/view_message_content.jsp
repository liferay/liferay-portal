<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/message_boards/init.jsp" %>

<%
MBMessageDisplay messageDisplay = (MBMessageDisplay)request.getAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE_DISPLAY);

MBTreeWalker mbTreeWalker = messageDisplay.getTreeWalker();

MBMessage rootMessage = mbTreeWalker.getRoot();

MBMessage message = messageDisplay.getMessage();

MBCategory category = messageDisplay.getCategory();

MBThread thread = messageDisplay.getThread();

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation"));

if (portletTitleBasedNavigation) {
	String redirect = ParamUtil.getString(request, "redirect");

	String backURL = redirect;

	if (Validator.isNull(redirect)) {
		PortletURL backPortletURL = renderResponse.createRenderURL();

		if ((category == null) || (category.getCategoryId() == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID)) {
			backPortletURL.setParameter("mvcRenderCommandName", "/message_boards/view");
		}
		else {
			backPortletURL.setParameter("mvcRenderCommandName", "/message_boards/view_category");
			backPortletURL.setParameter("mbCategoryId", String.valueOf(category.getCategoryId()));
		}

		backURL = backPortletURL.toString();
	}

	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(backURL);

	renderResponse.setTitle(message.getSubject());
}
%>

<c:if test="<%= !portletTitleBasedNavigation %>">
	<clay:content-row
		floatElements="end"
	>
		<clay:content-col
			expand="<%= true %>"
		>
			<h3 class="component-title"><%= HtmlUtil.escape(message.getSubject()) %></h3>
		</clay:content-col>

		<clay:content-col>
			<div class="btn-group">
				<liferay-ui:icon-menu
					direction="left-side"
					icon="<%= StringPool.BLANK %>"
					markupView="lexicon"
					message="actions"
					showWhenSingleIcon="<%= true %>"
				>
					<c:if test="<%= !thread.isLocked() && !thread.isInTrash() && MBMessagePermission.contains(permissionChecker, rootMessage, ActionKeys.PERMISSIONS) %>">
						<liferay-security:permissionsURL
							modelResource="<%= MBMessage.class.getName() %>"
							modelResourceDescription="<%= rootMessage.getSubject() %>"
							resourcePrimKey="<%= String.valueOf(thread.getRootMessageId()) %>"
							var="permissionsURL"
							windowState="<%= LiferayWindowState.POP_UP.toString() %>"
						/>

						<liferay-ui:icon
							message="permissions"
							method="get"
							url="<%= permissionsURL %>"
							useDialog="<%= true %>"
						/>
					</c:if>

					<c:if test="<%= enableRSS && !thread.isInTrash() && MBMessagePermission.contains(permissionChecker, rootMessage, ActionKeys.VIEW) %>">
						<liferay-rss:rss
							delta="<%= rssDelta %>"
							displayStyle="<%= rssDisplayStyle %>"
							feedType="<%= rssFeedType %>"
							url="<%= MBRSSUtil.getRSSURL(plid, 0, rootMessage.getThreadId(), 0, themeDisplay) %>"
						/>
					</c:if>

					<c:if test="<%= !thread.isInTrash() && MBMessagePermission.contains(permissionChecker, rootMessage, ActionKeys.SUBSCRIBE) && (mbGroupServiceSettings.isEmailMessageAddedEnabled() || mbGroupServiceSettings.isEmailMessageUpdatedEnabled()) %>">
						<c:choose>
							<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), MBThread.class.getName(), rootMessage.getThreadId()) %>">
								<portlet:actionURL name="/message_boards/edit_message" var="unsubscribeURL">
									<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="messageId" value="<%= String.valueOf(rootMessage.getMessageId()) %>" />
								</portlet:actionURL>

								<liferay-ui:icon
									message="unsubscribe"
									url="<%= unsubscribeURL %>"
								/>
							</c:when>
							<c:otherwise>
								<portlet:actionURL name="/message_boards/edit_message" var="subscribeURL">
									<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="messageId" value="<%= String.valueOf(rootMessage.getMessageId()) %>" />
								</portlet:actionURL>

								<liferay-ui:icon
									message="subscribe"
									url="<%= subscribeURL %>"
								/>
							</c:otherwise>
						</c:choose>
					</c:if>

					<c:if test="<%= !thread.isInTrash() && MBCategoryPermission.contains(permissionChecker, scopeGroupId, (category != null) ? category.getCategoryId() : MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, ActionKeys.LOCK_THREAD) %>">
						<c:choose>
							<c:when test="<%= thread.isLocked() %>">
								<portlet:actionURL name="/message_boards/edit_message" var="unlockThreadURL">
									<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNLOCK %>" />
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="threadId" value="<%= String.valueOf(rootMessage.getThreadId()) %>" />
								</portlet:actionURL>

								<liferay-ui:icon
									message="unlock"
									url="<%= unlockThreadURL %>"
								/>
							</c:when>
							<c:otherwise>
								<portlet:actionURL name="/message_boards/edit_message" var="lockThreadURL">
									<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.LOCK %>" />
									<portlet:param name="redirect" value="<%= currentURL %>" />
									<portlet:param name="threadId" value="<%= String.valueOf(rootMessage.getThreadId()) %>" />
								</portlet:actionURL>

								<liferay-ui:icon
									message="lock"
									url="<%= lockThreadURL %>"
								/>
							</c:otherwise>
						</c:choose>
					</c:if>

					<c:if test="<%= !thread.isInTrash() && MBCategoryPermission.contains(permissionChecker, scopeGroupId, (category != null) ? category.getCategoryId() : MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, ActionKeys.MOVE_THREAD) %>">
						<portlet:renderURL var="editThreadURL">
							<portlet:param name="mvcRenderCommandName" value="/message_boards/move_thread" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="mbCategoryId" value="<%= (category != null) ? String.valueOf(category.getCategoryId()) : String.valueOf(MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) %>" />
							<portlet:param name="threadId" value="<%= String.valueOf(rootMessage.getThreadId()) %>" />
						</portlet:renderURL>

						<liferay-ui:icon
							message="move"
							url="<%= editThreadURL %>"
						/>
					</c:if>

					<c:if test="<%= MBMessagePermission.contains(permissionChecker, rootMessage, ActionKeys.DELETE) && !thread.isLocked() %>">
						<portlet:renderURL var="parentCategoryURL">
							<c:choose>
								<c:when test="<%= (category == null) || (category.getCategoryId() == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) %>">
									<portlet:param name="mvcRenderCommandName" value="/message_boards/view" />
								</c:when>
								<c:otherwise>
									<portlet:param name="mvcRenderCommandName" value="/message_boards/view_category" />
									<portlet:param name="mbCategoryId" value="<%= String.valueOf(category.getCategoryId()) %>" />
								</c:otherwise>
							</c:choose>
						</portlet:renderURL>

						<portlet:actionURL name="/message_boards/delete_thread" var="deleteURL">
							<portlet:param name="<%= Constants.CMD %>" value="<%= trashHelper.isTrashEnabled(themeDisplay.getScopeGroupId()) ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
							<portlet:param name="redirect" value="<%= parentCategoryURL %>" />
							<portlet:param name="threadId" value="<%= String.valueOf(rootMessage.getThreadId()) %>" />
						</portlet:actionURL>

						<liferay-ui:icon-delete
							trash="<%= trashHelper.isTrashEnabled(themeDisplay.getScopeGroupId()) %>"
							url="<%= deleteURL %>"
						/>
					</c:if>
				</liferay-ui:icon-menu>
			</div>
		</clay:content-col>
	</clay:content-row>
</c:if>

<div class="thread-container">

	<%
	assetHelper.addLayoutTags(request, AssetTagLocalServiceUtil.getTags(MBMessage.class.getName(), thread.getRootMessageId()));
	%>

	<div class="message-scroll" id="<portlet:namespace />message_0"></div>

	<div class="card-tab-group message-container" id="<portlet:namespace />messageContainer">

		<%
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER, mbTreeWalker);
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CATEGORY, category);
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CUR_MESSAGE, rootMessage);
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_DEPTH, Integer.valueOf(0));
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_LAST_NODE, Boolean.valueOf(false));
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_SEL_MESSAGE, message);
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD, thread);
		request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_VIEWABLE_THREAD, Boolean.FALSE.toString());
		%>

		<liferay-util:include page="/message_boards/view_thread_tree.jsp" servletContext="<%= application %>" />

		<%
		int index = 0;
		int rootIndexPage = 0;
		boolean moreMessagesPagination = false;

		List<MBMessage> messages = mbTreeWalker.getMessages();

		int[] range = mbTreeWalker.getChildrenRange(rootMessage);

		MBMessageIterator mbMessageIterator = new MBMessageIterator(messages, range[0], range[1]);

		while (mbMessageIterator.hasNext()) {
			boolean messageFound = GetterUtil.getBoolean(request.getAttribute("view_thread_tree.jsp-messageFound"));

			index = GetterUtil.getInteger(request.getAttribute(WebKeys.MESSAGE_BOARDS_TREE_INDEX), 1);

			rootIndexPage = mbMessageIterator.getIndexPage();

			if (messageFound && ((index + 1) > PropsValues.DISCUSSION_COMMENTS_DELTA_VALUE)) {
				moreMessagesPagination = true;

				break;
			}

			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER, mbTreeWalker);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CATEGORY, category);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_CUR_MESSAGE, mbMessageIterator.next());
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_DEPTH, Integer.valueOf(0));
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_LAST_NODE, Boolean.valueOf(false));
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_SEL_MESSAGE, message);
			request.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_WALKER_THREAD, thread);
		%>

			<div class="card-tab message-container">
				<liferay-util:include page="/message_boards/view_thread_tree.jsp" servletContext="<%= application %>" />
			</div>

		<%
		}
		%>

		<c:if test="<%= !thread.isLocked() && !thread.isDraft() && MBCategoryPermission.contains(permissionChecker, scopeGroupId, rootMessage.getCategoryId(), ActionKeys.REPLY_TO_MESSAGE) %>">

			<%
			long replyToMessageId = message.getRootMessageId();
			%>

			<%@ include file="/message_boards/edit_message_quick.jspf" %>
		</c:if>
	</div>

	<c:if test="<%= thread.isApproved() && !thread.isLocked() && !thread.isDraft() && MBCategoryPermission.contains(permissionChecker, scopeGroupId, rootMessage.getCategoryId(), ActionKeys.REPLY_TO_MESSAGE) %>">

		<%
		String taglibReplyToMessageURL = "javascript:" + liferayPortletResponse.getNamespace() + "addReplyToMessage('" + rootMessage.getMessageId() + "', '');";
		%>

		<aui:button name='<%= "replyMessageButton" + rootMessage.getMessageId() %>' onclick="<%= taglibReplyToMessageURL %>" primary="<%= true %>" value="reply" />
	</c:if>

	<c:if test="<%= !thread.isInTrash() && moreMessagesPagination %>">
		<div class="reply-to-main-thread-container">
			<a class="btn btn-secondary" href="javascript:void(0);" id="<portlet:namespace />moreMessages"><liferay-ui:message key="more-messages" /></a>
		</div>

		<aui:form name="fm">
			<aui:input name="rootIndexPage" type="hidden" value="<%= String.valueOf(rootIndexPage) %>" />
			<aui:input name="index" type="hidden" value="<%= String.valueOf(index) %>" />
		</aui:form>
	</c:if>
</div>

<aui:script sandbox="<%= true %>">
	var moreMessagesButton = document.getElementById(
		'<portlet:namespace />moreMessages'
	);

	if (moreMessagesButton) {
		moreMessagesButton.addEventListener('click', (event) => {
			var form = document.<portlet:namespace />fm;

			var index = Liferay.Util.getFormElement(form, 'index');
			var rootIndexPage = Liferay.Util.getFormElement(form, 'rootIndexPage');

			var formData = new FormData();

			if (index && rootIndexPage) {
				formData.append('<portlet:namespace />index', index.value);
				formData.append(
					'<portlet:namespace />rootIndexPage',
					rootIndexPage.value
				);
			}

			<portlet:resourceURL id="/message_boards/get_messages" var="getMessagesURL">
				<portlet:param name="messageId" value="<%= String.valueOf(message.getMessageId()) %>" />
			</portlet:resourceURL>

			Liferay.Util.fetch('<%= getMessagesURL.toString() %>', {
				body: formData,
				method: 'POST',
			})
				.then((response) => {
					return response.text();
				})
				.then((response) => {
					var messageContainer = document.getElementById(
						'<portlet:namespace />messageContainer'
					);

					if (messageContainer) {
						response = Liferay.Util.setCSPNonce(response);

						messageContainer.appendChild(
							document
								.createRange()
								.createContextualFragment(response)
						);

						var replyContainer = document.querySelector(
							'#<portlet:namespace />messageContainer > .reply-container'
						);

						if (replyContainer) {
							messageContainer.append(replyContainer);
						}
					}
				});
		});
	}
</aui:script>