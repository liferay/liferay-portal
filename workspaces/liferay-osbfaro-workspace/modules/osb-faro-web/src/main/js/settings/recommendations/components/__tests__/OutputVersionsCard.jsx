import * as data from 'test/data';
import OutputVersionsCard from '../OutputVersionsCard';
import React from 'react';
import {JobRunFrequencies, JobRunStatuses} from 'shared/util/constants';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationJobRunsReq} from 'test/graphql-data';
import {render} from '@testing-library/react';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

describe('OutputVersionsCard', () => {
	it('should render', async () => {
		const {container} = render(
			<MockedProvider
				mocks={[
					mockRecommendationJobRunsReq([
						data.mockRecommendationJobRun(0),
						data.mockRecommendationJobRun(1, {
							status: JobRunStatuses.Failed
						}),
						data.mockRecommendationJobRun(2, {
							status: JobRunStatuses.Published
						}),
						data.mockRecommendationJobRun(3, {
							status: JobRunStatuses.Running
						})
					])
				]}
			>
				<OutputVersionsCard
					jobId='321'
					nextRunDate={new Date()}
					runFrequency={JobRunFrequencies.Every14Days}
				/>
			</MockedProvider>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should render w/o "Next X" date', async () => {
		const {queryByText} = render(
			<MockedProvider
				mocks={[
					mockRecommendationJobRunsReq([
						data.mockRecommendationJobRun(0),
						data.mockRecommendationJobRun(1, {
							status: JobRunStatuses.Failed
						}),
						data.mockRecommendationJobRun(2, {
							status: JobRunStatuses.Published
						}),
						data.mockRecommendationJobRun(3, {
							status: JobRunStatuses.Running
						})
					])
				]}
			>
				<OutputVersionsCard
					runFrequency={JobRunFrequencies.Every14Days}
				/>
			</MockedProvider>
		);

		jest.runAllTimers();

		expect(queryByText(/Next/)).toBeNull();
	});

	it('should render a zero count and no data for a missing count', async () => {
		const {container} = render(
			<MockedProvider
				mocks={[
					mockRecommendationJobRunsReq([
						data.mockRecommendationJobRun(0, {
							context: [
								{key: 'userItemInteractionsDatasetCount', value: 0}
							]
						})
					])
				]}
			>
				<OutputVersionsCard
					jobId='321'
					runFrequency={JobRunFrequencies.Every14Days}
				/>
			</MockedProvider>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		const cells = container.querySelectorAll('tbody tr td');

		expect(cells[1].textContent).toBe('0');
		expect(cells[2].querySelector('.text-secondary').textContent).toBe(
			'–'
		);
	});

	it('should render no data for a count without value', async () => {
		const {container} = render(
			<MockedProvider
				mocks={[
					mockRecommendationJobRunsReq([
						data.mockRecommendationJobRun(0, {
							context: [
								{
									key: 'userItemInteractionsDatasetCount',
									value: null
								},
								{key: 'itemsDatasetCount', value: ''}
							]
						})
					])
				]}
			>
				<OutputVersionsCard
					jobId='321'
					runFrequency={JobRunFrequencies.Every14Days}
				/>
			</MockedProvider>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		const cells = container.querySelectorAll('tbody tr td');

		expect(cells[1].textContent).toBe('–');
		expect(cells[2].textContent).toBe('–');
	});
});
