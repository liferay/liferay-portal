/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup} from '@testing-library/react';
import React from 'react';
import {Route} from 'react-router-dom';

import Tags from '../../../../src/main/resources/META-INF/resources/js/pages/tags/Tags.es';
import {renderComponent} from '../../../helpers.es';

import '@testing-library/jest-dom';

const mockTags = {
	data: {
		keywordsRanked: {
			items: [
				{
					actions: {
						subscribe: {
							operation: 'updateKeywordSubscribe',
							type: 'mutation',
						},
					},
					id: 37018,
					keywordUsageCount: 1,
					name: 'new',
				},
				{
					actions: {
						subscribe: {
							operation: 'updateKeywordSubscribe',
							type: 'mutation',
						},
					},
					id: 37019,
					keywordUsageCount: 1,
					name: 'osgi',
				},
				{
					actions: {
						subscribe: {
							operation: 'updateKeywordSubscribe',
							type: 'mutation',
						},
					},
					id: 37020,
					keywordUsageCount: 1,
					name: 'tag',
				},
			],
			lastPage: 1,
			page: 1,
			pageSize: 20,
			totalCount: 3,
		},
	},
};

describe('Tags', () => {
	afterEach(() => {
		jest.clearAllMocks();
		cleanup();
	});

	it('Shows list of tags', async () => {
		const route = '/tags';

		global.fetch.mockImplementation(() =>
			Promise.resolve({
				json: () => Promise.resolve(mockTags),
				ok: true,
				text: () => Promise.resolve(JSON.stringify(mockTags)),
			})
		);

		const {findByText} = renderComponent({
			contextValue: {siteKey: '20020'},
			fetch,
			route,
			ui: <Route component={Tags} />,
		});

		const firstTag = await findByText('new');
		const secondTag = await findByText('osgi');
		const thirdTag = await findByText('tag');

		expect(firstTag).toBeInTheDocument();
		expect(secondTag).toBeInTheDocument();
		expect(thirdTag).toBeInTheDocument();
	});
});
