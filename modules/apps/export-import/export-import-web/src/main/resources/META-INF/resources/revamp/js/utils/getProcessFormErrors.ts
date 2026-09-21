/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikValues} from 'formik';

/**
 * The errors shared by the process forms. `sitesEnabled` says whether sites are
 * on offer: where they are, either an entity type or a site satisfies the form,
 * and the error belongs to neither control, so it is reported as `selection`
 * rather than marking one of them invalid.
 */
export function getProcessFormErrors(
	values: FormikValues,
	sitesEnabled = false
): {
	[key: string]: string;
} {
	const errors: {[key: string]: string} = {};

	if (!values.name) {
		errors.name = Liferay.Language.get('this-field-is-required');
	}

	if (
		!values.contentSelection &&
		!values.siteExternalReferenceCodes?.length
	) {
		if (sitesEnabled) {
			errors.selection = Liferay.Language.get(
				'please-select-at-least-one-entity-type-or-site-to-continue'
			);
		}
		else {
			errors.contentSelection = Liferay.Language.get(
				'please-select-at-least-one-entity-type-to-continue'
			);
		}
	}

	return errors;
}
