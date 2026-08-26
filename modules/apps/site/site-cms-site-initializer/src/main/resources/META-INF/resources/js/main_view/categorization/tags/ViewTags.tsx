/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayLink from '@clayui/link';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {navigate, sub} from 'frontend-js-web';
import React, {ComponentProps} from 'react';

import {ActionDropdownItemProps} from '../../../common/components/Breadcrumb';
import {openCMSModal} from '../../../common/utils/openCMSModal';
import MultipleScopesRenderer from '../../props_transformer/cell_renderers/MultipleScopesRenderer';
import {executeAsyncItemAction} from '../../props_transformer/utils/executeAsyncItemAction';
import CategorizationToolbar from '../CategorizationToolbar';
import CreateTagsModal from './CreateTagsModal';
import EditTagsModal from './EditTagsModal';
import MergeTagsModal from './MergeTagsModal';

export default function ViewTags({
	actionItems,
	cmpEnabled,
	cmsGroupId,
	dataSetId,
	invalidTagCharacters,
	tagUsagesURL,
	tagsURL,
	vocabulariesURL,
}: {
	actionItems: ComponentProps<typeof ClayDropDownWithItems>['items'] &
		ActionDropdownItemProps;
	cmpEnabled?: boolean;
	cmsGroupId: number;
	dataSetId: string;
	invalidTagCharacters: string;
	tagUsagesURL: string;
	tagsURL: string;
	vocabulariesURL: string;
}) {
	const NAME_TABLE_CELL_RENDERER_NAME = 'NameTableCellRenderer';
	const VIEWS_SCOPE_TABLE_CELL_RENDERER_NAME = 'ViewsScopeTableCellRenderer';

	const creationMenu = {
		primaryItems: [
			{
				label: Liferay.Language.get('new'),
				onClick: () => {
					openCMSModal({
						contentComponent: ({
							closeModal,
						}: {
							closeModal: () => void;
						}) =>
							CreateTagsModal({
								closeModal,
								cmpEnabled,
								cmsGroupId,
								dataSetId,
								invalidTagCharacters,
							}),
						size: 'md',
					});
				},
			},
		],
	};

	const filters = [
		{
			apiURL: "/o/headless-asset-library/v1.0/asset-libraries?filter=type eq 'Space'",
			entityFieldType: 'string',
			id: 'groupIds',
			itemKey: 'siteId',
			itemLabel: 'name',
			label: Liferay.Language.get('space'),
			multiple: true,
			type: 'selection',
		},
		...(cmpEnabled && Liferay.FeatureFlags['LPD-99403']
			? [
					{
						apiURL: "/o/search/v1.0/search?emptySearch=true&nestedFields=embedded&filter=objectDefinitionExternalReferenceCode eq 'l_cmp_project' and status in (0, 1)",
						entityFieldType: 'string',
						id: 'projects',
						itemKey: 'embedded.scopeId',
						itemLabel: 'embedded.title',
						label: Liferay.Language.get('project'),
						multiple: true,
						type: 'selection',
					},
				]
			: []),
	];

	const views = [
		{
			contentRenderer: 'table',
			default: true,
			label: Liferay.Language.get('table'),
			name: 'table',
			schema: {
				fields: [
					{
						contentRenderer: NAME_TABLE_CELL_RENDERER_NAME,
						fieldName: 'name',
						label: Liferay.Language.get('title'),
						sortable: true,
					},
					{
						contentRenderer: VIEWS_SCOPE_TABLE_CELL_RENDERER_NAME,
						fieldName: 'assetLibraries',
						label: Liferay.Language.get('scope'),
						sortable: false,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateModified',
						label: Liferay.Language.get('modified'),
						sortable: false,
					},
				],
			},
			thumbnail: 'table',
		},
	];

	const emptyState = {
		description: Liferay.Language.get('click-new-to-create-your-first-tag'),
		image: '/states/cms_empty_state_categorization.svg',
		title: Liferay.Language.get('no-tags-yet'),
	};

	const deleteTag = ({
		itemData,
		loadData,
	}: {
		itemData: any;
		loadData: () => {};
	}) => {
		openCMSModal({
			bodyHTML: Liferay.Language.get(
				'are-you-sure-you-want-to-delete-this-tag'
			),
			buttons: [
				{
					autoFocus: true,
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					displayType: 'danger',
					label: Liferay.Language.get('delete'),
					onClick: ({processClose}: {processClose: Function}) => {
						executeAsyncItemAction({
							method: itemData.actions.delete.method,
							refreshData: loadData,
							successMessage: sub(
								Liferay.Language.get(
									'x-was-deleted-successfully'
								),
								`<strong>${Liferay.Util.escapeHTML(itemData.name)}</strong>`
							),
							url: itemData.actions.delete.href,
						});

						processClose();
					},
				},
			],
			center: true,
			status: 'danger',
			title: sub(
				Liferay.Language.get('delete-x'),
				Liferay.Language.get('tag')
			),
		});
	};

	const editTag = ({
		itemData,
		loadData,
	}: {
		itemData: any;
		loadData: () => {};
	}) => {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				EditTagsModal({
					assetLibraries: itemData.assetLibraries,
					closeModal,
					cmpEnabled,
					editTagURL: itemData.actions.replace.href,
					loadData,
					projects: itemData.projects,
					tagId: itemData.id,
					tagName: itemData.name,
				}),
			size: 'md',
		});
	};

	const mergeTag = ({
		itemData,
		loadData,
	}: {
		itemData: any;
		loadData: () => {};
	}) => {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				MergeTagsModal({
					closeModal,
					cmsGroupId,
					loadData,
					selectIntoTags: [
						{label: itemData.name, value: itemData.id},
					],
				}),
			id: 'mergeModal',
			size: 'md',
		});
	};

	const onActionDropdownItemClick = ({
		action,
		itemData,
		loadData,
	}: {
		action: any;
		itemData: any;
		loadData: () => {};
	}) => {
		if (action.id === 'deleteTag') {
			deleteTag({itemData, loadData});
		}
		else if (action.id === 'mergeTag') {
			mergeTag({itemData, loadData});
		}
		else if (action.id === 'editTag') {
			editTag({itemData, loadData});
		}
		else if (action.id === 'viewUsages') {
			navigate(`${tagUsagesURL}?keywordName=${itemData.name}`);
		}
	};

	return (
		<>
			<CategorizationToolbar
				actionItems={actionItems}
				activeTab="tags"
				tagsURL={tagsURL}
				vocabulariesURL={vocabulariesURL}
			/>

			<FrontendDataSet
				apiURL={`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`}
				creationMenu={creationMenu}
				customRenderers={{
					tableCell: [
						{
							component: ({
								itemData,
								loadData,
								value,
							}: {
								itemData: any;
								loadData: () => {};
								value: string;
							}) => (
								<div className="table-list-title">
									<ClayLink
										data-senna-off
										href="#"
										onClick={(event: React.MouseEvent) => {
											event.preventDefault();

											editTag({itemData, loadData});
										}}
									>
										{value}
									</ClayLink>
								</div>
							),
							name: NAME_TABLE_CELL_RENDERER_NAME,
							type: 'internal',
						},
						{
							component: (props: any) =>
								MultipleScopesRenderer({...props, cmpEnabled}),
							name: VIEWS_SCOPE_TABLE_CELL_RENDERER_NAME,
							type: 'internal',
						},
					],
				}}
				emptyState={emptyState}
				filters={filters}
				hideManagementBarInEmptyState={true}
				id={dataSetId}
				itemsActions={[
					{
						data: {
							permissionKey: 'replace',
						},
						icon: 'pencil',
						id: 'editTag',
						label: Liferay.Language.get('edit'),
					},
					{
						data: {
							permissionKey: 'get',
						},
						icon: 'list-ul',
						id: 'viewUsages',
						label: Liferay.Language.get('view-usages'),
					},
					{
						data: {
							permissionKey: 'get',
						},
						icon: 'merge',
						id: 'mergeTag',
						label: Liferay.Language.get('merge'),
					},
					{
						className: 'text-danger',
						data: {
							permissionKey: 'delete',
						},
						icon: 'trash',
						id: 'deleteTag',
						label: Liferay.Language.get('delete'),
					},
				]}
				onActionDropdownItemClick={onActionDropdownItemClick}
				pagination={{initialDelta: 20}}
				searchAsYouType={true}
				searchSuggestionsEnabled={true}
				showManagementBar={true}
				showPagination={true}
				showSearch={true}
				views={views}
			/>
		</>
	);
}
