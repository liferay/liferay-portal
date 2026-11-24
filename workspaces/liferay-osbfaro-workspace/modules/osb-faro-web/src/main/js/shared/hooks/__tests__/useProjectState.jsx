import * as API from 'shared/api';
import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {fromJS} from 'immutable';
import {Provider} from 'react-redux';
import {renderHook, waitFor} from '@testing-library/react';
import {useProjectState} from 'shared/hooks/useProjectState';

const renderUseProjectState = (groupId, store = mockStore()) =>
	renderHook(() => useProjectState({groupId}), {
		wrapper: ({children}) => (
			<Provider store={store}>{children}</Provider>
		),
	});

describe('useProjectState', () => {
	it('fetches the project and returns it once loaded', async () => {
		API.projects.fetch.mockReturnValue(
			Promise.resolve(data.mockProject('99'))
		);

		const {result} = renderUseProjectState('99', mockStore(fromJS({})));

		expect(result.current.loading).toBe(true);

		await waitFor(() => expect(result.current.project).toBeTruthy());

		expect(API.projects.fetch).toHaveBeenCalledWith(
			expect.objectContaining({groupId: '99'})
		);
		expect(result.current.loading).toBe(false);
		expect(result.current.error).toBe(false);
	});

	it('returns a project already in the store without waiting', () => {
		const {result} = renderUseProjectState('23');

		expect(result.current.loading).toBe(false);
		expect(result.current.project.groupId).toBe('23');
	});

	it('reports an error when the project request fails', async () => {
		API.projects.fetch.mockReturnValue(
			Promise.reject({status: 404})
		);

		const {result} = renderUseProjectState('98', mockStore(fromJS({})));

		await waitFor(() => expect(result.current.error).toBe(true));
	});
});
