/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';

import getCurrenciesItemSelectorProps from '../item_selector/getCurrenciesItemSelectorProps';

function addOpenSelectionModalEvent({excludedCurrencyIds, namespace}) {
	Liferay.on(namespace + 'addCommerceChannelCommerceCurrency', () => {
		openSelectionModal({
			excludedCurrencyIds,
			namespace,
		});
	});

	Liferay.on('destroyPortlet', () => {
		Liferay.detach(namespace + 'addCommerceChannelCommerceCurrency');
	});
}

function openSelectionModal({excludedCurrencyIds, namespace}) {
	const addCommerceChannelCommerceCurrencyFm = document.getElementById(
		namespace + 'addCommerceChannelCommerceCurrencyFm'
	);
	const currencyIds = document.getElementById(namespace + 'currencyIds');

	openItemSelectorModal({
		...getCurrenciesItemSelectorProps({
			excludedCurrencyIds,
			portletNamespace: namespace,
		}),
		items: [],
		onItemsChange: (currencies) => {
			if (!currencies.length) {
				return;
			}

			if (currencyIds) {
				const ids = currencies.map((currency) => currency.id);

				currencyIds.value = ids.join(',');
			}

			if (addCommerceChannelCommerceCurrencyFm) {
				submitForm(addCommerceChannelCommerceCurrencyFm);
			}
		},
	});
}

export default function (context) {
	addOpenSelectionModalEvent(context);
}
