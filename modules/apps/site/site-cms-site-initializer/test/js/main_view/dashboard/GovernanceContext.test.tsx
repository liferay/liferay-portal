/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {useContext} from 'react';

import {
	GovernanceContext,
	GovernanceContextProvider,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService'
);

const mockedGovernanceService = GovernanceService as jest.Mocked<
	typeof GovernanceService
>;

function StatisticsConsumer() {
	const {
		groupIds,
		loadingStatistics,
		reloadStatistics,
		setSpace,
		statistics,
	} = useContext(GovernanceContext);

	return (
		<>
			<span data-testid="groupIds">{groupIds.join()}</span>

			<span data-testid="loadingStatistics">
				{String(loadingStatistics)}
			</span>

			<span data-testid="totalCount">{statistics?.totalCount}</span>

			<button onClick={reloadStatistics}>Reload</button>

			<button
				onClick={() =>
					setSpace({label: 'My Space', siteId: 999, value: '999'})
				}
			>
				Select Space
			</button>
		</>
	);
}

function mockStatistics(totalCount: number) {
	return {data: {totalCount}} as any;
}

describe('[CMS Dashboard] GovernanceContext', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('scopes the group ids to every administered space until one is selected', async () => {
		mockedGovernanceService.getAssetStatistics.mockResolvedValue(
			mockStatistics(1)
		);

		render(
			<GovernanceContextProvider
				allSpacesAdministered={false}
				spaceGroupIds={['111', '222']}
				spaceIds={['1', '2']}
			>
				<StatisticsConsumer />
			</GovernanceContextProvider>
		);

		expect(screen.getByTestId('groupIds')).toHaveTextContent('111,222');

		await userEvent.click(
			screen.getByRole('button', {name: 'Select Space'})
		);

		expect(screen.getByTestId('groupIds')).toHaveTextContent('999');
	});

	it('leaves the group ids empty when the user administers every space', async () => {
		mockedGovernanceService.getAssetStatistics.mockResolvedValue(
			mockStatistics(1)
		);

		render(
			<GovernanceContextProvider
				allSpacesAdministered={true}
				spaceGroupIds={['111', '222']}
				spaceIds={['1', '2']}
			>
				<StatisticsConsumer />
			</GovernanceContextProvider>
		);

		expect(screen.getByTestId('groupIds')).toBeEmptyDOMElement();

		await userEvent.click(
			screen.getByRole('button', {name: 'Select Space'})
		);

		expect(screen.getByTestId('groupIds')).toHaveTextContent('999');
	});

	it('keeps showing the statistics while reloading them', async () => {
		mockedGovernanceService.getAssetStatistics
			.mockResolvedValueOnce(mockStatistics(1))
			.mockReturnValueOnce(new Promise(() => {}));

		render(
			<GovernanceContextProvider
				allSpacesAdministered={false}
				spaceGroupIds={[]}
				spaceIds={[]}
			>
				<StatisticsConsumer />
			</GovernanceContextProvider>
		);

		expect(await screen.findByText('1')).toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'Reload'}));

		expect(screen.getByTestId('loadingStatistics')).toHaveTextContent(
			'false'
		);
		expect(screen.getByTestId('totalCount')).toHaveTextContent('1');
	});

	it('shows the loading state while loading the statistics of another space', async () => {
		mockedGovernanceService.getAssetStatistics
			.mockResolvedValueOnce(mockStatistics(1))
			.mockReturnValueOnce(new Promise(() => {}));

		render(
			<GovernanceContextProvider
				allSpacesAdministered={false}
				spaceGroupIds={[]}
				spaceIds={[]}
			>
				<StatisticsConsumer />
			</GovernanceContextProvider>
		);

		expect(await screen.findByText('1')).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'Select Space'})
		);

		expect(screen.getByTestId('loadingStatistics')).toHaveTextContent(
			'true'
		);
	});

	it('keeps the statistics of the selected space when an earlier reload resolves after them', async () => {
		let resolveReload: (value: any) => void = () => {};

		mockedGovernanceService.getAssetStatistics
			.mockResolvedValueOnce(mockStatistics(1))
			.mockReturnValueOnce(
				new Promise((resolve) => {
					resolveReload = resolve;
				})
			)
			.mockResolvedValueOnce(mockStatistics(2));

		render(
			<GovernanceContextProvider
				allSpacesAdministered={false}
				spaceGroupIds={[]}
				spaceIds={[]}
			>
				<StatisticsConsumer />
			</GovernanceContextProvider>
		);

		expect(await screen.findByText('1')).toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'Reload'}));

		await userEvent.click(
			screen.getByRole('button', {name: 'Select Space'})
		);

		expect(await screen.findByText('2')).toBeInTheDocument();

		await act(async () => {
			resolveReload(mockStatistics(99));
		});

		expect(screen.getByTestId('totalCount')).toHaveTextContent('2');
	});
});
