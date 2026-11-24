import * as data from 'test/data';
import AttributeFilterDropdown from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import {AttributesProvider} from '../../../context/attributes';
import {DISPLAY_NAME} from 'shared/util/pagination';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventAttributeDefinitionsReq} from 'test/graphql-data';
import {OrderByDirections} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {range} from 'lodash';

describe('AttributeFilterDropdown', () => {
	const defaultMocks = [
		mockEventAttributeDefinitionsReq(
			range(10).map(i =>
				data.mockEventAttributeDefinition(i, {
					__typename: 'EventAttributeDefinition'
				})
			),
			{
				eventDefinitionId: '3',
				keyword: '',
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
			<MemoryRouter
				initialEntries={['/workspace/123/456/event-analysis/123']}
			>
				<RouterRoutes>
					<Route
						element={
							<MockedProvider addTypename={false} mocks={mocks}>
								<AttributesProvider>
									<AttributeFilterDropdown
										eventId='3'
										trigger={
											<button data-testid='target'>
												{'click me'}
											</button>
										}
										uneditableIds={[]}
										{...props}
									/>
								</AttributesProvider>
							</MockedProvider>
						}
						path='/workspace/:groupId/:channelId/event-analysis/:id/*'
					/>
				</RouterRoutes>
			</MemoryRouter>
		</Provider>
	);

	it('render', async () => {
		const {container, getByTestId} = render(<WrappedComponent />);

		fireEvent.click(getByTestId('target'));

		await waitFor(() =>
			expect(document.body.querySelector('.loading-root')).toBeNull()
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(container).toMatchSnapshot();

		const dropdownMenu = document.body.querySelector(
			'.base-dropdown-menu-root'
		);

		expect(dropdownMenu).toBeTruthy();
		expect(dropdownMenu).toMatchSnapshot();

		expect(
			dropdownMenu.querySelectorAll('.dropdown-item.active')
		).toHaveLength(0);
	});

	it('render w/ disabled attributes', async () => {
		const {getByTestId} = render(
			<WrappedComponent disabledIds={['1', '2']} />
		);

		fireEvent.click(getByTestId('target'));

		await waitFor(() =>
			expect(document.body.querySelector('.loading-root')).toBeNull()
		);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(
			document.body.querySelectorAll('.dropdown-item.disabled')
		).toHaveLength(2);
	});
});
