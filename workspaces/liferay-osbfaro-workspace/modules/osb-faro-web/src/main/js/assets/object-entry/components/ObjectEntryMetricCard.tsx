import ClayLink from '@clayui/link';
import MetricBaseCard, {
	IGenericMetricBaseCardProps,
} from 'shared/components/metric-card/MetricBaseCard';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	AssetMetricQuery,
	AssetTabsQuery,
} from 'shared/components/metric-card/queries';
import {
	DownloadsMetric,
	ImpressionMadeMetric,
	Metric,
	ViewsMetric,
} from 'shared/components/metric-card/metrics';
import {ICommonVariables} from 'shared/types';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {useAssetVariables} from 'shared/components/metric-card/hooks';

const NAME = 'objectEntry';

const ObjectEntryMetricCard: React.FC<IGenericMetricBaseCardProps> = (
	props
) => {
	const variables = (commonVariables: Omit<ICommonVariables, 'type'>) =>
		useAssetVariables({...commonVariables, type: 'objectEntry'});

	const metrics: Metric[] = [
		ImpressionMadeMetric,
		ViewsMetric,
		DownloadsMetric,
	];

	return (
		<MetricBaseCard
			{...props}
			emptyDescription={
				<>
					<span className="mr-1">
						{Liferay.Language.get(
							'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
						)}
					</span>

					<ClayLink
						href={URLConstants.VisitorBehaviorWebContentLink}
						key="DOCUMENTATION"
						target="_blank"
					>
						{Liferay.Language.get(
							'learn-more-about-visitor-behavior'
						)}
					</ClayLink>
				</>
			}
			emptyTitle={Liferay.Language.get('no-visitors-data-was-found')}
			metrics={metrics}
			queries={{
				MetricQuery: AssetMetricQuery(NAME),
				name: NAME,
				TabsQuery: AssetTabsQuery(metrics, NAME),
			}}
			reportContainer={ReportContainer.VisitorsBehaviorCard}
			variables={variables}
		/>
	);
};

export default ObjectEntryMetricCard;
