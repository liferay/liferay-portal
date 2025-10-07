/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

export function LoadingMessage() {
	return (
		<div className="text-center text-secondary" role="alert">
			<ClayLoadingIndicator displayType="secondary" size="sm" />

			<p className="c-mb-0">
				{Liferay.Language.get('the-upload-process-may-take-some-time')}
			</p>

			<p>
				{Liferay.Language.get(
					'closing-the-window-will-cancel-the-upload-process'
				)}
			</p>
		</div>
	);
}
