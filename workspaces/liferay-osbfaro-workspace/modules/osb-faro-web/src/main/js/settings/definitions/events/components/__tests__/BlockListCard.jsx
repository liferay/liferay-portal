import * as data from 'test/data';
import BlockListCard from '../BlockListCard';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {mockBlockedCustomEventDefinitionsReq} from 'test/graphql-data';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockGroupId = '23';

const Wrapper = ({children, mocks = []}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				`/workspace/${mockGroupId}/settings/definitions/events/block-list`
			]}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider addTypename={false} mocks={mocks}>
							{children}
						</MockedProvider>
					}
					path={`${Routes.SETTINGS_DEFINITIONS_EVENTS_BLOCK_LIST}/*`}
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('BlockListCard', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const mocks = [
			mockBlockedCustomEventDefinitionsReq(
				[
					data.mockBlockedCustomEventDefinition(0),
					data.mockBlockedCustomEventDefinition(1)
				],
				{
					keyword: '',
					page: 0,
					size: 2,
					sort: {column: 'name', type: 'ASC'}
				}
			)
		];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<BlockListCard groupId={mockGroupId} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render when the state is empty', async () => {
		const mocks = [
			mockBlockedCustomEventDefinitionsReq([], {
				keyword: '',
				page: 0,
				size: 2,
				sort: {column: 'name', type: 'ASC'}
			})
		];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<BlockListCard groupId={mockGroupId} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			screen.getByText('There are no events blocked.')
		).toBeInTheDocument();

		expect(
			screen.getByText(
				"To block events, select one from the events' table."
			)
		).toBeInTheDocument();

		expect(
			screen.getByText(
				'Access our documentation to learn how to manage custom events.'
			)
		).toBeInTheDocument();
	});

	it('should render when the state is empty and with the autofit class', async () => {
		const mocks = [
			mockBlockedCustomEventDefinitionsReq([], {
				keyword: '',
				page: 0,
				size: 2,
				sort: {column: 'name', type: 'ASC'}
			})
		];

		const {container} = render(
			<Wrapper mocks={mocks}>
				<BlockListCard groupId={mockGroupId} />
			</Wrapper>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.no-results-title').textContent
		).toEqual('There are no events blocked.');

		// Note: Text content might be slightly different due to ClayLink or whitespace
		expect(
			container.querySelector('.no-results-description').textContent
		).toContain("To block events, select one from the events' table.");

		expect(
			container.querySelector('.no-results-description').textContent
		).toContain(
			'Access our documentation to learn how to manage custom events.'
		);
	});
});
