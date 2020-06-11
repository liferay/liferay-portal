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

<%@ include file="/html/taglib/ui/discussion/init.jsp" %>

<%
String randomNamespace = StringUtil.randomId() + StringPool.UNDERLINE;

boolean skipEditorLoading = ParamUtil.getBoolean(request, "skipEditorLoading");

DiscussionRequestHelper discussionRequestHelper = new DiscussionRequestHelper(request);
DiscussionTaglibHelper discussionTaglibHelper = new DiscussionTaglibHelper(request);

DiscussionPermission discussionPermission = CommentManagerUtil.getDiscussionPermission(discussionRequestHelper.getPermissionChecker());

Discussion discussion = (Discussion)request.getAttribute("liferay-comment:discussion:discussion");

if (discussion == null) {
	discussion = CommentManagerUtil.getDiscussion(discussionTaglibHelper.getUserId(), discussionRequestHelper.getScopeGroupId(), discussionTaglibHelper.getClassName(), discussionTaglibHelper.getClassPK(), new ServiceContextFunction(renderRequest));
}

DiscussionComment rootDiscussionComment = (discussion == null) ? null : discussion.getRootDiscussionComment();

CommentSectionDisplayContext commentSectionDisplayContext = CommentDisplayContextProviderUtil.getCommentSectionDisplayContext(request, response, discussionPermission, discussion);
%>

