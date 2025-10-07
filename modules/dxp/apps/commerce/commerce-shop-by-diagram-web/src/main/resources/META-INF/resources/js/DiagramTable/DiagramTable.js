/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayTable from '@clayui/table';
import {
	AddToCartButtonComponent,
	InfiniteScrollerComponent,
	useCommerceAccount,
	useCommerceCart,
} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {DIAGRAM_EVENTS, DIAGRAM_TABLE_EVENTS} from '../utilities/constants';
import {deleteMappedProduct, getMappedProducts} from '../utilities/data';
import {
	formatMappedProductForTable,
	formatProductOptions,
} from '../utilities/index';
import ManagementBar from './ManagementBar';
import MappedProductRow from './MappedProductRow';
import TableHead from './TableHead';

const PAGE_SIZE = 15;

function formatCpInstances(cpInstances, quantities) {
	return cpInstances.reduce((selectedCpInstances, cpInstance) => {
		if (!cpInstance.selected) {
			return selectedCpInstances;
		}

		const skuOptions = formatProductOptions(
			cpInstance.skuOptions,
			cpInstance.productOptions
		);

		return [
			...selectedCpInstances,
			{
				inCart: false,
				quantity:
					quantities[cpInstance.skuId] || cpInstance.initialQuantity,
				skuId: cpInstance.skuId,
				skuOptions,
				validQuantity: true,
			},
		];
	}, []);
}

