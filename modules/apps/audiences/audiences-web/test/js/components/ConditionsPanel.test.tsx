/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ScreenReaderAnnouncerContextProvider} from '@liferay/layout-js-components-web';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ConditionsPanel from '../../../src/main/resources/META-INF/resources/js/components/ConditionsPanel';

const AUDIENCES_CRITERIA_TYPES = [
	{
		audiencesCriterias: [
			{
				icon: 'user',
				key: 'age',
				label: 'Age',
				operators: ['eq', 'gt', 'lt'],
				options: [],
				type: 'number',
			},
			{
				icon: 'calendar',
				key: 'signupDate',
				label: 'Signup Date',
				operators: ['eq', 'gt', 'lt'],
				options: [],
				type: 'date',
			},
		],
		label: 'User',
	},
];

describe('ConditionsPanel', () => {
	it('renders and edits the conditions', async () => {
		render(
			<ConditionsPanel
				audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
			/>
		);

		expect(screen.getByText('conditions')).toBeTruthy();
		expect(screen.getByText('Age')).toBeTruthy();
		expect(screen.getByText('Signup Date')).toBeTruthy();

		expect(screen.getByText('is-greater-than')).toBeTruthy();
		expect(screen.getByText('is-after')).toBeTruthy();

		const [ageOperator] = screen.getAllByLabelText('operator');

		await userEvent.selectOptions(ageOperator, 'lt');

		expect((ageOperator as HTMLSelectElement).value).toBe('lt');
	});

	it('duplicates a condition and announces it', async () => {
		render(
			<ScreenReaderAnnouncerContextProvider>
				<ConditionsPanel
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
				/>
			</ScreenReaderAnnouncerContextProvider>
		);

		expect(screen.getAllByText('Age')).toHaveLength(1);

		const [ageDuplicate] = screen.getAllByLabelText('duplicate');

		await userEvent.click(ageDuplicate);

		expect(screen.getAllByText('Age')).toHaveLength(2);
		expect(screen.getByText('a-condition-was-duplicated')).toBeTruthy();
	});
});
