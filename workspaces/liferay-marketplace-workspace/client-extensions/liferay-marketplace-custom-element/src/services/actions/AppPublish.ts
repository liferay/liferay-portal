/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {NewAppInitialState} from '../../context/NewAppContext';
import SearchBuilder from '../../core/SearchBuilder';
import {
	ProductLicense,
	ProductOfferingTypes,
	ProductSpecificationKey,
	ProductTags,
	ProductType,
	ProductTypeVocabulary,
	ProductVocabulary,
	ProductWorkflowStatusCode,
	SkuOptions,
	getOfferingTypes,
} from '../../enums/Product';
import {Liferay} from '../../liferay/liferay';
import {createProductVirtualEntry} from '../../utils/api';
import {base64ToText, fileToBase64} from '../../utils/file';
import HeadlessCommerceAdminCatalogImpl from '../rest/HeadlessCommerceAdminCatalog';
import HeadlessCommerceAdminPricing from '../rest/HeadlessCommerceAdminPricing';
import BaseAppPublish from './BaseAppPublish';

type ProductConfig = {
	isDraft: boolean;
};

function normalizeCategory(category: {
	label: string;
	value: number;
}): Partial<Categories> {
	return {
		id: String(category.value),
		name: category.label,
	};
}

function isTierPriceChanged(
	currentTierPrices: TierPrice[],
	newTierPrices: TierPrice[]
): boolean {
	if (currentTierPrices.length !== newTierPrices.length) {
		return true;
	}

	const priceMap = new Map(
		currentTierPrices.map(({minimumQuantity, price}) => [
			minimumQuantity,
			price,
		])
	);

	for (let i = 0; i < newTierPrices.length; i++) {
		const {minimumQuantity, price} = newTierPrices[i];
		if (priceMap.get(minimumQuantity) !== price) {
			return true;
		}
	}

	return false;
}

export default class AppPublish extends BaseAppPublish {
	private config: ProductConfig = {isDraft: false};

	constructor(private context: NewAppInitialState) {
		super();
	}

	private async createProductSKUs(product: Product) {
		if (!product.productOptions) {
			const _productOptions =
				await HeadlessCommerceAdminCatalogImpl.getProductOptions(
					product.productId
				);

			product.productOptions = _productOptions.items;
		}

		const [productOption] = product.productOptions ?? [];

		if (!product?.skus || !product.skus.length) {
			product.skus = [];
		}

		const productOptionValues = productOption.productOptionValues ?? [];

		for (const productOptionValue of productOptionValues) {
			const sku = await HeadlessCommerceAdminCatalogImpl.createProductSKU(
				{
					published: true,
					purchasable: true,
					sku: productOptionValue.name.en_US,
					skuOptions: [
						{
							key: productOption.id,
							value: productOptionValue.id,
						},
					],
				},
				product.productId
			);

			product.skus.push(sku);
		}
	}

	private async createProductOption(product: Product) {
		if (product?.productOptions?.length) {
			return product?.productOptions[0];
		}

		const {items: options} =
			await HeadlessCommerceAdminCatalogImpl.getOptions();

		const option = options.find(
			(option) => option.key === this.getProductOptionKey()
		);

		if (!option) {
			return;
		}

		(option as any).optionId = option?.id;

		delete (option as any).actions;
		delete (option as any).externalReferenceCode;

		const {
			items: [productOption],
		} = await HeadlessCommerceAdminCatalogImpl.createProductOption(
			[option],
			product.productId
		);

		if (!product.productOptions) {
			product.productOptions = [];

			product.productOptions.push(productOption);
		}

		return productOption;
	}

	private getProductOptionKey() {
		const optionsTypes = {
			[ProductType.CLOUD]: ProductLicense.CLOUD,
			[ProductType.DXP]: ProductLicense.DXP,
		};

		return (
			optionsTypes[
				this.context.build.appType as keyof typeof optionsTypes
			] || ProductLicense.BASE
		);
	}

	private getProductStatus() {
		const productStatus = this.config.isDraft
			? ProductWorkflowStatusCode.DRAFT
			: ProductWorkflowStatusCode.PENDING;

		return {
			productStatus,
			workflowStatusInfo: productStatus,
		};
	}

