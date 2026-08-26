/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {config} from '../config';

interface Props {
	experienceId?: string;
	languageId: string;
}

export default function PagePreview({experienceId, languageId}: Props) {
	const params = new URLSearchParams({
		languageId,
		selPlid: String(Liferay.ThemeDisplay.getPlid()),
	});

	if (experienceId) {
		params.set('segmentsExperienceId', experienceId);
	}

	return (
		<iframe
			className="version-history__preview"
			src={`${config.getPagePreviewURL}?${params}`}
			title={Liferay.Language.get('preview')}
		/>
	);
}
