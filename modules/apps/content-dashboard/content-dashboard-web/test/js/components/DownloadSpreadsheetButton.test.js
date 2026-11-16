/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import '@testing-library/jest-dom';
import {act} from 'react-dom/test-utils';

import DownloadSpreadsheetButton from '../../../src/main/resources/META-INF/resources/js/components/DownloadSpreadsheetButton/DownloadSpreadsheetButton';
import * as utils from '../../../src/main/resources/META-INF/resources/js/components/DownloadSpreadsheetButton/downloadSpreadsheetUtils';

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openToast: jest.fn(),
}));

const getComponent = (fileURL = 'demo-file-url') => {
	return <DownloadSpreadsheetButton fileURL={fileURL} total={12} />;
};

describe('DownloadSpreadsheetButton', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		jest.useFakeTimers();

		global.Liferay.on = jest.fn().mockReturnValue({
			detach: jest.fn(),
		});

		global.fetch = jest.fn().mockReturnValue({
			blob: jest.fn(),
		});

		global.URL = {
			createObjectURL: jest.fn(),
			revokeObjectURL: jest.fn(),
		};
	});

	afterEach(() => {
		jest.runOnlyPendingTimers();
		jest.useRealTimers();

		jest.restoreAllMocks();
		cleanup();
	});

	it('renders a DownloadSpreadsheetButton component with the proper initial UI state', () => {
		const {container, getByText, getByTitle} = render(getComponent());

		expect(getByText('export-xls')).toBeInTheDocument();

		const {className: btnClassName} = getByTitle(
			'download-your-data-as-an-xls-file'
		);

		expect(
			getByTitle('download-your-data-as-an-xls-file')
		).toBeInTheDocument();
		expect(btnClassName).toContain(
			'btn-outline-borderless btn-outline-secondary'
		);

		expect(
			container.getElementsByClassName('lexicon-icon-download').length
		).toBe(1);
	});

	it('...with the proper loading UI state', async () => {
		jest.spyOn(utils, 'fetchFile').mockResolvedValue(new Blob());
		jest.spyOn(utils, 'downloadFileFromBlob').mockImplementation(() => {});

		const {container, findByText, getByText} = render(getComponent());
		const exportButton = getByText('export-xls');

		expect(getByText('export-xls')).toBeInTheDocument();

		fireEvent(
			exportButton,
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		expect(getByText('generating-xls')).toBeInTheDocument();
		expect(
			container.getElementsByClassName('loading-animation').length
		).toBe(1);

		await findByText('xls-generated');

		act(() => {
			jest.runAllTimers();
		});

		expect(getByText('export-xls')).toBeInTheDocument();
	});

	it('...with the proper restored UI state after cancel', async () => {
		jest.spyOn(utils, 'fetchFile').mockImplementation(
			({controller}) =>
				new Promise((resolve, reject) => {
					controller.signal.addEventListener('abort', () =>
						reject(new DOMException('', 'AbortError'))
					);
				})
		);

		const {container, findByText, getByText, getByTitle} =
			render(getComponent());
		const exportButton = getByText('export-xls');

		fireEvent(
			exportButton,
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		const cancelButton = getByTitle('cancel-export');
		expect(cancelButton).toBeInTheDocument();
		expect(
			container.getElementsByClassName('lexicon-icon-times-circle').length
		).toBe(1);

		expect(getByText('generating-xls')).toBeInTheDocument();

		fireEvent(
			cancelButton,
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		await findByText('xls-generation-was-cancelled');

		act(() => {
			jest.runAllTimers();
		});

		await findByText('export-xls');
		expect(getByText('export-xls')).toBeInTheDocument();

		expect(
			container.getElementsByClassName('loading-animation').length
		).toBe(0);
	});

	it('...that calls the proper functions on events', async () => {

		/* eslint-disable no-import-assign */
		const fileURL = 'demo-file-url';
		const {getByText} = render(getComponent(fileURL));
		const exportButton = getByText('export-xls');

		utils.downloadFileFromBlob = jest.fn();

		const mockedFetch = jest.fn();
		utils.fetchFile = jest.fn(mockedFetch);

		const blob = new Blob();

		mockedFetch.mockReturnValue(blob);

		const controller = new AbortController();

		fireEvent(
			exportButton,
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		expect(getByText('generating-xls')).toBeInTheDocument();

		act(() => {
			jest.runAllTimers();
		});

		await expect(mockedFetch).toHaveBeenCalledWith({
			controller,
			url: fileURL,
		});
		expect(utils.downloadFileFromBlob).toHaveBeenCalledWith(blob);
		expect(openToast).toHaveBeenCalledWith({
			message: 'xls-was-successfully-generated',
			title: '',
			type: 'success',
		});
	});

	it('...that opens a toast when it unmounts during the download', async () => {
		let resolveFetch;

		jest.spyOn(utils, 'fetchFile').mockReturnValue(
			new Promise((resolve) => {
				resolveFetch = resolve;
			})
		);
		jest.spyOn(utils, 'downloadFileFromBlob').mockImplementation(() => {});

		const {findByText, getByText, unmount} = render(getComponent());

		fireEvent(
			getByText('export-xls'),
			new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
			})
		);

		await findByText('generating-xls');

		unmount();

		await act(async () => {
			resolveFetch(new Blob());
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(openToast).toHaveBeenCalledWith({
			message: 'xls-was-successfully-generated',
			title: '',
			type: 'success',
		});
	});
});
