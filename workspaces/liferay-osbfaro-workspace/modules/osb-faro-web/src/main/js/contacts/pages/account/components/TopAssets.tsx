import * as API from 'shared/api';
import Card from 'shared/components/Card';
import classNames from 'classnames';
import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import ClayTable from '@clayui/table';
import ClayTabs from '@clayui/tabs';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import {getMimeType} from 'assets/components/mime-type';
import {IAccount} from './AccountInfo';
import {ITopAsset, TopAssetMetric, TopAssetObjectType} from 'shared/api/assets';
import {Option, Picker, Text} from '@clayui/core';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useHistory, useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

interface ITopAssetsProps {
	account?: IAccount;
	className?: string;
}

enum GroupByMetric {
	DOWNLOADS = 'downloads',
	IMPRESSIONS = 'impressions',
	VIEWS = 'views',
}

const GROUP_BY_TO_METRIC: Record<GroupByMetric, TopAssetMetric> = {
	[GroupByMetric.DOWNLOADS]: 'downloadsMetric',
	[GroupByMetric.IMPRESSIONS]: 'impressionsMetric',
	[GroupByMetric.VIEWS]: 'viewsMetric',
};

const TABS = ['content', 'files'] as const;

// TODO(LPD-91217): confirm `objectType` values once backend lands.

const TAB_OBJECT_TYPES: Record<(typeof TABS)[number], TopAssetObjectType> = {
	content: 'content',
	files: 'file',
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

const GroupByTrigger = React.forwardRef<
	HTMLButtonElement,
	React.ButtonHTMLAttributes<HTMLButtonElement> & {label?: string}
>(({label, ...rest}, ref) => (
	<ClayButton
		{...rest}
		className="rounded-lg"
		displayType="secondary"
		ref={ref}
		size="sm"
	>
		{label}
		<ClayIcon
			className="inline-item inline-item-after"
			symbol="caret-bottom"
		/>
	</ClayButton>
));

interface ITopAssetsTabContentProps {
	accountId?: string;
	accountName?: string;
	assets: ITopAsset[];
	groupBy: GroupByMetric;
	isFiles: boolean;
	loading: boolean;
	metrics: GroupByMetric[];
	setGroupBy: (metric: GroupByMetric) => void;
}

const TopAssetsTabContent: React.FC<ITopAssetsTabContentProps> = ({
	accountId,
	accountName,
	assets,
	groupBy,
	isFiles,
	loading,
	metrics,
	setGroupBy,
}) => {
	const {channelId, groupId} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const groupByLabels: Record<GroupByMetric, string> = {
		[GroupByMetric.DOWNLOADS]: Liferay.Language.get('downloads'),
		[GroupByMetric.IMPRESSIONS]: Liferay.Language.get('impressions'),
		[GroupByMetric.VIEWS]: Liferay.Language.get('views'),
	};

	const groupByLabel = groupByLabels[groupBy];
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
				<div className="align-items-center d-flex">
					<div className="font-weight-semi-bold mr-2">
						<Text size={3}>{Liferay.Language.get('group-by')}</Text>
					</div>

					<Picker
						aria-label={Liferay.Language.get('group-by')}
						as={GroupByTrigger}
						items={metrics}
						label={groupByLabel}
						onSelectionChange={(key) =>
							setGroupBy(key as GroupByMetric)
						}
						selectedKey={groupBy}
					>
						{(key: GroupByMetric) => (
							<Option key={key}>{groupByLabels[key]}</Option>
						)}
					</Picker>
				</div>

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
								{accountId, accountName},
								toRoute(getAssetRoute(asset.assetType), {
									assetId: asset.id,
									channelId,
									groupId,
									touchpoint: 'overview',
									...(asset.assetType && {
										type: encodeURIComponent(
											asset.assetType
										),
									}),
									...(asset.assetTitle && {
										title: encodeURIComponent(
											asset.assetTitle
										),
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
											asset[selectedMetric].value
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

const TopAssets: React.FC<ITopAssetsProps> = ({account, className}) => {
	const history = useHistory();
	const {channelId, groupId} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const accountId = account?.id;
	const accountName = account?.accountName;

	const [activeTab, setActiveTab] = useState(0);
	const [groupBy, setGroupBy] = useState<GroupByMetric>(
		GroupByMetric.IMPRESSIONS
	);

	const selectedMetric = GROUP_BY_TO_METRIC[groupBy];

	const metrics = TAB_GROUP_BY_METRICS[TABS[activeTab]];

	const handleActiveTabChange = (index: number) => {
		setActiveTab(index);

		if (!TAB_GROUP_BY_METRICS[TABS[index]].includes(groupBy)) {
			setGroupBy(GroupByMetric.IMPRESSIONS);
		}
	};

	const {data, loading} = useRequest<
		Parameters<typeof API.assets.fetchAccountTopAssets>[0],
		{items: ITopAsset[]}
	>({
		dataSourceFn: API.assets.fetchAccountTopAssets,
		skipRequest: !accountId,
		variables: {
			accountId: accountId!,
			channelId,
			groupId,
			objectType: TAB_OBJECT_TYPES[TABS[activeTab]],
			selectedMetric,
		},
	});

	const assets = data?.items ?? [];

	const tabContent = (
		<TopAssetsTabContent
			accountId={accountId}
			accountName={accountName}
			assets={assets}
			groupBy={groupBy}
			isFiles={TABS[activeTab] === 'files'}
			loading={loading}
			metrics={metrics}
			setGroupBy={setGroupBy}
		/>
	);

	return (
		<Card className={classNames('top-assets', className)} minHeight={260}>
			<Card.Title className="p-3">
				<Text weight="semi-bold">
					{Liferay.Language.get('top-assets').toUpperCase()}
				</Text>
			</Card.Title>
			<Card.Body className="p-0">
				<ClayTabs
					active={activeTab}
					onActiveChange={handleActiveTabChange}
				>
					<ClayTabs.Item>
						{Liferay.Language.get('content')}
					</ClayTabs.Item>
					<ClayTabs.Item>
						{Liferay.Language.get('files')}
					</ClayTabs.Item>
				</ClayTabs>

				<ClayTabs.Content activeIndex={activeTab} fade>
					<ClayTabs.TabPane className="pb-0">
						{tabContent}
					</ClayTabs.TabPane>
					<ClayTabs.TabPane className="pb-0">
						{tabContent}
					</ClayTabs.TabPane>
				</ClayTabs.Content>

				{assets.length > 0 && (
					<div className="d-flex p-3">
						<ClayButton
							borderless
							className="ml-auto rounded-lg"
							onClick={() =>
								history.push(
									setUriQueryValues(
										{
											accountId,
											orderBy: selectedMetric,
											...(accountName && {accountName}),
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
		</Card>
	);
};

export default TopAssets;
