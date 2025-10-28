import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource, User} from 'shared/util/records';
import {Edit} from '../Edit';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes, toRoute} from 'shared/util/router';
import {StaticRouter} from 'react-router';
import {UserRoleNames} from 'shared/util/constants';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

jest.useRealTimers();

const csvProps = {
	groupId: '23',
	id: '23'
};

const salesforceProps = {
	currentUser: new User({roleName: UserRoleNames.Administrator}),
	dataSource: data.getImmutableMock(
		DataSource,
		data.mockSalesforceDataSource
	),
	groupId: '23',
	id: 'test'
};

describe('Edit', () => {
	it('should render a CSV data-source page', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<Edit
						{...csvProps}
						dataSource={new DataSource(data.mockCSVDataSource())}
					/>
				</StaticRouter>
			</Provider>
		);

		jest.runAllTimers();

		expect(getByText('Configure CSV')).toBeInTheDocument();
	});

	it('should render a Salesforce data-source page', async () => {
		const {container, getByText} = render(
			<Provider store={mockStore()}>
				<StaticRouter
					location={toRoute(
						Routes.SETTINGS_DATA_SOURCE,
						salesforceProps
					)}
				>
					<Edit {...salesforceProps} />
				</StaticRouter>
			</Provider>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Configure Salesforce')).toBeInTheDocument();
	});
});
