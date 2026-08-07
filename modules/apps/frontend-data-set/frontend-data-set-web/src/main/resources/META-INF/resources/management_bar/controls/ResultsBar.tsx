/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';
import FilterResume from './filters/FilterResume';
import SearchResume from './filters/SearchResume';

function ResultsBar({
	dataLoading,
	disabled,
	showFilters = true,
	total,
}: {
	dataLoading: boolean;
	disabled: boolean;
	showFilters?: boolean;
	total: number;
}) {
	const {globalFDSState, onClearResultsBar, searchParam, searching} =
		useContext(FrontendDataSetContext);
	const searchActive = Boolean(searchParam?.trim());

	// Filters the data set does not show cannot be resumed either, but the
	// search resume and its clear action stay available.

	const activeFilters = showFilters
		? globalFDSState.filters.filter((filter) => filter.active)
		: [];

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
											filter={filter}
											key={filter.id}
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
									onClick={onClearResultsBar}
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

export default ResultsBar;
