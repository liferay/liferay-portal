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
import {Rule} from '../../../src/main/resources/META-INF/resources/js/types';

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
		],
		label: 'User',
	},
];

const RULES: Rule[] = [
	{attribute: 'age', id: 'rule-age', operator: 'gt', value: '18'},
];

function renderConditionsPanel({
	dispatch = jest.fn(),
	rules = [] as Rule[],
} = {}) {
	render(
		<DragAndDropProvider backend={HTML5Backend}>
			<ScreenReaderAnnouncerContextProvider>
				<ConditionsPanel
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					conjunction="AND"
					dispatch={dispatch}
					rules={rules}
				/>
			</ScreenReaderAnnouncerContextProvider>
		</DragAndDropProvider>
	);

	return {dispatch};
}

describe('ConditionsPanel', () => {
	it('shows the empty state when there are no rules', () => {
		renderConditionsPanel();

		expect(screen.getByText('no-criteria-yet')).toBeTruthy();
	});

	it('renders the given rules', () => {
		renderConditionsPanel({rules: RULES});

		expect(screen.getByText('Age')).toBeTruthy();
		expect(screen.getByText('is-greater-than')).toBeTruthy();
	});

	it('dispatches a duplicate action', async () => {
		const {dispatch} = renderConditionsPanel({rules: RULES});

		await userEvent.click(screen.getByLabelText('duplicate'));

		expect(dispatch).toHaveBeenCalledWith({
			index: 0,
			type: 'DUPLICATE_RULE',
		});
	});
});
