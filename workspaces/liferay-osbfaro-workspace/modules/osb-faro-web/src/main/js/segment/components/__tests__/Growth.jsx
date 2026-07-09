import * as API from 'shared/api';
import * as data from 'test/data';
import React from 'react';
import SegmentGrowthWithList, {
	SegmentGrowthChart,
	SelectedPointInfo
} from '../Growth';
import {MemoryRouter, Route} from 'react-router-dom';
import {render, screen} from '@testing-library/react';
import {Routes} from 'shared/util/router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('SegmentGrowthWithList', () => {
	it('should render', async () => {
		const {container} = render(
			<MemoryRouter
				initialEntries={[
					'/workspace/23/123123/contacts/segments/321321/membership'
				]}
			>
				<Route path={Routes.CONTACTS_SEGMENT_MEMBERSHIP}>
					<SegmentGrowthWithList
						channelId='123'
						data={[
							{
								added: 1,
								modifiedDate: data.getTimestamp(),
								removed: 3
							}
						]}
						groupId='23'
						id='3'
						onPointSelect={jest.fn()}
					/>
				</Route>
			</MemoryRouter>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(screen.getByText('Known Members')).toBeInTheDocument();
	});

	it('requests only known members when the segment excludes anonymous users', async () => {
		const {container} = render(
			<MemoryRouter
				initialEntries={[
					'/workspace/23/123123/contacts/segments/321321/membership'
				]}
			>
				<Route path={Routes.CONTACTS_SEGMENT_MEMBERSHIP}>
					<SegmentGrowthWithList
						channelId='123'
						data={[
							{
								added: 1,
								modifiedDate: data.getTimestamp(),
								removed: 3
							}
						]}
						groupId='23'
						id='3'
						includeAnonymousUsers={false}
						onPointSelect={jest.fn()}
					/>
				</Route>
			</MemoryRouter>
		);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(API.individuals.search).toHaveBeenCalledWith(
			expect.objectContaining({individualTypes: ['KNOWN']})
		);
	});
});

describe('SegmentGrowthChart', () => {
	it('should render', () => {
		render(<SegmentGrowthChart data={[]} onPointSelect={jest.fn()} />);

		expect(
			screen.getByText('There is no data for segment membership.')
		).toBeInTheDocument();
	});
});

describe('SelectedPointInfo', () => {
	it('should render', () => {
		render(
			<SelectedPointInfo
				data={[
					{
						added: 1,
						modifiedDate: data.getTimestamp(),
						removed: 3
					}
				]}
				hasSelectedPoint
				onClearSelection={jest.fn()}
				selectedPoint={0}
			/>
		);

		expect(screen.getByText('Known Members')).toBeInTheDocument();
	});
});
