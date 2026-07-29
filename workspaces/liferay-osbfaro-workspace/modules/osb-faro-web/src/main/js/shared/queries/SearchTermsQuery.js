import {COMPOSITION_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';

export default gql`
	query SearchTerms(
		$accountId: String
		$channelId: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$segmentId: String
		$size: Int!
		$start: Int!
	) {
		searchTerms(
			accountId: $accountId
			channelId: $channelId
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			segmentId: $segmentId
			size: $size
			start: $start
		) {
			...compositionFragment
		}
	}

	${COMPOSITION_FRAGMENT}
`;
