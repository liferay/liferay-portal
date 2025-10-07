/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FragmentEntryLink,
	FragmentEntryLinkMap,
} from '../../../app/actions/addFragmentEntryLinks';
import {WidgetSet} from '../../../app/actions/updateWidgets';
import {FormLayoutDataItem} from '../../../types/layout_data/FormLayoutDataItem';
import {FragmentLayoutDataItem} from '../../../types/layout_data/FragmentLayoutDataItem';
import {
	LayoutData,
	LayoutDataItem,
} from '../../../types/layout_data/LayoutData';
import {FRAGMENT_ENTRY_TYPES} from '../../config/constants/fragmentEntryTypes';
import {LAYOUT_DATA_ITEM_TYPES} from '../../config/constants/layoutDataItemTypes';
import {getStepperChild} from '../../utils/getStepperChild';
import {formIsMapped} from '../formIsMapped';
import {getFormParent} from '../getFormParent';
import {getFormStepIndex} from '../getFormStepIndex';
import getItemWidget from '../getItemWidget';
import getWidget from '../getWidget';
import {hasCollectionParent} from '../hasCollectionParent';
import isLocalizationSelect from '../isLocalizationSelect';
import {isMultistepForm} from '../isMultistepForm';
import isStepper from '../isStepper';
import {isUnmappedCollection} from '../isUnmappedCollection';
import {isUnmappedForm} from '../isUnmappedForm';

export type MovementItem = LayoutDataItem & {
	fieldTypes: FragmentEntryLink['fieldTypes'];
	fragmentEntryType: FragmentEntryLink['fragmentEntryType'] | null;
	isWidget: boolean;
	name: string;
	portletId?: string;
};

const LAYOUT_DATA_CHECK_ALLOWED_CHILDREN = {
	[LAYOUT_DATA_ITEM_TYPES.root]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.dropZone,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
			LAYOUT_DATA_ITEM_TYPES.form,
		].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.collection]: () => false,
	[LAYOUT_DATA_ITEM_TYPES.collectionItem]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
		].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.dropZone]: () => false,
	[LAYOUT_DATA_ITEM_TYPES.container]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.dropZone,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
			LAYOUT_DATA_ITEM_TYPES.form,
		].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.form]: (
		child: LayoutDataItem,
		parent: LayoutDataItem
	) =>
		formIsMapped(parent as FormLayoutDataItem)
			? [
					LAYOUT_DATA_ITEM_TYPES.collection,
					LAYOUT_DATA_ITEM_TYPES.container,
					LAYOUT_DATA_ITEM_TYPES.dropZone,
					LAYOUT_DATA_ITEM_TYPES.row,
					LAYOUT_DATA_ITEM_TYPES.fragment,
					LAYOUT_DATA_ITEM_TYPES.formRelationship,
				].some((type) => type === child.type)
			: false,

	[LAYOUT_DATA_ITEM_TYPES.formRelationship]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.dropZone,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
			LAYOUT_DATA_ITEM_TYPES.formRelationship,
		].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.formStep]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.dropZone,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
			LAYOUT_DATA_ITEM_TYPES.form,
			LAYOUT_DATA_ITEM_TYPES.formRelationship,
		].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.formStepContainer]: (child: LayoutDataItem) =>
		[LAYOUT_DATA_ITEM_TYPES.formStep].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.row]: (child: LayoutDataItem) =>
		[LAYOUT_DATA_ITEM_TYPES.column].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.column]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.dropZone,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
			LAYOUT_DATA_ITEM_TYPES.form,
		].some((type) => type === child.type),
	[LAYOUT_DATA_ITEM_TYPES.fragment]: () => false,
	[LAYOUT_DATA_ITEM_TYPES.fragmentDropZone]: (child: LayoutDataItem) =>
		[
			LAYOUT_DATA_ITEM_TYPES.collection,
			LAYOUT_DATA_ITEM_TYPES.dropZone,
			LAYOUT_DATA_ITEM_TYPES.container,
			LAYOUT_DATA_ITEM_TYPES.row,
			LAYOUT_DATA_ITEM_TYPES.fragment,
			LAYOUT_DATA_ITEM_TYPES.form,
		].some((type) => type === child.type),
};

