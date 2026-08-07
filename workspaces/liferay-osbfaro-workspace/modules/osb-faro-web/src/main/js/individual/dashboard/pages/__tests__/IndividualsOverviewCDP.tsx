import IndividualsOverviewCDP from '../IndividualsOverviewCDP';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useQuery} from '@apollo/client';
import {useRequest} from 'shared/hooks/useRequest';

jest.unmock('react-dom');

jest.mock('@apollo/client', () => ({
	...jest.requireActual('@apollo/client'),
	useQuery: jest.fn(),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn(),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '123',
		groupId: '456',
	}),
}));

jest.mock('../IndividualsList', () => ({
	__esModule: true,
	default: () => null,
}));

const mockedIndividualMetrics = {
	data: {
		individualMetric: {
			__typename: 'IndividualMetric',

			anonymousIndividualsMetric: {
				__typename: 'Metric',
				trend: {
					__typename: 'Trend',
					percentage: 11.123,
					trendClassification: 'POSITIVE',
				},
				value: 5123,
			},
			knownIndividualsMetric: {
				__typename: 'Metric',
				trend: {
					__typename: 'Trend',
					percentage: 28.12,
					trendClassification: 'NEGATIVE',
				},
				value: 1103,
			},
			totalIndividualsMetric: {
				__typename: 'Metric',
				trend: {
					__typename: 'Trend',
					percentage: 30.23,
					trendClassification: 'POSITIVE',
				},
				value: 2120,
			},
		},
	},
};

const renderIndividualsOverviewCDP = () =>
	render(
		<Provider store={mockStore()}>
			<BrowserRouter>
				<IndividualsOverviewCDP />
			</BrowserRouter>
		</Provider>
	);

describe('IndividualsOverviewCDP', () => {
	beforeEach(() => {
		(useRequest as jest.Mock).mockReturnValue({
			data: {total: 1},
			loading: false,
		});
		(useCurrentUser as jest.Mock).mockReturnValue({isAdmin: () => true});
		(useDataSources as jest.Mock).mockReturnValue({empty: false});
	});

	afterEach(() => {
		jest.resetAllMocks();
	});

	it('should render Individuals Metrics Cards', () => {
		(useQuery as jest.Mock).mockReturnValue({
			data: mockedIndividualMetrics.data,
			loading: false,
		});

		const {getByText} = renderIndividualsOverviewCDP();

		expect(getByText('Total Individuals')).toBeInTheDocument();
		expect(getByText('Known Individuals')).toBeInTheDocument();
		expect(getByText('Anonymous Individuals')).toBeInTheDocument();
	});

	it('should render the period-over-period trend only for the known and anonymous individuals cards', () => {
		(useQuery as jest.Mock).mockReturnValue({
			data: mockedIndividualMetrics.data,
			loading: false,
		});

		const {getAllByText, queryByText} = renderIndividualsOverviewCDP();

		expect(getAllByText(/vs\. Last 30 Days/)).toHaveLength(2);
		expect(queryByText('30.2%')).toBeNull();
		expect(getAllByText('28.1%')).toHaveLength(1);
		expect(getAllByText('11.1%')).toHaveLength(1);
	});

	it('should render a centered loader inside each metric card while metrics are loading', () => {
		(useQuery as jest.Mock).mockReturnValue({
			data: undefined,
			loading: true,
		});

		const {container, queryByText} = renderIndividualsOverviewCDP();

		expect(queryByText(/Individuals$/)).not.toBeInTheDocument();

		expect(container.querySelectorAll('.loading-root')).toHaveLength(3);
	});
});
