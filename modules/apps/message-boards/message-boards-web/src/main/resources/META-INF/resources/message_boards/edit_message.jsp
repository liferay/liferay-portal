<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/message_boards/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

MBMessage message = (MBMessage)request.getAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE);

long messageId = BeanParamUtil.getLong(message, request, "messageId");

long categoryId = MBUtil.getCategoryId(request, message);
long threadId = BeanParamUtil.getLong(message, request, "threadId");
long parentMessageId = BeanParamUtil.getLong(message, request, "parentMessageId", MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID);

String subject = BeanParamUtil.getString(message, request, "subject");

MBThread thread = null;

MBMessage curParentMessage = null;

if (threadId > 0) {
	thread = MBThreadLocalServiceUtil.getThread(threadId);

	try {
		curParentMessage = MBMessageServiceUtil.getMessage(parentMessageId);

		if (Validator.isNull(subject)) {
			String curParentMessageSubject = curParentMessage.getSubject();

			if (curParentMessageSubject.startsWith(MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE)) {
				subject = curParentMessageSubject;
			}
			else {
				subject = MBMessageConstants.MESSAGE_SUBJECT_PREFIX_RE + curParentMessageSubject;
			}
		}
	}
	catch (Exception e) {
	}
}

String body = BeanParamUtil.getString(message, request, "body");
boolean quote = ParamUtil.getBoolean(request, "quote");
boolean splitThread = ParamUtil.getBoolean(request, "splitThread");

List<FileEntry> existingAttachmentsFileEntries = new ArrayList<FileEntry>();

if (message != null) {
	existingAttachmentsFileEntries = message.getAttachmentsFileEntries();
}

boolean allowPingbacks = PropsValues.MESSAGE_BOARDS_PINGBACK_ENABLED && BeanParamUtil.getBoolean(message, request, "allowPingbacks", true);

if (Validator.isNull(redirect)) {
	redirect = PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCRenderCommandName(
		"/message_boards/view_message"
	).setParameter(
		"messageId", messageId
	).buildString();
}

if (curParentMessage != null) {
	MBBreadcrumbUtil.addPortletBreadcrumbEntries(curParentMessage, request, renderResponse);

	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "reply"), currentURL);
	}
}
else if (message != null) {
	MBBreadcrumbUtil.addPortletBreadcrumbEntries(message, request, renderResponse);

	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
	}
}
else {
	MBBreadcrumbUtil.addPortletBreadcrumbEntries(categoryId, request, renderResponse);

	if (!layout.isTypeControlPanel()) {
		PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-message"), currentURL);
	}
}

String headerTitle = LanguageUtil.get(request, "add-message");

if (message != null) {
	headerTitle = LanguageUtil.format(request, "edit-x", HtmlUtil.escape(message.getSubject()), false);
}
else if (curParentMessage != null) {
	headerTitle = LanguageUtil.format(request, "reply-to-x", HtmlUtil.escape(curParentMessage.getSubject()), false);
}

boolean portletTitleBasedNavigation = GetterUtil.getBoolean(portletConfig.getInitParameter("portlet-title-based-navigation")) || Objects.equals(portletDisplay.getPortletResource(), PortletKeys.MY_WORKFLOW_TASK);

if (portletTitleBasedNavigation) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);

	renderResponse.setTitle(headerTitle);
}
%>

<clay:container-fluid
	cssClass="container-form-lg"
	id='<%= liferayPortletResponse.getNamespace() + "mbEditPageContainer" %>'
