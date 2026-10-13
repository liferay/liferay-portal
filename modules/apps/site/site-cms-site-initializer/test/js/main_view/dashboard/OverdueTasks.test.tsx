/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import {OverdueTasksCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/OverdueTasks';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

const LANGUAGE_KEYS: Record<string, string> = {
	'overdue-tasks': 'Overdue Tasks',
	'these-are-workflow-tasks-that-have-passed-their-due-date':
		'These are workflow tasks that have passed their due date.',
	'x-of-x-workflow-tasks': '{0} of {1} Workflow Tasks',
};

function renderCard({loadingStatistics = false} = {}) {
	return render(
		<GovernanceContext.Provider
			value={
				{
					loadingStatistics,
					statistics: {
						overdueWorkflowTasksCount: 11,
						workflowTasksCount: 113,
					},
				} as React.ContextType<typeof GovernanceContext>
			}
		>
			<OverdueTasksCard />
		</GovernanceContext.Provider>
	);
}

describe('[CMS Dashboard] OverdueTasks', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('shows the overdue tasks count and its share of all workflow tasks', () => {
		renderCard();

		expect(screen.getByText('Overdue Tasks')).toBeInTheDocument();
		expect(
			screen.getByText(
				'These are workflow tasks that have passed their due date.'
			)
		).toBeInTheDocument();
		expect(screen.getByText('11')).toBeInTheDocument();
		expect(
			screen.getByText('9.7% of 113 Workflow Tasks')
		).toBeInTheDocument();
	});

	it('shows no numbers while the statistics are loading', () => {
		renderCard({loadingStatistics: true});

		expect(screen.getByText('Overdue Tasks')).toBeInTheDocument();
		expect(screen.queryByText('11')).not.toBeInTheDocument();
		expect(
			screen.queryByText('9.7% of 113 Workflow Tasks')
		).not.toBeInTheDocument();
	});

	it('renders the card as a button for the list that opens below it', () => {
		renderCard();

		expect(
			screen.getByRole('button', {name: /Overdue Tasks/})
		).toBeInTheDocument();
	});

	it('is accessible', async () => {
		const {container} = renderCard();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
