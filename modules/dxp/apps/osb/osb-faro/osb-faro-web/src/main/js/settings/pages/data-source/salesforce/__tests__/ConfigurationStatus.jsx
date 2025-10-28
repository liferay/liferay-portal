import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import SalesforceConfigurationStatus from '../ConfigurationStatus';
import {BrowserRouter} from 'react-router-dom';
import {DataSource} from 'shared/util/records';
import {fromJS, Map} from 'immutable';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const DEFAULT_DATA_SOURCE = new DataSource(
	data.mockLiferayDataSource(23, {
		connectionStatuses: fromJS({
			accounts: {
				modifiedDate: data.getTimestamp(),
				status: 1
			},
			contacts: {
				modifiedDate: data.getTimestamp(),
				status: 1
			},
			leads: {
				modifiedDate: data.getTimestamp(),
				status: 1
			}
		}),
		provider: new Map({
			accountsConfiguration: {},
			contactsConfiguration: {},
			leadsConfiguration: {},
			type: 'SALESFORCE'
		})
	})
);

const WrappedComponent = ({dataSource = DEFAULT_DATA_SOURCE}) => (
	<Provider store={mockStore()}>
		<BrowserRouter>
			<SalesforceConfigurationStatus
				dataSource={dataSource}
				groupId='23'
				id='23'
			/>
		</BrowserRouter>
	</Provider>
);

describe('SalesforceConfigurationStatus', () => {
	it('should render', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render as read-only if the user is not authorized to make changes', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => false}));

		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});

	// TODO: should be rewritten when new endpoint to replace connectionStatuses is added
	it('should render already configured items with the button text "pause" instead of "enable"', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container} = render(
			<WrappedComponent
				dataSource={
					new DataSource(
						data.mockLiferayDataSource(23, {
							connectionStatuses: fromJS({
								accounts: {
									modifiedDate: data.getTimestamp(),
									status: 2
								},
								contacts: {
									modifiedDate: data.getTimestamp(),
									status: 2
								},
								leads: {
									modifiedDate: data.getTimestamp(),
									status: 2
								}
							}),
							provider: new Map({
								accountsConfiguration: {},
								contactsConfiguration: {},
								leadsConfiguration: {},
								type: 'SALESFORCE'
							})
						})
					)
				}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	// TODO: should be rewritten when new endpoint to replace connectionStatuses is added
	it('should render as configuring items with a metric bar instead of a button', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container} = render(
			<WrappedComponent
				dataSource={
					new DataSource(
						data.mockLiferayDataSource(23, {
							connectionStatuses: fromJS({
								accounts: {
									count: 123,
									modifiedDate: data.getTimestamp(),
									status: 1,
									totalCount: 1234
								},
								contacts: {
									count: 573,
									modifiedDate: data.getTimestamp(),
									status: 1,
									totalCount: 5433
								},
								leads: {
									count: 573,
									modifiedDate: data.getTimestamp(),
									status: 1,
									totalCount: 5433
								}
							}),
							provider: new Map({
								type: 'SALESFORCE'
							})
						})
					)
				}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
