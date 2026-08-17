import gql from 'graphql-tag';
import {DocumentNode} from '@apollo/client';
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
	}
`;

const SegmentFragment = gql`
	fragment segmentFragment on Metric {
		segment {
			metrics {
				value
				valueKey
			}
			total
		}
	}
`;

interface IQueryProps {
	metricName: string;
	name: Name;
}

const getPageQuery = (
	{metricName, name}: IQueryProps,
	fragment: DocumentNode,
	fragmentName: string,
	operationName: string
) => gql`
	query ${name}${operationName}(
		$accountId: String
		$channelId: String
		$devices: String
		$experienceId: String
		$location: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
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
			segmentId: $segmentId
			title: $title
		) {
			${metricName} {
				...${fragmentName}
			}
		}
	}

	${fragment}
`;

const getAssetQuery = (
	{metricName, name}: IQueryProps,
	fragment: DocumentNode,
	fragmentName: string,
	operationName: string
) => gql`
	query ${name}${operationName}(
		$accountId: String
		$assetId: String!
		$channelId: String
		$devices: String
		$location: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
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
			segmentId: $segmentId
			title: $title
		) {
			assetId
			assetTitle
			urls
			${metricName} {
				...${fragmentName}
			}
		}
	}

	${fragment}
`;

export const AssetAudienceReportQuery = (queryProps: IQueryProps) =>
	getAssetQuery(
		queryProps,
		AudienceReportFragment,
		'audienceReportFragment',
		'AudienceReportQuery'
	);

export const AssetSegmentQuery = (queryProps: IQueryProps) =>
	getAssetQuery(
		queryProps,
		SegmentFragment,
		'segmentFragment',
		'SegmentQuery'
	);

export const PageAudienceReportQuery = (queryProps: IQueryProps) =>
	getPageQuery(
		queryProps,
		AudienceReportFragment,
		'audienceReportFragment',
		'AudienceReportQuery'
	);

export const PageSegmentQuery = (queryProps: IQueryProps) =>
	getPageQuery(
		queryProps,
		SegmentFragment,
		'segmentFragment',
		'SegmentQuery'
	);
