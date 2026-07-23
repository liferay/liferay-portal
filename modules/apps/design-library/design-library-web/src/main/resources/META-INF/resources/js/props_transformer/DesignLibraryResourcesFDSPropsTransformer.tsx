/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFrontendDataSetProps} from '@liferay/frontend-data-set-web';
import {FragmentSetModalContent} from '@liferay/layout-js-components-web';
import {openModal, openToast} from 'frontend-js-components-web';
import {addParams, fetch, navigate} from 'frontend-js-web';
import React from 'react';
import {AddStyleBookModalContent} from 'style-book-web';

import {
	FRAGMENT_COLLECTION_ENTRY_CLASS_NAME,
	TableCellContentType,
} from '../constants';
import {
	AuthorRenderer,
	FromNowDateTimeRenderer,
	LinkRenderer,
	ResourceTypeRenderer,
	createSetItemComponentProps,
} from './cell_renderers';

const FRAGMENT_TYPE_COMPONENT = 1;
const FRAGMENT_TYPE_INPUT = 3;

type FragmentCollection = {fragmentCollectionId: number; name: string};

type FrontendTokenDefinitionProvider = {
	name: string;
	themeId: string;
};

interface DesignLibraryResourcesAdditionalProps {
	addFragmentCollectionURL?: string;
	addFragmentEntryURL?: string;
	addStyleBookEntryURL?: string;
	canAddStyleBook: boolean;
	canManageFragments: boolean;
	fragmentCollections?: Array<FragmentCollection>;
	fragmentNamespace?: string;
	frontendTokenDefinitionProviders?: Array<FrontendTokenDefinitionProvider>;
	styleBookNamespace?: string;
}

export default function DesignLibraryResourcesFDSPropsTransformer(
	props: IFrontendDataSetProps & {
		additionalProps?: DesignLibraryResourcesAdditionalProps;
	}
): IFrontendDataSetProps {
	const {
		addFragmentCollectionURL,
		addFragmentEntryURL,
		addStyleBookEntryURL,
		canAddStyleBook = false,
		canManageFragments = false,
		fragmentCollections = [],
		fragmentNamespace = '',
		frontendTokenDefinitionProviders = [],
		styleBookNamespace = '',
	} = props.additionalProps ?? {};

	const primaryItems: Array<{label: string; onClick: () => void}> = [];

	if (canAddStyleBook && addStyleBookEntryURL) {
		primaryItems.push({
			label: Liferay.Language.get('new-style-book'),
			onClick: () =>
				openModal({
					contentComponent: ({closeModal}) =>
						AddStyleBookModalContent({
							addStyleBookEntryURL,
							closeModal,
							frontendTokenDefinitionProviders,
							namespace: styleBookNamespace,
						}),
				}),
		});
	}

	if (canManageFragments && addFragmentEntryURL && addFragmentCollectionURL) {
		const pushAddFragmentItem = (label: string, fragmentType: number) => {
			primaryItems.push({
				label,
				onClick: () =>
					openModal({
						contentComponent: ({closeModal}) => (
							<FragmentSetModalContent
								addFragmentCollectionURL={
									addFragmentCollectionURL
								}
								allowCustomName
								closeModal={closeModal}
								fragmentCollections={fragmentCollections}
								onSubmitFragmentCollection={(
									fragmentCollectionId: number,
									fragmentName?: string
								) => {
									const formData = new FormData();

									formData.append(
										`${fragmentNamespace}fragmentCollectionId`,
										String(fragmentCollectionId)
									);

									formData.append(
										`${fragmentNamespace}name`,
										fragmentName ?? ''
									);

									formData.append(
										`${fragmentNamespace}type`,
										String(fragmentType)
									);

									fetch(addFragmentEntryURL, {
										body: formData,
										method: 'POST',
									})
										.then((response) => response.json())
										.then(({redirectURL}) => {
											if (!redirectURL) {
												navigate(location.href);

												return;
											}

											navigate(
												addParams(
													{
														[`${fragmentNamespace}redirect`]:
															location.href,
													},
													redirectURL
												)
											);
										})
										.catch(() =>
											openToast({
												message: Liferay.Language.get(
													'an-unexpected-error-occurred'
												),
												type: 'danger',
											})
										);
								}}
								portletNamespace={fragmentNamespace}
							/>
						),
					}),
			});
		};

		pushAddFragmentItem(
			Liferay.Language.get('new-basic-fragment'),
			FRAGMENT_TYPE_COMPONENT
		);

		pushAddFragmentItem(
			Liferay.Language.get('new-form-fragment'),
			FRAGMENT_TYPE_INPUT
		);
	}

	if (canManageFragments && addFragmentCollectionURL) {
		primaryItems.push({
			label: Liferay.Language.get('new-fragment-set'),
			onClick: () =>
				openModal({
					contentComponent: ({closeModal}) => (
						<FragmentSetModalContent
							addFragmentCollectionURL={addFragmentCollectionURL}
							closeModal={closeModal}
							fragmentCollections={[]}
							onSubmitFragmentCollection={() =>
								navigate(location.href)
							}
							portletNamespace={fragmentNamespace}
						/>
					),
				}),
		});
	}

	const creationMenu = primaryItems.length ? {primaryItems} : undefined;

	return {
		...props,
		creationMenu,
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
		showSelectAll: true,
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
			{
				contentRenderer: 'cards',
				label: Liferay.Language.get('cards'),
				name: 'cards',
				schema: {
					description: 'dateModified',
					symbol: '',
					title: 'embedded.name',
				},
				setItemComponentProps: createSetItemComponentProps('book'),
				thumbnail: 'cards2',
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
