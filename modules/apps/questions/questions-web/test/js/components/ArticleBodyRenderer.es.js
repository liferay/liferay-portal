/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import ArticleBodyRenderer from '../../../src/main/resources/META-INF/resources/js/components/ArticleBodyRenderer.es';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';

describe('ArticleBodyRenderer', () => {
	it('renders the signature inside a single style rule', () => {
		const {container} = render(
			<ArticleBodyRenderer
				articleBody="<p>Body</p>"
				elapsedTime="1 minute ago"
				encodingFormat="html"
				id="1"
				signature='Name"; } .a { content: "'
			/>
		);

		expect(container.querySelector('style').sheet.cssRules).toHaveLength(1);
	});
});
