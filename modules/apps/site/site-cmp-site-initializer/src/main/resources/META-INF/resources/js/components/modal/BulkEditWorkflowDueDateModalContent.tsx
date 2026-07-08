/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {IBulkActionFDSData} from '@liferay/site-cms-site-initializer';
import moment from 'moment';
import React, {useId, useState} from 'react';

import {bulkUpdateWorkflowTaskDueDate} from '../../utils/api';
import {
	displayBulkDueDateSuccessToast,
	displayErrorToast,
} from '../../utils/toastUtil';
import DateField, {dateConfig} from '../DateField';

type FDSItem = {embedded: {id: number}};

export default function BulkEditWorkflowDueDateModalContent({
	closeModal,
	loadData,
	selectedData,
}: {
	closeModal: () => void;
	loadData: () => void;
	selectedData: IBulkActionFDSData;
}) {
	const [dueDate, setDueDate] = useState('');
	const [submitDisabled, setSubmitDisabled] = useState(false);

	const dateFieldId = useId();

	const items = (selectedData.items ?? []) as FDSItem[];

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (!dueDate) {
			return;
		}

		setSubmitDisabled(true);

		// Bulk due-date update has no time picker; midnight UTC is used as
		// the default time, matching the date-only intent of the selection.

		const {error} = await bulkUpdateWorkflowTaskDueDate(
			items.map((item) => ({
				dueDate:
					moment(dueDate, dateConfig.momentFormat).format(
						'YYYY-MM-DD'
					) + 'T00:00:00.000Z',
				workflowTaskId: item.embedded.id,
			}))
		);

		if (!error) {
			displayBulkDueDateSuccessToast(items.length);

			closeModal();

			loadData();
		}
		else {
			displayErrorToast();

			setSubmitDisabled(false);
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('update-due-date')}
			</ClayModal.Header>

			<ClayModal.Body>
				<label htmlFor={dateFieldId}>
					{Liferay.Language.get('new-due-date')}
				</label>

				<DateField
					id={dateFieldId}
					onChange={(value) => setDueDate(value)}
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

						<ClayButton
							disabled={submitDisabled || !dueDate}
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
