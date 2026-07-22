import React from 'react';
import SourceCell from '../Source';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

describe('SourceCell', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<MemoryRouter>
				<SourceCell
					data={{
						dataSourceId: '456',
						dataSourceName: 'Test Data Source'
					}}
					groupId='123'
				/>
			</MemoryRouter>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render without an href', () => {
		const {container} = render(
			<MemoryRouter>
				<SourceCell
					data={{
						dataSourceName: 'Test Data Source'
					}}
					groupId='123'
				/>
			</MemoryRouter>
		);

		expect(container.querySelector('a')).toBeNull();
	});
});
