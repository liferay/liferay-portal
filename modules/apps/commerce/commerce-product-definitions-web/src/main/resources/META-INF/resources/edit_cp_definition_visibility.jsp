<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionsDisplayContext cpDefinitionsDisplayContext = (CPDefinitionsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDefinition cpDefinition = cpDefinitionsDisplayContext.getCPDefinition();

long cpDefinitionId = cpDefinitionsDisplayContext.getCPDefinitionId();

Map<String, String> contextParams = HashMapBuilder.<String, String>put(
	"cpDefinitionId", String.valueOf(cpDefinitionId)
).put(
	"permissionUserId", String.valueOf(themeDisplay.getUserId())
).build();
%>

<portlet:actionURL name="/cp_definitions/edit_cp_definition" var="editProductDefinitionActionURL" />

<aui:form action="<%= editProductDefinitionActionURL %>" cssClass="pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="updateVisibility" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpDefinitionId" type="hidden" value="<%= String.valueOf(cpDefinitionId) %>" />
	<aui:input name="commerceAccountGroupIds" type="hidden" value="" />
	<aui:input name="commerceChannelIds" type="hidden" value="" />
	<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_SAVE_DRAFT %>" />

	<c:if test="<%= cpDefinitionsDisplayContext.hasManageCommerceProductChannelVisibility() %>">
		<commerce-ui:panel
			bodyClasses="p-0"
			collapsed="<%= !cpDefinition.isChannelFilterEnabled() %>"
			collapseLabel='<%= LanguageUtil.get(request, "filter") %>'
			collapseSwitchName='<%= liferayPortletResponse.getNamespace() + "channelFilterEnabled" %>'
			title='<%= LanguageUtil.get(request, "channels") %>'
		>
			<frontend-data-set:classic-display
				contextParams="<%= contextParams %>"
				creationMenu="<%= cpDefinitionsDisplayContext.getChannelsCreationMenu() %>"
				dataProviderKey="<%= CommerceProductFDSNames.PRODUCT_CHANNELS %>"
				formName="fm"
				id="<%= CommerceProductFDSNames.PRODUCT_CHANNELS %>"
			/>
		</commerce-ui:panel>
	</c:if>

	<commerce-ui:panel
		bodyClasses="p-0"
		collapsed="<%= !cpDefinition.isAccountGroupFilterEnabled() %>"
		collapseLabel='<%= LanguageUtil.get(request, "filter") %>'
		collapseSwitchName='<%= liferayPortletResponse.getNamespace() + "accountGroupFilterEnabled" %>'
		title='<%= LanguageUtil.get(request, "account-groups") %>'
	>
		<frontend-data-set:classic-display
			contextParams="<%= contextParams %>"
			creationMenu="<%= cpDefinitionsDisplayContext.getAccountGroupsCreationMenu() %>"
			dataProviderKey="<%= CommerceProductFDSNames.PRODUCT_ACCOUNT_GROUPS %>"
			formName="fm"
			id="<%= CommerceProductFDSNames.PRODUCT_ACCOUNT_GROUPS %>"
		/>
	</commerce-ui:panel>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"accountGroupDataSetId", CommerceProductFDSNames.PRODUCT_ACCOUNT_GROUPS
		).put(
			"accountGroupItemSelectorURL", cpDefinitionsDisplayContext.getAccountGroupItemSelectorUrl()
		).put(
			"channelDataSetId", CommerceProductFDSNames.PRODUCT_CHANNELS
		).put(
			"channelItemSelectorURL", cpDefinitionsDisplayContext.getChannelItemSelectorUrl()
		).put(
			"checkedAccountGroupIds", cpDefinitionsDisplayContext.getCheckedCommerceAccountGroupIds()
		).put(
			"checkedCommerceChannelIds", cpDefinitionsDisplayContext.getCheckedCommerceChannelIds()
		).put(
			"productId", cpDefinition.getCProductId()
		).build()
	%>'
	module="{editCpDefinitionVisibility} from commerce-product-definitions-web"
/>