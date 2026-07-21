/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import {ManageMembersList} from 'frontend-js-components-web';
import React from 'react';

import AddMembersInput from '../../../../src/main/resources/META-INF/resources/js/common/components/AddMembersInput';
import {
	AddSpaceMembers,
	AddSpaceMembersProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/AddSpaceMembers';
import {SPACE_MEMBERS_CONFIG} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/spaceMembersConfig';

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	ManageMembersList: jest.fn(() => null),
}));

const mockManageMembersList = ManageMembersList as unknown as jest.Mock;

const mockLearnResources = {
	'site-cms-site-initializer': {
		'add-space-members': {
			en_US: {
				message: 'Test Message',
				url: 'https://learn.liferay.com/test-url',
			},
		},
	},
};

describe('AddSpaceMembers', () => {
	const props: AddSpaceMembersProps = {
		assetLibraryCreatorUserId: '0',
		assetLibraryId: '123',
		assetLibraryName: 'Test Space',
		baseAssetLibraryURL: '/web/cms/e/space/28632',
		externalReferenceCode: 'ERC',
		hasAssignMembersPermission: true,
		learnResources: mockLearnResources,
	};

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the section title, description and continue button', () => {
		render(<AddSpaceMembers {...props} />);

		expect(
			screen.getByRole('heading', {name: 'add-members-to-x'})
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'add-team-members-to-this-space-to-start-collaborating'
			)
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'continue-without-members'})
		).toBeInTheDocument();
	});

	it('renders the shared ManageMembersList with the space configuration', () => {
		render(<AddSpaceMembers {...props} />);

		expect(mockManageMembersList).toHaveBeenCalledTimes(1);

		const listProps = mockManageMembersList.mock.calls[0][0];

		expect(listProps.config).toBe(SPACE_MEMBERS_CONFIG);
		expect(listProps.className).toBe('c-p-4');
		expect(listProps.externalReferenceCode).toBe('ERC');
		expect(listProps.hasAssignMembersPermission).toBe(true);
		expect(listProps.ownerId).toBe('0');
		expect(listProps.emptyStateDescription).toBe(
			'add-members-to-this-space'
		);

		const api = {
			excludeMembers: [],
			onAutocompleteItemSelected: jest.fn(),
			onSelectChange: jest.fn(),
			selectValue: 'users',
		};

		expect(listProps.renderAddMembersInput(api).type).toBe(AddMembersInput);
	});

	it('flips the continue button label once members are selected', () => {
		render(<AddSpaceMembers {...props} />);

		const {onHasSelectedMembersChange} =
			mockManageMembersList.mock.calls[0][0];

		act(() => {
			onHasSelectedMembersChange(true);
		});

		expect(
			screen.getByRole('button', {name: 'continue'})
		).toBeInTheDocument();
	});
});
