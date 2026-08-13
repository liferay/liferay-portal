import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import classNames from 'classnames';
import ClayEmptyState from '@clayui/empty-state';
import ClayTable from '@clayui/table';
import ClayTabs from '@clayui/tabs';
import GroupByPicker, {
	getGroupByLabels,
	GROUP_BY_METRICS,
	GROUP_BY_TO_METRIC,
	GroupByMetric,
	GroupByMetricField,
} from 'shared/components/GroupByPicker';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {getSafeRangeSelectors} from 'shared/util/util';
import {RangeSelectors, SafeRangeSelectors} from 'shared/types';
import {Text} from '@clayui/core';
import {toThousands} from 'shared/util/numbers';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

export interface ITopCategory {
	downloadsMetric?: {value: number};
	id: string;
	impressionsMetric?: {value: number};
	name: string;
	viewsMetric?: {value: number};
	vocabularyId: string;
	vocabularyName: string;
}

export interface ITopTag {
	downloadsMetric?: {value: number};
	id: string;
	impressionsMetric?: {value: number};
	name: string;
	viewsMetric?: {value: number};
}

export type TaxonomyItem = ITopCategory | ITopTag;

export interface ITopCategoriesAndTagsRequestVariables
	extends SafeRangeSelectors {
	channelId: string;
	groupId: string;
	isCategory: boolean;
	selectedMetric: GroupByMetricField;
}

const TABS = ['category', 'tag'] as const;

interface ITabContentProps {
	groupBy: GroupByMetric;
	isCategory: boolean;
	items: TaxonomyItem[];
	loading: boolean;
	selectedMetric: GroupByMetricField;
	setGroupBy: (metric: GroupByMetric) => void;
}

const TabContent: React.FC<ITabContentProps> = ({
	groupBy,
	isCategory,
	items,
	loading,
	selectedMetric,
	setGroupBy,
}) => {
	const groupByLabel = getGroupByLabels()[groupBy];

	const isEmpty = !loading && items.length === 0;

	return (
		<StatesRenderer empty={isEmpty} loading={loading}>
			<StatesRenderer.Loading />
			<StatesRenderer.Empty>
				<ClayEmptyState
					className="py-3 text-center"
					description={
						isCategory
							? Liferay.Language.get(
									'categories-will-appear-here-when-available'
								)
							: Liferay.Language.get(
									'tags-will-appear-here-when-available'
								)
					}
					small
					title={
						isCategory
							? Liferay.Language.get('no-categories-available')
							: Liferay.Language.get('no-tags-available')
					}
				/>
			</StatesRenderer.Empty>
			<StatesRenderer.Success>
				<GroupByPicker
					metrics={GROUP_BY_METRICS}
					onGroupByChange={setGroupBy}
					value={groupBy}
				/>

				<ClayTable className="mt-3">
					<ClayTable.Head>
						<ClayTable.Row>
							<ClayTable.Cell expanded headingCell>
								{isCategory
									? Liferay.Language.get('category-name')
									: Liferay.Language.get('tag-name')}
							</ClayTable.Cell>
							{isCategory && (
								<ClayTable.Cell headingCell>
									{Liferay.Language.get('vocabulary')}
								</ClayTable.Cell>
							)}
							<ClayTable.Cell headingCell>
								{groupByLabel}
							</ClayTable.Cell>
						</ClayTable.Row>
					</ClayTable.Head>
					<ClayTable.Body>
						{items.map((item) => (
							<ClayTable.Row key={item.id}>
								<ClayTable.Cell expanded>
									<Text size={3} weight="semi-bold">
										{item.name}
									</Text>
								</ClayTable.Cell>
								{isCategory && (
									<ClayTable.Cell>
										<Text size={3}>
											{
												(item as ITopCategory)
													.vocabularyName
											}
										</Text>
									</ClayTable.Cell>
								)}
								<ClayTable.Cell>
									{toThousands(
										item[selectedMetric]?.value ?? 0
									)}
								</ClayTable.Cell>
							</ClayTable.Row>
						))}
					</ClayTable.Body>
				</ClayTable>
			</StatesRenderer.Success>
		</StatesRenderer>
	);
};

interface ITopCategoriesAndTagsBaseCardProps {
	className?: string;
	dataSourceFn: (
		variables: any
	) => Promise<{items: TaxonomyItem[]}> | undefined;
	dataSourceParams: object;
	skipRequest?: boolean;
}

const TopCategoriesAndTagsBaseCard: React.FC<
	ITopCategoriesAndTagsBaseCardProps
> = ({className, dataSourceFn, dataSourceParams, skipRequest}) => (
	<BaseCard
		className={classNames('top-categories-and-tags', className)}
		label={Liferay.Language.get(
			'top-asset-categories-and-tags'
		).toUpperCase()}
		legacyDropdownRangeKey={false}
		minHeight={260}
	>
		{({rangeSelectors}: {rangeSelectors: RangeSelectors}) => (
			<TopCategoriesAndTagsWithData
				dataSourceFn={dataSourceFn}
				dataSourceParams={dataSourceParams}
				rangeSelectors={rangeSelectors}
				skipRequest={skipRequest}
			/>
		)}
	</BaseCard>
);

interface ITopCategoriesAndTagsWithDataProps
	extends Omit<ITopCategoriesAndTagsBaseCardProps, 'className'> {
	rangeSelectors: RangeSelectors;
}

const TopCategoriesAndTagsWithData: React.FC<
	ITopCategoriesAndTagsWithDataProps
> = ({dataSourceFn, dataSourceParams, rangeSelectors, skipRequest}) => {
	const {channelId, groupId} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const [activeTab, setActiveTab] = useState(0);
	const [groupBy, setGroupBy] = useState<GroupByMetric>(
		GroupByMetric.IMPRESSIONS
	);

	const selectedMetric = GROUP_BY_TO_METRIC[groupBy];

	const isCategory = TABS[activeTab] === 'category';

	const {data, loading} = useRequest<
		ITopCategoriesAndTagsRequestVariables,
		{items: TaxonomyItem[]}
	>({
		dataSourceFn,
		skipRequest,
		variables: {
			...getSafeRangeSelectors(rangeSelectors),
			...dataSourceParams,
			channelId,
			groupId,
			isCategory,
			selectedMetric,
		},
	});

	const items = data?.items ?? [];

	/**
	 * `useRequest` starts out loading and never settles while the request is
	 * skipped, so a card with nothing to scope by would spin forever. Report it
	 * as done instead, which lands the card on its empty state.
	 */

	const isLoading = loading && !skipRequest;

	const tabContent = (
		<TabContent
			groupBy={groupBy}
			isCategory={isCategory}
			items={items}
			loading={isLoading}
			selectedMetric={selectedMetric}
			setGroupBy={setGroupBy}
		/>
	);

	return (
		<Card.Body className="p-0">
			<ClayTabs active={activeTab} onActiveChange={setActiveTab}>
				<ClayTabs.Item>
					{Liferay.Language.get('category')}
				</ClayTabs.Item>
				<ClayTabs.Item>{Liferay.Language.get('tag')}</ClayTabs.Item>
			</ClayTabs>

			<ClayTabs.Content activeIndex={activeTab} fade>
				<ClayTabs.TabPane className="pb-0">
					{tabContent}
				</ClayTabs.TabPane>
				<ClayTabs.TabPane className="pb-0">
					{tabContent}
				</ClayTabs.TabPane>
			</ClayTabs.Content>
		</Card.Body>
	);
};

export default TopCategoriesAndTagsBaseCard;
