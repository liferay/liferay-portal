import AssetMetricCard from 'assets/components/AssetMetricCard';
import AudienceReportCard from 'shared/components/audience-report/AudienceReportBaseCard';
import React from 'react';
import {AssetAppearsOnCard} from 'assets/components/AssetAppearsOnCard';
import {getAssetOverviewConfig} from 'assets/overviewConfig';

const Overview: React.FC<{slug: string}> = ({slug}) => {
	const {
		DevicesCard,
		LocationsCard,
		appearsOn,
		audienceReport,
		devicesLabel,
		locationsLabel,
		metricCard,
		metricLabel,
		renderExtraCards,
	} = getAssetOverviewConfig(slug);

	return (
		<>
			<div className="row">
				<div className="col-sm-12">
					<AssetMetricCard
						{...metricCard}
						label={Liferay.Language.get('visitors-behavior')}
					/>
				</div>
			</div>

			<div className="row">
				<div className="col-sm-12">
					<AudienceReportCard
						knownIndividualsTitle={
							audienceReport.knownIndividualsTitle
						}
						query={{
							metricName: audienceReport.metricName,
							name: audienceReport.name,
						}}
						segmentsTitle={audienceReport.segmentsTitle}
						uniqueVisitorsTitle={audienceReport.uniqueVisitorsTitle}
					/>
				</div>
			</div>

			<div className="row">
				<div className="col-lg-6 col-md-12">
					<LocationsCard
						label={locationsLabel}
						legacyDropdownRangeKey={false}
						metricLabel={metricLabel}
					/>
				</div>

				<div className="col-lg-6 col-md-12">
					<DevicesCard
						label={devicesLabel}
						legacyDropdownRangeKey={false}
						metricLabel={metricLabel}
					/>
				</div>
			</div>

			{renderExtraCards?.()}

			<div className="row">
				<div className="col-sm-12">
					<AssetAppearsOnCard
						accessors={appearsOn.accessors}
						assetType={appearsOn.assetType}
						emptyStateLink={appearsOn.emptyStateLink}
						emptyStateText={appearsOn.emptyStateText}
					/>
				</div>
			</div>
		</>
	);
};

export default Overview;
