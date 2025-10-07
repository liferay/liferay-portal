/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getAllEditables from '../../../../../app/components/fragment_content/getAllEditables';
import getAllPortals from '../../../../../app/components/layout_data_items/getAllPortals';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPE_LABELS} from '../../../../../app/config/constants/editableTypeLabels';
import {EDITABLE_TYPES} from '../../../../../app/config/constants/editableTypes';
import {FRAGMENT_ENTRY_TYPES} from '../../../../../app/config/constants/fragmentEntryTypes';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../app/config/constants/freemarkerFragmentEntryProcessor';
import {ITEM_TYPES} from '../../../../../app/config/constants/itemTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../app/config/constants/layoutDataItemTypes';
import {config} from '../../../../../app/config/index';
import selectLayoutDataItemLabel from '../../../../../app/selectors/selectLayoutDataItemLabel';
import canActivateEditable from '../../../../../app/utils/canActivateEditable';
import isMapped from '../../../../../app/utils/editable_value/isMapped';
import isMappedToCollection from '../../../../../app/utils/editable_value/isMappedToCollection';
import findPageContent from '../../../../../app/utils/findPageContent';
import {formIsMapped} from '../../../../../app/utils/formIsMapped';
import {formIsRestricted} from '../../../../../app/utils/formIsRestricted';
import getMappingFieldsKey from '../../../../../app/utils/getMappingFieldsKey';
import getSelectedField from '../../../../../app/utils/getSelectedField';
import isHideable from '../../../../../app/utils/isHideable';
import {isItemHidden} from '../../../../../app/utils/isItemHidden';
import isRemovable from '../../../../../app/utils/isRemovable';

const EDITABLE_TYPE_ICONS = {
	[EDITABLE_TYPES.action]: 'cursor',
	[EDITABLE_TYPES.backgroundImage]: 'picture',
	[EDITABLE_TYPES.html]: 'code',
	[EDITABLE_TYPES.image]: 'picture',
	[EDITABLE_TYPES.link]: 'link',
	[EDITABLE_TYPES['rich-text']]: 'text-editor',
	[EDITABLE_TYPES.text]: 'text',
};

const LAYOUT_DATA_ITEM_TYPE_ICONS = {
	[LAYOUT_DATA_ITEM_TYPES.collection]: 'list',
	[LAYOUT_DATA_ITEM_TYPES.collectionItem]: 'document',
	[LAYOUT_DATA_ITEM_TYPES.container]: 'container',
	[LAYOUT_DATA_ITEM_TYPES.form]: 'container',
	[LAYOUT_DATA_ITEM_TYPES.formStep]: 'arrow-end',
	[LAYOUT_DATA_ITEM_TYPES.formStepContainer]: 'table',
	[LAYOUT_DATA_ITEM_TYPES.dropZone]: 'box-container',
	[LAYOUT_DATA_ITEM_TYPES.fragment]: 'code',
	[LAYOUT_DATA_ITEM_TYPES.fragmentDropZone]: 'box-container',
	[LAYOUT_DATA_ITEM_TYPES.root]: 'page',
	[LAYOUT_DATA_ITEM_TYPES.row]: 'table',
};

export function getCollectionAncestor(layoutData, itemId) {
	const item = layoutData.items[itemId];

	if (!item) {
		if (process.env.NODE_ENV === 'development') {
			console.error(
				`Item with id ${itemId} does not exist in layout data.`
			);
		}
	}

	const parent = layoutData.items[item.parentId];

	if (!parent) {
		return null;
	}

	return parent.type === LAYOUT_DATA_ITEM_TYPES.collection
		? parent
		: getCollectionAncestor(layoutData, item.parentId);
}

function getDocumentFragment(content) {
	const fragment = document.createDocumentFragment();
	const div = document.createElement('div');

	div.innerHTML = content;

	return fragment.appendChild(div);
}

function getKey({collectionConfig, editable, infoItem, selectedMappingTypes}) {
	if (collectionConfig) {
		if (collectionConfig.classNameId) {
			return getMappingFieldsKey(collectionConfig);
		}
		else {
			return collectionConfig.key;
		}
	}
	else if (editable.mappedField) {
		return getMappingFieldsKey(selectedMappingTypes);
	}
	else if (!infoItem) {
		return null;
	}

	return getMappingFieldsKey(infoItem);
}

