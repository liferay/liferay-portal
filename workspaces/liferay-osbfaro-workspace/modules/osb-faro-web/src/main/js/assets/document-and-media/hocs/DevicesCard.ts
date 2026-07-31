import getDevicesMapper from 'cerebro-shared/hocs/mappers/devices';
import URLConstants from 'shared/util/url-constants';
import {BROWSER_FRAGMENT, DEVICE_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';
import {graphql} from '@apollo/client/react/hoc';
import {OperationOption} from '@apollo/client/react/hoc';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {withDevicesCard} from 'shared/hoc/DevicesCard';

type DocumentMetricResult = {
	document: {
		downloadsMetric: unknown;
	};
};

const BROWSER_DEVICE_QUERY = gql`
	query DocumentsAndMediaMetrics(
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
			segmentId: $segmentId
			title: $title
		) {
			assetId
			assetTitle
			downloadsMetric {
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
 * @description Documents And Media Devices
 */
const withDocumentsAndMediaDevices = () =>
	graphql(
		BROWSER_DEVICE_QUERY,
		getDevicesMapper(
			(result: DocumentMetricResult) => result.document.downloadsMetric
		) as OperationOption<object, object>
	);

export default withDevicesCard(withDocumentsAndMediaDevices, {
	documentationTitle: Liferay.Language.get(
		'learn-more-about-downloads-by-technology'
	),
	documentationUrl:
		URLConstants.SitesDashboardDocumentsAndMediaViewsByTechnology,
	reportContainer: ReportContainer.DownloadsByTechnologyCard,
	title: Liferay.Language.get(
		'there-are-no-downloads-on-the-selected-period'
	),
});
