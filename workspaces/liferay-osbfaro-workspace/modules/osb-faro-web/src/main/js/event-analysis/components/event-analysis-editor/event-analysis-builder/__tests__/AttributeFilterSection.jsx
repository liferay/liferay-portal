import AttributeFilterSection from '../AttributeFilterSection';
import mockStore from 'test/mock-store';
import React from 'react';
import {AttributesContext} from '../../context/attributes';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {fireEvent, render, screen} from '@testing-library/react';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

const WrappedComponent = ({eventId, ...attributes}) => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/event-analysis']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider freezeResults={false}>
							<DndProvider backend={HTML5Backend}>
								<AttributesContext.Provider
									value={{
										attributes: {},
										filterOrder: [],
										filters: {},
										...attributes
									}}
								>
									<AttributeFilterSection eventId={eventId} />
								</AttributesContext.Provider>
							</DndProvider>
						</MockedProvider>
					}
					path={`${Routes.EVENT_ANALYSIS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('AttributeFilterSection', () => {
	jest.useFakeTimers();

	it('renders only the title without an event', () => {
		render(<WrappedComponent />);

		expect(
			screen.getByRole('heading', {name: /filter.by/i})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: /add.filter/i})
		).toBeNull();
	});

	it('renders only the title and the add button without filters', () => {
		const {container} = render(<WrappedComponent eventId='1' />);

		expect(
			screen.getByRole('heading', {name: /filter.by/i})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: /add.filter/i})
		).toBeInTheDocument();
		expect(container.querySelector('.attribute-list')).toBeNull();
	});

	it('renders the filters and focuses the section when one is removed', () => {
		const deleteFilter = jest.fn();

		const {container} = render(
			<WrappedComponent
				attributes={{
					123123: {
						dataType: 'STRING',
						displayName: 'Job Title',
						id: '123123',
						name: 'jobTitle'
					}
				}}
				deleteFilter={deleteFilter}
				eventId='1'
				filterOrder={['123123']}
				filters={{
					123123: {
						attributeId: '123123',
						dataType: 'STRING',
						id: '123123',
						operator: 'eq',
						type: 'event',
						values: ['Stuff']
					}
				}}
			/>
		);

		expect(
			container.querySelectorAll('.attribute-list .condition-chip')
		).toHaveLength(1);
		expect(container.querySelector('.condition-chip')).toHaveTextContent(
			/stuff/i
		);
		expect(
			screen.getByRole('button', {name: /drag.job title/i})
		).toBeInTheDocument();

		fireEvent.click(container.querySelector('.condition-chip-remove'));

		jest.runAllTimers();

		expect(deleteFilter).toHaveBeenCalledWith({id: '123123'});
		expect(document.activeElement).toBe(
			container.querySelector('.attribute-filter-section-root')
		);
	});
});
