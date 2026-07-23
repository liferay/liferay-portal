/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React from 'react';

import {TableCellContentType} from '../constants';
import CreateDesignLibraryModal from '../modal/CreateDesignLibraryModal';
import confirmAndDeleteEntryAction from './actions/confirmAndDeleteEntryAction';
import {
	AuthorRenderer,
	FromNowDateTimeRenderer,
	LinkRenderer,
	createSetItemComponentProps,
} from './cell_renderers';

export default function DesignLibraryAdminFDSPropsTransformer({
	additionalProps: {canAddDesignLibrary, entryIdKey, redirectURL},
	id,
	...props
}: {
	additionalProps: {
		canAddDesignLibrary: boolean;
		entryIdKey: string;
		redirectURL: string;
	};

	id: string;
	props: Record<string, unknown>;
}): IFrontendDataSetProps {
	const creationMenu = canAddDesignLibrary
		? {
				primaryItems: [
					{
						label: Liferay.Language.get('new-design-library'),
						onClick: () => {
							openModal({
								contentComponent: ({closeModal}) =>
									CreateDesignLibraryModal({
										dataSetId: id,
										entryIdKey,
										onClose: closeModal,
										redirectURL,
									}),
								size: 'md',
							});
						},
					},
				],
			}
		: undefined;

	return {
		...props,
		creationMenu,
		customRenderers: {
			tableCell: [
				{
					component: (props) => (
						<LinkRenderer
							{...props}
							stickerClassName="design-library-fds-sticker-designlibrary"
							symbol="books"
						/>
					),
					name: TableCellContentType.DESIGN_LIBRARY_LINK,
					type: 'internal',
				},
				{
					component: AuthorRenderer,
					name: TableCellContentType.AUTHOR,
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
		id,
		onActionDropdownItemClick: ({
			action,
			event,
			itemData,
		}: {
			action: {
				data: {
					id: string;
				};
			};
			event: Event;
			itemData: {
				actions: {
					delete: {href: string; method: string};
				};
				name: string;
			};
		}) => {
			if (action.data.id === 'delete') {
				event?.preventDefault();

				confirmAndDeleteEntryAction({
					bodyHTML: `
						<p>${Liferay.Language.get('delete-design-library-confirmation-body-main')}</p>
						<p>${Liferay.Language.get('delete-design-library-confirmation-body-warning')}</p>
					`,
					deleteAction: itemData.actions.delete,
					loadData: () => {
						navigate(window.location.href);
					},
					successMessage: sub(
						Liferay.Language.get('x-was-successfully-deleted'),
						`<strong>${Liferay.Util.escapeHTML(itemData.name)}</strong>`
					),
					title: sub(
						Liferay.Language.get(
							'delete-design-library-confirmation-title'
						),
						itemData.name
					),
				});
			}
		},
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
							fieldName: 'name',
							label: Liferay.Language.get('title'),
							localizeLabel: true,
							sortable: true,
						},
						{
							contentRenderer: TableCellContentType.AUTHOR,
							fieldName: 'creator.name',
							label: Liferay.Language.get('author'),
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
			{
				contentRenderer: 'cards',
				label: Liferay.Language.get('cards'),
				name: 'cards',
				schema: {
					description: 'dateModified',
					symbol: '',
					title: 'name',
				},
				setItemComponentProps: createSetItemComponentProps('books'),
				thumbnail: 'cards2',
			},
		],
	};
}
