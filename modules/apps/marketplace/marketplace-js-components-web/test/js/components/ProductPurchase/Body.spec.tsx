/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import {ProductPurchase} from '../../../../src/main/resources/META-INF/resources/js/components/ProductPurchase';

describe('Body', () => {
	const primaryButtonProps = {
		children: 'Primary Button',
		onClick: jest.fn(),
	};

	const secondButtonProps = {
		children: 'Secondary Button',
		onClick: jest.fn(),
	};

	it('render product purchase body with props', () => {
		const {container, queryByText, rerender} = render(
			<ProductPurchase.Body title="header">children</ProductPurchase.Body>
		);

		expect(container.querySelectorAll('button')).toHaveLength(0);
		expect(queryByText('children')).toBeInTheDocument();
		expect(queryByText('header')).toBeInTheDocument();

		rerender(
			<ProductPurchase.Body
				primaryButtonProps={primaryButtonProps}
				title="Header"
			>
				children
			</ProductPurchase.Body>
		);

		expect(container.querySelectorAll('button')).toHaveLength(1);

		rerender(
			<ProductPurchase.Body
				secondaryButtonProps={secondButtonProps}
				title="Header"
			>
				children
			</ProductPurchase.Body>
		);

		expect(container.querySelectorAll('button')).toHaveLength(1);

		rerender(
			<ProductPurchase.Body
				primaryButtonProps={primaryButtonProps}
				secondaryButtonProps={secondButtonProps}
				title="Header"
			>
				children
			</ProductPurchase.Body>
		);

		expect(container.querySelectorAll('button')).toHaveLength(2);
	});

	it('can click rendered buttons', () => {
		const {getByText} = render(
			<ProductPurchase.Body
				primaryButtonProps={primaryButtonProps}
				secondaryButtonProps={secondButtonProps}
				title="Header"
			>
				children
			</ProductPurchase.Body>
		);

		const primaryButton = getByText('Primary Button');
		const secondaryButton = getByText('Secondary Button');

		fireEvent.click(primaryButton);
		fireEvent.click(secondaryButton);

		expect(primaryButtonProps.onClick).toHaveBeenCalledTimes(1);
		expect(secondButtonProps.onClick).toHaveBeenCalledTimes(1);
	});
});
