import * as API from 'shared/api';
import classNames from 'classnames';
import FilterPickerTrigger from 'shared/components/FilterPickerTrigger';
import React, {useMemo, useState} from 'react';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {
	createOrderIOMap,
	getDefaultSortOrder,
	NAME,
} from 'shared/util/pagination';
import {
	getSafeDecodedURIComponent,
	getSafeRangeSelectors,
	getSafeTouchpoint,
	truncateText,
} from 'shared/util/util';
import {Option, Picker, Text} from '@clayui/core';
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

interface ISegmentItem {
	disabled?: boolean;
	displayName?: string;
	id: string | null;
	name: string;
}

const ALL_SEGMENTS_ITEM: ISegmentItem = {
	id: null,
	name: Liferay.Language.get('all-segments'),
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
	const [selectedKey, setSelectedKey] = useState<string>('null');

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

	const displayItems = useMemo(() => {
		const apiItems: ISegmentItem[] =
			data?.items.map((item: any) => {
				const selectedSegmentData = segmentData?.segmentPageViews.find(
					({segmentId}) => segmentId === item.id
				);

				return {
					...item,
					disabled: !selectedSegmentData?.views,
				};
			}) ?? [];

		return [ALL_SEGMENTS_ITEM, ...apiItems].map((item) => ({
			...item,
			displayName: truncateText(item.name, 35, null),
			id: item.id === null ? 'null' : String(item.id),
		}));
	}, [data, segmentData]);

	const handleSelectionChange = (key: string) => {
		setSelectedKey(key);

		if (key === 'null') {
			onFilterChange(null);

			return;
		}

		const selectedItem = displayItems.find((item) => item.id === key);

		onFilterChange(
			selectedItem ? {id: selectedItem.id, name: selectedItem.name} : null
		);
	};

	return (
		<ClayTooltipProvider>
			<div className={classNames('segment-filter-dropdown', className)}>
				<Picker
					aria-label={Liferay.Language.get('all-segments')}
					as={FilterPickerTrigger}
					className="border-light form-control-sm"
					items={displayItems}
					onSelectionChange={(key) =>
						handleSelectionChange(String(key))
					}
					searchable
					selectedKey={selectedKey}
				>
					{(item: ISegmentItem) => (
						<Option
							disabled={item.disabled}
							key={String(item.id)}
							textValue={item.name}
						>
							<div
								className="w-100"
								title={
									item.name.length > 35
										? item.name
										: undefined
								}
							>
								<Text size={3}>{item.displayName}</Text>
							</div>
						</Option>
					)}
				</Picker>
			</div>
		</ClayTooltipProvider>
	);
};

export default SegmentDropdown;
