import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import Loading, {Align} from 'shared/components/Loading';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
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
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {
	SegmentPageViewsQuery,
	SegmentPageViewsQueryData,
	SegmentPageViewsQueryVariables,
} from 'shared/queries/SegmentPageViewsQuery';
import {sub} from 'shared/util/lang';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';

type Item = {
	children: Item[];
	disabled: boolean;
	id: string;
	name: string;
};

interface IFilterBySegment {
	onFilterChange: (item: Item | null) => void;
	rangeSelectors: RangeSelectors;
}

const filterBySegment: React.FC<IFilterBySegment> = ({
	onFilterChange,
	rangeSelectors,
}) => {
	const {channelId, groupId, title, touchpoint} = useParams();
	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME, getDefaultSortOrder(NAME)),
	});
	const [selectedItem, setSelectedItem] = useState<Item | null>(null);

	const {data, loading} = useRequest({
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

	const {data: segmentData, loading: segmentLoading} = useQuery<
		SegmentPageViewsQueryData,
		SegmentPageViewsQueryVariables
	>(SegmentPageViewsQuery, {
		fetchPolicy: 'network-only',
		skip: !data?.items.length,
		variables: {
			canonicalUrl: getSafeTouchpoint(touchpoint as string) ?? '',
			channelId: channelId as string,
			segmentIds: data?.items.map(({id}: any) => id),
			title: getSafeDecodedURIComponent(title as string),
			...getSafeRangeSelectors(rangeSelectors),
		},
	});

	const items = useMemo(
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
		<div className="d-flex justify-content-between w-100 analytics-segment-filter-root">
			<div className="align-items-center d-flex">
				<Dropdown
					channelId={channelId}
					groupId={groupId}
					items={items}
					loading={loading || segmentLoading}
					onFilterChange={(item: Item | null) => {
						setSelectedItem(item);

						onFilterChange(item);
					}}
				/>

				{selectedItem && (
					<ClayLabel
						className="ml-2"
						closeButtonProps={{
							'aria-label': Liferay.Language.get('close'),
							id: 'closeId',
							title: Liferay.Language.get('close'),
						}}
						large
						onClick={() => {
							setSelectedItem(null);
							onFilterChange(null);
						}}
					>
						{selectedItem.name}
					</ClayLabel>
				)}
			</div>

			{selectedItem && (
				<div className="d-flex">
					<ClayButton
						borderless
						data-tooltip
						data-tooltip-align="top"
						displayType="secondary"
						onClick={() => {
							setSelectedItem(null);
							onFilterChange(null);
						}}
						size="sm"
						title={Liferay.Language.get('remove-filter')}
					>
						<ClayIcon symbol="times-circle" />
					</ClayButton>

					<div className="divider" />
				</div>
			)}
		</div>
	);
};

const Dropdown = ({
	channelId,
	groupId,
	items,
	loading,
	onFilterChange,
}: any) => {
	const [value, setValue] = useState('');

	const filteredItems = useMemo(() => {
		if (!value) {
			return items;
		}

		return items.filter(
			({name}: any) => name.match(new RegExp(value, 'i')) !== null
		);
	}, [items, value]);

	return (
		<ClayDropDown
			closeOnClick
			trigger={
				<ClayButton
					borderless
					disabled={loading}
					displayType="secondary"
					size="sm"
				>
					{loading && <Loading align={Align.Left} />}

					{Liferay.Language.get('filter')}

					<ClayIcon className="ml-2" symbol="caret-bottom" />
				</ClayButton>
			}
		>
			<ClayDropDown.Search
				onChange={setValue}
				placeholder={Liferay.Language.get('search')}
			/>

			<ClayDropDown.ItemList
				items={[
					{
						children: filteredItems,
						id: 1,
						name: sub(Liferay.Language.get('filter-by-x'), [
							Liferay.Language.get('segment'),
						]),
					},
				]}
			>
				{(item: any) => (
					<ClayDropDown.Group
						header={item.name}
						items={item.children}
						key={item.name}
					>
						{(item: any) => (
							<ClayDropDown.Item
								disabled={item.disabled}
								key={item.name}
								onClick={() => {
									onFilterChange(item);
								}}
							>
								{item.name}
							</ClayDropDown.Item>
						)}
					</ClayDropDown.Group>
				)}
			</ClayDropDown.ItemList>

			{!!items.length && !filteredItems.length && (
				<ClayDropDown.Section>
					<NoResultsDisplay
						description={
							<div
								className="d-flex flex-column justify-content-center"
								style={{minHeight: 240}}
							>
								<div className="h4 no-results-title">
									{Liferay.Language.get(
										'no-results-were-found'
									)}
								</div>

								{Liferay.Language.get(
									'please-try-a-different-search-term'
								)}
							</div>
						}
						title={undefined}
					/>
				</ClayDropDown.Section>
			)}

			{!items.length && (
				<ClayDropDown.Section>
					<NoResultsDisplay
						description={
							<div
								className="d-flex flex-column justify-content-center"
								style={{minHeight: 240}}
							>
								<div className="h4 no-results-title">
									{Liferay.Language.get(
										'there-are-no-segments'
									)}
								</div>

								{Liferay.Language.get(
									'start-by-creating-a-segment'
								)}

								<ClayLink
									className="d-block mb-3"
									href={
										URLConstants.SegmentsDocumentationLink
									}
									key="DOCUMENTATION"
									target="_blank"
								>
									{Liferay.Language.get(
										'learn-more-about-segments'
									)}
								</ClayLink>
							</div>
						}
						title={undefined}
					/>
				</ClayDropDown.Section>
			)}

			<ClayDropDown.Section>
				<ClayLink
					block
					button
					displayType={
						!items.length || !filteredItems.length
							? 'primary'
							: 'secondary'
					}
					href={toRoute(Routes.CONTACTS_LIST_SEGMENT, {
						channelId,
						groupId,
						type: SEGMENTS,
					})}
					small
				>
					{Liferay.Language.get('create-segment')}
				</ClayLink>
			</ClayDropDown.Section>
		</ClayDropDown>
	);
};

export default filterBySegment;
