/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import React, {useId, useState} from 'react';

import {bulkChangeWorkflowTaskTransitions} from '../../utils/api';
import {
	displayBulkStateSuccessToast,
	displayErrorToast,
} from '../../utils/toastUtil';
import {ChangeTransition} from '../../utils/types';

export default function BulkUpdateWorkflowStateCommentModalContent({
	changeTransitions,
	closeModal,
	loadData,
}: {
	changeTransitions: ChangeTransition[];
	closeModal: () => void;
	loadData: () => void;
}) {
	const [comment, setComment] = useState('');
	const [submitDisabled, setSubmitDisabled] = useState(false);

	const commentId = useId();

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		setSubmitDisabled(true);

		const {error} = await bulkChangeWorkflowTaskTransitions(
			changeTransitions.map((changeTransition) => ({
				...changeTransition,
				comment,
			}))
		);

		if (!error) {
			displayBulkStateSuccessToast(changeTransitions.length);
		}
		else {
			displayErrorToast(error);
		}

		closeModal();

		loadData();
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-a-comment')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					{Liferay.Language.get('add-a-comment-to-provide-context')}
				</p>

				<label htmlFor={commentId}>
					{Liferay.Language.get('comment')}
				</label>

				<ClayInput
					component="textarea"
					id={commentId}
					onChange={(event) => setComment(event.target.value)}
					value={comment}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={submitDisabled}
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={submitDisabled}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
