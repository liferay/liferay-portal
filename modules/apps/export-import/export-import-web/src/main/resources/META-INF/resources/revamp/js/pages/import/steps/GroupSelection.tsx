/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {FormikFieldGroups} from '../../../components/forms/formik';
import {Group, GroupType} from '../../../types/exportImportPreview';

export default function GroupSelection({
	groups,
	name,
	type,
}: {
	groups: Group[];
	name: string;
	type: GroupType;
}) {
	return (
		<FormikFieldGroups
			groups={groups}
			name={name}
			process="import"
			totalCount={groups.length}
			type={type}
		/>
	);
}
