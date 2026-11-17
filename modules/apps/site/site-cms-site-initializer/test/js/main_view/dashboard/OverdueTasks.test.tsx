/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	FrontendDataSet,
	serializeFDSConfig,
} from '@liferay/frontend-data-set-web';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {FDS_EVENT_ACTION_PERFORMED} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import toDatePart from '../../../../src/main/resources/META-INF/resources/js/common/utils/toDatePart';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import {Operations} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/Operations';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	FrontendDataSet: jest.fn(() => null),
	findAction: (actions: Array<{data: {id: string}}>, id: string) =>
		actions.find((action) => action.data.id === id),
	getConfigParamName: (fdsName: string) => `${fdsName}_fdsConfig`,
	replaceTokens: (href: string, item: {embedded: {id: number}}) =>
		href.replace('{embedded.id}', String(item.embedded.id)),
	serializeFDSConfig: jest.fn(() => 'SERIALIZED_CONFIG'),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentProgress',
	() => ({ContentProgress: () => null})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContributorConcentration',
	() => ({ContributorConcentration: () => null})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/getDashboardAssetListFDSProps',
	() => ({__esModule: true, default: jest.fn(() => ({}))})
);

const ADDITIONAL_PROPS = {
	allSectionFDSName: 'allSection',
	contentProgressFilter: 'contentProgressFilter',
	fdsActionDropdownItems: [],
	tasksSectionFDSName: 'tasksSection',
	workflowTaskFDSActionDropdownItems: [
		{data: {id: 'workflow-transitions'}},
		{data: {id: 'actionLinkWorkflowTask'}, href: '/task?id={embedded.id}'},
		{data: {id: 'assignToMeWorkflowTask'}},
		{data: {id: 'assignToWorkflowTask'}},
		{data: {id: 'updateDueDateWorkflowTask'}},
	],
} as any;

const LANGUAGE_KEYS: Record<string, string> = {
	'all-workflow-tasks-are-within-their-due-date':
		'All workflow tasks are within their due date.',
	'no-overdue-tasks': 'No Overdue Tasks',
	'overdue-tasks': 'Overdue Tasks',
	'pending': 'Pending',
	'showing-x-of-x-items': 'Showing {0} of {1} Items',
	'these-are-workflow-tasks-that-have-passed-their-due-date':
		'These are workflow tasks that have passed their due date.',
	'view-all-x': 'View All {0}',
	'x-day-overdue': '{0} Day Overdue',
	'x-days-overdue': '{0} Days Overdue',
	'x-of-x-workflow-tasks': '{0} of {1} Workflow Tasks',
};

const MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

const NOW = Date.UTC(2026, 10, 16, 12, 0);

const reloadStatistics = jest.fn();

