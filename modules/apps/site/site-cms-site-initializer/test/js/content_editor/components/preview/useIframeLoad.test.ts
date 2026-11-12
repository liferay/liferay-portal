/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import useIframeLoad from '../../../../../src/main/resources/META-INF/resources/js/content_editor/components/preview/useIframeLoad';

const EXTERNAL_URL = 'http://localhost:3000/index.html';

describe('useIframeLoad', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('shows an external URL that loads immediately', () => {
		const {result} = renderHook(() => useIframeLoad(EXTERNAL_URL));

		expect(result.current.isIframeLoading).toBe(true);

		act(() => {
			result.current.handleIframeLoad();
		});

		expect(result.current.iframeError).toBe(false);
		expect(result.current.isIframeLoading).toBe(false);
	});

	it('shows an error when the URL does not load before the timeout', () => {
		const {result} = renderHook(() => useIframeLoad(EXTERNAL_URL));

		act(() => {
			jest.advanceTimersByTime(8000);
		});

		expect(result.current.iframeError).toBe(true);
		expect(result.current.isIframeLoading).toBe(false);
	});

	it('loads the URL again after an error', () => {
		const {result} = renderHook(() => useIframeLoad(EXTERNAL_URL));

		act(() => {
			jest.advanceTimersByTime(8000);
		});

		act(() => {
			result.current.reloadIframe();
		});

		expect(result.current.iframeError).toBe(false);
		expect(result.current.isIframeLoading).toBe(true);

		act(() => {
			result.current.handleIframeLoad();
		});

		expect(result.current.iframeError).toBe(false);
		expect(result.current.isIframeLoading).toBe(false);
	});
});
