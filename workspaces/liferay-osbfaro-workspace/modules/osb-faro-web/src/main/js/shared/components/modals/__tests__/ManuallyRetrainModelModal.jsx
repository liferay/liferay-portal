import ManuallyRetrainModelModal from '../ManuallyRetrainModelModal';
import React from 'react';
import RecommendationJobRunsMonthlyStatisticsQuery from 'settings/recommendations/queries/RecommendationJobRunsMonthlyStatisticsQuery';
import {MockedProvider} from '@apollo/client/testing';
import {mockRecommendationJobRunsMonthlyStatistics} from 'test/data';
import {render} from '@testing-library/react';
import {useModal} from '@clayui/modal';
import {waitForLoadingToBeRemoved} from 'test/helpers';
import {waitForModal} from 'test/render-modal';

function mockRecommendationJobRunsMonthlyStatisticsReq() {
	return {
		request: {
			query: RecommendationJobRunsMonthlyStatisticsQuery,
			variables: {
				jobId: '321'
			}
		},
		result: {
			data: {
				jobRunsMonthlyStatistics: {
					__typename: 'JobRunsMonthlyStatistics',
					...mockRecommendationJobRunsMonthlyStatistics()
				}
			}
		}
	};
}

jest.unmock('react-dom');

const TestModal = props => {
	const {observer} = useModal({defaultOpen: true});

	return <ManuallyRetrainModelModal observer={observer} {...props} />;
};

describe('ManuallyRetrainModelModal', () => {
	it('should render', async () => {
		render(
			<MockedProvider
				mocks={[mockRecommendationJobRunsMonthlyStatisticsReq()]}
			>
				<TestModal job={{id: '321'}} />
			</MockedProvider>
		);

		await waitForModal();

		await waitForLoadingToBeRemoved(document.body);

		jest.runAllTimers();

		expect(document.body).toMatchSnapshot();
	});
});
