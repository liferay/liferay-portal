import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import classNames from 'classnames';
import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import ClayTable from '@clayui/table';
import ClayTabs from '@clayui/tabs';
import GroupByPicker, {
	getGroupByLabels,
	GROUP_BY_TO_METRIC,
	GroupByMetric,
} from 'shared/components/GroupByPicker';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {AssetObjectTypes} from 'shared/util/constants';
import {getMimeType} from 'assets/components/mime-type';
import {getSafeRangeSelectors} from 'shared/util/util';
import {ITopAsset, TopAssetMetric} from 'shared/api/assets';
import {RangeSelectors, SafeRangeSelectors} from 'shared/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const TABS = ['content', 'files'] as const;

const TAB_OBJECT_TYPES: Record<(typeof TABS)[number], AssetObjectTypes> = {
	content: AssetObjectTypes.Content,
	files: AssetObjectTypes.File,
};

const TAB_GROUP_BY_METRICS: Record<(typeof TABS)[number], GroupByMetric[]> = {
	content: [GroupByMetric.IMPRESSIONS, GroupByMetric.VIEWS],
	files: [
		GroupByMetric.DOWNLOADS,
		GroupByMetric.IMPRESSIONS,
		GroupByMetric.VIEWS,
	],
};

const ASSET_ROUTE_MAP = {
	blog: Routes.ASSETS_BLOGS_OVERVIEW,
	document: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
	form: Routes.ASSETS_FORMS_OVERVIEW,
	webContent: Routes.ASSETS_WEB_CONTENT_OVERVIEW,
} as const;

const getAssetRoute = (assetType?: string) =>
	ASSET_ROUTE_MAP[assetType as keyof typeof ASSET_ROUTE_MAP] ??
	Routes.ASSETS_OBJECT_ENTRY_OVERVIEW;

export interface ITopAssetsRequestVariables extends SafeRangeSelectors {
	channelId: string;
	groupId: string;
	objectType: AssetObjectTypes;
	selectedMetric: TopAssetMetric;
}

interface ITopAssetsTabContentProps {
	assets: ITopAsset[];
	groupBy: GroupByMetric;
	isFiles: boolean;
	loading: boolean;
	metrics: GroupByMetric[];
	routeQueries: {[key: string]: any};
	setGroupBy: (metric: GroupByMetric) => void;
}

const TopAssetsTabContent: React.FC<ITopAssetsTabContentProps> = ({
	assets,
	groupBy,
	isFiles,
	loading,
	metrics,
	routeQueries,
	setGroupBy,
}) => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const groupByLabel = getGroupByLabels()[groupBy];
	const selectedMetric = GROUP_BY_TO_METRIC[groupBy];

	const isEmpty = !loading && assets.length === 0;

	return (
		<StatesRenderer empty={isEmpty} loading={loading}>
			<StatesRenderer.Loading />
			<StatesRenderer.Empty>
				<ClayEmptyState
					className="py-3 text-center"
					description={
						isFiles
							? Liferay.Language.get(
									'files-will-appear-here-when-available'
								)
							: Liferay.Language.get(
									'assets-will-appear-here-when-available'
								)
					}
					small
					title={
						isFiles
							? Liferay.Language.get('no-files-available')
							: Liferay.Language.get('no-assets-available')
					}
				/>
			</StatesRenderer.Empty>
			<StatesRenderer.Success>
				<GroupByPicker
					metrics={metrics}
					onGroupByChange={setGroupBy}
					value={groupBy}
				/>

				<ClayTable className="mt-3">
					<ClayTable.Head>
						<ClayTable.Row>
							<ClayTable.Cell headingCell>
								{Liferay.Language.get('title')}
							</ClayTable.Cell>
							<ClayTable.Cell headingCell>
								{groupByLabel}
							</ClayTable.Cell>
						</ClayTable.Row>
					</ClayTable.Head>
					<ClayTable.Body>
						{assets.map((asset) => {
							const mimeType = getMimeType({
								assetType: asset.assetType,
								mimeType: asset.mimeType,
							});

							const href = setUriQueryValues(
								routeQueries,
								toRoute(getAssetRoute(asset.assetType), {
									assetId: asset.id,
									channelId,
									groupId,
									touchpoint: 'overview',
									...(asset.assetType && {
										type: asset.assetType,
									}),
									...(asset.assetTitle && {
										title: asset.assetTitle,
									}),
								})
							);

							return (
								<ClayTable.Row key={asset.id}>
									<ClayTable.Cell expanded>
										<div className="align-items-center d-flex">
											<div className="mr-3">
												<ClaySticker
													className={classNames(
														mimeType.className
													)}
													displayType="unstyled"
												>
													<ClayIcon
														symbol={mimeType.icon}
													/>
												</ClaySticker>
											</div>
											<ClayLink
												className="font-weight-semi-bold text-dark"
												href={href}
											>
												{asset.assetTitle}
											</ClayLink>
										</div>
									</ClayTable.Cell>
									<ClayTable.Cell>
										{toThousands(
											asset[selectedMetric]?.value ?? 0
										)}
									</ClayTable.Cell>
								</ClayTable.Row>
							);
						})}
					</ClayTable.Body>
				</ClayTable>
			</StatesRenderer.Success>
		</StatesRenderer>
	);
};

