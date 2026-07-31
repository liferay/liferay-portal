import {BROWSER_FRAGMENT, DEVICE_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';

export default gql`
	query SiteMetrics(
		$accountId: String
		$channelId: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
	) {
		site(
			accountId: $accountId
			channelId: $channelId
			includePrevious: false
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			segmentId: $segmentId
		) {
			sessionsMetric {
				...browserFragment
				...deviceFragment

				value
			}
		}
	}

	${BROWSER_FRAGMENT}
	${DEVICE_FRAGMENT}
`;
