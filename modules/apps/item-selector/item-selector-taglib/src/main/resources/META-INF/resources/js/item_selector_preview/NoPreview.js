/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export default function NoPreview() {
	return (
		<div className="no-preview-container">
			<img
				alt=""
				className="no-preview-image"
				src={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/cms_empty_state_preview.svg`}
			/>

			<strong className="no-preview-title">
				{Liferay.Language.get('your-document-is-uploaded')}
			</strong>

			<p className="no-preview-description">
				{Liferay.Language.get(
					'a-preview-could-not-be-generated-but-the-document-is-available'
				)}
			</p>
		</div>
	);
}
