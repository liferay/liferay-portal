import * as data from 'test/data';
import AttributeList from '../AttributeList';
import mockStore from 'test/mock-store';
import React from 'react';
import {AttributeTypes} from 'event-analysis/utils/types';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventAttributeDefinitionsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const DefaultComponent = ({
	groupId = '23',
	mocks = [
		mockEventAttributeDefinitionsReq(
			[
				data.mockEventAttributeDefinition(0, {
					__typename: 'EventAttributeDefinition',
					dataType: 'STRING'
				})
			],
			{
				keyword: '',
				page: 0,
				size: 2,
				sort: {column: 'name', type: 'ASC'},
				type: AttributeTypes.Local
			}
		)
	]
}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				`/workspace/${groupId}/456/settings/definitions/event-attributes/custom`
			]}
		>
			<RouterRoutes>
				<Route
					element={
						<MockedProvider addTypename={false} mocks={mocks}>
							<AttributeList />
						</MockedProvider>
					}
					path='/workspace/:groupId/:channelId/settings/definitions/event-attributes/custom/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('AttributeList', () => {
	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render Data Typecast column with a label', async () => {
		const {getByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved();

		expect(getByText('STRING').parentElement).toHaveClass('label-info');
	});
});
