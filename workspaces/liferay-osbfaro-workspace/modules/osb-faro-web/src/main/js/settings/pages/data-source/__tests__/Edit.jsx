import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {DataSource} from 'shared/util/records';
import {Edit} from '../Edit';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: () => ({state: 'unblocked'}),
	useParams: () => ({
		groupId: '23'
	})
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn
}));

const csvProps = {
	groupId: '23',
	id: '23'
};

describe('Edit', () => {
	it('should render a CSV data-source page', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Edit
						{...csvProps}
						dataSource={new DataSource(data.mockCSVDataSource())}
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(getByText('Configure CSV')).toBeInTheDocument();
	});
});
