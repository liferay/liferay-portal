import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import {fromJS} from 'immutable';
import {Provider} from 'react-redux';
import {renderHook, waitFor} from '@testing-library/react';
import {useDefaultChannelId} from 'shared/hooks/useDefaultChannelId';

const renderUseDefaultChannelId = (store = mockStore(fromJS({}))) =>
	renderHook(() => useDefaultChannelId({groupId: '23'}), {
		wrapper: ({children}) => (
			<Provider store={store}>{children}</Provider>
		),
	});

describe('useDefaultChannelId', () => {
	it('fetches the default channel of the current user', async () => {
		API.preferences.fetchDefaultChannelId.mockReturnValueOnce(
			Promise.resolve({defaultChannelId: '456'})
		);

		const {result} = renderUseDefaultChannelId();

		expect(result.current.loading).toBe(true);

		await waitFor(() =>
			expect(result.current.defaultChannelId).toBe('456')
		);

		expect(API.preferences.fetchDefaultChannelId).toHaveBeenCalledWith(
			expect.objectContaining({groupId: '23'})
		);
		expect(result.current.loading).toBe(false);
	});

	it('stops loading when the request fails', async () => {
		API.preferences.fetchDefaultChannelId.mockReturnValueOnce(
			Promise.reject({})
		);

		const {result} = renderUseDefaultChannelId();

		await waitFor(() => expect(result.current.error).toBe(true));

		expect(result.current.loading).toBe(false);
	});
});
