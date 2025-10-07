/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openConfirmModal,
	openModal,
	openSelectionModal,
} from 'frontend-js-components-web';
import {addParams} from 'frontend-js-web';

import openDeleteArticleModal from './modals/openDeleteArticleModal';

const ACTIONS = {
	compareVersions({itemData, portletNamespace}) {
		openSelectionModal({
			onSelect: (selectedItem) => {
				let url = itemData.redirectURL;

				url = addParams(
					`${portletNamespace}sourceVersion=${selectedItem.sourceversion}`,
					url
				);
				url = addParams(
					`${portletNamespace}targetVersion=${selectedItem.targetversion}`,
					url
				);

				location.href = url;
			},
			selectEventName: `${portletNamespace}selectVersionFm`,
			title: Liferay.Language.get('compare-versions'),
			url: itemData.compareVersionsURL,
		});
	},

	copyArticle({itemData}) {
		this.send(itemData.copyArticleURL);
	},

	delete({itemData, trashEnabled}) {
		if (trashEnabled) {
			this.send(itemData.deleteURL);

			return;
		}

		openDeleteArticleModal({
			onDelete: () => {
				this.send(itemData.deleteURL);
			},
		});
	},

	deleteArticleTranslations({itemData, portletNamespace}) {
		openSelectionModal({
			buttonAddLabel: Liferay.Language.get('delete'),
			multiple: true,
			onSelect: (selectedItems) => {
				if (selectedItems?.length) {
					openConfirmModal({
						message: Liferay.Language.get(
							'are-you-sure-you-want-to-delete-the-selected-entries'
						),
						onConfirm: (isConfirmed) => {
							if (isConfirmed) {
								const form = document.hrefFm;

								if (!form) {
									return;
								}

								const input = document.createElement('input');

								input.name = `${portletNamespace}rowIds`;

								const languageIds = selectedItems
									.map(
										(item) =>
											JSON.parse(item.value).languageId
									)
									.join(',');

								input.value = languageIds;

								form.appendChild(input);

								submitForm(
									form,
									itemData.deleteArticleTranslationsURL
								);
							}
						},
					});
				}
			},
			title: Liferay.Language.get('delete-translations'),
			url: itemData.selectArticleTranslationsURL,
		});
	},

	discardArticleDraft({itemData}) {
		this.send(itemData.discardArticleDraftURL);
	},

	expireArticles({itemData}) {
		this.send(itemData.expireURL);
	},

	permissions({itemData}) {
		openModal({
			title: Liferay.Language.get('permissions'),
			url: itemData.permissionsURL,
		});
	},

	preview({itemData}) {
		openModal({
			iframeBodyCssClass: '',
			title: itemData.title,
			url: itemData.previewURL,
		});
	},

	publishArticleToLive({itemData}) {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-publish-the-selected-web-content'
			),
			onConfirm: (isConfirmed) =>
				isConfirmed && this.send(itemData.publishArticleURL),
		});
	},

	publishFolderToLive({itemData}) {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-publish-the-selected-folder'
			),
			onConfirm: (isConfirmed) =>
				isConfirmed && this.send(itemData.publishFolderURL),
		});
	},

	revertArticle({itemData}) {
		this.send(itemData.revertURL);
	},

	send(url) {
		submitForm(document.hrefFm, url);
	},

	subscribeArticle({itemData}) {
		this.send(itemData.subscribeArticleURL);
	},

	unsubscribeArticle({itemData}) {
		this.send(itemData.unsubscribeArticleURL);
	},
};

export default function propsTransformer({
	actions,
	additionalProps: {trashEnabled},
	items,
	portletNamespace,
	...props
}) {
	const bindAction = (item) => {
		const action = ACTIONS[item.data?.action];

		const transformedItem = {...item};

		if (typeof action === 'function') {
			transformedItem.onClick = (event) => {
				event.preventDefault();

				action.call(ACTIONS, {
					itemData: item.data,
					portletNamespace,
					trashEnabled,
				});
			};
		}

		if (Array.isArray(item.items)) {
			transformedItem.items = item.items.map(bindAction);
		}

		return transformedItem;
	};

	return {
		...props,
		actions: actions?.map(bindAction),
		items: items?.map(bindAction),
	};
}
