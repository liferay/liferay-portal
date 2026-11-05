/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormikValues, useFormikContext} from 'formik';
import React from 'react';

import {Group, GroupType} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import GroupsControl from '../group_selector/GroupsControl';

export function FormikFieldGroups({
	apiURL,
	groups,
	name,
	process,
	totalCount,
	type,
}: {
	apiURL?: string;
	groups?: Group[];
	name: string;
	process?: Exclude<ExportImportProcess, 'publish'>;
	totalCount?: number;
	type: GroupType;
}) {
	const {setFieldTouched, setFieldValue, values} =
		useFormikContext<FormikValues>();

	return (
		<GroupsControl
			apiURL={apiURL}
			groups={groups}
			onChange={(externalReferenceCodes) => {
				setFieldValue(name, externalReferenceCodes);
				setFieldTouched(name, true, false);
			}}
			process={process}
			selectedExternalReferenceCodes={values[name] ?? []}
			totalCount={totalCount}
			type={type}
		/>
	);
}
