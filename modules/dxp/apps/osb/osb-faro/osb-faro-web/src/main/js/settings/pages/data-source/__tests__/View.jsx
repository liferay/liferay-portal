import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {CredentialTypes} from 'shared/util/constants';
import {DataSource, User} from 'shared/util/records';
import {MemoryRouter} from 'react-router';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {View} from '../View';
import {waitForLoading, waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter
			initialEntries={[
				'/workspace/32719/settings/data-source/450553575308493949'
			]}
		>
			<View
				currentUser={data.getImmutableMock(User, data.mockUser)}
				groupId='23'
				id='24'
				{...props}
			/>
		</MemoryRouter>
	</Provider>
);

describe('View', () => {
	it('should render a CSV data-source page', async () => {
		const {container} = render(
			<DefaultComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockCSVDataSource
				)}
			/>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render a Liferay data-source page', () => {
		const {container} = render(
			<DefaultComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockLiferayDataSource,
					'123',
					{
						credentials: {
							type: CredentialTypes.Token
						}
					}
				)}
			/>
		);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should render a Liferay data-source old page', () => {
		const {container} = render(
			<DefaultComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockLiferayDataSource
				)}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render a Salesforce data-source page', async () => {
		const {container} = render(
			<DefaultComponent
				dataSource={data.getImmutableMock(
					DataSource,
					data.mockSalesforceDataSource
				)}
			/>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
