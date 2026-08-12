/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import React from 'react';

interface ISearchInterface {
	onSearch: Function;
	query: string;
}

const Search = ({onSearch, query}: ISearchInterface) => (
	<ClayInput.Group>
		<ClayInput.GroupItem>
			<ClayInput
				insetAfter
				onChange={(event) => onSearch(event.target.value)}
				placeholder={Liferay.Language.get('search')}
				type="text"
				value={query}
			/>

			<ClayInput.GroupInsetItem after tag="span">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('search')}
					displayType="unstyled"
					symbol="search"
				/>
			</ClayInput.GroupInsetItem>
		</ClayInput.GroupItem>
	</ClayInput.Group>
);

export default Search;
