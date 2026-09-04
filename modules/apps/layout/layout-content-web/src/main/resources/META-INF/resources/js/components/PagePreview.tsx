/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {preventIframeNavigation} from '@liferay/layout-js-components-web';
import React, {useState} from 'react';

import {config} from '../config';

const BLOCKED_EVENTS = ['auxclick', 'click', 'submit'] as const;

const FORWARDED_EVENTS = ['pointerdown', 'pointerup'] as const;

interface Props {
	experienceERC?: string;
	experienceId?: string;
	languageId: string;
	versionERC?: string;
}

export default function PagePreview({
	experienceERC,
	experienceId,
	languageId,
	versionERC,
}: Props) {
	const [loadedSrc, setLoadedSrc] = useState<string>();

	const params = new URLSearchParams({languageId});

	let url = config.getPagePreviewURL;

	if (versionERC) {
		url = config.getPageVersionPreviewURL;

		params.set('externalReferenceCode', versionERC);
		params.set('groupId', String(Liferay.ThemeDisplay.getScopeGroupId()));

		if (experienceERC) {
			params.set('segmentsExperienceERC', experienceERC);
		}
	}
	else {
		params.set('p_l_id', String(Liferay.ThemeDisplay.getPlid()));
		params.set('p_l_mode', 'preview');
		params.set('selPlid', String(Liferay.ThemeDisplay.getPlid()));

		if (experienceId) {
			params.set('segmentsExperienceId', experienceId);
		}
	}

	const src = `${url}?${params}`;

	const loading = loadedSrc !== src;

	const handleLoad = (event: React.SyntheticEvent<HTMLIFrameElement>) => {
		preventIframeNavigation(event);

		const iframe = event.target as HTMLIFrameElement;

		const iframeDocument = iframe.contentDocument;

		for (const type of BLOCKED_EVENTS) {
			iframeDocument?.addEventListener(type, blockEvent, true);
		}

		for (const type of FORWARDED_EVENTS) {
			iframeDocument?.addEventListener(type, forwardEvent, true);
		}

		setLoadedSrc(src);
	};

	return (
		<div className="position-relative">
			{loading ? (
				<div className="align-items-center d-flex h-100 justify-content-center position-absolute w-100">
					<ClayLoadingIndicator
						displayType="primary"
						shape="squares"
						size="lg"
						title={Liferay.Language.get('loading')}
					/>
				</div>
			) : null}

			<iframe
				className="version-history__preview"
				onLoad={handleLoad}
				src={src}
				style={{visibility: loading ? 'hidden' : 'visible'}}
				title={Liferay.Language.get('preview')}
			/>
		</div>
	);
}

function blockEvent(event: Event) {
	event.preventDefault();
	event.stopImmediatePropagation();
}

function forwardEvent(event: Event) {
	document.body.dispatchEvent(new PointerEvent(event.type, {bubbles: true}));
}
