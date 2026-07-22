import * as data from 'test/data';
import client from 'shared/apollo/client';
import EventAnalysisEdit from '../Edit';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {DndProvider} from 'react-dnd';
import {EventTypes} from 'event-analysis/utils/types';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {
	mockEventAnalysisReq,
	mockEventAttributeDefinitionsReq,
	mockEventDefinitionsReq,
	mockPreferenceReq,
	mockTimeRangeReq
} from 'test/graphql-data';
import {OrderByDirections} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '456',
		id: '1'
	})
}));

const WrappedComponent = () => (
	<Provider store={mockStore()}>
		<ApolloProvider client={client}>
			<MockedProvider
				mocks={[
					mockTimeRangeReq(),
					mockPreferenceReq(),
					mockEventAnalysisReq(),
					mockEventAttributeDefinitionsReq(
						range(10).map(i =>
							data.mockEventAttributeDefinition(i, {
								__typename: 'EventAttributeDefinition'
							})
						),
						{
							eventDefinitionId: '1',
							size: 200,
							sort: {
								column: DISPLAY_NAME,
								type: OrderByDirections.Ascending
							}
						}
					),
					mockEventDefinitionsReq(
						range(10).map(i =>
							data.mockEventDefinition(i, {
								__typename: 'EventDefinition'
							})
						),
						{
							eventType: EventTypes.All,
							hidden: false,
							page: 0,
							size: 200,
							sort: {
								column: DISPLAY_NAME,
								type: OrderByDirections.Ascending
							}
						}
					)
				]}
			>
				<MemoryRouter
					initialEntries={['/workspace/123/456/event-analysis/1']}
				>
					<RouterRoutes>
						<Route
							element={
								<DndProvider backend={HTML5Backend}>
									<EventAnalysisEdit />
								</DndProvider>
							}
							path={`${Routes.EVENT_ANALYSIS_EDIT}/*`}
						/>
					</RouterRoutes>
				</MemoryRouter>
			</MockedProvider>
		</ApolloProvider>
	</Provider>
);

