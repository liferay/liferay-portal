/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {performUserSwitch, userData} from '../../../../utils/performLogin';
import getCompanyId from './getCompanyId';

type TRole = {
	name: string;
	rolePermissions?: Array<{
		actionIds: string[];
		primaryKey: string;
		resourceName: string;
		scope: number;
	}>;
	roleType?: number | string;
};

interface IResourcePermission {
	actions: string[];
	resourceName: string;
}

async function assignRoleToUserAccount(
	apiHelpers: DataApiHelpers,
	dataSetUserRole: any,
	userAccount: any
) {
	await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
		dataSetUserRole.id,
		Number(userAccount.id)
	);

	apiHelpers.data.push({
		id: `${dataSetUserRole.id}_${userAccount.id}`,
		type: 'roleUserAccountAssociation',
	});
}

async function createUserAccount(apiHelpers: DataApiHelpers) {
	const userAccount = await apiHelpers.headlessAdminUser.postUserAccount();

	userData[userAccount.alternateName] = {
		name: userAccount.givenName,
		password: 'test',
		surname: userAccount.familyName,
	};

	return userAccount;
}

async function createUserRole(
	apiHelpers: DataApiHelpers,
	companyId: string,
	dataSetObject: any,
	dataSetResourcePermissions: IResourcePermission[],
	dataSetUserRoleName: string
) {
	let dataSetRolePermissions: TRole['rolePermissions'] = [];

	if (dataSetResourcePermissions && dataSetResourcePermissions.length) {
		dataSetRolePermissions = dataSetResourcePermissions.map(
			(resourcePermission) => {
				if (resourcePermission.resourceName === 'Data Set') {
					return {
						actionIds: resourcePermission.actions,
						primaryKey: companyId,
						resourceName: dataSetObject.className,
						scope: 1,
					};
				}
				else if (resourcePermission.resourceName === 'Data Sets') {
					return {
						actionIds: resourcePermission.actions,
						primaryKey: companyId,
						resourceName: `com.liferay.object#${dataSetObject.id}`,
						scope: 1,
					};
				}
			}
		);
	}

	const dataSetUserRole = await apiHelpers.headlessAdminUser.postRole({
		name: dataSetUserRoleName,
		rolePermissions: [
			{
				actionIds: ['VIEW_CONTROL_PANEL'],
				primaryKey: companyId,
				resourceName: '90',
				scope: 1,
			},
			{
				actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
				primaryKey: companyId,
				resourceName:
					'com_liferay_frontend_data_set_admin_web_internal_portlet_FDSAdminPortlet',
				scope: 1,
			},
			...dataSetRolePermissions,
		],
		roleType: 'regular',
	});

	return dataSetUserRole;
}

async function getDataSetObjectInfo(apiHelpers: DataApiHelpers) {
	return await apiHelpers.objectEntry.getObjectEntryByExternalReferenceCode({
		applicationName: 'object-admin/v1.0/object-definitions',
		externalReferenceCode: 'L_DATA_SET',
	});
}

export async function setupUserRoleAndLoginAsUser({
	apiHelpers,
	dataSetResourcePermissions,
	dataSetUserRoleName,
	page,
}: {
	apiHelpers: DataApiHelpers;
	dataSetResourcePermissions?: IResourcePermission[];
	dataSetUserRoleName: string;
	page: Page;
}) {
	const companyId = await getCompanyId(page);
	const dataSetObject = await getDataSetObjectInfo(apiHelpers);
	const dataSetUserRole = await createUserRole(
		apiHelpers,
		companyId,
		dataSetObject,
		dataSetResourcePermissions,
		dataSetUserRoleName
	);
	const userAccount = await createUserAccount(apiHelpers);

	await assignRoleToUserAccount(apiHelpers, dataSetUserRole, userAccount);

	await performUserSwitch(page, userAccount.alternateName);

	return {
		dataSetUserRole,
		userAccount,
	};
}
