import * as API from 'shared/api';
import * as data from 'test/data';
import * as NotificationAlertList from 'shared/components/NotificationAlertList';
import DataSourceList, {
	DataSourceName,
	disableRow,
	StatusRenderer
} from '../DataSourceList';
import mockStore, {mockStoreData} from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {DataSourceStates} from 'shared/util/constants';
import {MemoryRouter, Route} from 'react-router-dom';
import {Provider} from 'react-redux';
import {RemoteData} from 'shared/util/records';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

const mockNotificationAlertList = NotificationAlertList;

const defaultProps = {
	groupId: '23'
};

const DefaultComponent = ({queryString = '', ...otherProps}) => (
	<Provider store={mockStore(mockStoreData)}>
		<MemoryRouter
			initialEntries={[
				`/workspace/23/settings/data-source${queryString}`
			]}
		>
			<Route path={Routes.SETTINGS_DATA_SOURCE_LIST}>
				<DataSourceList {...defaultProps} {...otherProps} />
			</Route>
		</MemoryRouter>
	</Provider>
);

const DefaultUserComponent = () => (
	<Provider
		store={mockStore(
			mockStoreData.setIn(
				['currentUser'],
				new RemoteData({data: '24', loading: false})
			)
		)}
	>
		<MemoryRouter initialEntries={['/workspace/23/settings/data-source']}>
			<Route path={Routes.SETTINGS_DATA_SOURCE_LIST}>
				<DataSourceList {...defaultProps} />
			</Route>
		</MemoryRouter>
	</Provider>
);

describe('DataSourceList', () => {
	afterEach(() => {
		jest.runAllTimers();

		API.dataSource.search.mockClear();

		cleanup();
	});

	mockNotificationAlertList.useNotificationsAPI = jest.fn(() => ({
		data: [{}],
		loading: false,
		refetch: () => {}
	}));

	it('should render', async () => {
		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});

	it('should render with an empty state', async () => {
		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(
			<DefaultComponent queryString='?query=foo' />
		);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});

	it('should render with a message to connect datasources if there are none', async () => {
		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});

	it('should open a dropdown with "Liferay DXP" and "Salesforce" when clicking the "Add Data Source" button', async () => {
		const {container, getByText} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(getByText('Add Data Source'));

		expect(getByText('Liferay DXP')).toBeTruthy();
		expect(getByText('Salesforce')).toBeTruthy();
	});

	it('should render toast for one data source with invalid credentials', async () => {
		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					data.mockLiferayDataSource(1, {
						credentials: {
							oAuthOwner: {emailAddress: 'test@liferay.com'}
						},
						state: DataSourceStates.CredentialsInvalid
					})
				],
				total: 1
			})
		);

		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({
				items: [
					data.mockLiferayDataSource(1, {
						credentials: {
							oAuthOwner: {emailAddress: 'test@liferay.com'}
						},
						state: DataSourceStates.CredentialsInvalid
					})
				],
				total: 1
			})
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelectorAll('.embedded-alert-list-root')[1]
		).toMatchSnapshot();
	});

	it('should render without an "add data source" button if the user role is member', () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockMemberUser('24'))
		);

		const {queryByText} = render(<DefaultUserComponent />);

		jest.runAllTimers();

		expect(queryByText('Add Data Source')).toBeNull();
	});

	it('should render with a member-specific message to connect datasources if there are none', async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockMemberUser('24'))
		);

		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		API.dataSource.search.mockReturnValueOnce(
			Promise.resolve({items: [], total: 0})
		);

		const {container} = render(<DefaultUserComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(container.querySelector('.no-results-root')).toMatchSnapshot();
	});

	it("should render toast for one data source with invalid credentials for a member's view", async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockMemberUser('24'))
		);

		API.dataSource.search.mockReturnValue(
			Promise.resolve({
				items: [
					data.mockLiferayDataSource(1, {
						credentials: {
							oAuthOwner: {emailAddress: 'test@liferay.com'}
						},
						state: DataSourceStates.CredentialsInvalid
					})
				],
				total: 1
			})
		);

		const {container} = render(<DefaultUserComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelectorAll('.embedded-alert-list-root')[1]
		).toMatchSnapshot();
	});

	it('should render toast for multiple data sources with invalid credentials', async () => {
		API.dataSource.search.mockReturnValue(
			Promise.resolve({
				items: [
					data.mockLiferayDataSource(1, {
						credentials: {
							oAuthOwner: {emailAddress: 'test@liferay.com'}
						},
						state: DataSourceStates.CredentialsInvalid
					})
				],
				total: 2
			})
		);

		const {container} = render(<DefaultComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelectorAll('.embedded-alert-list-root')[1]
		).toMatchSnapshot();
	});

	it("should render toast for multiple data sources with invalid credentials for a member's view", async () => {
		API.user.fetchCurrentUser.mockReturnValueOnce(
			Promise.resolve(data.mockMemberUser('24'))
		);

		API.dataSource.search.mockReturnValue(
			Promise.resolve({
				items: [
					data.mockLiferayDataSource(1, {
						credentials: {
							oAuthOwner: {emailAddress: 'test@liferay.com'}
						},
						state: DataSourceStates.CredentialsInvalid
					})
				],
				total: 2
			})
		);

		const {container} = render(<DefaultUserComponent />);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelectorAll('.embedded-alert-list-root')[1]
		).toMatchSnapshot();
	});
});

describe('CellRenderers', () => {
	afterEach(cleanup);

	it('should show data-source as not configured', () => {
		const {getByText} = render(<StatusRenderer data={{state: null}} />);

		expect(getByText(/Not Configured/)).toBeTruthy();
	});

	it('should render as disabled if the datasource is in the process of being deleted', () => {
		const {container} = render(
			<DataSourceName
				data={{state: DataSourceStates.InProgressDeleting}}
			/>
		);

		expect(container.querySelector('a')).toBeNull();
	});
});

describe('disableRow', () => {
	it('should return true if datasource state is inProgressDeleting', () => {
		expect(disableRow({state: DataSourceStates.InProgressDeleting})).toBe(
			true
		);
	});

	it('should return false if datasource state is NOT inProgressDeleting', () => {
		expect(disableRow({state: DataSourceStates.Ready})).toBe(false);
	});
});
