/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ExportFormModal from '../../../../src/main/resources/META-INF/resources/js/components/export-form/ExportFormModal';

import '@testing-library/jest-dom';

describe('ExportFormModal', () => {
	let fileExtensions;
	let observer;

	beforeEach(() => {
		fileExtensions = ['csv', 'json', 'xls', 'xml'];
		observer = {
			dispatch: () => {},
			mutation: [1, 1],
		};
	});

	it('renders', () => {
		render(
			<ExportFormModal
				fileExtensions={fileExtensions}
				observer={observer}
			/>
		);

		expect(screen.getByText('cancel')).toBeInTheDocument();
		expect(screen.getByText('export')).toBeInTheDocument();
		expect(screen.getByText('ok')).toBeInTheDocument();
		expect(
			screen.getByText(
				'the-export-includes-data-from-all-fields-and-form-versions'
			)
		).toBeInTheDocument();
		expect(
			screen.getByText('timezone-warning-message')
		).toBeInTheDocument();
	});

	it('closes modal when clicking the cancel button', () => {
		const onClose = jest.fn();

		render(
			<ExportFormModal
				fileExtensions={fileExtensions}
				observer={observer}
				onClose={onClose}
			/>
		);

		userEvent.click(screen.getByText('cancel'));

		expect(onClose).toHaveBeenCalled();
	});

	it('changes warning message when changing file extension', () => {
		render(
			<ExportFormModal
				csvExport="enabled-with-warning"
				fileExtensions={fileExtensions}
				observer={observer}
			/>
		);

		const fileExtension = screen.getByLabelText('file-extension');

		expect(fileExtension).toHaveValue('csv');
		expect(screen.getByText('csv-warning-message')).toBeInTheDocument();

		userEvent.selectOptions(fileExtension, 'json');

		/* TODO: Remove that after bump userEvent to version 12.0.7 or later https://github.com/testing-library/user-event/issues/358 */
		fireEvent.change(fileExtension);

		expect(fileExtension).toHaveValue('json');
		expect(
			screen.queryByText('csv-warning-message')
		).not.toBeInTheDocument();

		userEvent.selectOptions(fileExtension, 'xls');

		/* TODO: Remove that after bump userEvent to version 12.0.7 or later https://github.com/testing-library/user-event/issues/358 */
		fireEvent.change(fileExtension);

		expect(fileExtension).toHaveValue('xls');
		expect(
			screen.getByText(
				'the-total-number-of-characters-that-a-cell-can-contain-is-32767-characters'
			)
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'the-total-number-of-columns-that-a-worksheet-can-contain-is-256-columns'
			)
		).toBeInTheDocument();

		userEvent.selectOptions(fileExtension, 'xml');

		/* TODO: Remove that after bump userEvent to version 12.0.7 or later https://github.com/testing-library/user-event/issues/358 */
		fireEvent.change(fileExtension);

		expect(fileExtension).toHaveValue('xml');
		expect(
			screen.queryByText(
				'the-total-number-of-characters-that-a-cell-can-contain-is-32767-characters'
			)
		).not.toBeInTheDocument();
		expect(
			screen.queryByText(
				'the-total-number-of-columns-that-a-worksheet-can-contain-is-256-columns'
			)
		).not.toBeInTheDocument();
	});

	it('does not show csv option according to csvExport', () => {
		render(
			<ExportFormModal
				csvExport="disabled"
				fileExtensions={fileExtensions}
				observer={observer}
			/>
		);

		expect(screen.getByLabelText('file-extension')).toHaveValue('json');
		expect(screen.queryByText('CSV')).not.toBeInTheDocument();
	});

	it('does not show csv warning message according to csvExport', () => {
		render(
			<ExportFormModal
				csvExport="enabled"
				fileExtensions={fileExtensions}
				observer={observer}
			/>
		);

		expect(screen.getByLabelText('file-extension')).toHaveValue('csv');
		expect(
			screen.queryByText('csv-warning-message')
		).not.toBeInTheDocument();
	});
});
