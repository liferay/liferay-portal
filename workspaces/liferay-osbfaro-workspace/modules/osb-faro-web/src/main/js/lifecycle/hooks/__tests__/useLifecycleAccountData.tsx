import * as API from 'shared/api';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {useDataSources} from 'shared/context/dataSources';
import {useLifecycleAccountData} from '../useLifecycleAccountData';
import {useRequest} from 'shared/hooks/useRequest';
import {AccountDataStatus} from 'lifecycle/utils/types';

jest.unmock('react-dom');

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

jest.mock('shared/context/dataSources', () => ({
	useDataSources: jest.fn(),
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '123', groupId: '23'}),
}));

const mockedUseDataSources = useDataSources as jest.Mock;
const mockedUseRequest = useRequest as jest.Mock;

const buildMetrics = (totalCount: number) => [
	{metricType: 'totalCount', value: totalCount},
	{metricType: 'newCount', value: 3},
];

const StatusProbe = () => (
	<span data-testid="status">{useLifecycleAccountData().status}</span>
);

const renderStatus = () => {
	render(<StatusProbe />);

	return screen.getByTestId('status').textContent;
};

describe('useLifecycleAccountData', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedUseDataSources.mockReturnValue({empty: false, loading: false});

		mockedUseRequest.mockReturnValue({
			data: buildMetrics(5),
			error: false,
			loading: false,
			refetch: jest.fn(),
		});
	});

	afterEach(cleanup);

	it('asks for the account metrics of the channel', () => {
		renderStatus();

		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				dataSourceFn: API.accounts.fetchMetrics,
				skipRequest: false,
				variables: {channelId: '123', groupId: '23'},
			})
		);
	});

	it('is ready when the channel has accounts', () => {
		expect(renderStatus()).toBe(AccountDataStatus.Ready);
	});

	it('reports no account data when the total is zero', () => {
		mockedUseRequest.mockReturnValue({
			data: buildMetrics(0),
			error: false,
			loading: false,
		});

		expect(renderStatus()).toBe(AccountDataStatus.NoAccountData);
	});

	it('reports an error instead of no account data when the metrics fail', () => {
		mockedUseRequest.mockReturnValue({
			data: null,
			error: true,
			loading: false,
		});

		expect(renderStatus()).toBe(AccountDataStatus.Error);
	});

	it('loads while the data sources or the metrics load', () => {
		mockedUseDataSources.mockReturnValue({empty: false, loading: true});

		expect(renderStatus()).toBe(AccountDataStatus.Loading);

		cleanup();

		mockedUseDataSources.mockReturnValue({empty: false, loading: false});

		mockedUseRequest.mockReturnValue({
			data: null,
			error: false,
			loading: true,
		});

		expect(renderStatus()).toBe(AccountDataStatus.Loading);
	});

	it('reports no data sources first and skips the metrics', () => {
		mockedUseDataSources.mockReturnValue({empty: true, loading: false});

		mockedUseRequest.mockReturnValue({
			data: null,
			error: true,
			loading: true,
		});

		expect(renderStatus()).toBe(AccountDataStatus.NoDataSources);
		expect(mockedUseRequest).toHaveBeenCalledWith(
			expect.objectContaining({skipRequest: true})
		);
	});
});
