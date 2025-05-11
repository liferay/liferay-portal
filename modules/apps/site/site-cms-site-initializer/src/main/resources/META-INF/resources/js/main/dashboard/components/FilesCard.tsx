/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {ActionsDropdown} from './ActionsDropdown';
import {BaseCard} from './BaseCard';
import {ContentAndFilesCard, TrendClassification} from './ContentAndFilesCard';
import {RangeSelectors, RangeSelectorsDropdown} from './RangeSelectorsDropdown';

// TODO: LPD-53333 - Remove it after implementing integration with backend

const MOCKED_VALUE = 999999;

export function FilesCard() {
	const [rangeSelector, setRangeSelector] = useState(
		RangeSelectors.Last7Days
	);
	const [action, setAction] = useState('');

	// TODO: LPD-53333 - Remove it after implementing integration with backend

	// eslint-disable-next-line no-console
	console.log({action, rangeSelector});

	return (
		<BaseCard
			Preferences={
				<>
					<RangeSelectorsDropdown
						activeRangeSelector={rangeSelector}
						className="mr-3"
						onChange={setRangeSelector}
					/>

					<ActionsDropdown
						items={[
							{
								icon: 'catalog',
								label: Liferay.Language.get('view-new-files'),
								value: 'viewNewFiles',
							},
						]}
						onChange={setAction}
					/>
				</>
			}
			description={Liferay.Language.get(
				'this-metric-calculates-the-total-amount-of-files-created-in-your-spaces'
			)}
			title={Liferay.Language.get('files')}
		>
			<ContentAndFilesCard
				categories={MOCKED_VALUE}
				tags={MOCKED_VALUE}
				title={sub(Liferay.Language.get('x-new-files'), [MOCKED_VALUE])}
				trend={{
					classification: TrendClassification.Positive,
					percentage: MOCKED_VALUE,
				}}
				vocabularies={MOCKED_VALUE}
			/>
		</BaseCard>
	);
}
