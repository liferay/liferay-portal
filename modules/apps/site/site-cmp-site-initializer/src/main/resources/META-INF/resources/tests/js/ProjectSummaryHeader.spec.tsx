/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SpaceSummaryHeader} from '@liferay/site-cms-site-initializer';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ProjectSummaryHeader from '../../js/components/members/ProjectSummaryHeader';
import manageMembersAction from '../../js/components/props_transformer/actions/manageMembersAction';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	SpaceSummaryHeader: jest.fn(() => null),
}));

jest.mock(
	'../../js/components/props_transformer/actions/manageMembersAction',
	() => jest.fn()
);

const mockSpaceSummaryHeader = SpaceSummaryHeader as unknown as jest.Mock;

describe('ProjectSummaryHeader', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the space summary header with the project members action', () => {
		const props = {
			apiURL: '/some-url',
			label: 'View All Members',
			title: 'Members (3)',
			url: '',
		};

		render(<ProjectSummaryHeader {...props} />);

		expect(mockSpaceSummaryHeader).toHaveBeenCalledTimes(1);

		const spaceSummaryHeaderProps = mockSpaceSummaryHeader.mock.calls[0][0];

		expect(spaceSummaryHeaderProps.apiURL).toBe(props.apiURL);
		expect(spaceSummaryHeaderProps.label).toBe(props.label);
		expect(spaceSummaryHeaderProps.onOpenMembersModal).toBe(
			manageMembersAction
		);
		expect(spaceSummaryHeaderProps.title).toBe(props.title);
		expect(spaceSummaryHeaderProps.url).toBe(props.url);
	});
});
