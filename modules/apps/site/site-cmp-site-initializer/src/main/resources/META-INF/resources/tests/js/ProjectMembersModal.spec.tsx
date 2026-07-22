/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AddMembersInput} from '@liferay/site-cms-site-initializer';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import {ManageMembersModal} from 'frontend-js-components-web';
import React from 'react';

import ProjectMembersModal from '../../js/components/members/ProjectMembersModal';
import {PROJECT_MEMBERS_CONFIG} from '../../js/components/members/projectMembersConfig';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	AddMembersInput: jest.fn(() => null),
}));

jest.mock('frontend-js-components-web', () => ({
	ManageMembersModal: jest.fn(() => null),
}));

const mockManageMembersModal = ManageMembersModal as unknown as jest.Mock;

describe('ProjectMembersModal', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the shared ManageMembersModal with the project configuration', () => {
		render(
			<ProjectMembersModal
				assetLibraryCreatorUserId="1"
				externalReferenceCode="ERC"
				filter="custom-filter"
				hasAssignMembersPermission={true}
			/>
		);

		expect(mockManageMembersModal).toHaveBeenCalledTimes(1);

		const props = mockManageMembersModal.mock.calls[0][0];

		expect(props.config).toBe(PROJECT_MEMBERS_CONFIG);
		expect(props.emptyStateDescription).toBe('add-members-to-this-project');
		expect(props.externalReferenceCode).toBe('ERC');
		expect(props.filter).toBe('custom-filter');
		expect(props.hasAssignMembersPermission).toBe(true);
		expect(props.headerTitle).toBe('all-members');
		expect(props.ownerId).toBe('1');
	});

	it('wires the project add-members input through renderAddMembersInput', () => {
		render(
			<ProjectMembersModal
				assetLibraryCreatorUserId="1"
				externalReferenceCode="ERC"
				hasAssignMembersPermission={true}
			/>
		);

		const {renderAddMembersInput} = mockManageMembersModal.mock.calls[0][0];

		const api = {
			excludeMembers: [],
			onAutocompleteItemSelected: jest.fn(),
			onSelectChange: jest.fn(),
			selectValue: 'users',
		};

		expect(renderAddMembersInput(api).type).toBe(AddMembersInput);
	});
});
