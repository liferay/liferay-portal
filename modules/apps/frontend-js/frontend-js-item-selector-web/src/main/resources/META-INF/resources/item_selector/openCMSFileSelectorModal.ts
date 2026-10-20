/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {render} from '@liferay/frontend-js-react-web';
import {mimeTypeUtils} from 'frontend-js-web';
import React from 'react';

import CMSFileUploaderComponent from '../item_selector_file_uploader/CMSFileUploaderComponent';
import DetachedCMSFilesItemSelectorModal from './DetachedCMSFilesItemSelectorModal';
import {IItemSelectorModalProps} from './ItemSelectorModal';
import {
	getCMSItemSelectorFilters,
	getCMSItemSelectorGroupedFilters,
} from './getCMSItemSelectorFilters';

interface CMSFile {
	description: string;
	embedded: {
		file?: {
			link?: {
				href?: string;
			};
			mimeType: string;
			thumbnailURL: string;
		};
		id: number;
		title: string;
		videoURL?: string;
	};
	title: string;
}

type CMSFileItemSelectorModalProps = IItemSelectorModalProps<CMSFile>;

type CMSFileItemSelectorModalConfig = {
	apiURL: CMSFileItemSelectorModalProps['apiURL'];
	items: CMSFileItemSelectorModalProps['items'];
	locator: CMSFileItemSelectorModalProps['locator'];
	multiSelect: CMSFileItemSelectorModalProps['multiSelect'];
};

function urlBuilder({
	base = location.origin,
	filters = [],
	folderId,
	resource = '/o/search/v1.0/search',
}: {
	base?: string;
	filters?: string[];
	folderId?: number | null;
	resource?: string;
}) {
	const finalURL = new URL(resource, base);

	const scopePredicates = folderId
		? [`(folderId eq ${folderId})`]
		: [
				'(cmsRoot eq true)',
				"(cmsSection eq 'files')",
				'(rootDescendantNode eq false)',
			];

	const filter = [...scopePredicates, '(status in (0, 2, 3))']
		.concat(filters.filter(Boolean))
		.join(' and ');

	finalURL.search = new URLSearchParams({
		emptySearch: 'true',
		filter,
		nestedFields: 'description,embedded,file.mimeType,file.thumbnailURL',
	}).toString();

	return finalURL.toString();
}

const CMS_FILE_ITEM_SELECTOR_CONFIG: CMSFileItemSelectorModalConfig = {
	apiURL: urlBuilder({}),
	items: [],
	locator: {
		id: 'embedded.id',
		label: 'embedded.title',
		value: 'embedded.id',
	},
	multiSelect: false,
};

const FDS_PROPS: Omit<
	CMSFileItemSelectorModalProps['fdsProps'],
	'filters' | 'id' | 'items'
> = {
	configInURLBehavior: EConfigInURLBehavior.OFF,
	pagination: {
		deltas: [{label: 20}, {label: 40}, {label: 60}],
		initialDelta: 20,
	},
	views: [
		{
			contentRenderer: 'cards',
			label: Liferay.Language.get('cards'),
			name: 'cards',
			schema: {
				description: 'description',
				symbol: '',
				title: 'title',
			},

			setItemComponentProps: ({
				item,
				props,
			}: {
				item: Pick<CMSFile, 'embedded'>;
				props: object;
			}) => {
				const fallbackStickerProps = {
					stickerProps: {
						className: 'file-icon-color-5',
						displayType: 'unstyled',
					},
				};

				if (item.embedded.file) {
					const mimeType = item.embedded.file.mimeType || '';

					return {
						...props,
						imgProps: {src: item.embedded.file.thumbnailURL},
						stickerProps: {
							className:
								mimeTypeUtils.getClassNameFromMimeType(
									mimeType
								),
							content: React.createElement(ClayIcon, {
								symbol: mimeTypeUtils.getIconFromMimeType(
									mimeType
								),
							}),
							displayType: 'unstyled',
						},
					};
				}

				if (item.embedded.videoURL) {
					return {
						...props,
						stickerProps: {
							className: 'file-icon-color-3',
							content: React.createElement(ClayIcon, {
								symbol: 'document-multimedia',
							}),
							displayType: 'unstyled',
						},
					};
				}

				return {
					...props,
					...fallbackStickerProps,
				};
			},

			thumbnail: 'cards2',
		},
		{
			contentRenderer: 'table',
			label: Liferay.Language.get('table'),
			name: 'table',
			schema: {
				fields: [
					{
						contentRenderer: 'cmsFilesTitleCellRenderer',
						fieldName: 'title',
						label: Liferay.Language.get('title'),
						sortable: true,
					},
					{
						contentRenderer: 'cmsFilesFallbackCellRenderer',
						fieldName: 'embedded.file.mimeType',
						label: Liferay.Language.get('type'),
						sortable: false,
					},
					{
						contentRenderer: 'cmsFilesFallbackCellRenderer',
						fieldName: 'embedded.file.size',
						label: Liferay.Language.get('size'),
						sortable: false,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateModified',
						label: Liferay.Language.get('modified-date'),
						sortable: true,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateCreated',
						label: Liferay.Language.get('create-date'),
						sortable: true,
					},
				],
			},
			thumbnail: 'table',
		},
	],
};

