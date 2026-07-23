import {gql} from '@apollo/client';

export default gql`
	query SiteMetrics(
		$accountId: String
		$channelId: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
	) {
		siteVisitorHeatMap(
			accountId: $accountId
			channelId: $channelId
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
		) {
			column: colDimension
			row: rowDimension
			value
		}
	}
`;
