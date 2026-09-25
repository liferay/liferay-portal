/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikValues, useFormikContext} from 'formik';
import React from 'react';

import {PreviewSite} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import SitesControl from '../site_selector/SitesControl';

export function FormikFieldSites({
	apiURL,
	name,
	previewSites,
	process,
	totalCount,
}: {
	apiURL?: string;
	name: string;
	previewSites?: PreviewSite[];
	process?: ExportImportProcess;
	totalCount?: number;
}) {
	const {setFieldTouched, setFieldValue, values} =
		useFormikContext<FormikValues>();

	return (
		<SitesControl
			apiURL={apiURL}
			onChange={(externalReferenceCodes) => {
				setFieldValue(name, externalReferenceCodes);
				setFieldTouched(name, true, false);
			}}
			previewSites={previewSites}
			process={process}
			selectedExternalReferenceCodes={values[name] ?? []}
			totalCount={totalCount}
		/>
	);
}
