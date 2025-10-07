/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetchParams} from 'commerce-frontend-js';

export default function (context) {
	const shippableCheckBox = document.getElementById(
		`${context.namespace}shippable`
	);
	const toggleBoxes = document.getElementsByClassName('show-if-shippable');

	if (shippableCheckBox && toggleBoxes.length) {
		shippableCheckBox.addEventListener('click', () => {
			for (const toggleBox of toggleBoxes) {
				toggleBox.classList.toggle('hide');
			}
		});
	}

	Liferay.provide(
		window,
		`${context.namespace}saveCPConfigurationEntry`,
		(event, form) => {
			event.preventDefault();

			const formData = new FormData(form);

			let URL = `/o/headless-commerce-admin-catalog/v1.0/product-configurations/${context.cpConfigurationEntryId}`;

			if (context.mode === 'add') {
				URL = `/o/headless-commerce-admin-catalog/v1.0/product-configuration-lists/${context.cpConfigurationListId}/product-configurations`;
			}

			return Liferay.Util.fetch(URL, {
				body: JSON.stringify({
					allowBackOrder:
						formData.get(`${context.namespace}backOrders`) === 'on',
					allowedOrderQuantities: (
						formData.get(
							`${context.namespace}allowedOrderQuantities`
						) || ''
					)
						.split(/[,. ]/g)
						.filter(Boolean),
					availabilityEstimateId:
						formData.get(
							`${context.namespace}commerceAvailabilityEstimateId`
						) || 0,
					displayAvailability:
						formData.get(
							`${context.namespace}displayAvailability`
						) === 'on',
					displayStockQuantity:
						formData.get(
							`${context.namespace}displayStockQuantity`
						) === 'on',
					entityId: context.entityId,
					inventoryEngine: formData.get(
						`${context.namespace}CPDefinitionInventoryEngine`
					),
					lowStockAction: formData.get(
						`${context.namespace}lowStockActivity`
					),
					maxOrderQuantity: formData.get(
						`${context.namespace}maxOrderQuantity`
					),
					minOrderQuantity: formData.get(
						`${context.namespace}minOrderQuantity`
					),
					minStockQuantity: formData.get(
						`${context.namespace}minStockQuantity`
					),
					multipleOrderQuantity: formData.get(
						`${context.namespace}multipleOrderQuantity`
					),
					productShippingConfiguration: {
						depth: formData.get(`${context.namespace}depth`),
						freeShipping:
							formData.get(`${context.namespace}freeShipping`) ===
							'on',
						height: formData.get(`${context.namespace}height`),
						shippable:
							formData.get(`${context.namespace}shippable`) ===
							'on',
						shippingSeparately:
							formData.get(
								`${context.namespace}shipSeparately`
							) === 'on',
						weight: formData.get(`${context.namespace}weight`),
						width: formData.get(`${context.namespace}width`),
					},
					productTaxConfiguration: {
						id:
							formData.get(
								`${context.namespace}CPTaxCategoryId`
							) || 0,
						taxable:
							formData.get(`${context.namespace}taxExempt`) !==
							'on',
					},
					purchasable:
						formData.get(`${context.namespace}purchasable`) ===
						'on',
				}),
				headers: fetchParams.headers,
				method: context.mode === 'add' ? 'POST' : 'PATCH',
			})
				.then((response) => {
					if (!response.ok) {
						return response.json().then((data) => {
							return Promise.reject(data);
						});
					}

					window.top.Liferay.Util.openToast({
						message: Liferay.Language.get(
							'your-request-completed-successfully'
						),
						type: 'success',
					});
					window.top.Liferay.fire('close-side-panel');
				})
				.catch((error) => {
					window.top.Liferay.Util.openToast({
						message:
							error.message ||
							Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						type: 'danger',
					});
				});
		}
	);
}
