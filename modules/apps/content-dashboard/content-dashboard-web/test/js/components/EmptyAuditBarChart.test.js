/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import EmptyAuditBarChart from '../../../src/main/resources/META-INF/resources/js/components/AuditGraphApp/EmptyAuditBarChart';

import '@testing-library/jest-dom';

const LEARN_HOW_URL =
	'https://help.liferay.com/hc/en-us/articles/360028820492-Defining-Categories-for-Content';

describe('EmptyAuditBarChart', () => {
	Liferay.Util.sub.mockImplementation((langKey) => langKey);
	const {ResizeObserver} = window;

	beforeAll(() => {
		delete window.ResizeObserver;
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		cleanup();
		window.ResizeObserver = ResizeObserver;
		jest.restoreAllMocks();
	});

	it('renders empty bar chart if there is no content labeled with categories', () => {
		const {getByText} = render(
			<EmptyAuditBarChart
				learnHowLink={{
					message: 'Learn how to tailor categories to your needs.',
					url: LEARN_HOW_URL,
				}}
			/>
		);

		expect(getByText('there-is-no-data')).toBeInTheDocument();
		expect(
			getByText('Learn how to tailor categories to your needs.')
		).toBeInTheDocument();
	});
});
