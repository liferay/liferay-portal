/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {debounce, sub} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import {UserAccount, UserGroup} from '../../common/types/UserAccount';
import {
	SelectOptions,
	SpaceMembersSelectOptions,
} from './SpaceMembersSelectOptions';

interface SearcheableSpaceMembersListProps {
	className?: string;
	onSelectChange: (value: SelectOptions) => void;
	selectValue: SelectOptions;
	onSearch: (value: string) => void;
}

export function SearcheableSpaceMembersList(
	props: SearcheableSpaceMembersListProps
) {
	const {className, onSearch, onSelectChange, selectValue} = props;

	const [search, setSearch] = useState('');

	const debouncedOnSearch = useMemo(
		() => debounce(onSearch, 400),
		[onSearch]
	);

	return (
		<SpaceMembersSelectOptions
			className={className}
			onSelectChange={onSelectChange}
			selectValue={selectValue}
		>
			<ClayInput
				aria-label={Liferay.Language.get('search-for-name-or-email')}
				className="form-control input-group-inset input-group-inset-after"
				name="search"
				onChange={(event) => {
					setSearch(event.target.value);

					debouncedOnSearch(event.target.value);
				}}
				placeholder={Liferay.Language.get('search-for-name-or-email')}
				type="text"
				value={search}
			/>
		</SpaceMembersSelectOptions>
	);
}
