/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {NewImport} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/import/NewImport';
import {postImportPreview} from '../../../../../src/main/resources/META-INF/resources/revamp/js/services/postImportPreview';
import {SCOPES} from '../../../../../src/main/resources/META-INF/resources/revamp/js/types/scope';
import formatDate from '../../../../../src/main/resources/META-INF/resources/revamp/js/utils/formatDate';
import {mockImportPreview} from '../../mocks/mockImportPreview';
import {mockPreviewPortletDataHandlerSectionsForFilterTest} from '../../mocks/mockSectionsForFilterTest';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/revamp/js/services/postImportPreview',
	() => ({
		postImportPreview: jest.fn(() =>
			Promise.resolve({data: mockImportPreview, error: null})
		),
	})
);

const renderComponent = ({
	commentsAndRatingsEnabled,
	lookAndFeelEnabled,
}: {
	commentsAndRatingsEnabled?: boolean;
	lookAndFeelEnabled?: boolean;
} = {}) =>
	render(
		<NewImport
			backURL="/some/back/url"
			commentsAndRatingsEnabled={commentsAndRatingsEnabled}
			importPreviewAPIURL="/o/export-import/v1.0/import-preview"
			importProcessAPIURL="/o/export-import/v1.0/import-processes"
			lookAndFeelEnabled={lookAndFeelEnabled}
			scope={SCOPES.SITE}
		/>
	);

let user: ReturnType<typeof userEvent.setup>;

const uploadFile = async (fileName = 'site.lar') => {
	jest.useFakeTimers();

	const fakeUser = userEvent.setup({
		advanceTimers: jest.advanceTimersByTime,
	});

	const file = new File(['test'], fileName, {type: '.lar'});
	const fileInput = document.querySelector(
		'input[type="file"]'
	) as HTMLInputElement;

	await fakeUser.upload(fileInput, file);

	act(() => {
		jest.runAllTimers();
	});

	jest.useRealTimers();
};

const goToDataSelectionStep = async (
	options: {
		commentsAndRatingsEnabled?: boolean;
		lookAndFeelEnabled?: boolean;
	} = {}
) => {
	const result = renderComponent(options);

	await user.type(screen.getByLabelText(/^name/i), 'My import');

	await uploadFile('site.lar');

	await waitFor(() => {
		expect(screen.getByRole('button', {name: /continue/i})).toBeEnabled();
	});

	await user.click(screen.getByRole('button', {name: /continue/i}));

	return result;
};

