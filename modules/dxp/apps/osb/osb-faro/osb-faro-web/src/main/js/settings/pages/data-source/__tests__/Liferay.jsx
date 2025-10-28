import * as data from 'test/data';
import LiferayDataSource from '../Liferay';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {DataSource, User} from 'shared/util/records';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({
		isAdmin: () => true
	})
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const defaultProps = {
	currentUser: data.getImmutableMock(User, data.mockUser),
	dataSource: data.getImmutableMock(DataSource, data.mockLiferayDataSource),
	groupId: '23',
	id: 'test'
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<StaticRouter>
			<LiferayDataSource {...defaultProps} {...props} />
		</StaticRouter>
	</Provider>
);

describe('LiferayDataSource', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
