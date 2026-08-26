/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {preventIframeNavigation} from '@liferay/layout-js-components-web';
import React from 'react';

import {config} from '../config';

const BLOCKED_EVENTS = ['auxclick', 'click', 'submit'] as const;

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

	const handleLoad = (event: React.SyntheticEvent<HTMLIFrameElement>) => {
		preventIframeNavigation(event);

		const iframe = event.target as HTMLIFrameElement;

		const iframeDocument = iframe.contentDocument;

		for (const type of BLOCKED_EVENTS) {
			iframeDocument?.addEventListener(type, blockEvent, true);
		}
	};

	return (
		<iframe
			className="version-history__preview"
			onLoad={handleLoad}
			src={`${config.getPagePreviewURL}?${params}`}
			title={Liferay.Language.get('preview')}
		/>
	);
}

function blockEvent(event: Event) {
	event.preventDefault();
	event.stopImmediatePropagation();
}
