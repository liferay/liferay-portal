/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import deleteRule from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteRule';
import {
	CACHE_KEYS,
	disposeCache,
	initializeCache,
	setCacheItem,
} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/cache';
import RulesSidebar from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_rules/components/RulesSidebar';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableSegmentsEntries: {},
		},
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/deleteRule',
	() => jest.fn()
);

const renderComponent = ({rules = []} = {}) =>
	render(
		<StoreAPIContextProvider
			dispatch={() => Promise.resolve()}
			getState={() => ({
				fragmentEntryLinks: {
					fragmentEntryLink1: {
						name: 'Fragment 1',
					},
					fragmentEntryLink2: {
						fragmentEntryType: 'input',
						name: 'Fragment 2',
					},
					fragmentEntryLink3: {
						fragmentEntryType: 'input',
						name: 'Fragment 3',
					},
				},
				layoutData: {
					items: {
						formItem1: {
							config: {
								fragmentEntryLinkId: 'fragmentEntryLink2',
							},
							itemId: 'formItem1',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						formItem2: {
							config: {
								fragmentEntryLinkId: 'fragmentEntryLink3',
							},
							itemId: 'formItem2',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						item1: {
							config: {
								fragmentEntryLinkId: 'fragmentEntryLink1',
							},
							itemId: 'item1',
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
					},
					pageRules: rules,
				},
			})}
		>
			<RulesSidebar />
		</StoreAPIContextProvider>
	);

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

	it('shows empty state when there are no rules', async () => {
		await act(async () => {
			renderComponent();
		});

		expect(screen.getByText('no-rules-yet')).toBeInTheDocument();
	});

	it('shows list of rules when there is any', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [],
						conditions: [],
						id: 'rule-1',
						name: 'Rule 1',
					},
				],
			});
		});

		expect(screen.getByText('Rule 1')).toBeInTheDocument();
	});

	it('opens modal to create new rule when clicking that button', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [],
						conditions: [],
						id: 'rule',
						name: 'rule',
					},
					{
						actions: [],
						conditions: [],
						id: 'rule-1',
						name: 'rule 1',
					},
				],
			});
		});

		const addRuleButton = screen.getByText('new-rule');

		act(() => {
			fireEvent.click(addRuleButton);
		});

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const modalTitle = document.querySelector('.modal-title');

		expect(modalTitle.innerHTML).toBe('new-rule');

		expect(screen.getByLabelText('rule-name')).toHaveValue('rule 2');
	});

	it('opens modal to edit a rule when clicking that option', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [],
						conditions: [],
						id: 'rule-1',
						name: 'rule 1',
					},
				],
			});
		});

		const openOptionsButton = document.querySelector('.dropdown-toggle');

		fireEvent.click(openOptionsButton);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		fireEvent.click(screen.getByText('edit'));

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const modalTitle = document.querySelector('.modal-title');

		expect(modalTitle.innerHTML).toBe('edit-rule');

		expect(screen.getByLabelText('rule-name')).toHaveValue('rule 1');
	});

	it('calls delete rule thunk with correct rule id when clicking that option', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [],
						conditions: [],
						id: 'rule-1',
						name: 'rule 1',
					},
				],
			});
		});

		const openOptionsButton = document.querySelector('.dropdown-toggle');

		fireEvent.click(openOptionsButton);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		fireEvent.click(screen.getByText('delete'));

		expect(deleteRule).toBeCalledWith(
			expect.objectContaining({
				ruleId: 'rule-1',
			})
		);
	});

	it('shows user conditions and actions description', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [
							{
								id: 'action-id',
								itemId: 'item1',
								type: 'show',
							},
						],
						conditionType: 'all',
						conditions: [
							{
								field: 'user',
								id: 'condition-id',
								options: {
									type: 'equal',
									value: 'userId1',
								},
								type: 'user',
							},
						],
						id: 'rule-1',
						name: 'Rule 1',
					},
				],
			});
		});

		const rule = document.querySelector('li');

		expect(rule.textContent).toBe(
			'Rule 1ifuseris-the-useruser1showFragment 1'
		);
	});

	it('shows form fragment conditions and actions description', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [
							{
								id: 'action-id',
								itemId: 'formItem1',
								type: 'disable',
							},
						],
						conditionType: 'all',
						conditions: [
							{
								field: 'formItem2',
								id: 'condition-id',
								options: {
									type: 'equal',
									value: 'true',
								},
								type: 'form',
							},
						],
						id: 'rule-1',
						name: 'Rule 1',
					},
				],
			});
		});

		const rule = document.querySelector('li');

		expect(rule.textContent).toBe(
			'Rule 1ifFragment 3is-equal-totruedisableFragment 2'
		);
	});

	it('adds aria-label to the user rule with conditions and actions description', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [
							{
								id: 'action-id',
								itemId: 'item1',
								type: 'show',
							},
						],
						conditionType: 'all',
						conditions: [
							{
								field: 'user',
								id: 'condition-id',
								options: {
									type: 'equal',
									value: 'userId1',
								},
								type: 'user',
							},
						],
						id: 'rule-1',
						name: 'Rule 1',
					},
				],
			});
		});

		expect(
			screen.getByLabelText(
				'Rule 1: if user is-the-user user1 show fragment Fragment 1'
			)
		).toBeInTheDocument();
	});

	it('adds aria-label to the user form fragment rule with conditions and actions description', async () => {
		await act(async () => {
			renderComponent({
				rules: [
					{
						actions: [
							{
								id: 'action-id',
								itemId: 'formItem1',
								type: 'disable',
							},
						],
						conditionType: 'all',
						conditions: [
							{
								field: 'formItem2',
								id: 'condition-id',
								options: {
									type: 'equal',
									value: 'true',
								},
								type: 'form',
							},
						],
						id: 'rule-1',
						name: 'Rule 1',
					},
				],
			});
		});

		expect(
			screen.getByLabelText(
				'Rule 1: if Fragment 3 is-equal-to true disable fragment Fragment 2'
			)
		).toBeInTheDocument();
	});
});
