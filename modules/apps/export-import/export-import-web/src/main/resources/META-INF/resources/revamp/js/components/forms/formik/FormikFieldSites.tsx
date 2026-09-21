/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikValues, useFormikContext} from 'formik';
import React from 'react';

import {PreviewSite} from '../../../types/exportImportPreview';
import SitesControl from '../site_selector/SitesControl';

export function FormikFieldSites({
	apiURL,
	name,
	previewSites,
	showExistsInInstance,
	totalCount,
}: {
	apiURL?: string;
	name: string;
	previewSites?: PreviewSite[];
	showExistsInInstance?: boolean;
	totalCount?: number;
}) {
	const {setFieldValue, values} = useFormikContext<FormikValues>();

	return (
		<SitesControl
			apiURL={apiURL}
			onChange={(externalReferenceCodes) =>
				setFieldValue(name, externalReferenceCodes)
			}
			previewSites={previewSites}
			selectedExternalReferenceCodes={values[name] ?? []}
			showExistsInInstance={showExistsInInstance}
			totalCount={totalCount}
		/>
	);
}
