/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createResourceURL, fetch} from 'frontend-js-web';

import {
	APIResponse,
	Cart,
	CloudUserProject,
	MarketplaceAuthorization,
	MarketplaceConfiguration,
	PlacedOrder,
	Product,
} from '../types';

type FetchOptions = RequestInit & {
	earlyReturn?: boolean;
	guestOperation?: boolean;
};

class MarketplaceRestError extends Error {
	public info: any;
	public status?: number;

	constructor(message: string) {
		super(message);
	}
}

export function safeJSONParse(value: string) {
	try {
		return JSON.parse(value);
	}
	catch {
		return null;
	}
}

async function getBaseFetch<T = any>(url: string, options?: FetchOptions) {
	const response = await fetch(url, options);

	if (!response.ok) {
		const error = new MarketplaceRestError(
			'An error occurred while fetching the data.'
		);

		error.info = await response.json();
		error.status = response.status;
		throw error;
	}

	if (options?.earlyReturn) {
		return response as unknown as T;
	}

	return response.json() as unknown as T;
}

const removeEdgesUnderscoresRegEx = /^_+|_+$/g;
const sessionKey = '@marketplace/token';

export class MarketplaceRest {
	private tokenRequestPromise: Promise<MarketplaceAuthorization> | null =
		null;

	constructor(
		protected baseResourceURL: string,
		protected marketplaceConfiguration: MarketplaceConfiguration
	) {}

	public async consoleProvisioningOrder(cart: Cart) {
		return this.fetchMarketplaceService<Response>(
			`/dxp/provisioning/${cart.id}`,
			{
				body: JSON.stringify({
					orderItemId: cart.cartItems[0]?.id,
					projectId: this.settings.cloudProject,
				}),
				earlyReturn: true,
				method: 'POST',
			}
		);
	}

	public async createCart(product: Product, baseCart?: Partial<Cart>) {
		const {account, channelId} = this.settings;

		const purchasableSKUs = product.skus.filter(
			({purchasable}) => purchasable
		);

		const cart = await this.fetchMarketplace<Cart>(
			`/o/headless-commerce-delivery-cart/v1.0/channels/${channelId}/carts?nestedFields=cartItems`,
			{
				body: JSON.stringify({
					accountId: account.id,
					cartItems: [
						{
							price: {
								currency: 'USD',
								discount: 0,
							},
							productId: product.productId,
							quantity: 1,
							settings: {
								maxQuantity: 1,
							},
							skuId: purchasableSKUs[0]?.id,
						},
					],
					currencyCode: 'USD',
					...baseCart,
				}),
				method: 'POST',
			}
		);

		return cart;
	}

	public async checkoutCart(cart: Cart) {
		return this.fetchMarketplace<Cart>(
			`/o/headless-commerce-delivery-cart/v1.0/carts/${cart.id}/checkout`,
			{
				method: 'POST',
			}
		);
	}

	static getBaseResourceURL(portletId?: string) {
		if (!portletId) {
			return `/group/guest/~/control_panel/manage?p_p_id=${Liferay.PortletKeys.INSTANCE_SETTINGS}`;
		}

		const currentURL = new URL(window.location.href);

		if (!currentURL.searchParams.get('p_p_id')) {
			const cleanPortletId = portletId.replace(
				removeEdgesUnderscoresRegEx,
				''
			);
			currentURL.searchParams.set('p_p_id', cleanPortletId);
		}

		return currentURL.href;
	}

	private static async getMarketplaceConfiguration() {
		const configuration = await getBaseFetch<{
			authorized: boolean;
			data: MarketplaceConfiguration;
		}>(
			createResourceURL(this.getBaseResourceURL(), {
				p_p_resource_id: '/marketplace_settings/get_configuration',
			}).toString()
		);

		return configuration.data;
	}

	public static async getMarketplaceRestWithConfiguration() {
		return new MarketplaceRest(
			this.getBaseResourceURL(),
			await this.getMarketplaceConfiguration()
		);
	}

	public async fetchMarketplace<T>(url: string, options?: FetchOptions) {
		const headers = {
			'Authorization': '',
			'Content-Type': 'application/json' as any,
			...options?.headers,
		};

		if (options?.body instanceof FormData) {
			delete headers['Content-Type'];
		}

		if (!options?.guestOperation) {
			const {accessToken} = await this.getMarketplaceToken();

			headers.Authorization = `Bearer ${accessToken}`;
		}

		return getBaseFetch<T>(`${this.marketplaceURL}${url}`, {
			...options,
			headers,
		});
	}

	public async fetchMarketplaceService<T>(
		url: string,
		options?: FetchOptions
	) {
		const {accessToken} = await this.getMarketplaceToken();

		return getBaseFetch<T>(`${this.marketplaceServiceURL}${url}`, {
			...options,
			headers: {
				'Authorization': `Bearer ${accessToken}`,
				'Content-Type': 'application/json',
			},
		});
	}

	public async getMarketplaceToken() {
		const cachedToken = safeJSONParse(
			Liferay.Util.SessionStorage.getItem(
				sessionKey,
				Liferay.Util.SessionStorage.TYPES.NECESSARY
			) as string
		);
		if (
			cachedToken &&
			new Date().getTime() < Number(cachedToken.accessTokenExpirationTime)
		) {
			return cachedToken as MarketplaceAuthorization;
		}

		// If a token request is already in progress, wait for it

		if (this.tokenRequestPromise) {
			return this.tokenRequestPromise;
		}

		this.tokenRequestPromise = (async () => {
			try {
				const authorization =
					await getBaseFetch<MarketplaceAuthorization>(
						createResourceURL(this.baseResourceURL, {
							p_p_resource_id:
								'/marketplace_settings/get_authorization',
						}).toString()
					);

				Liferay.Util.SessionStorage.setItem(
					sessionKey,
					JSON.stringify(authorization),
					Liferay.Util.SessionStorage.TYPES.NECESSARY
				);

				return authorization;
			}
			finally {
				this.tokenRequestPromise = null;
			}
		})();

		return this.tokenRequestPromise;
	}

	public async getPlacedOrder(
		orderId: number,
		urlSearchParams = new URLSearchParams()
	) {
		return this.fetchMarketplace<PlacedOrder>(
			`/o/headless-commerce-delivery-order/v1.0/placed-orders/${orderId}?${urlSearchParams.toString()}`
		);
	}

	public async getPlacedOrders(urlSearchParams = new URLSearchParams()) {
		const {
			account: {id: accountId},
			channelId,
		} = this.settings;

		return this.fetchMarketplace<APIResponse<PlacedOrder>>(
			`/o/headless-commerce-delivery-order/v1.0/channels/${channelId}/accounts/${accountId}/placed-orders?${urlSearchParams.toString()}`
		);
	}

	public async getProducts(urlSearchParams = new URLSearchParams()) {
		return this.fetchMarketplace<APIResponse<Product>>(
			`/o/headless-commerce-delivery-catalog/v1.0/channels/${this.settings.channelId}/products?${urlSearchParams.toString()}`
		);
	}

	public async getProjectUsage() {
		return this.fetchMarketplaceService<CloudUserProject>(
			`/dxp/project-usage?projectId=${this.settings.cloudProject}`
		);
	}

	private get marketplaceServiceURL() {
		return this.marketplaceConfiguration.serviceURL;
	}

	private get marketplaceURL() {
		return this.marketplaceConfiguration.url;
	}

	public get settings() {
		return this.marketplaceConfiguration.settings || {};
	}
}
