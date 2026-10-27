import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import TrainingItemsCard from '../TrainingItemsCard';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationPageAssetsReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {fireEvent, render, screen} from '@testing-library/react';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

describe('TrainingItemsCard', () => {
	it('should render', async () => {
		const {container} = render(
			<MockedProvider
				mocks={[
					mockRecommendationPageAssetsReq(
						range(10).map(i => data.mockRecommendationPageAsset(i)),
						{size: 0}
					)
				]}
			>
				<Provider store={mockStore()}>
					<TrainingItemsCard
						itemFilters={[
							{
								name: 'includeFilter',
								value: 'url ~ .*custom-assets'
							}
						]}
					/>
				</Provider>
			</MockedProvider>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should open the matching pages of a training item', async () => {
		const {container} = render(
			<MockedProvider
				mocks={[
					mockRecommendationPageAssetsReq([], {size: 0}),
					mockRecommendationPageAssetsReq([], {size: 0})
				]}
			>
				<MemoryRouter>
				<Provider store={mockStore()}>
					<TrainingItemsCard
						itemFilters={[
							{
								id: 'filter-1',
								name: 'includeFilter',
								value: 'url ~ .*custom-assets'
							}
						]}
					/>
				</Provider>
				</MemoryRouter>
			</MockedProvider>
		);

		await waitForLoading(container);

		expect(screen.queryByRole('dialog')).toBeNull();

		fireEvent.click(screen.getAllByLabelText('Watch')[0]);

		expect(await screen.findByRole('dialog')).toBeInTheDocument();

		expect(
			await screen.findByRole('heading', {name: 'Matching Pages'})
		).toBeInTheDocument();
	});
});
