/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	act,
	cleanup,
	fireEvent,
	getByRole,
	render,
	waitFor,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import ReactDOM from 'react-dom';

import {Modal} from '../../../../src/main/resources/META-INF/resources/js/components/share-form/openShareFormModal.es';

import '@testing-library/jest-dom';

global.ResizeObserver = require('resize-observer-polyfill');

const props = {
	autocompleteUserURL: 'https://liferay.com/admin/autocomplete_user',
	localizedName: {
		en_US: 'Created Form',
	},
	portletNamespace: 'portletNamespace_',
	shareFormInstanceURL: 'https://liferay.com/admin/share_form_instance',
	spritemap: 'spritemap',
	url: 'https://liferay.com/publish/url',
};

const renderShareFormModal = ({onClose}) => {
	return render(<Modal {...props} onClose={onClose} />);
};

describe('ShareFormModal', () => {
	beforeAll(() => {
		ReactDOM.createPortal = jest.fn((element) => {
			return element;
		});
		jest.useFakeTimers();
	});

	beforeEach(() => {
		fetch.mockResponse(JSON.stringify([]));
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	afterEach(() => {
		jest.clearAllTimers();
		jest.restoreAllMocks();
		cleanup();
	});

	it('renders', async () => {
		const {getByText} = renderShareFormModal({onClose: jest.fn()});

		act(() => {
			jest.runAllTimers();
		});

		expect(getByText('share')).toBeInTheDocument();
		expect(getByText('done')).toBeInTheDocument();
		expect(getByText('cancel')).toBeInTheDocument();
	});

	it(`doesn't allow users to enter invalid email addresses`, async () => {
		const {getByRole, getByText, queryByText} = renderShareFormModal({
			onClose: jest.fn(),
		});

		act(() => {
			jest.runAllTimers();
		});

		const addresses = getByRole('combobox');

		// Enters the invalid email format

		fireEvent.change(addresses, {
			target: {value: 'testWithoutEmailFormat'},
		});

		fireEvent.keyDown(addresses, {
			key: ',',
		});

		const newMultiSelectValue = queryByText('testWithoutEmailFormat');

		// Checks if the invalid value was added to the multi-select.

		expect(newMultiSelectValue).toBeNull();

		fireEvent.change(addresses, {
			target: {value: 'correctemail@liferay.com'},
		});

		fireEvent.keyDown(addresses, {
			key: ',',
		});

		expect(getByText('correctemail@liferay.com')).toBeInTheDocument();
	});

	it('calls onClose callback when hitting the cancel button on Share Modal', async () => {
		const onCloseStub = jest.fn();

		const {getByText} = renderShareFormModal({onClose: onCloseStub});

		act(() => {
			jest.runAllTimers();
		});

		userEvent.click(getByText('cancel'));

		act(() => {
			jest.runAllTimers();
		});

		expect(onCloseStub).toHaveBeenCalled();
	});

	it.skip('when rendered, fetch email addresses and names for emails autocomplete', async () => {
		renderShareFormModal({onClose: jest.fn()});

		act(() => {
			jest.runAllTimers();
		});

		await waitFor(() => expect(fetch).toHaveBeenCalled());

		act(() => {
			jest.runAllTimers();
		});

		expect(fetch.mock.calls[0][0]).toBe(props.autocompleteUserURL);
	});

	it.skip('when filling the email addresses and creating a subject, sends the email with the proper payload and endpoint', async () => {
		fetch.mockResponse(JSON.stringify([]));

		const CUSTOM_MESSAGE = 'my custom message';
		const CUSTOM_SUBJECT = 'my custom subject';

		const {getAllByRole, getByText} = renderShareFormModal({
			onClose: jest.fn(),
		});

		act(() => {
			jest.runAllTimers();
		});

		const [, addresses, subject, message] = getAllByRole('textbox');

		userEvent.type(subject, CUSTOM_SUBJECT);
		userEvent.type(message, CUSTOM_MESSAGE);

		fireEvent.change(addresses, {
			target: {value: 'test2@liferay.com'},
		});

		fireEvent.keyDown(addresses, {
			key: ',',
		});

		expect(getByText('test2@liferay.com')).toBeInTheDocument();

		const doneButton = getByText('done');

		expect(doneButton).toBeInTheDocument();

		userEvent.click(doneButton);

		act(() => {
			jest.runAllTimers();
		});

		const spy = jest.spyOn(window, 'fetch');

		expect(spy).toHaveBeenCalled();

		const request = spy.mock.calls[1][1].body;

		expect(request.get(`${props.portletNamespace}message`)).toBe(
			CUSTOM_MESSAGE
		);
		expect(request.get(`${props.portletNamespace}subject`)).toBe(
			CUSTOM_SUBJECT
		);
	});

	it('copies the form URL when clicking on the copy button', async () => {
		document.execCommand = jest.fn();

		const {getByText} = renderShareFormModal({onClose: jest.fn()});

		act(() => {
			jest.runAllTimers();
		});

		const linkSection = getByText('link').parentElement;

		const copyButton = getByRole(linkSection, 'button', {name: /copy/i});

		userEvent.click(copyButton);

		act(() => {
			jest.runAllTimers();
		});

		expect(document.execCommand).toHaveBeenCalled();

		expect(document.execCommand).toHaveBeenCalledWith('copy');
	});
});
