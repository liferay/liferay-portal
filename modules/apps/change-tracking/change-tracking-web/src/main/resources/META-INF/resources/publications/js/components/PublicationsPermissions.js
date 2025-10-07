/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch, objectToFormData} from 'frontend-js-web';
import React, {useState} from 'react';

import {showNotification} from '../util/util';
import PublicationsPermissionsSearchBar from './PublicationsPermissionsSearchBar';
import PublicationsPermissionsTable from './form/PublicationsPermissionsTable';

export default function PublicationsPermissions({
	defaultPermissions,
	namespace,
	roles,
	updatePermissionsURL,
}) {
	const [filteredRoles, setFilteredRoles] = useState(roles);
	const [showModal, setShowModal] = useState(false);
	const [permissions, setPermissions] = useState(defaultPermissions);

	const {observer} = useModal({
		onClose: () => {
			Liferay.Portlet.refresh(`#p_p_id${namespace}`);
		},
	});

	const saveRolePermissions = () => {
		const permissionsMap = new Map(
			permissions.map(({actionIds, roleId}) => [roleId, actionIds])
		);

		const formData = {
			[`${namespace}permissions`]: JSON.stringify(
				Object.fromEntries(permissionsMap)
			),
		};

		fetch(updatePermissionsURL, {
			body: objectToFormData(formData),
			method: 'POST',
		})
			.then(({errorMessage}) => {
				if (errorMessage) {
					showNotification(errorMessage, true);

					return;
				}

				showNotification(
					Liferay.Language.get('your-request-completed-successfully'),
					false
				);
			})
			.catch((error) => {
				showNotification(error.message, true);
			});
	};

	const renderModal = () => {
		if (!showModal) {
			return null;
		}

		return (
			<ClayModal observer={observer} size="full-screen">
				<ClayModal.Header withTitle>
					{Liferay.Language.get('permissions')}
				</ClayModal.Header>

				<ClayModal.Body scrollable>
					<PublicationsPermissionsSearchBar
						filteredRoles={filteredRoles}
						onChangeRoles={setFilteredRoles}
						roles={roles}
					/>

					<PublicationsPermissionsTable
						defaultPermissions={defaultPermissions}
						onChange={setPermissions}
						roles={filteredRoles}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								aria-label={Liferay.Language.get('cancel')}
								displayType="secondary"
								onClick={() =>
									Liferay.Portlet.refresh(
										`#p_p_id${namespace}`
									)
								}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								aria-label={Liferay.Language.get('save')}
								onClick={() => {
									saveRolePermissions();
								}}
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayModal>
		);
	};

	return (
		<>
			<ClayButton
				aria-label={Liferay.Language.get('edit-permissions')}
				displayType="secondary"
				onClick={() => setShowModal(true)}
				title={Liferay.Language.get('edit-permissions')}
			>
				{Liferay.Language.get('edit-permissions')}
			</ClayButton>

			{renderModal()}
		</>
	);
}
