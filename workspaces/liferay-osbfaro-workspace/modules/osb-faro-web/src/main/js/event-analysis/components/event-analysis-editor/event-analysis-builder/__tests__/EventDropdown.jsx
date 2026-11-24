import * as data from 'test/data';
import EventDropdown from '../EventDropdown';
import mockStore from 'test/mock-store';
import React from 'react';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventDefinitionsReq} from 'test/graphql-data';
import {OrderByDirections} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {waitForLoadingToBeRemoved} from 'test/helpers';

describe('EventDropdown', () => {
	const defaultMocks = [
		mockEventDefinitionsReq(
			range(10).map(i =>
				data.mockEventDefinition(i, {
					__typename: 'EventDefinition'
				})
			),
			{
				eventType: 'ALL',
				hidden: false,
				keyword: '',
				page: 0,
				size: 200,
				sort: {
					column: DISPLAY_NAME,
					type: OrderByDirections.Ascending
				}
			}
		)
	];

	const WrappedComponent = ({mocks = defaultMocks, ...props}) => (
		<Provider store={mockStore()}>
			<MemoryRouter>
				<MockedProvider
					cache={
						new InMemoryCache({
							addTypename: false,
							freezeResults: false
						})
					}
					mocks={mocks}
				>
					<EventDropdown
						onEventChange={jest.fn()}
						trigger={
							<button data-testid='target'>{'click me'}</button>
						}
						{...props}
					/>
				</MockedProvider>
			</MemoryRouter>
		</Provider>
	);

	it('render', async () => {
		const {getByTestId} = render(<WrappedComponent />);

		fireEvent.click(getByTestId('target'));

		await waitFor(() =>
			expect(document.body.querySelector('.dropdown-menu')).toBeTruthy()
		);

		await waitForLoadingToBeRemoved(document.body);

		expect(document.body.querySelector('.dropdown-menu')).toMatchSnapshot();

		const dropdownMenu = document.body.querySelector(
			'.base-dropdown-menu-root'
		);

		expect(
			dropdownMenu.querySelectorAll('.dropdown-item.active')
		).toHaveLength(0);
	});

});
