/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';

import deleteItemAction from '../actions/deleteItem';
import {ITEM_ACTIVATION_ORIGINS} from '../config/constants/itemActivationOrigins';
import {LAYOUT_DATA_ITEM_TYPES} from '../config/constants/layoutDataItemTypes';
import {LAYOUT_STRUCTURE_ITEM_CLASS_NAME_PREFIX} from '../config/constants/layoutStructureItemClassNamePrefix';
import selectFormConfiguration from '../selectors/selectFormConfiguration';
import FormService from '../services/FormService';
import LayoutService from '../services/LayoutService';
import {CACHE_KEYS, getCacheItem, getCacheKey} from '../utils/cache';
import {getDescendantIds} from '../utils/getDescendantIds';
import {
	FORM_ERROR_TYPES,
	getFormErrorDescription,
} from '../utils/getFormErrorDescription';
import {getFormParent} from '../utils/getFormParent';
import getFragmentEntryLinkIdsFromItemId from '../utils/getFragmentEntryLinkIdsFromItemId';
import getPortletId from '../utils/getPortletId';
import {isRequiredFormInput} from '../utils/isRequiredFormInput';
import {clearPageContents} from '../utils/usePageContents';
import filterSelectedItems from './filterSelectedItems';

export function getPreviousItemId(deletedItems, items, nextItems) {
	const parentId = items[deletedItems[0]].parentId;

	const parent = items[parentId];

	if (nextItems[parentId].children.length) {
		const firstDeletedChild = parent.children.find((childId) =>
			deletedItems.includes(childId)
		);
		const index = parent.children.indexOf(firstDeletedChild);

		return nextItems[parentId].children[index ? index - 1 : index];
	}
	else if (
		parent.type === LAYOUT_DATA_ITEM_TYPES.collectionItem ||
		parent.type === LAYOUT_DATA_ITEM_TYPES.column
	) {
		return nextItems[parent.parentId].itemId;
	}
	else if (parent.type === LAYOUT_DATA_ITEM_TYPES.root) {
		return null;
	}

	return parentId;
}

export default function deleteItem({itemIds, selectItems = () => {}}) {
	return (dispatch, getState) => {
		const {
			availableSegmentsExperiences = {},
			elementVariations = [],
			fragmentEntryLinks,
			layoutData,
			segmentsExperienceId,
		} = getState();

		const handleDeleteItems = async () => {
			return markItemForDeletion({
				fragmentEntryLinks,
				itemIds,
				layoutData,
				onNetworkStatus: dispatch,
				segmentsExperienceId,
			}).then(async ({portletIds = [], layoutData: nextLayoutData}) => {
				const nextItemId = getPreviousItemId(
					itemIds,
					layoutData.items,
					nextLayoutData.items
				);

				if (!nextItemId) {
					document
						.querySelector('button[data-panel-id="browser"]')
						.focus();

					selectItems(null);
				}
				else {
					selectItems([nextItemId], {
						origin: ITEM_ACTIVATION_ORIGINS.itemActions,
					});
				}

				const fragmentEntryLinkIds = itemIds.flatMap((itemId) =>
					getFragmentEntryLinkIdsFromItemId({
						itemId,
						layoutData: nextLayoutData,
					})
				);

				dispatch(
					deleteItemAction({
						fragmentEntryLinkIds,
						itemIds,
						layoutData: nextLayoutData,
						portletIds,
					})
				);

				clearPageContents();

				// Show warning if deleting some required form input

				for (const itemId of itemIds) {
					if (
						await isRequiredFormField(
							layoutData,
							itemId,
							fragmentEntryLinks
						)
					) {
						const {message} = getFormErrorDescription({
							type: FORM_ERROR_TYPES.deletedFragment,
						});

						openToast({
							message,
							type: 'warning',
						});

						break;
					}
				}
			});
		};

		const handleRules = async () => {
			if (isUsedInRule(itemIds, layoutData)) {
				openConfirmModal({
					buttonLabel: Liferay.Language.get('delete'),
					onConfirm: handleDeleteItems,
					status: 'warning',
					text: Liferay.Language.get(
						'one-or-more-of-the-selected-fragments-are-referenced-in-one-or-more-rules'
					),
					title: Liferay.Language.get('delete-referenced-fragments'),
				});
			}
			else {
				return handleDeleteItems();
			}
		};

		if (
			isUsedInElementVariation({
				availableSegmentsExperiences,
				elementVariations,
				itemIds,
				layoutData,
				segmentsExperienceId,
			})
		) {
			openConfirmModal({
				buttonLabel: Liferay.Language.get('delete'),
				onConfirm: handleRules,
				status: 'warning',
				text: Liferay.Language.get(
					'one-or-more-of-the-selected-fragments-are-referenced-in-one-or-more-element-variations.-are-you-sure-you-want-to-proceed-with-the-deletion'
				),
				title: Liferay.Language.get('delete-referenced-fragments'),
			});
		}
		else {
			return handleRules();
		}
	};
}