function renderOperations({
	loadingStatistics = false,
	space = {value: 'all'},
	spaceGroupIds = [111, 222],
}: {
	loadingStatistics?: boolean;
	space?: {label?: string; siteId?: number; value: string};
	spaceGroupIds?: number[];
} = {}) {
	return render(
		<GovernanceContext.Provider
			value={
				{
					groupIds: space.siteId ? [space.siteId] : [],
					loadingStatistics,
					reloadStatistics,
					selectedSpaceGroupIds: space.siteId
						? [space.siteId]
						: spaceGroupIds,
					space,
					statistics: {
						overdueWorkflowTasksCount: 11,
						workflowTasksCount: 113,
					},
				} as any
			}
		>
			<Operations additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

function getCard() {
	return screen.getByRole('button', {name: /^Overdue Tasks/});
}

async function expandList() {
	await userEvent.click(getCard());
}

function getFDSProps() {
	return (FrontendDataSet as jest.Mock).mock.calls.at(-1)[0];
}

function renderTask(embedded: object) {
	const TaskRenderer = getFDSProps().customRenderers.tableCell[0].component;

	render(
		<TaskRenderer
			actions={ADDITIONAL_PROPS.workflowTaskFDSActionDropdownItems}
			itemData={{
				embedded: {
					id: 42,
					label: 'Review',
					objectReviewed: {assetTitle: 'Spring Campaign'},
					...embedded,
				},
			}}
		/>
	);
}

describe('[CMS Dashboard] OverdueTasks', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(Date, 'now').mockReturnValue(NOW);

		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('shows the overdue tasks count and its share of all workflow tasks', () => {
		renderOperations();

		expect(getCard()).toHaveTextContent('11');
		expect(getCard()).toHaveTextContent('9.7% of 113 Workflow Tasks');
		expect(getCard()).toHaveTextContent(
			'These are workflow tasks that have passed their due date.'
		);
	});

	it('shows no numbers while the statistics are loading', () => {
		renderOperations({loadingStatistics: true});

		expect(getCard()).not.toHaveTextContent('11');
		expect(getCard()).not.toHaveTextContent('9.7% of 113 Workflow Tasks');
	});

	it('toggles the list of overdue tasks when clicking the card', async () => {
		renderOperations();

		expect(getCard()).toHaveAttribute('aria-expanded', 'false');

		await expandList();

		expect(getCard()).toHaveAttribute('aria-expanded', 'true');
		expect(
			screen.getByRole('region', {name: 'Overdue Tasks'})
		).toHaveTextContent('Showing 8 of 11 Items');

		await userEvent.click(getCard());

		expect(getCard()).toHaveAttribute('aria-expanded', 'false');
		expect(
			screen.queryByRole('region', {name: 'Overdue Tasks'})
		).not.toBeInTheDocument();
	});

	it('lists the overdue tasks of the selected space', async () => {
		renderOperations({
			space: {label: 'My Space', siteId: 12345, value: '999'},
		});

		await expandList();

		expect(
			new URL(getFDSProps().apiURL, 'http://localhost').searchParams.get(
				'filter'
			)
		).toContain('and scopeGroupId eq 12345');
	});

	it('lists only the overdue tasks of the spaces with all spaces selected', async () => {
		renderOperations();

		await expandList();

		expect(
			new URL(getFDSProps().apiURL, 'http://localhost').searchParams.get(
				'filter'
			)
		).toContain('and (scopeGroupId eq 111 or scopeGroupId eq 222)');
	});

	it('lists no overdue tasks without any space', async () => {
		renderOperations({spaceGroupIds: []});

		await expandList();

		expect(getFDSProps().apiURL).toBeUndefined();
		expect(getFDSProps().items).toEqual([]);
	});

	it('reloads the statistics when a task of the list transitions', async () => {
		const handlers: Record<string, (event?: {id?: string}) => void> = {};

		jest.spyOn(Liferay, 'on').mockImplementation(((
			name: string,
			handler: (event?: {id?: string}) => void
		) => {
			handlers[name] = handler;
		}) as any);

		renderOperations();

		await expandList();

		handlers[FDS_EVENT_ACTION_PERFORMED]({id: 'longStandingDrafts'});

		expect(reloadStatistics).not.toHaveBeenCalled();

		handlers[FDS_EVENT_ACTION_PERFORMED]({id: 'overdueTasks'});

		expect(reloadStatistics).toHaveBeenCalledTimes(1);
	});

	it('links each task to its workflow task with its name, assignee and days overdue', async () => {
		renderOperations();

		await expandList();

		renderTask({
			assigneePerson: {name: 'Iris Chen'},
			dateDue: new Date(NOW - 3 * MILLISECONDS_PER_DAY).toISOString(),
		});

		expect(
			screen.getByRole('link', {name: 'Spring Campaign'})
		).toHaveAttribute('href', '/task?id=42');
		expect(
			screen.getByText('Review · Iris Chen · 3 Days Overdue')
		).toBeInTheDocument();
	});

	it('shows a single day overdue in singular', async () => {
		renderOperations();

		await expandList();

		renderTask({
			assigneePerson: {name: 'Iris Chen'},
			dateDue: new Date(NOW - MILLISECONDS_PER_DAY).toISOString(),
		});

		expect(
			screen.getByText('Review · Iris Chen · 1 Day Overdue')
		).toBeInTheDocument();
	});

	it('shows the assignee roles of a task not assigned to a user', async () => {
		renderOperations();

		await expandList();

		renderTask({
			assigneeRoles: [{name: 'Reviewer'}, {name: 'Editor'}],
			dateDue: new Date(NOW - 3 * MILLISECONDS_PER_DAY).toISOString(),
		});

		expect(
			screen.getByText('Review · Reviewer, Editor · 3 Days Overdue')
		).toBeInTheDocument();
	});

	it('opens the Tasks page filtered by the pending tasks due up to today from View All', async () => {
		renderOperations();

		await expandList();

		expect(
			screen.getByRole('link', {name: 'View All Overdue Tasks'})
		).toHaveAttribute(
			'href',
			'/web/cms/tasks?tasksSection_fdsConfig=SERIALIZED_CONFIG'
		);

		const {filters} = (serializeFDSConfig as jest.Mock).mock.calls.at(
			-1
		)[0];

		const {day, month, year} = toDatePart(new Date());

		expect(filters).toEqual([
			{
				id: 'completed',
				selectedData: {
					exclude: false,
					selectedItems: [{label: 'Pending', value: 'false'}],
				},
			},
			{
				id: 'dueDate',
				selectedData: {
					exclude: false,
					from: null,
					to: expect.objectContaining({day, month, year}),
				},
			},
		]);
	});

	it('carries the selected space over to the Tasks page from View All', async () => {
		renderOperations({
			space: {label: 'My Space', siteId: 12345, value: '999'},
		});

		await expandList();

		const {filters} = (serializeFDSConfig as jest.Mock).mock.calls.at(
			-1
		)[0];

		expect(filters).toContainEqual({
			id: 'scopeGroupId',
			selectedData: {
				exclude: false,
				selectedItems: [{label: 'My Space', value: 12345}],
			},
		});
	});

	it('is accessible', async () => {
		const {container} = renderOperations();

		await expandList();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
