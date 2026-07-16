/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext, useState} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';
import {deactivateFilter} from '../../../utils/filters/deactivateFilter';
import {IBaseFilterState} from '../../../utils/types';
import Filter, {FILTER_IMPLEMENTATIONS} from './Filter';

function FilterResume({
	disabled,
	filter,
}: {
	disabled: boolean;
	filter: IBaseFilterState;
}) {
	const {id, label, type} = filter;

	const {globalFDSState, onFilterChange} = useContext(FrontendDataSetContext);

	const [open, setOpen] = useState(false);

	const filterImplementation = FILTER_IMPLEMENTATIONS[type];

	const {hiddenItemsCount, label: selectedItemsLabel} =
		filterImplementation.getSelectedItemsPreview?.(filter) ?? {
			hiddenItemsCount: 0,
			label: filterImplementation.getSelectedItemsLabel(filter),
		};

	const labelContent = (
		<>
			<span className="inline-item inline-item-before">
				<ClayIcon symbol={open ? 'caret-top' : 'caret-bottom'} />
			</span>

			<span className="label-section">
				<span>{`${label}: `}</span>

				<strong>{selectedItemsLabel}</strong>
			</span>
		</>
	);

	if (disabled) {
		return (
			<ClayButton
				className={classNames(
					'c-ml-2',
					'component-label',
					'filter-resume',
					'tbar-label'
				)}
				disabled
				displayType="secondary"
				size="sm"
			>
				{labelContent}
			</ClayButton>
		);
	}

	return (
		<span
			className={classNames(
				'c-ml-2',
				'component-label',
				'filter-resume',
				'label',
				'label-dismissible',
				'label-lg',
				'label-secondary',
				'tbar-label',
				open && 'active'
			)}
		>
			<ClayLabel.ItemExpand>
				<ClayDropDown
					active={open}
					className="d-inline-flex"
					onActiveChange={setOpen}
					trigger={
						<ClayButton
							className="filter-resume-trigger"
							displayType="unstyled"
						>
							{labelContent}
						</ClayButton>
					}
				>
					<li className="dropdown-subheader">{label}</li>

					<Filter {...filter} onClose={() => setOpen(false)} />
				</ClayDropDown>
			</ClayLabel.ItemExpand>

			{hiddenItemsCount > 0 && (
				<ClayLabel.ItemAfter>
					<span
						aria-label={sub(
							Liferay.Language.get('and-x-more'),
							hiddenItemsCount
						)}
						className="badge badge-secondary filter-resume-badge"
					>
						{`+${hiddenItemsCount}`}
					</span>
				</ClayLabel.ItemAfter>
			)}

			<ClayLabel.ItemAfter>
				<button
					aria-label={Liferay.Language.get('remove-filter')}
					className="close"
					onClick={() => {
						const filter = globalFDSState.filters.find(
							(filter) => filter.id === id
						);

						if (!filter) {
							return;
						}

						onFilterChange({
							changedFilter: deactivateFilter(filter),
						});
					}}
					title={Liferay.Language.get('remove-filter')}
					type="button"
				>
					<ClayIcon symbol="times-small" />
				</button>
			</ClayLabel.ItemAfter>
		</span>
	);
}

export default FilterResume;
