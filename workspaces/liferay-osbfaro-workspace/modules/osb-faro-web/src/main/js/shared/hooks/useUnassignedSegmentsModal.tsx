import * as API from 'shared/api';
import React, {useEffect} from 'react';
import UnassignedSegmentsModal from 'shared/components/modals/unassigned-segments-modal';
import {
	ActionType,
	useUnassignedSegmentsContext,
} from 'shared/context/unassignedSegments';
import {
	fetchUpgradeModalSeen,
	updateUpgradeModalSeen,
} from 'shared/actions/preferences';
import {RootState} from 'shared/store';
import {useChannelContext} from 'shared/context/channel';
import {useDispatch, useSelector} from 'react-redux';
import {useModal} from '@clayui/modal';
import {useRequest} from 'shared/hooks/useRequest';

/**
 * Loads the unassigned segments and, the first time there are any, opens the
 * modal that assigns them to a property. The caller renders the returned
 * modal.
 */
export const useUnassignedSegmentsModal = ({
	groupId,
}: {
	groupId: string;
}): React.ReactNode => {
	const dispatch = useDispatch();

	const modal = useModal();

	const upgradeModalSeen = useSelector((state: RootState) =>
		state.getIn(['preferences', 'user', 'upgradeModalSeen', 'data'], true)
	);

	const {unassignedSegmentsDispatch} = useUnassignedSegmentsContext();

	const {channels} = useChannelContext();

	const {data, error, loading} = useRequest({
		dataSourceFn: API.individualSegment.searchUnassigned,
		variables: {
			delta: 10000,
			groupId,
		},
	});

	useEffect(() => {
		dispatch(fetchUpgradeModalSeen(groupId) as any);
	}, [dispatch, groupId]);

	useEffect(() => {
		if (!data || error) {
			return;
		}

		const {items, total} = data;

		unassignedSegmentsDispatch?.({
			payload: items,
			type: ActionType.setSegments,
		});

		if (!upgradeModalSeen && !loading && !!total && !!channels.length) {
			modal.onOpenChange(true);
		}
	}, [data, error, loading, upgradeModalSeen]);

	if (!modal.open) {
		return null;
	}

	return (
		<UnassignedSegmentsModal
			groupId={groupId}
			observer={modal.observer}
			onClose={() => {
				dispatch(
					updateUpgradeModalSeen({
						groupId,
						upgradeModalSeen: true,
					}) as any
				);

				modal.onOpenChange(false);
			}}
		/>
	);
};