	async syncProfile() {
		const {
			_product,
			catalog,
			profile: {areas, categories, description, file, name, tags},
			references: {vocabulariesAndCategories},
		} = this.context;

		const productTypeCategories = (
			vocabulariesAndCategories[ProductVocabulary.PRODUCT_TYPE]
				?.categories ?? []
		).filter(({label}: any) => label === ProductTypeVocabulary.APP);

		const productCategories = [
			...areas,
			...productTypeCategories,
			...tags,
			categories,
		].map(normalizeCategory);

		if (_product) {
			if (file && (!file?.uploaded || file?.changed)) {
				await HeadlessCommerceAdminCatalogImpl.createProductImageByExternalReferenceCodeAxios(
					_product.externalReferenceCode,
					{
						attachment: base64ToText(
							(await fileToBase64(file.file)) as string
						),
						galleryEnabled: false,
						neverExpire: true,
						priority: 0,
						tags: [ProductTags.APP_ICON],
						title: {
							en_US: file.fileName,
						},
					}
				);
			}

			await HeadlessCommerceAdminCatalogImpl.updateProduct(
				_product.productId as number,
				{
					categories: productCategories,
					description: {en_US: description},
					name: {en_US: name},
					...this.getProductStatus(),
				}
			);

			const product = await HeadlessCommerceAdminCatalogImpl.getProduct(
				_product.productId,
				new URLSearchParams({
					nestedFields:
						'attachments,catalog,images,productSpecifications,productOptions,productVirtualSettings,skus',
				})
			);

			return product;
		}

		const product =
			await HeadlessCommerceAdminCatalogImpl.createVirtualProduct({
				catalogId: catalog.id,
				categories: productCategories,
				description,
				name,
				...this.getProductStatus(),
			});

		product.productSpecifications = [
			{
				key: ProductSpecificationKey.APP_DEVELOPER_NAME,
				value: catalog?.name,
			},
		];

		await BaseAppPublish.updateSpecifications(
			product,
			product.productSpecifications
		);

		if (file.file) {
			await HeadlessCommerceAdminCatalogImpl.createProductImageByExternalReferenceCodeAxios(
				product.externalReferenceCode,
				{
					attachment: base64ToText(
						(await fileToBase64(file.file)) as string
					),
					galleryEnabled: false,
					neverExpire: true,
					priority: 0,
					tags: [ProductTags.APP_ICON],
					title: {
						en_US: file.fileName,
					},
				}
			);
		}

		return product;
	}

	async syncBuild(product: Product) {
		const {
			_product,
			build: {appType, liferayPackages, resourceRequirements},
		} = this.context;

		const specifications = [
			{
				key: ProductSpecificationKey.APP_TYPE,
				value: appType as string,
			},
		];

		if (appType === ProductType.CLOUD) {
			const resourceRequirementSpecifications = [
				{
					key: ProductSpecificationKey.APP_BUILD_NUMBER_OF_CPUS,
					value: resourceRequirements.cpu as string,
				},
				{
					key: ProductSpecificationKey.APP_BUILD_RAM_IN_GBS,
					value: resourceRequirements.ram as string,
				},
			];

			specifications.push(...resourceRequirementSpecifications);
		}

		const {
			[ProductVocabulary.LIFERAY_PLATFORM_OFFERING]:
				compatibleOfferingVocabulary,
		} = this.context.references.vocabulariesAndCategories;

		const platformOfferingLabels = getOfferingTypes(appType);

		const compatibleOfferingCategories =
			compatibleOfferingVocabulary.categories ?? [];

		const compatibleOfferings = compatibleOfferingCategories
			.filter(({label}: {label: string}) =>
				platformOfferingLabels.includes(label as ProductOfferingTypes)
			)
			.map(normalizeCategory);

		await HeadlessCommerceAdminCatalogImpl.updateProduct(
			product.productId,
			{
				categories: [...product.categories, ...compatibleOfferings],
				...this.getProductStatus(),
			}
		);

		const liferayVersions = [];

		for (const liferayPackage of liferayPackages) {
			const {file, versions} = liferayPackage;

			if (file && file.file) {
				const formData = new FormData();
				const blob = new Blob([file.file]);

				formData.append('file', blob, file.fileName);
				formData.append(
					'productVirtualSettingsFileEntry',
					JSON.stringify({version: versions.toString()})
				);

				await createProductVirtualEntry({
					body: formData,
					callback: () => {},
					virtualSettingId: _product?.productVirtualSettings.id ?? '',
				});
			}

			liferayVersions.push(...versions);
		}

		const liferayVersionSpecifications = Array.from(
			new Set(liferayVersions)
		)
			.toSorted()
			.map((version) => ({
				key: ProductSpecificationKey.LIFERAY_VERSION,
				value: version,
			}));

		await BaseAppPublish.updateSpecifications(product, [
			...specifications,
			...liferayVersionSpecifications,
		]);
	}

