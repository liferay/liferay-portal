/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayCheckbox, ClayToggle} from '@clayui/form';
import ClayList from '@clayui/list';
import React, {useEffect, useState} from 'react';

import Sidebar from '../../shared/Sidebar';
import {
	ACTIVE,
	ALL,
	ASCENDING,
	DESCENDING,
	INACTIVE,
} from '../../utils/constants';
import removeDuplicates from '../../utils/functions/remove_duplicates';
import {TEST_IDS} from '../../utils/testIds';
import LearnMessage from './../../shared/LearnMessage';
import ManagementToolbar from './ManagementToolbar';

/**
 * Converts a class name "com.liferay.account.internal.search.spi.model.query.contributor.AccountEntryKeywordQueryContributor"
 * to "Account Entry Keyword Query Contributor".
 * @param {String} className The class name to be converted.
 * @return {String} The converted class name.
 */
const getClassDisplayName = (className) => {
	if (!className) {
		return '';
	}

	return className
		.split('.')
		.slice(-1)[0]
		.split(/([A-Z][a-z]+)/g)
		.join(' ');
};

export default function ({
	frameworkConfig,
	initialClauseContributorsList = [],
	onClose,
	onFetchContributors,
	onFrameworkConfigChange,
	visible,
}) {
	const [category, setCategory] = useState(ALL);
	const [contributors, setContributors] = useState(
		initialClauseContributorsList
	);
	const [keyword, setKeyword] = useState('');
	const [selected, setSelected] = useState([]);
	const [sortDirection, setSortDirection] = useState(DESCENDING);
	const [status, setStatus] = useState(ALL);

	const filterItems = [
		{
			items: [
				{
					active: status === ALL,
					label: ALL,
					onClick: () => setStatus(ALL),
				},
				{
					active: status === ACTIVE,
					label: ACTIVE,
					onClick: () => setStatus(ACTIVE),
				},
				{
					active: status === INACTIVE,
					label: INACTIVE,
					onClick: () => setStatus(INACTIVE),
				},
			],
			label: Liferay.Language.get('filter-by-status'),
			name: 'filter-by-status',
			type: 'group',
		},
		{
			items: [
				{
					active: category === ALL,
					label: ALL,
					onClick: () => setCategory(ALL),
				},
				...initialClauseContributorsList.map((contributor) => ({
					active: category === contributor.label,
					label: contributor.label,
					onClick: () => setCategory(contributor.label),
				})),
			],
			label: Liferay.Language.get('filter-by-category'),
			name: 'filter-by-category',
			type: 'group',
		},
	];

	useEffect(() => {
		const _isSearchVisible = (item, keyword) => {
			if (keyword) {
				return keyword
					.split(' ')
					.every((word) =>
						item.toLowerCase().includes(word.toLowerCase())
					);
			}
			else {
				return true;
			}
		};

		const _isStatusVisible = (item, status) => {
			if (status === ALL) {
				return true;
			}

			if (status === ACTIVE) {
				return frameworkConfig.clauseContributorsIncludes.includes(
					item
				);
			}

			if (status === INACTIVE) {
				return !frameworkConfig.clauseContributorsIncludes.includes(
					item
				);
			}
		};

		setContributors(
			initialClauseContributorsList
				.filter(({label}) => category === ALL || category === label)
				.map(({label, value}) => ({
					label,
					value: value
						.filter(
							(item) =>
								_isSearchVisible(item, keyword) &&
								_isStatusVisible(item, status)
						)
						.sort((a, b) =>
							sortDirection === DESCENDING
								? a.localeCompare(b)
								: b.localeCompare(a)
						),
				}))
		);
	}, [
		category,
		frameworkConfig,
		initialClauseContributorsList,
		keyword,
		sortDirection,
		status,
	]);

	const _handleEnableClauseContributors = (classNames) => {
		onFrameworkConfigChange({
			clauseContributorsExcludes: [],
			clauseContributorsIncludes: removeDuplicates([
				...frameworkConfig.clauseContributorsIncludes,
				...classNames,
			]),
		});
	};

	const _handleDisableClauseContributors = (classNames) => {
		onFrameworkConfigChange({

			// When clauseContributorsIncludes is empty, set
			// clauseContributorsExcludes to ['*'] since it
			// would essentially be like 'Disable All'.

			clauseContributorsExcludes:
				frameworkConfig.clauseContributorsIncludes.some(
					(clause) => !classNames.includes(clause)
				)
					? []
					: ['*'],
			clauseContributorsIncludes:
				frameworkConfig.clauseContributorsIncludes.filter(
					(clause) => !classNames.includes(clause)
				),
		});
	};

	const _handleSelectChange = (className) => () => {
		setSelected(
			selected.includes(className)
				? selected.filter(
						(preselectedClassName) =>
							preselectedClassName !== className
					)
				: [...selected, className]
		);
	};

	const _handleToggleClauseContributor = (className) => () => {
		if (frameworkConfig.clauseContributorsIncludes.includes(className)) {
			_handleDisableClauseContributors([className]);
		}
		else {
			_handleEnableClauseContributors([className]);
		}
	};

	const _handleUpdateSelected = (value) => () => {
		if (value) {
			_handleEnableClauseContributors(selected);
		}
		else {
			_handleDisableClauseContributors(selected);
		}
	};

	return (
		<Sidebar
			className="clause-contributors-sidebar"
			onClose={onClose}
			title={Liferay.Language.get('clause-contributors')}
			visible={visible}
		>
			<div className="sidebar-body">
				<div className="clause-contributors-description">
					<span className="help-text text-secondary">
						{Liferay.Language.get(
							'clause-contributors-description'
						)}
					</span>

					<LearnMessage resourceKey="query-clause-contributors-configuration" />
				</div>

				<ManagementToolbar
					allItems={contributors.reduce(
						(acc, curr) => [...curr.value, ...acc],
						[]
					)}
					category={category}
					filterItems={filterItems}
					keyword={keyword}
					onClearCategory={() => setCategory(ALL)}
					onClearStatus={() => setStatus(ALL)}
					onReverseSort={() =>
						setSortDirection(
							sortDirection === ASCENDING ? DESCENDING : ASCENDING
						)
					}
					onUpdateSelected={_handleUpdateSelected}
					selected={selected}
					setKeyword={setKeyword}
					setSelected={setSelected}
					sortDirection={sortDirection}
					status={status}
				/>

				{initialClauseContributorsList.some(
					({value}) => !value.length
				) && (
					<ClayAlert
						actions={
							<ClayButton.Group>
								<ClayButton alert onClick={onFetchContributors}>
									{Liferay.Language.get('refresh')}
								</ClayButton>
							</ClayButton.Group>
						}
						className="c-my-3"
						displayType="danger"
						title={Liferay.Language.get('error')}
						variant="inline"
					>
						{Liferay.Language.get(
							'an-error-has-occurred-and-we-were-unable-to-load-the-results'
						)}
					</ClayAlert>
				)}

				<ClayList>
					{contributors.map((contributor) => (
						<React.Fragment key={contributor.label}>
							<ClayList.Header>
								{contributor.label}
							</ClayList.Header>

							{contributor.value.map((className, index) => (
								<ClayList.Item
									active={selected.includes(className)}
									data-qa-id={
										TEST_IDS.CLAUSE_CONTRIBUTORS_SIDEBAR_LIST_ITEM
									}
									flex
									key={`${className}-${index}`}
								>
									<ClayList.ItemField>
										<ClayCheckbox
											aria-label={Liferay.Language.get(
												'checkbox'
											)}
											checked={selected.includes(
												className
											)}
											onChange={_handleSelectChange(
												className
											)}
										/>
									</ClayList.ItemField>

									<ClayList.ItemField expand>
										<ClayList.ItemTitle>
											{getClassDisplayName(className)}
										</ClayList.ItemTitle>

										<ClayList.ItemText className="text-secondary">
											{className}
										</ClayList.ItemText>
									</ClayList.ItemField>

									<ClayList.ItemField>
										<ClayToggle
											label={
												frameworkConfig.clauseContributorsIncludes.includes(
													className
												)
													? Liferay.Language.get('on')
													: Liferay.Language.get(
															'off'
														)
											}
											onToggle={_handleToggleClauseContributor(
												className
											)}
											toggled={frameworkConfig.clauseContributorsIncludes.includes(
												className
											)}
										/>
									</ClayList.ItemField>
								</ClayList.Item>
							))}
						</React.Fragment>
					))}
				</ClayList>
			</div>
		</Sidebar>
	);
}
