/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import {ContentAgeDistribution} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentAgeDistribution';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: unknown[]) =>
		values
			.flat()
			.reduce<string>(
				(result, value, index) =>
					result.replace(`{${index}}`, String(value)),
				template
			),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService',
	() => ({
		__esModule: true,
		...(jest.requireActual(
			'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService'
		) as object),
		default: {getContentAgeDistribution: jest.fn()},
	})
);

const ADDITIONAL_PROPS = {
	contentProgressFilter: 'contentProgressFilter',
} as any;

const FREQUENCIES = [53, 109, 87, 70, 31];

const LANGUAGE_KEYS: Record<string, string> = {
	'less-than-x-months': '< {0} Months',
	'more-than-x-years': '> {0} Years',
	'x-to-x-months': '{0} to {1} Months',
	'x-to-x-years': '{0} to {1} Years',
};

function mockContentAgeDistribution(
	frequencies: number[] | null,
	error: string | null = null
) {
	(
		GovernanceService.getContentAgeDistribution as jest.Mock
	).mockResolvedValue({data: frequencies, error});
}

function getGroupIds(space: {siteId?: number}) {
	return space.siteId ? [space.siteId] : [];
}

function renderContentAgeDistribution(
	space: {siteId?: number; value: string} = {value: 'all'}
) {
	return render(
		<GovernanceContext.Provider
			value={{groupIds: getGroupIds(space), space} as any}
		>
			<ContentAgeDistribution additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

describe('[CMS Dashboard] ContentAgeDistribution', () => {
	const {ResizeObserver} = window;

	afterAll(() => {
		window.ResizeObserver = ResizeObserver;
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it('requests the distribution by creation date for all spaces', async () => {
		mockContentAgeDistribution(FREQUENCIES);

		renderContentAgeDistribution();

		await waitFor(() =>
			expect(
				GovernanceService.getContentAgeDistribution
			).toHaveBeenCalledWith('contentProgressFilter', 'createDate', [])
		);
	});

	it('requests the distribution of the selected space', async () => {
		mockContentAgeDistribution(FREQUENCIES);

		renderContentAgeDistribution({siteId: 12345, value: '999'});

		await waitFor(() =>
			expect(
				GovernanceService.getContentAgeDistribution
			).toHaveBeenCalledWith('contentProgressFilter', 'createDate', [
				12345,
			])
		);
	});

	it('shows one bar per age range with its count', async () => {
		mockContentAgeDistribution(FREQUENCIES);

		renderContentAgeDistribution();

		const figure = await screen.findByRole('figure');

		expect(
			within(figure)
				.getAllByRole('listitem', {hidden: true})
				.map((item) => item.textContent)
		).toEqual([
			'< 3 Months15.1%',
			'3 to 6 Months31.1%',
			'6 to 12 Months24.9%',
			'1 to 2 Years20.0%',
			'> 2 Years8.9%',
		]);
	});

	it('requests the distribution by modified date when picked', async () => {
		mockContentAgeDistribution(FREQUENCIES);

		renderContentAgeDistribution();

		await screen.findByRole('figure');

		await userEvent.click(screen.getByRole('combobox', {name: 'date'}));

		await userEvent.click(
			await screen.findByRole('option', {name: 'modified-date'})
		);

		await waitFor(() =>
			expect(
				GovernanceService.getContentAgeDistribution
			).toHaveBeenLastCalledWith('contentProgressFilter', 'modified', [])
		);
	});

	it('shows the empty state when no range has content', async () => {
		mockContentAgeDistribution([0, 0, 0, 0, 0]);

		renderContentAgeDistribution();

		await waitFor(() =>
			expect(
				GovernanceService.getContentAgeDistribution
			).toHaveBeenCalled()
		);

		expect(
			await screen.findByText('nothing-to-analyze-yet')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'there-is-no-approved-content-in-the-selected-spaces'
			)
		).toBeInTheDocument();
		expect(screen.queryByRole('figure')).not.toBeInTheDocument();
	});

	it('shows the error when the request fails', async () => {
		mockContentAgeDistribution(null, 'Something went wrong');

		renderContentAgeDistribution();

		expect(
			await screen.findByText('Something went wrong')
		).toBeInTheDocument();
	});

	it('has no accessibility violations', async () => {
		mockContentAgeDistribution(FREQUENCIES);

		const {container} = renderContentAgeDistribution();

		await screen.findByRole('figure');

		await checkAccessibility({bestPractices: true, context: container});
	});
});
