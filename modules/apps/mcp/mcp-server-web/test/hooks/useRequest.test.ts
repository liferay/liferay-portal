/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook, waitFor} from '@testing-library/react';

import useRequest from '../../src/main/resources/META-INF/resources/js/hooks/useRequest';
import {RequestResult} from '../../src/main/resources/META-INF/resources/js/services/ApiHelper';

function deferred<T>() {
	let resolve: (result: RequestResult<T>) => void = () => {};

	const promise = new Promise<RequestResult<T>>((_resolve) => {
		resolve = _resolve;
	});

	return {promise, resolve};
}

describe('useRequest', () => {
	it('does not send the request without params', () => {
		const request = jest.fn();

		const {result} = renderHook(() => useRequest(request, null));

		expect(request).not.toHaveBeenCalled();
		expect(result.current).toEqual({
			data: null,
			error: null,
			loading: false,
		});
	});

	it('returns the data of the request', async () => {
		const request = jest.fn(() =>
			Promise.resolve({data: 'Prompt', error: null})
		);

		const {result} = renderHook(() => useRequest(request, 1));

		expect(request).toHaveBeenCalledWith(1);
		expect(result.current.loading).toBe(true);

		await waitFor(() =>
			expect(result.current).toEqual({
				data: 'Prompt',
				error: null,
				loading: false,
			})
		);
	});

	it('returns the error of the request', async () => {
		const request = jest.fn(() =>
			Promise.resolve({data: null, error: 'Prompt not found'})
		);

		const {result} = renderHook(() => useRequest(request, 1));

		await waitFor(() =>
			expect(result.current).toEqual({
				data: null,
				error: 'Prompt not found',
				loading: false,
			})
		);
	});

	it('sends the request once while the params do not change', async () => {
		const request = jest.fn(() =>
			Promise.resolve({data: 'Prompt', error: null})
		);

		const {rerender, result} = renderHook(() => useRequest(request, 1));

		await waitFor(() => expect(result.current.data).toBe('Prompt'));

		rerender();

		expect(request).toHaveBeenCalledTimes(1);
	});

	it('discards the response of a request that was replaced', async () => {
		const first = deferred<string>();
		const second = deferred<string>();

		const request = jest.fn((id: number) =>
			id === 1 ? first.promise : second.promise
		);

		const {rerender, result} = renderHook(
			({id}) => useRequest(request, id),
			{initialProps: {id: 1}}
		);

		rerender({id: 2});

		second.resolve({data: 'Second', error: null});

		await waitFor(() => expect(result.current.data).toBe('Second'));

		await act(async () => {
			first.resolve({data: 'First', error: null});

			await first.promise;
		});

		expect(result.current.data).toBe('Second');
	});
});
