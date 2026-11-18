/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ProductSticker from '../../../src/main/resources/META-INF/resources/js/cell_renderers/ProductSticker';

describe('ProductSticker', () => {
	it('renders the image when there is one', () => {
		const {container} = render(<ProductSticker imageURL="/image.png" />);

		expect(container.querySelector('img')).toHaveAttribute(
			'src',
			'/image.png'
		);
		expect(container.querySelector('.lexicon-icon-product')).toBeNull();
	});

	it('renders the product icon when there is no image', () => {
		const {container} = render(<ProductSticker />);

		expect(container.querySelector('img')).toBeNull();
		expect(
			container.querySelector('.lexicon-icon-product')
		).toBeInTheDocument();
	});
});
