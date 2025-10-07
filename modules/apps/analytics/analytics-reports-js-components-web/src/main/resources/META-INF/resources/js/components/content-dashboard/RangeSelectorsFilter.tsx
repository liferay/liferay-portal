/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {Context} from '../../Context';
import {
	RangeSelectors,
	RangeSelectorsDropdown,
} from '../RangeSelectorsDropdown';

const RangeSelectorsFilter = () => {
	const {changeRangeSelectorFilter, filters} = useContext(Context);

	const rangeSelectors = [
		RangeSelectors.Last7Days,
		RangeSelectors.Last28Days,
		RangeSelectors.Last30Days,
		RangeSelectors.Last90Days,
	];

	if (process.env.NODE_ENV === 'development') {
		rangeSelectors.push(RangeSelectors.Last24Hours);
	}

	return (
		<RangeSelectorsDropdown
			activeRangeSelector={filters.rangeSelector}
			availableRangeKeys={rangeSelectors}
			onChange={changeRangeSelectorFilter}
			size="xs"
		/>
	);
};

export {RangeSelectorsFilter};