<section>
	<div class="lfr-message-response" id="<%= randomNamespace %>discussionStatusMessages"></div>

	<c:if test="<%= (discussion != null) && discussion.isMaxCommentsLimitExceeded() %>">
		<div class="alert alert-warning">
			<liferay-ui:message key="maximum-number-of-comments-has-been-reached" />
		</div>
	</c:if>

	<c:if test="<%= commentSectionDisplayContext.isDiscussionVisible() %>">
		<div class="taglib-discussion" id="<%= namespace %>discussionContainer">
			<aui:form action="<%= discussionTaglibHelper.getFormAction() %>" method="post" name="<%= discussionTaglibHelper.getFormName() %>">
				<input name="p_auth" type="hidden" value="<%= AuthTokenUtil.getToken(request) %>" />
				<input name="namespace" type="hidden" value="<%= namespace %>" />

				<%
				String contentURL = PortalUtil.getCanonicalURL(discussionTaglibHelper.getRedirect(), themeDisplay, layout);

				contentURL = HttpUtil.removeParameter(contentURL, namespace + "skipEditorLoading");
				%>

				<input name="contentURL" type="hidden" value="<%= contentURL %>" />

				<aui:input name="randomNamespace" type="hidden" value="<%= randomNamespace %>" />
				<aui:input id="<%= randomNamespace + Constants.CMD %>" name="<%= Constants.CMD %>" type="hidden" />
				<aui:input name="redirect" type="hidden" value="<%= discussionTaglibHelper.getRedirect() %>" />
				<aui:input name="assetEntryVisible" type="hidden" value="<%= discussionTaglibHelper.isAssetEntryVisible() %>" />
				<aui:input name="className" type="hidden" value="<%= discussionTaglibHelper.getClassName() %>" />
				<aui:input name="classPK" type="hidden" value="<%= discussionTaglibHelper.getClassPK() %>" />
				<aui:input name="commentId" type="hidden" />
				<aui:input name="parentCommentId" type="hidden" />
				<aui:input name="body" type="hidden" />
				<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_PUBLISH) %>" />
				<aui:input name="ajax" type="hidden" value="<%= true %>" />

				<%
				DiscussionComment discussionComment = rootDiscussionComment;
				%>

				<c:if test="<%= commentSectionDisplayContext.isControlsVisible() %>">
					<aui:fieldset cssClass="add-comment" id='<%= randomNamespace + "messageScroll0" %>'>
						<c:if test="<%= !discussion.isMaxCommentsLimitExceeded() %>">
							<div id="<%= randomNamespace %>messageScroll<%= rootDiscussionComment.getCommentId() %>">
								<aui:input name="commentId0" type="hidden" value="<%= rootDiscussionComment.getCommentId() %>" />
								<aui:input name="parentCommentId0" type="hidden" value="<%= rootDiscussionComment.getCommentId() %>" />
							</div>
						</c:if>

						<%
						boolean subscribed = SubscriptionLocalServiceUtil.isSubscribed(company.getCompanyId(), user.getUserId(), discussionTaglibHelper.getClassName(), discussionTaglibHelper.getClassPK());

						String subscriptionURL = "javascript:" + randomNamespace + "subscribeToComments(" + !subscribed + ");";
						%>

						<c:if test="<%= themeDisplay.isSignedIn() %>">
							<c:choose>
								<c:when test="<%= subscribed %>">
									<liferay-ui:icon
										cssClass="subscribe-link"
										iconCssClass="icon-remove-sign"
										label="<%= true %>"
										message="unsubscribe-from-comments"
										url="<%= subscriptionURL %>"
									/>
								</c:when>
								<c:otherwise>
									<liferay-ui:icon
										cssClass="subscribe-link"
										iconCssClass="icon-ok-sign"
										label="<%= true %>"
										message="subscribe-to-comments"
										url="<%= subscriptionURL %>"
									/>
								</c:otherwise>
							</c:choose>
						</c:if>

						<c:if test="<%= !discussion.isMaxCommentsLimitExceeded() %>">
							<aui:input name="emailAddress" type="hidden" />

							<c:choose>
								<c:when test="<%= commentSectionDisplayContext.isReplyButtonVisible() %>">
									<div class="panel">
										<div class="panel-body">
											<div class="lfr-discussion-details">
												<liferay-ui:user-portrait
													cssClass="user-icon-lg"
													user="<%= user %>"
												/>
											</div>

											<div class="lfr-discussion-body">
												<liferay-ui:input-editor
													configKey="commentEditor"
													contents=""
													editorName='<%= PropsUtil.get("editor.wysiwyg.portal-web.docroot.html.taglib.ui.discussion.jsp") %>'
													name='<%= randomNamespace + "postReplyBody0" %>'
													onChangeMethod='<%= randomNamespace + "0ReplyOnChange" %>'
													placeholder="type-your-comment-here"
													showSource="<%= false %>"
													skipEditorLoading="<%= skipEditorLoading %>"
												/>

												<aui:input name="postReplyBody0" type="hidden" />

												<aui:button-row>
													<aui:button cssClass="btn-comment btn-lg btn-primary" disabled="<%= true %>" id='<%= randomNamespace + "postReplyButton0" %>' onClick='<%= randomNamespace + "postReply(0);" %>' value='<%= themeDisplay.isSignedIn() ? "reply" : "reply-as" %>' />
												</aui:button-row>
											</div>
										</div>
									</div>
								</c:when>
								<c:otherwise>
									<liferay-ui:icon
										iconCssClass="icon-reply"
										label="<%= true %>"
										message="please-sign-in-to-comment"
										url="<%= themeDisplay.getURLSignIn() %>"
									/>
								</c:otherwise>
							</c:choose>
						</c:if>
					</aui:fieldset>
				</c:if>

				<c:if test="<%= commentSectionDisplayContext.isMessageThreadVisible() %>">
					<a name="<%= randomNamespace %>messages_top"></a>

					<div>

						<%
						int index = 0;
						int rootIndexPage = 0;
						boolean moreCommentsPagination = false;

						DiscussionCommentIterator discussionCommentIterator = rootDiscussionComment.getThreadDiscussionCommentIterator();

						while (discussionCommentIterator.hasNext()) {
							index = GetterUtil.getInteger(request.getAttribute("liferay-ui:discussion:index"), 1);

							rootIndexPage = discussionCommentIterator.getIndexPage();

							if ((index + 1) > PropsValues.DISCUSSION_COMMENTS_DELTA_VALUE) {
								moreCommentsPagination = true;

								break;
							}

							request.setAttribute("liferay-ui:discussion:depth", 0);
							request.setAttribute("liferay-ui:discussion:discussion", discussion);
							request.setAttribute("liferay-ui:discussion:discussionComment", discussionCommentIterator.next());
							request.setAttribute("liferay-ui:discussion:randomNamespace", randomNamespace);
						%>

							<liferay-util:include page="/html/taglib/ui/discussion/view_message_thread.jsp" />

						<%
						}
						%>

						<c:if test="<%= moreCommentsPagination %>">
							<div id="<%= namespace %>moreCommentsPage"></div>

							<a class="btn btn-default" href="javascript:;" id="<%= namespace %>moreComments"><liferay-ui:message key="more-comments" /></a>

							<aui:input name="rootIndexPage" type="hidden" value="<%= String.valueOf(rootIndexPage) %>" />
							<aui:input name="index" type="hidden" value="<%= String.valueOf(index) %>" />
						</c:if>
					</div>
				</c:if>
			</aui:form>
		</div>

		<%
		PortletURL loginURL = PortletURLFactoryUtil.create(request, PortletKeys.FAST_LOGIN, PortletRequest.RENDER_PHASE);

		loginURL.setParameter("saveLastPath", Boolean.FALSE.toString());
		loginURL.setParameter("mvcRenderCommandName", "/login/login");
		loginURL.setPortletMode(PortletMode.VIEW);
		loginURL.setWindowState(LiferayWindowState.POP_UP);
		%>

		<aui:script use="aui-base">
			Liferay.provide(
				window,
				'<%= namespace + randomNamespace %>0ReplyOnChange',
				function(html) {
					Liferay.Util.toggleDisabled('#<%= namespace + randomNamespace %>postReplyButton0', html.trim() === '');
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>afterLogin',
				function(emailAddress, anonymousAccount) {
					var form = AUI.$('#<%= namespace %><%= HtmlUtil.escapeJS(discussionTaglibHelper.getFormName()) %>');

					form.fm('emailAddress').val(emailAddress);

					<%= namespace %>sendMessage(form, !anonymousAccount);
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>deleteMessage',
				function(i) {
					var form = AUI.$('#<%= namespace %><%= HtmlUtil.escapeJS(discussionTaglibHelper.getFormName()) %>');

					var commentId = form.fm('commentId' + i).val();

					form.fm('<%= randomNamespace %><%= Constants.CMD %>').val('<%= Constants.DELETE %>');
					form.fm('commentId').val(commentId);

					<%= namespace %>sendMessage(form);
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>hideEl',
				function(elId) {
					AUI.$('#' + elId).css('display', 'none');
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>hideEditor',
				function(editorName, formId) {
					if (window[editorName]) {
						window[editorName].dispose();
					}

					<%= randomNamespace %>hideEl(formId);
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>onMessagePosted',
				function(response, refreshPage) {
					Liferay.onceAfter(
						'<%= portletDisplay.getId() %>:portletRefreshed',
						function(event) {
							var randomNamespaceNodes = AUI.$('input[id^="<%= namespace %>randomNamespace"]');

							randomNamespaceNodes.each(
								function(index, item) {
									var randomId = item.value;

									if (index === 0) {
										<%= randomNamespace %>showStatusMessage(
											{
												id: randomId,
												message: '<%= UnicodeLanguageUtil.get(resourceBundle, "your-request-completed-successfully") %>',
												type: 'success'
											}
										);
									}

									var currentMessageSelector = '#' + randomId + 'message_' + response.commentId;

									var targetNode = AUI.$(currentMessageSelector);

									if (targetNode.length) {
										location.hash = currentMessageSelector;
									}
								}
							);
						}
					);

					if (refreshPage) {
						window.location.reload();
					}
					else {
						var portletNodeId = '#p_p_id_<%= portletDisplay.getId() %>_';

						var portletNode = A.one(portletNodeId);

						Liferay.Portlet.refresh(
							portletNodeId,
							A.merge(
								Liferay.Util.ns(
									'<%= namespace %>',
									{
										skipEditorLoading: true
									}
								),
								portletNode.refreshURLData || {}
							)
						);
					}
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>postReply',
				function(i) {
					var form = AUI.$('#<%= namespace %><%= HtmlUtil.escapeJS(discussionTaglibHelper.getFormName()) %>');

					var editorInstance = window['<%= namespace + randomNamespace %>postReplyBody' + i];

					var parentCommentId = form.fm('parentCommentId' + i).val();

					form.fm('<%= randomNamespace %><%= Constants.CMD %>').val('<%= Constants.ADD %>');
					form.fm('parentCommentId').val(parentCommentId);
					form.fm('body').val(editorInstance.getHTML());

					if (!themeDisplay.isSignedIn()) {
						window.namespace = '<%= namespace %>';
						window.randomNamespace = '<%= randomNamespace %>';

						Liferay.Util.openWindow(
							{
								dialog: {
									height: 450,
									width: 560
								},
								id: '<%= namespace %>signInDialog',
								title: '<%= UnicodeLanguageUtil.get(resourceBundle, "sign-in") %>',
								uri: '<%= loginURL.toString() %>'
							}
						);
					}
					else {
						<%= namespace %>sendMessage(form);

						editorInstance.dispose();
					}
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>scrollIntoView',
				function(commentId) {
					document.getElementById('<%= randomNamespace %>messageScroll' + commentId).scrollIntoView();
				}
			);

			Liferay.provide(
				window,
				'<%= namespace %>sendMessage',
				function(form, refreshPage) {
					var Util = Liferay.Util;

					form = AUI.$(form);

					var commentButtonList = form.find('.btn-comment');

					var cmd = form.fm('<%= randomNamespace %><%= Constants.CMD %>').val();

					var dataType = cmd === '<%= Constants.ADD %>' || cmd === '<%= Constants.UPDATE %>' ? 'json' : null;

					form.ajaxSubmit(
						{
							data: {
								doAsUserId: themeDisplay.getDoAsUserIdEncoded()
							},
							beforeSubmit: function() {
								Util.toggleDisabled(commentButtonList, true);
							},
							dataType: dataType,
							complete: function() {
								Util.toggleDisabled(commentButtonList, false);
							},
							error: function() {
								<%= randomNamespace %>showStatusMessage(
									{
										id: '<%= randomNamespace %>',
										message: '<%= UnicodeLanguageUtil.get(resourceBundle, "your-request-failed-to-complete") %>',
										type: 'danger'
									}
								);
							},
							success: function(response) {
								var exception = response.exception;

								if (!exception) {
									Liferay.onceAfter(
										'<%= portletDisplay.getId() %>:messagePosted',
										function(event) {
											<%= randomNamespace %>onMessagePosted(response, refreshPage);
										}
									);

									Liferay.fire('<%= portletDisplay.getId() %>:messagePosted', response);
								}
								else {
									var errorKey = '<%= UnicodeLanguageUtil.get(resourceBundle, "your-request-failed-to-complete") %>';

									if (exception.indexOf('DiscussionMaxCommentsException') > -1) {
										errorKey = '<%= UnicodeLanguageUtil.get(resourceBundle, "maximum-number-of-comments-has-been-reached") %>';
									}
									else if (exception.indexOf('MessageBodyException') > -1) {
										errorKey = '<%= UnicodeLanguageUtil.get(resourceBundle, "please-enter-a-valid-message") %>';
									}
									else if (exception.indexOf('NoSuchMessageException') > -1) {
										errorKey = '<%= UnicodeLanguageUtil.get(resourceBundle, "the-message-could-not-be-found") %>';
									}
									else if (exception.indexOf('PrincipalException') > -1) {
										errorKey = '<%= UnicodeLanguageUtil.get(resourceBundle, "you-do-not-have-the-required-permissions") %>';
									}
									else if (exception.indexOf('RequiredMessageException') > -1) {
										errorKey = '<%= UnicodeLanguageUtil.get(resourceBundle, "you-cannot-delete-a-root-message-that-has-more-than-one-immediate-reply") %>';
									}

									<%= randomNamespace %>showStatusMessage(
										{
											id: '<%= randomNamespace %>',
											message: errorKey,
											type: 'danger'
										}
									);
								}
							}
						}
					);
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>showEl',
				function(elId) {
					AUI.$('#' + elId).css('display', '');
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>showEditor',
				function(editorName, formId) {
					window[editorName].create();

					var html = window[editorName].getHTML();

					Liferay.Util.toggleDisabled('#' + editorName.replace('Body', 'Button'), html === '');

					<%= randomNamespace %>showEl(formId);
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>showStatusMessage',
				function(data) {
					new Liferay.Alert(
						{
							'delay.hide': 5000,
							message: data.message,
							type: data.type
						}
					).render('#' + data.id + 'discussionStatusMessages');
				},
				['liferay-alert']
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>subscribeToComments',
				function(subscribe) {
					var form = AUI.$('#<%= namespace %><%= HtmlUtil.escapeJS(discussionTaglibHelper.getFormName()) %>');

					var cmd = '<%= Constants.UNSUBSCRIBE_FROM_COMMENTS %>';

					if (subscribe) {
						cmd = '<%= Constants.SUBSCRIBE_TO_COMMENTS %>';
					}

					form.fm('<%= randomNamespace %><%= Constants.CMD %>').val(cmd);

					<%= namespace %>sendMessage(form);
				}
			);

			Liferay.provide(
				window,
				'<%= randomNamespace %>updateMessage',
				function(i, pending) {
					var form = AUI.$('#<%= namespace %><%= HtmlUtil.escapeJS(discussionTaglibHelper.getFormName()) %>');

					var editorInstance = window['<%= namespace + randomNamespace %>editReplyBody' + i];

					var commentId = form.fm('commentId' + i).val();

					if (pending) {
						form.fm('workflowAction').val('<%= WorkflowConstants.ACTION_SAVE_DRAFT %>');
					}

					form.fm('<%= randomNamespace %><%= Constants.CMD %>').val('<%= Constants.UPDATE %>');
					form.fm('commentId').val(commentId);
					form.fm('body').val(editorInstance.getHTML());

					<%= namespace %>sendMessage(form);

					editorInstance.dispose();
				}
			);

			<%
			String messageId = ParamUtil.getString(request, "messageId");
			%>

			<c:if test="<%= Validator.isNotNull(messageId) %>">
				<%= randomNamespace %>scrollIntoView(<%= messageId %>);
			</c:if>
		</aui:script>

		<aui:script sandbox="<%= true %>">
			$('#<%= namespace %>moreComments').on(
				'click',
				function(event) {
					var form = $('#<%= namespace %><%= HtmlUtil.escapeJS(discussionTaglibHelper.getFormName()) %>');

					var data = Liferay.Util.ns(
						'<%= namespace %>',
						{
							className: '<%= discussionTaglibHelper.getClassName() %>',
							classPK: <%= discussionTaglibHelper.getClassPK() %>,
							hideControls: '<%= discussionTaglibHelper.isHideControls() %>',
							index: form.fm('index').val(),
							randomNamespace: '<%= randomNamespace %>',
							ratingsEnabled: '<%= discussionTaglibHelper.isRatingsEnabled() %>',
							rootIndexPage: form.fm('rootIndexPage').val(),
							userId: '<%= discussionTaglibHelper.getUserId() %>'
						}
					);

					<%
					String paginationURL = HttpUtil.addParameter(discussionTaglibHelper.getPaginationURL(), "namespace", namespace);

					paginationURL = HttpUtil.addParameter(paginationURL, "skipEditorLoading", "true");
					%>

					$.ajax(
						'<%= paginationURL %>',
						{
							data: data,
							error: function() {
								<%= randomNamespace %>showStatusMessage(
									{
										id: <%= randomNamespace %>,
										message: '<%= UnicodeLanguageUtil.get(resourceBundle, "your-request-failed-to-complete") %>',
										type: 'danger'
									}
								);
							},
							success: function(data) {
								$('#<%= namespace %>moreCommentsPage').append(data);
							}
						}
					);
				}
			);
		</aui:script>

		<aui:script use="aui-popover,event-outside">
			var discussionContainer = A.one('#<%= namespace %>discussionContainer');

			var popover = new A.Popover(
				{
					constrain: true,
					cssClass: 'lfr-discussion-reply',
					position: 'top',
					visible: false,
					width: 400,
					zIndex: Liferay.zIndex.OVERLAY
				}
			).render(discussionContainer);

			var handle;

			var boundingBox = popover.get('boundingBox');

			discussionContainer.delegate(
				'click',
				function(event) {
					event.stopPropagation();

					if (handle) {
						handle.detach();

						handle = null;
					}

					handle = boundingBox.once('clickoutside', popover.hide, popover);

					popover.hide();

					var currentTarget = event.currentTarget;

					popover.set('align.node', currentTarget);
					popover.set('bodyContent', currentTarget.attr('data-metaData'));
					popover.set('headerContent', currentTarget.attr('data-title'));

					popover.show();
				},
				'.lfr-discussion-parent-link'
			);
		</aui:script>
	</c:if>
</section>