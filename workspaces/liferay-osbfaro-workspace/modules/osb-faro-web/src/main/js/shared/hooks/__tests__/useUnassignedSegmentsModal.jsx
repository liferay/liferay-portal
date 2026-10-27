import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import React from 'react';
import {
	ActionType,
	UnassignedSegmentsContext,
} from 'shared/context/unassignedSegments';
import {ChannelContext} from 'shared/context/channel';
import {mockChannelContext} from 'test/mock-channel-context';
import {Provider} from 'react-redux';
import {renderHook, waitFor} from '@testing-library/react';
import {useUnassignedSegmentsModal} from 'shared/hooks/useUnassignedSegmentsModal';

jest.unmock('react-dom');

jest.mock(
	'shared/components/modals/unassigned-segments-modal',
	() =>
		({groupId}) => <div data-testid="unassigned-modal">{groupId}</div>
);

const SEGMENTS = [{id: '1', name: 'Unassigned Segment'}];

const renderUseUnassignedSegmentsModal = (unassignedSegmentsDispatch) =>
	renderHook(() => useUnassignedSegmentsModal({groupId: '23'}), {
		wrapper: ({children}) => (
			<Provider store={mockStore()}>
				<ChannelContext.Provider value={mockChannelContext()}>
					<UnassignedSegmentsContext.Provider
						value={{
							unassignedSegments: [],
							unassignedSegmentsDispatch,
						}}
					>
						{children}
					</UnassignedSegmentsContext.Provider>
				</ChannelContext.Provider>
			</Provider>
		),
	});

describe('useUnassignedSegmentsModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		API.individualSegment.searchUnassigned.mockReturnValue(
			Promise.resolve({items: SEGMENTS, total: SEGMENTS.length})
		);
	});

	it('shares the unassigned segments through their context', async () => {
		const unassignedSegmentsDispatch = jest.fn();

		renderUseUnassignedSegmentsModal(unassignedSegmentsDispatch);

		await waitFor(() =>
			expect(unassignedSegmentsDispatch).toHaveBeenCalledWith({
				payload: SEGMENTS,
				type: ActionType.setSegments,
			})
		);
	});

	it('returns the unassigned segments modal when the user has not seen it', async () => {
		API.preferences.fetchUpgradeModalSeen.mockReturnValueOnce(
			Promise.resolve(false)
		);

		const {result} = renderUseUnassignedSegmentsModal(jest.fn());

		await waitFor(() => expect(result.current).not.toBeNull());

		expect(result.current.props.groupId).toBe('23');
	});

	it('does not return the unassigned segments modal when the user has seen it', async () => {
		API.preferences.fetchUpgradeModalSeen.mockReturnValueOnce(
			Promise.resolve(true)
		);

		const unassignedSegmentsDispatch = jest.fn();

		const {result} = renderUseUnassignedSegmentsModal(
			unassignedSegmentsDispatch
		);

		await waitFor(() =>
			expect(unassignedSegmentsDispatch).toHaveBeenCalled()
		);

		expect(result.current).toBeNull();
	});
});
