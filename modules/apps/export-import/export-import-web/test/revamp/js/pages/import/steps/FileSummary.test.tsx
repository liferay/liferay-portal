/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import FileSummary from '../../../../../../src/main/resources/META-INF/resources/revamp/js/pages/import/steps/FileSummary';
import {ImportPreview} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';
import formatDate from '../../../../../../src/main/resources/META-INF/resources/revamp/js/utils/formatDate';

const baseImportPreview: ImportPreview = {
	additionCount: 0,
	author: 'Jane Doe',
	deletionCount: 0,
	exportDate: '2026-05-01T00:00:00Z',
	fileName: 'site.lar',
	fileSize: 1024,
	portletDataHandlers: [],
};

const valueOf = (label: string) =>
	screen.getByText(label).nextElementSibling as HTMLElement;

describe('FileSummary', () => {
	it('renders the file name in the sheet header', () => {
		render(<FileSummary importPreview={baseImportPreview} />);

		expect(screen.getByText('site.lar')).toBeInTheDocument();
	});

	it('renders author, absolute export date, and formatted file size', () => {
		render(<FileSummary importPreview={baseImportPreview} />);

		expect(valueOf('author')).toHaveTextContent('Jane Doe');
		expect(valueOf('exported')).toHaveTextContent(
			formatDate('2026-05-01T00:00:00Z')
		);
		expect(valueOf('size')).toHaveTextContent('1 KB');
	});

	it('falls back to em-dash when author is null', () => {
		render(
			<FileSummary
				importPreview={{
					...baseImportPreview,
					author: null as unknown as string,
				}}
			/>
		);

		expect(valueOf('author')).toHaveTextContent('—');
	});

	it('falls back to em-dash when exportDate is empty', () => {
		render(
			<FileSummary
				importPreview={{...baseImportPreview, exportDate: ''}}
			/>
		);

		expect(valueOf('exported')).toHaveTextContent('—');
	});

	it('falls back to em-dash when fileSize is null', () => {
		render(
			<FileSummary
				importPreview={{
					...baseImportPreview,
					fileSize: null as unknown as number,
				}}
			/>
		);

		expect(valueOf('size')).toHaveTextContent('—');
	});

	it('renders 0 KB (not em-dash) when fileSize is 0', () => {
		render(
			<FileSummary importPreview={{...baseImportPreview, fileSize: 0}} />
		);

		expect(valueOf('size')).toHaveTextContent('0 KB');
	});

	it('omits the file name node in the header when fileName is empty', () => {
		const {container} = render(
			<FileSummary importPreview={{...baseImportPreview, fileName: ''}} />
		);

		expect(screen.getByText('file-summary')).toBeInTheDocument();
		expect(
			container.querySelectorAll('.sheet-text.text-3.text-secondary')
		).toHaveLength(0);
	});
});
