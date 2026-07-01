/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React from 'react';

function messageFromError(error: Error | string): string {
	const message = typeof error === 'string' ? error : error.message;

	return message || Liferay.Language.get('an-error-occurred');
}

interface Props {
	error?: Error | string;
	style?: React.CSSProperties;
}

export default function ChartErrorState({error, style}: Props) {
	return (
		<div style={style}>
			<ClayAlert
				displayType="danger"
				title={Liferay.Language.get('error')}
			>
				{error
					? messageFromError(error)
					: Liferay.Language.get('an-error-occurred')}
			</ClayAlert>
		</div>
	);
}
