<style>
	.app-container {
		border-color: #2e5aac !important;
		color: #2e5aac;
		font-size: MEDIUM;
	}

	.app-category {
		display: block !important;
		flex: 1;
		font-size: 11px;
		max-width: 200px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.app-container .app-category {
		background-color: #e6ebf5;
		color: #1c3667;
		font-weight: 600;
		height: 20px;
		padding: 3px 8px;
	}

	.app-container .app-product-type {
		font-size: 11px;
		font-weight: 600;
		height: 20px;
		min-width: 0;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.client-extension-product-type {
		background-color: #FFE6C6;
		color: #9D4C00;
	}

	.cloud-product-type {
		background-color: #D1EEDC;
		color: #0E7835;
	}

	.composite-app-product-type {
		background-color: #FBE0FF;
		color: #720086;
	}

	.diamond-icon-container {
		color: #C9C9CF;
		height: 4px;
		width: 4px;
	}

	.dxp-product-type {
		background-color: #D1ECFA;
		color: #166E9E;
	}

	.low-code-configuration-product-type {
		background-color: #DCD7E9;
		color: #503690;
	}

	@media screen and (max-width: 768px) {
		.app-container {
			font-size: small;
		}
	}

	@media screen and (max-width: 576px) {
		.app-container {
			font-size: x-small;
		}

		.app-container .app-category,
		.app-container .app-product-type {
			padding: 2px 4px;
		}
	}
</style>

<#assign
	productTypeValues =
		{
			"client-extension": "Client Extension",
			"cloud": "Cloud App",
			"composite-app": "Composite App",
			"dxp": "DXP App",
			"low-code-configuration": "Low-Code"
		}

	vocabularyProductCategory = "MARKETPLACE APP CATEGORY"
/>

<#if themeDisplay?has_content>
	<#assign scopeGroupId = themeDisplay.getScopeGroupId() />
</#if>

<#if currentURL?has_content>
	<#if currentURL?contains('web')>
		<#assign
			index = 2
			partsUrl = currentURL?split('/')
			siteName = partsUrl[index..index]?join('/')
		/>
	</#if>
</#if>

<#assign channel = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels?accountId=-1&filter=name eq 'Marketplace Channel' and siteGroupId eq '${scopeGroupId}'") />

<#if channel?has_content>
	<#assign channelId = channel.items[0].id />
</#if>

<#if (CPDefinition_cProductId.getData())??>
	<#assign productId = CPDefinition_cProductId.getData() />
</#if>

<#assign
	product = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels/"+ channelId +"/products/"+ productId +"?accountId=-1&nestedFields=productSpecifications,categories")
	categories = product.categories![]
	productSpecifications = product.productSpecifications![]
/>

<div class="app-container color-neutral-3 d-flex flex-wrap font-size-paragraph-small justify-content-between w-100">
	<div class="d-flex">
		<#if productSpecifications?has_content>
			<#assign productTypes = productSpecifications?filter(item -> stringUtil.equals(item.specificationKey, "type")) />

			<#list productTypes as productType>
				<#if productType?has_content>
					<#assign appType = (productTypeValues[productType.value]!) />

					<#if appType?has_content>
						<div class="align-items-center app-product-type border border-radius-small d-flex mb-1 mr-2 px-2 rounded-lg ${productType.value}-product-type">
							<span class="bg-neutral-8">${appType}</span>
						</div>
					</#if>
				</#if>
			</#list>
		</#if>

		<#if categories?has_content>
			<#assign filteredCategories = categories?filter(category -> category.vocabulary?upper_case == vocabularyProductCategory) />

			<#if filteredCategories?has_content && appType?has_content>
				<span class="align-items-center d-flex justify-content-between">
					<span class="align-items-center d-flex diamond-icon-container justify-content-between mr-3">
						<@clay["icon"] symbol="diamond" />
					</span>
				</span>
			</#if>

			<#list categories as category>
				<#if category.vocabulary?upper_case == vocabularyProductCategory>
					<span class="app-category bg-neutral-8 border-radius-small mb-1 mr-2 px-3 rounded-lg" title="${category.name}">
						${category.name}
					</span>
				</#if>
			</#list>
		</#if>
	</div>
</div>