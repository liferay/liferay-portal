import * as data from 'test/data';
import LiferayDataSourceOld from '../LiferayOld';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource} from 'shared/util/records';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes, toRoute} from 'shared/util/router';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const defaultProps = {
	dataSource: data.getImmutableMock(DataSource, data.mockLiferayDataSource),
	groupId: '23',
	id: 'test'
};

describe('LiferayDataSourceOld', () => {
	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter
					location={toRoute(
						Routes.SETTINGS_DATA_SOURCE,
						defaultProps
					)}
				>
					<LiferayDataSourceOld {...defaultProps} />
				</StaticRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
