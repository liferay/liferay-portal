/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import React, {useContext, useEffect, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';

function MainSearch({onClear}: {onClear: () => void}) {
	const {apiURL, appURL, onSearch, searchParam} = useContext(
		FrontendDataSetContext
	);

	const [inputValue, setInputValue] = useState(searchParam || '');

	useEffect(() => {
		setInputValue(searchParam || '');
	}, [searchParam]);

	return (
		<ClayInput.Group>
			<ClayInput.GroupItem>
				<ClayInput
					aria-label={Liferay.Language.get('search')}
					className="input-group-inset input-group-inset-after"
					onChange={(event) => {
						setInputValue(event.target.value);

						if (!event.target.value) {
							onClear();
						}

						if (!apiURL && !appURL) {
							onSearch({query: event.target.value});
						}
					}}
					onKeyDown={(event) => {
						if (event.key === 'Enter' && (apiURL || appURL)) {
							event.preventDefault();

							onSearch({query: inputValue});
						}
					}}
					placeholder={Liferay.Language.get('search')}
					type="search"
					value={inputValue}
				/>

				<ClayInput.GroupInsetItem after tag="div">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('search')}
						displayType="unstyled"
						monospaced={false}
						onClick={(event) => {
							event.preventDefault();

							onSearch({query: inputValue});
						}}
						symbol="search"
						type="submit"
					/>
				</ClayInput.GroupInsetItem>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export default MainSearch;
