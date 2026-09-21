/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {FormikFieldSites} from '../../../components/forms/formik';
import {PreviewSite} from '../../../types/exportImportPreview';

export default function SiteSelection({
	previewSites,
}: {
	previewSites: PreviewSite[];
}) {
	return (
		<FormikFieldSites
			name="siteExternalReferenceCodes"
			previewSites={previewSites}
			process="import"
			totalCount={previewSites.length}
		/>
	);
}
