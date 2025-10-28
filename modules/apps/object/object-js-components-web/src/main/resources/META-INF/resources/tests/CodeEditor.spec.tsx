/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import CodeEditor from '../components/CodeEditor/index';

describe('CodeEditor', () => {
	test('can render a custom placeholder', () => {
		const customPlaceholder = 'Custom placeholder text.';

		render(
			<CodeEditor onChange={() => {}} placeholder={customPlaceholder} />
		);

		expect(screen.getByText(customPlaceholder)).toBeInTheDocument();
	});
});
