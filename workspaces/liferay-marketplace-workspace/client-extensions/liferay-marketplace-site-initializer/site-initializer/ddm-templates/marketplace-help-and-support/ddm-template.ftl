<style>
	.custom-field-links {
		border-color: var(--neutral-9);
		border-style: solid;
		border-width: 0 0 2px 0;
		color:var(--body-color);
	}

	.help-and-support-link {
		color: inherit;
		cursor: pointer;
		text-decoration: none;
	}

	.help-and-support-link:hover {
		color: inherit;
		text-decoration: none;
	}

	.help-and-support-link-arrow {
		fill: rgb(133, 140, 148);
	}

	.help-and-support-link-icon {
		color: rgb(133, 140, 148);
	}

	.help-and-support-svg mask,
	.link-arrow mask {
		mask-type: alpha;
	}
</style>

<#if (_CUSTOM_FIELD_Documentation.getData())?has_content && (_CUSTOM_FIELD_Documentation.getData())?starts_with("https")>
	<a class="align-items-center bg-whiteColor cursor-pointer d-flex justify-content-between pb-2 pl-4 pr-4 pt-2 text-decoration-none" href="${_CUSTOM_FIELD_Documentation.getData()}" target="_blank">
		<svg fill="none" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
			<mask id="doc" maskUnits="userSpaceOnUse" x="2" y="1" width="12" height="14">
				<path fill-rule="evenodd" clip-rule="evenodd" d="M2.673 2.666c0-.733.594-1.333 1.327-1.333h4.78c.353 0 .693.14.94.393l3.22 3.22c.253.247.393.587.393.94v7.447c0 .733-.6 1.333-1.333 1.333H3.993c-.733 0-1.326-.6-1.326-1.333l.006-10.667Zm5.994-.333v3c0 .367.3.667.666.667h3L8.667 2.333Z" fill="#000" />
			</mask>

			<g mask="url(#doc)">
				<path fill="var(--neutral-5)" d="M0 0h16v16H0z" />
			</g>
		</svg>

		<span class="copy-text ml-1">${languageUtil.get(locale, "installation-documentation", "Installation Documentation")}</span>

		<svg class="link-arrow ml-auto" fill="none" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
			<mask id="arrow" maskUnits="userSpaceOnUse" x="5" y="4" width="6" height="8">
				<path d="m6 10.584 2.587-2.587L6 5.41a.664.664 0 1 1 .94-.94L10 7.53c.26.26.26.68 0 .94l-3.06 3.06c-.26.26-.68.26-.94 0a.678.678 0 0 1 0-.946Z" fill="#000" />
			</mask>

			<g mask="url(#arrow)">
				<path fill="var(--neutral-5)" d="M0 0h16v16H0z" />
			</g>
		</svg>
	</a>
</#if>

<#if (_CUSTOM_FIELD_Source.getData())?has_content && (_CUSTOM_FIELD_Source.getData())?starts_with("https")>
	<a class="align-items-center bg-whiteColor cursor-pointer d-flex justify-content-between pb-2 pl-4 pr-4 pt-2 text-decoration-none" href="${_CUSTOM_FIELD_Source.getData()}" target="_blank">
		<svg width="16" height="16" fill="none" xmlns="http://www.w3.org/2000/svg">
			<mask id="code" maskUnits="userSpaceOnUse" x="1" y="4" width="14" height="8">
				<path fill-rule="evenodd" clip-rule="evenodd" d="m12.8 8-2.6 2.6a.656.656 0 0 0 0 .933c.26.26.673.26.933 0l3.06-3.067c.26-.26.26-.68 0-.94l-3.06-3.06a.656.656 0 0 0-.933 0 .656.656 0 0 0 0 .934L12.8 8ZM3.2 8l2.6 2.6c.26.26.26.673 0 .933a.656.656 0 0 1-.933 0l-3.06-3.067a.664.664 0 0 1 0-.94l3.06-3.06a.656.656 0 0 1 .933 0c.26.26.26.674 0 .934L3.2 8Z" fill="#000" />
			</mask>

			<g mask="url(#code)">
				<path fill="var(--neutral-5)" d="M0 0h16v16H0z" />
			</g>
		</svg>

		<span class="copy-text ml-1">${languageUtil.get(locale, "source-code", "Source Code")}</span>

		<svg class="link-arrow ml-auto" fill="none" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
			<mask id="arrow" maskUnits="userSpaceOnUse" x="5" y="4" width="6" height="8">
				<path d="m6 10.584 2.587-2.587L6 5.41a.664.664 0 1 1 .94-.94L10 7.53c.26.26.26.68 0 .94l-3.06 3.06c-.26.26-.68.26-.94 0a.678.678 0 0 1 0-.946Z" fill="#000" />
			</mask>

			<g mask="url(#arrow)">
				<path fill="var(--neutral-5)" d="M0 0h16v16H0z" />
			</g>
		</svg>
	</a>
</#if>

