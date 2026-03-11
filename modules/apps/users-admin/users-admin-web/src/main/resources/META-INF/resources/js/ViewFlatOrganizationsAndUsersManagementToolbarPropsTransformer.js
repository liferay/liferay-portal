/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openSelectionModal} from 'frontend-js-components-web';
import {createPortletURL, navigate} from 'frontend-js-web';

import {ACTIONS} from './actions';

const SELECTION_CONFIGS = {
	selectAccountEntries: {
		paramName: 'accountEntryIds',
		selection: 'selected-account-users',
		urlKey: 'accountEntriesSelectorURL',
	},
	selectOrganizations: {
		paramName: 'organizationIds',
		selection: 'selected-organization-users',
		urlKey: 'organizationsSelectorURL',
	},
};

export default function propsTransformer({portletNamespace, ...otherProps}) {
	const select = (itemData, selection) => {
		const config = SELECTION_CONFIGS[selection];

		if (!config) {
			return;
		}

		openSelectionModal({
			multiple: true,
			onSelect: (selectedItems) => {
				if (!selectedItems?.length) {
					return;
				}

				const values = selectedItems.map((item) => item.value);

				const redirectURL = createPortletURL(itemData?.redirectURL, {
					[config.paramName]: values.join(','),
					selection: config.selection,
				});

				navigate(redirectURL);
			},
			title: itemData?.dialogTitle,
			url: itemData?.[config.urlKey],
		});
	};

	return {
		...otherProps,
		onActionButtonClick: (event, {item}) => {
			const data = item?.data;

			const action = data?.action;

			if (action) {
				event.preventDefault();

				ACTIONS[action](data, portletNamespace);
			}
		},
		onFilterDropdownItemClick(event, {item}) {
			select(item?.data, item?.data?.action);
		},
	};
}
