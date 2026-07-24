import * as API from 'shared/api';
import classNames from 'classnames';
import FilterPicker, {FilterPickerItem} from 'shared/components/FilterPicker';
import React, {useMemo} from 'react';
import {
	createOrderIOMap,
	getDefaultSortOrder,
	NAME,
} from 'shared/util/pagination';
import {
	getSafeDecodedURIComponent,
	getSafeRangeSelectors,
	getSafeTouchpoint,
} from 'shared/util/util';
import {RangeSelectors} from 'shared/types';
import {
	SegmentPageViewsQuery,
	SegmentPageViewsQueryData,
	SegmentPageViewsQueryVariables,
} from 'shared/queries/SegmentPageViewsQuery';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';

type Item = {
	id: string;
	name: string;
};

interface ISegmentDropdownProps {
	className?: string;
	onFilterChange: (item: Item | null) => void;
	rangeSelectors: RangeSelectors;
}

const SegmentDropdown: React.FC<ISegmentDropdownProps> = ({
	className,
	onFilterChange,
	rangeSelectors,
}) => {
	const {channelId, groupId, title, touchpoint} = useParams<{
		channelId: string;
		groupId: string;
		title: string;
		touchpoint: string;
	}>();
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME, getDefaultSortOrder(NAME)),
	});

	const {data} = useRequest({
		dataSourceFn: API.individualSegment.search,
		variables: {
			channelId,
			delta,
			groupId,
			orderIOMap,
			page,
			query,
		},
	});

	const {data: segmentData} = useQuery<
		SegmentPageViewsQueryData,
		SegmentPageViewsQueryVariables
	>(SegmentPageViewsQuery, {
		fetchPolicy: 'network-only',
		skip: !data?.items.length,
		variables: {
			canonicalUrl: getSafeTouchpoint(touchpoint) ?? '',
			channelId,
			segmentIds: data?.items.map(({id}: any) => id),
			title: getSafeDecodedURIComponent(title),
			...getSafeRangeSelectors(rangeSelectors),
		},
	});

	const items: FilterPickerItem[] = useMemo(
		() =>
			data?.items.map((item: any) => {
				const selectedSegmentData = segmentData?.segmentPageViews.find(
					({segmentId}) => segmentId === item.id
				);

				return {
					...item,
					disabled: !selectedSegmentData?.views,
				};
			}) ?? [],
		[data, segmentData]
	);

	return (
		<FilterPicker
			allItemsLabel={Liferay.Language.get('all-segments')}
			className={classNames('segment-filter-dropdown', className)}
			items={items}
			onFilterChange={onFilterChange}
		/>
	);
};

export default SegmentDropdown;
