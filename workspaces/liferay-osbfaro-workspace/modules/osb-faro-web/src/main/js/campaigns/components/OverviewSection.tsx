import ClayLayout from '@clayui/layout';
import MetricCard from 'shared/components/MetricCard';
import React from 'react';
import TrailingNinetyDayRange from 'shared/components/TrailingNinetyDayRange';
import {CampaignMetricType, ICampaignMetric} from '../utils/mock-campaigns';
import {SectionHeader} from 'shared/components/SectionHeader';
import {sub} from 'shared/util/lang';
import {toThousands} from 'shared/util/numbers';

interface IOverviewSectionProps {
	metrics: ICampaignMetric[];
}

const CARDS = [
	{
		description: Liferay.Language.get(
			'number-of-active-campaigns-with-at-least-one-touched-account-in-the-last-90-days'
		),
		metricType: CampaignMetricType.CampaignCount,
		title: Liferay.Language.get('campaigns'),
	},
	{
		description: Liferay.Language.get(
			'displays-the-unique-count-of-accounts-across-all-campaigns-in-the-last-90-days'
		),
		metricType: CampaignMetricType.AccountsTouched,
		title: Liferay.Language.get('accounts-touched'),
	},
	{
		description: Liferay.Language.get(
			'displays-the-total-pipeline-amount-across-touched-accounts-in-the-last-90-days'
		),
		metricType: CampaignMetricType.OpenPipelineAmount,
		title: Liferay.Language.get('pipeline-value'),
	},
	{
		description: Liferay.Language.get(
			'displays-the-closed-won-amount-across-touched-accounts-in-the-last-90-days'
		),
		metricType: CampaignMetricType.ClosedWonAmount,
		title: Liferay.Language.get('closed-won'),
	},
];

const renderTrendLabel = (percentageNode: React.ReactNode) =>
	sub(Liferay.Language.get('x-vs-last-x-days'), [percentageNode, 90], false);

const OverviewSection: React.FC<IOverviewSectionProps> = ({metrics}) => (
	<>
		<SectionHeader
			icon="box-container"
			rightContent={<TrailingNinetyDayRange />}
			title={Liferay.Language.get('overview')}
		/>

		<ClayLayout.Row className="row g-4">
			{CARDS.map(({description, metricType, title}) => {
				const metric = metrics.find(
					(metric) => metric.metricType === metricType
				);

				return (
					<ClayLayout.Col key={metricType} lg={3} md={6}>
						<MetricCard
							description={description}
							minHeight={200}
							renderTrendLabel={renderTrendLabel}
							title={title}
							trend={metric?.trend}
							trendClassName="text-lowercase"
							value={toThousands(metric?.value ?? 0)}

							// Lowercasing, the default, would render "504m"
							// against the "15.2K" of the table right below.

							valueClassName="text-uppercase"
						/>
					</ClayLayout.Col>
				);
			})}
		</ClayLayout.Row>
	</>
);

export default OverviewSection;
