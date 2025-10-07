/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	RangeSelector,
	RangeSelectors,
	RangeSelectorsDropdown,
} from '@liferay/analytics-reports-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {BaseCard} from './BaseCard';
import {ContentAndFilesCard} from './ContentAndFilesCard';

export function ContentCard() {
	const [rangeSelector, setRangeSelector] = useState<RangeSelector>({
		rangeEnd: '',
		rangeKey: RangeSelectors.Last7Days,
		rangeStart: '',
	});

	return (
		<BaseCard
			Preferences={
				<>
					<RangeSelectorsDropdown
						activeRangeSelector={rangeSelector}
						availableRangeKeys={[
							RangeSelectors.Last24Hours,
							RangeSelectors.Last7Days,
							RangeSelectors.Last28Days,
							RangeSelectors.Last30Days,
							RangeSelectors.Last90Days,
							RangeSelectors.CustomRange,
						]}
						onChange={setRangeSelector}
					/>
				</>
			}
			description={Liferay.Language.get(
				'this-metric-calculates-the-total-amount-of-content-assets-created-in-your-spaces'
			)}
			title={Liferay.Language.get('content')}
		>
			<ContentAndFilesCard
				endpointURL="/o/analytics-cms-rest/v1.0/content-overview"
				rangeSelector={rangeSelector}
				title={(totalCount) =>
					sub(
						totalCount === 1
							? Liferay.Language.get('x-new-content-item')
							: Liferay.Language.get('x-new-content-items'),
						[totalCount]
					)
				}
			/>
		</BaseCard>
	);
}
