/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {MembersPermissionSelect} from '../../src/main/resources/META-INF/resources/manage_members_modal/MembersPermissionSelect';
import {Role} from '../../src/main/resources/META-INF/resources/manage_members_modal/types';

const ROLES: Role[] = [
	{externalReferenceCode: 'L_ASSET_LIBRARY_MEMBER', id: 1, name: 'Member'},
	{externalReferenceCode: 'L_ASSET_LIBRARY_OWNER', id: 2, name: 'Owner'},
	{
		externalReferenceCode: 'L_ASSET_LIBRARY_CONTENT_REVIEWER',
		id: 3,
		name: 'Consumer',
	},
	{
		externalReferenceCode: 'L_ASSET_LIBRARY_ADMINISTRATOR',
		id: 4,
		name: 'Editor',
	},
];

describe('MembersPermissionSelect', () => {
	function renderOpened(
		props: Partial<
			React.ComponentProps<typeof MembersPermissionSelect>
		> = {}
	) {
		render(
			<MembersPermissionSelect
				defaultRoleExternalReferenceCode="L_ASSET_LIBRARY_MEMBER"
				onChange={() => {}}
				roles={ROLES}
				selectedRoles={['Member']}
				{...props}
			/>
		);

		fireEvent.click(screen.getByRole('button'));
	}

	it('renders the provided roles', () => {
		renderOpened();

		expect(screen.getByLabelText('Member')).toBeInTheDocument();
		expect(screen.getByLabelText('Editor')).toBeInTheDocument();
	});

	it('disables the default role checkbox so it cannot be unchecked', () => {
		renderOpened();

		expect(screen.getByLabelText('Member')).toBeDisabled();
		expect(screen.getByLabelText('Editor')).not.toBeDisabled();
	});

	it('labels a role with the caller-provided name for its external reference code', () => {
		renderOpened({
			roleNames: {L_ASSET_LIBRARY_ADMINISTRATOR: 'Custom Editor'},
		});

		expect(screen.getByLabelText('Custom Editor')).toBeInTheDocument();
		expect(screen.getByLabelText('Member')).toBeInTheDocument();
	});
});
