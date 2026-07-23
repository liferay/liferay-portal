import {COMPOSITION_FRAGMENT} from 'shared/queries/fragments';
import {gql} from '@apollo/client';
import {SafeRangeSelectors} from 'shared/types';

export interface AcquisitionsQueryData {
	acquisitions: {
		acquisitions: {
			compositions: {
				count: number;
				name: string;
			};
			maxCount: number;
			total: number;
			totalCount: number;
		};
	};
}

export interface AcquisitionsQueryVariables extends SafeRangeSelectors {
	accountId?: string | null;
	activeTabId: string;
	channelId?: string;
	size: number;
	start: number;
}

export default gql`
	query Acquisitions(
		$accountId: String
		$activeTabId: AcquisitionType!
		$channelId: String
		$rangeEnd: String
		$rangeKey: Int
		$rangeStart: String
		$size: Int!
		$start: Int!
	) {
		acquisitions(
			accountId: $accountId
			acquisitionType: $activeTabId
			channelId: $channelId
			rangeEnd: $rangeEnd
			rangeKey: $rangeKey
			rangeStart: $rangeStart
			size: $size
			start: $start
		) {
			...compositionFragment
		}
	}

	${COMPOSITION_FRAGMENT}
`;
