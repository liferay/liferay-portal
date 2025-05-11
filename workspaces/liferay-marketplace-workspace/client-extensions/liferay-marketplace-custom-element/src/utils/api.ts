/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../liferay/liferay';
import fetcher from '../services/fetcher';
import {axios} from './axios';

const headers = {
	'Content-Type': 'application/json',
	'X-CSRF-Token': Liferay.authToken,
};

export const baseURL =
	window.location.origin + Liferay.ThemeDisplay.getPathContext();

// LiferayJsonWS.addExpandoValue

export async function addExpandoValue({
	attributeValues,
	className,
	classPK,
	companyId,
	tableName,
}: {
	attributeValues: Object;
	className: string;
	classPK: number;
	companyId: string;
	tableName: string;
}) {
	await Liferay.Service('/expandovalue/add-values', {
		attributeValues,
		className,
		classPK,
		companyId,
		tableName,
	});
}

export async function getTierPrice(
	channelId: number,
	productId: number | undefined,
	accountId: number | undefined
) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-delivery-catalog/v1.0/channels/${channelId}/products/${productId}?accountId=${accountId}&skus.accountId=${accountId}&nestedFields=skus`,
		{
			headers,
			method: 'GET',
		}
	);

	const {skus = []} = await response.json();

	const tierPrices: [any?] = [];

	skus.forEach((sku: any) => {
		tierPrices.push({skuId: sku.id, tierPrice: sku.tierPrices || []});
	});

	return tierPrices;
}

/**
 * @deprecated We will remove this object definition soon
 */
export async function getLicenseDescription() {
	const response = await fetch(`${baseURL}/o/c/licensetypesdescriptions/`, {
		headers,
		method: 'GET',
	});

	return response.json();
}

export async function createAppSKU({
	appProductId,
	body,
}: {
	appProductId: number;
	body: Object;
}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${appProductId}/skus
	  `,
		{
			body: JSON.stringify(body),
			headers,
			method: 'POST',
		}
	);

	return (await response.json()) as SKU;
}

export async function createContactSales(formData: ContactSales) {
	const response = await fetch(`${baseURL}/o/c/contactsaleses/`, {
		body: JSON.stringify(formData),
		headers,
		method: 'POST',
	});

	return response.json();
}

export async function createAttachmentAxios({
	body,
	callback,
	externalReferenceCode,
}: {
	body: Object;
	callback: (progress: number) => void;
	externalReferenceCode: string;
}) {
	const response = await axios.post(
		`/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}/attachments`,
		body,
		{
			onUploadProgress: (event: any) => {
				const progress = Math.round(
					(event.loaded * 100) / Number(event.total)
				);

				callback(progress);
			},
		}
	);

	return response.data;
}

export async function createProductVirtualEntry({
	body,
	callback,
	virtualSettingId,
}: {
	body: Object;
	callback: (progress: number) => void;
	virtualSettingId: string;
}) {
	const response = await axios.post(
		`/o/headless-commerce-admin-catalog/v1.0/product-virtual-settings/${virtualSettingId}/product-virtual-settings-file-entries`,
		body,
		{
			onUploadProgress: (event) => {
				const progress = Math.round(
					(event.loaded * 100) / Number(event.total || 1)
				);

				callback(progress);
			},
		}
	);

	return response.data;
}

export async function createImageAxios({
	body,
	callback,
	externalReferenceCode,
}: {
	body: Object;
	callback: (progress: number) => void;
	externalReferenceCode: string;
}) {
	const response = await axios.post(
		`/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}/images`,
		body,
		{
			onUploadProgress: (event: any) => {
				const progress = Math.round(
					(event.loaded * 100) / Number(event.total)
				);

				callback(progress);
			},
		}
	);

	return response.data;
}

export function deleteAttachment(attachmentId: string) {
	return fetcher.delete(
		`/o/headless-commerce-admin-catalog/v1.0/attachment/${attachmentId}`
	);
}

export function createImage({
	body,
	externalReferenceCode,
}: {
	body: Object;
	externalReferenceCode: string;
}) {
	return fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}/images`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'POST',
		}
	);
}

export async function createProductSpecification({
	body,
	id,
}: {
	body: Object;
	id: number | string;
}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${id}/productSpecifications`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'POST',
		}
	);

	return await response.json();
}

