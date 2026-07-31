import AssetAppearsOnQuery from 'shared/queries/AssetAppearsOnQuery';
import BaseCard from 'shared/components/base-card';
import ClayLink from '@clayui/link';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import FaroConstants from 'shared/util/constants';
import React, {useMemo, useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import Table from 'shared/components/table';
import TextTruncate from 'shared/components/TextTruncate';
import URLConstants from 'shared/util/url-constants';
import {AssetTypes} from 'shared/util/constants';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import {
	getSafeDecodedURIComponent,
	getSafeRangeSelectors,
} from 'shared/util/util';
import {getUrl} from 'shared/util/urls';
import {metricsListColumns} from 'shared/util/table-columns';
import {pickBy} from 'lodash';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {Routes} from 'shared/util/router';
import {useParams} from 'react-router-dom';
import {useQuery} from '@apollo/client';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

const {cur, delta, deltaValues} = FaroConstants.pagination;

export enum Accessor {
	DownloadsMetric = 'downloadsMetric',
	ImpressionMadeMetric = 'impressionMadeMetric',
	SubmissionsMetric = 'submissionsMetric',
	ViewsMetric = 'viewsMetric',
}

export enum EmptyStateLink {
	Blog = URLConstants.AssetsAppearsBlogsOnDocumentation,
	Document = URLConstants.AssetsAppearsDocumentsAndMediaOnDocumentation,
	Form = URLConstants.AssetsAppearsFormsOnDocumentation,
	Journal = URLConstants.AssetsAppearsWebContentOnDocumentation,
	ObjectEntry = URLConstants.AssetsCustomAssetsListDocumentation,
}

export const EmptyStateText = {
	Blog: Liferay.Language.get('learn-more-about-blogs'),
	Document: Liferay.Language.get('learn-more-about-documents-and-media'),
	Form: Liferay.Language.get('learn-more-about-forms'),
	Journal: Liferay.Language.get('learn-more-about-web-content'),
	ObjectEntry: Liferay.Language.get('learn-more-about-assets'),
} as const;

// eslint-disable-next-line no-redeclare
export type EmptyStateText =
	(typeof EmptyStateText)[keyof typeof EmptyStateText];

interface IAssetAppearsOnCardProps {
	accessors: Accessor[];
	assetType: AssetTypes;
	emptyStateLink: EmptyStateLink;
	emptyStateText: EmptyStateText;
}

export const AssetAppearsOnCard: React.FC<IAssetAppearsOnCardProps> = ({
	accessors,
	assetType,
	emptyStateLink,
	emptyStateText,
}) => (
	<BaseCard
		label={Liferay.Language.get('asset-appears-on')}
		legacyDropdownRangeKey={false}
		minHeight={536}
		reportContainer={ReportContainer.AssetAppearsOnCard}
	>
		{({accountId, rangeSelectors, segmentId}) => (
			<AssetAppearsOnStateRenderer
				accessors={accessors}
				accountId={accountId}
				assetType={assetType}
				emptyStateLink={emptyStateLink}
				emptyStateText={emptyStateText}
				rangeSelectors={rangeSelectors}
				segmentId={segmentId}
			/>
		)}
	</BaseCard>
);

const AssetAppearsOnStateRenderer = ({
	accessors,
	accountId,
	assetType,
	emptyStateLink,
	emptyStateText,
	rangeSelectors,
	segmentId,
}: any) => {
	const {assetId, channelId, title} = useParams();
	const [pagination, setPagination] = useState({
		page: cur,
		size: delta,
		start: (cur - 1) * delta,
	});

	const {data, error, loading} = useQuery(AssetAppearsOnQuery, {
		fetchPolicy: 'network-only',
		variables: {
			accountId,
			assetId,
			assetType:
				assetType === AssetTypes.ObjectEntry
					? 'OBJECT_ENTRY'
					: assetType.toUpperCase(),
			segmentId,
			selectedMetrics: accessors,
			...(assetType !== AssetTypes.ObjectEntry && {
				channelId,
				title: getSafeDecodedURIComponent(title as string),
			}),
			...pagination,
			...getSafeRangeSelectors(rangeSelectors),
		},
	});

	return (
		<StatesRenderer
			empty={!data?.assetPages.total}
			error={!!error}
			loading={loading}
		>
			<StatesRenderer.Loading />
			<StatesRenderer.Empty
				description={
					<>
						<span className="mr-1">
							{Liferay.Language.get(
								'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
							)}
						</span>

						<ClayLink
							href={emptyStateLink}
							key="DOCUMENTATION"
							target="_blank"
						>
							{emptyStateText}
						</ClayLink>
					</>
				}
				showIcon={false}
				title={Liferay.Language.get(
					'there-are-no-assets-on-the-selected-period'
				)}
			/>
			<StatesRenderer.Error apolloError={error}>
				<ErrorDisplay />
			</StatesRenderer.Error>
			<StatesRenderer.Success>
				<AssetApperarsOnContentCard
					accessors={accessors}
					data={data}
					onPaginationChange={setPagination}
					pagination={pagination}
				/>
			</StatesRenderer.Success>
		</StatesRenderer>
	);
};

const formatItems = (data: any) =>
	data.assetPages.assetMetrics.map(
		({assetId, assetTitle, selectedMetrics}: any) => ({
			title: assetTitle ? assetTitle : assetId,
			touchpoint: assetId,
			...selectedMetrics.reduce((acc: any, {name, value}: any) => {
				acc[name] = value;

				return acc;
			}, {}),
		})
	);

const AssetApperarsOnContentCard = ({
	accessors,
	data,
	onPaginationChange,
	pagination,
}: any) => {
	const {channelId, groupId} = useParams();
	const rangeSelectors = useQueryRangeSelectors();

	const items = useMemo(() => formatItems(data), [data]);

	return (
		<>
			<Table
				className="mb-3 table-hover"
				columns={getTableColumns({
					accessors,
					channelId,
					groupId,
					rangeSelectors,
				})}
				items={items}
				rowIdentifier={['touchpoint', 'title']}
			/>

			<ClayPaginationBarWithBasicItems
				active={pagination.page}
				activeDelta={pagination.size}
				className="px-3 pb-2"
				deltas={deltaValues.map((delta) => ({label: delta}))}
				onActiveChange={(page) =>
					onPaginationChange({
						...pagination,
						page,
						start: (page - 1) * pagination.size,
					})
				}
				onDeltaChange={(size) =>
					onPaginationChange({...pagination, size})
				}
				totalItems={data?.assetPages.total}
			/>
		</>
	);
};

const getTableColumns = ({
	accessors,
	channelId,
	groupId,
	rangeSelectors,
}: any) => {
	const generateURL = ({title, touchpoint}: any) => {
		const router = {
			params: {
				channelId,
				groupId,
				title,
				touchpoint: encodeURIComponent(touchpoint),
			},
			query: {
				...pickBy(rangeSelectors),
			},
		};

		return getUrl(Routes.SITES_TOUCHPOINTS_OVERVIEW, router);
	};

	const tableColumns = [
		{
			accessor: 'title',
			cellRenderer: ({data}: any) => {
				const url = generateURL(data);

				return (
					<td className="table-cell-expand">
						<ClayLink
							className="font-weight-semibold text-truncate-inline text-dark"
							href={url}
						>
							<TextTruncate title={data.title} />
						</ClayLink>
					</td>
				);
			},
			className: 'table-cell-expand',
			label: Liferay.Language.get('page-name'),
			sortable: false,
			title: true,
		},
		{
			accessor: 'url',
			cellRenderer: ({data}: any) => (
				<td className="table-cell-expand">
					<ClayLink
						className="text-secondary text-truncate-inline"

						// @ts-ignore
						externalLink
						href={data.touchpoint}
						target="_blank"
					>
						<TextTruncate title={data.touchpoint} />
					</ClayLink>
				</td>
			),
			className: 'table-cell-expand',
			label: Liferay.Language.get('canonical-url'),
			sortable: false,
		},
		...accessors.map((accessor: Accessor) => ({
			...metricsListColumns[accessor],
			sortable: false,
		})),
	];

	return tableColumns;
};
