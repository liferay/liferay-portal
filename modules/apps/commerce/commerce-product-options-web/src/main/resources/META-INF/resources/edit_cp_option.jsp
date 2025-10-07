<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPOptionDisplayContext cpOptionDisplayContext = (CPOptionDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPOption cpOption = cpOptionDisplayContext.getCPOption();

long cpOptionId = cpOptionDisplayContext.getCPOptionId();

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(String.valueOf(renderResponse.createRenderURL()));
%>

<portlet:actionURL name="/cp_options/edit_cp_option" var="editOptionActionURL" />

<liferay-portlet:renderURL var="editCPOptionExternalReferenceCodeURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/cp_options/edit_cp_option_external_reference_code" />
	<portlet:param name="cpOptionId" value="<%= String.valueOf(cpOptionId) %>" />
</liferay-portlet:renderURL>

<commerce-ui:header
	actions="<%= cpOptionDisplayContext.getHeaderActionModels() %>"
	bean="<%= cpOption %>"
	beanIdLabel="id"
	externalReferenceCode="<%= cpOption.getExternalReferenceCode() %>"
	externalReferenceCodeEditUrl="<%= editCPOptionExternalReferenceCodeURL %>"
	model="<%= CPOption.class %>"
	title="<%= cpOption.getName(locale) %>"
	wrapperCssClasses="side-panel-top-anchor"
/>

<aui:form action="<%= editOptionActionURL %>" cssClass="col pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpOption == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpOptionId" type="hidden" value="<%= String.valueOf(cpOptionId) %>" />

	<div class="container">
		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "details") %>'
		>

			<%
			List<CommerceOptionType> commerceOptionTypes = cpOptionDisplayContext.getCommerceOptionTypes();
			%>

			<liferay-ui:error-marker
				key="<%= WebKeys.ERROR_SECTION %>"
				value="product-option-details"
			/>

			<aui:model-context bean="<%= cpOption %>" model="<%= CPOption.class %>" />

			<liferay-ui:error exception="<%= CPOptionKeyException.class %>" message="that-key-is-already-being-used" />

			<aui:fieldset>
				<aui:input name="name" wrapperCssClass="commerce-product-option-title" />

				<aui:input name="description" wrapperCssClass="commerce-product-option-description" />

				<aui:select label="option-field-type" name="commerceOptionTypeKey" showEmptyOption="<%= true %>">

					<%
					for (CommerceOptionType commerceOptionType : commerceOptionTypes) {
					%>

						<aui:option label="<%= commerceOptionType.getLabel(locale) %>" selected="<%= (cpOption != null) && cpOption.getCommerceOptionTypeKey().equals(commerceOptionType.getKey()) %>" value="<%= commerceOptionType.getKey() %>" />

					<%
					}
					%>

				</aui:select>

				<aui:input checked="<%= (cpOption == null) ? false : cpOption.isFacetable() %>" label="use-in-faceted-navigation" name="facetable" type="toggle-switch" />

				<aui:input checked="<%= (cpOption == null) ? false : cpOption.getRequired() %>" name="required" type="toggle-switch" />

				<aui:input checked="<%= (cpOption == null) ? false : cpOption.isSkuContributor() %>" name="skuContributor" type="toggle-switch" />

				<aui:input helpMessage="key-help" name="key" />
			</aui:fieldset>

			<c:if test="<%= CustomAttributesUtil.hasCustomAttributes(company.getCompanyId(), CPOption.class.getName(), cpOptionId, null) %>">
				<aui:fieldset>
					<liferay-expando:custom-attribute-list
						className="<%= CPOption.class.getName() %>"
						classPK="<%= (cpOption != null) ? cpOption.getCPOptionId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</aui:fieldset>
			</c:if>
		</commerce-ui:panel>

		<c:if test="<%= cpOptionDisplayContext.hasValues(cpOption) %>">
			<commerce-ui:panel
				bodyClasses="p-0"
				title='<%= LanguageUtil.get(request, "values") %>'
			>
				<frontend-data-set:headless-display
					apiURL='<%= "/o/headless-commerce-admin-catalog/v1.0/options/" + cpOptionId + "/optionValues" %>'
					creationMenu="<%= cpOptionDisplayContext.getOptionValueCreationMenu(cpOptionId) %>"
					fdsActionDropdownItems="<%= cpOptionDisplayContext.getOptionValueFDSActionDropdownItems() %>"
					id="<%= CommerceOptionFDSNames.OPTION_VALUES %>"
					itemsPerPage="<%= 10 %>"
					style="stacked"
				/>
			</commerce-ui:panel>
		</c:if>
	</div>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"allowedSkuContributorTypeNames", StringUtil.merge(CPConstants.PRODUCT_OPTION_SKU_CONTRIBUTOR_FIELD_TYPES, StringPool.COMMA)
		).put(
			"availableTypeNames", cpOptionDisplayContext.getCommerceOptionTypeKeys()
		).build()
	%>'
	module="{editCpOptionAndValue} from commerce-product-options-web"
/>