/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import DesignLibraryConnectedSitesSectionHeader from '../../src/main/resources/META-INF/resources/js/DesignLibraryConnectedSitesSectionHeader';

const DEFAULT_PROPS = {
	count: 2,
	externalReferenceCode: 'erc',
	hasConnectSitesPermission: true,
};

describe('DesignLibraryConnectedSitesSectionHeader', () => {
	it('localizes the connected sites count into the title', () => {
		render(<DesignLibraryConnectedSitesSectionHeader {...DEFAULT_PROPS} />);

		expect(Liferay.Util.sub).toHaveBeenCalledWith(
			'x-x',
			'connected-sites',
			'2'
		);
	});

	it('shows the manage action when the user can connect sites and there are connected sites', () => {
		render(<DesignLibraryConnectedSitesSectionHeader {...DEFAULT_PROPS} />);

		expect(
			screen.getByRole('button', {name: 'manage-sites'})
		).toBeInTheDocument();
	});

	it('hides the manage action for a user who cannot connect sites', () => {
		render(
			<DesignLibraryConnectedSitesSectionHeader
				{...DEFAULT_PROPS}
				hasConnectSitesPermission={false}
			/>
		);

		expect(
			screen.queryByRole('button', {name: 'manage-sites'})
		).not.toBeInTheDocument();
	});

	it('hides the manage action when there are no connected sites', () => {
		render(
			<DesignLibraryConnectedSitesSectionHeader
				{...DEFAULT_PROPS}
				count={0}
			/>
		);

		expect(
			screen.queryByRole('button', {name: 'manage-sites'})
		).not.toBeInTheDocument();
	});
});
