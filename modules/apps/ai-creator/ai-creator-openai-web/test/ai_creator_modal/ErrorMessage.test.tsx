/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {ErrorMessage} from '../../src/main/resources/META-INF/resources/ai_creator_modal/ErrorMessage';

describe('ErrorMessage', () => {
	it('shows the given error message inside an alert', () => {
		render(<ErrorMessage message="Sample message" />);

		expect(screen.getByRole('alert')).toHaveTextContent('Sample message');
	});
});
