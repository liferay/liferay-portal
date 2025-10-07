<#assign
	channel = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels?accountId=-1&filter=siteGroupId eq '${themeDisplay.getScopeGroupId()}'")

	product = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels/" + channel.items[0].id + "/products/" + CPDefinition_cProductId.getData() + "?accountId=-1&nestedFields=categories,productSpecifications,skus&skus.accountId=-1&skus.currencyCode=USD")
	productSpecifications = product.productSpecifications![]

	licenseSpecifications = productSpecifications?filter(spec -> stringUtil.equals(spec.specificationKey, "license"))
	storefrontVideoURLSpecifications = productSpecifications?filter(spec -> stringUtil.equals(spec.specificationKey, "app-storefront-video-url"))

	licenseValue = (licenseSpecifications[0].value)!""
	storefrontVideoURLValue = (storefrontVideoURLSpecifications[0].value)!""
/>

<span class="title">${languageUtil.get(locale, "description")}</span>

<#if description.getData()?has_content>
	<div class="description-content mt-4">
		${description.getData()}
	</div>
</#if>

<div>
	<#if licenseSpecifications?has_content>
		<span class="title">${languageUtil.get(locale, "license")}</span>

		<div class="description-content mt-4">
			${licenseValue}
		</div>
	</#if>
</div>

<#if storefrontVideoURLSpecifications?has_content>
	<script>
		setTimeout(function () {
			Liferay.fire('plyr:play', { videoURL: "${storefrontVideoURLValue}" });
		}, 300);
	</script>
</#if>

<style ${nonceAttribute}>
	.description-content {
		font-size: 18px;
		line-height: 32px;
	}

	.title {
	 	font-size: 24px;
		font-weight: 600;
	}

</style>