	async syncLicensing(product: Product) {
		const {
			licensing: {licenseType},
		} = this.context;

		if (!licenseType) {
			return;
		}

		await BaseAppPublish.updateSpecification(
			product,
			ProductSpecificationKey.APP_LICENSING_TYPE,
			licenseType
		);

		await this.createProductOption(product);

		await this.createProductSKUs(product);

		await this.updatePrices();
	}

	async syncPricing(product: Product) {
		const {
			pricing: {priceModel},
		} = this.context;

		await BaseAppPublish.updateSpecification(
			product,
			ProductSpecificationKey.APP_PRICING_MODEL,
			priceModel
		);
	}

	async syncSupport(product: Product) {
		const {support} = this.context;

		await BaseAppPublish.updateSpecifications(product, [
			{
				key: ProductSpecificationKey.APP_SUPPORT_USAGE_TERMS_URL,
				value: support.appUsageTermsURL,
			},

			{
				key: ProductSpecificationKey.APP_SUPPORT_DOCUMENTATION_URL,
				value: support.documentationURL,
			},

			{
				key: ProductSpecificationKey.APP_SUPPORT_EMAIL,
				value: support.email,
			},

			{
				key: ProductSpecificationKey.APP_SUPPORT_INSTALLATION_GUIDE_URL,
				value: support.installationGuideURL,
			},

			{
				key: ProductSpecificationKey.APP_SUPPORT_PHONE,
				value: support.phone,
			},

			{
				key: ProductSpecificationKey.APP_SUPPORT_PUBLISHER_WEBSITE_URL,
				value: support.publisherWebsiteURL,
			},

			{
				key: ProductSpecificationKey.APP_SUPPORT_URL,
				value: support.url,
			},
		]);
	}

	async syncStorefront(product: Product) {
		const {
			storefront: {images, video},
		} = this.context;

		// Process Upload Images, priority starts in 1 to not conflict with
		// the app icon defined as priority 0

		await AppPublish.addOrUpdateImages(images, null, product, 1);

		await BaseAppPublish.updateSpecifications(product, [
			{
				key: ProductSpecificationKey.APP_STOREFRONT_VIDEO_DESCRIPTION,
				value: video.description as string,
			},
			{
				key: ProductSpecificationKey.APP_STOREFRONT_VIDEO_URL,
				value: video.videoURL as string,
			},
		]);
	}

	async syncVersion(product: Product) {
		const {
			version: {notes, version},
		} = this.context;

		await BaseAppPublish.updateSpecifications(product, [
			{
				key: ProductSpecificationKey.APP_VERSION,
				value: version,
			},
			{
				key: ProductSpecificationKey.APP_VERSION_NOTES,
				value: notes,
			},
		]);
	}

	public async sync(config: ProductConfig) {
		let product;

		this.config = config;

		try {
			product = await this.syncProfile();

			this.context._product = product;

			await AppPublish.deleteReferences(
				this.context.references.imagesToDelete
			);

			for (const sync of [
				this.syncBuild.bind(this),
				this.syncStorefront.bind(this),
				this.syncVersion.bind(this),
				this.syncPricing.bind(this),
				this.syncLicensing.bind(this),
				this.syncSupport.bind(this),
			]) {
				this.context._product = product;

				try {
					await sync(product);
				}
				catch (error) {
					console.error(`Unable to sync ${sync.name}`, error);
				}
			}
		}
		catch (error) {
			console.error(error);
		}

		return product;
	}

	private async deleteUnusedPriceLists(priceLists: PriceList[]) {
		const currencies = Object.keys(this.context.licensing.prices);

		const priceListsToDelete = priceLists.filter(
			({catalogId, currencyCode}) =>
				this.context.catalog.id === catalogId &&
				!currencies.includes(currencyCode)
		);

		await Promise.allSettled(
			priceListsToDelete.map(({id}) =>
				HeadlessCommerceAdminPricing.deletePriceList(id)
			)
		);
	}

	private getNonTrialSKUs() {
		const skus = (this.context._product?.skus || []).filter(
			({skuOptions}) =>
				skuOptions.some(({value}) => value !== SkuOptions.TRIAL)
		);

		return skus;
	}

