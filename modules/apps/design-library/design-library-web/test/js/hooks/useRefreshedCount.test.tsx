/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, renderHook, waitFor} from '@testing-library/react';

import useRefreshedCount from '../../../src/main/resources/META-INF/resources/js/hooks/useRefreshedCount';

const getDisplayUpdatedHandler = () =>
	(Liferay.on as jest.Mock).mock.calls.find(
		([eventName]: [string]) => eventName === 'fds-display-updated'
	)[1];

describe('useRefreshedCount', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('returns the server-rendered count initially', () => {
		const {result} = renderHook(() =>
			useRefreshedCount(3, ['fds-members'], () => Promise.resolve(9))
		);

		expect(result.current).toBe(3);
	});

	it('re-fetches the count when a matching data set is refreshed', async () => {
		const fetchCount = jest.fn(() => Promise.resolve(5));

		const {result} = renderHook(() =>
			useRefreshedCount(3, ['fds-members'], fetchCount)
		);

		await act(async () => {
			getDisplayUpdatedHandler()({id: 'fds-members'});
		});

		await waitFor(() => expect(result.current).toBe(5));

		expect(fetchCount).toHaveBeenCalledTimes(1);
	});

	it('ignores refreshes for unrelated data sets', () => {
		const fetchCount = jest.fn(() => Promise.resolve(5));

		renderHook(() => useRefreshedCount(3, ['fds-members'], fetchCount));

		act(() => {
			getDisplayUpdatedHandler()({id: 'fds-assets'});
		});

		expect(fetchCount).not.toHaveBeenCalled();
	});
});