function getRandomId(): string {
	return Math.random().toString(36).substring(2, 9);
}

function normalizeExtensions(allowedExtensions: string) {
	const cleanExtensions = allowedExtensions
		.split(',')
		.map((item) => item.trim().replace(/^\./, ''))
		.filter(Boolean);

	if (cleanExtensions.length < 1) {
		return '';
	}

	const extensions = cleanExtensions.map((item) => `'${item}'`).join(',');

	return `(extension in (${extensions}) or objectDefinitionExternalReferenceCode eq 'L_OBJECT_ENTRY_FOLDER')`;
}

export default function openCMSFileSelectorModal({
	allowDragAndDrop = false,
	allowedExtensions,
	config,
	fdsProps,
	filters,
	folderMemoryKey,
	groupId,
	itemTypeLabel,
	maxFileSize,
	onSelect,
}: {
	allowDragAndDrop?: boolean;
	allowedExtensions?: string;
	config?: Partial<CMSFileItemSelectorModalConfig>;
	fdsProps?: Partial<CMSFileItemSelectorModalProps['fdsProps']>;
	filters?: string[];

	/**
	 * Stable identifier of the field opening the selector. When provided, the
	 * selector reopens at the folder where this field's previous selection was
	 * made. Each field must pass its own key so the remembered folder is not
	 * shared between fields.
	 */
	folderMemoryKey?: string;
	groupId?: number;
	itemTypeLabel?: string;
	maxFileSize?: number;
	onSelect: (items: Array<CMSFile>) => void;
}) {
	let effectiveFilters: string[] = [];

	if (filters?.length) {
		effectiveFilters = filters;
	}
	else if (allowedExtensions) {
		effectiveFilters = [normalizeExtensions(allowedExtensions)];
	}

	const buildApiURL = (folderId: number | null) =>
		urlBuilder({
			filters: effectiveFilters,
			folderId: folderId ?? undefined,
		});

	const finalConfig = {
		...CMS_FILE_ITEM_SELECTOR_CONFIG,
		...config,
		apiURL: buildApiURL(null),
	};

	return render(
		DetachedCMSFilesItemSelectorModal,
		{
			...finalConfig,
			allowedExtensions,
			buildApiURL,
			fdsProps: {
				...FDS_PROPS,
				emptyState: allowDragAndDrop
					? {
							description: Liferay.Language.get(
								'drag-and-drop-to-upload'
							),
							image: '/states/cms_empty_state_files.svg',
							title: Liferay.Language.get('no-files-yet'),
						}
					: undefined,
				filters: getCMSItemSelectorFilters(
					groupId ?? Liferay.ThemeDisplay.getScopeGroupId()
				),
				groupedFilters: getCMSItemSelectorGroupedFilters(),
				hideManagementBarInEmptyState: true,
				...fdsProps,
				id: `CMSItemSelectorFDS_${getRandomId()}`,
			},
			filesUploaderComponent: allowDragAndDrop
				? CMSFileUploaderComponent
				: undefined,
			folderMemoryKey,
			groupId,
			itemTypeLabel: itemTypeLabel ?? Liferay.Language.get('files'),
			maxFileSize,
			onItemsChange: onSelect,
		},
		document.createElement('div')
	);
}
