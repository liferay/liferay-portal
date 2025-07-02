<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceTermEntryDisplayContext commerceTermEntryDisplayContext = (CommerceTermEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceTermEntry commerceTermEntry = commerceTermEntryDisplayContext.getCommerceTermEntry();
long commerceTermEntryId = commerceTermEntryDisplayContext.getCommerceTermEntryId();

boolean neverExpire = ParamUtil.getBoolean(request, "neverExpire", true);

if ((commerceTermEntry != null) && (commerceTermEntry.getExpirationDate() != null)) {
	neverExpire = false;
}
%>

<portlet:actionURL name="/commerce_term_entry/edit_commerce_term_entry" var="editCommerceTermEntryActionURL" />

<aui:form action="<%= editCommerceTermEntryActionURL %>" cssClass="pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceTermEntry == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="externalReferenceCode" type="hidden" value="<%= commerceTermEntry.getExternalReferenceCode() %>" />
	<aui:input name="commerceTermEntryId" type="hidden" value="<%= commerceTermEntryId %>" />
	<aui:input name="name" type="hidden" value="<%= (commerceTermEntry == null) ? null : commerceTermEntry.getName() %>" />
	<aui:input name="workflowAction" type="hidden" value="<%= String.valueOf(WorkflowConstants.ACTION_SAVE_DRAFT) %>" />

	<liferay-ui:error exception="<%= CommerceTermEntryNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= CommerceTermEntryPriorityException.class %>" message="please-enter-a-valid-priority" />
	<liferay-ui:error exception="<%= CommerceTermEntryTypeException.class %>" message="please-select-a-valid-type" />

	<aui:model-context bean="<%= commerceTermEntry %>" model="<%= CommerceTermEntry.class %>" />

	<div class="row">
		<div class="col-12 col-xl-8">
			<commerce-ui:panel
				bodyClasses="flex-fill"
				collapsed="<%= false %>"
				collapsible="<%= false %>"
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<div class="row">
					<div class="col">
						<aui:input defaultLanguageId="<%= themeDisplay.getLanguageId() %>" label="title" localized="<%= true %>" name="labelMapAsXML" required="<%= true %>" type="text" />

						<aui:input name="priority" />

						<aui:input label='<%= HtmlUtil.escape("active") %>' name="active" type="toggle-switch" value="<%= commerceTermEntry.isActive() %>" />
					</div>

					<div class="col-auto">
						<aui:select disabled="<%= true %>" label="type" name="type" required="<%= true %>">

							<%
							for (CommerceTermEntryType commerceTermEntryType : commerceTermEntryDisplayContext.getCommerceTermEntryTypes()) {
							%>

								<aui:option label="<%= commerceTermEntryType.getLabel(locale) %>" value="<%= commerceTermEntryType.getKey() %>" />

							<%
							}
							%>

						</aui:select>

						<aui:input disabled="<%= true %>" label="key" name="name" type="text" />
					</div>
				</div>

				<div class="row">
					<div class="col">

						<%
						String descriptionMapAsXML = StringPool.BLANK;

						if (commerceTermEntry != null) {
							descriptionMapAsXML = commerceTermEntry.getDescriptionMapAsXML();
						}
						%>

						<aui:field-wrapper>
							<label class="control-label" for="<portlet:namespace />descriptionMapAsXML"><liferay-ui:message key="description" /></label>

							<div class="entry-content form-group">
								<liferay-editor:input-localized
									defaultLanguageId="<%= themeDisplay.getLanguageId() %>"
									name="descriptionMapAsXML"
									xml="<%= descriptionMapAsXML %>"
								/>
							</div>
						</aui:field-wrapper>
					</div>
				</div>
			</commerce-ui:panel>
		</div>

		<div class="col-12 col-xl-4">
			<commerce-ui:panel
				bodyClasses="flex-fill"
				title='<%= LanguageUtil.get(request, "schedule") %>'
			>
				<liferay-ui:error exception="<%= CommerceTermEntryExpirationDateException.class %>" message="please-select-a-valid-expiration-date" />

				<aui:input formName="fm" label="publish-date" name="displayDate" />

				<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= neverExpire %>" formName="fm" name="expirationDate" />
			</commerce-ui:panel>
		</div>
	</div>
</aui:form>