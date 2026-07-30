/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import DesignLibraryMembersSectionHeader from '../../src/main/resources/META-INF/resources/js/DesignLibraryMembersSectionHeader';

const DEFAULT_PROPS = {
	count: 3,
	externalReferenceCode: 'erc',
	hasAssignMembersPermission: true,
	ownerId: '1',
};

describe('DesignLibraryMembersSectionHeader', () => {
	it('localizes the member count into the title', () => {
		render(<DesignLibraryMembersSectionHeader {...DEFAULT_PROPS} />);

		expect(Liferay.Util.sub).toHaveBeenCalledWith('x-z', 'members', '3');
	});

	it('shows the manage action when the user can assign members and there are members', () => {
		render(<DesignLibraryMembersSectionHeader {...DEFAULT_PROPS} />);

		expect(
			screen.getByRole('button', {name: 'manage-members'})
		).toBeInTheDocument();
	});

	it('hides the manage action for a user who cannot assign members', () => {
		render(
			<DesignLibraryMembersSectionHeader
				{...DEFAULT_PROPS}
				hasAssignMembersPermission={false}
			/>
		);

		expect(
			screen.queryByRole('button', {name: 'manage-members'})
		).not.toBeInTheDocument();
	});

	it('hides the manage action when there are no members', () => {
		render(
			<DesignLibraryMembersSectionHeader {...DEFAULT_PROPS} count={0} />
		);

		expect(
			screen.queryByRole('button', {name: 'manage-members'})
		).not.toBeInTheDocument();
	});
});