type Result = {
	reason?:
		| 'input-outside-form'
		| 'disabled-part-of-collection'
		| 'existing-stepper'
		| 'form-relationship-outside-form'
		| 'noninstanceable-widget-inside-collection'
		| 'stepper-outside-form'
		| 'stepper-multiple-action'
		| 'targeting-step-container'
		| 'unmapped-collection'
		| 'unmapped-form'
		| 'widget-inside-form';
	valid: boolean;
};

/**
 * Checks if the given child can be nested inside given parent
 */
export default function checkAllowedChild(
	child: MovementItem,
	parent: MovementItem,
	layoutData: LayoutData,
	fragmentEntryLinks: FragmentEntryLinkMap,
	getWidgets: () => WidgetSet[],
	isMultiple: boolean = false
): Result {
	if (isUnmappedCollection(parent)) {
		return {reason: 'unmapped-collection', valid: false};
	}
	else if (parent.type === LAYOUT_DATA_ITEM_TYPES.collection) {
		return {reason: 'disabled-part-of-collection', valid: false};
	}

	if (isUnmappedForm(parent)) {
		return {reason: 'unmapped-form', valid: false};
	}

	if (isMultiple && isStepper(child)) {
		return {reason: 'stepper-multiple-action', valid: false};
	}

	const formParent = getFormParent(parent, layoutData);

	if (
		!isStepper(child) &&
		isMultistepForm(formParent) &&
		getFormStepIndex(parent, layoutData) === null
	) {
		return {reason: 'targeting-step-container', valid: false};
	}

	if (child.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
		if (isStepper(child)) {
			if (parent.type !== LAYOUT_DATA_ITEM_TYPES.form) {
				return {reason: 'stepper-outside-form', valid: false};
			}

			const existingStepper = getStepperChild(
				parent,
				layoutData,
				fragmentEntryLinks
			);

			if (existingStepper && existingStepper.itemId !== child.itemId) {
				return {reason: 'existing-stepper', valid: false};
			}
		}
		else {
			if (formParent && child.isWidget) {
				return {reason: 'widget-inside-form', valid: false};
			}

			if (hasCollectionParent(parent, layoutData) && child.isWidget) {
				const childItem = layoutData.items[child.itemId];

				const widgets = getWidgets();

				const widget = child.portletId
					? getWidget(widgets, child.portletId)
					: getItemWidget(
							childItem as FragmentLayoutDataItem,
							fragmentEntryLinks,
							widgets
						);

				if (!widget?.instanceable) {
					return {
						reason: 'noninstanceable-widget-inside-collection',
						valid: false,
					};
				}
			}
		}
	}

	if (!formParent && child.type === 'form-relationship') {
		return {reason: 'form-relationship-outside-form', valid: false};
	}

	if (
		!formParent &&
		hasInputChildOutsideForm(child, layoutData, fragmentEntryLinks)
	) {
		return {reason: 'input-outside-form', valid: false};
	}

	if (!LAYOUT_DATA_CHECK_ALLOWED_CHILDREN[parent.type](child, parent)) {
		return {valid: false};
	}

	return {valid: true};
}

function hasInputChildOutsideForm(
	child: MovementItem,
	layoutData: LayoutData,
	fragmentEntryLinks: FragmentEntryLinkMap
): boolean {
	if (
		child.fragmentEntryType === FRAGMENT_ENTRY_TYPES.input &&
		!isLocalizationSelect(child)
	) {
		return true;
	}

	const childItem = layoutData.items[child.itemId];

	if (!childItem || childItem.type === LAYOUT_DATA_ITEM_TYPES.form) {
		return false;
	}

	if (childItem.type === LAYOUT_DATA_ITEM_TYPES.fragment) {
		const fragment =
			fragmentEntryLinks?.[childItem.config.fragmentEntryLinkId!];

		return (
			fragment.fragmentEntryType === FRAGMENT_ENTRY_TYPES.input &&
			!isLocalizationSelect(fragment)
		);
	}

	return childItem.children?.some((childId) => {
		const child = layoutData.items[childId];

		return hasInputChildOutsideForm(
			child as MovementItem,
			layoutData,
			fragmentEntryLinks
		);
	});
}
