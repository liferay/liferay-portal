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
List<CommerceCatalog> commerceCatalogs = cpDefinitionsDisplayContext.getCommerceCatalogs();

String defaultLanguageId = cpDefinitionsDisplayContext.getCatalogDefaultLanguageId();

String productTypeName = BeanParamUtil.getString(cpDefinition, request, "productTypeName");

String friendlyURLBase = themeDisplay.getPortalURL() + cpDefinitionsDisplayContext.getProductURLSeparator();

boolean neverExpire = ParamUtil.getBoolean(request, "neverExpire", true);

if ((cpDefinition != null) && (cpDefinition.getExpirationDate() != null)) {
	neverExpire = false;
}
%>

<c:if test="<%= (cpDefinition != null) && cpDefinition.isPending() %>">
	<div class="alert alert-info">
		<liferay-ui:message key="there-is-a-publication-workflow-in-process" />
	</div>
</c:if>

<portlet:actionURL name="/cp_definitions/edit_cp_definition" var="editProductDefinitionActionURL" />

<aui:form action="<%= editProductDefinitionActionURL %>" cssClass="pt-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpDefinition == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpDefinitionId" type="hidden" value="<%= String.valueOf(cpDefinitionId) %>" />
	<aui:input name="productTypeName" type="hidden" value="<%= productTypeName %>" />
	<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_SAVE_DRAFT %>" />

	<aui:model-context bean="<%= cpDefinition %>" model="<%= CPDefinition.class %>" />

	<liferay-ui:error exception="<%= CPDefinitionMetaDescriptionException.class %>" message="the-meta-description-is-too-long" />
	<liferay-ui:error exception="<%= CPDefinitionMetaKeywordsException.class %>" message="the-meta-keywords-are-too-long" />
	<liferay-ui:error exception="<%= CPDefinitionMetaTitleException.class %>" message="the-meta-title-is-too-long" />
	<liferay-ui:error exception="<%= CPDefinitionNameDefaultLanguageException.class %>" message="please-enter-the-product-name-for-the-default-language" />
	<liferay-ui:error exception="<%= FriendlyURLLengthException.class %>" message="the-friendly-url-is-too-long" />
	<liferay-ui:error exception="<%= NoSuchCatalogException.class %>" message="please-select-a-valid-catalog" />

	<div class="row">
		<div class="col-8">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<c:choose>
					<c:when test="<%= (cpDefinition != null) || ((cpDefinition == null) && (commerceCatalogs.size() > 1)) %>">
						<aui:select disabled="<%= cpDefinition != null %>" label="catalog" name="commerceCatalogGroupId" required="<%= true %>">

							<%
							for (CommerceCatalog curCommerceCatalog : commerceCatalogs) {
							%>

								<aui:option data-languageId="<%= curCommerceCatalog.getCatalogDefaultLanguageId() %>" label="<%= HtmlUtil.escape(curCommerceCatalog.getName()) %>" selected="<%= (cpDefinition == null) ? (commerceCatalogs.size() == 1) : cpDefinitionsDisplayContext.isSelectedCatalog(curCommerceCatalog) %>" value="<%= curCommerceCatalog.getGroupId() %>" />

							<%
							}
							%>

						</aui:select>
					</c:when>
					<c:otherwise>

						<%
						CommerceCatalog commerceCatalog = commerceCatalogs.get(0);
						%>

						<aui:input name="commerceCatalogGroupId" type="hidden" value="<%= commerceCatalog.getGroupId() %>" />
					</c:otherwise>
				</c:choose>

				<aui:input defaultLanguageId="<%= defaultLanguageId %>" label="name" localized="<%= true %>" name="nameMapAsXML" required="<%= true %>" type="text" />

				<aui:input defaultLanguageId="<%= defaultLanguageId %>" label="short-description" localized="<%= true %>" name="shortDescriptionMapAsXML" resizable="<%= true %>" type="textarea" />

				<%
				String descriptionMapAsXML = StringPool.BLANK;

				if (cpDefinition != null) {
					descriptionMapAsXML = cpDefinition.getDescriptionMapAsXML();
				}
				%>

				<aui:field-wrapper>
					<label class="control-label" for="<portlet:namespace />descriptionMapAsXML"><liferay-ui:message key="full-description" /></label>

					<div class="entry-content form-group">
						<c:choose>
							<c:when test='<%= !FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
								<liferay-ui:input-localized
									defaultLanguageId="<%= defaultLanguageId %>"
									name="descriptionMapAsXML"
									type="editor"
									xml="<%= descriptionMapAsXML %>"
								/>
							</c:when>
							<c:otherwise>
								<liferay-editor:input-localized
									defaultLanguageId="<%= defaultLanguageId %>"
									name="descriptionMapAsXML"
									xml="<%= descriptionMapAsXML %>"
								/>
							</c:otherwise>
						</c:choose>
					</div>
				</aui:field-wrapper>
			</commerce-ui:panel>

			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "seo") %>'
			>
				<div class="form-group">
					<label for="<portlet:namespace />urlTitleMapAsXML"><liferay-ui:message key="friendly-url" /><liferay-ui:icon-help message='<%= LanguageUtil.format(request, "for-example-x", "<em>news</em>", false) %>' /></label>

					<liferay-ui:input-localized
						defaultLanguageId="<%= defaultLanguageId %>"
						inputAddon="<%= StringUtil.shorten(friendlyURLBase, 40) %>"
						name="urlTitleMapAsXML"
						xml="<%= HttpComponentsUtil.decodeURL(cpDefinitionsDisplayContext.getUrlTitleMapAsXML()) %>"
					/>
				</div>

				<aui:input defaultLanguageId="<%= defaultLanguageId %>" label="meta-title" localized="<%= true %>" name="metaTitleMapAsXML" type="text" />

				<aui:input defaultLanguageId="<%= defaultLanguageId %>" label="meta-description" localized="<%= true %>" name="metaDescriptionMapAsXML" type="textarea" />

				<aui:input defaultLanguageId="<%= defaultLanguageId %>" label="meta-keywords" localized="<%= true %>" name="metaKeywordsMapAsXML" type="textarea" />
			</commerce-ui:panel>
		</div>

		<div class="col-4">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "categorization") %>'
			>
				<liferay-asset:asset-categories-error />

				<liferay-asset:asset-tags-error />

				<aui:field-wrapper>
					<liferay-asset:asset-categories-selector
						className="<%= CPDefinition.class.getName() %>"
						classPK="<%= cpDefinitionId %>"
						groupIds="<%= new long[] {company.getGroupId()} %>"
						visibilityTypes="<%= AssetVocabularyConstants.VISIBILITY_TYPES %>"
					/>
				</aui:field-wrapper>

				<aui:field-wrapper>
					<liferay-asset:asset-tags-selector
						className="<%= CPDefinition.class.getName() %>"
						classPK="<%= cpDefinitionId %>"
						groupIds="<%= new long[] {company.getGroupId()} %>"
					/>
				</aui:field-wrapper>
			</commerce-ui:panel>

			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "schedule") %>'
			>
				<liferay-ui:error exception="<%= CPDefinitionExpirationDateException.class %>" message="please-select-a-valid-expiration-date" />

				<aui:input name="published" />

				<aui:input formName="fm" name="displayDate" />

				<aui:input dateTogglerCheckboxLabel="never-expire" disabled="<%= neverExpire %>" formName="fm" name="expirationDate" />
			</commerce-ui:panel>

			<c:if test="<%= cpDefinitionsDisplayContext.hasCustomAttributesAvailable() %>">
				<commerce-ui:panel
					title='<%= LanguageUtil.get(request, "custom-attribute") %>'
				>
					<liferay-expando:custom-attribute-list
						className="<%= CPDefinition.class.getName() %>"
						classPK="<%= (cpDefinition != null) ? cpDefinition.getCPDefinitionId() : 0 %>"
						editable="<%= true %>"
						label="<%= true %>"
					/>
				</commerce-ui:panel>
			</c:if>
		</div>

		<c:if test="<%= cpDefinition != null %>">
			<div class="col-12">
				<commerce-ui:panel
					bodyClasses="p-0"
					title='<%= LanguageUtil.get(request, "specifications") %>'
				>
					<frontend-data-set:classic-display
						contextParams='<%=
							HashMapBuilder.<String, String>put(
								"cpDefinitionId", String.valueOf(cpDefinitionId)
							).build()
						%>'
						creationMenu="<%= cpDefinitionsDisplayContext.getCPDefinitionSpecificationOptionValueCreationMenu() %>"
						dataProviderKey="<%= CommerceProductFDSNames.PRODUCT_DEFINITION_SPECIFICATIONS %>"
						formName="fm"
						id="<%= CommerceProductFDSNames.PRODUCT_DEFINITION_SPECIFICATIONS %>"
						itemsPerPage="<%= 10 %>"
						selectedItemsKey="cpdefinitionSpecificationOptionValueId"
						showManagementBar="<%= true %>"
						showSearch="<%= true %>"
					/>
				</commerce-ui:panel>
			</div>
		</c:if>
	</div>
</aui:form>

<c:if test="<%= cpDefinition == null %>">
	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"namespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
		module="{debounceDetails} from commerce-product-definitions-web"
	/>

	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"portletNamespace", liferayPortletResponse.getNamespace()
			).build()
		%>'
		module="{changeLocalizedInputs} from commerce-product-definitions-web"
	/>
</c:if>