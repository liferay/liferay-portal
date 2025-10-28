/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {Observer} from '@clayui/modal/lib/types';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import {ConnectionWithMarketplaceNeededModal} from '../../../../src/main/resources/META-INF/resources/js/views/Modal/ConnectionWithMarketplaceNeededModal';

globalThis.Liferay = {
	Language: {
		get: jest.fn((key) => key),
	},
	PortletKeys: {
		INSTANCE_SETTINGS: 'mocked-instance-settings',
	},
	Util: {
		navigate: jest.fn(),
	},
} as any;

jest.mock('frontend-js-web', () => ({
	createRenderURL: jest.fn(() => 'liferay.com/instance-settings'),
}));

const observer = {
	dispatch: () => {},
	mutation: [true, true],
} as unknown as Observer;

describe('ConnectionWithMarketplaceNeededModal', () => {
	it('rendering components with props', () => {
		const {getByText, queryByText, rerender} = render(
			<ConnectionWithMarketplaceNeededModal
				message="message"
				observer={observer}
				open={true}
			/>
		);

		expect(
			queryByText('connection-with-marketplace-needed')
		).toBeInTheDocument();
		expect(queryByText('go-to-instance-settings')).toBeInTheDocument();
		expect(queryByText('message')).toBeInTheDocument();

		fireEvent.click(getByText('go-to-instance-settings'));

		expect(globalThis.Liferay.Util.navigate).toHaveBeenCalledWith(
			'liferay.com/instance-settings'
		);

		rerender(
			<ConnectionWithMarketplaceNeededModal
				observer={observer}
				open={false}
			/>
		);

		expect(queryByText('go-to-instance-settings')).toBeFalsy();
	});
});
