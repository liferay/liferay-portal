import * as data from 'test/data';
import EventList from '../EventList';
import mockStore from 'test/mock-store';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventDefinitionsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = ({event, ...otherProps}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/23/settings/definitions/events/default?delta=1'
			]}
		>
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
								mockEventDefinitionsReq([
									data.mockEventDefinition(0, {
										__typename: 'EventDefinition',
										...event
									})
								])
							]}
						>
							<EventList groupId='23' {...otherProps} />
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_DEFINITIONS_EVENTS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('EventList', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render hide/unhide icon and not move to the right side border of the table when row is clicked', async () => {
		const {container} = render(
			<DefaultComponent
				event={{
					hidden: true
				}}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		const firstTr = container.querySelector('.clickable');

		fireEvent.click(firstTr);

		expect(firstTr.querySelector('.custom-control-input').checked).toBe(
			true
		);

		expect(firstTr.querySelector('.row-actions')).toBeTruthy();
	});
});
