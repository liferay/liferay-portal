<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceOrder commerceOrder = commerceOrderContentDisplayContext.getCommerceOrder();

boolean manageNotesPermission = commerceOrderContentDisplayContext.hasModelPermission(commerceOrder, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_NOTES);
boolean manageRestrictedNotesPermission = commerceOrderContentDisplayContext.hasModelPermission(commerceOrder, CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_RESTRICTED_NOTES);

PortletURL backURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/commerce_open_order_content/edit_commerce_order"
).setParameter(
	"commerceOrderId", commerceOrder.getCommerceOrderId()
).buildPortletURL();
%>

<portlet:actionURL name="/commerce_open_order_content/edit_commerce_order_note" var="editCommerceOrderNoteURL">
	<portlet:param name="mvcRenderCommandName" value="/commerce_open_order_content/edit_commerce_order_notes" />
</portlet:actionURL>

<div class="b2b-portlet-content-header">
	<div class="autofit-float autofit-row header-title-bar">
		<div class="autofit-col autofit-col-expand">
			<liferay-ui:header
				backURL="<%= String.valueOf(backURL) %>"
				title='<%= LanguageUtil.format(request, "order-x", commerceOrder.getCommerceOrderId()) %>'
			/>
		</div>
	</div>
</div>

<aui:form action="<%= editCommerceOrderNoteURL %>" cssClass="order-notes-form" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.ADD %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceOrderId" type="hidden" value="<%= commerceOrder.getCommerceOrderId() %>" />

	<liferay-ui:error exception="<%= CommerceOrderNoteContentException.class %>" message="please-enter-valid-content" />

	<aui:model-context model="<%= CommerceOrderNote.class %>" />

	<div class="taglib-discussion">
		<c:if test="<%= manageNotesPermission || manageRestrictedNotesPermission %>">
			<div class="commerce-panel">
				<div class="commerce-panel__content">
					<div class="lfr-discussion-details">
						<liferay-user:user-portrait
							size="lg"
							user="<%= user %>"
						/>
					</div>

					<div class="lfr-discussion-body">
						<aui:input label="" name="content" placeholder="type-here" />

						<div class="order-notes-submit-actions">
							<c:if test="<%= manageRestrictedNotesPermission %>">
								<aui:input helpMessage="restricted-help" label="private" name="restricted" />
							</c:if>

							<aui:button-row>
								<aui:button type="submit" />
							</aui:button-row>
						</div>
					</div>
				</div>
			</div>
		</c:if>

		<%
		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale, timeZone);

		for (CommerceOrderNote commerceOrderNote : commerceOrderContentDisplayContext.getCommerceOrderNotes(commerceOrder)) {
		%>

			<article class="commerce-panel">
				<div class="commerce-panel__content">
					<div class="panel-body">
						<div class="card-row">
							<div class="card-col-content">
								<div class="lfr-discussion-details">
									<liferay-user:user-portrait
										size="lg"
										userId="<%= commerceOrderNote.getUserId() %>"
									/>
								</div>

								<div class="lfr-discussion-body">
									<div class="lfr-discussion-message">
										<header class="lfr-discussion-message-author">

											<%
											User noteUser = commerceOrderNote.getUser();
											%>

											<aui:a cssClass="author-link" href="<%= ((noteUser != null) && noteUser.isActive()) ? noteUser.getDisplayURL(themeDisplay) : null %>">
												<%= HtmlUtil.escape(commerceOrderNote.getUserName()) %>

												<c:if test="<%= commerceOrderNote.getUserId() == user.getUserId() %>">
													(<liferay-ui:message key="you" />)
												</c:if>
											</aui:a>

											<c:if test="<%= commerceOrderNote.isRestricted() %>">
												<clay:icon
													cssClass="d-block"
													symbol="lock"
												/>
											</c:if>

											<%
											Date createDate = commerceOrderNote.getCreateDate();
											%>

											<span class="small">
												<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />

												<c:if test="<%= createDate.before(commerceOrderNote.getModifiedDate()) %>">
													<strong onmouseover="Liferay.Portal.ToolTip.show(this, '<%= HtmlUtil.escapeJS(dateTimeFormat.format(commerceOrderNote.getModifiedDate())) %>');">
														- <liferay-ui:message key="edited" />
													</strong>
												</c:if>
											</span>
										</header>

										<div class="lfr-discussion-message-body">
											<%= HtmlUtil.escape(commerceOrderNote.getContent()) %>
										</div>
									</div>
								</div>
							</div>

							<div class="card-col-field">
								<liferay-ui:icon-menu
									direction="left-side"
									icon="<%= StringPool.BLANK %>"
									markupView="lexicon"
									message="<%= StringPool.BLANK %>"
									showWhenSingleIcon="<%= true %>"
								>
									<portlet:renderURL var="editURL">
										<portlet:param name="mvcRenderCommandName" value="/commerce_open_order_content/edit_commerce_order_note" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="commerceOrderNoteId" value="<%= String.valueOf(commerceOrderNote.getCommerceOrderNoteId()) %>" />
									</portlet:renderURL>

									<liferay-ui:icon
										message="edit"
										url="<%= editURL %>"
									/>

									<portlet:actionURL name="/commerce_open_order_content/edit_commerce_order_note" var="deleteURL">
										<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.DELETE %>" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="commerceOrderNoteId" value="<%= String.valueOf(commerceOrderNote.getCommerceOrderNoteId()) %>" />
									</portlet:actionURL>

									<liferay-ui:icon-delete
										label="<%= true %>"
										url="<%= deleteURL %>"
									/>
								</liferay-ui:icon-menu>
							</div>
						</div>
					</div>
				</div>
			</article>

		<%
		}
		%>

	</div>
</aui:form>