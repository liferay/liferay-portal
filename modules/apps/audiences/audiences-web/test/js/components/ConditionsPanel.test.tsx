/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ScreenReaderAnnouncerContextProvider} from '@liferay/layout-js-components-web';

import '@testing-library/jest-dom';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import ConditionsPanel from '../../../src/main/resources/META-INF/resources/js/components/ConditionsPanel';
import {
	AudiencesCriteriaType,
	CriteriaNode,
} from '../../../src/main/resources/META-INF/resources/js/types';

const DragAndDropProvider = DndProvider as unknown as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

const AUDIENCES_CRITERIA_TYPES: AudiencesCriteriaType[] = [
	{
		audiencesCriterias: [
			{
				icon: 'user',
				inputType: 'text',
				key: 'age',
				label: 'Age',
				options: [],
				type: 'number',
			},
			{
				icon: 'user',
				inputType: 'text',
				key: 'city',
				label: 'City',
				options: [],
				type: 'string',
			},
		],
		key: 'user',
		label: 'User',
	},
];

const RULES: CriteriaNode[] = [
	{attribute: 'age', id: 'rule-age', operator: 'gt', value: '18'},
];

const RULES_WITH_REMOVED: CriteriaNode[] = [
	{attribute: 'removed', id: 'rule-removed', operator: 'eq', value: ''},
];

const NESTED_GROUP: CriteriaNode[] = [
	{
		conjunction: 'OR',
		id: 'group-nested',
		items: [
			{attribute: 'age', id: 'rule-age', operator: 'gt', value: '18'},
			{
				attribute: 'city',
				id: 'rule-city',
				operator: 'eq',
				value: 'Madrid',
			},
		],
	},
];

function renderConditionsPanel({
	dispatch = jest.fn(),
	items = [] as CriteriaNode[],
} = {}) {
	render(
		<DragAndDropProvider backend={HTML5Backend}>
			<ScreenReaderAnnouncerContextProvider>
				<ConditionsPanel
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
					dispatch={dispatch}
					root={{conjunction: 'AND', id: 'group-root', items}}
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
		renderConditionsPanel({items: RULES});

		expect(screen.getByText('Age')).toBeTruthy();
		expect(screen.getByText('is-greater-than')).toBeTruthy();
	});

	it('dispatches a duplicate action with the rule path', async () => {
		const {dispatch} = renderConditionsPanel({items: RULES});

		await userEvent.click(screen.getByLabelText('duplicate'));

		expect(dispatch).toHaveBeenCalledWith({
			path: [0],
			type: 'DUPLICATE_RULE',
		});
	});

	it('dispatches the conjunction', async () => {
		const {dispatch} = renderConditionsPanel({items: RULES});

		await userEvent.click(screen.getByLabelText('conjunction'));
		await userEvent.click(screen.getByRole('option', {name: 'any'}));

		expect(dispatch).toHaveBeenCalledWith({
			conjunction: 'OR',
			type: 'SET_CONJUNCTION',
		});
	});

	it('shows an error state for a removed criteria', () => {
		renderConditionsPanel({items: RULES_WITH_REMOVED});

		expect(
			screen.getByText('the-criteria-is-no-longer-available')
		).toBeTruthy();
	});

	it('renders a nested group with its own conjunction control', () => {
		renderConditionsPanel({items: NESTED_GROUP});

		expect(screen.getByRole('group')).toBeTruthy();
		expect(screen.getByText('Age')).toBeTruthy();
		expect(screen.getByText('City')).toBeTruthy();
		expect(screen.getAllByLabelText('conjunction')).toHaveLength(2);
	});

	it('dispatches a delete action for a rule nested in a group by path', async () => {
		const {dispatch} = renderConditionsPanel({items: NESTED_GROUP});

		const group = screen.getByRole('group');

		await userEvent.click(within(group).getAllByLabelText('delete')[0]);

		expect(dispatch).toHaveBeenCalledWith({
			path: [0, 0],
			type: 'DELETE_RULE',
		});
	});

	it('keeps only the current row in the tab order', () => {
		renderConditionsPanel({
			items: [
				{attribute: 'age', id: 'rule-age', operator: 'gt', value: '18'},
				{
					attribute: 'city',
					id: 'rule-city',
					operator: 'eq',
					value: 'Madrid',
				},
			],
		});

		const deleteButtons = screen.getAllByLabelText('delete');

		expect(deleteButtons[0]).toHaveAttribute('tabindex', '0');
		expect(deleteButtons[1]).toHaveAttribute('tabindex', '-1');
	});

	it('navigates into a nested group with arrow keys', async () => {
		renderConditionsPanel({
			items: [
				{attribute: 'age', id: 'rule-age', operator: 'gt', value: '18'},
				{
					conjunction: 'OR',
					id: 'group-nested',
					items: [
						{
							attribute: 'city',
							id: 'rule-city',
							operator: 'eq',
							value: 'Madrid',
						},
					],
				},
			],
		});

		const rows = screen.getAllByRole('menuitem');

		rows[0].focus();

		await userEvent.keyboard('{ArrowDown}');

		expect(rows[1]).toHaveFocus();
	});
});
