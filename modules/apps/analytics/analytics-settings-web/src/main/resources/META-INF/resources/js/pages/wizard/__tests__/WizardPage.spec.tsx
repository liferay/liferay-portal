/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';

import {
	AppContextProvider,
	EPageView,
	TData,
	initialState,
	useData,
} from '../../../index';
import {mockResponse} from '../../../utils/__tests__/helpers';
import {fetchPropertiesResponse} from '../../../utils/__tests__/mocks';
import WizardPage from '../WizardPage';

const responsePeopleMock = {
	syncAllAccounts: false,
	syncAllContacts: false,
	syncedAccountGroupIds: [],
	syncedOrganizationIds: [],
	syncedUserGroupIds: [],
};

const responseAttributesMock = {
	account: 25,
	order: 0,
	people: 43,
	product: 0,
};

const WrappedComponent: React.FC<
	{children?: React.ReactNode | undefined} & React.HTMLAttributes<HTMLElement>
> = ({children}) => {
	return (
		<AppContextProvider
			connected={false}
			liferayAnalyticsURL=""
			token=""
			wizardMode
		>
			{children}
		</AppContextProvider>
	);
};

describe('Wizard Page', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('render WizardPage without crashing', () => {
		const {container} = render(<WizardPage />);

		const wizardSheet = container.getElementsByClassName('sheet-lg');

		const wizardMultiStep =
			container.getElementsByClassName('multi-step-nav');

		expect(wizardSheet).toBeTruthy();

		expect(wizardMultiStep).toBeTruthy();
	});

	it('render titles and buttons of multiSteps without crashing', () => {
		const {container, getByRole} = render(<WizardPage />);

		const titleMultiSteps = container.querySelectorAll(
			'.multi-step-indicator-label'
		);

		const buttonConnect = getByRole('button', {name: /1/i});

		const buttonProperty = getByRole('button', {name: /2/i});

		const buttonPeople = getByRole('button', {name: /3/i});

		const buttonAttributes = getByRole('button', {name: /4/i});

		expect(titleMultiSteps[0]).toHaveTextContent('connect');

		expect(titleMultiSteps[1]).toHaveTextContent('property');

		expect(titleMultiSteps[2]).toHaveTextContent('people');

		expect(titleMultiSteps[3]).toHaveTextContent('attributes');

		expect(buttonConnect).toBeInTheDocument();

		expect(buttonProperty).toBeInTheDocument();

		expect(buttonPeople).toBeInTheDocument();

		expect(buttonAttributes).toBeInTheDocument();
	});

	it.skip('render each of all steps only with connect token', async () => {
		let data: TData = initialState;

		const ComponentWithData = () => {
			data = useData();

			return <WizardPage />;
		};

		render(
			<WrappedComponent>
				<ComponentWithData />
			</WrappedComponent>
		);

		// First Step: Connect

		fetch.mockReturnValue(mockResponse(fetchPropertiesResponse));

		expect(data.connected).toBeFalsy();
		expect(data.pageView).toEqual(EPageView.Wizard);
		expect(
			screen.getByTestId(/multi-step-item-active/i).textContent
		).toEqual('connect1');

		await act(async () => {
			await fireEvent.change(screen.getByTestId('input-token'), {
				target: {value: 'anything'},
			});
		});

		await act(async () => {
			await fireEvent.click(
				screen.getByRole('button', {name: /connect/i})
			);
		});

		fetch.mockResponse(JSON.stringify(responsePeopleMock));

		// Second Step: Property

		expect(data.connected).toBeTruthy();
		expect(data.pageView).toEqual(EPageView.Wizard);
		expect(
			screen.getByTestId(/multi-step-item-active/i).textContent
		).toEqual('property2');

		await act(async () => {
			await fireEvent.click(screen.getByRole('button', {name: /next/i}));
		});

		fetch.mockResponse(JSON.stringify(responsePeopleMock));

		// Third Step: People

		expect(data.connected).toBeTruthy();
		expect(data.pageView).toEqual(EPageView.Wizard);
		expect(
			screen.getByTestId(/multi-step-item-active/i).textContent
		).toEqual('people3');

		await act(async () => {
			await fireEvent.click(screen.getByRole('button', {name: /next/i}));
		});

		fetch.mockResponseOnce(JSON.stringify(responseAttributesMock));

		// Fourth Step: Attributes

		expect(data.connected).toBeTruthy();
		expect(data.pageView).toEqual(EPageView.Wizard);
		expect(
			screen.getByTestId(/multi-step-item-active/i).textContent
		).toEqual('attributes4');

		await act(async () => {
			await fireEvent.click(
				screen.getByRole('button', {name: /finish/i})
			);
		});

		expect(data.connected).toBeTruthy();
		expect(data.pageView).toEqual(EPageView.Default);
	});

	it('not possible go to any steps without connect token', () => {
		const {getByRole} = render(<WizardPage />);

		const buttonConnect = getByRole('button', {name: /1/i});

		const buttonProperty = getByRole('button', {name: /2/i});

		const buttonPeople = getByRole('button', {name: /3/i});

		const buttonAttributes = getByRole('button', {name: /4/i});

		expect(buttonConnect).not.toHaveClass('active');

		expect(buttonProperty).not.toHaveClass('active');

		expect(buttonPeople).not.toHaveClass('active');

		expect(buttonAttributes).not.toHaveClass('active');
	});
});
