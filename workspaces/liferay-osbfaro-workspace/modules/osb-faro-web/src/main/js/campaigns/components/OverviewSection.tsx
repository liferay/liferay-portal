import ClayLayout from '@clayui/layout';
import MetricCard from 'shared/components/MetricCard';
import moment from 'moment';
import React from 'react';
import {CampaignMetricType, ICampaignMetric} from 'shared/api/campaigns';
import {DEFAULT_DATE_FORMAT} from 'shared/util/date';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {SectionHeader} from 'shared/components/SectionHeader';
import {sub} from 'shared/util/lang';
import {toThousands} from 'shared/util/numbers';

interface IOverviewSectionProps {
	loading?: boolean;
	metrics?: ICampaignMetric[] | null;
	rangeSelectors: RangeSelectors;
}

// The trend compares the selected range against the window of the same length
// before it, so the label names that length in days. All Time has no window
// before it, Last 24 Hours names its own window, and a single day stands in
// for Yesterday.

const getRangeDays = ({rangeEnd, rangeKey, rangeStart}: RangeSelectors) => {
	if (rangeKey === RangeKeyTimeRanges.AllTime) {
		return null;
	}

	if (rangeKey === RangeKeyTimeRanges.CustomRange) {
		return (
			moment(rangeEnd, DEFAULT_DATE_FORMAT).diff(
				moment(rangeStart, DEFAULT_DATE_FORMAT),
				'days'
			) + 1
		);
	}

	return Math.max(Number(rangeKey), 1);
};

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

const OverviewSection: React.FC<IOverviewSectionProps> = ({
	loading = false,
	metrics,
	rangeSelectors,
}) => {
	const rangeDays = getRangeDays(rangeSelectors);

	const renderTrendLabel = (percentageNode: React.ReactNode) => {
		if (rangeSelectors.rangeKey === RangeKeyTimeRanges.Last24Hours) {
			return sub(
				Liferay.Language.get('x-vs-last-24-hours'),
				[percentageNode],
				false
			);
		}

		return rangeDays
			? sub(
					Liferay.Language.get('x-vs-last-x-days'),
					[percentageNode, rangeDays],
					false
				)
			: percentageNode;
	};

	return (
		<>
			<SectionHeader
				icon="box-container"
				title={Liferay.Language.get('overview')}
			/>

			<ClayLayout.Row className="row g-4">
				{CARDS.map(({description, metricType, title}) => {
					const metric = metrics?.find(
						(metric) => metric.metricType === metricType
					);

					return (
						<ClayLayout.Col key={metricType} lg={3} md={6}>
							<MetricCard
								description={description}
								loading={loading}
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
};

export default OverviewSection;
