/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

const IFRAME_LOAD_TIMEOUT_MS = 8000;

type Status = 'error' | 'idle' | 'loaded' | 'loading';

export default function useIframeLoad(previewURL: string | undefined) {
	const [status, setStatus] = useState<Status>('idle');
	const [iframeKey, setIframeKey] = useState<number>(0);

	useEffect(() => {
		if (!previewURL) {
			setStatus('idle');

			return;
		}

		setStatus('loading');

		const timeoutId = setTimeout(() => {
			setStatus((current) => (current === 'loading' ? 'error' : current));
		}, IFRAME_LOAD_TIMEOUT_MS);

		return () => clearTimeout(timeoutId);
	}, [iframeKey, previewURL]);

	const handleIframeLoad = () => {
		setStatus((current) => (current === 'loading' ? 'loaded' : current));
	};

	const reloadIframe = () => {
		setIframeKey((key) => key + 1);
	};

	return {
		handleIframeLoad,
		iframeError: status === 'error',
		iframeKey,
		isIframeLoading: status === 'loading',
		reloadIframe,
	};
}