export async function getSpecification(
	specificationKey: string
): Promise<Specification> {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/specifications?search=%7Bkey=${specificationKey}%7D`,
		{headers, method: 'GET'}
	);

	const {items}: {items: Specification[]} = (await response.json()) || [];

	return items.find((item) => {
		return item.key === specificationKey;
	}) as Specification;
}

export async function deleteTrialSKU(skuTrialId: number) {
	await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/skus/${skuTrialId}`,
		{
			headers,
			method: 'DELETE',
		}
	);
}

export async function getAccountInfoFromCommerce(accountId?: number) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-account/v1.0/accounts/${accountId}`,
		{headers, method: 'GET'}
	);

	return (await response.json()) as CommerceAccount;
}

export async function getAccountAddressesFromCommerce(accountId: number) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-account/v1.0/accounts/${accountId}/accountAddresses`,
		{headers, method: 'GET'}
	);

	return (await response.json()) as {items: BillingAddress[]};
}

export async function createCart({
	accountId,
	channelId,
	currencyCode = 'USD',
	orderTypeExternalReferenceCode,
}: {
	accountId: number;
	channelId: number | string;
	currencyCode?: string;
	orderTypeExternalReferenceCode: string;
}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-delivery-cart/v1.0/channels/${channelId}/carts`,
		{
			body: JSON.stringify({
				accountId,
				currencyCode,
				orderTypeExternalReferenceCode,
			}),
			headers,
			method: 'POST',
		}
	);

	return response.json();
}

export async function getCart(cartId: number | string) {
	const cartResponse = await fetch(
		`${baseURL}/o/headless-commerce-delivery-cart/v1.0/carts/${cartId}`,
		{
			headers,
			method: 'GET',
		}
	);

	return await cartResponse.json();
}

export async function getCartItems(cartId: number | string) {
	const cartResponse = await fetch(
		`${baseURL}/o/headless-commerce-delivery-cart/v1.0/carts/${cartId}/items`,
		{
			headers,
			method: 'GET',
		}
	);

	return await cartResponse.json();
}

export async function getCategories({vocabId}: {vocabId: number}) {
	const response = await fetch(
		`${baseURL}/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabId}/taxonomy-categories`,
		{
			headers,
			method: 'GET',
		}
	);

	const {items} = (await response.json()) as {items: any[]};

	return items;
}

export async function getPaymentMethodURL(
	orderId: number,
	callbackURL: string
) {
	const paymentResponse = await fetch(
		`${baseURL}/o/headless-commerce-delivery-cart/v1.0/carts/${orderId}/payment-url?callbackURL=${callbackURL}`,
		{
			headers,
			method: 'GET',
		}
	);

	return await paymentResponse.text();
}

export async function getOptions() {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/options`,
		{
			headers,
			method: 'GET',
		}
	);

	const {items} = await response.json();

	return items as CommerceOption[];
}

export async function getProductById({
	nestedFields,
	productId,
}: {
	nestedFields?: string;
	productId?: string | number;
}) {
	let url = `${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${productId}`;

	if (nestedFields) {
		url = `${url}?nestedFields=${nestedFields}`;
	}
	const response = await fetch(url, {
		headers,
		method: 'GET',
	});

	return (await response.json()) as Product;
}

export async function getProductIdCategories({appId}: {appId: string}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${appId}/categories`,
		{
			headers,
			method: 'GET',
		}
	);

	return (await response.json()) as {items: Categories[]};
}

export async function getProductSKU({appProductId}: {appProductId?: number}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${appProductId}/skus`,
		{
			headers,
			method: 'GET',
		}
	);

	return (await response.json()) as {items: SKU[]};
}

export async function getProductSpecifications({
	appProductId,
}: {
	appProductId?: number;
}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${appProductId}/productSpecifications`,
		{
			headers,
			method: 'GET',
		}
	);

	const {items} = (await response.json()) as {
		items: ProductSpecification[];
	};

	return items;
}

export async function getProductIdSkusPage(productId: number) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${productId}/skus`,
		{
			headers,
			method: 'GET',
		}
	);

	return await response.json();
}

