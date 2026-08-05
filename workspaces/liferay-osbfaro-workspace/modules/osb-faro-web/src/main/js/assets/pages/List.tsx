import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import FaroConstants, {RangeKeyTimeRanges} from 'shared/util/constants';
import React, {useMemo, useState} from 'react';
import URLConstants from 'shared/util/url-constants';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {FrontendDataSet, pagination} from 'shared/components/FrontendDataSet';
import {getMimeType} from 'assets/components/mime-type';
import {InfoPanel} from 'assets/components/InfoPanel';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {
	removeUriQueryParam,
	Routes,
	setUriQueryValues,
	toRoute,
} from 'shared/util/router';
import {toThousands} from 'shared/util/numbers';
import {useChannelContext} from 'shared/context/channel';
import {useHistory, useLocation, useParams} from 'react-router-dom';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const {cur: DEFAULT_CUR} = FaroConstants.pagination;

const mapRoutes = {
	blog: Routes.ASSETS_BLOGS_OVERVIEW,
	document: Routes.ASSETS_DOCUMENTS_AND_MEDIA_OVERVIEW,
	form: Routes.ASSETS_FORMS_OVERVIEW,
	webContent: Routes.ASSETS_WEB_CONTENT_OVERVIEW,
};

const getAssetURL = ({
	accountId,
	accountName,
	channelId,
	groupId,
	itemData,
	rangeSelectorParams,
	segmentId,
	segmentName,
	value = '',
}: {
	accountId?: string | null;
	accountName?: string | null;
	channelId: string;
	groupId: string;
	itemData: any;
	rangeSelectorParams: string;
	segmentId?: string | null;
	segmentName?: string | null;
	value?: string;
}) => {
	const assetTitle = value || itemData.assetTitle || itemData.id;

	const oldAssetRoute =
		mapRoutes[itemData.assetType as keyof typeof mapRoutes];

	const route = oldAssetRoute ?? Routes.ASSETS_OBJECT_ENTRY_OVERVIEW;

	const queryParams = new URLSearchParams(rangeSelectorParams);

	if (accountId) {
		queryParams.set('accountId', accountId);
	}

	if (accountName) {
		queryParams.set('accountName', accountName);
	}

	if (segmentId) {
		queryParams.set('segmentId', segmentId);
	}

	if (segmentName) {
		queryParams.set('segmentName', segmentName);
	}

	return `${toRoute(route, {
		assetId: itemData.id,
		channelId,
		groupId,
		touchpoint: 'Any',
		...(itemData.assetType && {
			type: encodeURIComponent(itemData.assetType),
		}),
		...(assetTitle && {
			title: encodeURIComponent(assetTitle),
		}),
	})}?${queryParams.toString()}`;
};

const columns = {
	assetMetricRenderer: ({value}: {value: {value: number}}) => (
		<span>{toThousands(value.value)}</span>
	),
	assetTitleRenderer:
		({
			accountId,
			accountName,
			channelId,
			groupId,
			rangeSelectorParams,
			segmentId,
			segmentName,
		}: {
			accountId?: string | null;
			accountName?: string | null;
			channelId: string;
			groupId: string;
			rangeSelectorParams: string;
			segmentId?: string | null;
			segmentName?: string | null;
		}) =>
		({itemData, value}: {itemData: any; value?: string}) => {
			const URL = getAssetURL({
				accountId,
				accountName,
				channelId,
				groupId,
				itemData,
				rangeSelectorParams,
				segmentId,
				segmentName,
				value,
			});

			const mimeType = getMimeType({
				assetType: itemData?.assetType,
				mimeType: itemData?.mimeType,
			});

			return (
				<div className="align-items-center d-flex">
					<div className="mr-3">
						<ClaySticker
							className={mimeType.className}
							displayType="dark"
						>
							<ClayIcon symbol={mimeType.icon} />
						</ClaySticker>
					</div>

					<div>
						<ClayLink displayType="tertiary" href={URL}>
							{value || itemData.id}
						</ClayLink>
					</div>
				</div>
			);
		},
};

const assetsEmptyStateDescription = (
	<>
		<span className="mr-1">
			{Liferay.Language.get(
				'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
			)}
		</span>

		<ClayLink
			href={URLConstants.AssetsDefinitionDocumentation}
			key="DOCUMENTATION"
			target="_blank"
		>
			{Liferay.Language.get('learn-more-about-assets')}
		</ClayLink>
	</>
);

