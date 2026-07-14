/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Item} from './FilterDropdown';

export interface IAllFiltersDropdown extends React.HTMLAttributes<HTMLElement> {
	item: Item;
	onSelectItem: (item: Item) => void;
}

export const initialFilters = {
	category: {
		label: Liferay.Language.get('all-categories'),
		value: 'all',
	},
	structure: {
		label: Liferay.Language.get('all-content-structures'),
		value: 'all',
	},
	tag: {
		label: Liferay.Language.get('all-tags'),
		value: 'all',
	},
	vocabulary: {
		label: Liferay.Language.get('all-vocabularies'),
		value: 'all',
	},
};

export function filterBySpaces(
	assetLibraries: {id: number}[],
	depotEntryId: string
) {
	return assetLibraries.some(({id}) => {

		// Returns true if id belongs to all spaces (-1).

		if (id === -1) {
			return true;
		}

		// Decreasing -1 due a bug where response is increasing +1 in the id.
		// Returns true if match id with id from space.

		return String(id - 1) === depotEntryId;
	});
}
