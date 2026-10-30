/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayManagementToolbar, {
	ClayResultsBar,
} from '@clayui/management-toolbar';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import ClayTable from '@clayui/table';
import React, {useCallback, useEffect, useState} from 'react';

// @ts-ignore

import ServiceProvider from '../../ServiceProvider/index';
import {
	CART_UPDATED,
	CURRENT_ORDER_UPDATED,
	ORDER_INFORMATION_ALTERED,

	// @ts-ignore

} from '../../utilities/eventsDefinitions';

import './Multishipping.scss';

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {openConfirmModal} from 'frontend-js-components-web';

import AddDeliveryGroupButton from './AddDeliveryGroupButton';
import DeliveryGroupHeaderCell from './DeliveryGroupHeaderCell';
import {showError} from './ErrorMessage';
import OrderItemRow, {
	copyColumnOrderItem,
	removeOrderItem,
	resetOrderItem,
	splitOrderItem,
} from './OrderItemRow';
import {
	IAPIResponseError,
	IAddressSubtypeConfiguration,
	IDeliveryGroup,
	IOrderItem,
	IOrderItemAPIResponse,
	IOrderItemDeliveryGroup,
} from './Types';

const MAX_DELIVERY_GROUPS = 20;

interface IMultishippingProps {
	accountId: number;
	addressSubtypeConfiguration?: IAddressSubtypeConfiguration;
	defaultAddressId?: number;
	defaultAddressName?: string;
	namespace?: string;
	orderId: number;
	readonly?: boolean;
	spritemap?: string;
}

export function formatCartItem(
	deliveryGroup: IDeliveryGroup,
	orderItem: IOrderItem,
	quantity: number = 1
) {
	return {
		deliveryGroupName: deliveryGroup.name,
		options: orderItem.options,
		quantity: Number(
			Number(quantity).toFixed(orderItem.skuUnitOfMeasure?.precision || 0)
		),
		replacedSkuId: orderItem.replacedSkuId || 0,
		requestedDeliveryDate: deliveryGroup.deliveryDate,
		shippingAddressId: deliveryGroup.addressId,
		skuId: orderItem.skuId,
		skuUnitOfMeasure: orderItem.skuUnitOfMeasure,
	};
}

const createRequestData = (
	deliveryGroups: Array<IDeliveryGroup>,
	items: Array<IOrderItem>
) => {
	const data: Array<IOrderItem> = [];

	items.forEach((orderItem) => {
		if (deliveryGroups.length) {
			for (const [deliveryGroupId, orderItemConf] of Object.entries(
				orderItem.deliveryGroups || {}
			)) {
				let deliveryGroup = null;

				if (0 === Number(deliveryGroupId)) {
					deliveryGroup = deliveryGroups[0];
				}
				else {
					deliveryGroup = deliveryGroups.find((item) => {
						return item.id === Number(deliveryGroupId);
					});
				}

				if (deliveryGroup && orderItemConf.quantity > 0) {
					data.push({
						deliveryGroupName: deliveryGroup.name,
						id: orderItemConf.orderItemId,
						options: orderItemConf.options,
						quantity: orderItemConf.quantity,
						replacedSkuId: orderItemConf.replacedSkuId,
						requestedDeliveryDate: deliveryGroup.deliveryDate,
						shippingAddressId: deliveryGroup.addressId,
						skuId: orderItemConf.skuId,
						skuUnitOfMeasure: orderItemConf.skuUnitOfMeasure,
					});
				}
			}
		}
		else {
			if (orderItem.quantity > 0) {
				data.push(
					formatCartItem(
						{
							addressId: 0,
							deliveryDate: '',
							id: 0,
							name: '',
						},
						orderItem,
						orderItem.quantity
					) as IOrderItem
				);
			}
		}
	});

	return data;
};

const updateCartById = async function (
	orderId: number,
	data: Array<IOrderItem>,
	callback: (response: IOrderItemAPIResponse) => void
) {
	return await ServiceProvider.DeliveryCartAPI('v1')
		.updateCartById(
			orderId,
			{
				cartItems: data,
			},
			{pageSize: -1}
		)
		.then(async (response: IOrderItemAPIResponse) => {
			Liferay.fire(CURRENT_ORDER_UPDATED, {
				order: response,
				updatedFromCart: false,
			});
			Liferay.fire(ORDER_INFORMATION_ALTERED, {
				order: response,
			});

			callback(response);
		});
};

