/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {ProductPurchase} from '../../../../src/main/resources/META-INF/resources/js/components/ProductPurchase/';

describe('Header', () => {
	it('rendering Header with all props', () => {
		const {container, queryAllByAltText, queryByText, rerender} = render(
			<ProductPurchase.Header
				image="image/src"
				rightNode="right node"
				subtitle="subTitle"
				title="heading"
			>
				children
			</ProductPurchase.Header>
		);

		expect(container.querySelector('h1')).toBeTruthy();
		expect(queryAllByAltText('App Icon')).toBeTruthy();
		expect(queryByText('children')).toBeInTheDocument();
		expect(queryByText('heading')).toBeInTheDocument();
		expect(queryByText('subTitle')).toBeInTheDocument();

		rerender(
			<ProductPurchase.Header title="This Title is Longer than 30 characters making it an h3" />
		);

		expect(container.querySelector('h3')).toBeTruthy();
	});
});
