/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ScreenReaderAnnouncerContextProvider} from '@liferay/layout-js-components-web';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import ConditionsPanel from '../../../src/main/resources/META-INF/resources/js/components/ConditionsPanel';

const DragAndDropProvider = DndProvider as unknown as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

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

const JSON_WITH_RULES = JSON.stringify({
	conjunction: 'AND',
	rules: [{attribute: 'age', operator: 'gt', value: '18'}],
});

function renderConditionsPanel(json?: string) {
	return render(
		<DragAndDropProvider backend={HTML5Backend}>
			<ScreenReaderAnnouncerContextProvider>
				<ConditionsPanel
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					json={json}
					namespace="_test_"
				/>
			</ScreenReaderAnnouncerContextProvider>
		</DragAndDropProvider>
	);
}

describe('ConditionsPanel', () => {
	it('shows the empty state when there are no rules', () => {
		renderConditionsPanel();

		expect(screen.getByText('no-criteria-yet')).toBeTruthy();
	});

	it('loads the rules from the json prop', () => {
		renderConditionsPanel(JSON_WITH_RULES);

		expect(screen.getByText('Age')).toBeTruthy();
		expect(screen.getByText('is-greater-than')).toBeTruthy();
	});

	it('duplicates a condition and announces it', async () => {
		renderConditionsPanel(JSON_WITH_RULES);

		expect(screen.getAllByText('Age')).toHaveLength(1);

		const [ageDuplicate] = screen.getAllByLabelText('duplicate');

		await userEvent.click(ageDuplicate);

		expect(screen.getAllByText('Age')).toHaveLength(2);
		expect(screen.getByText('a-condition-was-duplicated')).toBeTruthy();
	});
});
