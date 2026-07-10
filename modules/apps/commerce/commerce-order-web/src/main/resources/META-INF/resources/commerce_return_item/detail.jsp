<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceReturnEditDisplayContext commerceReturnEditDisplayContext = (CommerceReturnEditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceOrderItem commerceOrderItem = commerceReturnEditDisplayContext.getCommerceReturnItemCommerceOrderItem();
CommerceReturn commerceReturn = commerceReturnEditDisplayContext.getCommerceReturn();
CommerceReturnItem commerceReturnItem = commerceReturnEditDisplayContext.getCommerceReturnItem();
%>

<aui:form name="commerceReturnItemsFm" onSubmit="event.preventDefault();">
	<aui:input name="commerceReturnItemId" type="hidden" value="<%= commerceReturnEditDisplayContext.getCommerceReturnItemId() %>" />
	<aui:input name="commerceReturnStatus" type="hidden" value="<%= commerceReturn.getReturnStatus() %>" />

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "details") %>'
	>
		<div class="row vertically-divided">
			<div class="col-xl-4">
				<commerce-ui:info-box
					elementClasses="py-3"
					title='<%= LanguageUtil.get(request, "unit-of-measure") %>'
				>
					<p class="mb-0" data-qa-id="commerceReturnUnitOfMeasure"><%= commerceOrderItem.getUnitOfMeasureKey() %></p>
				</commerce-ui:info-box>
			</div>

			<div class="col-xl-4">
				<commerce-ui:info-box
					elementClasses="py-3"
					title='<%= LanguageUtil.get(request, "requested-quantity") %>'
				>
					<p class="mb-0" data-qa-id="commerceReturnRequestedQuantity"><%= commerceReturnItem.getQuantity() %></p>
				</commerce-ui:info-box>
			</div>

			<div class="col-xl-4">
				<commerce-ui:info-box
					elementClasses="py-3"
					title='<%= LanguageUtil.get(request, "return-reason") %>'
				>
					<p class="mb-0" data-qa-id="commerceReturnReturnReason"><%= HtmlUtil.escape(commerceReturnEditDisplayContext.getReturnReasonName()) %></p>
				</commerce-ui:info-box>
			</div>
		</div>
	</commerce-ui:panel>

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "workflow-actions") %>'
	>
		<div class="sheet-section">
			<aui:field-wrapper cssClass="sheet-subtitle" helpMessage="authorization-step" label="authorization-step" />

			<div class="row">
				<div class="col">
					<aui:input name="authorized" readonly="<%= ArrayUtil.contains(CommerceReturnConstants.RETURN_STATUSES_LATEST, commerceReturn.getReturnStatus()) || StringUtil.equals(CommerceReturnConstants.RETURN_ITEM_STATUS_PROCESSED, commerceReturnItem.getReturnItemStatus()) %>" required="<%= true %>" type="text" value="<%= commerceReturnItem.getAuthorized() %>" />
					<aui:input disabled="<%= ArrayUtil.contains(CommerceReturnConstants.RETURN_STATUSES_LATEST, commerceReturn.getReturnStatus()) || StringUtil.equals(CommerceReturnConstants.RETURN_ITEM_STATUS_PROCESSED, commerceReturnItem.getReturnItemStatus()) %>" inlineLabel="right" label="authorize-return-without-returning-products" name="authorizeReturnWithoutReturningProducts" type="checkbox" value="<%= commerceReturnItem.isAuthorizeReturnWithoutReturningProducts() %>" />
				</div>
			</div>

			<aui:field-wrapper cssClass="sheet-subtitle" helpMessage="item-acceptance-step" label="item-acceptance-step" />

			<div class="row">
				<div class="col">
					<aui:input name="received" readonly="<%= ArrayUtil.contains(CommerceReturnConstants.RETURN_STATUSES_LATEST, commerceReturn.getReturnStatus()) || StringUtil.equals(CommerceReturnConstants.RETURN_ITEM_STATUS_PROCESSED, commerceReturnItem.getReturnItemStatus()) %>" required="<%= true %>" type="text" value="<%= commerceReturnItem.getReceived() %>" />
				</div>
			</div>

			<aui:field-wrapper cssClass="sheet-subtitle" helpMessage="resolution-method-step" label="resolution-method-step" />

			<div class="row">
				<div class="col">
					<aui:field-wrapper label='<%= LanguageUtil.get(resourceBundle, "resolution-method") %>' name="resolutionMethodFieldWrapper">
						<div id="autocomplete-root"></div>
					</aui:field-wrapper>
				</div>
			</div>
		</div>
	</commerce-ui:panel>

	<commerce-ui:panel
		title='<%= LanguageUtil.get(request, "comments") %>'
	>
		<div id="<portlet:namespace />commerceReturnItemComments">

			<%
			for (DiscussionComment discussionComment : commerceReturnEditDisplayContext.getDiscussionComments()) {
			%>

				<article class="card-tab-group lfr-discussion">
					<div class="border-bottom m-0 panel">
						<div class="panel-body px-0 py-4">
							<div class="row">
								<div class="col-auto">
									<liferay-user:user-portrait
										size="lg"
										userId="<%= discussionComment.getUserId() %>"
									/>
								</div>

								<div class="col">
									<header class="lfr-discussion-message-author">

										<%
										User discussionCommentUser = discussionComment.getUser();

										String label = HtmlUtil.escape(discussionComment.getUserName());

										if (discussionCommentUser.getUserId() == themeDisplay.getUserId()) {
											label = StringBundler.concat(label, StringPool.SPACE, StringPool.OPEN_PARENTHESIS, LanguageUtil.get(request, "you"), StringPool.CLOSE_PARENTHESIS);
										}
										%>

										<clay:link
											cssClass="author-link"
											href="<%= ((discussionCommentUser != null) && discussionCommentUser.isActive()) ? discussionCommentUser.getDisplayURL(themeDisplay) : null %>"
											label="<%= label %>"
										/>
									</header>

									<div class="lfr-discussion-message-body">
										<%= HtmlUtil.escape(discussionComment.getBody()) %>
									</div>
								</div>

								<div class="align-items-center col-auto d-flex">
									<span class="small">

										<%
										Date discussionCommentCreateDate = discussionComment.getCreateDate();
										%>

										<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - discussionCommentCreateDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />

										<c:if test="<%= discussionCommentCreateDate.before(discussionComment.getModifiedDate()) %>">

											<%
											Format format = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);
											%>

											<strong onmouseover="Liferay.Portal.ToolTip.show(this, '<%= HtmlUtil.escapeJS(format.format(discussionComment.getModifiedDate())) %>');">
												- <liferay-ui:message key="edited" />
											</strong>
										</c:if>
									</span>
								</div>

								<div class="align-items-center col-auto d-flex">
									<clay:dropdown-actions
										aria-label='<%= LanguageUtil.get(request, "edit-comment") %>'
										dropdownItems="<%= commerceReturnEditDisplayContext.getCommerceReturnItemCommentDropdownItemList(discussionComment) %>"
									/>
								</div>
							</div>
						</div>
					</div>
				</article>

			<%
			}
			%>

			<article class="card-tab-group lfr-discussion">
				<div class="panel-body px-0 py-4">
					<div class="row">
						<div class="col-auto">
							<div class="lfr-discussion-details">
								<liferay-user:user-portrait
									size="lg"
									user="<%= user %>"
								/>
							</div>
						</div>

						<div class="col">
							<div class="lfr-discussion-body">
								<aui:input name="className" type="hidden" value="<%= commerceReturnEditDisplayContext.getCommerceReturnItemClassName() %>" />
								<aui:input name="classPK" type="hidden" value="<%= commerceReturnItem.getId() %>" />

								<aui:input label="" name="content" placeholder="type-your-comment-here" type="textarea" />
							</div>
						</div>
					</div>
				</div>
			</article>
		</div>
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" value="save" />

		<aui:button cssClass="btn-lg" type="cancel" />
	</aui:button-row>
</aui:form>