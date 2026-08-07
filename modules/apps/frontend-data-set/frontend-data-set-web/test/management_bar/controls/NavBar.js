/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import NavBar from '../../../src/main/resources/META-INF/resources/management_bar/controls/NavBar';
import ViewsContext from '../../../src/main/resources/META-INF/resources/views/ViewsContext';

jest.mock(
	'../../../src/main/resources/META-INF/resources/management_bar/controls/snapshots/SnapshotsControls',
	() => () => <div data-testid="snapshots-controls-mock" />
);

global.Liferay = {
	FeatureFlags: {},
	Language: {
		get: (key) => key,
	},
};

describe('NavBar', () => {
	const mockFDSContext = {
		globalFDSState: {filters: []},
		showInfoPanel: false,
	};

	const mockViewsContext = [{snapshotsEnabled: true, sorts: [], views: []}];

	const renderNavBar = (props = {}, filters = []) => {
		return render(
			<FrontendDataSetContext.Provider
				value={{...mockFDSContext, globalFDSState: {filters}}}
			>
				<ViewsContext.Provider value={mockViewsContext}>
					<NavBar {...props} />
				</ViewsContext.Provider>
			</FrontendDataSetContext.Provider>
		);
	};

	const FILTERS = [{id: 'status', label: 'Status', type: 'selection'}];

	it('renders SnapshotsControls component when data set manager feature flag LPS-164563 is enabled', () => {
		global.Liferay.FeatureFlags['LPS-164563'] = true;

		renderNavBar();

		expect(
			screen.queryByTestId('snapshots-controls-mock')
		).toBeInTheDocument();
	});

	it('does not render SnapshotsControls component when data set manager feature flag LPS-164563 is disabled', () => {
		global.Liferay.FeatureFlags['LPS-164563'] = false;

		renderNavBar();

		expect(
			screen.queryByTestId('snapshots-controls-mock')
		).not.toBeInTheDocument();
	});

	it('renders the filters dropdown when the data set has filters', () => {
		renderNavBar({}, FILTERS);

		expect(screen.queryByText('filter')).toBeInTheDocument();
	});

	it('does not render the filters dropdown when showFilters is false', () => {
		renderNavBar({showFilters: false}, FILTERS);

		expect(screen.queryByText('filter')).not.toBeInTheDocument();
	});
});
