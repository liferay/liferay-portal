/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {ApiHelper} from '@liferay/site-cms-site-initializer';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {navigate, sessionStorage} from 'frontend-js-web';
import React from 'react';

import EditorToolbar from '../../js/components/EditorToolbar';

jest.mock('@clayui/button', () => {
	const Button = ({children, onClick, ...props}: any) => (
		<button onClick={onClick} {...props}>
			{children}
		</button>
	);

	Button.Group = ({children}: any) => <div>{children}</div>;

	return {__esModule: true, default: Button};
});

jest.mock('@clayui/form', () => ({
	ClayInput: ({...props}: any) => <input {...props} />,
}));

jest.mock('@clayui/link', () => ({
	__esModule: true,
	default: ({children, href, onClick}: any) => (
		<a href={href} onClick={onClick}>
			{children}
		</a>
	),
}));

jest.mock('@liferay/layout-js-components-web', () => ({
	isCtrlOrMeta: (event: KeyboardEvent) => event.ctrlKey || event.metaKey,
}));

jest.mock('@liferay/site-cms-site-initializer', () => {
	const Toolbar = ({children, onBackClick}: any) => (
		<div>
			<a href="/back" onClick={onBackClick}>
				back
			</a>

			{children}
		</div>
	);

	Toolbar.Item = ({children}: any) => <div>{children}</div>;

	return {
		ApiHelper: {delete: jest.fn(() => Promise.resolve({error: null}))},
		Toolbar,
	};
});

jest.mock('frontend-js-web', () => ({
	navigate: jest.fn(),
	sessionStorage: {
		TYPES: {NECESSARY: 'NECESSARY'},
		getItem: jest.fn(),
		removeItem: jest.fn(),
		setItem: jest.fn(),
	},
	sub: jest.fn((key: string, ...args: string[]) => {
		let result = key;

		args.forEach((arg, index) => {
			result = result.replace(`{${index}}`, arg);
		});

		return result;
	}),
}));

const SUCCESS_MESSAGE_KEY =
	'com.liferay.site.cmp.site.initializer.successMessage';

const renderComponent = ({
	discardURL,
	hasUpdatePermission = true,
	isNew = false,
}: {
	discardURL?: string;
	hasUpdatePermission?: boolean;
	isNew?: boolean;
} = {}) =>
	render(
		<>
			<EditorToolbar
				backURL="/back"
				discardURL={discardURL}
				groupId={0}
				hasUpdatePermission={hasUpdatePermission}
				isNew={isNew}
				title="My Project"
			/>

			<form className="lfr-main-form-container" id="formId">
				<input
					name="ObjectField_title"
					readOnly
					type="text"
					value="My Test Project"
				/>
			</form>
		</>
	);

describe('EditorToolbar', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(global as any).Liferay = {
			Browser: {
				isMac: jest.fn(() => false),
			},
			FeatureFlags: {
				'LPD-62272': false,
			},
			Language: {
				get: jest.fn((key: string) => {
					if (key === 'x-was-created-successfully') {
						return '{0} was created successfully';
					}

					if (key === 'x-was-updated-successfully') {
						return '{0} was updated successfully';
					}

					return key;
				}),
			},
		};
	});

	it('disables the save button when the user lacks update permission', () => {
		renderComponent({hasUpdatePermission: false});

		expect(screen.getByText('save')).toBeDisabled();
	});

	it('discards the draft entry and navigates back when canceling', async () => {
		renderComponent({
			discardURL: '/o/c/cmpprojects/123',
			isNew: true,
		});

		fireEvent.click(screen.getByText('cancel'));

		expect(ApiHelper.delete).toHaveBeenCalledWith('/o/c/cmpprojects/123');

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/back'));
	});

	it('discards the draft entry and navigates back when going back', async () => {
		renderComponent({
			discardURL: '/o/c/cmpprojects/123',
			isNew: true,
		});

		fireEvent.click(screen.getByText('back'));

		expect(ApiHelper.delete).toHaveBeenCalledWith('/o/c/cmpprojects/123');

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/back'));
	});

	it('discards the draft entry once when canceling twice', async () => {
		renderComponent({
			discardURL: '/o/c/cmpprojects/123',
			isNew: true,
		});

		fireEvent.click(screen.getByText('cancel'));
		fireEvent.click(screen.getByText('cancel'));

		expect(ApiHelper.delete).toHaveBeenCalledTimes(1);

		await waitFor(() => expect(navigate).toHaveBeenCalledWith('/back'));
	});

	it('does not discard anything when canceling an existing entry', () => {
		renderComponent({isNew: false});

		fireEvent.click(screen.getByText('cancel'));

		expect(ApiHelper.delete).not.toHaveBeenCalled();
	});

	it('does not publish through the keyboard shortcut when the user lacks update permission', () => {
		renderComponent({hasUpdatePermission: false});

		const form = document.querySelector(
			'.lfr-main-form-container'
		) as HTMLFormElement;

		form.submit = jest.fn();

		window.dispatchEvent(
			new KeyboardEvent('keydown', {
				altKey: true,
				ctrlKey: true,
				key: 'Enter',
			})
		);

		expect(form.submit).not.toHaveBeenCalled();
	});

	it('publishes through the keyboard shortcut when the user has update permission', () => {
		renderComponent({hasUpdatePermission: true});

		const form = document.querySelector(
			'.lfr-main-form-container'
		) as HTMLFormElement;

		form.submit = jest.fn();

		window.dispatchEvent(
			new KeyboardEvent('keydown', {
				altKey: true,
				ctrlKey: true,
				key: 'Enter',
			})
		);

		expect(form.submit).toHaveBeenCalled();
	});

	it('shows created message when saving a new entry', () => {
		renderComponent({isNew: true});

		const form = document.querySelector(
			'.lfr-main-form-container'
		) as HTMLFormElement;

		form.checkValidity = jest.fn(() => true);

		fireEvent.click(screen.getByText('save'));

		expect(sessionStorage.setItem).toHaveBeenCalledWith(
			SUCCESS_MESSAGE_KEY,
			'<strong>My Test Project</strong> was created successfully',
			'NECESSARY'
		);
	});

	it('shows updated message when saving an existing entry', () => {
		renderComponent({isNew: false});

		const form = document.querySelector(
			'.lfr-main-form-container'
		) as HTMLFormElement;

		form.checkValidity = jest.fn(() => true);

		fireEvent.click(screen.getByText('save'));

		expect(sessionStorage.setItem).toHaveBeenCalledWith(
			SUCCESS_MESSAGE_KEY,
			'<strong>My Test Project</strong> was updated successfully',
			'NECESSARY'
		);
	});
});
