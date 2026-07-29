/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '@liferay/frontend-data-set-web';
import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {openToast} from 'frontend-js-components-web';
import {v4 as uuidv4} from 'uuid';

import ObjectEntryLinkService, {
	ObjectEntryLinkContext,
	toLinkedAsset,
} from '../../../common/services/ObjectEntryLinkService';

type Asset = {
	embedded: {
		externalReferenceCode: string;
		file?: {
			mimeType: string;
		};
		id: number;
		systemProperties?: {scope?: {externalReferenceCode?: string}};
		title: string;
	};
	entryClassName: string;
};

export default function selectAssetsAction(
	{searchAPIURL, ...context}: ObjectEntryLinkContext & {searchAPIURL: string},
	loadData?: () => void
) {
	openItemSelectorModal({
		apiURL: `${window.location.origin}${Liferay.ThemeDisplay.getPathContext()}${searchAPIURL}`,
		fdsProps: {
			id: `itemSelectorModal-cms-${uuidv4()}`,
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
						description: 'embedded.description',
						image: 'embedded.file.thumbnailURL',
						title: 'embedded.title',
					},
					setItemComponentProps: ({
						item,
						props,
					}: {
						item: Asset;
						props: any;
					}) => {
						const {embedded} = item;

						if (!embedded.file?.mimeType.startsWith('image')) {
							return {
								...props,
								className: 'file-icon-color-5',
								displayType: 'unstyled',
								imgProps: null,
							};
						}

						return {
							...props,
							className: 'file-icon-color-5',
							displayType: 'unstyled',
						};
					},
					thumbnail: 'cards2',
				},
			] as IView[],
		},
		itemTypeLabel: Liferay.Language.get('cms-assets'),
		items: [],
		locator: {
			id: 'embedded.id',
			label: 'embedded.title',
			value: 'embedded.id',
		},
		multiSelect: true,
		onItemsChange: async (assets: Asset[]) => {
			const results = await Promise.all(
				assets.map((asset: Asset) =>
					ObjectEntryLinkService.linkAsset({
						context,
						linkedAsset: toLinkedAsset(asset),
					})
				)
			);

			const failedResult = results.find(({error}) => error);

			if (failedResult?.error) {
				openToast({message: failedResult.error, type: 'danger'});

				return;
			}

			openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});

			loadData?.();
		},
	});
}
