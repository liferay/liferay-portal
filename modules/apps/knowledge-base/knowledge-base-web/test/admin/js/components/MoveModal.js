/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import MoveModal from '../../../../src/main/resources/META-INF/resources/js/admin/components/MoveModal';

describe('MoveModal', () => {
	it('finds a child article when searching', async () => {
		const items = [
			{
				classNameId: '1',
				href: 'article1_url',
				id: '1',
				name: 'Knowledge Base Article Title',
				type: 'KBArticle',
			},
			{
				children: [
					{
						classNameId: '1',
						href: 'article3_url',
						id: '3',
						name: 'apple',
						type: 'KBArticle',
					},
				],
				classNameId: '1',
				href: 'article2_url',
				id: '2',
				name: 'Knowledge Base Article Title2',
				type: 'KBArticle',
			},
		];

		render(<MoveModal items={items} moveParentKBObjectId="1" />);

		await userEvent.type(
			screen.getByPlaceholderText('search-for'),
			'apple'
		);

		const searchResults = within(screen.getByRole('list')).getAllByRole(
			'link'
		);

		expect(searchResults).toHaveLength(1);
		expect(searchResults[0]).toHaveTextContent('apple');
	});
});
