/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {UploadedImage} from '../../components/FileList/FileList';
import {MarketplaceProduct} from '../../entity/MarketplaceProduct';
import {axios} from '../../utils/axios';
import fetcher from '../fetcher';
import GraphQL from './HeadlessGraphQL';

export default class HeadlessCommerceAdminCatalog {
	static async addOrUpdateProductImageByExternalReferenceCode(
		externalReferenceCode: string,
		image: UploadedImage
	) {
		return fetcher.post(
			`/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}/images`,
			image
		);
	}

	static async createProductImageByExternalReferenceCodeAxios(
		externalReferenceCode: string,
		body: unknown,
		onUploadProgressCallback: (progress: number) => void = () => null
	) {
		return axios.post(
			`/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}/images`,
			body,
			{
				onUploadProgress: (event) =>
					onUploadProgressCallback(
						Math.round((event.loaded * 100) / Number(event.total))
					),
			}
		);
	}

	static async createProductOption(body: unknown[], productId: number) {
		return fetcher.post<APIResponse<ProductOption>>(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}/productOptions?nestedFields=productOptionValues`,
			body
		);
	}

	static async createProductOptionValue(body: unknown, optionId: number) {
		return fetcher.post(
			`/o/headless-commerce-admin-catalog/v1.0/productOptions/${optionId}/productOptionValues`,
			body
		);
	}

	static async createVirtualProduct({
		catalogId,
		categories,
		description,
		name,
		productSpecifications,
		productStatus,
		workflowStatusInfo,
	}: {
		catalogId: number;
		categories: Partial<Categories>[];
		description: string;
		name: string;
		productSpecifications?: any;
		productStatus?: number;
		workflowStatusInfo?: number;
	}) {
		return fetcher.post(
			`/o/headless-commerce-admin-catalog/v1.0/products?nestedFields=productVirtualSettings`,
			{
				active: true,
				catalogId,
				categories,
				description: {en_US: description},
				name: {en_US: name},
				productConfiguration: {
					allowBackOrder: true,
					maxOrderQuantity: 99,
				},
				productSpecifications,
				productStatus,
				productType: 'virtual',
				productVirtualSettings: {},
				workflowStatusInfo,
			}
		);
	}

	static async createProductSKU(body: unknown, productId: string | number) {
		return fetcher.post<SKU>(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}/skus`,
			body
		);
	}

	static async deleteAttachmentByExternalReferenceCode(
		externalReferenceCode: string
	) {
		return fetcher.delete(
			`/o/headless-commerce-admin-catalog/v1.0/attachment/by-externalReferenceCode/${externalReferenceCode}`
		);
	}

	static async updateProduct(productId: number, body: unknown) {
		return fetcher.patch(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}`,
			body
		);
	}

	static async createProductSpecification(
		productId: number | string,
		productSpecification: ProductSpecification
	) {
		return fetcher.post(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}/productSpecifications`,
			productSpecification
		);
	}

	static async deleteProduct(productId: string | number) {
		return fetcher.delete(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}`
		);
	}

	static async getCatalog(
		catalogId: string | number,
		searchParams = new URLSearchParams()
	) {
		return fetcher(
			`/o/headless-commerce-admin-catalog/v1.0/catalog/${catalogId}?${searchParams.toString()}`
		);
	}

	static async getCatalogs(searchParams = new URLSearchParams()) {
		return fetcher<APIResponse<Catalog>>(
			`/o/headless-commerce-admin-catalog/v1.0/catalogs?${searchParams.toString()}`
		);
	}

	static async getOptions() {
		return fetcher<APIResponse<CommerceOption>>(
			'/o/headless-commerce-admin-catalog/v1.0/options'
		);
	}

	static async getProduct(
		productId: string | number,
		searchParams = new URLSearchParams()
	) {
		return fetcher<Product>(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}?${searchParams.toString()}`
		);
	}

	static async getProductByExternalReferenceCode(
		externalReferenceCode: string,
		searchParams = new URLSearchParams()
	): Promise<Product> {
		return fetcher(
			`/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}?${searchParams.toString()}`
		);
	}

	static async getProducts(searchParams = new URLSearchParams()) {
		const response = await fetcher<APIResponse<Product>>(
			`/o/headless-commerce-admin-catalog/v1.0/products?${searchParams.toString()}`
		);

		return {
			...response,
			items: response.items.map((item) => ({
				...item,
				__marketplaceProduct: new MarketplaceProduct(item),
			})),
		};
	}

	static async getProductsDashboardKPI(
		filters: Record<string, string>,
		options?: Record<string, any>
	) {
		return GraphQL.metrics<Product>(
			{
				group: 'headlessCommerceAdminCatalog_v1_0',
				name: 'products',
			},
			filters,
			options
		);
	}

	static async getProductOptions(productId: number) {
		return fetcher<APIResponse<ProductOption>>(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}/productOptions?nestedFields=productOptionValues`
		);
	}

	static async getProductSkus(productId: string | number) {
		return fetcher<APIResponse<SKU>>(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}/skus`
		);
	}

	static async getProductSpecifications(productId: string | number) {
		const response = await fetcher(
			`/o/headless-commerce-admin-catalog/v1.0/products/${productId}/productSpecifications`
		);

		return (response?.items ?? []) as ProductSpecification[];
	}

	static async getSku(skuId: number, searchParams = new URLSearchParams()) {
		return fetcher<SKU>(
			`o/headless-commerce-admin-catalog/v1.0/skus/${skuId}?${searchParams}`
		);
	}

	static async getSpecifications(searchParams = new URLSearchParams()) {
		return fetcher<APIResponse>(
			`/o/headless-commerce-admin-catalog/v1.0/specifications?${searchParams}`
		);
	}

	static async updateProductByExternalReferenceCode(
		externalReferenceCode: string,
		body: unknown
	) {
		return fetcher.patch(
			`/o/headless-commerce-admin-catalog/v1.0/products/by-externalReferenceCode/${externalReferenceCode}`,
			body
		);
	}

	static async updateProductSpecification(
		id: number | string,
		productSpecification: ProductSpecification
	) {
		return fetcher.patch(
			`/o/headless-commerce-admin-catalog/v1.0/productSpecifications/${id}`,
			productSpecification
		);
	}
}
