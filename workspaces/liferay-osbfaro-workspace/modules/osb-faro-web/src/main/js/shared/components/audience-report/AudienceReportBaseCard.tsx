import AudienceReport from './AudienceReport';
import BaseCard from 'shared/components/base-card';
import Card from '../Card';
import React from 'react';
import {
	AssetAudienceReportQuery,
	AssetSegmentQuery,
	PageAudienceReportQuery,
	PageSegmentQuery,
} from './queries';
import {IAudienceReportBaseCardProps, Name} from './types';
import {ReportContainer} from '../download-report/DownloadPDFReport';

function AudienceReportBaseCard({
	query: {metricName, name},
	...props
}: IAudienceReportBaseCardProps) {
	const AudienceReportQuery =
		name === Name.Page ? PageAudienceReportQuery : AssetAudienceReportQuery;

	const SegmentQuery =
		name === Name.Page ? PageSegmentQuery : AssetSegmentQuery;

	return (
		<BaseCard
			className="analytics-audience-report-card"
			label={Liferay.Language.get('audience')}
			legacyDropdownRangeKey={false}
			minHeight={536}
			reportContainer={ReportContainer.AudienceCard}
		>
			{({
				accountId,
				experienceId,
				filters,
				rangeSelectors,
				segmentId,
			}) => (
				<Card.Body>
					<AudienceReport
						{...props}
						accountId={accountId}
						AudienceReportQuery={AudienceReportQuery({
							metricName,
							name,
						})}
						experienceId={experienceId}
						filters={filters}
						mapper={(result: any) => result?.[name]?.[metricName]}
						name={name}
						rangeSelectors={rangeSelectors}
						segmentId={segmentId}
						SegmentQuery={SegmentQuery({
							metricName,
							name,
						})}
					/>
				</Card.Body>
			)}
		</BaseCard>
	);
}

export default AudienceReportBaseCard;
