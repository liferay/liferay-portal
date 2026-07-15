/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ContentsMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/ContentsMessageBalloon';

describe('ContentsMessageBalloon', () => {
	it('does not list links that are not content edit pages', () => {
		render(
			<ContentsMessageBalloon message="Here is the [documentation](https://liferay.com/docs)." />
		);

		expect(screen.queryByRole('link', {name: 'documentation'})).toBeNull();
		expect(screen.queryByText('draft')).toBeNull();
	});

	it('renders each content edit link from the markdown message as a draft', () => {
		render(
			<ContentsMessageBalloon
				message={
					'I created these contents for you:\n\n' +
					'- [Travelling around Japan](/cms/edit_content_item/1)\n' +
					'- [North Japan](/cms/edit_content_item/2)'
				}
			/>
		);

		expect(
			screen.getByRole('link', {name: 'Travelling around Japan'})
		).toHaveAttribute('href', '/cms/edit_content_item/1');
		expect(screen.getByRole('link', {name: 'North Japan'})).toHaveAttribute(
			'href',
			'/cms/edit_content_item/2'
		);
		expect(screen.getAllByText('draft')).toHaveLength(2);
	});
});
