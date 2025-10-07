/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {useFormik} from 'formik';
import React from 'react';

import {transitionWorkflowState} from '../../../common/services/WorkflowService';

export default function TransitionWorkflowStateModelContent({
	closeModal,
	loadData,
	transitionName,
	workflowTaskId,
}: {
	closeModal: () => void;
	loadData: () => Promise<void>;
	transitionName: string;
	workflowTaskId: number;
}) {
	const transitionWorkflowStateCall = async (values: any) => {
		const res = await transitionWorkflowState({
			comment: values.comment,
			transitionName,
			workflowTaskId,
		});

		if (res.error) {
			Liferay.Util.openToast({
				message: Liferay.Language.get(
					'your-request-failed-to-complete'
				),
				type: 'danger',
			});
		}
		else {
			Liferay.Util.openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});

			loadData();

			closeModal();
		}
	};

	const {handleChange, handleSubmit} = useFormik({
		initialValues: {
			assigneeId: 0,
			comment: '',
			workflowTaskId,
		},
		onSubmit: (values) => {
			transitionWorkflowStateCall(values);
		},
	});

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get(transitionName)}
			</ClayModal.Header>

			<ClayModal.Body>
				<div>
					<label htmlFor={`commentInput-${workflowTaskId}`}>
						{Liferay.Language.get('comment')}
					</label>

					<ClayInput
						component="textarea"
						id={`commentInput-${workflowTaskId}`}
						name="comment"
						onChange={(event) => {
							handleChange(event);
						}}
						placeholder={Liferay.Language.get('comment')}
						type="text"
					/>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" type="submit">
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