	async updatePrices() {
		const skus = this.getNonTrialSKUs();

		const response = await HeadlessCommerceAdminPricing.getPriceLists(
			new URLSearchParams({
				filter: SearchBuilder.eq('type', 'price-list'),
				search: SearchBuilder.eq(
					'catalogName',
					this.context.catalog.name
				),
			})
		);

		await this.deleteUnusedPriceLists(response.items);

		for (const currencyCode in this.context.licensing.prices) {
			const prices = this.context.licensing.prices[currencyCode];

			let priceList = response.items.find(
				(item) =>
					item.catalogId === this.context.catalog.id &&
					item.currencyCode === currencyCode
			);

			if (!priceList) {
				priceList = await HeadlessCommerceAdminPricing.createPriceList({
					active: true,
					catalogId: this.context.catalog.id,
					currencyCode,
					name: `${Liferay.CommerceContext.account?.accountName} ${currencyCode} Price List`,
					type: 'price-list',
				});
			}

			const priceEntriesResponse =
				await HeadlessCommerceAdminPricing.getPriceListEntries(
					priceList.id,
					new URLSearchParams({
						nestedFields: 'product,sku',
						pageSize: '-1',
					})
				);

			const priceEntries = priceEntriesResponse.items.filter(
				({product}) => product.id === this.context._product!.id
			);

			for (let i = 0; i < skus.length; i++) {
				const sku = skus[i];

				const priceEntry = priceEntries.find(
					({sku: {id}}) => id === sku.id
				);

				const skuOptionValue = sku.skuOptions.find(
					(skuOption) => skuOption.key === this.getProductOptionKey()
				)?.value;

				if (!skuOptionValue) {
					continue;
				}

				const tierPrices =
					prices[skuOptionValue as keyof typeof prices];

				if (!tierPrices) {
					continue;
				}

				const tierPricesEntries = Object.entries(tierPrices).map(
					([quantity, price]) => ({
						active: true,
						minimumQuantity: Number(quantity),
						neverExpire: true,
						price,
						priceEntryId: priceEntry?.priceEntryId || 0,
					})
				);

				if (priceEntry) {
					await this.updatePriceEntry(
						priceEntry,
						tierPricesEntries as unknown as TierPrice[]
					);

					continue;
				}

				await HeadlessCommerceAdminPricing.createPriceEntry(
					{
						hasTierPrice: true,
						price: tierPricesEntries[0]?.price || 0,
						priceListId: priceList.id,
						sku: sku.sku,
						skuExternalReferenceCode: sku.externalReferenceCode,
						skuId: sku.id,
						tierPrices: tierPricesEntries,
					},
					priceList.id
				);
			}
		}
	}

	private async updatePriceEntry(
		priceEntry: PriceEntry,
		tierPricesEntries: TierPrice[]
	) {
		const {items: tierPrices} =
			await HeadlessCommerceAdminPricing.getTierPricesByPriceEntryId(
				priceEntry.priceEntryId
			);

		if (
			!isTierPriceChanged(
				tierPrices,
				tierPricesEntries as unknown as TierPrice[]
			)
		) {
			return;
		}

		await this.deleteUnusedTierPrices(tierPrices, tierPricesEntries);

		const tierPricesWithExternalReferenceCode = tierPricesEntries.map(
			(tierPriceEntry) => {
				const tierPrice = tierPrices.find(
					(tierPrice) =>
						tierPrice.minimumQuantity ===
						Number(tierPriceEntry.minimumQuantity)
				);

				if (tierPrice) {
					return {
						...tierPriceEntry,
						externalReferenceCode: tierPrice.externalReferenceCode,
						id: tierPrice.id,
					};
				}

				return tierPriceEntry;
			}
		);

		await HeadlessCommerceAdminPricing.updatePriceEntry(
			{
				...priceEntry,
				price: tierPricesEntries[0]?.price ?? priceEntry.price,
				tierPrices: tierPricesWithExternalReferenceCode,
			},
			priceEntry.priceEntryId
		);
	}

	private async deleteUnusedTierPrices(
		tierPrices: TierPrice[],
		tierPricesEntries: TierPrice[]
	) {
		const priceEntriesToDelete = tierPrices.filter(
			(tierPrice) =>
				!tierPricesEntries.some(
					(tierPriceEntry) =>
						tierPriceEntry.minimumQuantity ===
						tierPrice.minimumQuantity
				)
		);

		await Promise.allSettled(
			priceEntriesToDelete.map(({id}) =>
				HeadlessCommerceAdminPricing.deleteTierPrice(id)
			)
		);
	}
}