function getMappedFieldLabel(
	editable,
	collectionConfig,
	pageContents,
	mappingFields
) {
	const infoItem = findPageContent(pageContents, editable);

	const {selectedMappingTypes} = config;

	if (!infoItem && !selectedMappingTypes && !collectionConfig) {
		for (const [mappingFieldsKey, fields] of Object.entries(
			mappingFields
		)) {
			if (mappingFieldsKey.startsWith(editable.classNameId)) {
				const field = getSelectedField({
					fields,
					mappingFieldsKey,
					value:
						editable.mappedField ||
						editable.fieldId ||
						editable.collectionFieldId,
				});

				return field?.label;
			}
		}

		return null;
	}

	const key = getKey({
		collectionConfig,
		editable,
		infoItem,
		selectedMappingTypes,
	});
	const fields = mappingFields[key];

	if (fields) {
		const field = getSelectedField({
			fields,
			mappingFieldsKey: key,
			value:
				editable.mappedField ||
				editable.fieldId ||
				editable.collectionFieldId,
		});

		return field?.label;
	}

	return null;
}

function getNameInfo(item) {
	if (
		item.type === LAYOUT_DATA_ITEM_TYPES.container &&
		item.config.htmlTag !== 'div'
	) {
		return item.config.htmlTag;
	}

	return null;
}

function fragmentIsMapped(item, fragmentEntryLinks) {
	if (item.type === LAYOUT_DATA_ITEM_TYPES.form) {
		return formIsMapped(item);
	}
	else if (item.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
		const {editableValues, fragmentEntryType} =
			fragmentEntryLinks[item.config.fragmentEntryLinkId];

		return fragmentEntryType === FRAGMENT_ENTRY_TYPES.input
			? Boolean(
					editableValues[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]
						?.inputFieldId
				)
			: false;
	}

	return false;
}

