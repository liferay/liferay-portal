/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

export function LoadingMessage({
	description,
	title,
}: {
	description: string;
	title: string;
}) {
	return (
		<div className="align-items-center bg-white d-flex justify-content-center loading-message position-absolute">
			<div className="text-center text-secondary" role="alert">
				<ClayLoadingIndicator
					className="position-relative"
					displayType="secondary"
					size="sm"
				/>

				<p className="c-mb-0">{title}</p>

				<p>{description}</p>
			</div>
		</div>
	);
}
