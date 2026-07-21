/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {usePrevious} from '@clayui/shared';
import {useCallback, useEffect, useRef, useState} from 'react';

type Props = {
	active: boolean;
	searchable?: boolean;
	searchableThreshold?: number;
	totalItems: number;
};

export function useSearch({
	active,
	searchable,
	searchableThreshold,
	totalItems,
}: Props) {
	const searchRef = useRef<HTMLInputElement | null>(null);

	const [searchValue, setSearchValue] = useState('');

	const previousActive = usePrevious(active);

	const isSearchable =
		searchable === true ||
		(searchable === undefined &&
			searchableThreshold !== undefined &&
			totalItems > searchableThreshold);

	const filter = useCallback(
		(value: string) =>
			value.toLowerCase().includes(searchValue.toLowerCase()),
		[searchValue]
	);

	useEffect(
		function focusSearchInputWhenBecomingActive() {
			if (active && !previousActive && isSearchable) {
				searchRef.current?.focus({preventScroll: true});
			}

			if (!active && previousActive) {
				setSearchValue('');
			}
		},
		[active, isSearchable]
	);

	return {filter, isSearchable, searchRef, searchValue, setSearchValue};
}
