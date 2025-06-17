/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {
	AppsPermissions,
	Marketplace,
	MarketplaceProduct,
	MarketplaceRest,
	MarketplaceView,
	Product,
	useMarketplaceContext,
} from '@liferay/marketplace-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback} from 'react';

import importZipFile from '../import/importZipFile';
import {InstallFragmentModalBody} from './InstallFragmentModal';

async function fetchFragmentBlob(
	marketplaceRest: MarketplaceRest,
	url: string
) {
	const response = await marketplaceRest.fetchMarketplace<Response>(url, {
		earlyReturn: true,
	});

	return response.blob();
}

async function getProductVirtualEntryBlob(
	marketplaceRest: MarketplaceRest,
	product: Product
): Promise<Blob> {
	const cart = await marketplaceRest.createCart(product as Product, {
		orderTypeExternalReferenceCode: 'LOW_CODE_CONFIGURATION',
	});

	await marketplaceRest.checkoutCart(cart);

	const placedOrder = await marketplaceRest.getPlacedOrder(
		cart.id,
		new URLSearchParams({nestedFields: 'placedOrderItems'})
	);

	const placedOrderItem = placedOrder.placedOrderItems.find(
		(placedOrderItem) => placedOrderItem?.virtualItems?.length
	);

	const virtualItem = placedOrderItem?.virtualItems?.find(
		(virtualItem) => virtualItem.url
	);

	if (!virtualItem) {
		throw new Error('Product has no virtual entries.');
	}

	return fetchFragmentBlob(marketplaceRest, virtualItem.url);
}

interface MarketplaceViewsProps {
	fragmentPortletNamespace: string;
	fragmentsImportURL: string;
	hideBackButton?: boolean;
}

export default function MarketplaceViews({
	fragmentPortletNamespace,
	fragmentsImportURL,
	hideBackButton,
}: MarketplaceViewsProps) {
	const {
		marketplaceRest,
		modal: {onOpenChange},
		permissions,
		product,
		setProduct,
		setView,
		view,
	} = useMarketplaceContext();

	const handleImportFile = useCallback(
		async (file: File) => {
			try {
				await importZipFile({
					file,
					handleResponse: ({importResults}, file) => {
						if (!Object.keys(importResults).length) {
							openToast({
								message: sub(
									Liferay.Language.get(
										'no-new-items-were-imported'
									),
									file.name
								),
								type: 'info',
							});
						}
						else {
							openToast({
								message: Liferay.Language.get(
									'your-request-completed-successfully'
								),
								title: Liferay.Language.get('success'),
								type: 'success',
							});

							window.location.reload();
						}
					},
					importURL: fragmentsImportURL,
					marketplace: true,
					overwriteStrategy: 'keep_both',
					portletNamespace: fragmentPortletNamespace,
				});
			}
			catch (error) {
				if (process.env.NODE_ENV === 'development') {
					console.error('Import failed:', error);
				}
			}
		},
		[fragmentsImportURL, fragmentPortletNamespace]
	);

	const handleInstallProduct = useCallback(
		async (product: Product) => {
			setView(MarketplaceView.PURCHASE);
			setProduct(product);

			try {
				const blob = await getProductVirtualEntryBlob(
					marketplaceRest,
					product
				);

				if (!blob) {
					throw new Error('Product has no response blob.');
				}

				const file = new File(
					[blob],
					`${product.name.replace(' ', '-').toLowerCase()}.zip`,
					{type: 'application/zip'}
				);

				await handleImportFile(file);
			}
			catch (error) {
				if (process.env.NODE_ENV === 'development') {
					console.error('Installation failed:', error);
				}
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					title: Liferay.Language.get('danger'),
					type: 'danger',
				});
			}
			finally {
				onOpenChange(false);
			}
		},
		[marketplaceRest, setProduct, setView, handleImportFile, onOpenChange]
	);

	return (
		<>
			{view === MarketplaceView.PRODUCTS && (
				<Marketplace.Products
					onClickProduct={(product) => {
						setProduct(product);
						setView(MarketplaceView.STOREFRONT);
					}}
				>
					{(product) => (
						<MarketplaceInstallButton
							className="w-100"
							handleInstallProduct={handleInstallProduct}
							permissions={permissions}
							product={product}
						/>
					)}
				</Marketplace.Products>
			)}

			{view === MarketplaceView.STOREFRONT && (
				<Marketplace.Storefront
					onClickBack={
						hideBackButton
							? undefined
							: () => setView(MarketplaceView.PRODUCTS)
					}
					primaryButton={
						<MarketplaceInstallButton
							className="ml-auto mt-3 rounded"
							handleInstallProduct={handleInstallProduct}
							permissions={permissions}
							product={product}
						/>
					}
				/>
			)}

			{view === MarketplaceView.PURCHASE && (
				<div className="p-4">
					<InstallFragmentModalBody />
				</div>
			)}
		</>
	);
}

interface MarketplaceInstallButtonProps {
	className: string;
	handleInstallProduct: (product: Product) => void;
	permissions?: AppsPermissions & {
		manageFragmentsEntries?: boolean;
	};
	product: Product;
}

function MarketplaceInstallButton({
	className,
	handleInstallProduct,
	permissions,
	product,
}: MarketplaceInstallButtonProps) {
	const marketplaceProduct = new MarketplaceProduct(product);

	if (
		permissions &&
		marketplaceProduct.hasPermissionToInstall(permissions) &&
		permissions.manageFragmentsEntries
	) {
		return (
			<ClayButton
				className={className}
				onClick={() => handleInstallProduct(product)}
			>
				{Liferay.Language.get('install')}
			</ClayButton>
		);
	}

	return null;
}
