/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	FILTERS,
	Selections,
	getOptionLabels,
	getSelectedValues,
} from './filters';

interface AppliedFiltersProps {
	onClearAll: () => void;
	onClearFilter: (filterId: string) => void;
	selections: Selections;
}

/**
 * What the element currently filters by, one chip per filter, so that the
 * whole selection stays readable while every panel below is collapsed.
 */
function AppliedFilters({
	onClearAll,
	onClearFilter,
	selections,
}: AppliedFiltersProps) {
	const appliedFilters = FILTERS.filter(
		({id}) => getSelectedValues(selections, id).length
	);

	if (!appliedFilters.length) {
		return null;
	}

	return (
		<div className="align-items-start d-flex justify-content-between mb-3">
			<div className="d-flex flex-wrap" style={{gap: '0.25rem'}}>
				{appliedFilters.map((filterDefinition) => {
					const {id, label} = filterDefinition;

					const labels = getOptionLabels(
						filterDefinition,
						getSelectedValues(selections, id)
					);

					return (
						<span className="label label-secondary" key={id}>
							<span className="label-item label-item-expand">
								{label}: {labels.join(', ')}
							</span>

							<span className="label-item label-item-after">
								<button
									aria-label={`Remove the ${label} filter`}
									className="close"
									onClick={() => onClearFilter(id)}
									type="button"
								>
									<span aria-hidden="true">&times;</span>
								</button>
							</span>
						</span>
					);
				})}
			</div>

			<button
				className="btn btn-unstyled flex-shrink-0 link ml-2"
				onClick={onClearAll}
				type="button"
			>
				Clear all
			</button>
		</div>
	);
}

export default AppliedFilters;
