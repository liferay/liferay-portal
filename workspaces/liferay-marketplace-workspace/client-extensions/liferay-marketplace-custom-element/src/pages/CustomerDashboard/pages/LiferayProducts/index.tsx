/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useNavigate} from 'react-router-dom';

import ListView from '../../../../components/ListView';
import OrderStatus from '../../../../components/OrderStatus';
import Page from '../../../../components/Page';
import SearchBuilder from '../../../../core/SearchBuilder';
import {
	OrderTypes,
	OrderWorkflowStatusCode,
	orderTypeDocumentationURL,
} from '../../../../enums/Order';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import {getSiteURL} from '../../../../utils/site';
import {safeJSONParse} from '../../../../utils/util';
import {useProductPurchaseOutletContext} from '../../../ProductPurchase/ProductPurchaseOutlet';
import {ProductPurchaseAIHubToken} from '../../../ProductPurchase/services/ProductPurchaseAIHubToken';
import {useCustomerDashboardOutletContext} from '../../CustomerDashboardOutlet';

const searchParams = new URLSearchParams({
	filter: SearchBuilder.in('orderTypeExternalReferenceCode', [
		OrderTypes.ADDONS,
		OrderTypes.AI_HUB,
		OrderTypes.CMP,
		OrderTypes.CMP_BETA,
		OrderTypes.DSR,
		OrderTypes.DXP,
	]),
	nestedFields: 'placedOrderItems',
	sort: 'createDate:desc',
});

const getViewDetailsPath = (placedOrder: PlacedOrder) => {
	return `${placedOrder.id}`;
};

const LiferayProductsListView = () => {
	const {selectedAccount} = useCustomerDashboardOutletContext();
	const {handlePurchase, product} = useProductPurchaseOutletContext();
	const navigate = useNavigate();

	const onClickBuyLiferayTokens = () => {
		const productPurchase = new ProductPurchaseAIHubToken(
			selectedAccount,
			product
		);

		handlePurchase(productPurchase);
	};

	return (
		<Page
			description={i18n.translate(
				'manage-your-products-purchased-from-the-marketplace'
			)}
			title={i18n.translate('products')}
		>
			<div className="customer-products">
				<ListView<PlacedOrder>
					emptyStateProps={{
						className:
							'border px-4 py-6 d-flex align-items-center flex-column justify-content-center',
						type: 'BLANK',
					}}
					id={`customer-products${selectedAccount?.id}`}
					initialContext={{
						pageSize: 20,
						paginationDeltaOptions: [20, 40, 80, 120],
					}}
					resource={`o/headless-commerce-delivery-order/v1.0/channels/${Liferay.CommerceContext.commerceChannelId}/accounts/${selectedAccount?.id}/placed-orders?${searchParams}`}
					tableProps={{
						actions: [
							{
								name: i18n.translate('view-details'),
								onClick: (placedOrder: PlacedOrder) =>
									navigate(getViewDetailsPath(placedOrder)),
							},
							{
								hidden: (row: PlacedOrder) =>
									!orderTypeDocumentationURL[
										row.orderTypeExternalReferenceCode as OrderTypes
									],
								name: i18n.translate('documentation'),
								onClick: (row: PlacedOrder) => {
									const url =
										orderTypeDocumentationURL[
											row.orderTypeExternalReferenceCode as OrderTypes
										];

									if (url) {
										window.open(
											url,
											'_blank',
											'noopener,noreferrer'
										);
									}
								},
							},
							{
								hidden: (row: PlacedOrder) =>
									row.orderTypeExternalReferenceCode !==
									OrderTypes.CMP,
								name: i18n.translate('create-license-key'),
								onClick: (placedOrder: PlacedOrder) =>
									navigate(getViewDetailsPath(placedOrder)),
							},
							{
								hidden: (row: PlacedOrder) =>
									![
										OrderTypes.AI_HUB,
										OrderTypes.CMP_BETA,
										OrderTypes.DSR,
									].includes(
										row.orderTypeExternalReferenceCode as OrderTypes
									),
								name: i18n.translate('share-your-feedback'),
								onClick: (row: PlacedOrder) =>
									Liferay.Util.navigate(
										`${getSiteURL()}/product-feedback?orderId=${row.id}`
									),
							},
							{
								hidden: (placedOrder: PlacedOrder) => {
									const {
										orderStatusInfo,
										orderTypeExternalReferenceCode,
									} = placedOrder;

									const isNotAIHub =
										orderTypeExternalReferenceCode !==
										OrderTypes.AI_HUB;

									const isNotActive =
										orderStatusInfo?.code !==
										OrderWorkflowStatusCode.COMPLETED;

									return isNotAIHub || isNotActive;
								},
								name: i18n.translate('buy-liferay-tokens'),
								onClick: onClickBuyLiferayTokens,
							},
						],
						columns: [
							{
								clickable: true,
								id: 'placedOrderItems',
								name: i18n.translate('name'),
								render: (placedOrderItems) => {
									const placedOrderItem =
										placedOrderItems[0] || [];

									const options = safeJSONParse<
										{skuOptionValueKey: string}[]
									>(placedOrderItem.options, []);

									const betaOption = options.find(
										(option) =>
											option.skuOptionValueKey === 'beta'
									);

									const privateBetaOption = options.find(
										(option) =>
											option.skuOptionValueKey ===
											'private-beta'
									);

									return (
										<div style={{width: 300}}>
											<div className="d-flex">
												<img
													alt="App Image"
													className="order-details-publisher-table-icon"
													src={
														placedOrderItem?.thumbnail
													}
												/>

												<div className="d-flex flex-column ml-1">
													<span className="align-items-center d-flex font-weight-semi-bold">
														{placedOrderItem?.name}

														{(betaOption ||
															privateBetaOption) && (
															<span className="beta-badge-label ml-2">
																{betaOption
																	? 'Beta'
																	: 'Private Beta'}
															</span>
														)}
													</span>

													<span className="text-black-50">
														By Liferay
													</span>
												</div>
											</div>
										</div>
									);
								},
								size: 'sm',
							},
							{
								clickable: true,
								id: 'author',
								name: i18n.translate('purchased-by'),
								render: (author, {createDate}) => (
									<div className="d-flex flex-column">
										<span className="dashboard-table-row-text">
											{author}
										</span>

										<span className="dashboard-table-row-purchased-date text-black-50">
											{new Date(
												createDate
											).toLocaleDateString('en-US', {
												day: 'numeric',
												month: 'short',
												year: 'numeric',
											})}
										</span>
									</div>
								),
							},
							{
								id: 'id',
								name: i18n.translate('order-id'),
							},
							{
								id: 'orderStatusInfo',
								name: 'Status',
								render: (_, item) => {
									return <OrderStatus placedOrder={item} />;
								},
							},
						],
						navigateTo: (placedOrder) =>
							getViewDetailsPath(placedOrder),
					}}
				/>
			</div>
		</Page>
	);
};

export default LiferayProductsListView;
