/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';
import React from 'react';

import {
	FRAGMENT_COLLECTION_ENTRY_CLASS_NAME,
	TableCellContentType,
} from '../constants';
import {
	AuthorRenderer,
	FromNowDateTimeRenderer,
	LinkRenderer,
	ResourceTypeRenderer,
} from './cell_renderers';

export default function DesignLibraryResourcesFDSPropsTransformer(
	props: IFrontendDataSetProps
): IFrontendDataSetProps {
	return {
		...props,
		customRenderers: {
			tableCell: [
				{
					component: (rendererProps: any) => {
						const isFragmentCollection =
							rendererProps?.itemData?.entryClassName ===
							FRAGMENT_COLLECTION_ENTRY_CLASS_NAME;

						return (
							<LinkRenderer
								{...rendererProps}
								options={{
									actionId: isFragmentCollection
										? 'view'
										: 'edit',
								}}
								stickerClassName={
									isFragmentCollection
										? 'design-library-fds-sticker-fragment-set'
										: 'design-library-fds-sticker-stylebook'
								}
								symbol={getSymbol(
									rendererProps?.itemData?.entryClassName
								)}
							/>
						);
					},
					name: TableCellContentType.DESIGN_LIBRARY_LINK,
					type: 'internal',
				},
				{
					component: AuthorRenderer,
					name: TableCellContentType.AUTHOR,
					type: 'internal',
				},
				{
					component: ResourceTypeRenderer,
					name: TableCellContentType.RESOURCE_TYPE,
					type: 'internal',
				},
				{
					component: FromNowDateTimeRenderer,
					name: TableCellContentType.FROM_NOW_DATE_TIME,
					type: 'internal',
				},
			],
		},
		hideManagementBarInEmptyState: true,
		views: [
			{
				contentRenderer: 'table',
				default: true,
				label: Liferay.Language.get('table'),
				name: 'table',
				schema: {
					fields: [
						{
							actionId: 'edit',
							contentRenderer:
								TableCellContentType.DESIGN_LIBRARY_LINK,
							fieldName: 'embedded.name',
							label: Liferay.Language.get('title'),
							localizeLabel: true,
						},
						{
							contentRenderer: TableCellContentType.AUTHOR,
							fieldName: 'embedded.creator.name',
							label: Liferay.Language.get('author'),
							localizeLabel: true,
							truncate: true,
						},
						{
							contentRenderer: TableCellContentType.RESOURCE_TYPE,
							fieldName: 'type',
							label: Liferay.Language.get('type'),
							localizeLabel: true,
							truncate: true,
						},
						{
							contentRenderer:
								TableCellContentType.FROM_NOW_DATE_TIME,
							fieldName: 'dateModified',
							label: Liferay.Language.get('modified'),
							localizeLabel: true,
							sortable: true,
						},
					],
				},
				thumbnail: 'table',
			},
		],
	};
}

function getSymbol(entryClassName?: string): string {
	if (entryClassName === FRAGMENT_COLLECTION_ENTRY_CLASS_NAME) {
		return 'squares';
	}

	return 'book';
}
