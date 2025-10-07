/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {config} from '../config/index';
import {ObjectFields} from '../contexts/ObjectDataContext';
import draftServiceFetch, {OnNetworkStatus} from './draftServiceFetch';
import serviceFetch from './serviceFetch';

import type {FormLayoutDataItem} from '../../types/layout_data/FormLayoutDataItem';
import type {LayoutData} from '../../types/layout_data/LayoutData';
import type {
	FragmentEntryLink,
	FragmentEntryLinkMap,
} from '../actions/addFragmentEntryLinks';

export interface FormField {
	key: string;
	label: string;
	localizable: boolean;
	name: string;
	required: boolean;
	type: string;
	typeLabel: string;
}

export default {
	getFormConfig({classNameId}: {classNameId: string}) {
		return serviceFetch<{supportStatus: boolean}>(config.getFormConfigURL, {
			body: {classNameId},
		});
	},

	getFormFields({
		classNameId,
		classTypeId,
	}: {
		classNameId: string;
		classTypeId: string;
	}) {
		return serviceFetch<ObjectFields>(config.getFormFieldsURL, {
			body: {
				classNameId,
				classTypeId,
			},
		});
	},

	getFragmentEntryInputFieldTypes({
		fragmentEntryKey,
		groupId,
	}: {
		fragmentEntryKey: string;
		groupId?: string;
	}) {
		return serviceFetch<string[]>(
			config.getFragmentEntryInputFieldTypesURL,
			{
				body: {
					fragmentEntryKey,
					groupId: groupId || null,
				},
			}
		);
	},

	removeFormStep({
		itemId,
		onNetworkStatus,
		segmentsExperienceId,
		stepperFragmentEntryLinkId,
	}: {
		itemId: string;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
		stepperFragmentEntryLinkId?: string;
	}) {
		return draftServiceFetch<{
			fragmentEntryLinks: FragmentEntryLinkMap;
			layoutData: LayoutData;
			movedItemIds: {itemId: string; parentId: string}[];
			removedItemIds: string[];
		}>(
			config.deleteFormStepURL,
			{
				body: {
					itemId,
					segmentsExperienceId,
					stepperFragmentEntryLinkId,
				},
			},
			onNetworkStatus
		);
	},

	updateFormItemConfig({
		fields,
		itemConfig,
		itemId,
		onNetworkStatus,
		segmentsExperienceId,
		stepperFragmentEntryLinkId,
	}: {
		fields: string[] | undefined;
		itemConfig: FormLayoutDataItem['config'];
		itemId: string;
		onNetworkStatus: OnNetworkStatus;
		segmentsExperienceId: string;
		stepperFragmentEntryLinkId: FragmentEntryLink['fragmentEntryLinkId'];
	}) {
		return draftServiceFetch<{
			errorMessage?: string;
			fragmentEntryLinks: FragmentEntryLinkMap;
			layoutData: LayoutData;
			removedFragmentEntryLinkIds: string[];
		}>(
			config.updateFormItemConfigURL,
			{
				body: {
					...(fields && {fields}),
					itemConfig: JSON.stringify(itemConfig),
					itemId,
					segmentsExperienceId,
					stepperFragmentEntryLinkId,
				},
			},
			onNetworkStatus
		);
	},
};
