/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {
	RangeSelectors,
	RangeSelectorsDropdown,
} from '@liferay/analytics-reports-js-components-web';
import React, {useContext} from 'react';

import {SpacePicker} from '../../common/SpacePicker';
import {PerformanceContext} from '../PerformanceContext';

export function Filters() {
	const {range, setRange, setSpace, space} = useContext(PerformanceContext);

	return (
		<ClayLayout.Row className="mb-4">
			<ClayLayout.Col size={12}>
				<div className="d-flex">
					<SpacePicker
						className="mr-3"
						onSelectSpace={setSpace}
						selectedSpace={space}
					/>

					<RangeSelectorsDropdown
						activeRangeSelector={range}
						availableRangeKeys={[
							RangeSelectors.Last24Hours,
							RangeSelectors.Last7Days,
							RangeSelectors.Last28Days,
							RangeSelectors.Last30Days,
							RangeSelectors.Last90Days,
						]}
						borderless={false}
						onChange={setRange}
					/>
				</div>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
}
