/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '../css/main.scss';

export default function DLVideoIframe({
	videoPreviewURL,
}: {
	videoPreviewURL: string;
}) {
	return (
		<div className="preview-file video-preview video-preview-framed">
			<div className="video-preview-aspect-ratio">
				<iframe
					data-video-liferay
					height="315"
					src={videoPreviewURL}
					width="560"
				/>
			</div>
		</div>
	);
}
