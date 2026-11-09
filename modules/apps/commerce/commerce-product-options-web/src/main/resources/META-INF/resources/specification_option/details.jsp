<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPSpecificationOption cpSpecificationOption = (CPSpecificationOption)request.getAttribute(CPWebKeys.CP_SPECIFICATION_OPTION);
CPSpecificationOptionDisplayContext cpSpecificationOptionDisplayContext = (CPSpecificationOptionDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

long cpOptionCategoryId = BeanParamUtil.getLong(cpSpecificationOption, request, "CPOptionCategoryId");

List<CPOptionCategory> cpOptionCategories = cpSpecificationOptionDisplayContext.getCPOptionCategories();
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="details"
/>

<aui:model-context bean="<%= cpSpecificationOption %>" model="<%= CPSpecificationOption.class %>" />

<liferay-ui:error exception="<%= CPSpecificationOptionKeyException.MustNotBeDuplicate.class %>" message="that-key-is-already-being-used" />
<liferay-ui:error exception="<%= CPSpecificationOptionKeyException.MustNotBeNull.class %>" message="please-enter-a-valid-key" />

<commerce-ui:panel
	elementClasses="mt-4"
>
	<aui:fieldset>
		<aui:input label="label" name="title" />

		<aui:input name="description" />

		<aui:input checked="<%= (cpSpecificationOption == null) ? false : cpSpecificationOption.isFacetable() %>" inlineLabel="right" label="use-in-faceted-navigation" labelCssClass="simple-toggle-switch" name="facetable" type="toggle-switch" />

		<aui:input checked="<%= (cpSpecificationOption == null) ? true : cpSpecificationOption.isVisible() %>" inlineLabel="right" label="visible" labelCssClass="simple-toggle-switch" name="visible" type="toggle-switch" />

		<aui:select label="default-specification-group" name="CPOptionCategoryId" showEmptyOption="<%= true %>">

			<%
			for (CPOptionCategory cpOptionCategory : cpOptionCategories) {
			%>

				<aui:option label="<%= HtmlUtil.escape(cpOptionCategory.getTitle(locale)) %>" selected="<%= cpOptionCategoryId == cpOptionCategory.getCPOptionCategoryId() %>" value="<%= cpOptionCategory.getCPOptionCategoryId() %>" />

			<%
			}
			%>

		</aui:select>

		<aui:input helpMessage="key-help" name="key" />

		<aui:input name="priority" />
	</aui:fieldset>
</commerce-ui:panel>

<commerce-ui:panel
	elementClasses="mt-4"
	title='<%= LanguageUtil.get(request, "picklist") %>'
>
	<frontend-data-set:classic-display
		additionalProps='<%=
			HashMapBuilder.<String, Object>put(
				"specificationId", (cpSpecificationOption == null) ? 0 : cpSpecificationOption.getCPSpecificationOptionId()
			).build()
		%>'
		contextParams='<%=
			HashMapBuilder.put(
				"specificationId", (cpSpecificationOption == null) ? "0" : String.valueOf(cpSpecificationOption.getCPSpecificationOptionId())
			).build()
		%>'
		creationMenu="<%= cpSpecificationOptionDisplayContext.getCreationMenu(cpSpecificationOption) %>"
		dataProviderKey="<%= CommerceSpecificationOptionFDSNames.LIST_TYPE_DEFINITIONS %>"
		id="<%= CommerceSpecificationOptionFDSNames.LIST_TYPE_DEFINITIONS %>"
		propsTransformer="{CPSpecificationOptionListTypeDefinitionPropsTransformer} from commerce-product-options-web"
		style="stacked"
	/>
</commerce-ui:panel>

<div>
	<react:component
		module="{ListTypeEntriesModal} from object-web"
	/>
</div>

<div>
	<react:component
		module="{ModalDeleteListType} from object-web"
	/>
</div>

<c:if test="<%= cpSpecificationOption == null %>">
	<liferay-frontend:component
		module="{CPSpecificationOptionDetails} from commerce-product-options-web"
	/>
</c:if>