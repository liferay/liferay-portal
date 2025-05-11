/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ProductPriceModel,
	ProductSpecificationKey,
	ProductType,
	ProductTypeLabels,
	ProductVocabulary,
} from '../enums/Product';
import i18n from '../i18n';
import {ConsoleUserProject} from '../services/oauth/types';

const productTypeIcons = {
	cloud: 'cloud',
	dxp: 'site-template',
};

export class MarketplaceDeliveryProduct {
	constructor(protected product: DeliveryProduct) {}

	get createDate() {
		return this.product.createDate;
	}

	get catalogName() {
		return this.product.catalogName;
	}

	get friendlyURL() {
		return this.product.urls.en_US;
	}

	get appType() {
		const {APP_TYPE} = this.specificationValues;

		let type: string =
			ProductTypeLabels[APP_TYPE as unknown as ProductType];

		if (!type) {
			const categories = this.getCategories(
				ProductVocabulary.PRODUCT_TYPE
			);

			type = categories[0]?.name;
		}

		return type || APP_TYPE;
	}

	get appVersion() {
		const {APP_VERSION} = this.specificationValues;

		return APP_VERSION || '1.0.0';
	}

	getPurchasableSKUs() {
		return this.product.skus.filter(({purchasable}) => purchasable);
	}

	get specificationValues() {
		const _specifications = {} as typeof ProductSpecificationKey;

		for (const specificationKey in ProductSpecificationKey) {
			const _key =
				specificationKey as keyof typeof ProductSpecificationKey;

			(_specifications as any)[_key] = this.getSpecificationValue(
				ProductSpecificationKey[_key]
			);
		}

		return _specifications;
	}

	private getCategories(vocabulary: string) {
		return this.product.categories.filter(
			(category) =>
				category.vocabulary?.toLowerCase() === vocabulary?.toLowerCase()
		);
	}

	public getProductImages() {
		return this.product.images
			.filter((image) => image.priority !== 0)
			.map((image) => image.src);
	}

	public getPrice() {
		const priceModel = this.getPriceModel();

		if (priceModel.toLowerCase() === ProductPriceModel.FREE.toLowerCase()) {
			return ProductPriceModel.FREE;
		}

		const [purchasableSKU] = this.getPurchasableSKUs();

		return purchasableSKU?.price?.priceFormatted;
	}

	public getAppCategories() {
		return this.getCategories(ProductVocabulary.APP_CATEGORY);
	}

	public getSolutionCategories() {
		return this.getCategories(ProductVocabulary.SOLUTION_CATEGORY);
	}

	public getCloudResourceLabel(consoleUserProject: ConsoleUserProject) {
		let output = '';

		if (!consoleUserProject) {
			return output;
		}

		const round = (value: number) => {
			if (!value) {
				return 0;
			}

			return Math.floor(value);
		};

		output += `${consoleUserProject.environments.length} ${i18n.translate('environment')}, `;
		output += `${round(consoleUserProject.rootProjectPlanUsage.cpu.free)} CPUs, `;
		output += `${round(consoleUserProject.rootProjectPlanUsage.memory.free / 1000)} GB RAM`;

		return output;
	}

	public getEditions() {
		return this.getCategories(ProductVocabulary.EDITION);
	}

	public hasEnoughResources(cloudUserProject: ConsoleUserProject) {
		if (!cloudUserProject) {
			return false;
		}

		if (!cloudUserProject.rootProjectPlanUsage.instance.free) {
			return false;
		}

		const cpuSpecification = Number(
			this.getSpecificationValue(
				ProductSpecificationKey.APP_BUILD_NUMBER_OF_CPUS,
				'0'
			)
		);

		const ramSpecification = Number(
			this.getSpecificationValue(
				ProductSpecificationKey.APP_BUILD_RAM_IN_GBS,
				'0'
			)
		);

		if (
			cloudUserProject.rootProjectPlanUsage.cpu.free < cpuSpecification ||
			Math.floor(
				cloudUserProject.rootProjectPlanUsage.memory.free / 1000
			) < ramSpecification
		) {
			return false;
		}

		return true;
	}

	public getPlatformOfferings() {
		return this.getCategories(ProductVocabulary.LIFERAY_PLATFORM_OFFERING);
	}

	public getProductType() {
		const type = this.getSpecificationValue(
			ProductSpecificationKey.APP_TYPE
		);

		return {
			icon:
				(productTypeIcons as any)[
					type as keyof typeof productTypeIcons
				] || 'cog',
			label: `${type} App`,
			type,
		};
	}

	public getPriceModel() {
		return this.getSpecificationValue(
			ProductSpecificationKey.APP_PRICING_MODEL,
			'Free'
		);
	}

	public getSpecification(
		specificationKey: string | typeof ProductSpecificationKey
	) {
		return this.product.productSpecifications.find(
			(specification) =>
				specification.specificationKey === specificationKey
		);
	}

	private getSpecificationValue(
		specificationKey: string | typeof ProductSpecificationKey,
		value = ''
	) {
		return this.getSpecification(specificationKey)?.value || value;
	}

	public get productImage() {
		return this.product.urlImage;
	}

	public getProductResourceLabel() {
		const cpuSpecification = this.getSpecificationValue(
			ProductSpecificationKey.APP_BUILD_NUMBER_OF_CPUS,
			'0'
		);

		const ramSpecification = this.getSpecificationValue(
			ProductSpecificationKey.APP_BUILD_RAM_IN_GBS,
			'0'
		);

		return `${cpuSpecification}CPUs, ${ramSpecification}GB RAM`;
	}
}
