/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';
import {deactivateFilter} from '../../../utils/filters/deactivateFilter';
import ViewsContext from '../../../views/ViewsContext';
import FilterResume from './FilterResume';
import SearchResume from './SearchResume';

function ActiveFiltersBar({dataLoading, disabled, total}) {
	const {onSearch, searchParam, searching, setSearching, updateFilters} =
		useContext(FrontendDataSetContext);
	const [{filters}, viewsDispatch] = useContext(ViewsContext);

	const searchActive = Boolean(searchParam?.trim());

	const resetFiltersValue = () => {
		setSearching(true);

		viewsDispatch(
			updateFilters(filters.map((filter) => deactivateFilter(filter)))
		);

		onSearch({query: ''});
	};

	const activeFilters = filters.filter((filter) => filter.active);

	return activeFilters.length || searchActive ? (
		<div
			className="management-bar management-bar-light navbar navbar-expand-md"
			data-qa-id="activeFiltersToolbar"
		>
			<div className="container-fluid">
				<nav className="subnav-tbar subnav-tbar-light w-100">
					<ul className="tbar-nav">
						<li className="p-0 tbar-item tbar-item-expand">
							<div className="tbar-section">
								{dataLoading && searching ? (
									<span>
										{Liferay.Language.get(
											'requesting-results-for-colon'
										)}
									</span>
								) : (
									<span>
										{sub(
											total === 1
												? Liferay.Language.get(
														'x-result-found-for-colon'
													)
												: Liferay.Language.get(
														'x-results-found-for-colon'
													),
											total
										)}
									</span>
								)}

								{searchActive && <SearchResume />}

								{activeFilters.map((filter) => {
									return (
										<FilterResume
											disabled={disabled}
											key={filter.id}
											{...filter}
										/>
									);
								})}
							</div>
						</li>

						<li className="tbar-item">
							<div className="tbar-section">
								<ClayButton
									className="component-link tbar-link"
									disabled={disabled}
									displayType="unstyled"
									onClick={resetFiltersValue}
								>
									{Liferay.Language.get('clear')}
								</ClayButton>
							</div>
						</li>
					</ul>
				</nav>
			</div>
		</div>
	) : null;
}

ActiveFiltersBar.propTypes = {
	dataLoading: PropTypes.bool,
	disabled: PropTypes.bool,
	total: PropTypes.number,
};

export default ActiveFiltersBar;
