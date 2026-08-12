/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from '../actions/addFragmentEntryLinks';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/freemarkerFragmentEntryProcessor';

export default function getFragmentConfigurationValues(
	fragmentEntryLink: FragmentEntryLink
) {
	return {
		...fragmentEntryLink.defaultConfigurationValues,
		...(fragmentEntryLink.editableValues[
			FREEMARKER_FRAGMENT_ENTRY_PROCESSOR
		] || {}),
	};
}
