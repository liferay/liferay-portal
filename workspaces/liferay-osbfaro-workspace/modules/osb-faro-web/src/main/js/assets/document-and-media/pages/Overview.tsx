import AssetMetricCard from 'assets/components/AssetMetricCard';
import AudienceReportCard from 'shared/components/audience-report/AudienceReportBaseCard';
import DevicesCard from 'assets/document-and-media/hocs/DevicesCard';
import LocationsCard from 'assets/document-and-media/hocs/LocationsCard';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {
	CommentsMetric,
	DownloadsMetric,
	ImpressionMadeMetric,
	RatingsMetric,
} from 'shared/components/metric-card/metrics';
import {
	Accessor,
	AssetAppearsOnCard,
	EmptyStateLink,
	EmptyStateText,
} from 'assets/components/AssetAppearsOnCard';
import {AssetTypes} from 'shared/util/constants';
import {MetricName} from 'shared/types/MetricName';
import {Name} from 'shared/components/audience-report/types';

const Overview = () => (
	<>
		<div className="row">
			<div className="col-sm-12">
				<AssetMetricCard
					documentationURL={
						URLConstants.VisitorBehaviorDocumentsAndMediaLink
					}
					label={Liferay.Language.get('visitors-behavior')}
					metrics={[
						DownloadsMetric,
						ImpressionMadeMetric,
						CommentsMetric,
						RatingsMetric,
					]}
					name="document"
				/>
			</div>
		</div>

		<div className="row">
			<div className="col-sm-12">
				<AudienceReportCard
					knownIndividualsTitle={Liferay.Language.get(
						'segmented-downloads'
					)}
					query={{
						metricName: MetricName.Downloads,
						name: Name.Document,
					}}
					segmentsTitle={Liferay.Language.get('downloaded-segments')}
					uniqueVisitorsTitle={Liferay.Language.get('downloads')}
				/>
			</div>
		</div>

		<div className="row">
			<div className="col-lg-6 col-md-12">
				<LocationsCard
					label={Liferay.Language.get('downloads-by-location')}
					legacyDropdownRangeKey={false}
					metricLabel={Liferay.Language.get('downloads')}
				/>
			</div>

			<div className="col-lg-6 col-md-12">
				<DevicesCard
					label={Liferay.Language.get('downloads-by-technology')}
					legacyDropdownRangeKey={false}
					metricLabel={Liferay.Language.get('downloads')}
				/>
			</div>
		</div>

		<div className="row">
			<div className="col-sm-12">
				<AssetAppearsOnCard
					accessors={[
						Accessor.DownloadsMetric,
						Accessor.ImpressionMadeMetric,
					]}
					assetType={AssetTypes.Document}
					emptyStateLink={EmptyStateLink.Document}
					emptyStateText={EmptyStateText.Document}
				/>
			</div>
		</div>
	</>
);

export default Overview;
