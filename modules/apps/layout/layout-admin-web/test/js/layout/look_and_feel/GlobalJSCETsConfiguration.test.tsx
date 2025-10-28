/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	act,
	findByRole,
	fireEvent,
	render,
	screen,
} from '@testing-library/react';
import {openSelectionModal} from 'frontend-js-components-web';
import * as React from 'react';

import GlobalJSCETsConfiguration from '../../../../src/main/resources/META-INF/resources/js/layout/look_and_feel/GlobalJSCETsConfiguration';

jest.mock('frontend-js-components-web', () => ({
	openSelectionModal: jest.fn(),
	openToast: () => {},
}));

const openSelectionModalMock = openSelectionModal as jest.Mock<
	typeof openSelectionModal
>;

describe('GlobalJSCETsConfiguration', () => {
	afterEach(() => {
		openSelectionModalMock.mockReset();
	});

	it('shows "no extensions loaded" if there are no extensions', async () => {
		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		await screen.findByText('no-javascript-client-extensions-were-loaded');
	});

	it('renders the given list of global extensions', async () => {
		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global JS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		await screen.findByText('Nice Global JS');
	});

	it('renders a dropdown with default, async and defer options for js global extensions', async () => {
		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global JS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		const selectElement =
			document.querySelector<HTMLInputElement>('.load-type-select');

		expect(selectElement?.value).toBe('default');

		fireEvent.change(selectElement as Element, {
			target: {value: 'async'},
		});

		expect(selectElement?.value).toBe('async');

		fireEvent.change(selectElement as Element, {
			target: {value: 'defer'},
		});

		expect(selectElement?.value).toBe('defer');
	});

	it('renders a hidden input with the list of selected extensions', async () => {
		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global JS',
					},
					{
						cetExternalReferenceCode: 'anotherNiceId',
						inherited: false,
						inheritedLabel: '',
						loadType: 'defer',
						name: 'Nice Global JS v2',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		await screen.findByDisplayValue('niceId_default_bottom');
		await screen.findByDisplayValue('anotherNiceId_defer_bottom');
	});

	it('opens a selection modal when "add" button is pressed', async () => {
		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global JS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		fireEvent.click(
			await screen.findByRole('button', {
				name: 'add-javascript-client-extensions',
			})
		);

		const item = await screen.findByRole('menuitem', {
			name: 'in-page-bottom',
		});

		fireEvent.click(item.firstChild!);

		expect(openSelectionModal).toHaveBeenCalled();
	});

	it('removes duplicated extensions if any', async () => {
		openSelectionModalMock.mockImplementation(() => () => {});

		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global JS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		fireEvent.click(
			await screen.findByRole('button', {
				name: 'add-javascript-client-extensions',
			})
		);

		const item = await screen.findByRole('menuitem', {
			name: 'in-page-bottom',
		});

		fireEvent.click(item.firstChild!);

		expect(openSelectionModal).toHaveBeenCalledTimes(1);

		expect(openSelectionModal).toHaveBeenCalledWith(
			expect.objectContaining({
				onSelect: expect.any(Function),
			})
		);

		const [[{onSelect}]] = openSelectionModalMock.mock.calls;

		act(() => {
			onSelect({
				value: [
					JSON.stringify({
						cetExternalReferenceCode: 'niceId',
						name: 'Nice Global JS',
					}),
					JSON.stringify({
						cetExternalReferenceCode: 'someNiceId',
						name: 'Some Nice Global JS',
					}),
				],
			});
		});

		await screen.findByDisplayValue('niceId_default_bottom');
		await screen.findByDisplayValue('someNiceId_default_bottom');
	});

	it('computes order when moving items', async () => {
		openSelectionModalMock.mockImplementation(() => () => {});

		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'a',
						inherited: false,
						inheritedLabel: '',
						name: 'A',
						scriptLocation: 'head',
					},
					{
						cetExternalReferenceCode: 'b',
						inherited: false,
						inheritedLabel: '',
						name: 'B',
						scriptLocation: 'head',
					},
					{
						cetExternalReferenceCode: 'c',
						inherited: false,
						inheritedLabel: '',
						name: 'C',
						scriptLocation: 'bottom',
					},
					{
						cetExternalReferenceCode: 'd',
						inherited: false,
						inheritedLabel: '',
						name: 'D',
						scriptLocation: 'bottom',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		expect(
			(await screen.findByText('1')).nextElementSibling
		).toHaveTextContent('A');

		expect(
			(await screen.findByText('2')).nextElementSibling
		).toHaveTextContent('B');

		expect(
			(await screen.findByText('3')).nextElementSibling
		).toHaveTextContent('C');

		expect(
			(await screen.findByText('4')).nextElementSibling
		).toHaveTextContent('D');

		fireEvent.click(
			await screen.findByRole('button', {
				name: 'add-javascript-client-extensions',
			})
		);

		const item = await screen.findByRole('menuitem', {
			name: 'in-page-head',
		});

		fireEvent.click(item.firstChild!);

		expect(openSelectionModal).toHaveBeenCalledTimes(1);

		expect(openSelectionModal).toHaveBeenCalledWith(
			expect.objectContaining({
				onSelect: expect.any(Function),
			})
		);

		const [[{onSelect}]] = openSelectionModalMock.mock.calls;

		act(() => {
			onSelect({
				value: [
					JSON.stringify({
						cetExternalReferenceCode: 'd',
						name: 'D',
					}),
				],
			});
		});

		expect(
			(await screen.findByText('1')).nextElementSibling
		).toHaveTextContent('A');

		expect(
			(await screen.findByText('2')).nextElementSibling
		).toHaveTextContent('B');

		expect(
			(await screen.findByText('3')).nextElementSibling
		).toHaveTextContent('D');

		expect(
			(await screen.findByText('4')).nextElementSibling
		).toHaveTextContent('C');
	});

	it('allows removing extensions by pressing dropdown "remove" button', async () => {
		render(
			<GlobalJSCETsConfiguration
				globalJSCETSelectorURL=""
				globalJSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global JS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalJSCETsEventName=""
			/>
		);

		fireEvent.click(
			await screen.findByRole('button', {name: 'show-options'})
		);

		const item = await findByRole(
			await screen.findByRole('presentation', {name: 'show-options'}),
			'menuitem',
			{name: 'delete'}
		);

		fireEvent.click(item.firstChild!);

		await screen.findByText('no-javascript-client-extensions-were-loaded');
	});
});
