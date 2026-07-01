/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {
	RangeSelector,
	RangeSelectors,
	RangeSelectorsDropdown,
} from '@liferay/analytics-reports-js-components-web';
import React, {useState} from 'react';

import {
	SpaceOption,
	SpacesDropdown,
	initialSpace,
} from '../../common/SpacesDropdown';

const initialRangeSelector: RangeSelector = {
	rangeEnd: '',
	rangeKey: RangeSelectors.Last7Days,
	rangeStart: '',
};

export function Filters() {
	const [rangeSelector, setRangeSelector] =
		useState<RangeSelector>(initialRangeSelector);
	const [space, setSpace] = useState<SpaceOption>(initialSpace);

	return (
		<ClayLayout.Row className="mb-4">
			<ClayLayout.Col size={12}>
				<div className="d-flex">
					<SpacesDropdown
						className="mr-3"
						onSelectSpace={setSpace}
						selectedSpace={space}
					/>

					<RangeSelectorsDropdown
						activeRangeSelector={rangeSelector}
						availableRangeKeys={[
							RangeSelectors.Last24Hours,
							RangeSelectors.Last7Days,
							RangeSelectors.Last28Days,
							RangeSelectors.Last30Days,
							RangeSelectors.Last90Days,
						]}
						borderless={false}
						onChange={setRangeSelector}
					/>
				</div>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
}
