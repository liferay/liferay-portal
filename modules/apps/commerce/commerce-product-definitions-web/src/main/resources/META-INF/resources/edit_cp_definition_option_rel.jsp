<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionOptionRelDisplayContext cpDefinitionOptionRelDisplayContext = (CPDefinitionOptionRelDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CPDefinitionOptionRel cpDefinitionOptionRel = cpDefinitionOptionRelDisplayContext.getCPDefinitionOptionRel();

String name = ParamUtil.getString(request, "name", cpDefinitionOptionRel.getName());
String description = ParamUtil.getString(request, "description", cpDefinitionOptionRel.getDescription());
String commerceOptionTypeKey = ParamUtil.getString(request, "commerceOptionTypeKey", cpDefinitionOptionRel.getCommerceOptionTypeKey());
String infoItemServiceKey = ParamUtil.getString(request, "infoItemServiceKey", cpDefinitionOptionRel.getInfoItemServiceKey());
String priority = ParamUtil.getString(request, "priority", String.valueOf(cpDefinitionOptionRel.getPriority()));
boolean facetable = ParamUtil.getBoolean(request, "facetable", cpDefinitionOptionRel.isFacetable());
boolean required = ParamUtil.getBoolean(request, "required", cpDefinitionOptionRel.isRequired());
boolean skuContributor = ParamUtil.getBoolean(request, "skuContributor", cpDefinitionOptionRel.isSkuContributor());
String priceType = ParamUtil.getString(request, "priceType", cpDefinitionOptionRel.getPriceType());

cpDefinitionOptionRel.setName(name);
cpDefinitionOptionRel.setDescription(description);
cpDefinitionOptionRel.setCommerceOptionTypeKey(commerceOptionTypeKey);

long cpDefinitionOptionRelId = cpDefinitionOptionRelDisplayContext.getCPDefinitionOptionRelId();
String defaultLanguageId = cpDefinitionOptionRelDisplayContext.getCatalogDefaultLanguageId();
%>

<portlet:actionURL name="/cp_definitions/edit_cp_definition_option_rel" var="editProductDefinitionOptionRelActionURL" />

<liferay-frontend:side-panel-content
	title='<%= (cpDefinitionOptionRel == null) ? LanguageUtil.get(request, "add-option") : LanguageUtil.format(request, "edit-x", cpDefinitionOptionRel.getName(languageId), false) %>'
>
	<aui:form action="<%= editProductDefinitionOptionRelActionURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

		<portlet:renderURL var="redirectURL">
			<portlet:param name="mvcRenderCommandName" value="/cp_definitions/edit_cp_definition_option_rel" />
			<portlet:param name="cpDefinitionId" value="<%= String.valueOf(cpDefinitionOptionRel.getCPDefinitionId()) %>" />
			<portlet:param name="cpDefinitionOptionRelId" value="<%= String.valueOf(cpDefinitionOptionRel.getCPDefinitionOptionRelId()) %>" />
		</portlet:renderURL>

		<aui:input name="redirect" type="hidden" value="<%= redirectURL %>" />

		<aui:input name="cpDefinitionId" type="hidden" value="<%= String.valueOf(cpDefinitionOptionRel.getCPDefinitionId()) %>" />
		<aui:input name="cpDefinitionOptionRelId" type="hidden" value="<%= String.valueOf(cpDefinitionOptionRelId) %>" />
		<aui:input name="cpOptionId" type="hidden" value="<%= cpDefinitionOptionRel.getCPOptionId() %>" />

		<liferay-ui:error exception="<%= CPDefinitionOptionRelPriceTypeException.class %>" message="price-type-cannot-be-changed-for-the-current-option-value-setup" />
		<liferay-ui:error exception="<%= CPDefinitionOptionRelPriceTypeException.class %>" message="price-type-can-only-be-dynamic-for-externally-defined-product-options" />
		<liferay-ui:error exception="<%= CPDefinitionOptionSKUContributorException.class %>" message="sku-contributor-cannot-be-set-as-true-for-the-selected-field-type" />

		<aui:model-context bean="<%= cpDefinitionOptionRel %>" model="<%= CPDefinitionOptionRel.class %>" />

		<commerce-ui:panel
			title='<%= LanguageUtil.get(request, "details") %>'
		>
			<div class="row">
				<div class="col-12">
					<aui:input defaultLanguageId="<%= defaultLanguageId %>" name="name" value="<%= name %>" />
				</div>

				<div class="col-6">
					<aui:input defaultLanguageId="<%= defaultLanguageId %>" name="description" value="<%= description %>" />
				</div>

				<div class="col-6">
					<aui:input label="position" name="priority" value="<%= priority %>">
						<aui:validator name="min">[0]</aui:validator>
						<aui:validator name="number" />
					</aui:input>
				</div>

				<div class="col-3">
					<aui:input checked="<%= (cpDefinitionOptionRel == null) ? false : facetable %>" inlineField="<%= true %>" label="use-in-faceted-navigation" name="facetable" type="toggle-switch" />
				</div>

				<div class="col-3">
					<aui:input checked="<%= (cpDefinitionOptionRel == null) ? false : required %>" inlineField="<%= true %>" name="required" type="toggle-switch" />
				</div>

				<div class="col-3">
					<aui:input checked="<%= (cpDefinitionOptionRel == null) ? false : skuContributor %>" inlineField="<%= true %>" name="skuContributor" type="toggle-switch" />
				</div>

				<div class="col-3">
					<aui:input checked="<%= (cpDefinitionOptionRel == null) ? false : cpDefinitionOptionRel.isDefinedExternally() %>" inlineField="<%= true %>" label="define-externally" name="definedExternally" type="toggle-switch" />
				</div>

				<div class="col-12">
					<aui:select label="field-type" name="commerceOptionTypeKey" showEmptyOption="<%= true %>">

						<%
						for (CommerceOptionType commerceOptionType : cpDefinitionOptionRelDisplayContext.getCommerceOptionTypes()) {
						%>

							<aui:option label="<%= commerceOptionType.getLabel(locale) %>" selected="<%= (cpDefinitionOptionRel != null) && cpDefinitionOptionRel.getCommerceOptionTypeKey().equals(commerceOptionType.getKey()) %>" value="<%= commerceOptionType.getKey() %>" />

						<%
						}
						%>

					</aui:select>
				</div>

				<div class="col-12">
					<aui:select name="priceType" showEmptyOption="<%= true %>">
						<aui:option label="static" selected="<%= (cpDefinitionOptionRel != null) && priceType.equals(CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC) %>" value="<%= CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC %>" />
						<aui:option label="dynamic" selected="<%= (cpDefinitionOptionRel != null) && priceType.equals(CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC) %>" value="<%= CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC %>" />
					</aui:select>
				</div>

				<div class="<%= cpDefinitionOptionRel.isDefinedExternally() ? "col-12" : "col-12 hide" %>">
					<aui:select label="collection-provider" name="infoItemServiceKey" onChange='<%= liferayPortletResponse.getNamespace() + "selectCollectionProvider();" %>' showEmptyOption="<%= true %>">

						<%
						for (RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider : cpDefinitionOptionRelDisplayContext.getRelatedInfoItemCollectionProviders()) {
						%>

							<aui:option label="<%= HtmlUtil.escape(relatedInfoItemCollectionProvider.getLabel(locale)) %>" selected="<%= infoItemServiceKey.equals(relatedInfoItemCollectionProvider.getCollectionItemClassName()) %>" value="<%= relatedInfoItemCollectionProvider.getClass().getName() %>" />

						<%
						}
						%>

					</aui:select>
				</div>

				<div class="<%= cpDefinitionOptionRel.isDefinedExternally() ? "col-12" : "col-12 hide" %>">
					<clay:multiselect
						id="categoryIds"
						inputName='<%= liferayPortletResponse.getNamespace() + "categoryIds" %>'
						label='<%= LanguageUtil.get(request, "category") %>'
						selectedMultiselectItems="<%= cpDefinitionOptionRelDisplayContext.getSelectedCategoriesMultiselectItems(locale) %>"
						sourceMultiselectItems="<%= cpDefinitionOptionRelDisplayContext.getCategoriesMultiselectItems(infoItemServiceKey, locale) %>"
					/>
				</div>
			</div>
		</commerce-ui:panel>

		<c:if test="<%= cpDefinitionOptionRelDisplayContext.hasCustomAttributesAvailable() %>">
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "custom-attribute") %>'
			>
				<liferay-expando:custom-attribute-list
					className="<%= CPDefinitionOptionRel.class.getName() %>"
					classPK="<%= (cpDefinitionOptionRel != null) ? cpDefinitionOptionRel.getCPDefinitionOptionRelId() : 0 %>"
					editable="<%= true %>"
					label="<%= true %>"
				/>
			</commerce-ui:panel>
		</c:if>

		<div class="d-none" id="values-container">
			<commerce-ui:panel
				bodyClasses="p-0"
				title='<%= LanguageUtil.get(request, "values") %>'
			>

				<%
				String dataSetDisplayId = CommerceProductFDSNames.PRODUCT_OPTION_VALUES;

				if (cpDefinitionOptionRel.isPriceTypeStatic()) {
					dataSetDisplayId = CommerceProductFDSNames.PRODUCT_OPTION_VALUES_STATIC;
				}
				%>

				<frontend-data-set:classic-display
					contextParams='<%=
						HashMapBuilder.<String, String>put(
							"cpDefinitionOptionRelId", String.valueOf(cpDefinitionOptionRelId)
						).build()
					%>'
					creationMenu="<%= cpDefinitionOptionRel.isDefinedExternally() ? null : cpDefinitionOptionRelDisplayContext.getCreationMenu() %>"
					dataProviderKey="<%= CommerceProductFDSNames.PRODUCT_OPTION_VALUES %>"
					id="<%= dataSetDisplayId %>"
					itemsPerPage="<%= 10 %>"
					selectedItemsKey="cpdefinitionOptionValueRelId"
				/>
			</commerce-ui:panel>
		</div>

		<aui:script>
			var allowedPriceContributorTypeNames =
				'<%= StringUtil.merge(CPConstants.PRODUCT_OPTION_PRICE_CONTRIBUTOR_FIELD_TYPES, StringPool.COMMA) %>';
			var allowedPriceContributorFieldTypeSelectOptions =
				allowedPriceContributorTypeNames.split('<%= StringPool.COMMA %>');
			var allowedSkuContributorTypeNames =
				'<%= StringUtil.merge(CPConstants.PRODUCT_OPTION_SKU_CONTRIBUTOR_FIELD_TYPES, StringPool.COMMA) %>';
			var allowedSkuContributorFieldTypeSelectOptions =
				allowedSkuContributorTypeNames.split('<%= StringPool.COMMA %>');
			var availableTypeNames =
				'<%= cpDefinitionOptionRelDisplayContext.getCommerceOptionTypeKeys() %>';
			var availableFieldTypeSelectOptions = availableTypeNames.split(
				'<%= StringPool.COMMA %>'
			);
			var defineExternallyInput = document.getElementById(
				'<portlet:namespace />definedExternally'
			);
			var multipleValuesTypeNames =
				'<%= StringUtil.merge(CPConstants.PRODUCT_OPTION_MULTIPLE_VALUES_FIELD_TYPES, StringPool.COMMA) %>';
			var multipleValuesFieldTypeSelectOptions = multipleValuesTypeNames.split(
				'<%= StringPool.COMMA %>'
			);

			var formFieldTypeSelect = document.getElementById(
				'<portlet:namespace />commerceOptionTypeKey'
			);
			var priceTypeSelect = document.getElementById('<portlet:namespace />priceType');
			var skuContributorInput = document.getElementById(
				'<portlet:namespace />skuContributor'
			);
			var valuesContainer = document.getElementById('values-container');

			function checkDDMFormFieldType(event) {
				var priceTypeSelectValue =
					priceTypeSelect.options[priceTypeSelect.selectedIndex].value;
				var skuContributorInputChecked = skuContributorInput.checked;

				enableFormFieldTypeSelectOptionValues(availableFieldTypeSelectOptions);

				if (priceTypeSelectValue != '') {
					enableFormFieldTypeSelectOptionValues(
						allowedPriceContributorFieldTypeSelectOptions
					);
				}

				if (skuContributorInputChecked) {
					enableFormFieldTypeSelectOptionValues(
						allowedSkuContributorFieldTypeSelectOptions
					);
				}
			}

			function enableFormFieldTypeSelectOptionValues(array) {
				if (
					formFieldTypeSelect.value != '' &&
					!endsWith(formFieldTypeSelect.value, array)
				) {
					Liferay.Util.openAlertModal({
						message:
							'<liferay-ui:message key="selected-field-type-price-type-and-sku-contributor-combination-is-not-allowed" />',
					});

					return;
				}

				for (var i = 0; i < formFieldTypeSelect.options.length; i++) {
					var formFieldTypeSelectOption = formFieldTypeSelect.options[i];

					if (formFieldTypeSelectOption.value == '') {
						continue;
					}

					if (endsWith(formFieldTypeSelectOption.value, array)) {
						if (formFieldTypeSelectOption.getAttribute('disabled')) {
							formFieldTypeSelectOption.removeAttribute('disabled');
						}

						continue;
					}

					formFieldTypeSelectOption.setAttribute('disabled', true);
				}
			}

			function handleFormFieldTypeSelectChanges() {
				if (
					endsWith(
						formFieldTypeSelect.value,
						allowedPriceContributorFieldTypeSelectOptions
					)
				) {
					enable(defineExternallyInput);
					enable(priceTypeSelect);
				}
				else {
					if (priceTypeSelect.value == '') {
						disable(defineExternallyInput);
						disable(priceTypeSelect);
					}
					else {
						Liferay.Util.openAlertModal(
							'<liferay-ui:message key="selected-field-type-price-type-and-sku-contributor-combination-is-not-allowed" />'
						);

						return;
					}
				}

				if (
					endsWith(
						formFieldTypeSelect.value,
						allowedSkuContributorFieldTypeSelectOptions
					)
				) {
					enable(skuContributorInput);
				}
				else {
					if (!skuContributorInput.checked) {
						disable(skuContributorInput);
					}
					else {
						Liferay.Util.openAlertModal({
							message:
								'<liferay-ui:message key="selected-field-type-price-type-and-sku-contributor-combination-is-not-allowed" />',
						});

						return;
					}
				}

				if (
					endsWith(
						formFieldTypeSelect.value,
						multipleValuesFieldTypeSelectOptions
					)
				) {
					valuesContainer.classList.remove('d-none');
				}
				else {
					valuesContainer.classList.add('d-none');
				}
			}

			function disable(element) {
				if (!element.getAttribute('disabled')) {
					element.setAttribute('disabled', true);
				}
			}

			function enable(element) {
				if (element.getAttribute('disabled')) {
					element.removeAttribute('disabled');
				}
			}

			function endsWith(value, array) {
				value = value.toLowerCase();

				for (var i = 0; i < array.length; i++) {
					if (value.endsWith(array[i].toLowerCase())) {
						return true;
					}
				}

				return false;
			}

			formFieldTypeSelect.addEventListener(
				'change',
				handleFormFieldTypeSelectChanges
			);
			skuContributorInput.addEventListener('change', checkDDMFormFieldType);
			priceTypeSelect.addEventListener('change', checkDDMFormFieldType);
			handleFormFieldTypeSelectChanges();
		</aui:script>

		<aui:button-row>
			<aui:button cssClass="btn-lg" type="submit" value="save" />

			<aui:button cssClass="btn-lg" type="cancel" />
		</aui:button-row>
	</aui:form>
</liferay-frontend:side-panel-content>

<aui:script sandbox="<%= true %>">
	Liferay.provide(window, '<portlet:namespace />selectCollectionProvider', () => {
		const portletURL = Liferay.Util.PortletURL.createPortletURL(
			'<%= currentURLObj %>',
			{
				description: document.getElementById(
					'<portlet:namespace />description'
				).value,
				facetable: document.getElementById('<portlet:namespace />facetable')
					.checked,
				infoItemServiceKey: document.getElementById(
					'<portlet:namespace />infoItemServiceKey'
				).value,
				name: document.getElementById('<portlet:namespace />name').value,
				priceType: document.getElementById('<portlet:namespace />priceType')
					.value,
				priority: document.getElementById('<portlet:namespace />priority')
					.value,
				required: document.getElementById('<portlet:namespace />required')
					.checked,
				skuContributor: document.getElementById(
					'<portlet:namespace />skuContributor'
				).checked,
			}
		);

		window.location.replace(portletURL.toString());
	});
</aui:script>