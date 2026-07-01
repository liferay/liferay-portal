/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

interface Props {
	style?: React.CSSProperties;
}

export default function ChartLoadingState({style}: Props) {
	return (
		<div
			aria-live="polite"
			className="align-items-center d-flex justify-content-center"
			role="status"
			style={style}
		>
			<ClayLoadingIndicator
				aria-label={Liferay.Language.get('loading')}
			/>
		</div>
	);
}
