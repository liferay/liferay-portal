import gql from 'graphql-tag';
import {Name} from './types';

const AudienceReportFragment = gql`
	fragment audienceReportFragment on Metric {
		audienceReport {
			anonymousUsersCount
			knownUsersCount
			nonsegmentedKnownUsersCount
			segmentedAnonymousUsersCount
			segmentedKnownUsersCount
		}
		segment {
			metrics {
				value
				valueKey
			}
			total
		}
	}
`;

export const PageAudienceReportQuery = ({
	metricName,
	name,
}: {
	metricName: string;
	name: Name;
}) => gql`
	query ${name}AudienceReportQuery(
		$accountId: String
		$channelId: String
		$devices: String
		$experienceId: String
		$location: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$title: String
		$touchpoint: String
	) {
		${name}(
			accountId: $accountId
			channelId: $channelId
			canonicalUrl: $touchpoint
			country: $location
			deviceType: $devices
			experienceId: $experienceId
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			title: $title
		) {
			${metricName} {
				...audienceReportFragment
			}
		}
	}

	${AudienceReportFragment}
`;

export const AssetAudienceReportQuery = ({
	metricName,
	name,
}: {
	metricName: string;
	name: Name;
}) => gql`
	query ${name}AudienceReportQuery(
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
		${name}(
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
			${metricName} {
				...audienceReportFragment
			}
		}
	}

	${AudienceReportFragment}
`;
