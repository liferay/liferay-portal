/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {TextContent} from '../../src/main/resources/META-INF/resources/ai_creator_modal/TextContent';

describe('TextContent', () => {
	it('shows the given content inside a readonly input', () => {
		render(
			<TextContent
				content="Sample content"
				portletNamespace="namespace"
			/>
		);

		const input = screen.getByLabelText('content');

		expect(input).toHaveAttribute('readonly');
		expect(input).toHaveValue('Sample content');
	});
});
