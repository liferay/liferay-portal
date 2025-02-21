<style type="text/css">
	.adt-apps-search-results .app-search-results-card:hover {
		color: var(--black);
	}

	.lfr-layout-structure-item-com-liferay-site-navigation-breadcrumb-web-portlet-sitenavigationbreadcrumbportlet {
		background: #ffffff;
		border-radius: 10px;
		height: 40px;
		padding: 0px 16px;
	}

	.adt-apps-search-results .card-image-title-container .image-container {
		height: 3rem;
	}

	.adt-apps-search-results .card-image-title-container .title-container {
		word-break: break-word;
		word-wrap: break-word;
	}

	.adt-apps-search-results .cards-container .app-search-results-card .card-image-title-container .image-container .app-search-image {
		height: 48px;
		object-fit: contain;
		width: 48px;
	}

	.adt-apps-search-results .labels .category-label-remainder:hover .category-names {
		display: block;
	}

	.app-search-results-card {
		border-radius: 10px;
		border: 1px solid #E7EFFF;
		display: flex;
		height: 289px;
		padding: 16px;
		position: relative;
	}

	.app-type-label {
		border-radius: 2px 2px 8px 8px;
		font-size: 11px;
		font-weight: bold;
		height: 20px;
		line-height: 20;
		position: absolute;
		right: 24px;
		top: -8px;
	}

	.app-type-label-cloud {
		background-color: #D1EEDC;
		color: #0E7835;
	}

	.app-type-label-cx {
		background-color: #FFE6C6;
		color: #9D4C00;
	}

	.app-type-label-dxp {
		background-color: #D1ECFA;
		color: #166E9E;
	}

	.app-type-label-fragment {
		background-color: #DCD7E9;
		color: #503690;
	}

	.banner__product-tag {
		background-color: #e6ebf5;
		color: #1c3667;
		font-size: 0.8125rem;
		white-space: nowrap;
		width: fit-content;
	}

	.cards-container {
		display: grid;
		grid-column-gap: 1rem;
		grid-row-gap: 1.5rem;
		grid-template-columns: repeat(3, minmax(0, 1fr));
	}

	.card-image-title-container {
		height: 48px;
		margin-bottom: 18px;
	}

	.developer-name {
		color: #54555F;
		font-size: 13px;
		font-weight: 400;
		line-height: 16px;
	}

	.title-container {
		font-size: 18px;
		font-weight: 600;
		line-height: 20px;
	}

	@media screen and (max-width: 599px) {
		.adt-apps-search-results .cards-container {
			grid-column-gap: .5rem;
			grid-row-gap: .5rem;
			grid-template-columns: 293px;
			justify-content: center;
		}

		.adt-apps-search-results .app-search-results-card {
			height: 281px;
		}
	}

	@media screen and (min-width:600px) and (max-width: 899px) {
		.adt-apps-search-results .cards-container {
			grid-column-gap: .5rem;
			grid-row-gap: 1.5rem;
			grid-template-columns: repeat(2, minmax(0, 1fr));
		}
	}
</style>

<#assign appTypeClasses = {
	"Client Extension": "app-type-label-cx",
	"Cloud App": "app-type-label-cloud",
	"DXP App": "app-type-label-dxp",
	"Fragment": "app-type-label-fragment"
} />

<#if searchContainer?has_content>
	<div class="color-neutral-3 d-md-block d-none pb-4 pt-2">
		<strong class="color-black">
			${searchContainer.getTotal()}
		</strong>
		Applications Available
	</div>
</#if>

<#if themeDisplay?has_content>
	<#assign scopeGroupId = themeDisplay.getScopeGroupId() />
</#if>

<#assign
	channel = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels?accountId=-1&filter=name eq 'Marketplace Channel' and siteGroupId eq '${scopeGroupId}'")
	productThumbnail1 = "/o/commerce-media/default/?groupId=${scopeGroupId}"
/>

<#if channel?has_content>
	<#assign channelId = channel.items[0].id />
</#if>