<#if (_CUSTOM_FIELD_Support.getData())?has_content && (_CUSTOM_FIELD_Terms.getData())?starts_with("https")>
	<a class="align-items-center bg-whiteColor cursor-pointer d-flex justify-content-between pb-2 pl-4 pr-4 pt-2 text-decoration-none" href="${_CUSTOM_FIELD_Terms.getData()}" target="_blank">
		<svg width="16" height="16" fill="none" xmlns="http://www.w3.org/2000/svg">
			<mask id="support" maskUnits="userSpaceOnUse" x="2" y="1" width="12" height="14">
				<path fill-rule="evenodd" clip-rule="evenodd" d="M8 1.333A5.67 5.67 0 0 0 2.333 7 5.67 5.67 0 0 0 8 12.666h.333v2C11.573 13.106 13.667 10 13.667 7A5.67 5.67 0 0 0 8 1.333ZM7.333 11V9.666h1.334V11H7.333Zm1.58-3.154c.007-.013.014-.026.02-.033.182-.266.435-.488.69-.712.592-.519 1.202-1.053.997-2.188-.193-1.127-1.093-2.053-2.22-2.22a2.668 2.668 0 0 0-2.953 1.86.61.61 0 0 0 .58.78h.133c.273 0 .493-.193.587-.433.213-.594.84-1 1.533-.854.64.134 1.107.767 1.047 1.42-.045.51-.41.79-.813 1.103-.251.195-.519.402-.734.684l-.007-.007c-.011.012-.02.028-.03.044-.007.013-.015.025-.023.036-.01.017-.022.034-.033.05l-.034.05c-.06.094-.106.187-.146.294a.104.104 0 0 1-.017.033.104.104 0 0 0-.017.033c-.006.007-.006.014-.006.02-.08.24-.134.527-.134.867h1.34a1.406 1.406 0 0 1 .12-.593.239.239 0 0 1 .027-.074c.027-.053.06-.106.093-.16Z" fill="#000" />
			</mask>

			<g mask="url(#support)">
				<path fill="var(--neutral-5)" d="M0 0h16v16H0z"/>\
			</g>
		</svg>

		<span class="copy-text ml-1">${languageUtil.get(locale, "support-levels-and-informations", "Support Levels & Information")}</span>

		<svg class="link-arrow ml-auto" fill="none" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
			<mask id="arrow" maskUnits="userSpaceOnUse" x="5" y="4" width="6" height="8">
				<path d="m6 10.584 2.587-2.587L6 5.41a.664.664 0 1 1 .94-.94L10 7.53c.26.26.26.68 0 .94l-3.06 3.06c-.26.26-.68.26-.94 0a.678.678 0 0 1 0-.946Z" fill="#000" />
			</mask>

			<g mask="url(#arrow)">
				<path fill="var(--neutral-5)" d="M0 0h16v16H0z" />
			</g>
		</svg>
	</a>
</#if>

<#if (CPDefinition_cProductId.getData())??>
	<#assign productId = CPDefinition_cProductId.getData() />
</#if>

<#if themeDisplay?has_content>
	<#assign scopeGroupId = themeDisplay.getScopeGroupId() />
</#if>

<#assign channel = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels?accountId=-1&filter=name eq 'Marketplace Channel' and siteGroupId eq '${scopeGroupId}'") />

<#if channel?has_content>
	<#assign channelId = channel.items[0].id />
</#if>

<#assign
	productId = CPDefinition_cProductId.getData()
	product = restClient.get("/headless-commerce-delivery-catalog/v1.0/channels/"+ channelId +"/products/"+ productId +"?accountId=-1&images.accountId=-1&nestedFields=categories,images,productSpecifications")
	catalogName = product.catalogName
	productImage = product.images![]
	solutionHeaderImages = productImage?filter(image -> image.tags?seq_contains("app icon"))
/>

<#if product.productSpecifications?has_content>
	<#assign
		productSpecifications = product.productSpecifications
		publisherUrlFiltered = productSpecifications?filter(specification -> stringUtil.equals(specification.specificationKey, "publisherwebsiteurl"))
		supportEmailFiltered = productSpecifications?filter(specification -> stringUtil.equals(specification.specificationKey, "supportemailaddress"))
		supportPhoneFiltered = productSpecifications?filter(specification -> stringUtil.equals(specification.specificationKey, "supportphone"))
	/>

	<#if supportEmailFiltered?has_content>
		<#assign
			supportEmail = supportEmailFiltered[0].value
		/>
	</#if>

	<#if publisherUrlFiltered?has_content>
		<#assign publisherUrl = publisherUrlFiltered[0].value?trim?replace(' ', '') />

		<#if publisherUrl?starts_with("http://") || publisherUrl?starts_with("https://")>
			<#assign sanitizedUrl = publisherUrl />
		<#else>
			<#assign sanitizedUrl = "https://" + publisherUrl />
		</#if>
	</#if>

	<#if supportPhoneFiltered?has_content>
		<#assign
			supportPhone = supportPhoneFiltered[0].value
		/>
	</#if>
</#if>

