import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {DropdownRangeKey} from '../DropdownRangeKey';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq, mockTimeRangeReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {SEVEN_MONTHS} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

const WrapperComponent = ({children, retentionPeriodTimeRange}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider
							cache={
								new InMemoryCache({
									addTypename: false,
									freezeResults: false
								})
							}
							mocks={[
								mockTimeRangeReq(),
								mockPreferenceReq(retentionPeriodTimeRange)
							]}
						>
							{children}
						</MockedProvider>
					}
					path='/workspace/:groupId/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('DropdownRangeKey', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<WrapperComponent>
				<DropdownRangeKey />
			</WrapperComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a filter icon in the trigger button', async () => {
		const {container} = render(
			<WrapperComponent>
				<DropdownRangeKey />
			</WrapperComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.lexicon-icon-filter')
		).toBeInTheDocument();
	});

	it('should render the trigger button borderless by default', async () => {
		const {container} = render(
			<WrapperComponent>
				<DropdownRangeKey />
			</WrapperComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.button-root')).toHaveClass(
			'btn-outline-borderless'
		);
	});

	it('should render the trigger button bordered when the bordered prop is true', async () => {
		const {container} = render(
			<WrapperComponent>
				<DropdownRangeKey bordered />
			</WrapperComponent>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.button-root')).not.toHaveClass(
			'btn-outline-borderless'
		);
	});

	it('should display a message with retention period for 13 months on date picker', async () => {
		const {getByTestId, getByText} = render(
			<WrapperComponent>
				<DropdownRangeKey legacy={false} />
			</WrapperComponent>
		);

		await waitForLoadingToBeRemoved(document.body);

		fireEvent.click(getByText(/custom range/i));

		const previousMonthButton = getByTestId('previous-month');

		// Expect to disable prev button when month is 14

		for (let month = 1; month < 14; month++) {
			expect(previousMonthButton).not.toBeDisabled();

			if (month === 14) {
				expect(previousMonthButton).toBeDisabled();
			}

			fireEvent.click(previousMonthButton);
		}

		expect(
			getByText(
				"Dates prior to 13 months cannot be selected due to your workspace's data retention period."
			)
		).toBeInTheDocument();
	});

	it('should display a message with retention period for 7 months on date picker', async () => {
		const {getByTestId, getByText} = render(
			<WrapperComponent retentionPeriodTimeRange={SEVEN_MONTHS}>
				<DropdownRangeKey legacy={false} />
			</WrapperComponent>
		);

		await waitForLoadingToBeRemoved(document.body);

		fireEvent.click(getByText(/custom range/i));

		const previousMonthButton = getByTestId('previous-month');

		// Expect to disable prev button when month is 8

		for (let month = 1; month < 8; month++) {
			expect(previousMonthButton).not.toBeDisabled();

			if (month === 8) {
				expect(previousMonthButton).toBeDisabled();
			}

			fireEvent.click(previousMonthButton);
		}
		expect(
			getByText(
				"Dates prior to 7 months cannot be selected due to your workspace's data retention period."
			)
		).toBeInTheDocument();
	});
});
