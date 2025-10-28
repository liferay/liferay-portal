/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {ProductPurchase} from '../../../../src/main/resources/META-INF/resources/js/components/ProductPurchase/';

describe('ProductPurchase', () => {
	it('rendering component', () => {
		const {container} = render(<ProductPurchase />);

		expect(container.querySelector('div')).toBeTruthy();
	});
});
