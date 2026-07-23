import {gql} from '@apollo/client';

export default gql`
	query CohortHeatMap(
		$accountId: String
		$channelId: String
		$interval: String!
	) {
		cohort(
			accountId: $accountId
			channelId: $channelId
			interval: $interval
		) {
			anonymousCohortHeatMapMetrics {
				retention
				rowKey
				rowDimension
				colDimension
				value
			}
			knownCohortHeatMapMetrics {
				retention
				rowKey
				rowDimension
				colDimension
				value
			}
			visitorsCohortHeatMapMetrics {
				retention
				rowKey
				rowDimension
				colDimension
				value
			}
		}
	}
`;
