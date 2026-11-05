/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikValues} from 'formik';

import {
	EnabledGroupSelection,
	getEnabledGroupSelection,
} from './getEnabledGroupSelection';

const SELECTION_MESSAGES: Record<EnabledGroupSelection, string> = {
	sites: Liferay.Language.get(
		'please-select-at-least-one-entity-type-or-site-to-continue'
	),
	sitesAndSpaces: Liferay.Language.get(
		'please-select-at-least-one-entity-type,-site,-or-space-to-continue'
	),
	spaces: Liferay.Language.get(
		'please-select-at-least-one-entity-type-or-space-to-continue'
	),
};

export function getProcessFormErrors(
	values: FormikValues,
	options: {
		siteSelectionEnabled?: boolean;
		spaceSelectionEnabled?: boolean;
	} = {}
): {
	[key: string]: string;
} {
	const errors: {[key: string]: string} = {};

	if (!values.name) {
		errors.name = Liferay.Language.get('this-field-is-required');
	}

	if (
		!values.contentSelection &&
		!values.siteExternalReferenceCodes?.length &&
		!values.spaceExternalReferenceCodes?.length
	) {
		const enabledGroupSelection = getEnabledGroupSelection(options);

		if (enabledGroupSelection) {
			errors.selection = SELECTION_MESSAGES[enabledGroupSelection];
		}
		else {
			errors.contentSelection = Liferay.Language.get(
				'please-select-at-least-one-entity-type-to-continue'
			);
		}
	}

	return errors;
}
