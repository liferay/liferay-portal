import getLocationsMapper, {
	getLocationsMapperCountries,
} from 'cerebro-shared/hocs/mappers/locations';
import URLConstants from 'shared/util/url-constants';
import {GEOLOCATION_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';
import {graphql, OperationOption} from '@apollo/client/react/hoc';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {withLocationsCard} from 'cerebro-shared/hocs/LocationsCard';

type JournalMetricResult = {
	journal: {
		viewsMetric: unknown;
	};
};

const GEOLOCATION_QUERY = gql`
	query WebContentMetrics(
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
		journal(
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
			urls
			viewsMetric {
				...geolocationFragment
			}
		}
	}

	${GEOLOCATION_FRAGMENT}
`;

/**
 * HOC
 * @description Web Content Locations
 */
const withWebContentLocations = () =>
	graphql(
		GEOLOCATION_QUERY,
		getLocationsMapper(
			(result: JournalMetricResult) => result.journal.viewsMetric
		) as OperationOption<object, object>
	);

/**
 * HOC
 * @description Web Content Countries
 */
const withWebContentLocationsCountries = () =>
	graphql(
		GEOLOCATION_QUERY,
		getLocationsMapperCountries(
			(result: JournalMetricResult) => result.journal.viewsMetric
		) as OperationOption<object, object>
	);

export default withLocationsCard(
	withWebContentLocations,
	withWebContentLocationsCountries,
	{
		documentationTitle: Liferay.Language.get(
			'learn-more-about-views-by-location'
		),
		documentationUrl: URLConstants.SitesDashboardWebContentViewsByLocation,
		reportContainer: ReportContainer.ViewsByLocationCard,
		title: Liferay.Language.get(
			'there-are-no-views-on-the-selected-period'
		),
	}
);
