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
import {ICommonVariables} from 'shared/types';
import {Metric, ViewsMetric} from 'shared/components/metric-card/metrics';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {useAssetVariables} from 'shared/components/metric-card/hooks';

const NAME = 'journal';

const WebContentMetricCard: React.FC<IGenericMetricBaseCardProps> = (props) => {
	const variables = (commonVariables: ICommonVariables) =>
		useAssetVariables(commonVariables);

	const metrics: Metric[] = [ViewsMetric];

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

export default WebContentMetricCard;
