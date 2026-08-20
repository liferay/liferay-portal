/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {
	IBulkActionFDSData,
	IBulkActionTaskStarterDTO,
	triggerAssetBulkAction,
} from '@liferay/site-cms-site-initializer';
import React, {useCallback, useId, useState} from 'react';

import {displayErrorToast} from '../../utils/toastUtil';
import DateField, {getDateError, toServerDate} from '../DateField';

export default function BulkEditDueDateModalContent({
	apiURL,
	closeModal,
	dataSetId,
	selectedData,
}: {
	apiURL?: string;
	closeModal: () => void;
	dataSetId: string;
	selectedData: IBulkActionFDSData;
}) {
	const [dueDate, setDueDate] = useState<string>('');
	const [errorMessage, setErrorMessage] = useState<string>('');
	const [submitDisabled, setSubmitDisabled] = useState<boolean>(false);

	const dateFieldId = useId();

	const doBulkSubmit = useCallback(async () => {
		const dateError = getDateError(dueDate, true);

		if (dateError) {
			setErrorMessage(dateError);

			return;
		}

		const formattedDate = toServerDate(dueDate);

		setSubmitDisabled(true);

		triggerAssetBulkAction({
			apiURL,
			dataSetId,
			keyValues: {dueDate: formattedDate},
			onCreateError: ({error}) => {
				setSubmitDisabled(false);

				displayErrorToast(error as string);
			},
			onCreateSuccess: ({error = ''}) => {
				if (error) {
					setSubmitDisabled(false);

					displayErrorToast(error as string);

					return;
				}

				closeModal();
			},
			overrideDefaultErrorToast: true,
			selectedData,
			type: 'DueDateObjectBulkSelectionAction',
		} as IBulkActionTaskStarterDTO<'DueDateObjectBulkSelectionAction'>);
	}, [
		apiURL,
		closeModal,
		dataSetId,
		dueDate,
		selectedData,
		setSubmitDisabled,
	]);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('update-due-date')}
			</ClayModal.Header>

			<ClayModal.Body>
				<DateField
					errorMessage={errorMessage}
					id={dateFieldId}
					label={Liferay.Language.get('new-due-date')}
					onChange={async (value: string) => {
						setErrorMessage('');

						setDueDate(value);
					}}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={submitDisabled}
							displayType="primary"
							onClick={doBulkSubmit}
							type="button"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
