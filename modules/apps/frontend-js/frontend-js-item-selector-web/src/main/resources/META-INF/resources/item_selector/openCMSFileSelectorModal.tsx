/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ClayVerticalNav} from '@clayui/nav';
import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {render} from '@liferay/frontend-js-react-web';
import {fetch, mimeTypeUtils} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import CMSFileUploaderComponent from '../item_selector_file_uploader/CMSFileUploaderComponent';
import DetachedCMSFilesItemSelectorModal from './DetachedCMSFilesItemSelectorModal';
import {IItemSelectorModalProps} from './ItemSelectorModal';
import {
	getCMSItemSelectorFilters,
	getCMSItemSelectorGroupedFilters,
} from './getCMSItemSelectorFilters';

const DL_FILE_ENTRY_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFileEntry';

const DL_FOLDER_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFolder';

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

interface DLFile {
	embedded: {
		contentUrl?: string;
		encodingFormat?: string;
		id: number;
		title?: string;
	};
	title: string;
}

type ActiveKey = 'cms' | `dm-${number}`;

type Site = {id: number; name: string};

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

function buildDLApiURL(
	extensions: string[],
	siteId: number,
	_folderId: number | null
): string {
	const url = new URL('/o/search/v1.0/search', location.origin);

	const predicates = [
		`(scopeGroupId eq ${siteId})`,
		'(status eq 0)',
	];

	if (extensions.length) {
		const extList = extensions.map((ext) => `'${ext}'`).join(',');

		predicates.push(`(extension in (${extList}))`);
	}

	url.search = new URLSearchParams({
		emptySearch: 'true',
		entryClassNames: DL_FILE_ENTRY_CLASS_NAME,
		filter: predicates.join(' and '),
		nestedFields: 'embedded',
	}).toString();

	return url.toString();
}

async function fetchSites(): Promise<Site[]> {
	try {
		const response = await fetch(
			'/o/headless-admin-user/v1.0/my-user-account'
		);

		if (!response.ok) {
			return [];
		}

		const data = await response.json();

		return (data.siteBriefs ?? []).map(
			(site: {id: number; name: string}) => ({
				id: site.id,
				name: site.name,
			})
		);
	}
	catch (error) {
		return [];
	}
}

