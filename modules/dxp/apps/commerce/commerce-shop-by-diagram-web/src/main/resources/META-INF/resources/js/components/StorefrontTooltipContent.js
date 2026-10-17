/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayLabel from '@clayui/label';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClaySticker from '@clayui/sticker';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {AddToCartComponent, isProductPurchasable} from 'commerce-frontend-js';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {getCartItems} from '../utilities/data';
import {
	formatProductOptions,
	getProductName,
	getProductURL,
} from '../utilities/index';
import Price from './Price';

function SkuContent({
	accountId,
	cartId,
	channelGroupId,
	channelId,
	currencyCode,
	orderUUID,
	product: mappedProduct,
	productBaseURL,
}) {
	const product =
		mappedProduct.firstAvailableReplacementMappedProduct || mappedProduct;
	const isMounted = useIsMounted();
	const productURL = getProductURL(productBaseURL, product.urls);
	const productName = getProductName(product);
	const skuUnitOfMeasure = product.skuUnitOfMeasures?.[0];
	const [inCart, setInCart] = useState(false);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		if (!cartId) {
			setInCart(false);

			setLoading(false);

			return;
		}

		setLoading(true);

		getCartItems(cartId, product.skuId)
			.then((jsonResponse) => {
				if (isMounted()) {
					const items = jsonResponse.items || [];

					setInCart(
						items.some(
							({skuUnitOfMeasure: itemUnitOfMeasure}) =>
								skuUnitOfMeasure?.key === itemUnitOfMeasure?.key
						)
					);

					setLoading(false);
				}
			})
			.catch(() => {
				if (isMounted()) {
					setLoading(false);
				}
			});
	}, [cartId, isMounted, product.skuId, skuUnitOfMeasure?.key]);

	const productPurchasable = isProductPurchasable(
		product.availability,
		product.productConfiguration,
		product.purchasable
	);

	return (
		<div className="row">
			{mappedProduct.firstAvailableReplacementMappedProduct && (
				<div className="col-12">
					<ClayAlert
						className="p-2"
						displayType="warning"
						title={Liferay.Language.get('alert')}
					>
						{sub(
							Liferay.Language.get('x-has-been-replaced-by-x'),
							mappedProduct.sku,
							product.sku
						)}
					</ClayAlert>
				</div>
			)}

			{product.thumbnail && (
				<div className="col-auto">
					<ClaySticker className="fill-cover" size="xl">
						<ClaySticker.Image
							alt={productName}
							src={product.thumbnail}
						/>
					</ClaySticker>
				</div>
			)}

			<div className="col">
				<div className="mb-1">
					<ClayLabel
						displayType={productPurchasable ? 'success' : 'danger'}
					>
						{productPurchasable
							? Liferay.Language.get('available')
							: Liferay.Language.get('unavailable')}
					</ClayLabel>
				</div>

				<div className="component-title mb-1">
					<a href={productURL}>{product.sku}</a>
				</div>

				<p className="component-subtitle mb-1">
					<a href={productURL}>{productName}</a>
				</p>

				<p>
					{Liferay.Language.get('quantity')}: {product.quantity}
				</p>
			</div>

			<div className="col-3 text-right">
				{loading ? (
					<ClayLoadingIndicator className="my-3" small />
				) : (
					<>
						<Price className="mb-1" {...product.price} />

						<AddToCartComponent
							accountId={accountId}
							cartId={cartId}
							cartUUID={orderUUID}
							channel={{
								currencyCode,
								groupId: channelGroupId,
								id: channelId,
							}}
							cpInstance={{
								inCart,
								quantity: product.quantity,
								skuId: product.skuId,
								skuOptions: formatProductOptions(
									product.skuOptions,
									product.productOptions
								),
								skuUnitOfMeasure,
							}}
							disabled={!productPurchasable}
							settings={{
								alignment: 'full-width',
								iconOnly: true,
								inline: false,
								productConfiguration:
									product.productConfiguration,
								size: 'sm',
							}}
						/>
					</>
				)}
			</div>
		</div>
	);
}

function DiagramContent({product, productBaseURL}) {
	const productURL = getProductURL(productBaseURL, product.urls);
	const productName = getProductName(product);

	return (
		<div className="row">
			{product.thumbnail && (
				<div className="col-auto">
					<ClaySticker className="fill-cover" size="xl">
						<ClaySticker.Image
							alt={productName}
							src={product.thumbnail}
						/>
					</ClaySticker>
				</div>
			)}

			<div className="col">
				<div className="component-title">
					<a href={productURL}>{productName}</a>
				</div>
			</div>

			<div className="col-auto">
				<a className="btn btn-secondary" href={productURL}>
					{Liferay.Language.get('view')}
				</a>
			</div>
		</div>
	);
}

function ExternalContent({product}) {
	return (
		<>
			<div className="h4 mb-1">{product.sku || product.name}</div>

			{!!product.quantity && (
				<p className="mb-0">
					{Liferay.Language.get('quantity')}: {product.quantity}
				</p>
			)}
		</>
	);
}

const ContentsMap = {
	diagram: DiagramContent,
	external: ExternalContent,
	sku: SkuContent,
};

function StorefrontTooltipContent({
	accountId,
	cartId,
	channelGroupId,
	channelId,
	currencyCode,
	orderUUID,
	productBaseURL,
	selectedPin,
}) {
	const Renderer = ContentsMap[selectedPin.mappedProduct.type];

	return (
		<div className="diagram-storefront-tooltip">
			<Renderer
				accountId={accountId}
				cartId={cartId}
				channelGroupId={channelGroupId}
				channelId={channelId}
				currencyCode={currencyCode}
				orderUUID={orderUUID}
				product={selectedPin.mappedProduct}
				productBaseURL={productBaseURL}
			/>
		</div>
	);
}

export default StorefrontTooltipContent;
