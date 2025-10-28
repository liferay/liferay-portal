/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import TemplatesPanel from '../../../../src/main/resources/META-INF/resources/js/admin/components/TemplatesPanel';

const EMPTY_STATE_TEXT = 'there-are-no-article-templates';

describe('Templates Panel', () => {
	beforeEach(() => {
		cleanup();
	});

	it('renders two navigation items', () => {
		const items = [
			{
				href: 'template1_url',
				id: '1',
				name: 'Template 1',
				type: 'template',
			},
			{
				href: 'template2_url',
				id: '2',
				name: 'Template 2',
				type: 'template',
			},
		];

		const {getAllByRole} = render(<TemplatesPanel items={items} />);

		expect(getAllByRole('link').length).toBe(2);
	});

	it('renders empty message if there are no items', () => {
		const {getAllByRole, getByText} = render(<TemplatesPanel />);

		expect(getAllByRole('presentation').length).toBe(2);
		expect(getByText(EMPTY_STATE_TEXT)).toBeInTheDocument();
	});
});
