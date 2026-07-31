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
		siteVisitorHeatMap(
			accountId: $accountId
			channelId: $channelId
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			segmentId: $segmentId
		) {
			column: colDimension
			row: rowDimension
			value
		}
	}
`;
