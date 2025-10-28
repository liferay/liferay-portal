import * as data from 'test/data';
import BaseTabsPage from '../BaseTabsPage';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource, User} from 'shared/util/records';
import {DataSourceStates} from 'shared/util/constants';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

function getSalesforceDataSourceMock(id, config) {
	return data.getImmutableMock(
		DataSource,
		data.mockSalesforceDataSource,
		id,
		config
	);
}

const DefaultComponent = props => (
	<StaticRouter>
		<Provider store={mockStore()}>
			<BaseTabsPage
				addRoute={Routes.SETTINGS_DATA_SOURCE_EDIT}
				configurationRoute={Routes.SETTINGS_DATA_SOURCE_LIST}
				currentUser={new User(data.mockUser())}
				dataSource={getSalesforceDataSourceMock(1, {
					provider: {
						type: 'LIFERAY'
					}
				})}
				groupId='23'
				id='test'
				{...props}
			/>
		</Provider>
	</StaticRouter>
);

const allConfigured = {
	provider: {
		analyticsConfiguration: {sites: ['1']},
		contactsConfiguration: {enableAllContacts: true},
		type: 'LIFERAY'
	}
};

describe('BaseTabsPage', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render without a sticker in the configure datasource tab if all items are configured', () => {
		const {container} = render(
			<DefaultComponent
				dataSource={getSalesforceDataSourceMock(1, allConfigured)}
			/>
		);

		expect(container.querySelector('.badge')).toBeFalsy();
	});

	it('should render with the CONFIGURE_DATA_SOURCE tab as active', () => {
		const {getAllByText} = render(
			<DefaultComponent activeTabId='configureDataSource' />
		);

		expect(
			getAllByText('Configure Data Source')[1].parentElement
		).toHaveClass('active');
	});

	it('should render with the CONFIGURE_DATA_SOURCE tab enabled if state is ready', () => {
		const {getAllByText, getByText} = render(
			<DefaultComponent
				dataSource={getSalesforceDataSourceMock(0, {
					state: DataSourceStates.Ready
				})}
			/>
		);

		expect(
			getAllByText('Configure Data Source')[1].parentElement
		).not.toHaveClass('disabled');
		expect(getByText('Active')).toBeTruthy();
		expect(
			getByText(
				'All data coming from this data source is up to date. There are no errors to report.'
			)
		).toBeTruthy();
	});

	it('should render with the CONFIGURE_DATA_SOURCE tab disabled if state is not valid', () => {
		const {getAllByText, getByText} = render(
			<DefaultComponent
				dataSource={getSalesforceDataSourceMock(0, {
					state: DataSourceStates.CredentialsInvalid
				})}
			/>
		);

		expect(
			getAllByText('Configure Data Source')[1].parentElement
		).toHaveClass('disabled');
		expect(getByText('Invalid Credentials')).toBeTruthy();
		expect(
			getByText(
				'The authorization for this data source has expired. Please reauthorize the token in the oAuth tab.'
			)
		).toBeTruthy();
	});
});
