/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {Observer} from '@clayui/modal/lib/types';
import {fireEvent, render} from '@testing-library/react';
import React, {ReactNode} from 'react';

import {MarketplaceContext} from '../../../../src/main/resources/META-INF/resources/js/MarketplaceContext';
import {MarketplaceModal} from '../../../../src/main/resources/META-INF/resources/js/views/Modal';

const observer = {
	dispatch: () => null,
	mutation: [true, true],
} as Observer;

const onClick = jest.fn().mockImplementation(jest.fn());
const onOpenChange = jest.fn();

const reactTrigger = <button onClick={() => onClick}>trigger</button>;

type ContextWrapperProps = {
	children: ReactNode;
	value: any;
};

const ContextWrapper: React.FC<ContextWrapperProps> = ({children, value}) => (
	<MarketplaceContext.Provider value={value}>
		{children}
	</MarketplaceContext.Provider>
);

describe('MarketplaceModal', () => {
	it('render component without connection to marketplace', () => {
		const {getByText, queryByText} = render(
			<MarketplaceModal
				noConnectionMessage="you have no connection"
				trigger={reactTrigger}
			>
				children
			</MarketplaceModal>,
			{
				wrapper: ({children}) => (
					<ContextWrapper
						value={{
							marketplaceConfiguration: {
								authorized: true,
								loading: false,
							},
							modal: {observer, onOpenChange, open: true},
						}}
					>
						{children}
					</ContextWrapper>
				),
			}
		);

		fireEvent.click(getByText('trigger'));

		expect(onOpenChange).toHaveBeenCalled();
		expect(queryByText('children')).toBeInTheDocument();
	});

	it('render component with connection to marketplace', () => {
		const {queryByText} = render(
			<MarketplaceModal trigger={reactTrigger}>
				children
			</MarketplaceModal>,

			{
				wrapper: ({children}) => (
					<ContextWrapper
						value={{
							marketplaceConfiguration: {
								authorized: true,
								loading: false,
							},
							modal: {observer, onOpenChange, open: false},
						}}
					>
						{children}
					</ContextWrapper>
				),
			}
		);

		expect(queryByText('children')).toBeFalsy();
	});

	it('render component while loading', () => {
		const emptyMockTrigger = <p></p>;

		const {container} = render(
			<MarketplaceModal trigger={emptyMockTrigger}>
				children
			</MarketplaceModal>,

			{
				wrapper: ({children}) => (
					<ContextWrapper
						value={{
							marketplaceConfiguration: {
								loading: true,
							},
							modal: {observer, onOpenChange, open: false},
						}}
					>
						{children}
					</ContextWrapper>
				),
			}
		);

		expect(container.querySelectorAll('div')).toHaveLength(0);
	});
});
