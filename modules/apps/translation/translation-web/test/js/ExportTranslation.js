/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import ExportTranslation from '../../src/main/resources/META-INF/resources/js/ExportTranslation';

const baseProps = {
	availableExportFileFormats: [{displayName: '', mimeType: ''}],
	availableSourceLocales: [{displayName: '', languageId: '0'}],
	availableTargetLocales: [{displayName: '', languageId: '0'}],
	defaultSourceLanguageId: 'en-US',
	exportTranslationURL: 'http://export-url',
};
const renderComponent = (props) => render(<ExportTranslation {...props} />);

describe('Export Translation', () => {
	afterEach(cleanup);

	it('renders the experiences selector when multiple pages are selected', () => {
		const {getByText} = renderComponent({
			...baseProps,
			multipleExperiences: true,
			multiplePagesSelected: true,
		});

		expect(getByText('export-experiences')).toBeInTheDocument();
	});

	it('renders a radio button to select all experiences, with default value selected', () => {
		const {getAllByRole} = renderComponent({
			...baseProps,
			multipleExperiences: true,
			multiplePagesSelected: true,
		});

		const radioButton = getAllByRole('radio');

		expect(radioButton[0].parentElement).toHaveTextContent(
			'default-experience'
		);
		expect(radioButton[1].parentElement).toHaveTextContent(
			'all-experience'
		);

		expect(radioButton[0]).toBeChecked();
	});
});
