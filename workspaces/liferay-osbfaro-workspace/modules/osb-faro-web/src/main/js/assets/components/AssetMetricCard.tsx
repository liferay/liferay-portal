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
import {Metric} from 'shared/components/metric-card/metrics';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {useAssetVariables} from 'shared/components/metric-card/hooks';

export interface IAssetMetricCardProps extends IGenericMetricBaseCardProps {
	documentationURL: URLConstants;
	metrics: Metric[];
	name: string;

	/**
	 * Object entries are the one asset type whose metric query is scoped by an
	 * explicit `type`; the other four leave it off.
	 */
	variableType?: string;
}

const AssetMetricCard: React.FC<IAssetMetricCardProps> = ({
	documentationURL,
	metrics,
	name,
	variableType,
	...props
}) => {
	const variables = (commonVariables: ICommonVariables) =>
		useAssetVariables(
			variableType
				? {...commonVariables, type: variableType}
				: commonVariables
		);

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
						href={documentationURL}
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
				MetricQuery: AssetMetricQuery(name),
				name,
				TabsQuery: AssetTabsQuery(metrics, name),
			}}
			reportContainer={ReportContainer.VisitorsBehaviorCard}
			variables={variables}
		/>
	);
};

export default AssetMetricCard;
