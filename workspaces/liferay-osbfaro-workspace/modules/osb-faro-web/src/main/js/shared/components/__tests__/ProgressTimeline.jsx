import ProgressTimeline from '../ProgressTimeline';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

describe('ProgressTimeline', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<MemoryRouter>
				<ProgressTimeline
					activeIndex={1}
					items={[
						{
							title: 'This is a really long title for this step'
						},
						{title: 'Step 2'},
						{title: 'Step 3'},
						{title: 'Step 4'}
					]}
				/>
			</MemoryRouter>
		);

		expect(container).toMatchSnapshot();
	});
});
