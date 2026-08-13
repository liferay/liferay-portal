/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React, {useState} from 'react';

interface ISearchInterface {
	onSearch: Function;
	query: string;
}

const AutoSearch = ({onSearch, query}: ISearchInterface) => {
	const [focused, setFocused] = useState<boolean>(false);

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem>
				{!focused && (
					<ClayInput.GroupInsetItem before tag="span">
						<ClayIcon
							className="inline-item inline-item-before"
							focusable="false"
							role="presentation"
							symbol="search"
						/>
					</ClayInput.GroupInsetItem>
				)}

				<ClayInput
					insetAfter={focused}
					insetBefore={!focused}
					onChange={(event) => onSearch(event.target.value)}
					onFocus={() => setFocused(true)}
					placeholder={Liferay.Language.get('search')}
					type="text"
					value={query}
				/>

				{focused && (
					<ClayInput.GroupInsetItem after tag="span">
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('clear-search')}
							borderless
							displayType="secondary"
							monospaced={false}
							onClick={() => {
								onSearch('');
								setFocused(false);
							}}
							size="sm"
							symbol="times"
							title={Liferay.Language.get('clear-search')}
						/>
					</ClayInput.GroupInsetItem>
				)}
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
};

export default AutoSearch;
