import {ACCOUNTS_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';

export default gql`
	query KnownAccountsListAssetQuery(
		$assetId: String!
		$devices: String
		$keywords: String
		$location: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
		$size: Int!
		$start: Int!
		$touchpoint: String
	) {
		objectEntry(
			assetId: $assetId
			canonicalUrl: $touchpoint
			country: $location
			deviceType: $devices
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
		) {
			viewsMetric {
				...accountsFragment
			}
		}
	}

	${ACCOUNTS_FRAGMENT}
`;
