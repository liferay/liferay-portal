/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';

import getCountriesItemSelectorProps from '../item_selector/getCountriesItemSelectorProps';

function addOpenSelectionModalEvent({excludedCountryIds, namespace}) {
	Liferay.on(namespace + 'addCommerceChannelCountry', () => {
		openSelectionModal({
			excludedCountryIds,
			namespace,
		});
	});

	Liferay.on('destroyPortlet', () => {
		Liferay.detach(namespace + 'addCommerceChannelCountry');
	});
}

function openSelectionModal({excludedCountryIds, namespace}) {
	const addCommerceChannelCountryFm = document.getElementById(
		namespace + 'addCommerceChannelCountryFm'
	);
	const countryIds = document.getElementById(namespace + 'countryIds');

	openItemSelectorModal({
		...getCountriesItemSelectorProps({
			excludedCountryIds,
			portletNamespace: namespace,
		}),
		items: [],
		onItemsChange: (countries) => {
			if (!countries.length) {
				return;
			}

			if (countryIds) {
				const ids = countries.map((country) => country.id);

				countryIds.value = ids.join(',');
			}

			if (addCommerceChannelCountryFm) {
				submitForm(addCommerceChannelCountryFm);
			}
		},
	});
}

export default function (context) {
	addOpenSelectionModalEvent(context);
}
