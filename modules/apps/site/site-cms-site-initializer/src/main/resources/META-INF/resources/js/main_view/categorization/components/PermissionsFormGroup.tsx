/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayPanel from '@clayui/panel';
import React from 'react';

import CategorizationPermissionsTable from './CategorizationPermissionsTable';

function PermissionsFormGroup({onChange, ...props}: {onChange: Function}) {
	return (
		<ClayPanel
			aria-label="permissions"
			className="mb-4"
			collapsable={false}
			displayType="secondary"
			role="group"
		>
			<ClayForm.Group
				className="c-gap-4 d-flex flex-column p-4"
				data-testid="categorization-permissions-form-group"
			>
				<div className="d-flex">
					<h2 className="mb-0 py-2 text-6 text-dark">
						{Liferay.Language.get('permissions')}
					</h2>
				</div>

				<CategorizationPermissionsTable
					onChange={onChange}
					{...props}
				/>
			</ClayForm.Group>
		</ClayPanel>
	);
}

export default PermissionsFormGroup;
