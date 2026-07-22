import * as data from 'test/data';
import InterestsCard, {InterestsList} from '../InterestsCard';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {mockSegment} from 'test/data';
import {SEGMENTS} from 'shared/util/router';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('InterestsCard', () => {
	afterEach(cleanup);

	it('should render', async () => {
		const {container} = render(
			<MemoryRouter>
				<InterestsCard
					entity={mockSegment(15)}
					groupId='23'
					type={SEGMENTS}
				/>
			</MemoryRouter>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(container).toMatchSnapshot();
	});
});

describe('InterestsList', () => {
	it('should render', () => {
		const {container} = render(
			<MemoryRouter>
				<InterestsList
					groupId='23'
					id='foo'
					interests={[data.mockInterestData()]}
					type={SEGMENTS}
				/>
			</MemoryRouter>
		);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});
});
