/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React from 'react';

function getImage(filename: string) {
	return `${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/frontend-js-components-web/images/${filename}`;
}

export default function DragZoneBackground() {
	return (
		<div className="text-center text-secondary">
			<img
				alt={Liferay.Language.get('drag-and-drop-your-files-or')}
				src={getImage('drag_drop_image.svg')}
			/>

			<p className="my-2 text-weight-semi-bold">
				{Liferay.Language.get('drag-and-drop-your-files-or')}
			</p>

			<ClayButton displayType="secondary">
				{Liferay.Language.get('select-files')}
			</ClayButton>
		</div>
	);
}
