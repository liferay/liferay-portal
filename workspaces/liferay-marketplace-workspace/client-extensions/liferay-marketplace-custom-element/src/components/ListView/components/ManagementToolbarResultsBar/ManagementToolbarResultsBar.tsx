/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {ClayResultsBar} from '@clayui/management-toolbar';
import {useContext, useEffect} from 'react';
import {useLocation, useNavigate} from 'react-router-dom';

import i18n from '../../../../i18n';
import {ListViewContext, ListViewTypes} from '../../hooks/ListViewContext';

import './ManagementToolbarResultsBar.scss';

type ManagementToolbarResultsBarProps = {
	totalItems: number;
};

const ManagementToolbarResultsBar: React.FC<
	ManagementToolbarResultsBarProps
> = ({totalItems}) => {
	const location = useLocation();
	const navigate = useNavigate();
	const searchParams = new URLSearchParams(location.search);

	const filter = searchParams.get('filter');

	const [{filters}, dispatch] = useContext(ListViewContext);

	const handleRemoveItemFromFilter = (itemToRemove: string) => {
		if (!filter) {
			return;
		}

		const filterJSON = JSON.parse(decodeURIComponent(filter));

		delete filterJSON[itemToRemove];

		if (Object.keys(filterJSON).length) {
			searchParams.set('filter', JSON.stringify(filterJSON));
		}
		else {
			searchParams.delete('filter');
			searchParams.delete('filterSchema');
			searchParams.delete('page');
		}

		navigate({
			search: `?${searchParams.toString()}`,
		});
	};

	const onRemoveFilter = (filterName: string) => {
		dispatch({payload: filterName, type: ListViewTypes.SET_REMOVE_FILTER});

		handleRemoveItemFromFilter(filterName);
	};

	useEffect(() => {
		if (!filter) {
			filters.entries
				.filter(({value}) => value)
				.forEach((entry) =>
					dispatch({
						payload: entry.name,
						type: ListViewTypes.SET_REMOVE_FILTER,
					})
				);
		}
	}, [dispatch, filter, filters.entries]);

	return (
		<div className="border-bottom border-top d-block toolbar-results-container w-100">
			<ClayResultsBar>
				<ClayResultsBar.Item>
					<span className="component-text text-truncate-inline">
						<span className="text-truncate">
							{i18n.sub('x-results-for', [totalItems.toString()])}
						</span>
					</span>
				</ClayResultsBar.Item>

				{filters.entries
					.filter(({value}) => value)
					.map((entry, index) => (
						<ClayResultsBar.Item
							expand={index === filters.entries.length - 1}
							key={index}
						>
							<ClayLabel
								className="component-label result-filter tbar-label"
								displayType="unstyled"
							>
								<span className="d-flex flex-row">
									<b>{entry.label}</b>

									{`: ${
										Array.isArray(entry.value)
											? entry.value
													.map((entryValue) =>
														String(
															typeof entryValue ===
																'object'
																? entryValue?.label
																: entryValue
														)
													)
													.sort((entryA, entryB) =>
														entryA.localeCompare(
															entryB
														)
													)
													.join(', ')
											: entry.value
									}`}

									<ClayIcon
										className="cursor-pointer ml-2"
										onClick={() =>
											onRemoveFilter(entry.name)
										}
										symbol="times"
									/>
								</span>
							</ClayLabel>
						</ClayResultsBar.Item>
					))}
			</ClayResultsBar>
		</div>
	);
};

export default ManagementToolbarResultsBar;