>
	<c:if test="<%= !portletTitleBasedNavigation %>">
		<h3><%= headerTitle %></h3>
	</c:if>

	<portlet:actionURL name="/message_boards/edit_message" var="editMessageURL">
		<portlet:param name="mvcRenderCommandName" value="/message_boards/edit_message" />
	</portlet:actionURL>

	<aui:form action="<%= editMessageURL %>" enctype="multipart/form-data" method="post" name="fm" onSubmit="event.preventDefault();">
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="portletResource" type="hidden" value="<%= portletDisplay.getPortletResource() %>" />
		<aui:input name="messageId" type="hidden" value="<%= messageId %>" />
		<aui:input name="mbCategoryId" type="hidden" value="<%= categoryId %>" />
		<aui:input name="threadId" type="hidden" value="<%= threadId %>" />
		<aui:input name="parentMessageId" type="hidden" value="<%= parentMessageId %>" />
		<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_SAVE_DRAFT) %>" />

		<liferay-ui:error exception="<%= AntivirusScannerException.class %>">

			<%
			AntivirusScannerException ase = (AntivirusScannerException)errorException;
			%>

			<liferay-ui:message key="<%= ase.getMessageKey() %>" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
		<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
		<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
		<liferay-ui:error exception="<%= DuplicateFileEntryException.class %>" message="please-enter-a-unique-document-name" />

		<liferay-ui:error exception="<%= LiferayFileItemException.class %>">
			<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(FileItem.THRESHOLD_SIZE, locale) %>" key="please-enter-valid-content-with-valid-content-size-no-larger-than-x" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<%
		DLConfiguration dlConfiguration = ConfigurationProviderUtil.getSystemConfiguration(DLConfiguration.class);
		%>

		<liferay-ui:error exception="<%= FileExtensionException.class %>">
			<liferay-ui:message arguments="<%= StringUtil.merge(dlConfiguration.fileExtensions(), StringPool.COMMA_AND_SPACE) %>" key="please-enter-a-file-with-a-valid-extension-x" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= FileNameException.class %>" message="please-enter-a-file-with-a-valid-file-name" />

		<liferay-ui:error exception="<%= FileSizeException.class %>">

			<%
			FileSizeException fileSizeException = (FileSizeException)errorException;
			%>

			<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(fileSizeException.getMaxSize(), locale) %>" key="please-enter-a-file-with-a-valid-file-size-no-larger-than-x" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<liferay-ui:error exception="<%= LockedThreadException.class %>" message="thread-is-locked" />
		<liferay-ui:error exception="<%= MessageBodyException.class %>" message="please-enter-a-valid-message" />
		<liferay-ui:error exception="<%= MessageSubjectException.class %>" message="please-enter-a-valid-subject" />

		<liferay-ui:error exception="<%= UploadRequestSizeException.class %>">
			<liferay-ui:message arguments="<%= LanguageUtil.formatStorageSize(UploadServletRequestConfigurationProviderUtil.getMaxSize(), locale) %>" key="request-is-larger-than-x-and-could-not-be-processed" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<liferay-asset:asset-categories-error />

		<liferay-asset:asset-tags-error />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<c:if test="<%= curParentMessage != null %>">
						<div class="reply-to-message">
							<span class="control-label">
								<liferay-ui:message key="replying-to" />:
							</span>

							<%
							request.setAttribute("edit-message.jsp-showPermanentLink", Boolean.TRUE);
							request.setAttribute("edit-message.jsp-showRecentPosts", Boolean.TRUE);
							request.setAttribute("edit_message.jsp-category", null);
							request.setAttribute("edit_message.jsp-editable", Boolean.FALSE);
							request.setAttribute("edit_message.jsp-message", curParentMessage);
							request.setAttribute("edit_message.jsp-thread", thread);
							%>

							<liferay-util:include page="/message_boards/view_thread_message.jsp" servletContext="<%= application %>" />
						</div>
					</c:if>

					<aui:model-context bean="<%= message %>" model="<%= MBMessage.class %>" />

					<aui:input name="subject" value="<%= subject %>" />

					<aui:field-wrapper cssClass="message-content">
						<c:choose>
							<c:when test='<%= ((messageId != 0) && message.isFormatBBCode()) || ((messageId == 0) && messageFormat.equals("bbcode")) %>'>
								<%@ include file="/message_boards/bbcode_editor.jspf" %>
							</c:when>
							<c:otherwise>
								<%@ include file="/message_boards/html_editor.jspf" %>
							</c:otherwise>
						</c:choose>

						<aui:input name="body" type="hidden" />
					</aui:field-wrapper>
				</aui:fieldset>

				<liferay-expando:custom-attributes-available
					className="<%= MBMessage.class.getName() %>"
				>
					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="custom-fields">
						<liferay-expando:custom-attribute-list
							className="<%= MBMessage.class.getName() %>"
							classPK="<%= messageId %>"
							editable="<%= true %>"
							label="<%= true %>"
						/>
					</aui:fieldset>
				</liferay-expando:custom-attributes-available>

				<c:if test="<%= MBCategoryPermission.contains(permissionChecker, scopeGroupId, categoryId, ActionKeys.ADD_FILE) %>">
					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="attachments">
						<%@ include file="/message_boards/edit_message_attachment.jspf" %>

						<div class="<%= (existingAttachmentsFileEntries.size() == 0) ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />fileAttachments">
							<liferay-ui:search-container
								emptyResultsMessage="this-message-does-not-have-file-attachments"
								headerNames="file-name,size,action"
								id="messageAttachments"
								total="<%= existingAttachmentsFileEntries.size() %>"
							>
								<liferay-ui:search-container-results
									results="<%= existingAttachmentsFileEntries %>"
								/>

								<liferay-ui:search-container-row
									className="com.liferay.portal.kernel.repository.model.FileEntry"
									escapedModel="<%= true %>"
									keyProperty="fileEntryId"
									modelVar="fileEntry"
								>
									<liferay-ui:search-container-column-text
										href='<%= PortletFileRepositoryUtil.getDownloadPortletFileEntryURL(themeDisplay, fileEntry, "status=" + WorkflowConstants.STATUS_APPROVED) %>'
										name="file-name"
										value="<%= fileEntry.getTitle() %>"
									/>

									<liferay-ui:search-container-column-text
										name="size"
										value="<%= LanguageUtil.formatStorageSize(fileEntry.getSize(), locale) %>"
									/>

									<liferay-ui:search-container-column-text
										cssClass="entry-action"
										name="action"
									>
										<liferay-portlet:actionURL name="/message_boards/edit_message_attachments" var="deleteURL">
											<portlet:param name="<%= Constants.CMD %>" value="<%= trashHelper.isTrashEnabled(scopeGroupId) ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
											<portlet:param name="redirect" value="<%= currentURL %>" />
											<portlet:param name="messageId" value="<%= String.valueOf(messageId) %>" />
											<portlet:param name="fileName" value="<%= HtmlUtil.unescape(fileEntry.getTitle()) %>" />
										</liferay-portlet:actionURL>

										<a class="delete-attachment" data-rowid="<%= fileEntry.getFileEntryId() %>" href="<%= deleteURL.toString() %>">
											<liferay-ui:message key="delete" />
										</a>
									</liferay-ui:search-container-column-text>
								</liferay-ui:search-container-row>

								<liferay-ui:search-iterator
									markupView="lexicon"
									paginate="<%= false %>"
								/>
							</liferay-ui:search-container>
						</div>
					</aui:fieldset>
				</c:if>

				<c:if test="<%= curParentMessage == null %>">
					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="categorization">
						<liferay-asset:asset-categories-selector
							className="<%= MBMessage.class.getName() %>"
							classPK="<%= (message != null) ? message.getMessageId() : 0 %>"
							visibilityTypes="<%= AssetVocabularyConstants.VISIBILITY_TYPES %>"
						/>

						<liferay-asset:asset-tags-selector
							className="<%= MBMessage.class.getName() %>"
							classPK="<%= (message != null) ? message.getMessageId() : 0 %>"
						/>
					</aui:fieldset>
				</c:if>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="related-assets">
					<liferay-asset:input-asset-links
						className="<%= MBMessage.class.getName() %>"
						classPK="<%= (message != null) ? message.getMessageId() : 0 %>"
					/>
				</aui:fieldset>

				<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="more-settings">
					<c:if test="<%= curParentMessage == null %>">

						<%
						MBCategory category = MBCategoryLocalServiceUtil.getCategory(categoryId);

						boolean disabled = false;
						boolean question = threadAsQuestionByDefault;

						String displayStyle = category.getDisplayStyle();

						if (message != null) {
							if (thread.isQuestion() || message.isAnswer()) {
								question = true;

								if ((category != null) && Objects.equals(displayStyle, "question")) {
									disabled = true;
								}
							}
						}
						else if ((category != null) && Objects.equals(displayStyle, "question")) {
							disabled = true;
							question = true;
						}
						%>

						<aui:input disabled="<%= disabled %>" helpMessage="message-boards-message-question-help" label="mark-as-a-question" name="question" type="checkbox" value="<%= question %>" />
					</c:if>

					<c:if test="<%= (message == null) && themeDisplay.isSignedIn() && allowAnonymousPosting %>">
						<aui:input helpMessage="message-boards-message-anonymous-help" name="anonymous" type="checkbox" />
					</c:if>

					<c:if test="<%= (message == null) && themeDisplay.isSignedIn() && !SubscriptionLocalServiceUtil.isSubscribed(themeDisplay.getCompanyId(), user.getUserId(), MBThread.class.getName(), threadId) && !SubscriptionLocalServiceUtil.isSubscribed(themeDisplay.getCompanyId(), user.getUserId(), MBCategory.class.getName(), categoryId) %>">
						<aui:input helpMessage="message-boards-message-subscribe-me-help" label="subscribe-me" name="subscribe" type='<%= (mbGroupServiceSettings.isEmailMessageAddedEnabled() || mbGroupServiceSettings.isEmailMessageUpdatedEnabled()) ? "checkbox" : "hidden" %>' value="<%= subscribeByDefault %>" />
					</c:if>

					<c:if test="<%= (priorities.length > 0) && MBCategoryPermission.contains(permissionChecker, scopeGroupId, categoryId, ActionKeys.UPDATE_THREAD_PRIORITY) %>">

						<%
						double threadPriority = BeanParamUtil.getDouble(message, request, "priority");
						%>

						<aui:select name="priority">
							<aui:option value="" />

							<%
							for (int i = 0; i < priorities.length; i++) {
								String[] priority = StringUtil.split(priorities[i], StringPool.PIPE);

								try {
									String priorityName = priority[0];

									double priorityValue = GetterUtil.getDouble(priority[2]);
							%>

									<c:if test="<%= priorityValue > 0 %>">
										<aui:option label="<%= HtmlUtil.escape(priorityName) %>" selected="<%= threadPriority == priorityValue %>" value="<%= priorityValue %>" />
									</c:if>

							<%
								}
								catch (Exception e) {
								}
							}
							%>

						</aui:select>
					</c:if>

					<c:if test="<%= PropsValues.MESSAGE_BOARDS_PINGBACK_ENABLED %>">
						<aui:input helpMessage="to-allow-pingbacks,-please-also-ensure-the-entry's-guest-view-permission-is-enabled" label="allow-pingbacks" name="allowPingbacks" value="<%= allowPingbacks %>" />
					</c:if>
				</aui:fieldset>

				<c:if test="<%= message == null %>">
					<aui:fieldset collapsed="<%= true %>" collapsible="<%= true %>" label="permissions">
						<liferay-ui:input-permissions
							modelName="<%= MBMessage.class.getName() %>"
						/>
					</aui:fieldset>
				</c:if>

				<c:if test="<%= (message == null) && captchaConfiguration.messageBoardsEditMessageCaptchaEnabled() %>">
					<liferay-captcha:captcha />
				</c:if>

				<%
				boolean pending = false;

				if (message != null) {
					pending = message.isPending();
				}
				%>

				<c:if test="<%= pending %>">
					<div class="alert alert-info">
						<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
					</div>
				</c:if>

				<c:if test="<%= (message != null) && message.isApproved() && WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(message.getCompanyId(), message.getGroupId(), MBMessage.class.getName()) %>">
					<div class="alert alert-info">
						<liferay-ui:message arguments="<%= ResourceActionsUtil.getModelResource(locale, MBMessage.class.getName()) %>" key="this-x-is-approved.-publishing-these-changes-will-cause-it-to-be-unpublished-and-go-through-the-approval-process-again" translateArguments="<%= false %>" />
					</div>
				</c:if>

				<div class="sheet-footer">
					<div class="btn-group">

						<%
						String saveButtonLabel = "save";

						if ((message == null) || message.isDraft() || message.isApproved()) {
							saveButtonLabel = "save-as-draft";
						}

						String publishButtonLabel = "publish";

						if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, MBMessage.class.getName())) {
							publishButtonLabel = "submit-for-workflow";
						}
						%>

						<div class="btn-group-item">
							<aui:button disabled="<%= pending %>" name="publishButton" type="submit" value="<%= publishButtonLabel %>" />
						</div>

						<c:if test="<%= themeDisplay.isSignedIn() %>">
							<div class="btn-group-item">
								<aui:button name="saveButton" value="<%= saveButtonLabel %>" />
							</div>
						</c:if>

						<div class="btn-group-item">
							<aui:button href="<%= redirect %>" type="cancel" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</aui:form>
</clay:container-fluid>

<%
MBEditMessageDisplayContext mbEditMessageDisplayContext = new MBEditMessageDisplayContext(request, liferayPortletRequest, liferayPortletResponse, message, trashHelper);
%>

<liferay-frontend:component
	context="<%= mbEditMessageDisplayContext.getMBPortletComponentContext() %>"
	module="{MBPortlet} from message-boards-web"
	servletContext="<%= application %>"
/>