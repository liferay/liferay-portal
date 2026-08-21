<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
EditCommerceOrderAttachmentDisplayContext editCommerceOrderAttachmentDisplayContext = (EditCommerceOrderAttachmentDisplayContext)request.getAttribute(EditCommerceOrderAttachmentDisplayContext.class.getName());

long commerceOrderAttachmentId = editCommerceOrderAttachmentDisplayContext.getCommerceOrderAttachmentId();
long fileEntryId = editCommerceOrderAttachmentDisplayContext.getFileEntryId();
String mode = editCommerceOrderAttachmentDisplayContext.getMode();
%>

<liferay-frontend:side-panel-content
	title='<%= LanguageUtil.get(request, Objects.equals(mode, "edit") ? "edit-attachment" : "add-attachment") %>'
>
	<aui:form method="post" name="fm" onSubmit='<%= liferayPortletResponse.getNamespace() + "editCommerceOrderAttachment(event, this.form)" %>'>
		<aui:input name="commerceOrderAttachmentId" type="hidden" value="<%= commerceOrderAttachmentId %>" />

		<commerce-ui:panel>
			<div class="<%= (fileEntryId > 0) ? StringPool.BLANK : " d-none" %> font-weight-bold mb-3" id="<portlet:namespace />attachmentFileName">
				<c:if test="<%= fileEntryId > 0 %>">
					<%= HtmlUtil.escape(editCommerceOrderAttachmentDisplayContext.getFileEntryName()) %>
				</c:if>
			</div>

			<input class="d-none" id="<portlet:namespace />attachmentFile" type="file" />

			<clay:button
				displayType="secondary"
				id='<%= liferayPortletResponse.getNamespace() + "selectFileButton" %>'
				label="select-file"
			/>

			<div class="d-none has-error" id="<portlet:namespace />attachmentFileError">
				<div class="form-feedback-item">
					<liferay-ui:message key="this-field-is-required" />
				</div>
			</div>
		</commerce-ui:panel>

		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "details") %>'
		>
			<aui:input label="title" name="title" required="<%= true %>" value="<%= editCommerceOrderAttachmentDisplayContext.getTitle() %>" />

			<aui:select label="type" name="type" required="<%= true %>">
				<aui:option label="" selected="<%= Validator.isNull(editCommerceOrderAttachmentDisplayContext.getType()) %>" value="" />

				<%
				for (ListTypeEntry listTypeEntry : editCommerceOrderAttachmentDisplayContext.getAttachmentTypes()) {
				%>

					<aui:option label="<%= HtmlUtil.escape(listTypeEntry.getName(locale)) %>" selected="<%= Objects.equals(listTypeEntry.getKey(), editCommerceOrderAttachmentDisplayContext.getType()) %>" value="<%= HtmlUtil.escape(listTypeEntry.getKey()) %>" />

				<%
				}
				%>

			</aui:select>

			<aui:input name="priority" type="text" value="<%= editCommerceOrderAttachmentDisplayContext.getPriority() %>" />

			<c:if test="<%= editCommerceOrderAttachmentDisplayContext.hasViewRestrictedCommerceOrderAttachmentsPermission() %>">
				<aui:input checked="<%= editCommerceOrderAttachmentDisplayContext.getRestricted() %>" inlineLabel="right" label="restricted" labelCssClass="simple-toggle-switch" name="restricted" type="toggle-switch" />
			</c:if>
		</commerce-ui:panel>

		<div class="dialog-footer">
			<clay:button
				data-qa-id="cancelButton"
				displayType="secondary"
				label="cancel"
				onClick="window.top.Liferay.fire('close-side-panel');"
			/>

			<clay:button
				data-qa-id="saveButton"
				displayType="primary"
				label="save"
				type="submit"
			/>
		</div>

		<liferay-frontend:component
			context='<%=
				HashMapBuilder.<String, Object>put(
					"addURL", editCommerceOrderAttachmentDisplayContext.getAddURL()
				).put(
					"commerceOrderAttachmentId", commerceOrderAttachmentId
				).put(
					"commerceOrderId", editCommerceOrderAttachmentDisplayContext.getCommerceOrderId()
				).put(
					"editURL", editCommerceOrderAttachmentDisplayContext.getEditURL()
				).put(
					"fdsId", CommerceOrderFDSNames.ATTACHMENTS
				).put(
					"mode", mode
				).put(
					"namespace", liferayPortletResponse.getNamespace()
				).build()
			%>'
			module="{editCommerceOrderAttachment} from commerce-order-web"
		/>
	</aui:form>
</liferay-frontend:side-panel-content>