import EventSection from '../EventSection';
import mockStore from 'test/mock-store';
import React from 'react';
import {AttributesContext} from '../../context/attributes';
import {fireEvent, render} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

const DefaultComponent = ({attributesValue, ...props}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/event-analysis']}>
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
						>
							{attributesValue ? (
								<AttributesContext.Provider value={attributesValue}>
									<EventSection {...props} />
								</AttributesContext.Provider>
							) : (
								<EventSection {...props} />
							)}
						</MockedProvider>
					}
					path={`${Routes.EVENT_ANALYSIS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('EventSection', () => {
	it('render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('render with event', () => {
		const {container} = render(
			<DefaultComponent event={{name: 'View Article'}} />
		);

		expect(container).toMatchSnapshot();
	});

	it('clears the event and the attributes when the remove button is clicked', () => {
		const deleteAllAttributes = jest.fn();
		const onEventChange = jest.fn();

		const {container} = render(
			<DefaultComponent
				attributesValue={{
					attributes: {},
					breakdownOrder: [],
					breakdowns: {},
					changed: false,
					deleteAllAttributes,
					filterOrder: [],
					filters: {}
				}}
				event={{id: '1', name: 'View Article'}}
				onEventChange={onEventChange}
			/>
		);

		fireEvent.click(container.querySelector('.remove-button'));

		expect(onEventChange).toHaveBeenCalledTimes(1);
		expect(onEventChange).toHaveBeenCalledWith(null);
		expect(deleteAllAttributes).toHaveBeenCalledTimes(1);
	});
});
