/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import CMSFileUploaderComponent from '../src/main/resources/META-INF/resources/item_selector_file_uploader/CMSFileUploaderComponent';

const DEFAULT_PROPS = {
	files: [],
	onCloseUploadView: jest.fn(),
};

describe('CMSFileUploaderComponent', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('asks for the space when no group is given', () => {
		render(<CMSFileUploaderComponent {...DEFAULT_PROPS} />);

		expect(screen.getByLabelText(/space/)).toBeInTheDocument();

		expect(
			screen.getByText('select-the-space-to-upload-the-file')
		).toBeInTheDocument();
	});

	it('does not ask for the space when a group is given', () => {
		render(<CMSFileUploaderComponent {...DEFAULT_PROPS} groupId={123} />);

		expect(screen.queryByLabelText(/space/)).not.toBeInTheDocument();
	});
});
