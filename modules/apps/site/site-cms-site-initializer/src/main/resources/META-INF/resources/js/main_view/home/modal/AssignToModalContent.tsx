/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput, ClaySelect} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {useFormik} from 'formik';
import React, {useEffect, useState} from 'react';

import {
	assignToMe,
	assignToUser,
	getAssignableUsers,
} from '../../../common/services/WorkflowService';
import {AssignableUser} from '../../../common/types/AssignableUser';

export default function AssignToModalContent({
	assignable,
	closeModal,
	loadData,
	workflowTaskId,
}: {
	assignable: boolean;
	closeModal: () => void;
	loadData: () => Promise<void>;
	workflowTaskId: number;
}) {
	const [hasAssignableUsers, setHasAssignableUsers] =
		useState<boolean>(false);

	const [assignableUsers, setAssignableUsers] = useState<AssignableUser[]>(
		[]
	);

	useEffect(() => {
		if (assignable) {
			const fetchAssignableUsers = async () => {
				const res = await getAssignableUsers(workflowTaskId);
				if (res.length) {
					setAssignableUsers(res);
				}
				else {
					setAssignableUsers([]);
				}
				setHasAssignableUsers(!!res.length);
			};

			fetchAssignableUsers();
		}

		return () => {
			setAssignableUsers([]);
			setHasAssignableUsers(false);
		};
	}, [assignable, workflowTaskId]);

	const assignTo = async (values: any) => {
		const res = assignable
			? await assignToUser({
					assigneeId: values.assigneeId,
					comment: values.comment,
					workflowTaskId,
				})
			: await assignToMe({
					comment: values.comment,
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
			assignable: false,
			assigneeId: 0,
			comment: '',
			workflowTaskId,
		},
		onSubmit: (values) => {
			assignTo(values);
		},
	});

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get('assign-to-...')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div>
					{assignable && (
						<div>
							<label htmlFor={`assigneeInput-${workflowTaskId}`}>
								{Liferay.Language.get('assign-to')}
							</label>

							<ClaySelect
								disabled={!hasAssignableUsers}
								id={`assigneeInput-${workflowTaskId}`}
								name="assigneeId"
								onChange={(event) => {
									handleChange(event);
								}}
								value=""
							>
								{assignableUsers?.map((user) => (
									<ClaySelect.Option
										key={user.id}
										label={
											hasAssignableUsers ? user.name : ''
										}
										value={user.id}
									/>
								))}
							</ClaySelect>
						</div>
					)}

					<label htmlFor={`commentInput-${workflowTaskId}`}>
						{Liferay.Language.get('comment')}
					</label>

					<ClayInput
						component="textarea"
						disabled={assignable && !hasAssignableUsers}
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
