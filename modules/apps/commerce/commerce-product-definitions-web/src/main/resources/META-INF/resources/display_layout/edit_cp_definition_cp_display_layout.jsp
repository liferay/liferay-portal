<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionDisplayLayoutDisplayContext cpDefinitionDisplayLayoutDisplayContext = (CPDefinitionDisplayLayoutDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDisplayLayout cpDisplayLayout = cpDefinitionDisplayLayoutDisplayContext.getCPDisplayLayout();

List<CPDefinition> cpDefinitionAsList = new ArrayList<>();

if (cpDisplayLayout != null) {
	cpDefinitionAsList = Arrays.asList(cpDisplayLayout.fetchCPDefinition());
}

String searchContainerId = "CPDefinitionsSearchContainer";
%>

<liferay-util:buffer
	var="removeCPDefinitionIcon"
>
	<liferay-ui:icon
		icon="times"
		markupView="lexicon"
		message="remove"
	/>
</liferay-util:buffer>

<liferay-frontend:side-panel-content
	title='<%= (cpDisplayLayout == null) ? LanguageUtil.get(request, "add-display-layout") : LanguageUtil.get(request, "edit-display-layout") %>'
>
	<portlet:actionURL name="/commerce_channels/edit_cp_definition_cp_display_layout" var="editCPDefinitionCPDisplayLayoutActionURL" />

	<aui:form action="<%= editCPDefinitionCPDisplayLayoutActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpDisplayLayout == null) ? Constants.ADD : Constants.UPDATE %>" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
		<aui:input name="cpDisplayLayoutId" type="hidden" value="<%= (cpDisplayLayout == null) ? 0 : cpDisplayLayout.getCPDisplayLayoutId() %>" />
		<aui:input name="classPK" type="hidden" value="<%= (cpDisplayLayout == null) ? 0 : cpDisplayLayout.getClassPK() %>" />
		<aui:input name="commerceChannelId" type="hidden" value="<%= cpDefinitionDisplayLayoutDisplayContext.getCommerceChannelId() %>" />

		<liferay-ui:error exception="<%= CPDisplayLayoutEntryException.class %>" message="please-select-a-valid-product" />
		<liferay-ui:error exception="<%= CPDisplayLayoutEntryUuidException.class %>" message="please-select-a-valid-layout" />
		<liferay-ui:error exception="<%= NoSuchCPDefinitionException.class %>" message="please-select-a-valid-product" />

		<aui:model-context bean="<%= cpDisplayLayout %>" model="<%= CPDisplayLayout.class %>" />

		<div class="sheet">
			<div class="panel-group panel-group-flush">
				<aui:fieldset>
					<liferay-ui:search-container
						curParam="cpDefinitionCur"
						headerNames="null,null"
						id="<%= searchContainerId %>"
						iteratorURL="<%= currentURLObj %>"
						total="<%= cpDefinitionAsList.size() %>"
					>
						<liferay-ui:search-container-results
							results="<%= cpDefinitionAsList %>"
						/>

						<liferay-ui:search-container-row
							className="com.liferay.commerce.product.model.CPDefinition"
							keyProperty="CPDefinitionId"
							modelVar="cpDefinition"
						>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand"
								value="<%= HtmlUtil.escape(cpDefinition.getName(languageId)) %>"
							/>

							<liferay-ui:search-container-column-text>
								<a class="float-right modify-link" data-rowId="<%= cpDefinition.getCPDefinitionId() %>" href="javascript:void(0);"><%= removeCPDefinitionIcon %></a>
							</liferay-ui:search-container-column-text>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator
							markupView="lexicon"
						/>
					</liferay-ui:search-container>

					<aui:button cssClass="mb-4" name="selectProduct" value='<%= LanguageUtil.format(locale, "select-x", "product") %>' />

					<liferay-frontend:screen-navigation
						containerWrapperCssClass="container"
						key="<%= CPDefinitionScreenNavigationConstants.SCREEN_NAVIGATION_KEY_CP_DEFINITION_DISPLAY_LAYOUT_GENERAL %>"
						modelBean="<%= cpDisplayLayout %>"
						portletURL="<%= currentURLObj %>"
					/>
				</aui:fieldset>
			</div>
		</div>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" />
		</aui:button-row>
	</aui:form>
</liferay-frontend:side-panel-content>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"commerceChannelId", cpDefinitionDisplayLayoutDisplayContext.getCommerceChannelId()
		).put(
			"layoutItemSelectorUrl", cpDefinitionDisplayLayoutDisplayContext.getLayoutItemSelectorUrl()
		).put(
			"layoutPageTemplateEntryItemSelectorUrl", cpDefinitionDisplayLayoutDisplayContext.getLayoutPageTemplateEntryItemSelectorURL()
		).put(
			"portletNamespace", liferayPortletResponse.getNamespace()
		).put(
			"removeIcon", removeCPDefinitionIcon
		).put(
			"searchContainerId", searchContainerId
		).build()
	%>'
	module="{EditDisplayLayout} from commerce-product-definitions-web"
/>