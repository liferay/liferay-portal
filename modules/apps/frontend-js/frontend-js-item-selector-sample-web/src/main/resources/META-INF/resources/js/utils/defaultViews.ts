/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const assetLibraryViews = [
	{
		contentRenderer: 'cards',
		label: Liferay.Language.get('cards'),
		name: 'cards',
		schema: {
			description: 'description',
			title: 'name',
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
					fieldName: 'name',
					label: Liferay.Language.get('name'),
					sortable: false,
				},
				{
					fieldName: 'description',
					label: Liferay.Language.get('description'),
					sortable: false,
				},
			],
		},
		thumbnail: 'table',
	},
];

export const documentViews = [
	{
		contentRenderer: 'cards',
		label: Liferay.Language.get('cards'),
		name: 'cards',
		schema: {
			description: 'description',
			image: 'contentUrl',
			title: 'title',
		},
		setItemComponentProps: ({item, props}: {item: any; props: any}) => {
			const stickerProps = {
				stickerProps: {
					className: 'file-icon-color-5',
					displayType: 'unstyled',
				},
			};

			if (!item.encodingFormat.startsWith('image')) {
				return {
					...props,
					imgProps: null,
					...stickerProps,
				};
			}

			return {
				...props,
				...stickerProps,
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
					fieldName: 'title',
					label: Liferay.Language.get('title'),
					sortable: false,
				},
				{
					fieldName: 'description',
					label: Liferay.Language.get('description'),
					sortable: false,
				},
				{
					fieldName: 'fileName',
					label: Liferay.Language.get('fileName'),
					sortable: false,
				},
				{
					fieldName: 'fileExtension',
					label: Liferay.Language.get('type'),
					sortable: false,
				},
			],
		},
		thumbnail: 'table',
	},
];

export const userViews = [
	{
		contentRenderer: 'cards',
		label: Liferay.Language.get('cards'),
		name: 'cards',
		schema: {
			description: 'emailAddress',
			image: 'image',
			symbol: 'symbol',
			title: 'givenName',
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
					fieldName: 'givenName',
					label: Liferay.Language.get('givenName'),
					sortable: false,
				},
				{
					fieldName: 'emailAddress',
					label: Liferay.Language.get('email'),
					sortable: false,
				},
				{
					fieldName: 'jobTitle',
					label: Liferay.Language.get('job-title'),
					sortable: false,
				},
			],
		},
		thumbnail: 'table',
	},
];

export const cmsFileViews = [
	{
		contentRenderer: 'cards',
		label: Liferay.Language.get('cards'),
		name: 'cards',
		schema: {
			description: 'description',
			title: 'title',
		},

		setItemComponentProps: ({
			item,
			props,
		}: {
			item: {
				embedded:
					| {coverImage: {link: {href: string}}}
					| {file: {thumbnailURL: string}};
			};
			props: object;
		}) => {
			const stickerProps = {
				stickerProps: {
					className: 'file-icon-color-5',
					displayType: 'unstyled',
				},
			};

			if ('file' in item.embedded) {
				return {
					...props,
					imgProps: {src: item.embedded.file.thumbnailURL},
					...stickerProps,
				};
			}

			return {
				...props,
				...stickerProps,
			};
		},

		thumbnail: 'cards2',
	},
];
