/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectBox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useEffect, useState} from 'react';

import ChartContext from '../ChartContext';
import {
	addUserEmailsToAccount,
	addUserEmailsToOrganization,
	getAccountRoles,
	getOrganizationRoles,
} from '../data/users';
import {MODEL_TYPE_MAP} from '../utils/constants';

export default function InviteUserModal({closeModal, observer, parentData}) {
	const [emailsQuery, setEmailsQuery] = useState('');
	const [selectedEmails, setSelectedEmails] = useState([]);
	const {chartInstanceRef} = useContext(ChartContext);

	const [selectedRoleIds, setSelectedRoleIds] = useState([]);
	const [roles, setRoles] = useState([]);
	const [errors, setErrors] = useState([]);

	useEffect(() => {
		if (parentData) {
			const getRoles =
				parentData.type === MODEL_TYPE_MAP.organization
					? getOrganizationRoles()
					: getAccountRoles(parentData.id);

			getRoles.then(setRoles);
		}
	}, [parentData]);

	function handleSave() {
		const typedEmails = selectedEmails
			.map((email) => email.emailAddress)
			.sort();

		if (emailsQuery) {
			typedEmails.push(emailsQuery);
		}

		const inviteUser =
			parentData.type === MODEL_TYPE_MAP.organization
				? addUserEmailsToOrganization
				: addUserEmailsToAccount;

		inviteUser(parentData.id, selectedRoleIds.join(','), typedEmails)
			.then((users) => {
				const message =
					users.length === 1
						? sub(
								Liferay.Language.get('1-user-was-added-to-x'),
								parentData.name
							)
						: sub(
								Liferay.Language.get('x-users-were-added-to-x'),
								users.length,
								parentData.name
							);

				openToast({
					message,
					type: 'success',
				});

				chartInstanceRef.current.addNodes(
					users,
					MODEL_TYPE_MAP.user,
					parentData
				);

				chartInstanceRef.current.updateNodeContent({
					...parentData,
					numberOfUsers: parentData.numberOfUsers + users.length,
				});

				closeModal();
			})
			.catch((error) => {
				setErrors([error.title]);
			});
	}

	return (
		<ClayModal center observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('invite-users')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm.Group
					className={classNames(!!errors.length && 'has-error')}
				>
					<label htmlFor="inviteUsersEmailInput">
						{Liferay.Language.get('emails')}

						<ClayIcon
							className="ml-1 reference-mark"
							symbol="asterisk"
						/>
					</label>

					<ClayInput.Group>
						<ClayInput.GroupItem>
							<ClayMultiSelect
								id="inviteUsersEmailInput"
								items={selectedEmails}
								locator={{
									label: 'emailAddress',
									value: 'emailAddress',
								}}
								onChange={setEmailsQuery}
								onItemsChange={setSelectedEmails}
								placeholder={Liferay.Language.get(
									'users-emails'
								)}
								value={emailsQuery}
							/>
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ClayForm.Group>

				<ClayForm.Group
					className={classNames(!!errors.length && 'has-error')}
				>
					<label htmlFor="inviteUsersRoleInput">
						{Liferay.Language.get('roles')}

						<ClayIcon
							className="ml-1 reference-mark"
							symbol="asterisk"
						/>
					</label>

					<ClaySelectBox
						id="inviteUsersRoleInput"
						items={roles.map((role) => ({
							label: role.name,
							value: role.id,
						}))}
						multiple
						onSelectChange={setSelectedRoleIds}
						size={5}
						value={selectedRoleIds}
					/>

					<ClayInput.Group>
						<ClayInput.GroupItem>
							{!!errors.length && (
								<ClayForm.FeedbackGroup>
									{errors.map((error, i) => (
										<ClayForm.FeedbackItem key={i}>
											<ClayForm.FeedbackIndicator symbol="info-circle" />

											{error}
										</ClayForm.FeedbackItem>
									))}
								</ClayForm.FeedbackGroup>
							)}
						</ClayInput.GroupItem>
					</ClayInput.Group>
				</ClayForm.Group>
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

						<ClayButton displayType="primary" onClick={handleSave}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
