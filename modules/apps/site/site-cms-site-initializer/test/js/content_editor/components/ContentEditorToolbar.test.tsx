/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import {sessionStorage} from 'frontend-js-web';
import React from 'react';

import ContentEditorToolbar from '../../../../src/main/resources/META-INF/resources/js/content_editor/components/ContentEditorToolbar';

jest.mock('frontend-js-web', () => {
	const original = jest.requireActual('frontend-js-web');

	return {
		...original,
		sessionStorage: {
			TYPES: {
				NECESSARY: 'NECESSARY',
			},
			getItem: jest.fn(),
			removeItem: jest.fn(),
			setItem: jest.fn(),
		},
		sub: jest.fn((key, ...args) => {
			let result = key;

			args.forEach((arg, index) => {
				result = result.replace(`{${index}}`, arg);
			});

			return result;
		}),
	};
});

const renderComponent = (isNew = false) => {
	return render(
		<>
			<ContentEditorToolbar
				backURL=""
				defaultLanguageId="en_US"
				displayDate="2025-10-31T13:00"
				getPreviewDataURL="getPreviewDataURL"
				groupId={0}
				hasWorkflow={false}
				headerTitle="New Content edit"
				isNew={isNew}
				title="New Content"
				type="Basic Web Content"
			/>

			<form
				className="lfr-main-form-container"
				data-testid="form"
				id="formId"
			>
				<input
					name="ObjectField_title"
					readOnly
					type="text"
					value="My Test Content"
				/>
			</form>
		</>
	);
};

describe('ContentEditorToolbar', () => {
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
				get: jest.fn((key) => {
					if (key === 'x-was-created-successfully') {
						return '{0} was created successfully';
					}

					if (key === 'x-was-updated-successfully') {
						return '{0} was updated successfully';
					}

					return key;
				}),
			},
			ThemeDisplay: {
				getDefaultLanguageId: jest.fn(() => 'en_US'),
			},
			Util: {
				sub: jest.fn((key, ...args) => {
					let result = key;

					args.forEach((arg, index) => {
						result = result.replace(`{${index}}`, arg);
					});

					return result;
				}),
			},
			detach: jest.fn(),
			fire: jest.fn(),
			on: jest.fn(),
		};
	});

	it('does not render ObjectEntry_displayDate hidden input when there is no selected date', () => {
		renderComponent();

		expect(
			document.querySelector('input[name="ObjectEntry_displayDate"]')
		).not.toBeInTheDocument();
	});

	it('publishes the content pressing ctrl + alt + Enter', () => {
		renderComponent();

		const form = screen.getByTestId('form') as HTMLFormElement;

		form.checkValidity = jest.fn(() => true);

		const submitSpy = jest.fn((event: Event) => event.preventDefault());

		form.addEventListener('submit', submitSpy);

		const event = new KeyboardEvent('keydown', {
			altKey: true,
			bubbles: true,
			ctrlKey: true,
			key: 'Enter',
		});

		document.body.dispatchEvent(event);

		expect(submitSpy).toHaveBeenCalledTimes(1);

		expect(sessionStorage.setItem).toHaveBeenCalledWith(
			'com.liferay.site.cms.site.initializer.successMessage',
			'<strong>My Test Content</strong> was updated successfully',
			'NECESSARY'
		);
	});

	it('shows created message when publishing a new entry with ctrl + alt + Enter', () => {
		renderComponent(true);

		const form = screen.getByTestId('form') as HTMLFormElement;

		form.checkValidity = jest.fn(() => true);

		form.addEventListener('submit', (event: Event) =>
			event.preventDefault()
		);

		document.body.dispatchEvent(
			new KeyboardEvent('keydown', {
				altKey: true,
				bubbles: true,
				ctrlKey: true,
				key: 'Enter',
			})
		);

		expect(sessionStorage.setItem).toHaveBeenCalledWith(
			'com.liferay.site.cms.site.initializer.successMessage',
			'<strong>My Test Content</strong> was created successfully',
			'NECESSARY'
		);
	});
});
