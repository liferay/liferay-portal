/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from '../app/actions/addFragmentEntryLinks';
import {FRAGMENT_ENTRY_TYPES} from '../app/config/constants/fragmentEntryTypes';

const DEFAULT_EDITABLE_VALUES: FragmentEntryLink['editableValues'] = {
	'com.liferay.fragment.entry.processor.background.image.BackgroundImageFragmentEntryProcessor':
		{},
	'com.liferay.fragment.entry.processor.editable.EditableFragmentEntryProcessor':
		{},
	'com.liferay.fragment.entry.processor.freemarker.FreeMarkerFragmentEntryProcessor':
		{},
};

const DEFAULT: FragmentEntryLink = {
	comments: [],
	configuration: {},
	content: '',
	cssClass: '',
	defaultConfigurationValues: {},
	editableTypes: {},
	editableValues: DEFAULT_EDITABLE_VALUES,
	fieldTypes: [],
	fragmentEntryId: '0',
	fragmentEntryKey: '',
	fragmentEntryLinkId: '0',
	fragmentEntryType: FRAGMENT_ENTRY_TYPES.composition,
	groupId: '0',
	icon: '',
	name: '',
	removed: false,
	segmentsExperienceId: '0',
};

export default function getMockFragmentEntryLink(
	overrides: Partial<Omit<FragmentEntryLink, 'editableValues'>> & {
		editableValues?: Partial<FragmentEntryLink['editableValues']>;
	} = {}
): FragmentEntryLink {
	return {
		...DEFAULT,
		...overrides,
		editableValues: {
			...DEFAULT_EDITABLE_VALUES,
			...overrides.editableValues,
		},
	};
}
