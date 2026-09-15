/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import ElementVariationFilterMenu from './ElementVariationFilterMenu';
import {
	Filter,
	FilterType,
	getFilterLabel,
	getFilterText,
} from './elementVariationFilters';

interface Props {
	audiences: Array<{label: string; value: string}>;
	filters: Filter[];
	onAddFilter: (filter: Filter) => void;
	onClearFilters: () => void;
	onClearSearch: () => void;
	onDeleteFilter: (filterType: FilterType) => void;
	resultsCount: number;
	searchTerm: string;
}

export default function AppliedFilters({
	audiences,
	filters,
	onAddFilter,
	onClearFilters,
	onClearSearch,
	onDeleteFilter,
	resultsCount,
	searchTerm,
}: Props) {
	return (
		<div className="border-bottom px-3 py-2 text-3 text-secondary">
			<div className="align-items-center d-flex justify-content-between">
				<span>
					{sub(
						resultsCount === 1
							? Liferay.Language.get('x-result-found-for-colon')
							: Liferay.Language.get('x-results-found-for-colon'),
						String(resultsCount)
					)}
				</span>

				<ClayButton
					className="flex-shrink-0 ml-2 p-0 text-3 text-weight-semi-bold"
					displayType="link"
					onClick={onClearFilters}
				>
					{Liferay.Language.get('clear')}
				</ClayButton>
			</div>

			<div className="c-gapy-2 d-flex flex-wrap mt-2">
				{searchTerm ? (
					<AppliedSearch
						onClearSearch={onClearSearch}
						searchTerm={searchTerm}
					/>
				) : null}

				{filters.map((filter) => (
					<AppliedFilter
						audiences={audiences}
						filter={filter}
						filters={filters}
						key={filter.type}
						onAddFilter={onAddFilter}
						onDeleteFilter={onDeleteFilter}
					/>
				))}
			</div>
		</div>
	);
}

interface AppliedSearchProps {
	onClearSearch: () => void;
	searchTerm: string;
}

function AppliedSearch({onClearSearch, searchTerm}: AppliedSearchProps) {
	return (
		<span
			className="label label-dismissible label-lg label-secondary p-2"
			role="group"
		>
			<ClayLabel.ItemExpand>
				<span className="text-break text-secondary text-weight-normal text-wrap">
					{`${Liferay.Language.get('search-colon')} `}

					<strong>{searchTerm}</strong>
				</span>
			</ClayLabel.ItemExpand>

			<ClayLabel.ItemAfter>
				<button
					aria-label={Liferay.Language.get('clear-search')}
					className="close"
					onClick={onClearSearch}
					title={Liferay.Language.get('clear-search')}
					type="button"
				>
					<ClayIcon symbol="times-small" />
				</button>
			</ClayLabel.ItemAfter>
		</span>
	);
}

interface AppliedFilterProps {
	audiences: Array<{label: string; value: string}>;
	filter: Filter;
	filters: Filter[];
	onAddFilter: (filter: Filter) => void;
	onDeleteFilter: (filterType: FilterType) => void;
}

function AppliedFilter({
	audiences,
	filter,
	filters,
	onAddFilter,
	onDeleteFilter,
}: AppliedFilterProps) {
	const [open, setOpen] = useState(false);

	const {hiddenCount, label} = getFilterText(filter, audiences);

	return (
		<span
			className="label label-dismissible label-lg label-secondary p-2"
			role="group"
		>
			<ClayLabel.ItemExpand>
				<ElementVariationFilterMenu
					audiences={audiences}
					className="d-inline-flex"
					defaultFilterType={filter.type}
					filters={filters}
					onActiveChange={setOpen}
					onAddFilter={onAddFilter}
					trigger={
						<ClayButton
							className="text-break text-secondary text-weight-normal text-wrap"
							displayType="unstyled"
						>
							<span className="inline-item inline-item-before">
								<ClayIcon
									symbol={open ? 'caret-top' : 'caret-bottom'}
								/>
							</span>

							<span>
								{`${getFilterLabel(filter.type)}: `}

								<strong>{label}</strong>
							</span>
						</ClayButton>
					}
				/>
			</ClayLabel.ItemExpand>

			{hiddenCount > 0 && (
				<ClayLabel.ItemAfter>
					<span
						aria-label={sub(
							Liferay.Language.get('and-x-more'),
							String(hiddenCount)
						)}
						className="badge badge-secondary"
					>
						{`+${hiddenCount}`}
					</span>
				</ClayLabel.ItemAfter>
			)}

			<ClayLabel.ItemAfter>
				<button
					aria-label={Liferay.Language.get('remove-filter')}
					className="close"
					onClick={() => onDeleteFilter(filter.type)}
					title={Liferay.Language.get('remove-filter')}
					type="button"
				>
					<ClayIcon symbol="times-small" />
				</button>
			</ClayLabel.ItemAfter>
		</span>
	);
}
