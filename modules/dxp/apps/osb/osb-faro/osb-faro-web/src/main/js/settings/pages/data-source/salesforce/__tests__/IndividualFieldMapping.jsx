import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource} from 'shared/util/records';
import {IndividualFieldMapping} from '../IndividualFieldMapping';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router-dom';
import {waitForLoadingToBeRemoved} from 'test/helpers';

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
	id: '27'
};

describe('IndividualFieldMapping', () => {
	it('should render', async () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<IndividualFieldMapping {...defaultProps} />
				</StaticRouter>
			</Provider>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});
