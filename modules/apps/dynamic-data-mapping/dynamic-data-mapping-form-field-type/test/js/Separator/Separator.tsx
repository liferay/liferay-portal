/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import {Separator} from '../../../src/main/resources/META-INF/resources/js';

describe('Separator', () => {
	afterEach(cleanup);

	describe('Non localizable Tooltip', () => {
		const testCases = [true, false, undefined];

		test.each(testCases)(
			'hides when editOnlyInDefaultLanguage is %p',
			(editOnlyInDefaultLanguage) => {
				render(
					<Separator
						editOnlyInDefaultLanguage={editOnlyInDefaultLanguage}
						label="separator"
						readOnly={true}
						showLabel={true}
					/>
				);

				const tooltip = screen.queryByRole('presentation');

				expect(tooltip).not.toBeInTheDocument();
			}
		);
	});
});
