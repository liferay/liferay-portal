/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {renderHook} from '@testing-library/react';
import React from 'react';

import FrontendDataSetContext from '../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import recentlyVisited from '../../src/main/resources/META-INF/resources/utils/recentlyVisited';
import {useRecordVisit} from '../../src/main/resources/META-INF/resources/utils/useRecordVisit';
import ViewsContext from '../../src/main/resources/META-INF/resources/views/ViewsContext';

const FDS_NAME = 'FDS_NAME';

const BLOGS = {href: '/blogs/1', label: 'Blogs'};

describe('useRecordVisit', () => {
	afterEach(() => {
		recentlyVisited.clear(FDS_NAME);
	});

	function renderRecordVisit({
		accessibleNameField,
		searchSuggestionsEnabled = true,
	}: {
		accessibleNameField?: string;
		searchSuggestionsEnabled?: boolean;
	} = {}) {
		const {result} = renderHook(() => useRecordVisit(), {
			wrapper: ({children}: {children: React.ReactNode}) =>
				React.createElement(
					FrontendDataSetContext.Provider,
					{value: {id: FDS_NAME, searchSuggestionsEnabled} as any},
					React.createElement(
						ViewsContext.Provider,
						{
							value: [
								{activeView: {schema: {accessibleNameField}}},
								() => {},
							] as any,
						},
						children
					)
				),
		});

		return result.current;
	}

	it('remembers the item a renderer of its own navigates to', () => {
		renderRecordVisit()(null, BLOGS);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
	});

	it('remembers nothing while the suggestions are turned off', () => {
		renderRecordVisit({searchSuggestionsEnabled: false})(null, BLOGS);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});

	it('names the item after the field the view names its rows by', () => {
		renderRecordVisit({accessibleNameField: 'headline'})(
			{headline: 'Ten Ways To Fold A Commit', title: 'Untitled'},
			BLOGS
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: BLOGS.href, label: 'Ten Ways To Fold A Commit'},
		]);
	});

	it('falls back to the text the user clicked', () => {
		renderRecordVisit()({id: 42}, BLOGS);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
	});
});
