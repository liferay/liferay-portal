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

if (Validator.isNull(redirect)) {
	redirect = currentURL;
}

Comment comment = (Comment)request.getAttribute(WebKeys.COMMENT);

long commentId = BeanParamUtil.getLong(comment, request, "commentId");

long parentCommentId = comment.getParentCommentId();

Comment parentComment = CommentManagerUtil.fetchComment(parentCommentId);

if ((parentComment != null) && parentComment.isRoot()) {
	parentComment = null;
}

WorkflowableComment workflowableComment = null;

boolean approved = true;
boolean pending = false;

if (comment instanceof WorkflowableComment) {
	workflowableComment = (WorkflowableComment)comment;

	approved = workflowableComment.getStatus() == WorkflowConstants.STATUS_APPROVED;
	pending = workflowableComment.getStatus() == WorkflowConstants.STATUS_PENDING;
}
%>

<liferay-ui:header
	backURL="<%= backURL %>"
	title='<%= (comment == null) ? "new-message" : "edit-message" %>'
/>

<clay:container-fluid>
	<aui:form action='<%= themeDisplay.getPathMain() + "/portal/comment/discussion/edit" %>' enctype="multipart/form-data" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "saveComment();" %>'>
		<input name="p_auth" type="hidden" value="<%= AuthTokenUtil.getToken(request) %>" />
		<input name="namespace" type="hidden" value="<%= liferayPortletResponse.getNamespace() %>" />

		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
		<aui:input name="commentId" type="hidden" value="<%= commentId %>" />
		<aui:input name="parentCommentId" type="hidden" value="<%= parentCommentId %>" />
		<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(pending ? WorkflowConstants.ACTION_SAVE_DRAFT : WorkflowConstants.ACTION_PUBLISH) %>" />
		<aui:input name="ajax" type="hidden" value="<%= false %>" />

		<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
		<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
		<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
		<liferay-ui:error exception="<%= DiscussionMaxCommentsException.class %>" message="maximum-number-of-comments-has-been-reached" />
		<liferay-ui:error exception="<%= MessageBodyException.class %>" message="please-enter-a-valid-message" />

		<aui:model-context bean="<%= comment %>" model="<%= comment.getModelClass() %>" />

		<aui:fieldset>
			<c:if test="<%= workflowableComment != null %>">
				<aui:workflow-status model="<%= CommentConstants.getDiscussionClass() %>" status="<%= workflowableComment.getStatus() %>" />
			</c:if>

			<liferay-editor:editor
				configKey="commentEditor"
				contents="<%= comment.getBody() %>"
				editorName="ckeditor"
				name="bodyEditor"
				showSource="<%= false %>"
			/>

			<aui:input name="body" type="hidden" value="<%= comment.getBody() %>" />
		</aui:fieldset>

		<c:if test="<%= parentComment != null %>">
			<liferay-ui:message key="replying-to" />:

			<%
			request.setAttribute(WebKeys.COMMENT, parentComment);
			%>

			<liferay-util:include page="/asset/discussion_full_content.jsp" />
		</c:if>

		<c:if test="<%= pending %>">
			<div class="alert alert-info">
				<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
			</div>
		</c:if>

		<aui:button-row>
			<c:if test="<%= (comment == null) || !approved %>">
				<aui:button type="submit" />
			</c:if>

			<c:if test="<%= (workflowableComment != null) && approved && WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(workflowableComment.getCompanyId(), workflowableComment.getGroupId(), CommentConstants.getDiscussionClassName()) %>">
				<div class="alert alert-info">
					<liferay-ui:message arguments="<%= ResourceActionsUtil.getModelResource(locale, comment.getModelClassName()) %>" key="this-x-is-approved.-publishing-these-changes-will-cause-it-to-be-unpublished-and-go-through-the-approval-process-again" translateArguments="<%= false %>" />
				</div>
			</c:if>

			<aui:button href="<%= redirect %>" type="cancel" />
		</aui:button-row>
	</aui:form>
</clay:container-fluid>

<aui:script>
	function <portlet:namespace />saveComment() {
		Liferay.Util.postForm(document.<portlet:namespace />fm, {
			data: {
				body: window.<portlet:namespace />bodyEditor.getHTML(),
			},
		});
	}
</aui:script>