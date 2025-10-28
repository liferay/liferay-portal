/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import SuggestionsPanel from '../../../../src/main/resources/META-INF/resources/js/admin/components/SuggestionsPanel';

const EMPTY_STATE_TEXT = 'there-are-no-suggestions';

describe('Suggestions Panel', () => {
	beforeEach(() => {
		cleanup();
	});

	it('renders empty message if there are no items', () => {
		const {getAllByRole, getByText} = render(<SuggestionsPanel />);

		expect(getAllByRole('presentation').length).toBe(2);
		expect(getByText(EMPTY_STATE_TEXT)).toBeInTheDocument();
	});
});