interface ITopAssetsBaseCardProps {
	className?: string;
	dataSourceFn: (variables: any) => Promise<{items: ITopAsset[]}> | undefined;
	dataSourceParams: object;
	routeQueries: {[key: string]: any};
	showViewAll?: boolean;
	skipRequest?: boolean;
}

const TopAssetsBaseCard: React.FC<ITopAssetsBaseCardProps> = ({
	className,
	dataSourceFn,
	dataSourceParams,
	routeQueries,
	showViewAll = true,
	skipRequest,
}) => (
	<BaseCard
		className={classNames('top-assets', className)}
		label={Liferay.Language.get('top-assets').toUpperCase()}
		legacyDropdownRangeKey={false}
		minHeight={260}
	>
		{({rangeSelectors}: {rangeSelectors: RangeSelectors}) => (
			<TopAssetsWithData
				dataSourceFn={dataSourceFn}
				dataSourceParams={dataSourceParams}
				rangeSelectors={rangeSelectors}
				routeQueries={routeQueries}
				showViewAll={showViewAll}
				skipRequest={skipRequest}
			/>
		)}
	</BaseCard>
);

interface ITopAssetsWithDataProps
	extends Omit<ITopAssetsBaseCardProps, 'className'> {
	rangeSelectors: RangeSelectors;
}

const TopAssetsWithData: React.FC<ITopAssetsWithDataProps> = ({
	dataSourceFn,
	dataSourceParams,
	rangeSelectors,
	routeQueries,
	showViewAll,
	skipRequest,
}) => {
	const history = useHistoryAdapter();
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const [activeTab, setActiveTab] = useState(0);
	const [groupBy, setGroupBy] = useState<GroupByMetric>(
		GroupByMetric.IMPRESSIONS
	);

	const selectedMetric = GROUP_BY_TO_METRIC[groupBy];

	const objectType = TAB_OBJECT_TYPES[TABS[activeTab]];

	const metrics = TAB_GROUP_BY_METRICS[TABS[activeTab]];

	const handleActiveTabChange = (index: number) => {
		setActiveTab(index);

		if (!TAB_GROUP_BY_METRICS[TABS[index]].includes(groupBy)) {
			setGroupBy(GroupByMetric.IMPRESSIONS);
		}
	};

	const {data, loading} = useRequest<
		ITopAssetsRequestVariables,
		{items: ITopAsset[]}
	>({
		dataSourceFn,
		skipRequest,
		variables: {
			...getSafeRangeSelectors(rangeSelectors),
			...dataSourceParams,
			channelId,
			groupId,
			objectType,
			selectedMetric,
		},
	});

	const assets = data?.items ?? [];

	/**
	 * `useRequest` starts out loading and never settles while the request is
	 * skipped, so a card with nothing to scope by would spin forever. Report it
	 * as done instead, which lands the card on its empty state.
	 */

	const isLoading = loading && !skipRequest;

	const tabContent = (
		<TopAssetsTabContent
			assets={assets}
			groupBy={groupBy}
			isFiles={TABS[activeTab] === 'files'}
			loading={isLoading}
			metrics={metrics}
			routeQueries={routeQueries}
			setGroupBy={setGroupBy}
		/>
	);

	return (
		<Card.Body className="p-0">
			<ClayTabs active={activeTab} onActiveChange={handleActiveTabChange}>
				<ClayTabs.Item>{Liferay.Language.get('content')}</ClayTabs.Item>
				<ClayTabs.Item>{Liferay.Language.get('files')}</ClayTabs.Item>
			</ClayTabs>

			<ClayTabs.Content activeIndex={activeTab} fade>
				<ClayTabs.TabPane className="pb-0">
					{tabContent}
				</ClayTabs.TabPane>
				<ClayTabs.TabPane className="pb-0">
					{tabContent}
				</ClayTabs.TabPane>
			</ClayTabs.Content>

			{showViewAll && assets.length > 0 && (
				<div className="d-flex p-3">
					<ClayButton
						borderless
						className="ml-auto rounded-lg"
						onClick={() =>
							history.push(
								setUriQueryValues(
									{
										...routeQueries,
										objectType,
										orderBy: selectedMetric,
									},
									toRoute(Routes.ASSETS, {
										channelId,
										groupId,
									})
								)
							)
						}
						size="sm"
					>
						{Liferay.Language.get('view-all')}
					</ClayButton>
				</div>
			)}
		</Card.Body>
	);
};

export default TopAssetsBaseCard;
