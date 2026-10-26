import * as API from 'shared/api';
import {
	AccountMetricType,
	IAccountMetric,
} from 'contacts/pages/account/utils/types';
import {useDataSources} from 'shared/context/dataSources';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {AccountDataStatus} from 'lifecycle/utils/types';

interface IUseLifecycleAccountData {
	refetch: () => void;
	status: AccountDataStatus;
}

export const useLifecycleAccountData = (): IUseLifecycleAccountData => {
	const {channelId, groupId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

	const {data, error, loading, refetch} = useRequest<
		{channelId: string; groupId: string},
		IAccountMetric[]
	>({
		dataSourceFn: API.accounts.fetchMetrics,
		skipRequest: dataSourcesLoading || noDataSources,
		variables: {channelId: channelId!, groupId: groupId!},
	});

	const getStatus = (): AccountDataStatus => {
		if (dataSourcesLoading) {
			return AccountDataStatus.Loading;
		}

		if (noDataSources) {
			return AccountDataStatus.NoDataSources;
		}

		if (loading) {
			return AccountDataStatus.Loading;
		}

		if (error) {
			return AccountDataStatus.Error;
		}

		const totalAccounts = data?.find(
			({metricType}) => metricType === AccountMetricType.Total
		)?.value;

		return totalAccounts
			? AccountDataStatus.Ready
			: AccountDataStatus.NoAccountData;
	};

	return {refetch, status: getStatus()};
};
