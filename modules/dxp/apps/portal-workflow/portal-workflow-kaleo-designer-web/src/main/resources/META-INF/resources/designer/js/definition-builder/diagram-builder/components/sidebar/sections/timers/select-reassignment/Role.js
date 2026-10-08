/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect} from 'react';

import {retrieveRole} from '../../../../../../util/fetchUtil';
import SidebarPanel from '../../../SidebarPanel';
import BaseRole from '../../shared-components/BaseRole';

const Role = ({actionData, actionSectionsIndex, setActionSections}) => {
	const updateRole = (role) => {
		setActionSections((currentSections) => {
			const updatedSections = [...currentSections];

			updatedSections[actionSectionsIndex].assignmentType = 'roleId';
			updatedSections[actionSectionsIndex].roleExternalReferenceCode =
				role.externalReferenceCode;
			updatedSections[actionSectionsIndex].roleId = role.id;
			updatedSections[actionSectionsIndex].name = role.name;
			updatedSections[actionSectionsIndex].roleType = role.roleType;

			return updatedSections;
		});
	};

	useEffect(() => {
		if (actionData.roleId && !actionData.name) {
			retrieveRole(
				actionData.roleExternalReferenceCode,
				actionData.roleId
			)
				.then((response) => response.json())
				.then((response) => {
					setActionSections((currentSections) => {
						const updatedSections = [...currentSections];
						updatedSections[actionSectionsIndex].name =
							response.name;
						updatedSections[actionSectionsIndex].roleType =
							response.roleType;

						return updatedSections;
					});
				});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('select-role')}>
			<BaseRole
				defaultFieldValue={{
					id: actionData.roleId || '',
					name: actionData.name || '',
				}}
				inputLabel={Liferay.Language.get('role-id')}
				selectLabel={Liferay.Language.get('role')}
				updateSelectedItem={updateRole}
			/>
		</SidebarPanel>
	);
};

export default Role;
