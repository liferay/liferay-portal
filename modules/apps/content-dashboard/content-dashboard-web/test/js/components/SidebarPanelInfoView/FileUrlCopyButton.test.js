/* eslint-disable no-unused-vars */

/* eslint-disable no-import-assign */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import {act} from 'react-dom/test-utils';

import FileUrlCopyButton from '../../../../src/main/resources/META-INF/resources/js/components/SidebarPanelInfoView/FileUrlCopyButton';

const demoFileUrl = 'http://localhost:8080/documents/my-demo-url.txt';

describe('FileUrlCopyButton', () => {
	beforeEach(() => {
		jest.useFakeTimers();

		global.navigator.writeText = jest.fn();
		Liferay.component = jest.fn();
	});

	afterEach(() => {
		jest.runOnlyPendingTimers();
		jest.useRealTimers();

		jest.restoreAllMocks();
	});

	it('matches the snapshot', () => {
		const {asFragment} = render(<FileUrlCopyButton url={demoFileUrl} />);

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders an input and a button with the proper value and the initial UI', () => {
		const {getByDisplayValue, getByRole} = render(
			<FileUrlCopyButton url={demoFileUrl} />
		);

		expect(getByDisplayValue(demoFileUrl)).toBeInTheDocument();

		const button = getByRole('button');
		expect(button).toBeInTheDocument();

		const icon = button.getElementsByTagName('svg')[0];
		expect(icon.classList).toContain('lexicon-icon-copy');
	});

	xit('renders the proper icon after clicking the button', async () => {
		const {getByRole} = render(<FileUrlCopyButton url={demoFileUrl} />);
		const button = getByRole('button');

		fireEvent(
			button,
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		let icon = button.getElementsByTagName('svg')[0];
		expect(icon.classList).toContain('lexicon-icon-check');

		act(() => {
			jest.runAllTimers();
		});

		icon = button.getElementsByTagName('svg')[0];
		expect(icon.classList).toContain('lexicon-icon-copy');

		expect(Liferay.component).toHaveBeenCalledTimes(1);
	});
});
