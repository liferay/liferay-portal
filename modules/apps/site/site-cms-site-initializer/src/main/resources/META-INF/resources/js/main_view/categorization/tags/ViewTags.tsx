/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React from 'react';

import MultipleSpacesRenderer from '../../props_transformer/cell_renderers/MultipleSpacesRenderer';
import {executeAsyncItemAction} from '../../props_transformer/utils/executeAsyncItemAction';
import CategorizationToolbar from '../CategorizationToolbar';
import CreateTagsModal from './CreateTagsModal';
import EditTagsModal from './EditTagsModal';
import MergeTagsModal from './MergeTagsModal';

export default function ViewTags({
	cmsGroupId,
	dataSetId,
	tagUsagesURL,
	tagsURL,
	vocabulariesURL,
}: {
	cmsGroupId: number;
	dataSetId: string;
	tagUsagesURL: string;
	tagsURL: string;
	vocabulariesURL: string;
}) {
	const VIEWS_SPACE_TABLE_CELL_RENDERER_NAME = 'ViewsSpaceTableCellRenderer';

	const creationMenu = {
		primaryItems: [
			{
				label: Liferay.Language.get('new'),
				onClick: () => {
					openModal({
						contentComponent: ({
							closeModal,
						}: {
							closeModal: () => void;
						}) =>
							CreateTagsModal({
								closeModal,
								cmsGroupId,
								dataSetId,
							}),
						size: 'md',
					});
				},
			},
		],
	};

	const filters = [
		{
			apiURL: '/o/headless-asset-library/v1.0/asset-libraries',
			entityFieldType: 'string',
			id: 'groupIds',
			itemKey: 'id',
			itemLabel: 'name',
			label: 'Space',
			multiple: true,
			type: 'selection',
		},
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
						fieldName: 'name',
						label: Liferay.Language.get('title'),
						sortable: true,
					},
					{
						contentRenderer: VIEWS_SPACE_TABLE_CELL_RENDERER_NAME,
						fieldName: 'assetLibraries',
						label: Liferay.Language.get('space'),
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
		openModal({
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
		openModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				EditTagsModal({
					assetLibraries: itemData.assetLibraries,
					closeModal,
					editTagURL: itemData.actions.replace.href,
					loadData,
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
		openModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				MergeTagsModal({
					closeModal,
					cmsGroupId,
					loadData,
					tagId: itemData.id,
					tagName: itemData.name,
				}),
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
		<div className="categorization-section">
			<CategorizationToolbar
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
							component: MultipleSpacesRenderer,
							name: VIEWS_SPACE_TABLE_CELL_RENDERER_NAME,
							type: 'internal',
						},
					],
				}}
				emptyState={emptyState}
				filters={filters}
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
						icon: 'null',
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
				showManagementBar={true}
				showPagination={true}
				showSearch={true}
				views={views}
			/>
		</div>
	);
}
