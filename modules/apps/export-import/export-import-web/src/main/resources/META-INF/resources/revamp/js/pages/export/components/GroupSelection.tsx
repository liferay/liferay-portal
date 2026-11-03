/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {FormikFieldGroups} from '../../../components/forms/formik';
import {getExportPreviewGroupsCount} from '../../../services/getExportPreviewGroupsCount';
import {GroupType} from '../../../types/exportImportPreview';

export default function GroupSelection({
	apiURL,
	name,
	type,
}: {
	apiURL: string;
	name: string;
	type: GroupType;
}) {
	const [totalCount, setTotalCount] = useState<number | undefined>();

	useEffect(() => {
		getExportPreviewGroupsCount(apiURL).then((response) => {
			if (response.data) {
				setTotalCount(response.data.totalCount);
			}
		});
	}, [apiURL]);

	return (
		<FormikFieldGroups
			apiURL={apiURL}
			name={name}
			totalCount={totalCount}
			type={type}
		/>
	);
}
