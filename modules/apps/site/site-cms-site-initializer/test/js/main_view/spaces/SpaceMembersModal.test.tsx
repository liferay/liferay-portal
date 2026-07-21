/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import {ManageMembersModal} from 'frontend-js-components-web';
import React from 'react';

import AddMembersInput from '../../../../src/main/resources/META-INF/resources/js/common/components/AddMembersInput';
import SpaceMembersModal from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceMembersModal';
import {SPACE_MEMBERS_CONFIG} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/spaceMembersConfig';

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	ManageMembersModal: jest.fn(() => null),
}));

const mockManageMembersModal = ManageMembersModal as unknown as jest.Mock;

describe('SpaceMembersModal', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the shared ManageMembersModal with the space configuration', () => {
		render(
			<SpaceMembersModal
				assetLibraryCreatorUserId="1"
				externalReferenceCode="ERC"
				filter="custom-filter"
				hasAssignMembersPermission={true}
			/>
		);

		expect(mockManageMembersModal).toHaveBeenCalledTimes(1);

		const props = mockManageMembersModal.mock.calls[0][0];

		expect(props.config).toBe(SPACE_MEMBERS_CONFIG);
		expect(props.externalReferenceCode).toBe('ERC');
		expect(props.filter).toBe('custom-filter');
		expect(props.hasAssignMembersPermission).toBe(true);
		expect(props.headerTitle).toBe('all-members');
		expect(props.emptyStateDescription).toBe('add-members-to-this-space');
		expect(props.ownerId).toBe('1');
	});

	it('wires the space add-members input through renderAddMembersInput', () => {
		render(
			<SpaceMembersModal
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
