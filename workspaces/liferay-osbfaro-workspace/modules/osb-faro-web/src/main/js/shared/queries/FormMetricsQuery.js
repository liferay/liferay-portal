import {
	BROWSER_FRAGMENT,
	DEVICE_FRAGMENT,
	GEOLOCATION_FRAGMENT,
	METRIC_FRAGMENT,
} from 'shared/queries/fragments';
import {gql} from '@apollo/client';

export default gql`
	query FormsMetrics(
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
		form(
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
			abandonmentsMetric {
				...metricFragment
			}
			assetId
			assetTitle
			completionTimeMetric {
				...metricFragment
			}
			submissionsMetric {
				...browserFragment
				...deviceFragment
				...geolocationFragment
				...metricFragment
			}
			urls
			viewsMetric {
				...metricFragment
			}
		}
	}

	${BROWSER_FRAGMENT}
	${DEVICE_FRAGMENT}
	${GEOLOCATION_FRAGMENT}
	${METRIC_FRAGMENT}
`;
