/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import {ManagementToolbar} from 'frontend-js-components-web';
import React, {useState} from 'react';

export default function PublicationsPermissionsSearchBar({
	filteredRoles,
	onChangeRoles,
	roles,
}) {
	const [resultsKeywords, setResultsKeywords] = useState('');
	const [searchTerms, setSearchTerms] = useState('');

	const format = (key, args) => {
		const SPLIT_REGEX = /({\d+})/g;

		const keyArray = key
			.split(SPLIT_REGEX)
			.filter((val) => val.length !== 0);

		for (let i = 0; i < args.length; i++) {
			const arg = args[i];

			const indexKey = `{${i}}`;

			let argIndex = keyArray.indexOf(indexKey);

			while (argIndex >= 0) {
				keyArray.splice(argIndex, 1, arg);

				argIndex = keyArray.indexOf(indexKey);
			}
		}

		return keyArray.join('');
	};

	const onSubmit = (keywords) => {
		setResultsKeywords(keywords);

		if (keywords) {
			onChangeRoles(
				roles.filter((role) => {
					return role.label
						.toLowerCase()
						.includes(keywords.toLowerCase());
				})
			);
		}
		else {
			onChangeRoles(roles);
		}
	};

	const renderResultsBar = () => {
		if (!resultsKeywords) {
			return '';
		}

		let count = 0;
		let key = Liferay.Language.get('x-results-for');

		count = filteredRoles.length;

		if (count === 1) {
			key = Liferay.Language.get('x-result-for');
		}

		return (
			<div className="p-3 results-bar">
				<ManagementToolbar.ResultsBar>
					<ManagementToolbar.ResultsBarItem expand>
						<span className="component-text text-truncate-inline">
							<span className="text-truncate">
								{format(key, [count]) + ' '}

								<strong>{resultsKeywords}</strong>
							</span>
						</span>
					</ManagementToolbar.ResultsBarItem>

					<ManagementToolbar.ResultsBarItem>
						<ClayButton
							aria-label={Liferay.Language.get('clear')}
							className="component-link tbar-link"
							displayType="unstyled"
							onClick={() => {
								onSubmit('');
								setSearchTerms('');
							}}
						>
							{Liferay.Language.get('clear')}
						</ClayButton>
					</ManagementToolbar.ResultsBarItem>
				</ManagementToolbar.ResultsBar>
			</div>
		);
	};

	const renderSearchBar = () => {
		return (
			<ManagementToolbar.Container>
				<ManagementToolbar.Search
					onSubmit={(event) => {
						event.preventDefault();

						onSubmit(searchTerms.trim());
					}}
				>
					<ClayInput.Group>
						<ClayInput.GroupItem>
							<ClayInput
								aria-label={Liferay.Language.get('search')}
								className="input-group-inset input-group-inset-after"
								onChange={(event) =>
									setSearchTerms(event.target.value)
								}
								placeholder={`${Liferay.Language.get(
									'search'
								)}...`}
								type="text"
								value={searchTerms}
							/>

							<ClayInput.GroupInsetItem after tag="span">
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get('submit')}
									displayType="unstyled"
									symbol="search"
									type="submit"
								/>
							</ClayInput.GroupInsetItem>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ManagementToolbar.Search>
			</ManagementToolbar.Container>
		);
	};

	return (
		<>
			{renderSearchBar()}

			{renderResultsBar()}
		</>
	);
}