async function markItemForDeletion({
	fragmentEntryLinks,
	itemIds,
	layoutData,
	onNetworkStatus: dispatch,
	segmentsExperienceId,
}) {

	// We just need to remove the parents of the selected items

	const selectedItemIds = filterSelectedItems(itemIds, layoutData.items);

	const portletIds = selectedItemIds.flatMap((itemId) =>
		findPortletIds(itemId, layoutData, fragmentEntryLinks)
	);

	return LayoutService.markItemForDeletion({
		itemIds: selectedItemIds,
		onNetworkStatus: dispatch,
		portletIds,
		segmentsExperienceId,
	}).then((response) => {
		return {...response, portletIds};
	});
}

function findPortletIds(itemId, layoutData, fragmentEntryLinks) {
	const item = layoutData.items[itemId];

	const {config = {}, children = []} = item;

	if (
		item.type === LAYOUT_DATA_ITEM_TYPES.fragment &&
		config.fragmentEntryLinkId
	) {
		const {editableValues = {}} =
			fragmentEntryLinks[config.fragmentEntryLinkId];

		if (editableValues.portletId) {
			return [getPortletId(editableValues)];
		}
	}

	const deletedWidgets = [];

	children.forEach((itemId) => {
		deletedWidgets.push(
			...findPortletIds(itemId, layoutData, fragmentEntryLinks)
		);
	});

	return deletedWidgets;
}

async function isRequiredFormField(layoutData, itemId, fragmentEntryLinks) {
	const item = layoutData?.items?.[itemId];

	if (
		!item ||
		item.type !== LAYOUT_DATA_ITEM_TYPES.fragment ||
		!getFormParent(item, layoutData)
	) {
		return false;
	}

	const {classNameId, classTypeId} = selectFormConfiguration(
		item,
		layoutData
	);

	const cacheKey = getCacheKey([
		CACHE_KEYS.formFields,
		classNameId,
		classTypeId,
	]);

	const {data: fields} = getCacheItem(cacheKey);

	const promise = fields
		? Promise.resolve(fields)
		: FormService.getFormFields({
				classNameId,
				classTypeId,
			});

	const formFields = await promise;

	if (
		item.type === LAYOUT_DATA_ITEM_TYPES.fragment &&
		isRequiredFormInput(item, fragmentEntryLinks, formFields)
	) {
		return true;
	}
}

function isUsedInElementVariation({
	availableSegmentsExperiences,
	elementVariations,
	itemIds,
	layoutData,
	segmentsExperienceId,
}) {
	const segmentsExperienceERC =
		availableSegmentsExperiences[segmentsExperienceId]
			?.segmentsExperienceERC;

	const experienceElementVariations = elementVariations.filter(
		(elementVariation) =>
			elementVariation.segmentsExperienceERC === segmentsExperienceERC
	);

	if (!experienceElementVariations.length) {
		return false;
	}

	const deletedItemIds = itemIds.flatMap((itemId) => [
		itemId,
		...getDescendantIds(layoutData, itemId),
	]);

	return experienceElementVariations.some((elementVariation) =>
		deletedItemIds.some((itemId) =>
			elementVariation.targetElement.startsWith(
				`.${LAYOUT_STRUCTURE_ITEM_CLASS_NAME_PREFIX}${itemId} `
			)
		)
	);
}

function isUsedInRule(itemIds, layoutData) {
	return layoutData.pageRules.some((rule) => {
		const actionHasItem = rule.actions.some(({itemId}) =>
			itemIds.includes(itemId)
		);

		const conditionHasItem = rule.conditions.some(({field}) =>
			itemIds.includes(field)
		);

		return actionHasItem || conditionHasItem;
	});
}