export async function getPriceListByCatalogName(catalogName: string) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-pricing/v2.0/price-lists?search=catalogName eq '${catalogName}'`,
		{
			headers,
			method: 'GET',
		}
	);

	return await response.json();
}

export async function getPriceListIdPriceEntries(priceListId: number) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-pricing/v2.0/price-lists/${priceListId}/price-entries?nestedFields=sku`,
		{
			headers,
			method: 'GET',
		}
	);

	return await response.json();
}

export async function getVocabularies() {
	const response = await fetch(
		`${baseURL}/o/headless-admin-taxonomy/v1.0/sites/${Liferay.ThemeDisplay.getCompanyGroupId()}/taxonomy-vocabularies`,
		{
			headers,
			method: 'GET',
		}
	);

	return response.json();
}

export async function patchOrderByERC(erc: string, body: any) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-order/v1.0/orders/by-externalReferenceCode/${erc}`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'PATCH',
		}
	);

	return response;
}

export async function patchProductIdCategory({
	appId,
	body,
}: {
	appId: string;
	body: any;
}) {
	await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${appId}/categories`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'PATCH',
		}
	);
}

export async function patchSKUById(skuId: number, body: any) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/skus/${skuId}`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'PATCH',
		}
	);

	return await response.json();
}

export async function postCartByChannelId({
	cartBody,
	channelId,
}: {
	cartBody: any;
	channelId: number;
}) {
	const cartResponse = await fetch(
		`${baseURL}/o/headless-commerce-delivery-cart/v1.0/channels/${channelId}/carts`,
		{
			body: JSON.stringify(cartBody),
			headers,
			method: 'POST',
		}
	);

	return (await cartResponse.json()) as Cart;
}

export async function postCheckoutCart({
	body,
	cartId,
}: {
	body?: any;
	cartId: number;
}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-delivery-cart/v1.0/carts/${cartId}/checkout`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'POST',
		}
	);

	return (await response.json()) as Cart;
}

export async function postOptionValue(optionBody: any, optionId: number) {
	{
		const response = await fetch(
			`${baseURL}/o/headless-commerce-admin-catalog/v1.0/productOptions/${optionId}/productOptionValues`,
			{
				body: JSON.stringify(optionBody),
				headers,
				method: 'POST',
			}
		);

		const {id} = await response.json();

		return id;
	}
}

export async function patchPriceEntry(priceEntry: any, priceEntryId: number) {
	const response = await fetch(
		`/o/headless-commerce-admin-pricing/v2.0/price-entries/${priceEntryId}`,
		{
			body: JSON.stringify(priceEntry),
			headers,
			method: 'PATCH',
		}
	);

	return await response.json();
}

export async function postPriceEntryIdTierPrice(
	priceEntryId: any,
	tierPrice: any
) {
	const response = await fetch(
		`/o/headless-commerce-admin-pricing/v2.0/price-entries/${priceEntryId}/tier-prices`,
		{
			body: JSON.stringify(tierPrice),
			headers,
			method: 'POST',
		}
	);

	return await response.json();
}

export async function postOption(optionBody: any) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/options`,
		{
			body: JSON.stringify(optionBody),
			headers,
			method: 'POST',
		}
	);

	const {id} = await response.json();

	return id;
}

export async function postProductOption(
	productId: number,
	productOptionBody: any
) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/products/${productId}/productOptions`,
		{
			body: JSON.stringify([productOptionBody]),
			headers,
			method: 'POST',
		}
	);

	const {
		items: [{id}],
	} = (await response.json()) as {items: ProductOptionItem[]};

	return id;
}

export async function updateProductSpecification({
	body,
	id,
}: {
	body: Object;
	id: number | string;
}) {
	const response = await fetch(
		`${baseURL}/o/headless-commerce-admin-catalog/v1.0/productSpecifications/${id}`,
		{
			body: JSON.stringify(body),
			headers,
			method: 'PATCH',
		}
	);

	return await response.json();
}

export async function postEmailAppInformation(emailInformation: any) {
	await fetch(`${baseURL}/o/c/getappinformations/`, {
		body: JSON.stringify(emailInformation),
		headers,
		method: 'POST',
	});
}

export async function getSiteStructuredContentByKey(key: string) {
	const response = await fetch(
		`${baseURL}/o/headless-delivery/v1.0/sites/${Liferay.ThemeDisplay.getScopeGroupId()}/structured-contents/by-key/${key}`,
		{
			headers,
		}
	);

	return await response.json();
}
