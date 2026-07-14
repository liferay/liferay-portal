/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import {sessionStorage} from 'frontend-js-web';
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
	default: ({children, href}: any) => <a href={href}>{children}</a>,
}));

jest.mock('@liferay/layout-js-components-web', () => ({
	isCtrlOrMeta: (event: KeyboardEvent) => event.ctrlKey || event.metaKey,
}));

jest.mock('@liferay/site-cms-site-initializer', () => {
	const Toolbar = ({children}: any) => <div>{children}</div>;

	Toolbar.Item = ({children}: any) => <div>{children}</div>;

	return {Toolbar};
});

jest.mock('frontend-js-web', () => ({
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

const renderComponent = (isNew = false) =>
	render(
		<>
			<EditorToolbar
				backURL="/back"
				groupId={0}
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

	it('shows updated message when saving an existing entry', () => {
		renderComponent(false);

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

	it('shows created message when saving a new entry', () => {
		renderComponent(true);

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
});
