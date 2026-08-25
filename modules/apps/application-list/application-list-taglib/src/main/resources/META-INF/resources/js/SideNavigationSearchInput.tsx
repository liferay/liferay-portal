/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import Form, {ClayInput} from '@clayui/form';
import {InternalDispatch, useDebounce, useIsFirstRender} from '@clayui/shared';
import React, {useEffect, useRef, useState} from 'react';

function SideNavigationSearchInput({
	onChange,
	onFocus,
}: {
	onChange?: InternalDispatch<string>;
	onFocus?: () => void;
}) {
	const inputRef = useRef<HTMLInputElement>(null);

	const [query, setQuery] = useState('');

	const debouncedQuery = useDebounce(query, 300);
	const isFirstRender = useIsFirstRender();

	useEffect(() => {
		if (!isFirstRender && onChange) {
			onChange(debouncedQuery);
		}
	}, [debouncedQuery, isFirstRender, onChange]);

	return (
		<Form.Group className="c-mx-1 c-px-2">
			<ClayInput.Group>
				<ClayInput.GroupItem>
					<ClayInput
						aria-label={Liferay.Language.get('search')}
						className="c-pl-3"
						data-qa-id="sideNavigationSearchInput"
						insetAfter={!!query}
						onChange={(event) => setQuery(event.target.value)}
						onFocus={onFocus}
						placeholder={Liferay.Language.get('search')}
						ref={inputRef}
						type="search"
						value={query}
					/>

					{query && (
						<ClayInput.GroupInsetItem after tag="span">
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'clear-search'
								)}
								borderless
								data-qa-id="sideNavigationClearSearchButton"
								displayType="secondary"
								monospaced={false}
								onClick={() => {
									setQuery('');

									inputRef.current?.focus();
								}}
								size="sm"
								symbol="times"
								title={Liferay.Language.get('clear-search')}
							/>
						</ClayInput.GroupInsetItem>
					)}
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</Form.Group>
	);
}

export default SideNavigationSearchInput;
