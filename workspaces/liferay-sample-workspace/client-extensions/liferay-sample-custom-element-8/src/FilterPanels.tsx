/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import {
	FILTERS,
	FilterDefinition,
	Selections,
	getSelectedValues,
} from './filters';

const Chevron = ({expanded}: {expanded: boolean}) => (
	<svg
		aria-hidden="true"
		fill="none"
		height="10"
		stroke="currentColor"
		strokeLinecap="round"
		strokeLinejoin="round"
		strokeWidth="2"
		style={{
			flexShrink: 0,
			transform: expanded ? 'rotate(90deg)' : undefined,
			transition: 'transform 150ms',
		}}
		viewBox="0 0 10 10"
		width="10"
	>
		<path d="M3 1l4 4-4 4" />
	</svg>
);

interface FilterPanelsProps {
	disabled: boolean;
	onToggleOption: (filterDefinition: FilterDefinition, value: string) => void;
	selections: Selections;
}

/**
 * One collapsible panel per filter, each holding the options it offers.
 * Selecting an option applies it right away: an "apply" button would only
 * stand between the user and a data set that already reloads on every change.
 */
function FilterPanels({
	disabled,
	onToggleOption,
	selections,
}: FilterPanelsProps) {
	const [expandedIds, setExpandedIds] = useState<Array<string>>([
		FILTERS[0].id,
	]);

	return (
		<div className="panel-group panel-group-flush">
			{FILTERS.map((filterDefinition) => {
				const {id, label, options} = filterDefinition;

				const expanded = expandedIds.includes(id);
				const selectedValues = getSelectedValues(selections, id);

				return (
					<div className="panel panel-unstyled" key={id}>
						<div className="p-0 panel-header">
							<button
								aria-controls={`${id}-options`}
								aria-expanded={expanded}
								className="align-items-center btn btn-unstyled d-flex justify-content-between py-2 w-100"
								onClick={() =>
									setExpandedIds((ids) =>
										expanded
											? ids.filter((each) => each !== id)
											: [...ids, id]
									)
								}
								type="button"
							>
								<span className="font-weight-semi-bold text-truncate">
									{label}

									{selectedValues.length
										? ` (${selectedValues.length})`
										: ''}
								</span>

								<Chevron expanded={expanded} />
							</button>
						</div>

						{expanded && (
							<div
								className="panel-body px-0 py-1"
								id={`${id}-options`}
							>
								{options.map((option) => (
									<div
										className="custom-checkbox custom-control"
										key={option.value}
									>
										<label>
											<input
												checked={selectedValues.includes(
													option.value
												)}
												className="custom-control-input"
												disabled={disabled}
												onChange={() =>
													onToggleOption(
														filterDefinition,
														option.value
													)
												}
												type="checkbox"
											/>

											<span className="custom-control-label">
												<span className="custom-control-label-text">
													{option.label}
												</span>
											</span>
										</label>
									</div>
								))}
							</div>
						)}
					</div>
				);
			})}
		</div>
	);
}

export default FilterPanels;
