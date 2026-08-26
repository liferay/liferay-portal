import {gql} from '@apollo/client';
import {SessionEntityTypes} from 'shared/util/constants';

export interface AccountUserSessionEvent {
	applicationId: string;
	assetTitle: string;
	canonicalUrl: string;
	createDate: string;
	eventDate: string;
	eventId: string;
	name: string;
	pageDescription: string;
	pageGroupId?: string | null;
	pageKeywords: string;
	pageTitle: string;
	properties: Array<{name: string; value: string}>;
	referrer: string;
	url: string;
}

export interface AccountUserSession {
	browserName: string;
	completeDate: string | null;
	contentLanguageId: string;
	createDate: string;
	devicePixelRatio: number;
	deviceType: string;
	events: AccountUserSessionEvent[];
	individualId: string | null;
	languageId: string;
	screenHeight: number;
	screenWidth: number;
	timezoneOffset: string;
	userAgent: string;
	userId: string | null;
	userName: string | null;
}

export interface AccountUserSessionData {
	eventsByUserSessions: {
		totalPageGroupsMetric: {value: number} | null;
		userSessions: AccountUserSession[];
	};
}

export interface AccountUserSessionVariables {
	accountId: string;
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
	query AccountUserSessions(
		$accountId: String!
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
			accountId: $accountId
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
			totalPageGroupsMetric {
				value
			}
			userSessions {
				... on UserSession {
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
					individualId
					languageId
					screenHeight
					screenWidth
					timezoneOffset
					userAgent
					userId
					userName
				}
			}
		}
	}
`;
