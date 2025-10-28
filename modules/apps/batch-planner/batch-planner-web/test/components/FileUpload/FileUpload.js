/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import parseFile from '../../../src/main/resources/META-INF/resources/js/FileParsers';
import FileUpload from '../../../src/main/resources/META-INF/resources/js/components/FileUpload';
import {
	FILE_EXTENSION_INPUT_PARTIAL_NAME,
	FILE_SCHEMA_EVENT,
} from '../../../src/main/resources/META-INF/resources/js/constants';

jest.mock('../../../src/main/resources/META-INF/resources/js/FileParsers');

const fileContents = `currencyCode,type,name
    USD,site,My Channel 0
    USD,site,My Channel 1
    USD,site,My Channel 2
    USD,site,My Channel 3
    USD,site,My Channel 4
`;
const fileSchema = ['currencyCode', 'type', 'name'];
const file = new Blob([fileContents], {type: 'text/csv'});

file.name = 'test.csv';

describe('FileUpload', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(cleanup);

	it('must read the file on input change', async () => {
		const mockFileSchemaListener = jest.fn();

		Liferay.on(FILE_SCHEMA_EVENT, mockFileSchemaListener);

		parseFile.mockImplementationOnce(({onComplete}) =>
			onComplete({schema: fileSchema})
		);

		const {getByLabelText} = render(<FileUpload portletNamespace="test" />);

		act(() => {
			fireEvent.change(getByLabelText(/file \((.*)\)/), {
				target: {files: [file]},
			});
		});

		expect(mockFileSchemaListener).toHaveBeenLastCalledWith(
			expect.objectContaining({
				schema: fileSchema,
			})
		);
	});

	it('must update the fileExtension Input value on input change', async () => {
		const testInput = document.createElement('input');

		testInput.id = `test_${FILE_EXTENSION_INPUT_PARTIAL_NAME}`;

		document.body.appendChild(testInput);

		const mockFileSchemaListener = jest.fn();

		Liferay.on(FILE_SCHEMA_EVENT, mockFileSchemaListener);

		parseFile.mockImplementationOnce(({onComplete}) =>
			onComplete({extension: 'tst'})
		);

		const {getByLabelText} = render(
			<FileUpload portletNamespace="test_" />
		);

		act(() => {
			fireEvent.change(getByLabelText(/file \((.*)\)/), {
				target: {files: [file]},
			});
		});

		expect(testInput.value).toBe('TST');
	});
});
