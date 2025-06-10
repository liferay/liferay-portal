/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayDropDown from '@clayui/drop-down';
import React, {useRef, useState} from 'react';

function PatcherAccountsAutocomplete() {
	const [autocompleteSearchValue, setAutocompleteSearchValue] = useState('');

	const [showDropDown, setShowDropDown] = useState(false);

	const currentItemSelectedRef = useRef('');

	const _handleInputChange = (event) => {
		if (!event.target.value) {
			currentItemSelectedRef.current = '';
		}

		setAutocompleteSearchValue(event.target.value);
		setShowDropDown(true);
	};

	return (
		<ClayAutocomplete>
			<ClayAutocomplete.Input
				aria-label={Liferay.Language.get('find-account')}
				id="accountEntryCode"
				name="accountEntryCode"
				onChange={_handleInputChange}
				value={autocompleteSearchValue}
			/>

			<ClayAutocomplete.DropDown
				active={showDropDown}
				onSetActive={setShowDropDown}
			>
				<ClayDropDown.ItemList>
					{(item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					)}
				</ClayDropDown.ItemList>
			</ClayAutocomplete.DropDown>
		</ClayAutocomplete>
	);
}

export default PatcherAccountsAutocomplete;
