/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {displayErrorToast} from '@liferay/site-cms-site-initializer';
import moment from 'moment';
import React, {useId, useState} from 'react';

import {patchTaskById} from '../../utils/api';
import {displayDueDateSuccessToast} from '../../utils/toastUtil';
import {ITaskObjectEntry} from '../../utils/types';
import DateField, {dateConfig, getDateError, toServerDate} from '../DateField';

type Props = {
	closeModal: () => void;
	cmpTaskObjectEntryId: string;
	cmpTaskObjectEntryTitle: string;
	dueDate?: string;
	loadData: Function;
	onTaskUpdated?: (task: ITaskObjectEntry) => void;
};

export default function UpdateDueDateModalContent({
	closeModal,
	cmpTaskObjectEntryId,
	cmpTaskObjectEntryTitle,
	dueDate: initialDueDate,
	loadData,
	onTaskUpdated,
}: Props) {
	const initialValue = initialDueDate
		? moment(initialDueDate.slice(0, 10)).format(dateConfig.momentFormat)
		: '';

	const [dueDate, setDueDate] = useState<string>(initialValue);
	const [errorMessage, setErrorMessage] = useState<string>('');

	const dateFieldId = useId();

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const dateError = getDateError(dueDate, false);

		if (dateError) {
			setErrorMessage(dateError);

			return;
		}

		const {data, error} = await patchTaskById({
			body: {
				dueDate: dueDate.trim() ? toServerDate(dueDate) : '',
			},
			taskId: cmpTaskObjectEntryId,
		});

		if (!error) {
			closeModal();

			if (onTaskUpdated && data) {
				onTaskUpdated(data);
			}
			else {
				loadData();
			}

			displayDueDateSuccessToast(cmpTaskObjectEntryTitle);
		}
		else {
			displayErrorToast(error);
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get('update-due-date')}
			</ClayModal.Header>

			<ClayModal.Body>
				<DateField
					errorMessage={errorMessage}
					id={dateFieldId}
					initialValue={initialValue}
					label={Liferay.Language.get('due-date')}
					onChange={async (value: string) => {
						setErrorMessage('');

						setDueDate(value);
					}}
					required={false}
				/>
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
							{Liferay.Language.get('update')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