function DiagramTable({
	cartId,
	channelGroupId,
	channelId,
	commerceAccountId: initialAccountId,
	commerceCurrencyCode,
	guestOrderEnabled,
	isAdmin,
	orderUUID,
	productId,
}) {
	const [currentPage, setCurrentPage] = useState(1);
	const [lastPage, setLastPage] = useState(null);
	const [loaderActive, setLoaderActive] = useState(true);
	const [mappedProducts, setMappedProducts] = useState(null);
	const [newQuantities, setNewQuantities] = useState({});
	const [query, setQuery] = useState('');
	const [refreshTrigger, setRefreshTrigger] = useState(false);
	const commerceAccount = useCommerceAccount({id: initialAccountId});
	const commerceCart = useCommerceCart({
		guestOrderEnabled,
		initialCart: {id: cartId},
	});
	const wrapperRef = useRef();

	const handleDiagramUpdated = useCallback(
		({diagramProductId}) => {
			if (diagramProductId === productId) {
				setRefreshTrigger((trigger) => !trigger);

				setCurrentPage(1);
			}
		},
		[productId]
	);

	useEffect(() => {
		Liferay.on(DIAGRAM_EVENTS.DIAGRAM_UPDATED, handleDiagramUpdated);

		return () => {
			Liferay.detach(
				DIAGRAM_EVENTS.DIAGRAM_UPDATED,
				handleDiagramUpdated
			);
		};
	}, [handleDiagramUpdated]);

	useEffect(() => {
		getMappedProducts(
			productId,
			!isAdmin && channelId,
			query,
			currentPage,
			PAGE_SIZE,
			commerceAccount.id
		).then((data) => {
			setLoaderActive(false);

			const fetchedProducts = formatMappedProductForTable(
				data.items,
				isAdmin
			);

			setMappedProducts((mappedProducts) =>
				mappedProducts && currentPage > 1
					? [...mappedProducts, ...fetchedProducts]
					: fetchedProducts
			);

			setLastPage(data.lastPage);
		});
	}, [
		channelId,
		currentPage,
		isAdmin,
		productId,
		query,
		refreshTrigger,
		commerceAccount,
	]);

	function handleTitleClicked(product) {
		Liferay.fire(DIAGRAM_TABLE_EVENTS.SELECT_PIN, {
			diagramProductId: productId,
			product,
		});
	}

	function handleMouseEnter(product) {
		Liferay.fire(DIAGRAM_TABLE_EVENTS.HIGHLIGHT_PIN, {
			diagramProductId: productId,
			sequence: product.sequence,
		});
	}

	function handleMouseLeave(product) {
		Liferay.fire(DIAGRAM_TABLE_EVENTS.REMOVE_PIN_HIGHLIGHT, {
			diagramProductId: productId,
			sequence: product.sequence,
		});
	}

	const handleMappedProductDelete = (mappedProductId) => {
		deleteMappedProduct(mappedProductId).then(() => {
			setMappedProducts((mappedProducts) =>
				mappedProducts.filter(
					(mappedProduct) => mappedProduct.id !== mappedProductId
				)
			);

			Liferay.fire(DIAGRAM_TABLE_EVENTS.TABLE_UPDATED, {
				diagramProductId: productId,
			});
		});
	};

	let content = <div className="full-height-content" />;

	if (loaderActive) {
		content = (
			<div className="full-height-content">
				<ClayLoadingIndicator />
			</div>
		);
	}

	if (!loaderActive && mappedProducts && !!mappedProducts.length) {
		content = (
			<InfiniteScrollerComponent
				onBottomTouched={() => setCurrentPage(currentPage + 1)}
				scrollCompleted={currentPage >= lastPage}
			>
				<ClayTable borderless>
					<TableHead
						isAdmin={isAdmin}
						mappedProducts={mappedProducts}
						setMappedProducts={setMappedProducts}
					/>

					<ClayTable.Body>
						{Boolean(mappedProducts?.length) &&
							mappedProducts.map((product) => (
								<MappedProductRow
									handleMouseEnter={handleMouseEnter}
									handleMouseLeave={handleMouseLeave}
									handleTitleClicked={handleTitleClicked}
									isAdmin={isAdmin}
									key={product.id}
									onDelete={handleMappedProductDelete}
									product={product}
									quantity={
										newQuantities[product.skuId] ??
										product.initialQuantity
									}
									setMappedProducts={setMappedProducts}
									setNewQuantity={(newQuantity) => {
										setNewQuantities({
											...newQuantities,
											[product.skuId]: newQuantity,
										});
									}}
								/>
							))}
					</ClayTable.Body>
				</ClayTable>
			</InfiniteScrollerComponent>
		);
	}

	const selectedProductsCounter =
		!isAdmin && mappedProducts
			? mappedProducts.reduce(
					(counter, product) =>
						product.selected ? counter + 1 : counter,
					0
				)
			: 0;

	return (
		<div className="shop-by-diagram-table" ref={wrapperRef}>
			<ManagementBar
				updateQuery={(query) => {
					setCurrentPage(1);
					setLoaderActive(true);
					setQuery(query);
					setMappedProducts(null);
				}}
			/>

			{mappedProducts && !mappedProducts.length && !loaderActive && (
				<ClayEmptyState
					className="full-height-content"
					description={Liferay.Language.get(
						'sorry,-no-results-were-found'
					)}
					title={Liferay.Language.get('there-are-no-results')}
				/>
			)}

			{content}

			{!isAdmin && (
				<AddToCartButtonComponent
					accountId={commerceAccount.id}
					cartId={commerceCart.id}
					cartUUID={orderUUID}
					channel={{
						currencyCode: commerceCurrencyCode,
						groupId: channelGroupId,
						id: channelId,
					}}
					cpInstances={formatCpInstances(
						mappedProducts || [],
						newQuantities
					)}
					disabled={!commerceAccount?.id || !selectedProductsCounter}
					hideIcon={true}
					onAdd={() => {
						const message =
							selectedProductsCounter === 1
								? Liferay.Language.get(
										'the-product-was-successfully-added-to-the-cart'
									)
								: sub(
										Liferay.Language.get(
											'x-products-were-successfully-added-to-the-cart'
										),
										selectedProductsCounter
									);

						openToast({
							message,
							type: 'success',
						});
					}}
					settings={{
						alignment: 'full-width',
						buttonText: Liferay.Language.get(
							'add-selected-products-to-the-order'
						),
					}}
				/>
			)}
		</div>
	);
}

DiagramTable.propTypes = {
	cartId: PropTypes.string,
	channelGroupId: PropTypes.string,
	channelId: PropTypes.string,
	commerceAccountId: PropTypes.string,
	commerceCurrencyCode: PropTypes.string,
	isAdmin: PropTypes.bool,
	orderUUID: PropTypes.string,
	productId: PropTypes.string.isRequired,
};

export default DiagramTable;
