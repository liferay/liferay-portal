/* eslint-disable no-undef */

/* eslint-disable @liferay/portal/no-global-fetch */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const contactPublisherButtonElement = fragmentElement.querySelector(
	'button#contact-publisher'
);
const contactPublisherModal = document.querySelector('#contact-publisher');
const getAppButtonElement = fragmentElement.querySelector('button#get-app');
const getAppDescriptionElement = fragmentElement.querySelector(
	'#get-app-description'
);
const tooltipElement = fragmentElement.querySelector('.clay-tooltip-bottom');

const isFreeApp = (productSpecifications = []) =>
	productSpecifications.some(
		(productSpecification) =>
			productSpecification.specificationKey === 'price-model' &&
			productSpecification.value === 'Free'
	);

const trackAnalytics = (key, options) => {
	if (!window.Analytics) {
		return;
	}

	Analytics.track(key, options);
};

const productId = fragmentElement
	.querySelector('.product-id')
	.innerText.replace(/[\n\r]+|[\s]{2,}/g, ' ')
	.trim();

const getProductPrice = async (product) => {
	const {productSpecifications = []} = product;

	if (isFreeApp(productSpecifications)) {
		return 'Free';
	}

	const skus = product.skus.filter(({purchasable}) => purchasable);

	const hasTrialSku = skus.some(({skuOptions}) =>
		skuOptions.find((skuOption) =>
			['trial', 'yes'].includes(skuOption.skuOptionValueKey)
		)
	);

	const standardSku = skus.find(({skuOptions}) =>
		skuOptions.some((skuOption) =>
			['standard', 'no'].includes(skuOption.skuOptionValueKey)
		)
	);

	const licenseType = productSpecifications.find(
		(productSpecification) =>
			productSpecification.specificationKey === 'license-type'
	);

	const licenseTypeText =
		licenseType?.value === 'Perpetual' ? 'One-Time' : 'Annually';
	const price = `${hasTrialSku ? '30-day trial or' : ''} ${standardSku?.price?.priceFormatted}`;

	return `${price} ${licenseTypeText}`;
};

const customizeGetAppButton = async (product) => {
	getAppButtonElement.onclick = () => {
		trackAnalytics('Click on Get App Button', {
			isFree: isFreeApp(product.productSpecifications),
			productName: product.name,
		});

		Liferay.Util.navigate(
			`${getSiteURL()}/product-purchase?productId=${productId}`
		);
	};

	getAppDescriptionElement.innerText = await getProductPrice(product);
};

const getCommerceProduct = async (channelId) => {
	try {
		const response = await Liferay.Util.fetch(
			`/o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products/${productId}?nestedFields=productSpecifications,skus&accountId=-1&skus.accountId=-1&skus.currencyCode=${Liferay.CommerceContext.currency.currencyCode}`
		);

		const product = await response.json();

		return product ?? {skus: []};
	}
	catch {
		return {skus: []};
	}
};

const getSiteURL = () => {
	const layoutRelativeURL = Liferay.ThemeDisplay.getLayoutRelativeURL();

	if (layoutRelativeURL.startsWith('/web/')) {
		return layoutRelativeURL.split('/').slice(0, 3).join('/');
	}

	return '';
};

const customizeUnavailableButton = async (product) => {
	contactPublisherButtonElement.onmouseover = () =>
		tooltipElement.classList.replace('hide', 'show');

	contactPublisherButtonElement.onmouseout = () =>
		tooltipElement.classList.replace('show', 'hide');

	if (!themeDisplay.isSignedIn()) {
		contactPublisherButtonElement.onclick = () => {
			sessionStorage.setItem(
				'@marketplace/redirect-to',
				window.location.href
			);

			location.href = `${getSiteURL()}/sign-in`;
		};

		return;
	}

	contactPublisherButtonElement.onclick = () => {
		trackAnalytics('Click on Contact Publisher Button', {
			isFree: isFreeApp(product.productSpecifications),
			productName: product.name,
		});

		contactPublisherModal.click();
	};

	if (sessionStorage.getItem('@marketplace/redirect-to')) {
		contactPublisherButtonElement.click();

		sessionStorage.removeItem('@marketplace/redirect-to');
	}
};

const main = async () => {
	const channelId = Liferay.CommerceContext.commerceChannelId;

	if (!channelId) {
		return;
	}

	const product = await getCommerceProduct(channelId);

	const isReferral = product.productSpecifications.some(
		({specificationKey, value}) =>
			specificationKey === 'type' && value === 'referral'
	);

	if (isReferral) {
		return;
	}

	const skuPublished = product.skus.some((sku) => sku.purchasable);

	if (skuPublished) {
		getAppButtonElement.classList.remove('d-none');

		return customizeGetAppButton(product);
	}

	contactPublisherButtonElement.classList.remove('d-none');

	customizeUnavailableButton(product);
};

main();
