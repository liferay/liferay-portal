/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {useFormik} from 'formik';
import React, {useEffect, useState} from 'react';

import {updateDueDate} from '../../../common/services/WorkflowService';

interface UpdateDueDateProps {
	closeModal: () => void;
	dueDate: string;
	loadData: () => Promise<void>;
	workflowTaskId: number;
}

export default function UpdateDueDateModalContent({
	closeModal,
	dueDate,
	loadData,
	workflowTaskId,
}: UpdateDueDateProps) {
	const [date, setDate] = useState('');
	const [time, setTime] = useState('');

	const updateDueDateCall = async (values: any) => {
		const dateObj = new Date(date);

		const dateString = dateObj.toISOString().split('T')[0];

		const res = await updateDueDate({
			comment: values.comment,
			dueDate: `${dateString}T${time}:00.000Z`,
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

	useEffect(() => {
		const date = dueDate ? new Date(dueDate) : new Date();

		setDate(date.toISOString().split('T')[0]);
		setTime(
			String(date.getUTCHours()).padStart(2, '0') +
				':' +
				String(date.getUTCMinutes()).padStart(2, '0')
		);

		return () => {
			setDate('');
			setTime('');
		};
	}, [dueDate]);

	const {handleSubmit} = useFormik({
		initialValues: {
			comment: '',
			dueDate: 0,
			workflowTaskId,
		},
		onSubmit: (values) => {
			updateDueDateCall(values);
		},
	});

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get('update-due-date')}
			</ClayModal.Header>

			<ClayModal.Body>
				<div>
					<label>{Liferay.Language.get('due-date')}</label>

					<div className="row">
						<div className="col-6">
							<input
								className="form-control"
								onChange={(event) =>
									setDate(event.target.value)
								}
								type="date"
								value={date}
							/>
						</div>

						<div className="col-6">
							<input
								className="form-control"
								onChange={(event) =>
									setTime(event.target.value)
								}
								type="time"
								value={time}
							/>
						</div>
					</div>

					<label htmlFor={`commentInput-${workflowTaskId}`}>
						{Liferay.Language.get('comment')}
					</label>

					<ClayInput
						component="textarea"
						id={`commentInput-${workflowTaskId}`}
						name="comment"
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
