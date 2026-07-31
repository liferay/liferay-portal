import {ACCOUNTS_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';

/**
 * Known Accounts List Asset Query
 * @description Create a GraphQL query
 * @param {string} queryName
 * @param {string} metricName
 * @returns GraphQL query
 */
export default (queryName, metricName) => gql`
		query KnownAccountsListAssetQuery(
			$assetId: String!
			$channelId: String
			$devices: String
			$keywords: String
			$location: String
			$rangeEnd: String
			$rangeKey: Int
			$rangeStart: String
			$segmentId: String
			$size: Int!
			$start: Int!
			$title: String
			$touchpoint: String
		) {
			${queryName}(
				assetId: $assetId
				canonicalUrl: $touchpoint
				channelId: $channelId
				country: $location
				deviceType: $devices
				rangeEnd: $rangeEnd
				rangeKey: $rangeKey
				rangeStart: $rangeStart
				title: $title
			) {
				${metricName} {
					...accountsFragment
				}
			}
		}

		${ACCOUNTS_FRAGMENT}
	`;