describe.skip('Event Analysis Edit', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container, getByText} = render(<WrappedComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Download Reports')).toBeTruthy();

		expect(
			container.querySelector('.event-analysis-editor-root')
		).toBeInTheDocument();
		expect(
			container.querySelector('input.title-input')
		).toBeInTheDocument();
	});

	it('should render event analysis with data', async () => {
		const {container, getAllByText, getByText} = render(
			<WrappedComponent />
		);

		jest.runOnlyPendingTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('My First Event Analysis')).toBeTruthy();
		expect(
			container.querySelector('.dropdown-range-key-root button')
				.textContent
		).toEqual('Last 90 days');
		expect(container.querySelector('.event-list').textContent).toBe(
			'assetClicked'
		);
		expect(
			container.querySelectorAll(
				'.attribute-breakdown-section-root .attribute-list .dropdown'
			).length
		).toBe(2);
		expect(
			container.querySelectorAll(
				'.attribute-filter-section-root .attribute-list .dropdown'
			).length
		).toBe(1);
		expect(
			container.querySelector('.compare-to-previous-checkbox input')
				.checked
		).toBeTruthy();
		expect(getAllByText('displayName-0')).toBeTruthy();
	});

	it('should check if search autocomplete is working properly', async () => {
		const {container, queryByText} = render(<WrappedComponent />);

		jest.runOnlyPendingTimers();

		await waitForLoadingToBeRemoved(container);

		const addAttributeButton = container.querySelector(
			'.attribute-filter-section-root .add-attribute'
		);

		fireEvent.click(addAttributeButton);

		jest.runOnlyPendingTimers();

		const dropdown = document.querySelector(
			'.base-dropdown-menu-root.show'
		);

		await waitFor(() => expect(dropdown).toBeTruthy());

		const individualTab = document.querySelector(
			'[data-testid="INDIVIDUAL"] button'
		);

		fireEvent.click(individualTab);

		jest.runOnlyPendingTimers();

		expect(queryByText('jobTitle')).toBeTruthy();
		expect(queryByText('languageId')).toBeTruthy();
		expect(queryByText('Role')).toBeTruthy();
		expect(queryByText('Site Membership')).toBeTruthy();
		expect(queryByText('Team')).toBeTruthy();
		expect(queryByText('User Group')).toBeTruthy();

		const searchInput = document.querySelectorAll(
			'[placeholder="Search"]'
		)[1];

		fireEvent.change(searchInput, {
			target: {
				value: 'jobTitle'
			}
		});

		jest.runOnlyPendingTimers();

		expect(queryByText('jobTitle')).toBeTruthy();
		expect(queryByText('languageId')).not.toBeTruthy();
		expect(queryByText('Role')).not.toBeTruthy();
		expect(queryByText('Site Membership')).not.toBeTruthy();
		expect(queryByText('Team')).not.toBeTruthy();
		expect(queryByText('User Group')).not.toBeTruthy();
	});

	it('should enable the save button when name is changed', async () => {
		const {container, getByText} = render(<WrappedComponent />);

		jest.runOnlyPendingTimers();

		await waitForLoadingToBeRemoved(container);

		const inputName = container.querySelector('input.title-input');

		fireEvent.change(inputName, {
			target: {
				value: 'My First Event Analysis Updated'
			}
		});

		expect(getByText('My First Event Analysis Updated')).toBeTruthy();

		expect(getByText('Save Analysis')).toBeEnabled();
	});

	it('should enable the save button when a new breakdown is added', async () => {
		const {container, getByText} = render(<WrappedComponent />);

		jest.runOnlyPendingTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Save Analysis')).toBeDisabled();

		const addAttributeButton = container.querySelector(
			'.attribute-breakdown-section-root .add-attribute'
		);

		fireEvent.click(addAttributeButton);

		jest.runOnlyPendingTimers();

		const dropdown = document.querySelector(
			'.base-dropdown-menu-root.show'
		);

		await waitFor(() => expect(dropdown).toBeTruthy());

		const hrefAttributeButton = dropdown.querySelector(
			'.base-dropdown-list li:nth-child(3) button'
		);

		fireEvent.click(hrefAttributeButton);

		jest.runOnlyPendingTimers();

		expect(
			container.querySelectorAll(
				'.attribute-breakdown-section-root .attribute-list .dropdown'
			).length
		).toBe(3);
		expect(getByText('Save Analysis')).toBeEnabled();
	});

	it('should enable the save button when compareToPrevious checkbox is changed', async () => {
		const {container, getByText} = render(<WrappedComponent />);

		jest.runOnlyPendingTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Save Analysis')).toBeDisabled();

		const compareToPreviousCheckbox = container.querySelector(
			'.compare-to-previous-checkbox input'
		);

		expect(compareToPreviousCheckbox.checked).toBeTruthy();

		fireEvent.click(compareToPreviousCheckbox);

		expect(compareToPreviousCheckbox.checked).toBeFalsy();

		expect(getByText('Save Analysis')).toBeEnabled();
	});

	it('should enable the save button when range selector is changed', async () => {
		const {container, getByText} = render(<WrappedComponent />);

		jest.runOnlyPendingTimers();

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Save Analysis')).toBeDisabled();

		const rangeKeyButton = container.querySelector(
			'.dropdown-range-key-root > button'
		);

		expect(rangeKeyButton.textContent).toEqual('Last 90 days');

		fireEvent.click(rangeKeyButton);

		jest.runOnlyPendingTimers();

		const dropdown = document.querySelector('.dropdown-menu.show');

		await waitFor(() => expect(dropdown).toBeTruthy());

		fireEvent.click(dropdown.querySelector('ul > li button'));

		jest.runOnlyPendingTimers();

		expect(rangeKeyButton.textContent).toEqual('Last 24 hours');

		expect(getByText('Save Analysis')).toBeEnabled();
	});
});
