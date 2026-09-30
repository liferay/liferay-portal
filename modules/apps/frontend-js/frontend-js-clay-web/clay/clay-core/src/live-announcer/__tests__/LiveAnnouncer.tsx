/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {LiveAnnouncer, VisuallyHidden} from '../';

describe('LiveAnnouncer', () => {
	afterEach(cleanup);

	it('positions the announcer relative to the viewport', () => {
		render(<LiveAnnouncer />);

		expect(
			document.body.querySelector('[data-live-announcer="true"]')
		).toHaveStyle({position: 'fixed'});
	});

	it('positions other visually hidden content where it renders', () => {
		render(<VisuallyHidden>Hidden content</VisuallyHidden>);

		expect(screen.getByText('Hidden content')).toHaveStyle({
			position: 'absolute',
		});
	});
});
