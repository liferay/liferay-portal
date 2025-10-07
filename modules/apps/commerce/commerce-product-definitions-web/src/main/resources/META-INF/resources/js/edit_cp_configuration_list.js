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
		`${context.namespace}saveCPConfigurationList`,
		(event, form) => {
			event.preventDefault();

			const parseDate = (year, month, day, hour, minute, second, am) => {
				if (!Number(year)) {
					return null;
				}

				return [
					year,
					'-',
					String(Number(month) + 1).padStart(2, 0),
					'-',
					String(day).padStart(2, 0),
					'T',
					String(Number(hour) + 12 * Number(am)).padStart(2, 0),
					':',
					String(minute).padStart(2, 0),
					':',
					String(second).padStart(2, 0),
					'Z',
				].join('');
			};

			const formData = new FormData(form);

			const prefix = `${context.namespace}ExpandoAttribute--`;
			const customFields = Array.from(formData.entries())
				.filter(([name]) => name.startsWith(prefix))
				.map(([name, value]) => {
					const fieldName = name.slice(prefix.length, -2);

					return {
						customValue: {
							data: value,
						},
						name: fieldName,
					};
				});

			const displayDate = parseDate(
				formData.get(`${context.namespace}displayDateYear`),
				formData.get(`${context.namespace}displayDateMonth`),
				formData.get(`${context.namespace}displayDateDay`),
				formData.get(`${context.namespace}displayDateHour`),
				formData.get(`${context.namespace}displayDateMinute`),
				0,
				formData.get(`${context.namespace}displayDateAmPm`)
			);
			const expirationDate = parseDate(
				formData.get(`${context.namespace}expirationDateYear`),
				formData.get(`${context.namespace}expirationDateMonth`),
				formData.get(`${context.namespace}expirationDateDay`),
				formData.get(`${context.namespace}expirationDateHour`),
				formData.get(`${context.namespace}expirationDateMinute`),
				0,
				formData.get(`${context.namespace}expirationDateAmPm`)
			);

			return Liferay.Util.fetch(
				`/o/headless-commerce-admin-catalog/v1.0/product-configuration-lists/${context.cpConfigurationListId}`,
				{
					body: JSON.stringify({
						customFields,
						displayDate,
						expirationDate,
						name: formData.get(`${context.namespace}name`),
						neverExpire: !expirationDate,
						priority:
							formData.get(`${context.namespace}priority`) || '0',
					}),
					headers: fetchParams.headers,
					method: 'PATCH',
				}
			)
				.then((response) => {
					if (!response.ok) {
						return response.json().then((data) => {
							return Promise.reject(data);
						});
					}

					if (!Number(context.cpConfigurationEntryId)) {
						return Promise.resolve();
					}

					return Liferay.Util.fetch(
						`/o/headless-commerce-admin-catalog/v1.0/product-configurations/${context.cpConfigurationEntryId}`,
						{
							body: JSON.stringify({
								allowBackOrder:
									formData.get(
										`${context.namespace}backOrders`
									) === 'on',
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
									depth: formData.get(
										`${context.namespace}depth`
									),
									freeShipping:
										formData.get(
											`${context.namespace}freeShipping`
										) === 'on',
									height: formData.get(
										`${context.namespace}height`
									),
									shippable:
										formData.get(
											`${context.namespace}shippable`
										) === 'on',
									shippingSeparately:
										formData.get(
											`${context.namespace}shipSeparately`
										) === 'on',
									weight: formData.get(
										`${context.namespace}weight`
									),
									width: formData.get(
										`${context.namespace}width`
									),
								},
								productTaxConfiguration: {
									id:
										formData.get(
											`${context.namespace}CPTaxCategoryId`
										) || 0,
									taxable:
										formData.get(
											`${context.namespace}taxExempt`
										) !== 'on',
								},
								purchasable:
									formData.get(
										`${context.namespace}purchasable`
									) === 'on',
							}),
							headers: fetchParams.headers,
							method: 'PATCH',
						}
					).then((response) => {
						if (!response.ok) {
							return response.json().then((data) => {
								return Promise.reject(data);
							});
						}

						return Promise.resolve();
					});
				})
				.then(() => {
					window.top.Liferay.Util.openToast({
						message: Liferay.Language.get(
							'your-request-completed-successfully'
						),
						type: 'success',
					});
				})
				.catch((error) => {
					Liferay.Util.openToast({
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
