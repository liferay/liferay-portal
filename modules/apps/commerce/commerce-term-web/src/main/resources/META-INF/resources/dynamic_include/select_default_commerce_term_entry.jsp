<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceChannelAccountEntryRelDisplayContext commerceChannelAccountEntryRelDisplayContext = (CommerceChannelAccountEntryRelDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

AccountEntry accountEntry = commerceChannelAccountEntryRelDisplayContext.getAccountEntry();

CommerceChannelAccountEntryRel commerceChannelAccountEntryRel = commerceChannelAccountEntryRelDisplayContext.fetchCommerceChannelAccountEntryRel();
%>

<commerce-ui:modal-content
	submitButtonLabel='<%= LanguageUtil.get(request, "save") %>'
	title="<%= commerceChannelAccountEntryRelDisplayContext.getModalTitle() %>"
>
	<portlet:actionURL name="/commerce_term_entry/edit_account_entry_default_commerce_term_entry" var="editAccountEntryDefaultCommerceTermEntryActionURL" />

	<aui:form action="<%= editAccountEntryDefaultCommerceTermEntryActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="accountEntryId" type="hidden" value="<%= accountEntry.getAccountEntryId() %>" />
		<aui:input name="commerceChannelAccountEntryRelId" type="hidden" value="<%= (commerceChannelAccountEntryRel == null) ? 0 : commerceChannelAccountEntryRel.getCommerceChannelAccountEntryRelId() %>" />
		<aui:input name="type" type="hidden" value="<%= commerceChannelAccountEntryRelDisplayContext.getType() %>" />

		<liferay-ui:error exception="<%= DuplicateCommerceChannelAccountEntryRelException.class %>" message="there-is-already-a-term-defined-for-the-selected-channel" />

		<aui:model-context bean="<%= commerceChannelAccountEntryRelDisplayContext.fetchCommerceChannelAccountEntryRel() %>" model="<%= CommerceChannelAccountEntryRel.class %>" />

		<aui:select label="channel" name="commerceChannelId">
			<aui:option label="<%= LanguageUtil.get(request, commerceChannelAccountEntryRelDisplayContext.getCommerceChannelsEmptyOptionKey()) %>" selected="<%= commerceChannelAccountEntryRelDisplayContext.isCommerceChannelSelected(0) %>" value="0" />

			<%
			for (CommerceChannel commerceChannel : commerceChannelAccountEntryRelDisplayContext.getFilteredCommerceChannels()) {
			%>

				<aui:option label="<%= commerceChannel.getName() %>" selected="<%= commerceChannelAccountEntryRelDisplayContext.isCommerceChannelSelected(commerceChannel.getCommerceChannelId()) %>" value="<%= commerceChannel.getCommerceChannelId() %>" />

			<%
			}
			%>

		</aui:select>

		<aui:input checked="<%= (commerceChannelAccountEntryRel == null) ? false : commerceChannelAccountEntryRel.isOverrideEligibility() %>" helpMessage="override-eligibility-help" label="override-eligibility" name="overrideEligibility" type="toggle-switch" />

		<aui:select label="term[condition]" name="classPK" required="<%= true %>">

			<%
			for (CommerceTermEntry commerceTermEntry : commerceChannelAccountEntryRelDisplayContext.getCommerceTermEntries()) {
			%>

				<aui:option label="<%= commerceTermEntry.getLabel(languageId) %>" selected="<%= commerceChannelAccountEntryRelDisplayContext.isCommerceTermEntrySelected(commerceTermEntry.getCommerceTermEntryId()) %>" value="<%= commerceTermEntry.getCommerceTermEntryId() %>" />

			<%
			}
			%>

		</aui:select>
	</aui:form>
</commerce-ui:modal-content>