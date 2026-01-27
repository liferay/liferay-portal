/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import ImportTranslationModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/ImportTranslationModalContent';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper',
	() => ({
		__esModule: true,
		default: {
			postFormData: jest.fn(),
		},
	})
);

const DEFAULT_PROPS = {
	actionLink: '/test-link',
	groupId: 10,
	itemId: 22,
	itemName: 'Asset 1',
	loadData: jest.fn(),
	onModalClose: jest.fn(),
};

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(<ImportTranslationModalContent {...props} />);
};

const createFile = (name: string, size: number, type = 'image/png') => {
	return new File(['a'.repeat(size)], name, {type});
};

describe('ImportTranslationModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('checks the accessibility of the modal component', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('renders the Multiple Upload component', () => {
		const {getByRole, getByText} = renderComponent();

		expect(
			getByRole('heading', {
				level: 1,
				name: 'import-translation',
			})
		).toBeInTheDocument();

		expect(
			getByText('please-upload-your-translation-files')
		).toBeInTheDocument();
		expect(getByText('drag-and-drop-your-files-or')).toBeInTheDocument();
		expect(getByRole('button', {name: 'select-files'})).toBeInTheDocument();
	});

	it('calls ApiHelper.postFormData and shows success toast if result is ok', async () => {
		(ApiHelper.postFormData as jest.Mock).mockResolvedValue({
			data: {
				failureMessagesJSON: [],
				successMessages: ['file1.xlf'],
			},
		});

		const {container, findByText, getByRole} = renderComponent();

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('file1.xlf', 2048);

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(await findByText('file1.xlf')).toBeInTheDocument();

		fireEvent.click(getByRole('button', {name: 'import'}));

		await waitFor(() => {
			expect(ApiHelper.postFormData).toHaveBeenCalledTimes(1);
		});

		expect(openToast).toHaveBeenCalledWith({
			message:
				'x-file-was-successfully-imported-x-is-now-published-with-new-translations',
			type: 'success',
		});
	});

	it('shows all errors if request fails', async () => {
		(ApiHelper.postFormData as jest.Mock).mockResolvedValue({
			data: {
				failureMessagesJSON: ['file1.xlf', 'file2.xlf'],
				successMessages: [],
			},
		});

		const {container, findByText, getByRole} = renderComponent();

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		const file = createFile('file1.xlf', 2048);

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(await findByText('file1.xlf')).toBeInTheDocument();

		fireEvent.click(getByRole('button', {name: 'import'}));

		await waitFor(() => {
			expect(ApiHelper.postFormData).toHaveBeenCalledTimes(1);
		});

		expect(openToast).not.toHaveBeenCalled();
	});
});
