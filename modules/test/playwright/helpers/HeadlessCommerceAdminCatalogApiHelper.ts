/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomDouble} from '../utils/getRandomDouble';
import {getRandomInt} from '../utils/getRandomInt';
import getRandomString from '../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

export type TAttachmentBase64 = {
	attachment: string;
	title: {
		[key: string]: string;
	};
};

type TCatalog = {
	accountId?: number;
	currencyCode?: string;
	defaultLanguageId?: string;
	id?: number;
	name?: string;
};

type TCategory = {
	checked?: boolean;
	externalReferenceCode?: string;
	id: number;
	label?: string;
	name: string;
	value?: string;
	vocabulary?: string;
};

type TChannel = {
	channelId: number;
	currencyCode: string;
	externalReferenceCode?: string;
	id?: number;
	name: string;
	type: string;
};

type TCurrency = {
	active?: boolean;
};

export type TDiagram = {
	attachmentBase64: TAttachmentBase64;
};

export type TPin = {
	id?: number;
	mappedProduct: {
		productId: number;
		quantity: number;
		sequence: string;
		sku: string;
		skuId: number;
		type?: number;
	};
	positionX?: number;
	positionY?: number;
	sequence: string;
};

export type TProduct = {
	active?: boolean;
	catalogId: number;
	categories?: TCategory[];
	createDate?: string;
	description?: {
		[key: string]: string;
	};
	diagram?: TDiagram;
	expirationDate?: string;
	externalReferenceCode?: string;
	id?: number;
	name?: {
		[key: string]: string;
	};
	neverExpire?: boolean;
	productAccountGroupFilter?: boolean;
	productAccountGroups?: {
		accountGroupId: number;
		id: number;
	}[];
	productChannelFilter?: boolean;
	productChannels?: TChannel[];
	productConfiguration?: TProductConfiguration;
	productId?: number;
	productOptions?: any[];
	productSpecifications?: any[];
	productStatus?: number;
	productType?: string;
	productVirtualSettings?: TProductVirtualSettings;
	shippingConfiguration?: {
		freeShipping?: boolean;
		shippable?: boolean;
		shippingSeparately?: boolean;
	};
	shortDescription?: {
		[key: string]: string;
	};
	skus?: TSku[];
	tags?: [string];
	version?: number;
};

export type TProductConfiguration = {
	allowBackOrder?: boolean;
	allowedOrderQuantities?: Array<number>;
	availabilityEstimateId?: number;
	availabilityEstimateName?: any;
	displayAvailability?: boolean;
	displayStockQuantity?: boolean;
	entityExternalReferenceCode?: string;
	entityId?: number;
	entityName?: string;
	entityType?: string;
	externalReferenceCode?: string;
	id?: number;
	inventoryEngine?: string;
	lowStockAction?: string;
	maxOrderQuantity?: number;
	minOrderQuantity?: number;
	minStockQuantity?: number;
	multipleOrderQuantity?: number;
	productShippingConfiguration?: any;
	productTaxConfiguration?: TProductTaxConfiguration;
	purchasable?: boolean;
	visible?: boolean;
};

export type TProductConfigurationList = {
	catalogExternalReferenceCode?: string;
	catalogId: number;
	externalReferenceCode?: string;
	id?: number;
	masterProductConfigurationList?: boolean;
	name?: string;
	neverExpire?: boolean;
	parentProductConfigurationListId?: number;
	priority?: number;
	productConfigurations?: TProductConfiguration[];
};

export type TProductTaxConfiguration = {
	id: number;
	taxable: boolean;
};

type TProductVirtualSettings = {
	activationStatus?: number;
	duration?: number;
	maxUsages?: number;
	productVirtualSettingsFileEntries?: TProductVirtualSettingsFileEntry[];
	sampleURL?: string;
	termsOfUseContent?: {
		[key: string]: string;
	};
	url?: string;
	useSample?: boolean;
};

type TProductVirtualSettingsFileEntry = {
	attachment: string;
	version: string;
};

type TRelatedProduct = {
	id?: number;
	priority?: number;
	productExternalReferenceCode?: string;
	productId?: number;
	type?: string;
};

