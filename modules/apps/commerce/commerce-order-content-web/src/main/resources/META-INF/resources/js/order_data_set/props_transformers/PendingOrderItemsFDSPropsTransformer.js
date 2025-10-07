/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	CommerceServiceProvider,
	MiniCartUtils,
	ProductOptionsDataRenderer,
	commerceEvents,
} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';

import ProductURLDataRenderer from '../data_renderers/ProductURLDataRenderer';

const DeliveryCartAPI = CommerceServiceProvider.DeliveryCartAPI('v1');

const PendingOrderItemsFDSPropsTransformer = (props) => {
	Liferay.on(commerceEvents.CART_UPDATED, () => {
		Liferay.fire('fds-update-display', {id: props.id});
	});

	return {
		...props,
		customDataRenderers: {
			productOptionsDataRenderer: (componentProps) =>
				ProductOptionsDataRenderer({
					...componentProps,
					additionalProps: props.additionalProps,
				}),
			productURLDataRenderer: (componentProps) =>
				ProductURLDataRenderer({
					...componentProps,
					additionalProps: props.additionalProps,
				}),
		},
		onActionDropdownItemClick: ({
			action: {
				data: {id: actionId},
			},
			itemData: {id: orderItemId, options, productURLs},
		}) => {
			if (actionId === 'view') {
				window.location.href = MiniCartUtils.generateProductPageURL(
					props.additionalProps.siteDefaultURL,
					productURLs,
					props.additionalProps.productURLSeparator
				);
			}

			if (actionId === 'edit') {
				const eventName = MiniCartUtils.hasOptions(options)
					? commerceEvents.OPEN_MINICART_FOR_EDITING
					: commerceEvents.OPEN_MINI_CART;

				Liferay.fire(eventName, {
					dataSetId: props.id,
					orderItemId,
				});
			}

			if (actionId === 'delete') {
				DeliveryCartAPI.deleteItemById(orderItemId)
					.then(() => {
						openToast({
							message: Liferay.Language.get(
								'your-request-completed-successfully'
							),
							type: 'success',
						});

						Liferay.fire(commerceEvents.CURRENT_ORDER_UPDATED, {
							order: {
								id: parseInt(
									props.additionalProps.commerceOrderId,
									10
								),
							},
						});
					})
					.catch((error) => {
						openToast({
							message:
								error.message ||
								Liferay.Language.get(
									'an-unexpected-error-occurred'
								),
							type: 'danger',
						});
					});
			}
		},
		onBulkActionItemClick: ({
			action: {
				data: {id: actionId},
			},
			loadData,
			selectedData: {items},
		}) => {
			if (actionId === 'delete') {
				DeliveryCartAPI.deleteItemsById(items)
					.then(() => {
						setTimeout(() => {
							loadData();

							openToast({
								message: Liferay.Language.get(
									'your-request-completed-successfully'
								),
								type: 'success',
							});

							Liferay.fire(commerceEvents.CURRENT_ORDER_UPDATED, {
								order: {
									id: props.additionalProps.commerceOrderId,
								},
							});
						}, 500);
					})
					.catch((error) => {
						openToast({
							message:
								error.message ||
								Liferay.Language.get(
									'an-unexpected-error-occurred'
								),
							type: 'danger',
						});
					});
			}
		},
	};
};

export default PendingOrderItemsFDSPropsTransformer;
