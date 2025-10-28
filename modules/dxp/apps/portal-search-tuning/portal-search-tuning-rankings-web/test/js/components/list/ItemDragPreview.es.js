/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ItemDragPreview from '../../../../src/main/resources/META-INF/resources/js/components/list/ItemDragPreview.es';
import {mockDocument} from '../../mocks/data.es';

import '@testing-library/jest-dom';

// Mock pinned document since only pinned results can be dragged.

const MOCK_DOCUMENT = mockDocument(1, {pinned: true});

/**
 * Tests a text string if the value is displayed in the component.
 * @param {string} text The text to test.
 */
function testText(text) {
	const {getByText} = render(<ItemDragPreview {...MOCK_DOCUMENT} />);

	expect(getByText(text, {exact: false})).toBeInTheDocument();
}

describe('ItemDragPreview', () => {
	it.each(['title', 'description', 'author', 'clicks', 'date'])(
		'displays the %s',
		(element) => {
			testText(`${MOCK_DOCUMENT[element]}`);
		}
	);

	it('displays the drag handle', () => {
		const {getByTestId} = render(<ItemDragPreview {...MOCK_DOCUMENT} />);

		expect(getByTestId('DRAG_ICON')).toBeVisible();
	});
});
