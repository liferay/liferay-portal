import * as API from 'shared/api';
import FilterPicker, {IFilterPickerItem} from './FilterPicker';
import React, {useMemo} from 'react';
import {
	createOrderIOMap,
	getDefaultSortOrder,
	NAME,
} from 'shared/util/pagination';
import {useParams} from 'react-router-dom';

interface ISegmentDropdownProps {
	className?: string;
	initialSegmentId?: string | null;
	initialSegmentName?: string | null;

	/**
	 * Options owned by the caller. The Path tab's chart uses this to layer its
	 * own "disable segments with no page views for this URL" enrichment on top
	 * of this component instead of the default fetch-all-segments behavior.
	 */

	items?: IFilterPickerItem[];

	/**
	 * Loading state owned by the caller. Only meaningful together with `items`.
	 */

	loading?: boolean;
	onFilterChange: (item: IFilterPickerItem | null) => void;
}

/**
 * `individual-segment.search` requires an `orderIOMap`, unlike the account
 * filter's endpoint. This dropdown always lists segments sorted by name, so
 * the map is fixed rather than configurable.
 */

const DEFAULT_ORDER_IO_MAP = createOrderIOMap(NAME, getDefaultSortOrder(NAME));

const SegmentDropdown: React.FC<ISegmentDropdownProps> = ({
	className,
	initialSegmentId,
	initialSegmentName,
	items,
	loading,
	onFilterChange,
}) => {
	const {channelId, groupId} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	// The segment comes from the URL, which may name one that is not on the
	// fetched page, so it is passed as the selection rather than looked up.

	const selected = useMemo(
		() =>
			initialSegmentId
				? {
						id: initialSegmentId,
						name: initialSegmentName || initialSegmentId,
					}
				: null,
		[initialSegmentId, initialSegmentName]
	);

	// A caller passing `items` owns its own data (see the prop doc above), so
	// the default fetch is skipped in favor of theirs.

	const hasOwnItems = items !== undefined;

	return (
		<FilterPicker
			className={className}
			dataSourceFn={
				hasOwnItems ? undefined : API.individualSegment.search
			}
			entityLabel={Liferay.Language.get('segments')}
			items={items}
			loading={loading}
			onFilterChange={onFilterChange}
			selected={selected}
			variables={{
				channelId,
				groupId,
				orderIOMap: DEFAULT_ORDER_IO_MAP,
			}}
		/>
	);
};

export default SegmentDropdown;
