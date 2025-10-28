import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {AccountFieldMapping} from '../AccountFieldMapping';
import {DataSource, User} from 'shared/util/records';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const defaultProps = {
	currentUser: data.getImmutableMock(User, data.mockUser),
	dataSource: data.getImmutableMock(
		DataSource,
		data.mockSalesforceDataSource
	),
	groupId: '23',
	id: '27'
};

describe('AccountFieldMapping', () => {
	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<AccountFieldMapping {...defaultProps} />
				</StaticRouter>
			</Provider>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