describe('NewImport', () => {
	beforeAll(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			(key: string) =>
				({
					'collapse-x': 'Collapse {0}',
					'expand-x': 'Expand {0}',
					'hide-all-x': 'Hide All {0}',
					'select-x': 'Select {0}',
					'selected-x': 'Selected {0}',
					'show-all-x': 'Show All {0}',
				})[key] ?? key
		);
	});

	beforeEach(() => {
		user = userEvent.setup();
	});

	it('renders the FileSelectionStep with the Name field', async () => {
		const {container} = renderComponent();

		expect(screen.getByLabelText(/^name/i)).toBeInTheDocument();

		expect(screen.getByText('file-upload')).toBeInTheDocument();

		expect(
			screen.getByText('select-and-upload-your-prepared-file')
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: /drag-and-drop-to-upload/i})
		).toBeInTheDocument();

		await checkAccessibility({context: container});
	});

	it('shows a required error when the Name field is left empty on blur', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText(/^name/i);

		await user.click(nameInput);
		nameInput.blur();

		await screen.findByText('this-field-is-required');
	});

	it('shows a client-side error when a non-lar file is selected', async () => {
		renderComponent();

		const dropzone = screen.getByRole('button', {
			name: /drag-and-drop-to-upload/i,
		});

		const file = new File(['test'], 'Document.jpg', {type: 'image/jpeg'});

		fireEvent.drop(dropzone, {
			dataTransfer: {
				files: [file],
				items: [
					{
						getAsFile: () => file,
						kind: 'file',
						type: 'image/jpeg',
					},
				],
				types: ['Files'],
			},
		});

		expect(await screen.findByRole('alert')).toHaveTextContent(
			'File type must be .lar'
		);
	});

	it('shows the import preview error and keeps Continue disabled when the lar is invalid', async () => {
		(postImportPreview as jest.Mock).mockImplementationOnce(() =>
			Promise.resolve({
				data: null,
				error: 'Uploaded LAR file type Portlet does not match layout-prototype, layout-set, layout-set-prototype.',
			})
		);

		renderComponent();

		await uploadFile('folder.portlet.lar');

		expect(await screen.findByRole('alert')).toHaveTextContent(
			'Uploaded LAR file type Portlet does not match'
		);

		expect(screen.getByRole('button', {name: /continue/i})).toBeDisabled();
	});

	it('auto-fills the Name field with the uploaded file name when Name is empty', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText(/^name/i) as HTMLInputElement;

		expect(nameInput).toHaveValue('');

		await uploadFile('site.lar');

		await waitFor(() => {
			expect(nameInput).toHaveValue('site');
		});
	});

	it('preserves the user-provided Name when a file is uploaded', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText(/^name/i) as HTMLInputElement;

		await user.type(nameInput, 'My custom import');

		await uploadFile('site.lar');

		await waitFor(() => {
			expect(nameInput).toHaveValue('My custom import');
		});
	});

	it('re-fills the Name field with the new file name when Name has been cleared', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText(/^name/i) as HTMLInputElement;

		await uploadFile('first.lar');

		await waitFor(() => {
			expect(nameInput).toHaveValue('first');
		});

		await user.clear(nameInput);

		await user.click(
			await screen.findByRole('button', {name: /remove-file/i})
		);

		await uploadFile('second.lar');

		await waitFor(() => {
			expect(nameInput).toHaveValue('second');
		});
	});

	it('does not re-fill the Name after the user manually clears it while the same file is still selected', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText(/^name/i) as HTMLInputElement;

		await uploadFile('site.lar');

		await waitFor(() => {
			expect(nameInput).toHaveValue('site');
		});

		await user.clear(nameInput);

		expect(nameInput).toHaveValue('');
	});

	it('does not auto-fill the Name when the user clears a name they typed before uploading the file', async () => {
		renderComponent();

		const nameInput = screen.getByLabelText(/^name/i) as HTMLInputElement;

		await user.type(nameInput, 'My custom import');

		await uploadFile('site.lar');

		await waitFor(() => {
			expect(nameInput).toHaveValue('My custom import');
		});

		await user.clear(nameInput);

		expect(nameInput).toHaveValue('');
	});

	it('checks every section by default', async () => {
		await goToDataSelectionStep();

		expect(screen.getByRole('checkbox', {name: 'Design'})).toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Site Builder'})
		).toBeChecked();
		expect(
			screen.getByRole('checkbox', {name: 'Content & Data'})
		).toBeChecked();
	});

	it('shows the file summary and the lar contents on the Data Selection step after uploading a file and clicking Continue', async () => {
		(postImportPreview as jest.Mock).mockImplementationOnce(() =>
			Promise.resolve({
				data: {...mockImportPreview, deletionCount: 0},
				error: null,
			})
		);

		const {container} = await goToDataSelectionStep();

		expect(screen.getByText('file-summary')).toBeInTheDocument();
		expect(screen.getAllByText('site.lar').length).toBeGreaterThan(0);
		expect(screen.getByText('Test User')).toBeInTheDocument();
		expect(
			screen.getByText(formatDate('2000-07-27T00:00:00Z'))
		).toBeInTheDocument();
		expect(screen.getByText('4 KB')).toBeInTheDocument();

		expect(screen.getByText('Design')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: 'Expand Design'}));

		expect(
			screen.getByRole('checkbox', {name: 'Theme Settings'})
		).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'Logo'})).toBeChecked();
		expect(screen.getByRole('checkbox', {name: 'Fragments'})).toBeChecked();

		expect(
			screen.getByLabelText(/^import-permissions/)
		).toBeInTheDocument();
		expect(
			screen.queryByLabelText(/^replicate-selected-deletions/)
		).not.toBeInTheDocument();

		await checkAccessibility({context: container});
	});

	it('shows the section as indeterminate and summarizes the selected handlers when the selection is partial', async () => {
		await goToDataSelectionStep();

		await user.click(screen.getByRole('button', {name: 'Expand Design'}));

		await user.click(screen.getByRole('checkbox', {name: 'Fragments'}));

		expect(
			screen.getByRole('checkbox', {name: 'Design'})
		).toBePartiallyChecked();

		expect(
			screen.getByText('Selected Theme Settings, Logo')
		).toBeInTheDocument();
	});

	it('lists the available handlers with a Select prefix when nothing is selected', async () => {
		await goToDataSelectionStep();

		await user.click(screen.getByRole('checkbox', {name: 'Design'}));

		expect(
			screen.getByText('Select Theme Settings, Logo, Fragments')
		).toBeInTheDocument();
	});

	it('reveals and hides the nested handler controls through the Show all and Hide all toggle', async () => {
		await goToDataSelectionStep();

		await user.click(
			screen.getByRole('button', {name: 'Expand Content & Data'})
		);

		expect(
			screen.queryByRole('checkbox', {name: 'Version History'})
		).not.toBeInTheDocument();

		await user.click(
			screen.getByRole('button', {name: 'Show All Web Content'})
		);

		expect(
			screen.getByRole('checkbox', {name: 'Version History'})
		).toBeInTheDocument();

		await user.click(
			screen.getByRole('button', {name: 'Hide All Web Content'})
		);

		expect(
			screen.queryByRole('checkbox', {name: 'Version History'})
		).not.toBeInTheDocument();
	});

	it('filters deletions-only sections based on the deletions toggle', async () => {
		(postImportPreview as jest.Mock).mockImplementationOnce(() =>
			Promise.resolve({
				data: {
					...mockImportPreview,
					previewPortletDataHandlerSections:
						mockPreviewPortletDataHandlerSectionsForFilterTest,
				},
				error: null,
			})
		);

		renderComponent();

		await user.type(screen.getByLabelText(/^name/i), 'My import');

		await uploadFile('site.lar');

		await waitFor(() => {
			expect(
				screen.getByRole('button', {name: /continue/i})
			).toBeEnabled();
		});

		await user.click(screen.getByRole('button', {name: /continue/i}));

		expect(screen.getByText('Additions Only')).toBeInTheDocument();
		expect(screen.queryByText('Deletions Only')).not.toBeInTheDocument();
		expect(screen.getByText('Both')).toBeInTheDocument();
		expect(screen.getByText('No Counts')).toBeInTheDocument();

		await user.click(
			screen.getByLabelText(/^replicate-selected-deletions/)
		);

		expect(screen.getByText('Deletions Only')).toBeInTheDocument();
	});

	it('renders the Look and Feel block inside the Site Builder section when lookAndFeelEnabled is true', async () => {
		await goToDataSelectionStep({lookAndFeelEnabled: true});

		expect(screen.getByText('look-and-feel')).toBeInTheDocument();
		expect(screen.getByLabelText('theme-settings')).toBeInTheDocument();
		expect(screen.getByLabelText('logo')).toBeInTheDocument();
		expect(
			screen.getByLabelText('site-pages-settings')
		).toBeInTheDocument();
		expect(
			screen.getByLabelText('site-template-settings')
		).toBeInTheDocument();
	});

	it('renders the Comments and Ratings block inside the Site Content section when commentsAndRatingsEnabled is true', async () => {
		await goToDataSelectionStep({commentsAndRatingsEnabled: true});

		expect(
			await screen.findByText('comments-and-ratings')
		).toBeInTheDocument();
		expect(screen.getByLabelText('comments')).toBeInTheDocument();
		expect(screen.getByLabelText('ratings')).toBeInTheDocument();
	});
});
