/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

function SideNavigationResultsSkeleton() {
	return (
		<div
			aria-label={Liferay.Language.get('loading')}
			className="side-navigation-results-skeleton"
			role="progressbar"
		>
			<svg viewBox="0 120 320 530">
				<use
					height="650"
					href={`${Liferay.ThemeDisplay.getPathThemeImages()}/skeletons/homes_side_navigation.svg#homes-side-navigation`}
					width="320"
				/>
			</svg>
		</div>
	);
}

export default SideNavigationResultsSkeleton;
