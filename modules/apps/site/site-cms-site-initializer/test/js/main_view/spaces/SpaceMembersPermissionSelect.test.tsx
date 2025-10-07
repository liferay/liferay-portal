/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {Role} from '../../../../src/main/resources/META-INF/resources/js/common/types/Role';
import {
	HIDDEN_MEMBER_ROLES,
	SPACE_MEMBER_ROLE_NAME,
	SpaceMembersPermissionSelect,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersPermissionSelect';

const mockRoles: Role[] = [
	{
		externalReferenceCode: 'space-member-external-reference-code',
		id: 100,
		name: SPACE_MEMBER_ROLE_NAME,
		name_i18n: {'en-US': 'Space Member US'},
	},
	{
		externalReferenceCode: 'role-1-external-reference-code',
		id: 101,
		name: 'Role 1',
		name_i18n: {'en-US': 'Role 1 US'},
	},
	{
		externalReferenceCode: 'role-2-external-reference-code',
		id: 102,
		name: 'Role 2',
		name_i18n: {'en-US': 'Role 2 US'},
	},
	{
		externalReferenceCode:
			'hidden-asset-library-owner-external-reference-code',
		id: 103,
		name: HIDDEN_MEMBER_ROLES[0],
		name_i18n: {'en-US': 'Asset Library Owner US'},
	},
	{
		externalReferenceCode: 'hidden-cms-consumer-external-reference-code',
		id: 104,
		name: HIDDEN_MEMBER_ROLES[1],
		name_i18n: {'en-US': 'CMS Consumer US'},
	},
];

describe('SpaceMembersPermissionSelect', () => {
	const mockOnChange = jest.fn();

	const props = {
		onChange: mockOnChange,
		roles: mockRoles,
		selectedRoles: [SPACE_MEMBER_ROLE_NAME],
	};

	afterEach(() => {
		mockOnChange.mockClear();
		jest.restoreAllMocks();
	});

	it('renders correctly and filters out hidden roles', async () => {
		render(<SpaceMembersPermissionSelect {...props} />);

		await userEvent.click(screen.getByRole('button'));

		expect(screen.getByLabelText('Space Member US')).toBeInTheDocument();
		expect(screen.getByLabelText('Role 1 US')).toBeInTheDocument();
		expect(screen.getByLabelText('Role 2 US')).toBeInTheDocument();

		expect(
			screen.queryByLabelText('Asset Library Owner US')
		).not.toBeInTheDocument();
		expect(
			screen.queryByLabelText('CMS Consumer US')
		).not.toBeInTheDocument();
	});

	it('displays the correct trigger text based on selected roles', () => {
		render(
			<SpaceMembersPermissionSelect
				{...props}
				selectedRoles={[SPACE_MEMBER_ROLE_NAME, 'Role 1']}
			/>
		);

		const triggerText = screen.getByTitle('Space Member US, Role 1 US');

		expect(triggerText).toHaveTextContent('Space Member US, Role 1 US');
	});

	it('disables the "Space Member" role checkbox', async () => {
		render(<SpaceMembersPermissionSelect {...props} />);

		await userEvent.click(screen.getByRole('button'));

		const spaceMemberCheckbox = screen.getByLabelText('Space Member US');
		expect(spaceMemberCheckbox).toBeDisabled();
	});

	it('calls onChange with the new role when a checkbox is checked', async () => {
		render(<SpaceMembersPermissionSelect {...props} />);

		await userEvent.click(screen.getByRole('button'));
		await userEvent.click(screen.getByLabelText('Role 1 US'));

		expect(mockOnChange).toHaveBeenCalledWith([
			SPACE_MEMBER_ROLE_NAME,
			'Role 1',
		]);
	});

	it('calls onChange without the role when a checkbox is unchecked', async () => {
		render(
			<SpaceMembersPermissionSelect
				{...props}
				selectedRoles={[SPACE_MEMBER_ROLE_NAME, 'Role 1']}
			/>
		);

		await userEvent.click(screen.getByRole('button'));
		await userEvent.click(screen.getByLabelText('Role 1 US'));

		expect(mockOnChange).toHaveBeenCalledWith([SPACE_MEMBER_ROLE_NAME]);
	});

	it('uses the default role name when i18n translation is not available', async () => {
		jest.spyOn(
			global.Liferay.ThemeDisplay,
			'getBCP47LanguageId'
		).mockReturnValue('fr-FR');

		render(
			<SpaceMembersPermissionSelect
				{...props}
				selectedRoles={[SPACE_MEMBER_ROLE_NAME, 'Role 1']}
			/>
		);

		const triggerText = screen.getByTitle('Asset Library Member, Role 1');
		expect(triggerText).toHaveTextContent('Asset Library Member, Role 1');

		await userEvent.click(screen.getByRole('button'));
		expect(
			screen.getByLabelText('Asset Library Member')
		).toBeInTheDocument();
		expect(screen.getByLabelText('Role 1')).toBeInTheDocument();
	});
});