type TSku = {
	cost: number;
	discontinued?: boolean;
	gtin?: string;
	id?: number;
	manufacturerPartNumber?: string;
	price: number;
	published: boolean;
	purchasable: boolean;
	replacementSkuId?: number;
	sku: string;
};

type TSkuUnitOfMeasure = {
	active?: boolean;
	basePrice?: number;
	incrementalOrderQuantity?: number;
	key?: string;
	name?: {
		[key: string]: string;
	};
	precision?: number;
	pricingQuantity?: number;
	primary?: boolean;
	priority?: number;
	rate?: number;
};

export class HeadlessCommerceAdminCatalogApiHelper {
	readonly apiHelpers: ApiHelpers | DataApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers | DataApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'headless-commerce-admin-catalog/v1.0';
	}

	async deleteAttachment(attachmentId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/attachment/${attachmentId}`
		);
	}

	async deleteCatalog(catalogId: number | string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/catalog/${catalogId}`
		);
	}

	async deleteOption(optionId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/options/${optionId}`
		);
	}

	async deleteOptionCategory(optionCategoryId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/optionCategories/${optionCategoryId}`
		);
	}

	async deletePin(pinId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/pins/${pinId}`
		);
	}

	async deleteProduct(productId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}`
		);
	}

	async deleteProductAccountGroup(id: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-account-groups/${id}`
		);
	}

	async deleteProductByVersion(productId: number, version: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/by-version/${version}`
		);
	}

	async deleteProductConfiguration(productConfigurationId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configurations/${productConfigurationId}`
		);
	}

	async deleteProductConfigurationList(productConfigurationListId: number) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists/${productConfigurationListId}`
		);
	}

	async deleteRelatedProduct(relatedProductId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/relatedProducts/${relatedProductId}`
		);
	}

	async deleteSkuUnitOfMeasure(skuUnitOfMeasureId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/sku-unit-of-measures/${skuUnitOfMeasureId}`
		);
	}

	async deleteSpecification(specificationId: string) {
		return this.apiHelpers.delete(
			`${this.apiHelpers.baseUrl}${this.basePath}/specifications/${specificationId}`
		);
	}

	async getCatalog(catalogId: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/catalogs/${catalogId}`
		);
	}

	async getCatalogsPage(search: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/catalogs?search=${search}`
		);
	}

	async getCurrenciesPage(search = '') {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/currencies?search=${search}`
		);
	}

	async getOptionCategory(optionCategoryId: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/optionCategories/${optionCategoryId}`
		);
	}

	async getOptionCategories() {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/optionCategories`
		);
	}

	async getOptions() {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/options`
		);
	}

	async getProduct(productId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}?nestedFields=skus`
		);
	}

	async getProductAccountGroups(productId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/product-account-groups`
		);
	}

	async getProductByVersion(productId: number, version: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/by-version/${version}`
		);
	}

	async getProductConfigurationListsPage(search: string = '') {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists?search=${search}`
		);
	}

	async getProducts(searchParams = new URLSearchParams()) {
		if (!searchParams.has('nestedFields')) {
			searchParams.append('nestedFields', 'skus');
		}

		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${
				this.basePath
			}/products?${searchParams.toString()}`
		);
	}

	async getProductsPage(pageSize: number, search: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/products?pageSize=${pageSize}&search=${search}`
		);
	}

	async getProductVirtualSettings(productId: number) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/product-virtual-settings`
		);
	}

	async getSkuByName(name: string) {
		const skus = await this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/skus?search=${name}`
		);

		return skus.items[0];
	}

	async getSpecification(specificationId: string) {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/specifications/${specificationId}`
		);
	}

	async getSpecifications() {
		return this.apiHelpers.get(
			`${this.apiHelpers.baseUrl}${this.basePath}/specifications`
		);
	}

	async patchCurrency(currencyId: string, currency?: TCurrency) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/currencies/${currencyId}`,
			{
				...(currency || {}),
			}
		);
	}

	async patchProduct(productId: string, product?: DataObject) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}`,
			{
				name: {
					en_US: 'Product' + getRandomInt(),
				},
				...(product || {}),
			}
		);
	}

	async patchProductByErc(
		externalReferenceCode: string,
		product?: DataObject
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/by-externalReferenceCode/${externalReferenceCode}`,
			{
				name: {
					en_US: `Product${getRandomInt()}`,
				},
				...(product || {}),
			}
		);
	}

	async patchProductTaxConfiguration(
		productId: number,
		productTaxConfiguration: TProductTaxConfiguration
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/taxConfiguration`,
			{
				...(productTaxConfiguration || {}),
			}
		);
	}

	async patchSpecification(
		specificationId: string,
		listTypeDefinitionIds: number[]
	) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/specifications/${specificationId}`,
			{
				listTypeDefinitionIds,
			}
		);
	}

	async patchSku(cpInstanceId: string, sku?: TSku) {
		return this.apiHelpers.patch(
			`${this.apiHelpers.baseUrl}${this.basePath}/skus/${cpInstanceId}`,
			{sku: 'Sku' + getRandomInt(), ...(sku || {})}
		);
	}

	async postAttachment(
		productId: number,
		fileEntryId: number,
		title: string = 'Attachment' + getRandomInt()
	) {
		const postAttachment = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/attachments`,
			{
				data: {
					fileEntryId,
					title: {en_US: title},
				},
			}
		);

		return postAttachment;
	}

	async postCatalog(catalog?: TCatalog) {
		catalog = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/catalogs`,
			{
				data: {
					accountId: 0,
					currencyCode: 'USD',
					defaultLanguageId: 'en_US',
					name: 'Catalog' + getRandomInt(),
					...(catalog || {}),
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: catalog.id, type: 'catalog'});
		}

		return catalog;
	}

	async postImage(
		productId: number,
		fileEntryId: number,
		title: string = 'Image' + getRandomInt()
	) {
		const postImage = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/images`,
			{
				data: {
					fileEntryId,
					title: {en_US: title},
				},
			}
		);

		return postImage;
	}

	async postOption(
		fieldType: string = 'select',
		key: string = 'key-' + getRandomInt(),
		name: string = 'Option' + getRandomInt(),
		priority: number = getRandomInt()
	) {
		const postOption = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/options`,
			{
				data: {
					fieldType,
					key,
					name: {
						en_US: name,
					},
					priority,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: postOption.id, type: 'option'});
		}

		return postOption;
	}

	async postOptionCategory(
		optionCategoryName: string = 'OptionCategory' + getRandomInt(),
		priority: number = getRandomInt()
	) {
		const postOptionCategory = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/optionCategories`,
			{
				data: {
					key: optionCategoryName,
					priority,
					title: {
						en_US: optionCategoryName,
					},
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: postOptionCategory.id,
				type: 'optionCategory',
			});
		}

		return postOptionCategory;
	}

	async postPin(productId: number, pin: TPin): Promise<TPin> {
		pin = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/pins`,
			{
				data: {
					mappedProduct: {
						productId: 0,
						quantity: 1,
						sequence: '1',
						skuId: 0,
						type: 'sku',
					},
					positionX: getRandomDouble(),
					positionY: getRandomDouble(),
					sequence: '1',
					...pin,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: pin, type: 'pin'});
		}

		return pin;
	}

	async postProduct(product: TProduct): Promise<TProduct> {
		product = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/products?nestedFields=productOptions,productSpecifications,skus`,
			{
				data: {
					active: true,
					catalogId: 0,
					name: {
						en_US: 'Product' + getRandomInt(),
					},
					productStatus: 0,
					productType: 'simple',
					skus: [
						{
							cost: 0,
							price: 0,
							published: true,
							purchasable: true,
							sku: 'Sku' + getRandomInt(),
						},
					],
					...product,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({id: product.productId, type: 'product'});
		}

		return product;
	}

	async postProductBatch(products: TProduct[]): Promise<TProduct[]> {
		products = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/batch`,
			{data: products}
		);

		return products;
	}

	async postProductConfiguration(
		productConfigurationListId: number,
		productConfiguration: TProductConfiguration
	): Promise<TProductConfiguration> {
		productConfiguration = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists/${productConfigurationListId}/product-configurations`,
			{
				data: {
					allowBackOrder: true,
					entityType: 'product',
					maxOrderQuantity: 10000,
					minOrderQuantity: 1,
					multipleOrderQuantity: 1,
					purchasable: true,
					visible: true,
					...productConfiguration,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: productConfiguration.id,
				type: 'productConfiguration',
			});
		}

		return productConfiguration;
	}

	async postProductConfigurationList(
		productConfigurationList: TProductConfigurationList
	): Promise<TProductConfigurationList> {
		productConfigurationList = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists?nestedFields=productConfigurations`,
			{
				data: {
					catalogId: getRandomInt(),
					name: getRandomString(),
					...productConfigurationList,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: productConfigurationList.id,
				type: 'productConfigurationList',
			});
		}

		return productConfigurationList;
	}

	async postProductConfigurationListAccountGroup(
		accountGroupId: number,
		productConfigurationListId: number
	): Promise<TProductConfigurationList> {
		const productConfigurationList = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists/${productConfigurationListId}/product-configuration-list-account-groups`,
			{
				data: {
					accountGroupId,
					productConfigurationListId,
				},
			}
		);

		return productConfigurationList;
	}

	async postProductConfigurationListChannel(
		channelId: number,
		productConfigurationListId: number
	): Promise<TProductConfigurationList> {
		const productConfigurationList = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists/${productConfigurationListId}/product-configuration-list-channels`,
			{
				data: {
					channelId,
					productConfigurationListId,
				},
			}
		);

		return productConfigurationList;
	}

	async postProductConfigurationListOrderType(
		orderTypeId: number,
		productConfigurationListId: number
	): Promise<TProductConfigurationList> {
		const productConfigurationList = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/product-configuration-lists/${productConfigurationListId}/product-configuration-list-order-types`,
			{
				data: {
					orderTypeId,
					productConfigurationListId,
				},
			}
		);

		return productConfigurationList;
	}

	async postProductRelatedProduct(
		productId: number,
		relatedProduct: TRelatedProduct
	): Promise<TRelatedProduct> {
		relatedProduct = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/products/${productId}/relatedProducts`,
			{
				data: {
					priority: 1,
					type: 'cross-sell',
					...relatedProduct,
				},
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: relatedProduct.id,
				type: 'relatedProduct',
			});
		}

		return relatedProduct;
	}

	async postSkuUnitOfMeasure(
		skuId: number,
		skuUnitOfMeasure: TSkuUnitOfMeasure
	) {
		const postSkuUnitOfMeasure = await this.apiHelpers.post(
			`${this.apiHelpers.baseUrl}${this.basePath}/skus/${skuId}/sku-unit-of-measures`,
			{
				data: {
					active: true,
					basePrice: 0,
					incrementalOrderQuantity: 1,
					key: 'key-' + getRandomInt(),
					name: {
						en_US: 'UOM' + getRandomInt(),
					},
					primary: false,
					priority: getRandomInt(),
					rate: getRandomInt(),
					...skuUnitOfMeasure,
				},
				failOnStatusCode: true,
			}
		);

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: postSkuUnitOfMeasure.id,
				type: 'skuUnitOfMeasure',
			});
		}

		return postSkuUnitOfMeasure;
	}

	async postSpecification(
		facetable: boolean = true,
		priority: number = 0,
		specificationTitle: string = 'Specification' + getRandomInt(),
		optionCategory?: DataObject,
		visible?: boolean,
		listTypeDefinitionIds?: number[]
	) {
		let postSpecification;

		if (typeof optionCategory !== 'undefined') {
			postSpecification = await this.apiHelpers.post(
				`${this.apiHelpers.baseUrl}${this.basePath}/specifications`,
				{
					data: {
						facetable,
						key: specificationTitle,
						listTypeDefinitionIds,
						optionCategory,
						priority,
						title: {
							en_US: specificationTitle,
						},
						visible,
					},
				}
			);
		}
		else {
			postSpecification = await this.apiHelpers.post(
				`${this.apiHelpers.baseUrl}${this.basePath}/specifications`,
				{
					data: {
						facetable,
						key: specificationTitle,
						priority,
						title: {
							en_US: specificationTitle,
						},
						visible,
					},
				}
			);
		}

		if (this.apiHelpers instanceof DataApiHelpers) {
			this.apiHelpers.data.push({
				id: postSpecification.id,
				type: 'specification',
			});
		}

		return postSpecification;
	}
}
