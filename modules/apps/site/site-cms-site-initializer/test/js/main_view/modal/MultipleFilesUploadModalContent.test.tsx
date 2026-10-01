/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import MultipleFilesUploadModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/MultipleFilesUploadModalContent';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper',
	() => ({
		__esModule: true,
		default: {
			post: jest.fn(),
		},
	})
);

const DEFAULT_PROPS = {
	assetLibraries: [
		{
			externalReferenceCode: 'space',
			groupId: 1,
			name: 'Space',
		},
	],
	baseAssetLibraryViewURL: '/web/cms/e/space/1/',
	maxFileSize: '1000',
	onModalClose: jest.fn(),
	parentObjectEntryFolderExternalReferenceCode: 'L_FILES',
} as any;

async function selectFile(size: number) {
	const user = userEvent.setup();

	const {container} = render(
		<MultipleFilesUploadModalContent {...DEFAULT_PROPS} />
	);

	await user.upload(
		container.querySelector<HTMLInputElement>('input[type="file"]')!,
		new File(['a'.repeat(size)], 'file.txt', {type: 'text/plain'})
	);
}

describe('MultipleFilesUploadModalContent', () => {
	it('accepts a file within the maximum file size', async () => {
		await selectFile(1000);

		expect(screen.getByText('file.txt')).toBeInTheDocument();
		expect(
			screen.queryByText(/please-enter-a-file-with-a-valid-file-size/)
		).not.toBeInTheDocument();
	});

	it('rejects a file larger than the maximum file size', async () => {
		await selectFile(1001);

		expect(
			screen.getByText(/please-enter-a-file-with-a-valid-file-size/)
		).toBeInTheDocument();
		expect(screen.queryByText('file.txt')).not.toBeInTheDocument();
	});
});
