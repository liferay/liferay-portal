import {gql} from '@apollo/client';
import {SessionEntityTypes} from 'shared/util/constants';

export interface UserSessionEvent {
	applicationId: string;
	assetTitle: string;
	canonicalUrl: string;
	createDate: string;
	eventDate: string;
	eventId: string;
	name: string;
	pageDescription: string;
	pageGroupId?: string | null;
	pageTitle: string;
	properties: Array<{name: string; value: string}>;
	referrer: string;
	url: string;
	utmCampaignId?: string | null;
	utmCampaignName?: string | null;
}

export interface UserSession {
	becameKnown: boolean;
	browserName: string;
	completeDate: Date;
	contentLanguageID: string;
	createDate: string;
	description: string;
	devicePixelRatioz: number;
	deviceType: string;
	events: Event[];
	keywords: string;
	languageID: string;
	screenHeight: number;
	screenWidth: number;
	timezoneOffset: string;
	userAgent: string;
}

export interface UserSessionData {
	eventsByUserSessions: {
		totalEvents: number;
		totalPageGroupsMetric: {value: number} | null;
		userSessions: UserSession[];
	};
}

export interface UserSessionVariables {
	channelId: string;
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
	query UserSession(
		$channelId: String!
		$entityId: String!
		$entityType: EntityType!
		$keywords: String
		$page: Int!
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$size: Int!
	) {
		eventsByUserSessions(
			channelId: $channelId
			entityId: $entityId
			entityType: $entityType
			includeWebhookEvents: true
			keywords: $keywords
			page: $page
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			size: $size
		) {
			userSessions {
				... on UserSession {
					becameKnown
					browserName
					completeDate
					contentLanguageId
					createDate
					devicePixelRatio
					deviceType
					events {
						applicationId
						assetTitle
						canonicalUrl
						createDate
						eventDate
						eventId
						name
						pageDescription
						pageGroupId
						pageKeywords
						pageTitle
						properties {
							name
							value
						}
						referrer
						url
					}
					languageId
					screenHeight
					screenWidth
					timezoneOffset
					userAgent
				}
			}
			totalEvents
			totalPageGroupsMetric {
				value
			}
		}
	}
`;
