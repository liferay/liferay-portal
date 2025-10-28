/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import {
	MarketplaceContext,
	MarketplaceView,
} from '../../../../src/main/resources/META-INF/resources/js/MarketplaceContext';
import BaseModal from '../../../../src/main/resources/META-INF/resources/js/views/Modal/BaseModal';

const observer = {
	dispatch: () => null,
	mutation: [true, true] as [boolean, boolean],
};

describe('BaseModal', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	afterEach(() => {
		jest.clearAllTimers();

		cleanup();
	});

	beforeEach(() => {
		jest.useFakeTimers();
	});

	it('rendering marketplace modal', async () => {
		const {queryByRole, queryByText, rerender} = render(
			<MarketplaceContext.Provider
				value={{view: MarketplaceView.PURCHASE} as any}
			>
				<BaseModal observer={observer} open={true}>
					children
				</BaseModal>
			</MarketplaceContext.Provider>
		);

		await act(async () => {
			jest.runAllTimers();
		});

		const modalParentElement = queryByRole('dialog')?.parentElement;

		expect(modalParentElement).toHaveClass(
			'modal-dialog modal-full-screen modal-dialog-centered'
		);
		expect(queryByText('add-from-marketplace')).toBeInTheDocument();
		expect(queryByText('children')).toBeTruthy();

		rerender(
			<MarketplaceContext.Provider value={{view: 0} as any}>
				<BaseModal observer={observer} open={true}>
					children
				</BaseModal>
			</MarketplaceContext.Provider>
		);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(modalParentElement).toHaveClass('modal-full-screen');

		rerender(
			<BaseModal observer={observer} open={false}>
				children
			</BaseModal>
		);

		expect(queryByText('add-from-marketplace')).toBeFalsy();
	});
});
