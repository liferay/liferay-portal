/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import addRule from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/addRule';
import {
	CACHE_KEYS,
	disposeCache,
	initializeCache,
	setCacheItem,
} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/cache';
import RulesModal from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_rules/components/RulesModal';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve())
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/addRule',
	() => jest.fn()
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/useConditionValues',
	() => jest.fn(() => [{id: 'condition-id'}])
);

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openToast: jest.fn(),
}));

const renderComponent = ({rules = []} = {}) => {
	render(
		<StoreAPIContextProvider
			dispatch={() => Promise.resolve()}
			getState={() => ({
				fragmentEntryLinks: [],
				layoutData: {
					items: {
						itemId: {
							config: {
								name: 'containercillo',
							},
							itemId: 'item1',
							type: LAYOUT_DATA_ITEM_TYPES.container,
						},
					},
					pageRules: rules,
				},
			})}
		>
			<RulesModal onCloseModal={() => {}} />
		</StoreAPIContextProvider>
	);

	act(() => {
		jest.runAllTimers();
	});
};

const selectPickerOption = (pickerLabel, optionValue) => {
	fireEvent.click(screen.getByLabelText(pickerLabel));

	fireEvent.click(
		screen.getByText(optionValue, {
			selector: '[role="option"]',
		})
	);
};

describe('RulesSidebar', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	beforeAll(() => {
		jest.useFakeTimers();
	});

	beforeEach(() => {
		disposeCache();
		initializeCache();

		setCacheItem({
			data: [
				{screenName: 'user1', userId: 'userId1'},
				{screenName: 'user2', userId: 'userId2'},
			],
			key: CACHE_KEYS.users,
			status: 'saved',
		});
	});

	it('renders', async () => {
		renderComponent();

		expect(screen.getByText('add-action')).toBeInTheDocument();
		expect(screen.getByText('add-condition')).toBeInTheDocument();
	});

	it('does not allow saving an incomplete rule', async () => {
		renderComponent();

		fireEvent.click(screen.getByText('save'));

		expect(
			screen.getByText(
				'the-rule-is-incomplete.-please-check-that-the-conditions-and-actions-are-completed-before-saving'
			)
		).toBeInTheDocument();
	});

	it('does not allow saving an unnamed rule', async () => {
		renderComponent();

		fireEvent.change(screen.getByLabelText('rule-name'), {
			target: {value: ''},
		});

		fireEvent.click(screen.getByText('save'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
	});

	it('does allow completing a condition', async () => {
		renderComponent();

		selectPickerOption('select-item-for-the-condition', 'user');
		selectPickerOption('select-condition', 'is-the-user');
		selectPickerOption('select-user', 'user1');

		expect(
			screen.getByText('user1', {selector: '[role="combobox"]'})
		).toBeInTheDocument();
	});

	it('does allow completing a action', async () => {
		renderComponent();

		selectPickerOption('select-action', 'show');
		selectPickerOption('select-fragment', 'containercillo');

		expect(
			screen.getByText('containercillo', {selector: '[role="combobox"]'})
		).toBeInTheDocument();
	});

	it('allows saving a rule', async () => {
		renderComponent();

		selectPickerOption('select-item-for-the-condition', 'user');
		selectPickerOption('select-condition', 'is-the-user');
		selectPickerOption('select-user', 'user1');

		selectPickerOption('select-action', 'show');
		selectPickerOption('select-fragment', 'containercillo');

		fireEvent.click(screen.getByText('save'));

		expect(addRule).toBeCalledWith(
			expect.objectContaining({
				actions: [
					expect.objectContaining({
						itemId: 'item1',
						type: 'show',
					}),
				],
				conditions: [
					expect.objectContaining({
						field: 'user',
						options: {
							type: 'equal',
							value: 'userId1',
						},
						type: 'user',
					}),
				],
				name: 'rule',
			})
		);
	});

	it('removes selection in first condition when pressing delete condition', async () => {
		renderComponent();

		selectPickerOption('select-item-for-the-condition', 'user');
		selectPickerOption('select-condition', 'is-the-user');
		selectPickerOption('select-user', 'user1');

		act(() => {
			fireEvent.click(screen.getByTitle('delete-condition'));
		});

		expect(screen.queryByText('select-condition')).not.toBeInTheDocument();
		expect(screen.queryByText('select-user')).not.toBeInTheDocument();
	});

	it('removes selection in first action when pressing delete action', async () => {
		renderComponent();

		selectPickerOption('select-action', 'show');
		selectPickerOption('select-fragment', 'containercillo');

		act(() => {
			fireEvent.click(screen.getByTitle('delete-action'));
		});

		expect(
			screen.queryByText('select-item-for-the-action')
		).not.toBeInTheDocument();
		expect(screen.queryByText('select-fragment')).not.toBeInTheDocument();
	});
});
