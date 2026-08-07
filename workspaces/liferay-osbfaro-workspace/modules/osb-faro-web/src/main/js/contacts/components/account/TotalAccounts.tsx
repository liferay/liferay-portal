import * as API from 'shared/api';
import ClayLayout from '@clayui/layout';
import MetricCard from 'shared/components/MetricCard';
import React from 'react';
import {
	AccountMetricType,
	IAccountMetric,
	Metric,
} from '../../pages/account/utils/types';
import {sub} from 'shared/util/lang';
import {toThousands} from 'shared/util/numbers';
import {useParams} from 'react-router';
import {useRequest} from 'shared/hooks/useRequest';

const renderAccountValue = (metric?: Metric) =>
	sub(
		metric?.value === 1
			? Liferay.Language.get('x-account')
			: Liferay.Language.get('x-accounts'),
		[toThousands(metric?.value ?? 0)]
	);

const TotalAccounts = ({groupId}: {groupId: string}) => {
	const {channelId} = useParams<{
		channelId: string;
	}>();
	const {data, loading} = useRequest({
		dataSourceFn: API.accounts.fetchMetrics,
		variables: {
			channelId,
			groupId,
		},
	});

	const metrics = data as IAccountMetric[] | undefined;

	const getMetric = (metricType: AccountMetricType) =>
		metrics?.find((metric) => metric.metricType === metricType);

	const renderTrendLabel = (percentageNode: React.ReactNode) =>
		sub(
			Liferay.Language.get('x-vs-previous-90-days'),
			[percentageNode],
			false
		);

	return (
		<ClayLayout.Row>
			<ClayLayout.Col lg={4} md={12}>
				<MetricCard
					description={Liferay.Language.get(
						'displays-all-accounts-included-in-this-property'
					)}
					loading={loading}
					minHeight={160}
					title={Liferay.Language.get('total-accounts')}
					value={renderAccountValue(
						getMetric(AccountMetricType.Total)
					)}
				/>
			</ClayLayout.Col>

			<ClayLayout.Col lg={4} md={12}>
				<MetricCard
					description={Liferay.Language.get(
						'displays-all-new-accounts-included-in-this-property'
					)}
					loading={loading}
					minHeight={160}
					renderTrendLabel={renderTrendLabel}
					title={Liferay.Language.get('new-accounts')}
					trend={getMetric(AccountMetricType.New)?.trend}
					value={renderAccountValue(getMetric(AccountMetricType.New))}
				/>
			</ClayLayout.Col>

			<ClayLayout.Col lg={4} md={12}>
				<MetricCard
					description={Liferay.Language.get(
						'displays-all-active-accounts-included-in-this-property'
					)}
					loading={loading}
					minHeight={160}
					renderTrendLabel={renderTrendLabel}
					title={Liferay.Language.get('active-accounts')}
					trend={getMetric(AccountMetricType.Active)?.trend}
					value={renderAccountValue(
						getMetric(AccountMetricType.Active)
					)}
				/>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

export default TotalAccounts;
