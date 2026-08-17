import AudienceReportDonut from './AudienceReportDonut';
import AudienceReportStateRenderer from './AudienceReportStateRenderer';
import HTMLBarChart from 'shared/components/HTMLBarChart';
import InfoPopover, {IInfoPopoverProps} from 'shared/components/InfoPopover';
import React from 'react';
import {DocumentNode, useQuery} from '@apollo/client';

import {fetchPolicyDefinition} from 'shared/util/graphql';
import {formatData} from './util';
import {getFilters, RawFilters} from 'shared/util/filter';
import {
	getSafeDecodedURIComponent,
	getSafeRangeSelectors,
	getSafeTouchpoint,
} from 'shared/util/util';
import {IAudienceReportBaseCardProps, Name, TData} from './types';
import {RangeSelectors} from 'shared/types';
import {useParams} from 'react-router-dom';

const AudienceReportTitle: React.FC<IInfoPopoverProps> = ({content, title}) => (
	<div className="d-inline-flex gap">
		<div className="h4 mb-3 mr-2 text-center text-secondary title">
			{title}
		</div>

		{content && <InfoPopover content={content} title={title} />}
	</div>
);

interface IAudienceReportWithDataProps
	extends Partial<IAudienceReportBaseCardProps> {
	name: Name;
	result: TData;
}

function AudienceReportWithData({
	knownIndividualsTitle,
	name,
	result,
	segmentsTitle = Liferay.Language.get('viewer-segments'),
	uniqueVisitorsTitle = Liferay.Language.get('visitors'),
}: IAudienceReportWithDataProps) {
	const {knownIndividuals, segments, uniqueVisitors} = formatData(result);

	return (
		<div className="audience-report-chart row w-100">
			<div className="col-sm-6">
				<div className="row">
					<div className="col-sm-6 text-center">
						<AudienceReportTitle title={uniqueVisitorsTitle} />

						<AudienceReportDonut {...uniqueVisitors} />
					</div>

					<div className="col-sm-6 text-center">
						<AudienceReportTitle
							content={
								name === Name.Page
									? Liferay.Language.get(
											'only-known-individuals-that-interacted-with-the-current-page-are-accounted-for-in-this-chart'
										)
									: Liferay.Language.get(
											'only-known-individuals-that-interacted-with-the-current-asset-are-accounted-for-in-this-chart'
										)
							}
							title={knownIndividualsTitle}
						/>

						<AudienceReportDonut {...knownIndividuals} />
					</div>
				</div>
			</div>

			<div className="col-sm-6 pl-5">
				<AudienceReportTitle
					content={
						name === Name.Page
							? Liferay.Language.get(
									'only-segmented-known-individuals-that-interacted-with-the-current-page-are-accounted-for-in-this-chart'
								)
							: Liferay.Language.get(
									'only-segmented-known-individuals-that-interacted-with-the-current-asset-are-accounted-for-in-this-chart'
								)
					}
					title={segmentsTitle}
				/>

				<div className="audience-report-chart-bar">
					<HTMLBarChart {...segments} />
				</div>
			</div>
		</div>
	);
}

interface IAudienceReportProps<TRawData>
	extends Partial<IAudienceReportBaseCardProps> {
	accountId?: string | null;
	AudienceReportQuery: DocumentNode;
	experienceId?: string | null;
	filters: RawFilters;
	rangeSelectors: RangeSelectors;
	segmentId?: string | null;
	SegmentQuery: DocumentNode;
	mapper: (data: TRawData) => TData;
	name: Name;
}

function AudienceReport<TRawData>({
	AudienceReportQuery,
	SegmentQuery,
	accountId,
	experienceId,
	filters,
	mapper,
	rangeSelectors,
	segmentId,
	...otherProps
}: IAudienceReportProps<TRawData>) {
	const {assetId, channelId, title, touchpoint} = useParams();

	const variables = {
		assetId,
		touchpoint: getSafeTouchpoint(touchpoint as string),
		...(accountId && {accountId}),
		...(experienceId && {experienceId}),
		...(segmentId && {segmentId}),
		...(otherProps.name !== Name.ObjectEntry && {
			channelId,
			title: getSafeDecodedURIComponent(title as string),
		}),
		...getFilters(filters),
		...getSafeRangeSelectors(rangeSelectors),
	};

	const {
		data: audienceReportData,
		error: audienceReportError,
		loading: audienceReportLoading,
	} = useQuery(AudienceReportQuery, {
		fetchPolicy: fetchPolicyDefinition(rangeSelectors),
		variables,
	});

	const {
		data: segmentData,
		error: segmentError,
		loading: segmentLoading,
	} = useQuery(SegmentQuery, {
		fetchPolicy: fetchPolicyDefinition(rangeSelectors),
		variables,
	});

	const result = {
		...mapper(audienceReportData),
		...mapper(segmentData),
	} as TData;

	return (
		<AudienceReportStateRenderer
			error={(audienceReportError ?? segmentError)!}
			loading={audienceReportLoading || segmentLoading}
		>
			<AudienceReportWithData {...otherProps} result={result} />
		</AudienceReportStateRenderer>
	);
}

export default AudienceReport;
