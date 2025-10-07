/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import PermissionsTable, {
	IPermissionItem,
} from '../../../common/components/forms/PermissionsTable';
import {
	DEFAULT_PERMISSIONS,
	DEFAULT_ROLES,
} from '../utils/CategorizationPermissionsUtil';

/**
 * Wrapper for PermissionsTable that includes defaults specific to the Categorization section.
 */
const CategorizationPermissionsTable = ({
	defaultPermissions = DEFAULT_PERMISSIONS,
	onChange,
	...otherProps
}: {
	defaultPermissions?: IPermissionItem[];
	onChange: Function;
}) => {
	return (
		<div data-testid="categorization-permissions-table">
			<PermissionsTable
				defaultPermissions={defaultPermissions}
				onChange={onChange}
				roles={DEFAULT_ROLES}
				{...otherProps}
			/>
		</div>
	);
};

export default CategorizationPermissionsTable;
