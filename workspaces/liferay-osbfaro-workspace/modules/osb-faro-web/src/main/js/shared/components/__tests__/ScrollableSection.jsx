import React from 'react';
import ScrollableSection from '../ScrollableSection';
import {cleanup, render} from '@testing-library/react';

describe('ScrollableSection', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<ScrollableSection>
				<ul>
					<li>{'test'}</li>
					<li>{'test 1'}</li>
					<li>{'test 2'}</li>
				</ul>
			</ScrollableSection>
		);

		expect(container).toMatchSnapshot();
	});
});
