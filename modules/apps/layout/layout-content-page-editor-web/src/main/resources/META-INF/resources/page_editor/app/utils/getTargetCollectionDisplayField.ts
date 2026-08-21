/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from '../actions/addFragmentEntryLinks';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../config/constants/freemarkerFragmentEntryProcessor';

interface TargetCollectionDisplayField {
	fieldName: string;
	targetCollections: string[];
}

export default function getTargetCollectionDisplayField(
	fragmentEntryLink: FragmentEntryLink
): TargetCollectionDisplayField | null {
	const configuration = fragmentEntryLink.configuration as {
		fieldSets?: Array<{
			fields?: Array<{
				name: string;
				type: string;
			}>;
		}>;
	};

	for (const fieldSet of configuration?.fieldSets || []) {
		for (const field of fieldSet.fields || []) {
			if (field.type === 'targetCollectionDisplay') {
				const configValues =
					fragmentEntryLink.editableValues?.[
						FREEMARKER_FRAGMENT_ENTRY_PROCESSOR
					] || {};

				const targetCollections = configValues[field.name];

				return {
					fieldName: field.name,
					targetCollections: Array.isArray(targetCollections)
						? targetCollections
						: [],
				};
			}
		}
	}

	return null;
}
