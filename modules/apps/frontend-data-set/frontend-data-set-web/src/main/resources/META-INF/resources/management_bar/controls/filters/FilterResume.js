/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import FrontendDataSetContext from '../../../FrontendDataSetContext';
import {deactivateFilter} from '../../../utils/filters/deactivateFilter';
import ViewsContext from '../../../views/ViewsContext';
import Filter from './Filter';

function FilterResume(props) {
	const {setSearching, updateFilters} = useContext(FrontendDataSetContext);
	const [{filters}, viewsDispatch] = useContext(ViewsContext);

	const [open, setOpen] = useState(false);

	const button = (
		<ClayButton
			className={classNames(
				'c-ml-2',
				'component-label',
				'filter-resume',
				'tbar-label',
				open && 'active'
			)}
			disabled={props.disabled}
			displayType="secondary"
			size="sm"
		>
			<span className="inline-item inline-item-before">
				<ClayIcon symbol={open ? 'caret-top' : 'caret-bottom'} />
			</span>

			<span className="label-section">
				{props.label}: <strong>{props.selectedItemsLabel}</strong>
			</span>
		</ClayButton>
	);

	const dropDownButtonGroup = (
		<ClayButton.Group>
			<ClayDropDown
				active={open}
				className="d-inline-flex"
				onActiveChange={setOpen}
				trigger={button}
			>
				<li className="dropdown-subheader">{props.label}</li>

				<Filter {...props} onClose={() => setOpen(false)} />
			</ClayDropDown>

			<ClayButton
				aria-label={Liferay.Language.get('remove-filter')}
				className="filter-resume-close"
				disabled={props.disabled}
				displayType="secondary"
				monospaced
				onClick={() => {
					setSearching(true);

					viewsDispatch(
						updateFilters(
							filters.map((filter) => ({
								...filter,
								...(filter.id === props.id
									? deactivateFilter(filter)
									: {}),
							}))
						)
					);
				}}
				size="sm"
				title={Liferay.Language.get('remove-filter')}
			>
				<ClayIcon symbol="times-small" />
			</ClayButton>
		</ClayButton.Group>
	);

	return props.disabled ? button : dropDownButtonGroup;
}

FilterResume.propTypes = {
	disabled: PropTypes.bool,
	id: PropTypes.string,
	label: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
	selectedItemsLabel: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.number,
	]),
};

export default FilterResume;
