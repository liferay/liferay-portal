/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {FormikFieldSites} from '../../../components/forms/formik';
import {PreviewSite} from '../../../types/exportImportPreview';

/**
 * The Sites row of the import form. The sites are the ones the file carries, so
 * the dialog works from that list rather than reading the instance.
 */
export default function SiteSelection({
	previewSites,
}: {
	previewSites: PreviewSite[];
}) {
	return (
		<FormikFieldSites
			name="siteExternalReferenceCodes"
			previewSites={previewSites}
			showExistsInInstance
			totalCount={previewSites.length}
		/>
	);
}
