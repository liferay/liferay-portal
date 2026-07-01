/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React from 'react';

interface Props {
	error?: Error | string;
	style?: React.CSSProperties;
}

export default function ChartErrorState({error, style}: Props) {
	const errorMessage = typeof error === 'string' ? error : error?.message;
	const safeMessage =
		errorMessage || Liferay.Language.get('an-error-occurred');

	return (
		<div style={style}>
			<ClayAlert
				displayType="danger"
				title={Liferay.Language.get('error')}
			>
				{safeMessage}
			</ClayAlert>
		</div>
	);
}
