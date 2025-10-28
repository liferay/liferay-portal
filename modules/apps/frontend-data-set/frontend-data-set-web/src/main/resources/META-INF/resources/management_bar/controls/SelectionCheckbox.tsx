/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';

interface SelectionCheckboxProps {
	handleCheckboxClick: () => void;
	items: Array<any>;
	selectedItemsValue: any;
}

const SelectionCheckbox = ({
	handleCheckboxClick,
	items,
	selectedItemsValue,
}: SelectionCheckboxProps) => {
	const {allItemsSelectedActive} = useContext(FrontendDataSetContext);
	const label = selectedItemsValue.length
		? sub(
				Liferay.Language.get(
					'clear-selection.-there-are-currently-x-of-x-x-selected'
				),
				selectedItemsValue.length.toString(),
				items.length.toString(),
				Liferay.Language.get('items')
			)
		: sub(
				Liferay.Language.get('select-all-x-on-the-page'),
				Liferay.Language.get('items')
			);

	return (
		<>
			<ClayCheckbox
				aria-labelledby="itemsSelectorLabel"
				checked={allItemsSelectedActive || !!selectedItemsValue.length}
				indeterminate={
					!!selectedItemsValue.length &&
					items.length !== selectedItemsValue.length
				}
				name="items-selector"
				onChange={handleCheckboxClick}
				title={
					selectedItemsValue.length
						? Liferay.Language.get('clear-selection')
						: Liferay.Language.get('select-items')
				}
			/>

			<span className="sr-only" id="itemsSelectorLabel">
				{label}
			</span>
		</>
	);
};

export default SelectionCheckbox;
