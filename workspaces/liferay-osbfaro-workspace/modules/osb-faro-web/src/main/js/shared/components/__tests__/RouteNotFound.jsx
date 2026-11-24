import React from 'react';
import RouteNotFound from '../RouteNotFound';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

describe('RouteNotFound', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<MemoryRouter>
				<RouteNotFound />
			</MemoryRouter>
		);
		expect(container).toBeTruthy();
	});
});
