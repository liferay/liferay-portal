/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import React from 'react';

interface Props {
	message?: string;
	style?: React.CSSProperties;
}

export default function ChartEmptyState({message, style}: Props) {
	return (
		<div aria-live="assertive" style={style}>
			<ClayEmptyState
				description={
					message ?? Liferay.Language.get('there-is-no-data')
				}
				image={
					<ClayIcon
						className="c-empty-state-icon c-empty-state-icon-empty-state"
						illustration
						symbol="empty-state"
					/>
				}
				title={Liferay.Language.get('no-data-available')}
			/>
		</div>
	);
}
