import gql from 'graphql-tag';
import {Metric} from './metrics';
import {
	METRIC_HISTOGRAM_FRAGMENT,
	METRIC_TABS_FRAGMENT,
} from 'shared/queries/fragments';

const capitalize = (str: string): string =>
	str.charAt(0).toUpperCase() + str.slice(1);

const tabsOperationName = (name: string) =>
	`${capitalize(name)}MetricTabsQuery`;

const metricOperationName = (name: string) => `${capitalize(name)}MetricQuery`;

const buildAssetTabsBody = (metrics: Metric[]) =>
	metrics.map(({name}) => `${name} { ...TabsFragment }`).join('\n\t\t\t\t');

export const AssetTabsQuery = (metrics: Metric[], name: string) => gql`
	query ${tabsOperationName(name)}(
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
			${buildAssetTabsBody(metrics)}
		}
	}

	${METRIC_TABS_FRAGMENT}
`;

export const AssetMetricQuery = (queryName: string) => (metricName: string) =>
	gql`
	query ${metricOperationName(queryName)}(
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
		${queryName}(
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
			${metricName} {
				...HistogramFragment
			}
		}
	}

	${METRIC_HISTOGRAM_FRAGMENT}
`;

export const SitesTabsQuery = gql`
	query SitesMetricTabsQuery(
		$accountId: String
		$channelId: String
		$interval: String!
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
	) {
		site(
			accountId: $accountId
			channelId: $channelId
			interval: $interval
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			segmentId: $segmentId
		) {
			bounceRateMetric {
				...TabsFragment
			}
			sessionDurationMetric {
				...TabsFragment
			}
			sessionsPerVisitorMetric {
				...TabsFragment
			}
			visitorsMetric {
				...TabsFragment
			}
		}
	}

	${METRIC_TABS_FRAGMENT}
`;

const SitesGenericMetricQuery = (metricName: string) => gql`
	query SitesMetricQuery(
		$accountId: String
		$channelId: String
		$interval: String!
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
	) {
		site(
			accountId: $accountId
			channelId: $channelId
			interval: $interval
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			segmentId: $segmentId
		) {
			${metricName} {
				...HistogramFragment
			}
		}
	}

	${METRIC_HISTOGRAM_FRAGMENT}
`;

const SitesCompositeMetricQuery = gql`
	query SitesMetricQuery(
		$accountId: String
		$channelId: String
		$interval: String!
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
	) {
		site(
			accountId: $accountId
			channelId: $channelId
			interval: $interval
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			segmentId: $segmentId
		) {
			visitorsMetric {
				...HistogramFragment
			}
			anonymousVisitorsMetric {
				...HistogramFragment
			}
			knownVisitorsMetric {
				...HistogramFragment
			}
		}
	}

	${METRIC_HISTOGRAM_FRAGMENT}
`;

export const SitesMetricQuery = (metricName: string) =>
	metricName === 'visitorsMetric'
		? SitesCompositeMetricQuery
		: SitesGenericMetricQuery(metricName);

export const PageMetricQuery = (metricName: string) => gql`
	query PageMetricQuery(
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
		page(
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
				...HistogramFragment
			}
		}
	}

	${METRIC_HISTOGRAM_FRAGMENT}
`;

export const PageMetricTabsQuery = gql`
	query PageMetricQuery(
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
		page(
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
			visitorsMetric {
				...TabsFragment
			}
			avgTimeOnPageMetric {
				...TabsFragment
			}
			bounceRateMetric {
				...TabsFragment
			}
			entrancesMetric {
				...TabsFragment
			}
			exitRateMetric {
				...TabsFragment
			}
			viewsMetric {
				...TabsFragment
			}
		}
	}

	${METRIC_TABS_FRAGMENT}
`;
