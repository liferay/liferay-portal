/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {Card, IView, replaceTokens} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import dateFormat from '../../../common/utils/dateFormat';

import '../../../../css/props_transformer/TransformViewsItemProps.scss';
import {
	ASSET_STATUS,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
} from '../../../common/utils/constants';
import {
	formatExpirationDate,
	formatExpirationDateLong,
	isExpiringSoon,
} from '../../../common/utils/expirationStatus';

type Card = React.ComponentProps<typeof Card> & {
	actions: {data: {id: string}; href?: string}[];
};

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

	return replaceTokens(selectedAction.href, item);
};

const getYouTubeVideoId = (videoURL: string) => {
	try {
		const url = new URL(videoURL);

		if (['www.youtube.com', 'youtube.com'].includes(url.hostname)) {
			return url.searchParams.get('v');
		}
		else if (['www.youtu.be', 'youtu.be'].includes(url.hostname)) {
			return url.pathname.substring(1);
		}
	}
	catch (_error) {
		return null;
	}

	return null;
};

const getThumbnailProps = (item: any) => {
	if (item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME) {
		return {symbol: 'folder'};
	}

	if (item.embedded?.file) {
		const {alternativeText, name, thumbnailURL} = item.embedded?.file;

		if (thumbnailURL) {
			return {
				imgProps: {
					alt: alternativeText || name,
					src: thumbnailURL,
				},
			};
		}
		else {
			return {symbol: 'documents-and-media'};
		}
	}

	if (item.embedded?.videoURL) {
		const videoId = getYouTubeVideoId(item.embedded?.videoURL);

		if (videoId) {
			return {
				imgProps: {
					alt: item.embedded?.title || item.title,
					src: `https://img.youtube.com/vi/${videoId}/0.jpg`,
				},
			};
		}

		return {symbol: 'video'};
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
		item.embedded?.systemProperties?.objectDefinitionBrief
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
			const mimeType = item.embedded?.file?.mimeType;

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

	const labels = props.labels ?? [];

	if (
		item.embedded?.status?.label === ASSET_STATUS.APPROVED &&
		isExpiringSoon(item.embedded?.expirationDate)
	) {
		const formattedDate = formatExpirationDate(
			item.embedded?.expirationDate
		);
		const formattedDateLong = formatExpirationDateLong(
			item.embedded?.expirationDate
		);

		if (formattedDate && formattedDateLong) {
			return [
				...labels,
				{
					'aria-label': sub(
						Liferay.Language.get('expiring-soon-expires-on-x'),
						formattedDateLong
					),
					'className': 'lfr-portal-tooltip',
					'displayType': 'warning',
					'tabIndex': 0,
					'title': formattedDate,
					'value': Liferay.Language.get('expiring-soon'),
				},
			];
		}
	}

	if (
		item.embedded?.status?.label === ASSET_STATUS.EXPIRED &&
		item.embedded?.expirationDate
	) {
		const formattedDate = formatExpirationDate(
			item.embedded?.expirationDate
		);
		const formattedDateLong = formatExpirationDateLong(
			item.embedded?.expirationDate
		);

		if (formattedDate && formattedDateLong) {
			const expiredText = Liferay.Language.get('expired');

			return labels.map((label: any) =>
				label.value === expiredText
					? {
							...label,
							'aria-label': sub(
								Liferay.Language.get('expired-on-x'),
								formattedDateLong
							),
							'className': 'lfr-portal-tooltip',
							'tabIndex': 0,
							'title': formattedDate,
						}
					: label
			);
		}
	}

	return labels;
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
		description: dateFormat(
			{
				day: 'numeric',
				hour: 'numeric',
				minute: 'numeric',
				month: 'short',
				second: 'numeric',
				timeZone: Liferay.ThemeDisplay.getTimeZone(),
				year: 'numeric',
			},
			item.dateModified
		),
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
		title: props.title || Liferay.Language.get('untitled-asset'),
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
		if (view.name === 'cards' || view.name === 'gallery') {
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
