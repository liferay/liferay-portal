/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {SpacesNavigation} from '../../../../src/main/resources/META-INF/resources/js';
import {AssetLibrary} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces_navigation/SpacesNavigation';

type SpacesNavigationProps = {
	assetLibraries?: AssetLibrary[];
	assetLibrariesCount?: number;
	showAddButton?: boolean;
};

const renderComponent = ({
	assetLibraries = [],
	assetLibrariesCount = 5,
	showAddButton = false,
}: SpacesNavigationProps = {}) => {
	return render(

		// @ts-ignore

		<SpacesNavigation
			assetLibraries={assetLibraries}
			assetLibrariesCount={assetLibrariesCount}
			showAddButton={showAddButton}
		/>
	);
};

describe('SpacesNavigation', () => {
	it('renders "Spaces" title', () => {
		renderComponent();
		expect(screen.getByText('spaces')).toBeInTheDocument();
	});

	it('renders "Add Space" button when showAddButton is true', () => {
		renderComponent({showAddButton: true});
		expect(
			screen.getByRole('menuitem', {name: 'add-space'})
		).toBeInTheDocument();
	});

	it('renders "All Spaces (X)"', async () => {
		renderComponent();
		expect(screen.getByText('all-spaces-x')).toBeInTheDocument();
	});
});
