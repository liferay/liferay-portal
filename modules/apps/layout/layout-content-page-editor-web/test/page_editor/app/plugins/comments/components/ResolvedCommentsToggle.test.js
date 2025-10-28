/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {toggleShowResolvedComments} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/actions/index';
import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import ResolvedCommentsToggle from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/comments/components/ResolvedCommentsToggle';

const renderComponent = (state = {}, dispatch) => {
	const {getByLabelText} = render(
		<StoreAPIContextProvider dispatch={dispatch} getState={() => state}>
			<ResolvedCommentsToggle />
		</StoreAPIContextProvider>
	);

	return getByLabelText('show-resolved-comments');
};

const RESOLVED_COMMENTS_STATE = {
	fragmentEntryLinks: {
		'fragment-entry-link-1': {comments: [{resolved: true}]},
	},
};

describe('ResolvedCommentsToggle', () => {
	afterEach(cleanup);

	it('is unchecked by default', () => {
		expect(renderComponent().checked).toBe(false);
	});

	it('is cheched if showResolvedComments is true', () => {
		expect(renderComponent({showResolvedComments: true}).checked).toBe(
			true
		);
	});

	it('is disabled if there are no resolved comments', () => {
		expect(renderComponent()).toBeDisabled();
	});

	it('is enabled if there are some resolved comments', () => {
		expect(renderComponent(RESOLVED_COMMENTS_STATE)).not.toBeDisabled();
	});

	it('dispatches toggleShowResolvedComments on change', () => {
		const dispatch = jest.fn();
		const checkbox = renderComponent(RESOLVED_COMMENTS_STATE, dispatch);

		fireEvent.click(checkbox);

		expect(dispatch).toHaveBeenCalledWith(
			toggleShowResolvedComments({showResolvedComments: true})
		);
	});
});
