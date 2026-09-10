/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import openModal from '../../../src/main/resources/META-INF/resources/modal/commands/openModal';
import openSelectionModal from '../../../src/main/resources/META-INF/resources/modal/commands/openSelectionModal';

jest.mock(
	'../../../src/main/resources/META-INF/resources/modal/commands/openModal',
	() => jest.fn((args) => args)
);

const getModalProps = () => (openModal as jest.Mock).mock.calls[0][0];

const getAddButtonOnClick = () =>
	getModalProps().buttons.find(({type}: {type?: string}) => type !== 'cancel')
		.onClick;

const createIframeWindow = () => {
	const iframeDocument = document.implementation.createHTMLDocument();

	iframeDocument.body.innerHTML =
		'<div class="searchcontainer" id="entries">' +
		'<table><tbody><tr data-value="row-payload">' +
		'<td><input type="checkbox" value="1" /></td>' +
		'</tr></tbody></table></div>';

	const checkbox = iframeDocument.querySelector('input');

	return {
		Liferay: {
			componentReady: () =>
				Promise.resolve({
					select: {
						getAllSelectedElements: () => ({
							getDOMNodes: () => [checkbox],
						}),
					},
				}),
		},
		document: iframeDocument,
	};
};

describe('openSelectionModal', () => {
	afterEach(() => {
		jest.resetAllMocks();
	});

	it('disables the modal buttons while the iframe loads', () => {
		openSelectionModal({
			multiple: true,
			onSelect: () => {},
			title: 'Select Organization',
			url: 'https://www.sample.url',
		});

		expect(getModalProps().disableButtonsOnLoading).toBe(true);
	});

	it('leaves the modal buttons alone when there is no add button', () => {
		openSelectionModal({
			onSelect: () => {},
			title: 'Select Organization',
			url: 'https://www.sample.url',
		});

		expect(getModalProps().buttons).toBeUndefined();

		expect(getModalProps().disableButtonsOnLoading).toBe(false);
	});

	it('selects the checked items when the add button is clicked after the iframe opens', async () => {
		const onSelect = jest.fn();
		const processClose = jest.fn();

		openSelectionModal({
			multiple: true,
			onSelect,
			title: 'Select Organization',
			url: 'https://www.sample.url',
		});

		getModalProps().onOpen({
			iframeWindow: createIframeWindow(),
			processClose,
		});

		getAddButtonOnClick()();

		await Promise.resolve();

		expect(onSelect).toHaveBeenCalledWith([{value: 'row-payload'}]);

		expect(processClose).toHaveBeenCalled();
	});
});
