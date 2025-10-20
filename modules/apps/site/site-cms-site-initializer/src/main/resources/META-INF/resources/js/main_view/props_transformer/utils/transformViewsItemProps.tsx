/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {Card, IView} from '@liferay/frontend-data-set-web';
import React from 'react';

import dateFormat from '../../../common/utils/dateFormat';
import formatActionURL from '../../../common/utils/formatActionURL';

import '../../../../css/props_transformer/TransformViewsItemProps.scss';

type Card = React.ComponentProps<typeof Card> & {
	actions: {data: {id: string}; href?: string}[];
};

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

const MULTIMEDIA_TYPES = ['audio/', 'image/', 'video/'];

const getHrefLink = (item: any, props: Card) => {
	const actionId = 'actionLink';
	const {actions} = props;

	if (!actions.length) {
		return null;
	}

	const isFolder = item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;
	const resolvedActionId = isFolder ? `${actionId}Folder` : actionId;

	const selectedAction = actions.find(
		({data}: {data: any}) => data?.id === resolvedActionId
	);

	if (!selectedAction?.href) {
		return null;
	}

	return formatActionURL(item, selectedAction.href);
};

const getThumbnailProps = (item: any) => {
	if (item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return {symbol: 'folder'};
	}

	if (item.embedded.file) {
		const {thumbnailURL} = item.embedded.file;

		if (thumbnailURL) {
			return {imgProps: thumbnailURL};
		}
		else {
			return {symbol: 'documents-and-media'};
		}
	}

	return {symbol: 'web-content'};
};

function isMultimediaMimeType(mimeType: string): boolean {
	return MULTIMEDIA_TYPES.some((prefix) => mimeType.startsWith(prefix));
}

export function getFileMimeTypeObjectDefinitionStickerValue(
	fileMimeTypeValues: Record<string, string> | undefined,
	objectDefinitionValues: Record<string, string>,
	item: any
) {
	if (item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return 'folder';
	}

	const objectDefinitionExternalReferenceCode =
		item.embedded.systemProperties?.objectDefinitionBrief
			?.externalReferenceCode;

	if (objectDefinitionExternalReferenceCode) {
		const objectDefinitionCssClass =
			objectDefinitionValues[objectDefinitionExternalReferenceCode];

		if (objectDefinitionCssClass) {
			return objectDefinitionCssClass;
		}

		if (
			fileMimeTypeValues &&
			objectDefinitionExternalReferenceCode === 'L_CMS_BASIC_DOCUMENT'
		) {
			const mimeType = item.embedded.file?.mimeType;

			if (!mimeType) {
				return fileMimeTypeValues['default'];
			}

			const fileMimeTypeCssClass = fileMimeTypeValues[mimeType];

			if (fileMimeTypeCssClass) {
				return fileMimeTypeCssClass;
			}

			if (isMultimediaMimeType(mimeType)) {
				const mimeTypeParts = mimeType.split('/');

				const fileMimeTypeCssClass =
					fileMimeTypeValues[mimeTypeParts[0]];

				if (fileMimeTypeCssClass) {
					return fileMimeTypeCssClass;
				}
			}

			return fileMimeTypeValues['default'];
		}

		return objectDefinitionValues['default'];
	}

	return '';
}

const getLabels = (item: any, props: Card) => {
	if (item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return [
			{
				displayType: 'empty',
				value: '',
			},
		];
	}

	return props.labels;
};

type ViewsItemsProps = {
	fileMimeTypeCssClasses?: Record<string, string>;
	fileMimeTypeIcons?: Record<string, string>;
	objectDefinitionCssClasses: Record<string, string>;
	objectDefinitionIcons: Record<string, string>;
	views: IView[];
};

export function transformItemCardView(
	item: any,
	fileMimeTypeCssClasses: Record<string, string> | undefined,
	fileMimeTypeIcons: Record<string, string> | undefined,
	objectDefinitionCssClasses: Record<string, string>,
	objectDefinitionIcons: Record<string, string>,
	props: Card
) {
	return {
		...props,
		description: dateFormat(item.dateModified),
		href: getHrefLink(item, props),
		labels: getLabels(item, props),
		stickerProps: {
			className: getFileMimeTypeObjectDefinitionStickerValue(
				fileMimeTypeCssClasses,
				objectDefinitionCssClasses,
				item
			),
			content: (
				<ClayIcon
					symbol={getFileMimeTypeObjectDefinitionStickerValue(
						fileMimeTypeIcons,
						objectDefinitionIcons,
						item
					)}
				/>
			),
		},
		...getThumbnailProps(item),
	};
}

export default function transformViewsItemProps({
	fileMimeTypeCssClasses,
	fileMimeTypeIcons,
	objectDefinitionCssClasses,
	objectDefinitionIcons,
	views,
}: ViewsItemsProps) {
	return views.map((view) => {
		if (view.name === 'cards') {
			view.setItemComponentProps = ({
				item,
				props,
			}: {
				item: any;
				props: Card;
			}) =>
				transformItemCardView(
					item,
					fileMimeTypeCssClasses,
					fileMimeTypeIcons,
					objectDefinitionCssClasses,
					objectDefinitionIcons,
					props
				);
		}

		return view;
	});
}
