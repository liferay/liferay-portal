/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useNavigate} from 'react-router-dom';

import {DashboardEmptyTable} from '../../../components/DashboardTable/DashboardEmptyTable';
import OrderStatus from '../../../components/OrderStatus';
import Table from '../../../components/Table/Table';
import {Analytics} from '../../../core/Analytics';
import MarketplaceDeliveryOrder from '../../../entity/MarketplaceDeliveryOrder';
import {OrderTypes, orderTypeLabel} from '../../../enums/Order';
import {ProductImageFallbackCategories} from '../../../enums/Product';
import i18n from '../../../i18n';
import {getProductImageFallback} from '../../../utils/productUtils';

type AppsTableProps = {
	items: Order[];
};

const AppsTable: React.FC<AppsTableProps> = ({items}) => {
	const navigate = useNavigate();

	if (!items?.length) {
		return (
			<DashboardEmptyTable
				description1={i18n.translate(
					'purchase-and-install-new-apps-and-they-will-show-up-here'
				)}
				description2={i18n.translate('click-on-add-apps-to-start')}
				icon="grid"
				title={i18n.translate('no-apps-yet')}
			/>
		);
	}

	return (
		<Table
			columns={[
				{
					key: 'name',
					render: (name, {thumbnail}) => (
						<div style={{width: 200}}>
							<img
								alt="App Image"
								className="order-details-publisher-table-icon"
								src={
									thumbnail ||
									getProductImageFallback(
										ProductImageFallbackCategories.PRODUCT_IMAGE
									)
								}
							/>

							<span className="font-weight-semi-bold ml-2">
								{name}
							</span>
						</div>
					),
					title: i18n.translate('name'),
				},
				{
					key: 'author',
					render: (author, {createDate}) => {
						return (
							<div className="d-flex flex-column">
								<span className="dashboard-table-row-text">
									{author}
								</span>

								<span className="dashboard-table-row-purchased-date">
									{new Date(createDate).toLocaleDateString(
										'en-US',
										{
											day: 'numeric',
											month: 'short',
											year: 'numeric',
										}
									)}
								</span>
							</div>
						);
					},
					title: i18n.translate('purchased-by'),
				},
				{
					key: 'orderTypeExternalReferenceCode',
					render: (orderTypeExternalReferenceCode) => {
						return orderTypeLabel[
							orderTypeExternalReferenceCode as OrderTypes
						];
					},
					title: i18n.translate('app-type'),
				},
				{
					key: 'id',
					title: i18n.translate('order-id'),
				},
				{
					key: 'orderStatusInfo',
					render: (orderStatusInfo) => (
						<OrderStatus orderStatus={orderStatusInfo?.label}>
							{orderStatusInfo?.label}
						</OrderStatus>
					),
					title: i18n.translate('order-status'),
				},
				{
					align: 'center',
					key: 'status',
					render: (_, placedOrder) => {
						const {account, id, name, virtualURL} = placedOrder;

						const marketplaceDeliveryOrder =
							new MarketplaceDeliveryOrder(placedOrder);

						const isDownloadable =
							marketplaceDeliveryOrder.isDownloadable;

						const isFreeApp = marketplaceDeliveryOrder.isFreeApp;

						const metadata = {
							account,
							productName: name,
						};

						const isOrderStatusCompleted =
							marketplaceDeliveryOrder.isOrderStatusCompleted;

						return (
							<div onClick={(event) => event.stopPropagation()}>
								<ClayDropDown
									trigger={
										<ClayButtonWithIcon
											aria-label="Kebab Button"
											displayType={null}
											symbol="ellipsis-v"
											title="Kebab Button"
										/>
									}
								>
									<ClayDropDown.Item
										onClick={() => {
											navigate(`order/${id}`);
										}}
									>
										{i18n.translate('view-details')}
									</ClayDropDown.Item>

									<ClayDropDown.ItemList>
										{isDownloadable && !isFreeApp && (
											<>
												<ClayTooltipProvider>
													<ClayDropDown.Item
														data-tooltip-align="left"
														disabled={
															!isOrderStatusCompleted
														}
														onClick={() =>
															navigate(
																`order/${id}/create-license`
															)
														}
														title={
															!isOrderStatusCompleted
																? i18n.translate(
																		'the-order-must-be-completed-before-licensing-this-app.'
																	)
																: undefined
														}
													>
														{i18n.translate(
															'create-license-key'
														)}
													</ClayDropDown.Item>
												</ClayTooltipProvider>

												<ClayDropDown.Item
													disabled={isFreeApp}
													onClick={() => {
														navigate(
															`order/${id}/licenses`
														);
													}}
												>
													{i18n.translate(
														'manage-license-keys'
													)}
												</ClayDropDown.Item>
											</>
										)}

										{!isDownloadable && (
											<ClayDropDown.Item
												onClick={() => {
													navigate(
														`order/${id}/cloud-provisioning`
													);
												}}
											>
												{i18n.translate(
													'cloud-provisioning'
												)}
											</ClayDropDown.Item>
										)}

										{isDownloadable && (
											<ClayTooltipProvider>
												<ClayDropDown.Item
													data-tooltip-align="left"
													disabled={
														isFreeApp
															? false
															: !isOrderStatusCompleted
													}
													onClick={() => {
														navigate(
															`order/${id}/download`
														);

														if (!virtualURL) {
															Analytics.track(
																'VIRTUAL_URL_NOT_FOUND',
																metadata
															);

															return alert(
																'Download file not found'
															);
														}

														Analytics.track(
															'DOWNLOAD_APP',
															metadata
														);

														window.open(virtualURL);
													}}
													title={
														!isOrderStatusCompleted
															? i18n.translate(
																	'this-order-must-be-completed-before-downloading-this-app.'
																)
															: undefined
													}
												>
													{i18n.translate(
														'download-app'
													)}
												</ClayDropDown.Item>
											</ClayTooltipProvider>
										)}
									</ClayDropDown.ItemList>
								</ClayDropDown>
							</div>
						);
					},
				},
			]}
			onClickRow={({id}) => navigate(`order/${id}`)}
			rows={items}
		/>
	);
};

export default AppsTable;