<div class="d-flex flex-column">
	<div class="d-flex mb-3">
		<span class="help-and-support-link-icon">
			<@clay["icon"] symbol="envelope-closed" />
		</span>

		<a class="d-flex help-and-support-link justify-content-between w-100" id="help-and-support-link-contact-button" onClick="openModal()">
			<span class="copy-text help-and-support-link ml-1">
				${languageUtil.get(locale, "publisher-support", "Publisher Support")}
			</span>

			<svg class="help-and-support-link-arrow link-arrow ml-auto" fill="none" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
				<mask height="8" id="arrow" maskUnits="userSpaceOnUse" width="6" x="5" y="4">
					<path d="m6 10.584 2.587-2.587L6 5.41a.664.664 0 1 1 .94-.94L10 7.53c.26.26.26.68 0 .94l-3.06 3.06c-.26.26-.68.26-.94 0a.678.678 0 0 1 0-.946Z" fill="#000" />
				</mask>

				<g mask="url(#arrow)">
					<path d="M0 0h16v16H0z" fill="var(--neutral-5)" />
				</g>
			</svg>
		</a>
	</div>

	<div class="d-flex">
		<span class="help-and-support-link-icon">
			<@clay["icon"] symbol="document" />
		</span>

		<a class="d-flex w-100 justify-content-between help-and-support-link" href="https://www.liferay.com/en/legal/marketplace-terms-of-service" target="_blank">
			<span class="copy-text ml-1 help-and-support-link">
				${languageUtil.get(locale, "terms-and-conditions", "Terms & Conditions")}
			</span>

			<svg class="link-arrow help-and-support-link-arrow ml-auto" fill="none" height="16" width="16" xmlns="http://www.w3.org/2000/svg">
				<mask height="8"id="arrow" maskUnits="userSpaceOnUse" width="6" x="5" y="4">
					<path d="m6 10.584 2.587-2.587L6 5.41a.664.664 0 1 1 .94-.94L10 7.53c.26.26.26.68 0 .94l-3.06 3.06c-.26.26-.68.26-.94 0a.678.678 0 0 1 0-.946Z" fill="#000" />
				</mask>

				<g mask="url(#arrow)">
					<path fill="var(--neutral-5)" d="M0 0h16v16H0z" />
				</g>
			</svg>
		</a>
	</div>
</div>

<script>
	function modalBody(){
		return `
			<#if catalogName?has_content>
				<div class="align-items-center d-flex flex-row mb-3">
					<span class="align-items-center bg-light d-flex justify-content-center mr-3 overflow-hidden p-3 rounded-circle">
						<#if solutionHeaderImages?has_content>
							<#list solutionHeaderImages as image>
								<#assign imageSourceSplitedUrl = image.src?split("/o") />

								<#if imageSourceSplitedUrl?has_content>
									<#assign productThumbnail = "/o/${imageSourceSplitedUrl[1]}" />

									<img alt="Slide ${image?index}" class="catalog-icon" src="${productThumbnail}" style="height: 40px; object-fit: contain; width: 40px;">
								</#if>
							</#list>
						<#else>
							<@clay["icon"]
								style="fill:#6B6C7E;"
								symbol="picture"
							/>
						</#if>
					</span>

					<div class="d-flex flex-column">
						<#if catalogName?has_content>
							<h3 class="font-weight-bold mb-0">
								${catalogName}
							</h3>
						</#if>
					</div>
				</div>
			</#if>

			<#if sanitizedUrl?has_content && publisherUrl?has_content>
				<div class="align-items-center d-flex flex-row mb-3">
					<span class="align-items-center bg-light d-flex justify-content-center mr-3 p-3 rounded-circle">
						<@clay["icon"]
							style="fill:#6B6C7E;"
							symbol="globe"
						/>
					</span>

					<div class="d-flex flex-column">
						<span class="text-black-50">Publisher website URL</span>

						<a href="${sanitizedUrl}" target="_blank" class="font-weight-bold">
							${publisherUrl}
						</a>
					</div>
				</div>
			</#if>

			<#if supportEmail?has_content>
				<div class="align-items-center d-flex flex-row mb-3">
					<span class="align-items-center bg-light d-flex justify-content-center mr-3 p-3 rounded-circle">
						<@clay["icon"] style="fill:#6B6C7E;"symbol="envelope-closed" />
					</span>

					<div class="d-flex flex-column">
						<span class="text-black-50">Support Email</span>

						<a class="font-weight-bold" href="mailto:${supportEmail}" target="_blank">
							${supportEmail}
						</a>
					</div>
				</div>
			</#if>

			<#if supportPhone?has_content>
				<div class="d-flex flex-row align-items-center mb-3">
					<span class="align-items-center bg-light d-flex justify-content-center mr-3 p-3 rounded-circle">
						<@clay["icon"]
							style="fill:#6B6C7E;"
							symbol="phone"
						/>
					</span>

					<div class="d-flex flex-column">
						<span class="text-black-50">Phone</span>

						<a class="font-weight-bold" href="tel:${supportPhone}" target="_blank">
							${supportPhone}
						</a>
					</div>
				</div>
			</#if>
		`
	}

	function openModal() {
		Liferay.Util.openModal({
			bodyHTML: modalBody(),
			center: true,
			headerCssClass: "pt-2",
			headerHTML: "<h2>Publisher Support Contact Info</h2>",
			size: "md",
		})
	}

</script>