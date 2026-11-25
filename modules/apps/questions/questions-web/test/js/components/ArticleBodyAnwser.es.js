/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ArticleBodyAnwser from '../../../src/main/resources/META-INF/resources/js/components/ArticleBodyAnwser.es';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';

describe('ArticleBodyAnwser', () => {
	it('renders the signature inside a single style rule', () => {
		const {container} = render(
			<ArticleBodyAnwser
				articleBody="<p>Body</p>"
				encodingFormat="html"
				id="1"
				signature='Name"; } .a { content: "'
			/>
		);

		expect(container.querySelector('style').sheet.cssRules).toHaveLength(1);
	});
});
