import React from 'react';
import SourceCell from '../Source';
import {cleanup, render, screen} from '@testing-library/react';
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

	it.each([null, undefined, ''])(
		'should render a %p data source name as no data',
		dataSourceName => {
			render(
				<MemoryRouter>
					<SourceCell data={{dataSourceName}} groupId='123' />
				</MemoryRouter>
			);

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		}
	);

	it('should render a missing data source name as no data inside the link', () => {
		const {container} = render(
			<MemoryRouter>
				<SourceCell data={{dataSourceId: '456'}} groupId='123' />
			</MemoryRouter>
		);

		expect(container.querySelector('a')).toHaveTextContent('–');
		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});
});
