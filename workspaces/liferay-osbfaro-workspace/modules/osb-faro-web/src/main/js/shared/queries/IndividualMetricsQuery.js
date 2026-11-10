import {gql} from '@apollo/client';

export default gql`
	query IndividualMetrics(
		$campaignId: String
		$channelId: String!
		$interval: String!
		$rangeKey: Int!
	) {
		individualMetric(
			campaignId: $campaignId
			channelId: $channelId
			interval: $interval
			rangeKey: $rangeKey
		) {
			anonymousIndividualsMetric {
				...trendFragment
			}
			knownIndividualsMetric {
				...trendFragment
			}
			totalIndividualsMetric {
				...trendFragment
			}
		}
	}

	fragment trendFragment on Metric {
		value
		trend {
			percentage
			trendClassification
		}
	}
`;
