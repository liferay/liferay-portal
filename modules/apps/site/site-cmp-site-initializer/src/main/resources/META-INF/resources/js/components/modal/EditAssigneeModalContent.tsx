/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {AssigneeValue} from '@liferay/object-dynamic-data-mapping-form-field-type';
import {displayErrorToast} from '@liferay/site-cms-site-initializer';
import React, {useState} from 'react';

import {patchTaskById} from '../../utils/api';
import {displayAssignSuccessToast} from '../../utils/toastUtil';
import {ITaskObjectEntry} from '../../utils/types';
import CustomAssignee from '../CustomAssignee';

import './../AssigneeTrigger.scss';

type Props = {
	closeModal: () => void;
	cmpTaskObjectEntryId: string;
	cmpTaskObjectEntryTitle: string;
	loadData: Function;
	onTaskUpdated?: (task: ITaskObjectEntry) => void;
	value: AssigneeValue | {} | null;
};

export default function EditAssigneeModalContent({
	closeModal,
	cmpTaskObjectEntryId,
	cmpTaskObjectEntryTitle,
	loadData,
	onTaskUpdated,
	value: initialValue,
}: Props) {
	const [value, setValue] = useState<AssigneeValue | null | {}>(initialValue);

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		const {data, error} = await patchTaskById({
			body: {assignTo: value},
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

			displayAssignSuccessToast(
				cmpTaskObjectEntryTitle,
				(value as AssigneeValue).name
			);
		}
		else {
			displayErrorToast(error);
		}
	};

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get('assign-to-...')}
			</ClayModal.Header>

			<ClayModal.Body>
				<CustomAssignee
					onChange={(value: AssigneeValue | {}) => {
						setValue(value);
					}}
					triggerClassName="form-control"
					value={value}
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
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
