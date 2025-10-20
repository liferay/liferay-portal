/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import {LayoutData, LayoutDataItem} from '../../types/layout_data/LayoutData';
import {
	FragmentEntryLink,
	FragmentEntryLinkMap,
} from '../actions/addFragmentEntryLinks';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/freemarkerFragmentEntryProcessor';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';
import selectLayoutDataItemLabel from '../selectors/selectLayoutDataItemLabel';
import {setIn} from './setIn';

type Props = {
	fragmentEntryLinks: FragmentEntryLinkMap;
	layoutData: LayoutData;
	movedIds: LayoutDataItem['itemId'][];
	targetId: LayoutDataItem['itemId'];
};

export function getUnmappedFragmentEntryLinks({
	fragmentEntryLinks,
	layoutData,
	movedIds,
	targetId,
}: Props): FragmentEntryLink[] {
	const targetItem = layoutData.items[targetId];
	const targetMappingParent = getMappingParent(targetItem, layoutData);

	if (!targetMappingParent) {
		return [];
	}

	const fragments = [];

	for (const movedId of movedIds) {
		const item = layoutData.items[movedId];

		if (!('fragmentEntryLinkId' in item.config)) {
			continue;
		}

		const fragmentEntryLinkId = item.config.fragmentEntryLinkId;

		const mappingParent = getMappingParent(item, layoutData);

		if (!mappingParent) {
			continue;
		}

		if (mappingParent.itemId !== targetMappingParent.itemId) {
			const label = selectLayoutDataItemLabel(
				{fragmentEntryLinks, layoutData},
				item
			);

			openToast({
				message: sub(
					Liferay.Language.get(
						'x-was-moved-and-its-mapping-was-reset.-you-can-set-it-up-again-later'
					),
					label
				),

				type: 'warning',
			});

			fragments.push(
				unmapFragmentEntryLink(fragmentEntryLinks[fragmentEntryLinkId])
			);
		}
	}

	return fragments;
}

export function getMappingParent(
	item: LayoutDataItem,
	layoutData: LayoutData
): LayoutDataItem | null {
	if (
		item.type === LAYOUT_DATA_ITEM_TYPES.formRelationship ||
		item.type === LAYOUT_DATA_ITEM_TYPES.form
	) {
		return item;
	}

	const parent = layoutData?.items?.[item.parentId];

	if (!parent) {
		return null;
	}

	return getMappingParent(parent, layoutData);
}

function unmapFragmentEntryLink(fragmentEntryLink: FragmentEntryLink) {
	return setIn(
		fragmentEntryLink,
		['editableValues', FREEMARKER_FRAGMENT_ENTRY_PROCESSOR],
		{
			...fragmentEntryLink.editableValues[
				FREEMARKER_FRAGMENT_ENTRY_PROCESSOR
			],
			inputFieldId: '',
		}
	);
}
