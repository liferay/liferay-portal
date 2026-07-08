/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClaySelect} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {IBulkActionFDSData} from '@liferay/site-cms-site-initializer';
import React, {useEffect, useId, useMemo, useState} from 'react';

import {
	bulkAssignWorkflowTasks,
	getAssignableUsersForWorkflowTasks,
} from '../../utils/api';
import {
	displayBulkAssignSuccessToast,
	displayErrorToast,
} from '../../utils/toastUtil';

type Assignee = {
	id: number;
	name: string;
};

type FDSItem = {embedded: {id: number}};

export default function BulkEditWorkflowAssigneeModalContent({
	closeModal,
	loadData,
	selectedData,
}: {
	closeModal: () => void;
	loadData: () => void;
	selectedData: IBulkActionFDSData;
}) {
	const [assignableUsers, setAssignableUsers] = useState<Assignee[]>([]);
	const [selectedUserId, setSelectedUserId] = useState(0);
	const [submitDisabled, setSubmitDisabled] = useState(false);

	const items = useMemo(
		() => (selectedData.items ?? []) as FDSItem[],
		[selectedData]
	);
	const selectId = useId();

	useEffect(() => {
		getAssignableUsersForWorkflowTasks(
			items.map((item) => item.embedded.id)
		).then(({data, error}) => {
			if (error || !data) {
				displayErrorToast();

				return;
			}

			const usersByTask: Assignee[][] =
				data.workflowTaskAssignableUsers.map(
					(entry) => entry.assignableUsers ?? []
				);

			const intersectedUsers = usersByTask.reduce(
				(common, taskUsers) =>
					common.filter((user) =>
						taskUsers.some((taskUser) => taskUser.id === user.id)
					),
				usersByTask[0] ?? []
			);

			setAssignableUsers(intersectedUsers);

			if (intersectedUsers.length) {
				setSelectedUserId(intersectedUsers[0].id);
			}
		});
	}, [items]);

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (!selectedUserId) {
			return;
		}

		setSubmitDisabled(true);

		const {error} = await bulkAssignWorkflowTasks(
			items.map((item) => ({
				assigneeId: selectedUserId,
				workflowTaskId: item.embedded.id,
			}))
		);

		if (!error) {
			const assignee = assignableUsers.find(
				(user) => user.id === selectedUserId
			);

			displayBulkAssignSuccessToast(assignee?.name ?? '', items.length);

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
				{Liferay.Language.get('assign-to-...')}
			</ClayModal.Header>

			<ClayModal.Body>
				<label htmlFor={selectId}>
					{Liferay.Language.get('assign-to')}
				</label>

				<ClaySelect
					disabled={!assignableUsers.length}
					id={selectId}
					name="assigneeId"
					onChange={(event) =>
						setSelectedUserId(Number(event.target.value))
					}
					value={selectedUserId}
				>
					{assignableUsers.map((user) => (
						<ClaySelect.Option
							key={user.id}
							label={user.name}
							value={user.id}
						/>
					))}
				</ClaySelect>
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
							disabled={submitDisabled || !assignableUsers.length}
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