export default function getTreeNodes(
	item,
	items,
	{
		canUpdateEditables,
		canUpdateItemConfiguration,
		editingNodeId,
		fragmentEntryLinks,
		hasHiddenAncestor,
		isMasterPage,
		layoutData,
		layoutDataRef,
		mappingFields,
		masterLayoutData,
		onHoverNode,
		pageContents,
		restrictedItemIds,
		selectedViewportSize,
	}
) {
	const children = [];

	const itemInMasterLayout =
		masterLayoutData &&
		Object.keys(masterLayoutData.items).includes(item.itemId);

	const hidden = isItemHidden(layoutData, item.itemId, selectedViewportSize);

	let icon = LAYOUT_DATA_ITEM_TYPE_ICONS[item.type];

	if (item.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
		const fragmentEntryLink =
			fragmentEntryLinks[item.config.fragmentEntryLinkId];

		if (!fragmentEntryLink) {
			if (process.env.NODE_ENV === 'development') {
				console.error(
					`No fragment entry link with id ${item.config.fragmentEntryLinkId} found for fragment id ${item.itemId}.`
				);
			}
		}

		icon = fragmentEntryLink.icon || icon;

		const documentFragment = getDocumentFragment(fragmentEntryLink.content);

		const sortedElements = [
			...getAllEditables(documentFragment),
			...getAllPortals(documentFragment),
		].sort((a, b) => a.priority - b.priority);

		const editableTypes = fragmentEntryLink.editableTypes;

		let collectionAncestor = null;

		sortedElements.forEach((element) => {
			if (element.editableId) {
				const {editableId} = element;

				const editable =
					fragmentEntryLink.editableValues[
						EDITABLE_FRAGMENT_ENTRY_PROCESSOR
					]?.[editableId];

				const childId = `${item.config.fragmentEntryLinkId}-${editableId}`;
				const type =
					editableTypes[editableId] || EDITABLE_TYPES.backgroundImage;

				if (!collectionAncestor) {
					collectionAncestor = isMappedToCollection(editable)
						? getCollectionAncestor(
								fragmentEntryLink.masterLayout
									? masterLayoutData
									: layoutData,
								item.itemId
							)
						: null;
				}

				const collectionConfig = collectionAncestor?.config?.collection;

				const mappedFieldLabel = isMapped(editable)
					? getMappedFieldLabel(
							editable,
							collectionConfig,
							pageContents,
							mappingFields
						)
					: null;

				children.push({
					activable:
						canUpdateEditables &&
						canActivateEditable(selectedViewportSize, type),
					children: [],
					draggable: false,
					hidden: false,
					hiddenAncestor: hasHiddenAncestor || hidden,
					hideable: false,
					icon: EDITABLE_TYPE_ICONS[type],
					id: childId,
					isMasterItem: !isMasterPage && itemInMasterLayout,
					itemType: ITEM_TYPES.editable,
					mapped: isMapped(editable),
					name: mappedFieldLabel || editableId,
					onHoverNode,
					parentId: item.itemId,
					removable: false,
					tooltipTitle: EDITABLE_TYPE_LABELS[type],
				});
			}
			else {
				const {dropZoneId, mainItemId} = element;

				if (!mainItemId) {
					if (process.env.NODE_ENV === 'development') {
						console.error(
							`Dropzone belonging to ${item.itemId} does not have a uuid.`
						);
					}
				}

				const mainItem = items[mainItemId];

				if (!mainItem) {
					if (process.env.NODE_ENV === 'development') {
						console.error(
							`Dropzone with uuid ${mainItemId} was not found in the layout data.`
						);
					}
				}

				children.push({
					...getTreeNodes(mainItem, items, {
						canUpdateEditables,
						canUpdateItemConfiguration,
						editingNodeId,
						fragmentEntryLinks,
						hasHiddenAncestor: hasHiddenAncestor || hidden,
						isMasterPage,
						layoutData,
						layoutDataRef,
						mappingFields,
						masterLayoutData,
						onHoverNode,
						pageContents,
						restrictedItemIds,
						selectedViewportSize,
					}),

					name: `${Liferay.Language.get('drop-zone')} ${dropZoneId}`,
					removable: false,
				});
			}
		});
	}
	else {
		item.children.forEach((childId) => {
			if (
				(item.type === LAYOUT_DATA_ITEM_TYPES.collection &&
					(!item.config.collection ||
						restrictedItemIds.has(item.itemId))) ||
				(item.type === LAYOUT_DATA_ITEM_TYPES.form &&
					(!formIsMapped(item) || formIsRestricted(item)))
			) {
				return;
			}

			const childItem = items[childId];
			if (!childItem) {
				if (process.env.NODE_ENV === 'development') {
					console.error(
						`Child item with id ${childId} was not found in the layout data.`
					);
				}
			}

			if (
				!isMasterPage &&
				childItem.type === LAYOUT_DATA_ITEM_TYPES.dropZone
			) {
				const mainItem = layoutData.items[layoutData.rootItems.main];

				if (!mainItem) {
					if (process.env.NODE_ENV === 'development') {
						console.error(
							`Root with id ${layoutData.rootItems.main} was not found in the layout data.`
						);
					}
				}

				const dropZoneChildren = getTreeNodes(
					mainItem,
					layoutData.items,
					{
						canUpdateEditables,
						canUpdateItemConfiguration,
						editingNodeId,
						fragmentEntryLinks,
						hasHiddenAncestor: hasHiddenAncestor || hidden,
						isMasterPage,
						layoutData,
						layoutDataRef,
						mappingFields,
						masterLayoutData,
						onHoverNode,
						pageContents,
						restrictedItemIds,
						selectedViewportSize,
					}
				).children;

				children.push(...dropZoneChildren);
			}
			else {
				const child = getTreeNodes(childItem, items, {
					canUpdateEditables,
					canUpdateItemConfiguration,
					editingNodeId,
					fragmentEntryLinks,
					hasHiddenAncestor: hasHiddenAncestor || hidden,
					isMasterPage,
					layoutData,
					layoutDataRef,
					mappingFields,
					masterLayoutData,
					onHoverNode,
					pageContents,
					restrictedItemIds,
					selectedViewportSize,
				});

				children.push(child);
			}
		});
	}

	return {
		activable:
			item.type !== LAYOUT_DATA_ITEM_TYPES.collectionItem &&
			canUpdateItemConfiguration,
		children,
		config: layoutDataRef?.current?.items[item.itemId]?.config,
		draggable: true,
		hidden,
		hiddenAncestor: hasHiddenAncestor,
		hideable:
			!itemInMasterLayout &&
			isHideable(item, fragmentEntryLinks, layoutData),
		icon,
		id: item.itemId,
		isMasterItem: !isMasterPage && itemInMasterLayout,
		itemType: ITEM_TYPES.layoutDataItem,
		mapped: fragmentIsMapped(item, fragmentEntryLinks),
		name: selectLayoutDataItemLabel({fragmentEntryLinks, layoutData}, item),
		nameInfo: getNameInfo(item),
		onHoverNode,
		parentItemId: item.parentId,
		removable: !itemInMasterLayout && isRemovable(item, layoutData),
		tooltipTitle: selectLayoutDataItemLabel(
			{fragmentEntryLinks, layoutData},
			item,
			{
				useCustomName: false,
			}
		),
		type: item.type,
	};
}
