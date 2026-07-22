import ListComponent from '../ListComponent';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

describe('ListComponent', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<MemoryRouter>
				<ListComponent items={[]} total={0} />
			</MemoryRouter>
		);

		expect(container).toMatchSnapshot();
	});
});
