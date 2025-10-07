/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import StatusLabel from '../../../src/main/resources/META-INF/resources/js/components/status-label/StatusLabel';

describe('The WorkflowStatus should', () => {
	const INITIAL_PROPS = {
		showInstanceTracker: true,
		statusMessage: 'Status Message',
		statusStyle: 'info',
	};

	beforeEach(cleanup);

	it('render as Linked Label', () => {
		render(<StatusLabel {...INITIAL_PROPS} />);

		const hasLink = document.querySelector('a');

		expect(hasLink).toBeTruthy();

		expect(hasLink).toHaveAttribute(
			'title',
			Liferay.Language.get('track-workflow')
		);
	});

	it('render as not Linked Label', () => {
		render(<StatusLabel {...INITIAL_PROPS} showInstanceTracker={false} />);

		const hasLink = document.querySelector('a');

		expect(hasLink).not.toBeInTheDocument();
	});

	it('open a modal on link click', async () => {
		render(<StatusLabel {...INITIAL_PROPS} />);

		const link = document.querySelector('a');

		await act(async () => {
			fireEvent.click(link);
		});

		const instanceTrackerModal =
			document.querySelector('.modal-full-screen');

		expect(instanceTrackerModal).toBeInTheDocument();
	});
});