<div class="adt-apps-search-results">
	<div class="cards-container pb-6">
		<#if entries?has_content>
			<#list entries as entry>
				<#if entry?has_content>
					<#assign
						portalURL = portalUtil.getLayoutURL(themeDisplay)
						productId = entry.getClassPK() + 1
						product = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels/"+ channelId +"/products/"+ productId +"?accountId=-1&images.accountId=-1&nestedFields=productSpecifications,categories,images")
						productImage = (product.images![])?filter(item -> item.tags?seq_contains("app icon"))![]
						remainingCategoriesText = []
					/>

					<#if product.categories?has_content && product.productSpecifications?has_content>
						<#assign
							productCategories = product.categories?filter(productCategory -> productCategory.vocabulary?replace(" ", "-") == "marketplace-app-category")![]
							categoriesListSize = productCategories?size-1
							productSpecifications = product.productSpecifications![]
						/>
					</#if>

					<#if product.name?has_content>
						<#assign productName = product.name />
						<#else>
							<#assign productName = "" />
					</#if>

					<#if product.description?has_content>
						<#assign productDescription = stringUtil.shorten(htmlUtil.stripHtml(product.description!""), 150, "...") />
						<#else>
							<#assign productDescription = "" />
					</#if>

					<#if product.urls?has_content>
						<#assign productURL = portalURL?replace("home", "p") + "/" + product.urls.en_US />
						<#else>
							<#assign productURL = "" />
					</#if>

					<#if productImage?has_content>
						<#assign productThumbnail = productImage[0].src?split("/o") />
						<#if productThumbnail?has_content && productThumbnail?size gte 2>
							<#assign productThumbnail1 = "/o/${productThumbnail[1]}" !"" />
						</#if>

					<#else>
						<#if product.urlImage?has_content>
							<#assign productThumbnail = product.urlImage?split("/o") />
							<#if productThumbnail?has_content && productThumbnail?size gte 2>
								<#assign productThumbnail1 = "/o/${productThumbnail[1]}" !"" />
							</#if>
						</#if>
					</#if>

					<a class="app-search-results-card bg-white border-radius-medium d-flex flex-column mb-0 text-dark text-decoration-none" href=${productURL}>
						<div class="align-items-center card-image-title-container d-flex">
							<#if product.productSpecifications?has_content>
								<#assign
									appType = ""
									productType = product.productSpecifications?filter(item -> stringUtil.equals(item.specificationKey, "type"))
								/>

								<#list productType as type>
									<#if type.value?has_content>
										<#if stringUtil.equals(type.value, "dxp")>
											<#assign appType = "DXP App" />
										<#elseif stringUtil.equals(type.value, "cloud")>
											<#assign appType = "Cloud App" />
										<#elseif stringUtil.equals(type.value, "fragment")>
											<#assign appType = "Fragment" />
										<#elseif stringUtil.equals(type.value, "client-extension")>
											<#assign appType = "Client Extension" />
										</#if>
									</#if>
								</#list>

								<#if appType?has_content>
									<div class="app-type-label ${appTypeClasses[appType]!} align-items-center d-flex justify-content-center px-2">
										${appType}
									</div>
								</#if>
							</#if>

							<div class="image-container mr-2 rounded">
								<img alt="${productName}" class="app-search-image" src="${productThumbnail1}" />
							</div>

							<div>
								<div class="title-container">
									${productName}
								</div>

								<#if productSpecifications?has_content>
									<#assign productDeveloperName = productSpecifications?filter(item -> item.specificationKey == "developer-name") />
									<#list productDeveloperName as developerNameItem>
										<#if developerNameItem.value?has_content>
											<#assign developerName = developerNameItem.value />
										<#else>
											<#assign developerName = "" />
										</#if>

										<div class="developer-name mt-1">
											${developerName}
										</div>
									</#list>
								</#if>
							</div>
						</div>

						<div class="d-flex flex-column font-size-paragraph-small h-100 justify-content-between">
							<div class="font-weight-normal mb-2 text-break">
								${productDescription}
							</div>

							<div class="d-flex flex-column">
								<#if productSpecifications?has_content>
									<#assign productPriceModels = productSpecifications?filter(item -> item.specificationKey == "price-model") />
									<#list productPriceModels as productPriceModel>
										<#if productPriceModel.value?has_content>
											<#assign priceModel = productPriceModel.value />
										<#else>
											<#assign priceModel = "" />
										</#if>

										<div class="font-weight-semi-bold mb-2 mt-1 text-capitalize">
											${priceModel}
										</div>
									</#list>
								</#if>

								<#if productCategories?has_content>
									<#assign
										principalCategory = productCategories[0]
										remainingCategories = productCategories?filter(category -> category.name != principalCategory.name)
									/>

									<#list remainingCategories as category>
										<#assign remainingCategoriesText = remainingCategoriesText + [category.name] />
									</#list>
								</#if>

								<#if principalCategory?has_content>
									<div>
										<span class="banner__product-tag rounded py-1 px-2 mr-2" title="${principalCategory.name}">
											${principalCategory.name}
										</span>
										<#if categoriesListSize?has_content && remainingCategoriesText?has_content>
											<span class="banner__product-tag rounded py-1 px-2" title="${remainingCategoriesText?join('\n')}">
												+ ${categoriesListSize}
											</span>
										</#if>
									</div>
								</#if>
							</div>
						</div>
					</a>
				</#if>
			</#list>
		</#if>
	</div>
</div>