const Multishipping = ({
	accountId,
	addressSubtypeConfiguration,
	defaultAddressId = 0,
	defaultAddressName = '',
	namespace = '',
	orderId,
	readonly = false,
	spritemap,
}: IMultishippingProps) => {
	const [deliveryGroups, setDeliveryGroups] = useState<Array<IDeliveryGroup>>(
		[]
	);
	const [checkedOrderItemIds, setCheckedOrderItemIds] = useState<
		Array<number>
	>([]);
	const [filterFormattedOrderItems, setFilteredFormattedOrderItems] =
		useState<Array<IOrderItem>>([]);
	const [formattedOrderItems, setFormattedOrderItems] = useState<
		Array<IOrderItem>
	>([]);
	const [loading, setLoading] = useState(true);
	const [pagination, setPagination] = useState({
		currentPage: 1,
		pageSize: 20,
	});
	const [saving, setSaving] = useState(false);
	const [search, setSearch] = useState('');

	const prepareData = useCallback(
		async (items: Array<IOrderItem>) => {
			const formattedDeliveryGroups: Array<IDeliveryGroup> = [];
			const formattedItems: Array<IOrderItem> = [];

			items.forEach((orderItem) => {
				let deliveryGroup = formattedDeliveryGroups.find((item) => {
					return (
						item.name === (orderItem.deliveryGroupName || '') &&
						item.deliveryDate ===
							(orderItem.requestedDeliveryDate || '') &&
						item.addressId === orderItem.shippingAddressId
					);
				});

				if (!deliveryGroup && orderItem.shippingAddressId > 0) {
					deliveryGroup = {
						addressId: orderItem.shippingAddressId,
						deliveryDate: orderItem.requestedDeliveryDate || '',
						id: Math.floor(Math.random() * 100000000),
						name: orderItem.deliveryGroupName || '',
					};

					formattedDeliveryGroups.push(deliveryGroup);
				}

				let formattedItem = formattedItems.find((item) => {
					return (
						item.skuId === orderItem.skuId &&
						item.skuUnitOfMeasure?.key ===
							orderItem.skuUnitOfMeasure?.key &&
						item.options === orderItem.options &&
						orderItem.deliveryGroupName !== ''
					);
				});

				if (
					!formattedItem ||
					(formattedItem &&
						deliveryGroup &&
						(formattedItem.deliveryGroups || {})[deliveryGroup.id])
				) {
					formattedItem = {
						...orderItem,
						deliveryGroups: {},
						quantity: 0,
					};

					formattedItems.push(formattedItem);
				}

				if (deliveryGroup) {
					formattedItem.deliveryGroups = {
						...formattedItem.deliveryGroups,
						[deliveryGroup.id]: {
							options: orderItem.options,
							orderItemId: orderItem.id,
							originalQuantity: orderItem.quantity,
							quantity: orderItem.quantity,
							replacedSkuId: orderItem.replacedSkuId || 0,
							skuId: orderItem.skuId,
							skuUnitOfMeasure: orderItem.skuUnitOfMeasure,
						} as IOrderItemDeliveryGroup,
					};
				}
				else {
					formattedItem.deliveryGroups = {
						[0]: {
							options: orderItem.options,
							orderItemId: orderItem.id,
							originalQuantity: orderItem.quantity,
							quantity: orderItem.quantity,
							replacedSkuId: orderItem.replacedSkuId || 0,
							skuId: orderItem.skuId,
							skuUnitOfMeasure: orderItem.skuUnitOfMeasure,
						},
					};
				}

				formattedItem.quantity =
					formattedItem.quantity + orderItem.quantity;
			});

			if (formattedDeliveryGroups.length) {
				formattedDeliveryGroups.sort((item1, item2) => {
					return item1.name.localeCompare(item2.name);
				});

				if (
					formattedItems.find((item) => item.deliveryGroupName === '')
				) {
					try {
						setSaving(true);

						await updateCartById(
							orderId,
							createRequestData(
								formattedDeliveryGroups,
								formattedItems
							),
							async (response) => {
								await prepareData(response.cartItems);
							}
						);

						return;
					}
					catch (error) {
						showError(error as IAPIResponseError);
					}
					finally {
						setSaving(false);
					}
				}
			}
			else if (defaultAddressId) {
				try {
					setSaving(true);

					await updateCartById(
						orderId,
						formattedItems.map((item) => {
							return formatCartItem(
								{
									addressId: defaultAddressId,
									deliveryDate: '',
									id: 0,
									name: defaultAddressName || 'Default',
								},
								item,
								item.quantity
							);
						}) as Array<IOrderItem>,
						async (response) => {
							await prepareData(response.cartItems);
						}
					);

					return;
				}
				catch (error) {
					showError(error as IAPIResponseError);
				}
				finally {
					setSaving(false);
				}
			}

			setDeliveryGroups(formattedDeliveryGroups);
			setFormattedOrderItems(
				formattedItems.sort(
					(item1, item2) =>
						item1.sku?.localeCompare(item2.sku || '') ||
						item1.id - item2.id
				)
			);
		},
		[defaultAddressId, defaultAddressName, orderId]
	);

	const loadOrderItemData = useCallback(
		({updatedFromCart = false}: {updatedFromCart: boolean}) => {
			if (!updatedFromCart) {
				return;
			}

			setLoading(true);

			ServiceProvider.DeliveryCartAPI('v1')
				.getItemsByCartId(orderId, {pageSize: -1})
				.then(async (response: IOrderItemAPIResponse) => {
					await prepareData(response.items);
				})
				.catch((error: IAPIResponseError) => {
					showError(error);
				})
				.finally(() => {
					setLoading(false);
				});
		},
		[orderId, prepareData]
	);

	const updateFullCart = useCallback(
		async (data: any) => {
			setSaving(true);

			await updateCartById(orderId, data, async (response) => {
				await prepareData(response.cartItems);
			}).finally(() => {
				setSaving(false);
			});
		},
		[orderId, prepareData]
	);

	const handleBulkAction = useCallback(
		async (action: string) => {
			const originalFormattedOrderItems = JSON.parse(
				JSON.stringify(formattedOrderItems)
			);

			try {
				let currentFormattedOrderItems = JSON.parse(
					JSON.stringify(formattedOrderItems)
				);

				for (const orderItemId of checkedOrderItemIds) {
					const orderItem = currentFormattedOrderItems.find(
						(orderItem: IOrderItem) => orderItem.id === orderItemId
					);

					if (!orderItem) {
						continue;
					}

					let currentOrderItem: IOrderItem | null = null;

					if (action === 'remove') {
						currentOrderItem = removeOrderItem(orderItem);
					}
					else if (action === 'reset') {
						currentOrderItem = resetOrderItem(
							deliveryGroups[0],
							orderItem
						);
					}
					else if (action === 'split') {
						currentOrderItem = splitOrderItem(
							deliveryGroups,
							orderItem
						);
					}
					else {
						currentOrderItem = copyColumnOrderItem(
							deliveryGroups,
							orderItem
						);
					}

					currentFormattedOrderItems = currentFormattedOrderItems.map(
						(item: IOrderItem) => {
							if (item.id === orderItemId) {
								return currentOrderItem;
							}

							return item;
						}
					);
				}

				await updateFullCart(
					createRequestData(
						deliveryGroups,
						currentFormattedOrderItems
					)
				);

				setCheckedOrderItemIds([]);
			}
			catch (error) {
				setFormattedOrderItems(originalFormattedOrderItems);

				showError({
					detail: Liferay.Language.get(
						'the-item-s-quantity-is-not-valid-for-the-number-of-delivery-groups'
					),
				});
			}
		},
		[
			checkedOrderItemIds,
			deliveryGroups,
			formattedOrderItems,
			updateFullCart,
		]
	);

	const handleDeleteDeliveryGroup = useCallback(
		async (deliveryGroup: any) => {
			try {
				if (deliveryGroups.length === 1) {
					await updateFullCart(
						formattedOrderItems.map((item) => {
							return formatCartItem(
								{
									addressId: 0,
									deliveryDate: '',
									id: 0,
									name: '',
								},
								item,
								item.quantity
							);
						})
					);
				}
				else {
					const updatedDeliveryGroups = deliveryGroups.filter(
						(item) => {
							return item.id !== deliveryGroup.id;
						}
					);

					await updateFullCart(
						createRequestData(
							updatedDeliveryGroups,
							formattedOrderItems
						)
					);
				}
			}
			catch (error) {
				showError(error as IAPIResponseError);
			}
		},
		[deliveryGroups, formattedOrderItems, updateFullCart]
	);

	const handlePaginationDeltaChange = useCallback((value: any) => {
		setPagination((prevState) => ({
			...prevState,
			pageSize: value,
		}));
	}, []);

	const handlePaginationPageChange = useCallback((value: any) => {
		setPagination((prevState) => ({
			...prevState,
			currentPage: value,
		}));
	}, []);

	const handleRowSelection = useCallback(async (orderItemId: number) => {
		setCheckedOrderItemIds((prevState) => {
			const currentCheckedOrderItemIds = [...prevState];

			if (currentCheckedOrderItemIds.includes(orderItemId)) {
				currentCheckedOrderItemIds.splice(
					currentCheckedOrderItemIds.indexOf(orderItemId),
					1
				);
			}
			else {
				currentCheckedOrderItemIds.push(orderItemId);
			}

			return currentCheckedOrderItemIds;
		});
	}, []);

	const handleRowUpdate = useCallback(
		async (orderItem: IOrderItem, saveFullOrder: boolean = false) => {
			const originalFormattedOrderItems = formattedOrderItems.map(
				(item) => {
					return {...item};
				}
			);
			const currentFormattedOrderItems = formattedOrderItems.map(
				(item) => {
					if (item.id === orderItem.id) {
						return orderItem;
					}

					return item;
				}
			);

			setFormattedOrderItems(currentFormattedOrderItems);

			if (saveFullOrder) {
				try {
					await updateFullCart(
						createRequestData(
							deliveryGroups,
							currentFormattedOrderItems
						)
					);
				}
				catch (error) {
					setFormattedOrderItems(originalFormattedOrderItems);

					showError(error as IAPIResponseError);
				}
			}
		},
		[deliveryGroups, formattedOrderItems, updateFullCart]
	);

	const handleSubmitDeliveryGroup = useCallback(
		async (deliveryGroup: IDeliveryGroup) => {
			const deliveryGroupsState = [...deliveryGroups];

			const index = deliveryGroupsState.findIndex(
				(item: IDeliveryGroup) => item.id === deliveryGroup.id
			);

			if (index >= 0) {
				deliveryGroupsState[index] = deliveryGroup;

				try {
					await updateFullCart(
						createRequestData(
							deliveryGroupsState,
							formattedOrderItems
						)
					);
				}
				catch (error) {
					showError(error as IAPIResponseError);
				}
			}
			else {
				deliveryGroup.id = new Date().getTime();
				deliveryGroupsState.push(deliveryGroup);

				setDeliveryGroups(deliveryGroupsState);

				if (deliveryGroupsState.length === 1) {
					try {
						await updateFullCart(
							formattedOrderItems.map((item) => {
								return formatCartItem(
									deliveryGroup,
									item,
									item.quantity
								);
							})
						);
					}
					catch (error) {
						showError(error as IAPIResponseError);

						setDeliveryGroups([]);

						return;
					}
				}

				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
				});
			}
		},
		[deliveryGroups, formattedOrderItems, updateFullCart]
	);

	useEffect(() => {
		loadOrderItemData({updatedFromCart: true});

		Liferay.on(CART_UPDATED, loadOrderItemData);

		return () => {
			Liferay.detach(CART_UPDATED, loadOrderItemData as any);
		};
	}, [loadOrderItemData]);

	useEffect(() => {
		if (search) {
			setFilteredFormattedOrderItems(
				formattedOrderItems.filter((item) => {
					return (
						(item.name || '')
							.toLowerCase()
							.indexOf(search.toLowerCase()) >= 0 ||
						(item.sku || '')
							.toLowerCase()
							?.indexOf(search.toLowerCase()) >= 0
					);
				})
			);
		}
		else {
			setFilteredFormattedOrderItems(formattedOrderItems);
		}
	}, [formattedOrderItems, search]);

	const managementBar = (
		<div className="management-bar-wrapper">
			<>
				{!checkedOrderItemIds.length && (
					<ClayManagementToolbar>
						{!readonly && (
							<ClayManagementToolbar.ItemList>
								<ClayManagementToolbar.Item>
									<ClayCheckbox
										aria-label={
											checkedOrderItemIds.length
												? Liferay.Language.get(
														'unselect-all'
													)
												: Liferay.Language.get(
														'select-all'
													)
										}
										checked={!!checkedOrderItemIds.length}
										data-qa-id="selectAllCheckbox"
										disabled={loading || readonly || saving}
										onChange={({target}) => {
											setCheckedOrderItemIds(
												target.checked
													? filterFormattedOrderItems.map(
															(orderItem) =>
																orderItem.id
														)
													: []
											);
										}}
									/>
								</ClayManagementToolbar.Item>
							</ClayManagementToolbar.ItemList>
						)}

						<ClayManagementToolbar.Search>
							<ClayInput.Group>
								<ClayInput.GroupItem>
									<ClayInput
										aria-label="search"
										className="form-control"
										disabled={loading}
										onChange={({target: {value}}) => {
											setSearch(value);
										}}
										type="text"
										value={search}
									/>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</ClayManagementToolbar.Search>

						{!readonly && (
							<ClayManagementToolbar.ItemList>
								<ClayManagementToolbar.Item>
									<AddDeliveryGroupButton
										accountId={accountId}
										addressSubtypeConfiguration={
											addressSubtypeConfiguration
										}
										disabled={
											loading ||
											readonly ||
											saving ||
											deliveryGroups.length >=
												MAX_DELIVERY_GROUPS ||
											formattedOrderItems.length < 1
										}
										handleSubmit={handleSubmitDeliveryGroup}
										hasManageAddressesPermission={true}
										namespace={namespace}
										spritemap={spritemap}
									/>
								</ClayManagementToolbar.Item>
							</ClayManagementToolbar.ItemList>
						)}
					</ClayManagementToolbar>
				)}

				{!!checkedOrderItemIds.length && (
					<ClayResultsBar>
						<ClayResultsBar.Item className="justify-content-center">
							<ClayCheckbox
								aria-label={
									checkedOrderItemIds.length
										? Liferay.Language.get('unselect-all')
										: Liferay.Language.get('select-all')
								}
								checked={!!checkedOrderItemIds.length}
								data-qa-id="selectAllCheckbox"
								onChange={({target}) => {
									setCheckedOrderItemIds(
										target.checked
											? filterFormattedOrderItems.map(
													(orderItem) => orderItem.id
												)
											: []
									);
								}}
							/>
						</ClayResultsBar.Item>

						<ClayResultsBar.Item>
							<span className="component-text text-truncate-inline">
								<span
									className="text-truncate"
									data-qa-id="selectionStats"
								>
									{`${checkedOrderItemIds.length} ${Liferay.Language.get('of')} ${filterFormattedOrderItems.length}`}
								</span>
							</span>
						</ClayResultsBar.Item>

						<ClayResultsBar.Item expand>
							<ClayButton
								className="tbar-link"
								data-qa-id="selectAllButton"
								displayType="link"
								onClick={() => {
									setCheckedOrderItemIds(
										filterFormattedOrderItems.map(
											(orderItem) => orderItem.id
										)
									);
								}}
							>
								{Liferay.Language.get('select-all')}
							</ClayButton>
						</ClayResultsBar.Item>

						<ClayResultsBar.Item>
							<ClayButton
								borderless
								className="tbar-link"
								data-qa-id="bulkSplitAction"
								disabled={!deliveryGroups.length}
								displayType="secondary"
								onClick={async () => {
									openConfirmModal({
										message: Liferay.Language.get(
											'if-the-total-quantity-cannot-be-equally-distributed,-any-remaining-units-will-be-allocated-to-the-primary-delivery-group'
										),
										onConfirm: async (isConfirmed) => {
											if (isConfirmed) {
												await handleBulkAction('split');
											}
										},
									});
								}}
							>
								<span className="inline-item inline-item-before">
									<ClayIcon
										spritemap={spritemap}
										symbol="arrow-split"
									/>
								</span>

								{Liferay.Language.get('split-quantity-evenly')}
							</ClayButton>

							<ClayButton
								borderless
								className="tbar-link"
								data-qa-id="bulkResetAction"
								disabled={!deliveryGroups.length}
								displayType="secondary"
								onClick={async () => {
									openConfirmModal({
										message: Liferay.Language.get(
											'by-resetting-the-rows,-all-columns-will-be-set-to-zero,-except-the-first-one'
										),
										onConfirm: async (isConfirmed) => {
											if (isConfirmed) {
												await handleBulkAction('reset');
											}
										},
									});
								}}
							>
								<span className="inline-item inline-item-before">
									<ClayIcon
										spritemap={spritemap}
										symbol="reload"
									/>
								</span>

								{Liferay.Language.get('reset-rows')}
							</ClayButton>

							<ClayButton
								borderless
								className="tbar-link"
								data-qa-id="bulkCopyAction"
								disabled={!deliveryGroups.length}
								displayType="secondary"
								onClick={async () => {
									await handleBulkAction('copy');
								}}
							>
								<span className="inline-item inline-item-before">
									<ClayIcon
										spritemap={spritemap}
										symbol="copy"
									/>
								</span>

								{Liferay.Language.get('copy-column-1-to-all')}
							</ClayButton>

							<ClayButton
								borderless
								className="tbar-link"
								data-qa-id="bulkRemoveAction"
								displayType="secondary"
								onClick={async () => {
									openConfirmModal({
										message: Liferay.Language.get(
											'by-removing-the-items,-they-will-disappear-from-the-list-of-ordered-items'
										),
										onConfirm: async (isConfirmed) => {
											if (isConfirmed) {
												await handleBulkAction(
													'remove'
												);
											}
										},
									});
								}}
							>
								<span className="inline-item inline-item-before">
									<ClayIcon
										spritemap={spritemap}
										symbol="times-circle"
									/>
								</span>

								{Liferay.Language.get('remove-items')}
							</ClayButton>
						</ClayResultsBar.Item>
					</ClayResultsBar>
				)}
			</>
		</div>
	);

	const view = (
		<div className="data-set-content-wrapper">
			{formattedOrderItems.length ? (
				<ClayTable
					borderedColumns
					borderless
					className={classNames('order-items-table', {
						'order-items-table-readonly': readonly,
					})}
				>
					<ClayTable.Head>
						<ClayTable.Row>
							{!readonly && (
								<ClayTable.Cell
									className="td-selection"
									headingCell
									key="selection"
								></ClayTable.Cell>
							)}

							<ClayTable.Cell
								className="td-sku-name"
								headingCell
								key="sku"
							>
								<div className="align-items-center d-flex flex-nowrap">
									<div className="flex-grow-1">
										<div className="text-nowrap text-truncate">
											{Liferay.Language.get('sku')}
										</div>
									</div>
								</div>
							</ClayTable.Cell>

							<ClayTable.Cell
								className="td-quantity"
								headingCell
								key="quantity"
							>
								<div className="align-items-center d-flex flex-nowrap">
									<div className="flex-grow-1">
										<div className="text-nowrap text-truncate">
											{Liferay.Language.get('quantity')}
										</div>
									</div>
								</div>
							</ClayTable.Cell>

							{deliveryGroups.map((deliveryGroup) => (
								<DeliveryGroupHeaderCell
									accountId={accountId}
									addressSubtypeConfiguration={
										addressSubtypeConfiguration
									}
									deliveryGroup={deliveryGroup}
									disabled={readonly}
									handleDeleteDeliveryGroup={
										handleDeleteDeliveryGroup
									}
									handleSubmitDeliveryGroup={
										handleSubmitDeliveryGroup
									}
									key={deliveryGroup.id}
									readonly={readonly}
									saving={saving}
								/>
							))}
						</ClayTable.Row>
					</ClayTable.Head>

					<ClayTable.Body>
						{filterFormattedOrderItems
							.slice(
								(pagination.currentPage - 1) *
									pagination.pageSize,
								pagination.currentPage * pagination.pageSize
							)
							.map((orderItem, currentIndex) => (
								<OrderItemRow
									checked={checkedOrderItemIds.includes(
										orderItem.id
									)}
									deliveryGroups={deliveryGroups}
									disabled={readonly || saving}
									handleSelection={handleRowSelection}
									handleSubmit={handleRowUpdate}
									key={orderItem.id}
									orderId={orderId}
									orderItem={orderItem}
									readonly={readonly}
									rowIndex={currentIndex}
								/>
							))}
					</ClayTable.Body>
				</ClayTable>
			) : (
				<ClayEmptyState
					description={Liferay.Language.get(
						'sorry,-no-results-were-found'
					)}
					image={
						<ClayIcon
							className="c-empty-state-icon c-empty-state-icon-search-state"
							illustration
							symbol="search-state"
						/>
					}
					title={Liferay.Language.get('no-results-found')}
				/>
			)}
		</div>
	);

	const paginationComponent = (
		<div className="data-set-pagination-wrapper">
			<ClayPaginationBarWithBasicItems
				activeDelta={pagination.pageSize}
				deltas={[4, 8, 20, 40, 60].map((size) => ({
					label: size,
				}))}
				ellipsisBuffer={3}
				onActiveChange={handlePaginationPageChange}
				onDeltaChange={handlePaginationDeltaChange}
				totalItems={filterFormattedOrderItems.length}
			/>
		</div>
	);

	return (
		<div className="data-set data-set-fluid multishipping-container">
			{managementBar}

			<div>
				{loading ? (
					<ClayLoadingIndicator
						data-qa-id="loadingSpinner"
						displayType="secondary"
						size="sm"
					/>
				) : (
					<>
						{view}

						{paginationComponent}
					</>
				)}
			</div>
		</div>
	);
};

export default Multishipping;
