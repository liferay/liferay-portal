/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import QuantitySelector from '../../../src/main/resources/META-INF/resources/components/quantity_selector/QuantitySelector';

const defaultProps = {
	alignment: 'top',
	allowedQuantities: null,
	disabled: false,
	max: 9999,
	min: 1,
	name: 'test-name',
	onUpdate: () => {},
	quantity: 1,
	size: 'md',
	step: 1,
};

describe('Quantity Selector', () => {
	it('must render an input when there are no allowed quantities', async () => {
		const quantitySelector = render(<QuantitySelector {...defaultProps} />);

		expect(
			quantitySelector.container.querySelector('input')
		).toBeInTheDocument();
	});

	it('must render a select box when there are allowed quantities', async () => {
		const quantitySelector = render(
			<QuantitySelector
				{...defaultProps}
				allowedQuantities={[1, 5, 10]}
			/>
		);

		expect(
			quantitySelector.container.querySelector('select')
		).toBeInTheDocument();
	});

	it("must render an input when allowed quantities is an array that doesn't contain elements", () => {
		const quantitySelector = render(
			<QuantitySelector {...defaultProps} allowedQuantities={[]} />
		);

		expect(
			quantitySelector.container.querySelector('select')
		).not.toBeInTheDocument();
	});
});
