<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionSpecificationOptionValueDisplayContext cpDefinitionSpecificationOptionValueDisplayContext = (CPDefinitionSpecificationOptionValueDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDefinition cpDefinition = cpDefinitionSpecificationOptionValueDisplayContext.getCPDefinition();

CommerceCatalog commerceCatalog = cpDefinition.getCommerceCatalog();
%>

<commerce-ui:modal-content
	title='<%= LanguageUtil.get(request, "create-new-specification-value") %>'
>
	<aui:form cssClass="container-fluid container-fluid-max-xl" method="post" name="fm" onSubmit='<%= "event.preventDefault(); " + liferayPortletResponse.getNamespace() + "apiSubmit();" %>'>
		<react:component
			module="{CPDefinitionSpecificationOptionValueAutocomplete} from commerce-product-definitions-web"
			props='<%=
				HashMapBuilder.<String, Object>put(
					"catalogDefaultLanguageId", commerceCatalog.getCatalogDefaultLanguageId()
				).put(
					"createNewSpecification", ParamUtil.getBoolean(request, "createNewSpecification")
				).put(
					"siteLanguage", LanguageUtil.getLanguageId(LocaleUtil.getSiteDefault())
				).build()
			%>'
		/>
	</aui:form>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"catalogDefaultLanguageId", commerceCatalog.getCatalogDefaultLanguageId()
			).put(
				"namespace", liferayPortletResponse.getNamespace()
			).put(
				"productId", cpDefinition.getCProductId()
			).build()
		%>'
		module="{addCPDefinitionSpecificationOptionValue} from commerce-product-definitions-web"
	/>
</commerce-ui:modal-content>