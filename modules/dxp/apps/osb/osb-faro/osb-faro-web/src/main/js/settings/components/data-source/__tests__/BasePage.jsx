import * as data from 'test/data';
import BaseDataSourcePage from '../BasePage';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {DataSource, User} from 'shared/util/records';
import {DataSourceStates, UserRoleNames} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router-dom';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

describe('BaseDataSourcePage', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<StaticRouter>
				<Provider store={mockStore()}>
					<BaseDataSourcePage
						currentUser={data.getImmutableMock(User, data.mockUser)}
						dataSource={data.getImmutableMock(
							DataSource,
							data.mockLiferayDataSource
						)}
						groupId='23'
						id='test'
					/>
				</Provider>
			</StaticRouter>
		);

		expect(container).toMatchSnapshot();
	});

	// TODO: remove "skip" on test below when Delete Property and Delete Data Source btns are back in the UI (LRAC-13389)

	it.skip('should render a delete button if showDelete is true', () => {
		const {queryByText} = render(
			<StaticRouter>
				<Provider store={mockStore()}>
					<BaseDataSourcePage
						currentUser={data.getImmutableMock(User, data.mockUser)}
						dataSource={data.getImmutableMock(
							DataSource,
							data.mockLiferayDataSource
						)}
						groupId='23'
						id='test'
						showDelete
					/>
				</Provider>
			</StaticRouter>
		);

		expect(queryByText('Delete Data Source')).toBeTruthy();
	});

	it('should NOT render a delete button if the user is not an admin level', () => {
		const {queryByText} = render(
			<StaticRouter>
				<Provider store={mockStore()}>
					<BaseDataSourcePage
						currentUser={data.getImmutableMock(
							User,
							data.mockUser,
							'23',
							{
								roleName: UserRoleNames.Member
							}
						)}
						dataSource={data.getImmutableMock(
							DataSource,
							data.mockLiferayDataSource
						)}
						groupId='23'
						id='test'
						showDelete
					/>
				</Provider>
			</StaticRouter>
		);

		expect(queryByText('Delete Data Source')).toBeNull();
	});

	it('should render with an UNDEFINED_ERROR message in the datasource status column', () => {
		const {queryByText} = render(
			<StaticRouter>
				<Provider store={mockStore()}>
					<BaseDataSourcePage
						currentUser={data.getImmutableMock(
							User,
							data.mockUser,
							'23',
							{
								roleName: UserRoleNames.Member
							}
						)}
						dataSource={data.getImmutableMock(
							DataSource,
							data.mockSalesforceDataSource,
							'test',
							{state: DataSourceStates.UndefinedError}
						)}
						groupId='23'
						id='test'
						showDelete
					/>
				</Provider>
			</StaticRouter>
		);

		expect(queryByText(/A server error occurred/)).toBeTruthy();
	});

	it('should render w/o datasource', () => {
		const {queryByText} = render(
			<StaticRouter>
				<Provider store={mockStore()}>
					<BaseDataSourcePage
						currentUser={data.getImmutableMock(User, data.mockUser)}
						groupId='23'
						id='test'
					/>
				</Provider>
			</StaticRouter>
		);

		expect(
			queryByText(
				'Data source has not been created. Please authorize and save to get started.'
			)
		).toBeTruthy();
	});
});
