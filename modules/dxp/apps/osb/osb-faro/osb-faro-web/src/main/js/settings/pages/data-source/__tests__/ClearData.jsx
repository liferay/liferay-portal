import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {DataSource} from 'shared/util/records';
import {ClearData as DataSourceClearData} from '../ClearData';
import {Provider} from 'react-redux';
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
	dataSource: new DataSource(data.mockLiferayDataSource()),
	groupId: '23',
	id: '26'
};

describe('DataSourceClearData', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<DataSourceClearData {...defaultProps} />
				</StaticRouter>
			</Provider>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
