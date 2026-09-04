import ClayLayout from '@clayui/layout';
import MetricCard from 'shared/components/MetricCard';
import React from 'react';
import {CampaignMetricType, ICampaignMetric} from '../utils/mock-campaigns';
import {formatUTCDate, getCustomDateFormat, getDateNow} from 'shared/util/date';
import {SectionHeader} from 'shared/components/SectionHeader';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
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

const OVERVIEW_RANGE_MONTHS = 3;

const renderTrendLabel = (percentageNode: React.ReactNode) =>
	sub(Liferay.Language.get('x-vs-last-x-days'), [percentageNode, 90], false);

// The endpoint computes every value over a trailing window rather than one the
// reader picks, so the range is derived here instead of fetched. Derived per
// render, not once at module scope, so a session left open overnight does not
// keep showing the day it was opened.

const getDateRange = () => {
	const today = getDateNow();
	const format = getCustomDateFormat();

	const startDate = today.clone().subtract(OVERVIEW_RANGE_MONTHS, 'months');

	return `${formatUTCDate(startDate, format)} – ${formatUTCDate(
		today,
		format
	)}`;
};

const OverviewSection: React.FC<IOverviewSectionProps> = ({metrics}) => (
	<>
		<SectionHeader
			icon="box-container"
			rightContent={
				<Text color="secondary" size={3}>
					{getDateRange()}
				</Text>
			}
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
							value={

								// `MetricCard` lowercases its value so the
								// cards that read "1.8k accounts" work. Here
								// the value is the abbreviation alone, and
								// lowercasing it renders "504m" against the
								// "15.2K" of the table right below.

								<span className="text-uppercase">
									{toThousands(metric?.value ?? 0)}
								</span>
							}
						/>
					</ClayLayout.Col>
				);
			})}
		</ClayLayout.Row>
	</>
);

export default OverviewSection;
