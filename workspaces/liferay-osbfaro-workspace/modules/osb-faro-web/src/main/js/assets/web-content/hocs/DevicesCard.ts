import getDevicesMapper from 'cerebro-shared/hocs/mappers/devices';
import URLConstants from 'shared/util/url-constants';
import {BROWSER_FRAGMENT, DEVICE_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';
import {graphql, OperationOption} from '@apollo/client/react/hoc';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {withDevicesCard} from 'shared/hoc/DevicesCard';

type JournalMetricResult = {
	journal: {
		viewsMetric: unknown;
	};
};

const BROWSER_DEVICE_QUERY = gql`
	query WebContentMetrics(
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
			segmentId: $segmentId
			title: $title
		) {
			assetId
			assetTitle
			urls
			viewsMetric {
				...browserFragment
				...deviceFragment

				previousValue
				value
			}
		}
	}

	${BROWSER_FRAGMENT}
	${DEVICE_FRAGMENT}
`;

/**
 * HOC
 * @description Web Content Devices
 */
const withWebContentDevices = () =>
	graphql(
		BROWSER_DEVICE_QUERY,
		getDevicesMapper(
			(result: JournalMetricResult) => result.journal.viewsMetric
		) as OperationOption<object, object>
	);

export default withDevicesCard(withWebContentDevices, {
	documentationTitle: Liferay.Language.get(
		'learn-more-about-views-by-technology'
	),
	documentationUrl: URLConstants.SitesDashboardWebContentViewsByTechnology,
	reportContainer: ReportContainer.ViewsByTechnologyCard,
	title: Liferay.Language.get('there-are-no-views-on-the-selected-period'),
});
