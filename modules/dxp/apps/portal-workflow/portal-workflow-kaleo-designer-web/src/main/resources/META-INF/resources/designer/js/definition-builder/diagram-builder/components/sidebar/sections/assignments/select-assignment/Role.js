/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {DiagramBuilderContext} from '../../../../../DiagramBuilderContext';
import SidebarPanel from '../../../SidebarPanel';
import BaseRole from '../../shared-components/BaseRole';

const Role = () => {
	const {selectedItem, setSelectedItem} = useContext(DiagramBuilderContext);

	const updateSelectedItem = (role) => {
		setSelectedItem((previousValue) => ({
			...previousValue,
			data: {
				...previousValue.data,
				assignments: {
					assignmentType: ['roleId'],
					roleExternalReferenceCode: role.externalReferenceCode,
					roleId: role.id,
					roleType: role.roleType,
					sectionsData: {
						id: role.id,
						name: role.name,
						roleType: role.roleType,
					},
				},
			},
		}));
	};

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('select-role')}>
			<BaseRole
				defaultFieldValue={{
					id: selectedItem.data.assignments?.sectionsData?.id || '',
					name:
						selectedItem.data.assignments?.sectionsData?.name || '',
				}}
				inputLabel={Liferay.Language.get('role-id')}
				selectLabel={Liferay.Language.get('role')}
				updateSelectedItem={updateSelectedItem}
			/>
		</SidebarPanel>
	);
};

export default Role;
