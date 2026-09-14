import AssetMetricCard from 'assets/components/AssetMetricCard';
import AudienceReportCard from 'shared/components/audience-report/AudienceReportBaseCard';
import DevicesCard from 'assets/web-content/hocs/DevicesCard';
import LocationsCard from 'assets/web-content/hocs/LocationsCard';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {ViewsMetric} from 'shared/components/metric-card/metrics';
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
						URLConstants.VisitorBehaviorWebContentLink
					}
					label={Liferay.Language.get('visitors-behavior')}
					metrics={[ViewsMetric]}
					name="journal"
				/>
			</div>
		</div>

		<div className="row">
			<div className="col-sm-12">
				<AudienceReportCard
					knownIndividualsTitle={Liferay.Language.get(
						'segmented-views'
					)}
					query={{
						metricName: MetricName.Views,
						name: Name.Journal,
					}}
					uniqueVisitorsTitle={Liferay.Language.get('views')}
				/>
			</div>
		</div>

		<div className="row">
			<div className="col-lg-6 col-md-12">
				<LocationsCard
					label={Liferay.Language.get('views-by-location')}
					legacyDropdownRangeKey={false}
				/>
			</div>
			<div className="col-lg-6 col-md-12">
				<DevicesCard
					label={Liferay.Language.get('views-by-technology')}
					legacyDropdownRangeKey={false}
				/>
			</div>
		</div>

		<div className="row">
			<div className="col-sm-12">
				<AssetAppearsOnCard
					accessors={[Accessor.ViewsMetric]}
					assetType={AssetTypes.Journal}
					emptyStateLink={EmptyStateLink.Journal}
					emptyStateText={EmptyStateText.Journal}
				/>
			</div>
		</div>
	</>
);

export default Overview;
