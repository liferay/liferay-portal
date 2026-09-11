/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import FrontendDataSetContext from '../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import LinkRenderer from '../../src/main/resources/META-INF/resources/renderers/LinkRenderer';
import recentlyVisited from '../../src/main/resources/META-INF/resources/utils/recentlyVisited';

const FDS_NAME = 'FDS_NAME';

const BLOGS = {href: '/blogs/1', label: 'Blogs'};

describe('LinkRenderer', () => {
	afterEach(() => {
		recentlyVisited.clear(FDS_NAME);
	});

	function renderLinkRenderer({searchSuggestionsEnabled = false} = {}) {
		render(
			<FrontendDataSetContext.Provider
				value={{id: FDS_NAME, searchSuggestionsEnabled} as any}
			>
				<LinkRenderer value={BLOGS} />
			</FrontendDataSetContext.Provider>
		);

		return screen.getByRole('link');
	}

	it('displays the label as a link to the item', () => {
		const link = renderLinkRenderer();

		expect(link).toHaveAttribute('href', BLOGS.href);
		expect(link).toHaveTextContent(BLOGS.label);
	});

	it('remembers the item the user navigates to', async () => {
		await userEvent.click(
			renderLinkRenderer({searchSuggestionsEnabled: true})
		);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([BLOGS]);
	});

	it('remembers the item under its own title rather than under the cell text', async () => {
		render(
			<FrontendDataSetContext.Provider
				value={{id: FDS_NAME, searchSuggestionsEnabled: true} as any}
			>
				<LinkRenderer
					itemData={{title: {en_US: 'Blogs Entry'}}}
					value={BLOGS}
				/>
			</FrontendDataSetContext.Provider>
		);

		await userEvent.click(screen.getAllByRole('link')[0]);

		expect(recentlyVisited.get(FDS_NAME)).toEqual([
			{href: BLOGS.href, label: 'Blogs Entry'},
		]);
	});

	it('remembers nothing when the Data Set keeps no search history', async () => {
		await userEvent.click(renderLinkRenderer());

		expect(recentlyVisited.get(FDS_NAME)).toEqual([]);
	});
});
