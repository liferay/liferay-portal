/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../types/State';
import {updateNetwork} from '../actions';
import {
	ConfigurationValue,
	FragmentEntryLink,
} from '../actions/addFragmentEntryLinks';
import updateFragmentEntryLinkConfiguration from '../actions/updateFragmentEntryLinkConfiguration';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/freemarkerFragmentEntryProcessor';
import FragmentService from '../services/FragmentService';
import {clearPageContents} from '../utils/usePageContents';

export default function updateFragmentConfiguration({
	configurationValues,
	fragmentEntryLink,
}: {
	configurationValues: Record<string, ConfigurationValue>;
	fragmentEntryLink: FragmentEntryLink;
}) {
	const {editableValues, fragmentEntryLinkId} = fragmentEntryLink;

	const nextEditableValues = {
		...editableValues,
		[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: configurationValues,
	};

	return (
		dispatch: (
			action: ReturnType<
				| typeof updateNetwork
				| typeof updateFragmentEntryLinkConfiguration
			>
		) => void,
		getState: () => State
	) => {
		const {languageId, segmentsExperienceId} = getState();

		return FragmentService.updateConfigurationValues({
			editableValues:
				nextEditableValues as FragmentEntryLink['editableValues'],
			fragmentEntryLinkId,
			languageId,
			onNetworkStatus: dispatch,
			segmentsExperienceId,
		}).then(({fragmentEntryLink, layoutData}) => {
			dispatch(
				updateFragmentEntryLinkConfiguration({
					fragmentEntryLink,
					fragmentEntryLinkId,
					layoutData,
				})
			);

			clearPageContents();
		});
	};
}
