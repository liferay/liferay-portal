import DistributionChart from '../DistributionChart';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, render} from '@testing-library/react';
import {DistributionTab} from 'shared/util/records';
import {FieldContexts} from 'shared/util/constants';
import {Provider} from 'react-redux';

describe('DistributionCard DistributionChart', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DistributionChart
						distributionKey='individualsDashboard'
						fetchDistribution={() => Promise.reject()}
						selectedTab={
							new DistributionTab({
								context: FieldContexts.Demographics,
								propertyType: 'number',
								title: 'Tab 1'
							})
						}
						viewAllLink='test/:id'
					/>
				</BrowserRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders empty state via props with noResultsRenderer', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<DistributionChart
						distributionKey='individualsDashboard'
						fetchDistribution={() => Promise.reject()}
						noResultsRenderer={() => <div>{'empty state'}</div>}
						selectedTab={
							new DistributionTab({
								context: FieldContexts.Demographics,
								propertyType: 'number',
								title: 'Tab 1'
							})
						}
						viewAllLink='test/:id'
					/>
				</BrowserRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