const TABLE_FIELDS = [
	{
		contentRenderer: 'assetTitleRenderer',
		fieldName: 'assetTitle',
		label: Liferay.Language.get('title'),
		sortable: true,
		truncate: true,
	},
	{
		fieldName: 'assetType',
		label: Liferay.Language.get('type'),
		sortable: true,
	},
	{
		contentRenderer: 'assetMetricRenderer',
		fieldName: 'viewsMetric',
		label: Liferay.Language.get('views'),
		sortable: true,
	},
	{
		contentRenderer: 'assetMetricRenderer',
		fieldName: 'impressionsMetric',
		label: Liferay.Language.get('impressions'),
		sortable: true,
	},
	{
		contentRenderer: 'assetMetricRenderer',
		fieldName: 'downloadsMetric',
		label: Liferay.Language.get('downloads'),
		sortable: true,
	},
];

const List = () => {
	const history = useHistory();
	const {search} = useLocation();
	const {selectedChannel} = useChannelContext();
	const {channelId, groupId} = useParams();
	const initialRangeSelectors = useQueryRangeSelectors();
	const LDPEnabled = useLDPEnabled({groupId: groupId!});

	const searchParams = new URLSearchParams(search);
	const accountId = searchParams.get('accountId');
	const accountName = searchParams.get('accountName');
	const orderBy = searchParams.get('orderBy');
	const segmentId = searchParams.get('segmentId');
	const segmentName = searchParams.get('segmentName');

	const sortableFields = TABLE_FIELDS.filter((field) => field.sortable);

	const sorts = sortableFields.some((field) => field.fieldName === orderBy)
		? sortableFields.map((field) => ({
				active: field.fieldName === orderBy,
				direction: 'desc' as const,
				key: field.fieldName,
				label: field.label,
			}))
		: undefined;

	const [rangeSelectors, setRangeSelectors] = useState<RangeSelectors>(
		initialRangeSelectors
	);

	const [infoPanelData, setInfoPanelData] = useState<any>(null);

	let rangeSelectorParams = `rangeKey=${rangeSelectors.rangeKey}`;

	if (rangeSelectors.rangeKey === RangeKeyTimeRanges.CustomRange) {
		rangeSelectorParams =
			`rangeEnd=${rangeSelectors.rangeEnd}` +
			`&rangeStart=${rangeSelectors.rangeStart}`;
	}

	const filters = useMemo(
		() => [
			...(LDPEnabled
				? [
						{
							apiURL: `/o/faro/contacts/${groupId}/account/search?channelId=${channelId}&filter=(rangeKey eq '${rangeSelectors.rangeKey}')`,
							autocompleteEnabled: true,
							entityFieldType: 'string',
							id: 'accountIds',
							itemKey: 'id',
							itemLabel: 'accountName',
							label: Liferay.Language.get('accounts'),
							multiple: true,
							...(accountId && {
								preloadedData: {
									selectedItems: [
										{
											label: accountName || accountId,
											value: accountId,
										},
									],
								},
							}),
							type: 'selection',
						},
						{
							apiURL: `/o/faro/contacts/${groupId}/individual_segment/search?channelId=${channelId}&${rangeSelectorParams}`,
							autocompleteEnabled: true,
							entityFieldType: 'string',
							id: 'segmentIds',
							itemKey: 'id',
							itemLabel: 'name',
							label: Liferay.Language.get('segments'),
							multiple: true,
							...(segmentId && {
								preloadedData: {
									selectedItems: [
										{
											label: segmentName || segmentId,
											value: segmentId,
										},
									],
								},
							}),
							type: 'selection',
						},
					]
				: []),
			{
				apiURL: `/o/faro/contacts/${groupId}/asset-summary-types?channelId=${channelId}&${rangeSelectorParams}`,
				autocompleteEnabled: true,
				entityFieldType: 'string',
				id: 'assetType',
				itemKey: 'name',
				itemLabel: 'name',
				label: Liferay.Language.get('type'),
				multiple: true,
				type: 'selection',
			},
			{
				apiURL: `/o/faro/contacts/${groupId}/asset-summary-tags?channelId=${channelId}&${rangeSelectorParams}`,
				autocompleteEnabled: true,
				entityFieldType: 'string',
				id: 'tags/id',
				itemKey: 'id',
				itemLabel: 'name',
				label: Liferay.Language.get('tags'),
				multiple: true,
				type: 'selection',
			},
			{
				apiURL: `/o/faro/contacts/${groupId}/asset-summary-categories?channelId=${channelId}&${rangeSelectorParams}`,
				autocompleteEnabled: true,
				entityFieldType: 'string',
				id: 'categories/id',
				itemKey: 'id',
				itemLabel: 'name',
				label: Liferay.Language.get('categories'),
				multiple: true,
				type: 'selection',
			},
			{
				apiURL: `/o/faro/contacts/${groupId}/asset-summary-mime-types?channelId=${channelId}&${rangeSelectorParams}`,
				autocompleteEnabled: true,
				entityFieldType: 'string',
				id: 'mimeType',
				itemKey: 'id',
				itemLabel: 'name',
				label: Liferay.Language.get('file-type'),
				multiple: true,
				type: 'selection',
			},
		],
		[
			accountId,
			accountName,
			channelId,
			groupId,
			LDPEnabled,
			rangeSelectorParams,
			segmentId,
			segmentName,
		]
	);

	return (
		<BasePage documentTitle={Liferay.Language.get('assets')}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
				]}
				fluid
				groupId={groupId!}
			>
				<BasePage.Header.TitleSection
					title={Liferay.Language.get('assets')}
				/>
			</BasePage.Header>

			<BasePage.SubHeader fluid>
				<div className="d-flex justify-content-end w-100">
					<DropdownRangeKey
						legacy={false}
						onRangeSelectorChange={(rangeSelectors) => {
							history.push(
								setUriQueryValues(
									pickBy({
										page: DEFAULT_CUR,
										...rangeSelectors,
									}),
									removeUriQueryParam(
										window.location.href,
										'rangeEnd',
										'rangeStart'
									)
								)
							);

							setRangeSelectors(rangeSelectors);
						}}
						rangeSelectors={rangeSelectors}
					/>
				</div>
			</BasePage.SubHeader>

			<BasePage.Body fluid sidebarOpened={!!infoPanelData}>
				<Card minHeight={300}>
					<FrontendDataSet
						apiURL={`/o/faro/contacts/${groupId}/asset-summary?channelId=${channelId}&${rangeSelectorParams}`}
						customDataRenderers={{
							assetMetricRenderer: columns.assetMetricRenderer,
							assetTitleRenderer: columns.assetTitleRenderer({
								accountId,
								accountName,
								channelId: channelId!,
								groupId: groupId!,
								rangeSelectorParams,
								segmentId,
								segmentName,
							}),
						}}
						emptyState={{
							description:
								assetsEmptyStateDescription as unknown as string,
							image: '/states/satellite.svg',
							title: Liferay.Language.get('no-assets-were-found'),
						}}
						filters={filters}
						groupedFilters={[
							{
								filters: [
									'assetType',
									'tags/id',
									'categories/id',
									'mimeType',
								],
								label: Liferay.Language.get('filter-by'),
							},
							...(LDPEnabled
								? [
										{
											filters: [
												'accountIds',
												'segmentIds',
											],
											label: Liferay.Language.get(
												'filter-by-people'
											),
										},
									]
								: []),
						]}
						id="assetTable"
						itemsActions={[
							{
								data: {
									id: 'infoPanel',
								},
								icon: 'info-circle-open',
								label: Liferay.Language.get('show-details'),
								onClick: setInfoPanelData,
							},
							{
								data: {
									id: 'viewAsset',
								},
								icon: 'view',
								label: Liferay.Language.get('view'),
								onClick: ({itemData}: {itemData: any}) => {
									history.push(
										getAssetURL({
											accountId,
											accountName,
											channelId: channelId!,
											groupId: groupId!,
											itemData,
											rangeSelectorParams,
											segmentId,
											segmentName,
										})
									);
								},
							},
						]}

						// Trick to restart FDS every time the rangeSelectors changes.

						key={Object.values(rangeSelectors).join()}
						pagination={pagination}
						showPagination
						snapshotsEnabled
						sorts={sorts}
						views={[
							{
								contentRenderer: 'table',
								default: true,
								label: Liferay.Language.get('default-view'),
								name: 'table',
								schema: {
									fields: TABLE_FIELDS,
								},
								thumbnail: 'table',
							},
						]}
					/>
				</Card>

				<InfoPanel
					data={infoPanelData}
					onClose={() => setInfoPanelData(null)}
				/>
			</BasePage.Body>
		</BasePage>
	);
};

export default List;
