/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {LabelRenderer} from '@liferay/frontend-data-set-web';
import React from 'react';

const labelDisplayStyles = {
	1: 'success',
	2: 'danger',
};

export default function ReportEntryStatusRenderer({
	value,
}: {
	value?: {
		code: 1 | 2;
		label: string;
		label_i18n?: string;
	};
}) {
	if (!value) {
		return null;
	}

	return (
		<LabelRenderer
			value={{
				displayStyle: labelDisplayStyles[value.code],
				label: value.label_i18n ?? value.label,
			}}
		/>
	);
}
