/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import {WorkflowStatus} from '../../src/main/resources/META-INF/resources/js/index';

describe('The WorkflowStatus should', () => {
	const INITIAL_PROPS = {
		id: '1',
		idLabel: 'ID',
		instanceId: '05050',
		showInstanceTracker: true,
		showStatusLabel: true,
		statusLabel: 'Status',
		statusMessage: 'Status Message',
		statusStyle: 'secondary',
		version: '1.0.0',
		versionLabel: 'version',
	};

	afterEach(cleanup);

	it('render with all valid props', () => {
		const {queryByText} = render(<WorkflowStatus {...INITIAL_PROPS} />);

		const hasStatus = queryByText('Status:');

		const hasStatusMessage = queryByText('Status Message');

		const isLabelSecondary = document.querySelector('.label-secondary');

		expect(hasStatus).toBeTruthy();

		expect(hasStatusMessage).toBeTruthy();

		expect(isLabelSecondary).toBeTruthy();
	});

	it('render without a status label', () => {
		render(<WorkflowStatus {...INITIAL_PROPS} showStatusLabel={false} />);

		const hasStatus = document.querySelector('workflow-label');

		expect(hasStatus).toBeFalsy();
	});
});