function toExtensionList(allowedExtensions: string): string[] {
	return allowedExtensions
		.split(',')
		.map((ext) => ext.trim().replace(/^\./, ''))
		.filter(Boolean);
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

const DL_LOCATOR: CMSFileItemSelectorModalProps['locator'] = {
	id: 'embedded.id',
	label: 'title',
	value: 'embedded.id',
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

const DL_FDS_VIEWS = [
	{
		contentRenderer: 'cards',
		label: Liferay.Language.get('cards'),
		name: 'cards',
		schema: {
			description: 'description',
			symbol: '',
			title: 'embedded.name',
		},

		setItemComponentProps: ({
			item,
			props,
		}: {
			item: DLFile;
			props: object;
		}) => {
			const mimeType = item.embedded.encodingFormat ?? '';

			const titleProps = {
				title: item.title || item.embedded.title,
			};

			if (mimeType) {
				return {
					...props,
					...titleProps,
					imgProps: item.embedded.contentUrl
						? {src: item.embedded.contentUrl}
						: undefined,
					stickerProps: {
						className:
							mimeTypeUtils.getClassNameFromMimeType(mimeType),
						content: React.createElement(ClayIcon, {
							symbol: mimeTypeUtils.getIconFromMimeType(mimeType),
						}),
						displayType: 'unstyled',
					},
				};
			}

			return {
				...props,
				...titleProps,
				stickerProps: {
					className: 'file-icon-color-5',
					displayType: 'unstyled',
				},
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
					fieldName: 'embedded.name',
					label: Liferay.Language.get('title'),
					sortable: true,
				},
				{
					contentRenderer: 'cmsFilesFallbackCellRenderer',
					fieldName: 'embedded.encodingFormat',
					label: Liferay.Language.get('type'),
					sortable: false,
				},
				{
					contentRenderer: 'cmsFilesFallbackCellRenderer',
					fieldName: 'embedded.fileLength',
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
];

type SourceSidebarProps = {
	activeKey: ActiveKey;
	onActiveKeyChange: (key: ActiveKey) => void;
	sites: Site[];
};

function SourceSidebar({
	activeKey,
	onActiveKeyChange,
	sites,
}: SourceSidebarProps) {
	const sourceItems: Array<{icon: string; key: ActiveKey; label: string}> = [
		{icon: 'box-container', key: 'cms', label: 'Spaces'},
		...sites.map((site) => ({
			icon: 'sites',
			key: `dm-${site.id}` as ActiveKey,
			label: site.name,
		})),
	];

	const items = sourceItems.map((source) => ({
		active: activeKey === source.key,
		label: (
			<>
				<ClayIcon
					className="inline-item inline-item-before"
					symbol={source.icon}
				/>

				{source.label}
			</>
		),
		onClick: () => onActiveKeyChange(source.key),
		textValue: source.label,
	}));

	return (
		<>
			<h3 className="p-2 text-2 text-secondary text-uppercase">
				{Liferay.Language.get('select-from')}
			</h3>

			<ClayVerticalNav
				aria-label={Liferay.Language.get('source')}
				displayType="primary"
				items={items}
				stacked
			/>
		</>
	);
}

function getRandomId(): string {
	return Math.random().toString(36).substring(2, 9);
}

function normalizeExtensions(allowedExtensions: string) {
	const cleanExtensions = toExtensionList(allowedExtensions);

	if (cleanExtensions.length < 1) {
		return '';
	}

	const extensions = cleanExtensions.map((item) => `'${item}'`).join(',');

	return `(extension in (${extensions}) or objectDefinitionExternalReferenceCode eq 'L_OBJECT_ENTRY_FOLDER')`;
}

type CMSFileSelectorModalProps = {
	allowDragAndDrop: boolean;
	allowedExtensions?: string;
	cmsFdsFilters: string[];
	extensionList: string[];
	fdsPropsOverride?: Partial<CMSFileItemSelectorModalProps['fdsProps']>;
	finalConfig: CMSFileItemSelectorModalConfig;
	folderMemoryKey?: string;
	groupId?: number;
	itemTypeLabel?: string;
	maxFileSize?: number;
	onSelect: (items: Array<CMSFile>) => void;
};

function CMSFileSelectorModal({
	allowDragAndDrop,
	allowedExtensions,
	cmsFdsFilters,
	extensionList,
	fdsPropsOverride,
	finalConfig,
	folderMemoryKey,
	groupId,
	itemTypeLabel,
	maxFileSize,
	onSelect,
}: CMSFileSelectorModalProps) {
	const [activeKey, setActiveKey] = useState<ActiveKey>('cms');
	const [sites, setSites] = useState<Site[]>([]);

	const [fdsBaseId] = useState(() => getRandomId());

	const fdsId = `CMSItemSelectorFDS_${fdsBaseId}_${activeKey}`;

	const isSidebarEnabled = Liferay.FeatureFlags['LPD-95652'];

	useEffect(() => {
		if (!isSidebarEnabled) {
			return;
		}

		fetchSites().then(setSites);
	}, [isSidebarEnabled]);

	const activeSiteId = useMemo(
		() =>
			activeKey.startsWith('dm-')
				? parseInt(activeKey.slice(3), 10)
				: null,
		[activeKey]
	);

	const buildApiURL = useCallback(
		(folderId: number | null) => {
			if (activeSiteId !== null) {
				return buildDLApiURL(extensionList, activeSiteId, folderId);
			}

			return urlBuilder({
				filters: cmsFdsFilters,
				folderId: folderId ?? undefined,
			});
		},
		[activeSiteId, cmsFdsFilters, extensionList]
	);

	const rootLabel = useMemo(
		() =>
			activeSiteId !== null
				? sites.find((site) => site.id === activeSiteId)?.name
				: undefined,
		[activeSiteId, sites]
	);

	const locator = useMemo(
		() => (activeSiteId !== null ? DL_LOCATOR : finalConfig.locator),
		[activeSiteId, finalConfig.locator]
	);

	const fdsViews = useMemo(
		() => (activeSiteId !== null ? DL_FDS_VIEWS : FDS_PROPS.views),
		[activeSiteId]
	);

	const {fdsFilters, fdsGroupedFilters} = useMemo(
		() =>
			activeSiteId !== null
				? {fdsFilters: undefined, fdsGroupedFilters: undefined}
				: {
						fdsFilters: getCMSItemSelectorFilters(
							groupId ?? Liferay.ThemeDisplay.getScopeGroupId()
						),
						fdsGroupedFilters: getCMSItemSelectorGroupedFilters(),
					},
		[activeSiteId, groupId]
	);

	const sidebar = isSidebarEnabled ? (
		<SourceSidebar
			activeKey={activeKey}
			onActiveKeyChange={setActiveKey}
			sites={sites}
		/>
	) : undefined;

	return (
		<DetachedCMSFilesItemSelectorModal
			{...finalConfig}
			allowedExtensions={allowedExtensions}
			buildApiURL={buildApiURL}
			fdsProps={{
				...FDS_PROPS,
				emptyState:
					allowDragAndDrop && activeSiteId === null
						? {
								description: Liferay.Language.get(
									'drag-and-drop-to-upload'
								),
								image: '/states/cms_empty_state_files.svg',
								title: Liferay.Language.get('no-files-yet'),
							}
						: undefined,
				filters: fdsFilters,
				groupedFilters: fdsGroupedFilters,
				hideManagementBarInEmptyState: true,
				...fdsPropsOverride,
				id: fdsId,
				views: fdsViews,
			}}
			filesUploaderComponent={
				allowDragAndDrop && activeSiteId === null
					? CMSFileUploaderComponent
					: undefined
			}
			folderMemoryKey={folderMemoryKey}
			groupId={groupId}
			itemTypeLabel={itemTypeLabel ?? Liferay.Language.get('files')}
			locator={locator}
			maxFileSize={maxFileSize}
			onItemsChange={onSelect}
			resetKey={activeKey}
			rootLabel={rootLabel}
			sidebar={sidebar}
		/>
	);
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
	let cmsFdsFilters: string[] = [];
	let extensionList: string[] = [];

	if (filters?.length) {
		cmsFdsFilters = filters;
	}
	else if (allowedExtensions) {
		cmsFdsFilters = [normalizeExtensions(allowedExtensions)];
		extensionList = toExtensionList(allowedExtensions);
	}

	const finalConfig: CMSFileItemSelectorModalConfig = {
		...CMS_FILE_ITEM_SELECTOR_CONFIG,
		...config,
		apiURL: urlBuilder({filters: cmsFdsFilters}),
	};

	return render(
		CMSFileSelectorModal,
		{
			allowDragAndDrop,
			allowedExtensions,
			cmsFdsFilters,
			extensionList,
			fdsPropsOverride: fdsProps,
			finalConfig,
			folderMemoryKey,
			groupId,
			itemTypeLabel,
			maxFileSize,
			onSelect,
		},
		document.createElement('div')
	);
}
