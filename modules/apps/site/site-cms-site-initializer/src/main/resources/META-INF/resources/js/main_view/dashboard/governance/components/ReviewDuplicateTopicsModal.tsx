/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {
	EConfigInURLBehavior,
	FrontendDataSet,
	IItemsActions,
	TSort,
	getItemActionURL,
} from '@liferay/frontend-data-set-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

import dateFormat from '../../../../common/utils/dateFormat';
import {getFileMimeTypeObjectDefinitionStickerValue} from '../../../props_transformer/utils/transformViewsItemProps';
import GovernanceService, {
	DuplicateTitle,
	DuplicateTopicAsset,
} from '../GovernanceService';
import {GovernanceAdditionalProps} from '../types';

const API_URL = '/o/search/v1.0/search';

const EDIT_ACTION_ID = 'edit';

const FDS_ID = 'cmsGovernanceDuplicateTopics';

const SORTS: TSort[] = [
	{direction: 'asc', key: 'title', label: Liferay.Language.get('title')},
];

const DUPLICATE_TOPICS_NESTED_FIELDS =
	'embedded,file.metadata,systemProperties.objectDefinitionBrief';

function getDatePattern() {
	return {
		day: 'numeric',
		hour: 'numeric',
		minute: 'numeric',
		month: 'numeric',
		timeZone: Liferay.ThemeDisplay.getTimeZone(),
		year: 'numeric',
	} as const;
}

function AssetItem({
	additionalProps,
	asset,
	itemsActions = [],
}: {
	additionalProps?: GovernanceAdditionalProps;
	asset: DuplicateTopicAsset;
	itemsActions?: IItemsActions[];
}) {
	const editURL = getItemActionURL(itemsActions, EDIT_ACTION_ID, asset);

	return (
		<ClayList.Item flex>
			<ClayList.ItemField>
				<ClaySticker className="text-secondary">
					<ClayIcon
						symbol={
							additionalProps
								? getFileMimeTypeObjectDefinitionStickerValue(
										additionalProps.fileMimeTypeIcons,
										additionalProps.objectDefinitionIcons,
										asset
									)
								: 'document-text'
						}
					/>
				</ClaySticker>
			</ClayList.ItemField>

			<ClayList.ItemField expand>
				<ClayList.ItemTitle>{asset.title}</ClayList.ItemTitle>

				<ClayList.ItemText>
					{`${Liferay.Language.get('last-modified')} ${dateFormat(
						getDatePattern(),
						asset.dateModified
					)}`}
				</ClayList.ItemText>
			</ClayList.ItemField>

			{editURL ? (
				<ClayList.ItemField>
					<ClayButtonWithIcon
						aria-label={sub(Liferay.Language.get('edit-x'), [
							asset.title,
						])}
						className="border-0"
						displayType="secondary"
						onClick={() => navigate(editURL)}
						symbol="pencil"
						title={Liferay.Language.get('edit')}
					/>
				</ClayList.ItemField>
			) : null}
		</ClayList.Item>
	);
}

export function DuplicateTopicsList({
	additionalProps,
	items = [],
	itemsActions = [],
	titles = [],
}: {
	additionalProps?: GovernanceAdditionalProps;
	items?: DuplicateTopicAsset[];
	itemsActions?: IItemsActions[];
	titles?: DuplicateTitle[];
}) {
	const groups = useMemo(() => {
		const assetsByTitle = new Map<string, DuplicateTopicAsset[]>();

		for (const asset of items) {
			const term = asset.title.toLowerCase();

			assetsByTitle.set(term, [
				...(assetsByTitle.get(term) ?? []),
				asset,
			]);
		}

		return [...assetsByTitle.entries()];
	}, [items]);

	const frequencies = useMemo(
		() => new Map(titles.map(({frequency, term}) => [term, frequency])),
		[titles]
	);

	return (
		<ClayList className="mb-0">
			{groups.map(([term, groupAssets]) => {
				const frequency = frequencies.has(term)
					? frequencies.get(term)
					: groupAssets.length;

				return (
					<React.Fragment key={term}>
						<ClayList.Header>
							{`${groupAssets[0].title} (${frequency})`}
						</ClayList.Header>

						{groupAssets.map((asset, index) => (
							<AssetItem
								additionalProps={additionalProps}
								asset={asset}
								itemsActions={itemsActions}
								key={asset.embedded?.id ?? `${term}-${index}`}
							/>
						))}
					</React.Fragment>
				);
			})}
		</ClayList>
	);
}

