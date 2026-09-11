import * as API from 'shared/api';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {sub} from 'shared/util/lang';
import {useDispatch} from 'react-redux';

interface IDeleteSegmentsParams {
	ids: string[];
	name?: string;
	onSuccess?: () => void;
}

/**
 * Opens the shared "delete segment(s)" confirmation modal and issues the
 * delete request, so the copy, modal styling, and API call stay identical
 * between the Segments list (which can delete more than one at a time) and
 * the Segment Dashboard (which only ever deletes the current segment).
 * Page-specific follow-up (reloading a table, navigating away, clearing a
 * selection) belongs in `onSuccess`.
 */
export function useDeleteSegments(groupId: string) {
	const dispatch = useDispatch();

	return ({ids, name, onSuccess}: IDeleteSegmentsParams) => {
		const isMultiple = ids.length > 1;

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
			: sub(Liferay.Language.get('deleting-x'), [name]);

		dispatch(
			open(modalTypes.CONFIRMATION_MODAL, {
				message: (
					<div>
						<div className="h4 text-secondary">{confirmation}</div>

						<p>{subtitle}</p>
					</div>
				),
				modalVariant: 'modal-warning',
				onClose: () => dispatch(close()),
				onSubmit: () =>
					API.individualSegment
						.delete({groupId, ids})
						.then(() => {
							dispatch(
								addAlert({
									alertType: Alert.Types.Success,
									message: Liferay.Language.get(
										'the-segment-has-been-deleted'
									),
								})
							);

							onSuccess?.();
						})
						.catch(() => {
							dispatch(
								addAlert({
									alertType: Alert.Types.Error,
									message: Liferay.Language.get('error'),
								})
							);
						}),
				submitButtonDisplay: 'warning',
				submitMessage: Liferay.Language.get('delete'),
				title,
				titleIcon: 'warning-full',
			})
		);
	};
}
