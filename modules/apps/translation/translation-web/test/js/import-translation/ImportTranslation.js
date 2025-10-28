/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render, within} from '@testing-library/react';
import React from 'react';

import ImportTranslation from '../../../src/main/resources/META-INF/resources/js/import-translation/ImportTranslation';

const baseProps = {
	errorMessage: '',
	portletNamespace: '_mock_TranslationPortlet_',
	portletResource: 'portletResource',
	publishButtonLabel: 'Publish',
	redirect: 'http://redirect-url',
	saveButtonLabel: 'Save',
	title: '',
	workflowActionPublish: 1,
	workflowActionSaveDraft: 2,
	workflowPending: false,
};

const files = [...new Array(5)].map(
	(_, i) => new File(['(っ◕‿◕)っ'], `translated_content_${i}.xlf`)
);

const renderComponent = (props) => render(<ImportTranslation {...props} />);

describe('ImportTranslation', () => {
	afterEach(cleanup);

	describe('renders (default)', () => {
		it('publish and save buttons are disabled', () => {
			const {getByRole} = renderComponent(baseProps);

			expect(
				getByRole('button', {
					name: baseProps.publishButtonLabel,
				})
			).toBeDisabled();

			expect(
				getByRole('button', {
					name: baseProps.saveButtonLabel,
				})
			).toBeDisabled();
		});

		it('select file button is enabled and remove files button is not rendered', () => {
			const {getByRole, queryByRole} = renderComponent(baseProps);

			expect(
				getByRole('button', {
					name: 'select-files',
				})
			).toBeEnabled();

			expect(
				queryByRole('button', {
					name: 'remove-files',
				})
			).not.toBeInTheDocument();
		});

		describe('fire file selection', () => {
			let renderedComponent;

			beforeEach(() => {
				renderedComponent = renderComponent(baseProps);

				fireEvent.change(renderedComponent.getByTestId('filesInput'), {
					target: {files},
				});
			});

			it('render a list of files', () => {
				const {getByText} = renderedComponent;

				files.forEach(({name: filename}) => {
					expect(getByText(filename)).toBeInTheDocument();
				});
			});

			it('enable the save and publish button', () => {
				const {getByRole} = renderedComponent;

				expect(
					getByRole('button', {
						name: baseProps.publishButtonLabel,
					})
				).toBeEnabled();

				expect(
					getByRole('button', {
						name: baseProps.saveButtonLabel,
					})
				).toBeEnabled();
			});

			it('select files button changes to replace files and remove files button is present', () => {
				const {getByRole, queryByRole} = renderedComponent;

				expect(
					queryByRole('button', {
						name: 'select-files',
					})
				).not.toBeInTheDocument();

				expect(
					getByRole('button', {
						name: 'replace-files',
					})
				).toBeEnabled();

				expect(
					getByRole('button', {
						name: 'remove-files',
					})
				).toBeEnabled();
			});
		});
	});

	describe('renders with workflowPending and fire file selection', () => {
		it('enable the save button but not the publish button', () => {
			const {getByRole, getByTestId} = renderComponent({
				...baseProps,
				workflowPending: true,
			});

			fireEvent.change(getByTestId('filesInput'), {target: {files}});

			expect(
				getByRole('button', {
					name: baseProps.publishButtonLabel,
				})
			).toBeDisabled();

			expect(
				getByRole('button', {
					name: baseProps.saveButtonLabel,
				})
			).toBeEnabled();
		});
	});

	it('a danger alert with the error message is rendered', () => {
		const errorMessage =
			'Please enter a file with a valid xliff file extension (.xliff, .xlf, .zip).';

		const {getByRole} = renderComponent({
			...baseProps,
			errorMessage,
		});

		const alert = getByRole('alert');
		const container = alert.parentElement;
		expect(within(container).getByText(errorMessage)).toBeInTheDocument();
		expect(container).toHaveClass('alert-danger');
	});
});
