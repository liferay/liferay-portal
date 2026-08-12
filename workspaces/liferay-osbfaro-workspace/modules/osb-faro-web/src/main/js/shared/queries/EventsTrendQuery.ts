import {gql} from '@apollo/client';
import {SessionEntityTypes} from 'shared/util/constants';
import {TrendClassification} from 'segment/types';

export interface EventsTrendData {
	eventsByUserSessions: {
		totalEventsMetric: {
			previousValue: number;
			trend: {
				percentage: number;
				trendClassification: TrendClassification;
			};
			value: number;
		} | null;
	};
}

export interface EventsTrendVariables {
	channelId: string;
	entityId: string;
	entityType: SessionEntityTypes;
	keywords?: string;
	rangeEnd?: string | null;
	rangeKey?: number | null;
	rangeStart?: string | null;
}

export default gql`
	query EventsTrend(
		$channelId: String!
		$entityId: String!
		$entityType: EntityType!
		$keywords: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
	) {
		eventsByUserSessions(
			channelId: $channelId
			entityId: $entityId
			entityType: $entityType
			includeWebhookEvents: true
			keywords: $keywords
			page: 0
			size: 1
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
		) {
			totalEventsMetric {
				previousValue
				trend {
					percentage
					trendClassification
				}
				value
			}
		}
	}
`;
