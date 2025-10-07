/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CMSDefaultPermissionObjectEntryDTO} from '../../main_view/default_permission/DefaultPermissionTypes';
import {Space} from '../types/Space';
import ApiHelper from './ApiHelper';

const BASE_PATH = '/o/cms/default-permissions';

async function addObjectEntry({
	classExternalReferenceCode,
	className,
	defaultPermissions,
}: {
	classExternalReferenceCode: string;
	className: string;
	defaultPermissions: string;
}) {
	return await ApiHelper.post(`/o/cms/default-permissions`, {
		classExternalReferenceCode,
		className,
		defaultPermissions,
	});
}

async function batchUpdateObjectEntry({
	bulkActionItems,
	configuration,
	defaultPermissions,
	depotGroupId,
	permissions,
	selectAll,
	treePath,
	type = 'DefaultPermissionBulkAction',
}: {
	bulkActionItems?: Array<{
		classExternalReferenceCode: string;
		className: string;
	}>;
	configuration?: string;
	defaultPermissions?: string;
	depotGroupId?: number;
	permissions?: Array<{
		actionIds: string[];
		roleExternalReferenceCode?: string;
		roleName: string;
	}>;
	selectAll: boolean;
	treePath?: string;
	type?: string;
}) {
	return await ApiHelper.post(`/o/headless-cms/v1.0/bulk-action`, {
		bulkActionItems,
		configuration,
		defaultPermissions,
		depotGroupId,
		permissions,
		selectAll,
		treePath,
		type,
	});
}

async function getObjectEntry({
	classExternalReferenceCode,
	className,
}: {
	classExternalReferenceCode: string;
	className: string;
}): Promise<CMSDefaultPermissionObjectEntryDTO> {
	const url = `${BASE_PATH}?filter=(classExternalReferenceCode eq '${classExternalReferenceCode}') and (className eq '${className}')`;

	const {data, error} = await ApiHelper.get<{
		items: CMSDefaultPermissionObjectEntryDTO[];
		lastPage: number;
		page: number;
		totalCount: number;
	}>(url);

	if (data && data.items.length) {
		return data.items[0];
	}

	throw new Error(error || '');
}

async function getSpace(spaceId: number): Promise<Space> {
	const url = `/o/headless-asset-library/v1.0/asset-libraries/${spaceId}`;

	const {data, error} = await ApiHelper.get<Space>(url);

	if (data) {
		return data;
	}

	throw new Error(error || '');
}

async function updateObjectEntry({
	defaultPermissions,
	externalReferenceCode,
}: {
	defaultPermissions: string;
	externalReferenceCode: string;
}) {
	return await ApiHelper.patch(
		{
			defaultPermissions,
		},
		`${BASE_PATH}/by-external-reference-code/${externalReferenceCode}`
	);
}

export default {
	addObjectEntry,
	batchUpdateObjectEntry,
	getObjectEntry,
	getSpace,
	updateObjectEntry,
};
