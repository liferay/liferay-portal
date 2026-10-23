import * as API from 'shared/api';
import ConfirmationModal from 'shared/components/ConfirmationModal';
import React, {useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {sub} from 'shared/util/lang';
import {useDispatch} from 'react-redux';
import {useModal} from '@clayui/modal';

interface IDeleteSegmentsParams {
	ids: string[];
	name?: string;
	onSuccess?: () => void;
}

/**
 * Owns the shared "delete segment(s)" confirmation modal and issues the
 * delete request, so the copy, modal styling, and API call stay identical
 * between the Segments list (which can delete more than one at a time) and
 * the Segment Dashboard (which only ever deletes the current segment).
 * Page-specific follow-up (reloading a table, navigating away, clearing a
 * selection) belongs in `onSuccess`. The caller renders `deleteSegmentsModal`.
 */
export function useDeleteSegments(groupId: string) {
	const dispatch = useDispatch();
	const modal = useModal();

	const [request, setRequest] = useState<IDeleteSegmentsParams>();

	const deleteSegments = (params: IDeleteSegmentsParams) => {
		setRequest(params);

		modal.onOpenChange(true);
	};

	let deleteSegmentsModal: React.ReactNode = null;

	if (modal.open && request) {
		const isMultiple = request.ids.length > 1;

		const confirmation = isMultiple
			? Liferay.Language.get(
					'are-you-sure-you-want-to-delete-the-selected-segments'
				)
			: Liferay.Language.get(
					'are-you-sure-you-want-to-delete-this-segment'
				);

		const subtitle = isMultiple
			? Liferay.Language.get(
					'you-will-lose-all-data-related-to-these-segments.-you-will-not-be-able-to-undo-this-operation'
				)
			: Liferay.Language.get(
					'you-will-lose-all-data-related-to-this-segment.-you-will-not-be-able-to-undo-this-operation'
				);

		const title = isMultiple
			? Liferay.Language.get('delete-segments')
			: sub(Liferay.Language.get('deleting-x'), [request.name]);

		deleteSegmentsModal = (
			<ConfirmationModal
				message={
					<div>
						<div className="h4 text-secondary">{confirmation}</div>

						<p>{subtitle}</p>
					</div>
				}
				observer={modal.observer}
				onClose={() => modal.onOpenChange(false)}
				onSubmit={() =>
					API.individualSegment
						.delete({groupId, ids: request.ids})
						.then(() => {
							dispatch(
								addAlert({
									alertType: Alert.Types.Success,
									message: Liferay.Language.get(
										'the-segment-has-been-deleted'
									),
								})
							);

							request.onSuccess?.();
						})
						.catch(() => {
							dispatch(
								addAlert({
									alertType: Alert.Types.Error,
									message: Liferay.Language.get('error'),
								})
							);
						})
				}
				submitButtonDisplay="warning"
				submitMessage={Liferay.Language.get('delete')}
				title={title}
				type="warning"
			/>
		);
	}

	return {deleteSegments, deleteSegmentsModal};
}
