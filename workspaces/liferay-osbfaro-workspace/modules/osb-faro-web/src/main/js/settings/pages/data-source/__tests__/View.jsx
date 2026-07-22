import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {CredentialTypes} from 'shared/util/constants';
import {DataSource} from 'shared/util/records';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {useRequest} from 'shared/hooks/useRequest';
import {View} from '../View';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn()
}));

const DefaultComponent = ({dataSource}) => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={['/workspace/23/settings/data-source/24']}
		>
			<RouterRoutes>
				<Route
					element={
						<View dataSource={dataSource} groupId='23' id='24' />
					}
					path='/workspace/:groupId/settings/data-source/:id/*'
				/>
			</RouterRoutes>
		</MemoryRouter>
	</Provider>
);

describe('View', () => {
	beforeEach(() => {
		useRequest.mockReturnValue({
			data: {
				channelsCount: 0,
				groupsCount: 0,
				individualsCount: 0,
				items: [],
				total: 0
			},
			loading: false
		});

		API.dataSource.fetchMappingsLite.mockReturnValue(
			Promise.resolve([
				{
					mapping: {name: 'MappingName', values: ['mappingValue']},
					name: 'Test0',
					suggestions: [
						{name: 'MappingName', values: ['mappingValue']}
					],
					values: ['value']
				}
			])
		);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('should render a CSV data-source page', async () => {
		const dataSource = data.getImmutableMock(
			DataSource,
			data.mockCSVDataSource
		);

		const {container} = render(
			<DefaultComponent dataSource={dataSource} />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a Liferay data-source page', async () => {
		const dataSource = data.getImmutableMock(
			DataSource,
			data.mockLiferayDataSource,
			'123',
			{
				credentials: {
					type: CredentialTypes.Token
				}
			}
		);

		const {container} = render(
			<DefaultComponent dataSource={dataSource} />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a Salesforce data-source page', async () => {
		const dataSource = data.getImmutableMock(
			DataSource,
			data.mockSalesforceDataSource
		);

		const {container} = render(
			<DefaultComponent dataSource={dataSource} />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
