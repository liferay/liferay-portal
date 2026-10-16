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
import {SLABreachesCard} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/SLABreaches';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

const LANGUAGE_KEYS: Record<string, string> = {
	'sla-breaches': 'SLA Breaches',
	'these-are-workflow-tasks-that-exceeded-the-time-allowed-by-their-service-level-agreement':
		'These are workflow tasks that exceeded the time allowed by their service level agreement.',
	'x-of-x-workflow-tasks': '{0} of {1} Workflow Tasks',
};

function renderCard({loadingStatistics = false} = {}) {
	return render(
		<GovernanceContext.Provider
			value={
				{
					loadingStatistics,
					statistics: {
						slaBreachedWorkflowTasksCount: 8,
						workflowTasksCount: 113,
					},
				} as React.ContextType<typeof GovernanceContext>
			}
		>
			<SLABreachesCard />
		</GovernanceContext.Provider>
	);
}

describe('[CMS Dashboard] SLABreaches', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('shows the SLA breaches count and its share of all workflow tasks', () => {
		renderCard();

		expect(screen.getByText('SLA Breaches')).toBeInTheDocument();
		expect(
			screen.getByText(
				'These are workflow tasks that exceeded the time allowed by their service level agreement.'
			)
		).toBeInTheDocument();
		expect(screen.getByText('8')).toBeInTheDocument();
		expect(
			screen.getByText('7.1% of 113 Workflow Tasks')
		).toBeInTheDocument();
	});

	it('shows no numbers while the statistics are loading', () => {
		renderCard({loadingStatistics: true});

		expect(screen.getByText('SLA Breaches')).toBeInTheDocument();
		expect(screen.queryByText('8')).not.toBeInTheDocument();
		expect(
			screen.queryByText('7.1% of 113 Workflow Tasks')
		).not.toBeInTheDocument();
	});

	it('renders the card as a button for the list that opens below it', () => {
		renderCard();

		expect(
			screen.getByRole('button', {name: /SLA Breaches/})
		).toBeInTheDocument();
	});

	it('is accessible', async () => {
		const {container} = renderCard();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
