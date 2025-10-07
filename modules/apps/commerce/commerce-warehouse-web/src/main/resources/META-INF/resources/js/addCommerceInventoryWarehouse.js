/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CommerceServiceProvider, commerceEvents} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import {createPortletURL} from 'frontend-js-web';

export default function ({
	defaultLanguageId,
	editCommerceInventoryWarehousePortletURL,
	namespace,
}) {
	const CommerceInventoryWarehouseResource =
		CommerceServiceProvider.AdminInventoryAPI('v1');

	const form = document.getElementById(`${namespace}fm`);

	form.addEventListener('submit', (event) => {
		event.preventDefault();

		const name = form.querySelector(`#${namespace}name`).value;

		if (!name) {
			openToast({
				message: Liferay.Language.get('please-enter-a-valid-name'),
				title: Liferay.Language.get('error'),
				type: 'danger',
			});

			return;
		}

		const commerceInventoryWarehouseData = {
			active: false,
			name: {[defaultLanguageId]: name},
		};

		if (defaultLanguageId !== Liferay.ThemeDisplay.getDefaultLanguageId()) {
			commerceInventoryWarehouseData.name[
				Liferay.ThemeDisplay.getDefaultLanguageId()
			] = name;
		}

		return CommerceInventoryWarehouseResource.addWarehouse(
			commerceInventoryWarehouseData
		)
			.then((payload) => {
				const redirectURL = createPortletURL(
					editCommerceInventoryWarehousePortletURL
				);

				redirectURL.searchParams.append(
					`${namespace}commerceInventoryWarehouseId`,
					payload.id
				);
				redirectURL.searchParams.append('p_auth', Liferay.authToken);

				window.parent.Liferay.fire(commerceEvents.CLOSE_MODAL, {
					redirectURL: redirectURL.toString(),
					successNotification: {
						message: Liferay.Language.get(
							'your-request-completed-successfully'
						),
						showSuccessNotification: true,
					},
				});
			})
			.catch((error) => {
				const errorsMap = {
					'please-enter-a-valid-name': Liferay.Language.get(
						'please-enter-a-valid-name'
					),
				};

				openToast({
					message:
						errorsMap[error.message] ||
						Liferay.Language.get('an-unexpected-error-occurred'),
					title: Liferay.Language.get('error'),
					type: 'danger',
				});
			});
	});
}