export default function ReviewDuplicateTopicsModal({
	additionalProps,
	closeModal,
	entryClassNames,
	siteId,
}: {
	additionalProps?: GovernanceAdditionalProps;
	closeModal: () => void;
	entryClassNames: string;
	siteId?: number;
}) {
	const [titles, setTitles] = useState<DuplicateTitle[]>();

	useEffect(() => {
		const controller = new AbortController();

		async function fetchTitles() {
			const titles = await GovernanceService.getDuplicateTitles({
				entryClassNames,
				signal: controller.signal,
				siteId,
			});

			if (!controller.signal.aborted) {
				setTitles(titles ?? []);
			}
		}

		fetchTitles();

		return () => controller.abort();
	}, [entryClassNames, siteId]);

	const filterQueryString = useMemo(() => {
		if (!titles?.length) {
			return '';
		}

		const filters = [
			`(${titles
				.map(({term}) => `title eq '${term.replace(/'/g, "''")}'`)
				.join(' or ')})`,
		];

		if (siteId) {
			filters.push(`groupIds/any(g:g eq ${siteId})`);
		}

		return `${new URLSearchParams({filter: filters.join(' and ')})}`;
	}, [siteId, titles]);

	const itemsActions: IItemsActions[] = useMemo(
		() => [
			{
				data: {id: EDIT_ACTION_ID},
				href: `${Liferay.ThemeDisplay.getPathMain()}/cms/edit_content_item?objectEntryId={embedded.id}&redirect=${encodeURIComponent(
					window.location.href
				)}`,
				icon: 'pencil',
				label: Liferay.Language.get('edit'),
			},
		],
		[]
	);

	const views = useMemo(
		() => [
			{
				component: ({
					items,
					itemsActions,
				}: {
					items?: DuplicateTopicAsset[];
					itemsActions?: IItemsActions[];
				}) => (
					<DuplicateTopicsList
						additionalProps={additionalProps}
						items={items}
						itemsActions={itemsActions}
						titles={titles}
					/>
				),
				default: true,
				label: Liferay.Language.get('list'),
				name: 'list',
				thumbnail: 'list',
			},
			{
				contentRenderer: 'table',
				label: Liferay.Language.get('table'),
				name: 'table',
				schema: {
					fields: [
						{
							fieldName: 'title',
							label: Liferay.Language.get('title'),
							sortable: true,
						},
						{
							contentRenderer: 'dateTime',
							fieldName: 'dateModified',
							label: Liferay.Language.get('last-modified'),
							sortable: false,
						},
					],
				},
				thumbnail: 'table',
			},
		],
		[additionalProps, titles]
	);

	const duplicatedCount = (titles ?? []).reduce(
		(count, {frequency}) => count + frequency,
		0
	);

	return (
		<>
			<ClayModal.Header>
				{sub(Liferay.Language.get('review-x-duplicated-topics'), [
					duplicatedCount,
				])}
			</ClayModal.Header>

			<ClayModal.Body>
				{!titles ? (
					<ClayLoadingIndicator displayType="secondary" size="md" />
				) : null}

				{titles && !titles.length ? (
					<ClayEmptyState
						description={Liferay.Language.get(
							'there-are-no-duplicated-topics-in-the-selected-spaces'
						)}
						imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state.svg`}
						title={Liferay.Language.get('no-duplicated-topics-yet')}
					/>
				) : null}

				{filterQueryString ? (
					<FrontendDataSet
						additionalAPIURLParameters={`emptySearch=true&entryClassNames=${entryClassNames}&nestedFields=${DUPLICATE_TOPICS_NESTED_FIELDS}`}
						apiURL={`${API_URL}?${filterQueryString}`}
						configInURLBehavior={EConfigInURLBehavior.OFF}
						hideManagementBarInEmptyState={true}
						id={FDS_ID}
						itemsActions={itemsActions}
						pagination={{initialDelta: 20}}
						showManagementBar={true}
						showPagination={true}
						showSearch={true}
						sorts={SORTS}
						style="fluid"
						views={views}
					/>
				) : null}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton displayType="secondary" onClick={closeModal}>
						{Liferay.Language.get('cancel')}
					</ClayButton>
				}
			/>
		</>
	);
}
