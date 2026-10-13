/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {usePrefersPersistentAlerts} from '..';
import {Provider} from '@clayui/provider';
import {act, cleanup, renderHook} from '@testing-library/react';
import React from 'react';

const BODY_CLASS = 'c-prefers-persistent-alerts';

describe('usePrefersPersistentAlerts', () => {
	afterEach(() => {
		cleanup();
		document.body.classList.remove(BODY_CLASS);
	});

	it('returns true when the Provider sets persistentAlerts', () => {
		const {result} = renderHook(() => usePrefersPersistentAlerts(), {
			wrapper: ({children}) => (
				<Provider persistentAlerts>{children}</Provider>
			),
		});

		expect(result.current).toBe(true);
	});

	it('returns true when the body has the class and the Provider sets persistentAlerts to false', () => {
		document.body.classList.add(BODY_CLASS);

		const {result} = renderHook(() => usePrefersPersistentAlerts(), {
			wrapper: ({children}) => (
				<Provider persistentAlerts={false}>{children}</Provider>
			),
		});

		expect(result.current).toBe(true);
	});

	it('follows the body class as it is added and removed', async () => {
		const {result} = renderHook(() => usePrefersPersistentAlerts());

		expect(result.current).toBe(false);

		await act(async () => {
			document.body.classList.add(BODY_CLASS);
		});

		expect(result.current).toBe(true);

		await act(async () => {
			document.body.classList.remove(BODY_CLASS);
		});

		expect(result.current).toBe(false);
	});
});
