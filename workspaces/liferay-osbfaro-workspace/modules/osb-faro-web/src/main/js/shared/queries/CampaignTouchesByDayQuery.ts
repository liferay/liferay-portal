import {CampaignTouch} from 'shared/util/activities';
import {gql} from '@apollo/client';
import {SessionEntityTypes} from 'shared/util/constants';

export const CAMPAIGNS_PER_PAGE = 8;

export interface CampaignTouchDay {
	campaignsCount: number;
	date: string;
	items: CampaignTouch[];
	touchesCount: number;
}

export interface CampaignTouchesByDayData {
	campaignTouchesByDay: CampaignTouchDay[];
}

export interface CampaignTouchesByDayVariables {
	accountId?: string;
	channelId: string;
	date?: string | null;
	entityId: string;
	entityType: SessionEntityTypes;
	keywords?: string;
	page: number;
	rangeEnd?: string | null;
	rangeKey?: number | null;
	rangeStart?: string | null;
	size: number;
}

export default gql`
	query CampaignTouchesByDay(
		$accountId: String
		$channelId: String!
		$date: String
		$entityId: String!
		$entityType: EntityType!
		$keywords: String
		$page: Int!
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$size: Int!
	) {
		campaignTouchesByDay(
			accountId: $accountId
			channelId: $channelId
			date: $date
			entityId: $entityId
			entityType: $entityType
			keywords: $keywords
			page: $page
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			size: $size
		) {
			campaignsCount
			date
			items {
				campaignId
				campaignName
				dataSourceType
				touches {
					individualId
					individualName
					jobTitle
					status
				}
			}
			touchesCount
		}
	}
`;
