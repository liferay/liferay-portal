import getLocationsMapper, {
	getLocationsMapperCountries,
} from 'cerebro-shared/hocs/mappers/locations';
import URLConstants from 'shared/util/url-constants';
import {GEOLOCATION_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';
import {graphql} from '@apollo/client/react/hoc';
import {OperationOption} from '@apollo/client/react/hoc';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {withLocationsCard} from 'cerebro-shared/hocs/LocationsCard';

type DocumentMetricResult = {
	document: {
		downloadsMetric: unknown;
	};
};

const GEOLOCATION_QUERY = gql`
	query DocumentsAndMediaMetrics(
		$accountId: String
		$assetId: String!
		$channelId: String
		$devices: String
		$location: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$title: String
		$touchpoint: String
	) {
		document(
			accountId: $accountId
			assetId: $assetId
			canonicalUrl: $touchpoint
			channelId: $channelId
			country: $location
			deviceType: $devices
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			title: $title
		) {
			assetId
			assetTitle
			downloadsMetric {
				...geolocationFragment
			}
		}
	}

	${GEOLOCATION_FRAGMENT}
`;

/**
 * HOC
 * @description Documents And Media Locations
 */
const withBlogsLocations = () =>
	graphql(
		GEOLOCATION_QUERY,
		getLocationsMapper(
			(result: DocumentMetricResult) => result.document.downloadsMetric
		) as OperationOption<object, object>
	);

/**
 * HOC
 * @description Documents And Media Countries
 */
const withBlogsLocationsCountries = () =>
	graphql(
		GEOLOCATION_QUERY,
		getLocationsMapperCountries(
			(result: DocumentMetricResult) => result.document.downloadsMetric
		) as OperationOption<object, object>
	);

export default withLocationsCard(
	withBlogsLocations,
	withBlogsLocationsCountries,
	{
		documentationTitle: Liferay.Language.get(
			'learn-more-about-downloads-by-location'
		),
		documentationUrl:
			URLConstants.SitesDashboardDocumentsAndMediaDownloadByLocation,
		reportContainer: ReportContainer.DownloadsByLocationCard,
		title: Liferay.Language.get(
			'there-are-no-downloads-on-the-selected-period'
		),
	}
);
