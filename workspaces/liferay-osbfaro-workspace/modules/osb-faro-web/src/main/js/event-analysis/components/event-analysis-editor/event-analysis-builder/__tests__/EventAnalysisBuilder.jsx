import EventAnalysisBuilder from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter initialEntries={['/workspace/23/event-analysis']}>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider freezeResults={false}>
							<DndProvider backend={HTML5Backend}>
								<EventAnalysisBuilder {...props} />
							</DndProvider>
						</MockedProvider>
					}
					path={`${Routes.EVENT_ANALYSIS}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('Event Analysis Builder', () => {
	it('render', () => {
		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});

	it('render with filters & breakdowns', () => {
		const {container} = render(
			<WrappedComponent
				attributes={[
					{
						id: '321321',
						name: 'Article Title'
					},
					{
						id: '123123',
						name: 'Job Title'
					}
				]}
				breakdowns={[
					{
						attributeId: '321321',
						dataType: 'string',
						type: 'event'
					},
					{
						attributeId: '123123',
						dataType: 'string',
						type: 'event'
					}
				]}
				event={{
					id: '123123',
					name: 'Article Views',
					type: 'custom'
				}}
				filters={[
					{
						attributeId: '123123',
						operator: 'eq',
						value: ['Stuff']
					}
				]}
				onEventChange={jest.fn()}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
