/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import {ContributorConcentration} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContributorConcentration';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService',
	() => ({
		__esModule: true,
		default: {getContributors: jest.fn()},
	})
);

const ADDITIONAL_PROPS = {
	contentProgressFilter: 'contentProgressFilter',
} as any;

const CONTRIBUTORS = [
	{frequency: 34, name: 'Daniel Reyes'},
	{frequency: 25, name: null},
	{frequency: 23, name: 'Leo Brandt'},
	{frequency: 18, name: 'Ana Torres'},
	{frequency: 13, name: null},
	{frequency: 7, name: 'Iris Chen'},
	{frequency: 5, name: 'Nora Whitfield'},
];

function mockContributors(contributors: object[], totalCount: number) {
	(GovernanceService.getContributors as jest.Mock).mockResolvedValue({
		data: {contributors, totalCount},
		error: null,
	});
}

function getGroupIds(space: {siteId?: number}) {
	return space.siteId ? [space.siteId] : [];
}

function renderContributorConcentration(
	space: {siteId?: number; value: string} = {value: 'all'}
) {
	return render(
		<GovernanceContext.Provider
			value={{groupIds: getGroupIds(space), space} as any}
		>
			<ContributorConcentration additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

async function getBarLabels() {
	const bars = await screen.findAllByRole('img');

	return bars.map((bar) => bar.getAttribute('aria-label'));
}

describe('ContributorConcentration', () => {
	const {ResizeObserver} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserver;
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('groups deleted users and the remaining contributors ordered by value', async () => {
		mockContributors(CONTRIBUTORS, 150);

		renderContributorConcentration();

		expect(await getBarLabels()).toEqual([
			'deleted-users: 38',
			'Daniel Reyes: 34',
			'others: 25',
			'Leo Brandt: 23',
			'Ana Torres: 18',
			'Iris Chen: 7',
			'Nora Whitfield: 5',
		]);
	});

	it('omits the deleted user and others bars when there is nothing to group', async () => {
		mockContributors(
			[
				{frequency: 3, name: 'Daniel Reyes'},
				{frequency: 2, name: 'Leo Brandt'},
			],
			5
		);

		renderContributorConcentration();

		expect(await getBarLabels()).toEqual([
			'Daniel Reyes: 3',
			'Leo Brandt: 2',
		]);
	});

	it('scopes the request to the selected space', async () => {
		mockContributors(CONTRIBUTORS, 150);

		renderContributorConcentration({siteId: 123, value: '456'});

		await screen.findAllByRole('img');

		expect(GovernanceService.getContributors).toHaveBeenCalledWith(
			'contentProgressFilter',
			[123]
		);
	});

	it('toggles the percentages legend', async () => {
		mockContributors(
			[
				{frequency: 3, name: 'Daniel Reyes'},
				{frequency: 1, name: 'Leo Brandt'},
			],
			4
		);

		renderContributorConcentration();

		await screen.findAllByRole('img');

		expect(screen.queryByText('75.0%')).not.toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'show-percentages'})
		);

		expect(screen.getByText('75.0%')).toBeInTheDocument();
		expect(screen.getByText('25.0%')).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'hide-percentages'})
		);

		expect(screen.queryByText('75.0%')).not.toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'show-percentages'})
		).toBeInTheDocument();
	});

	it('shows the empty state when the scope has no content', async () => {
		mockContributors([], 0);

		renderContributorConcentration();

		expect(
			await screen.findByText('nothing-to-compare-yet')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'there-is-no-contributor-activity-to-measure-concentration-against'
			)
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'show-percentages'})
		).toBeDisabled();
	});

	it('has no accessibility violations', async () => {
		mockContributors(CONTRIBUTORS, 150);

		const {container} = renderContributorConcentration();

		await screen.findAllByRole('img');

		await checkAccessibility({bestPractices: true, context: container});
	});
});
