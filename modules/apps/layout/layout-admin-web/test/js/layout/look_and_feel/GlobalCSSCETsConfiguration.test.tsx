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
import userEvent from '@testing-library/user-event';
import {openSelectionModal} from 'frontend-js-components-web';
import * as React from 'react';

import GlobalCSSCETsConfiguration from '../../../../src/main/resources/META-INF/resources/js/layout/look_and_feel/GlobalCSSCETsConfiguration';

jest.mock('frontend-js-components-web', () => ({
	openSelectionModal: jest.fn(),
	openToast: () => {},
}));

const openSelectionModalMock = openSelectionModal as jest.Mock<
	typeof openSelectionModal
>;

describe('GlobalCSSCETsConfiguration', () => {
	afterEach(() => {
		openSelectionModalMock.mockReset();
	});

	it('shows "no extensions loaded" if there are no extensions', async () => {
		render(
			<GlobalCSSCETsConfiguration
				globalCSSCETSelectorURL=""
				globalCSSCETs={[]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalCSSCETsEventName=""
			/>
		);

		await screen.findByText('no-css-client-extensions-were-loaded');
	});

	it('renders the given list of global extensions', async () => {
		render(
			<GlobalCSSCETsConfiguration
				globalCSSCETSelectorURL=""
				globalCSSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalCSSCETsEventName=""
			/>
		);

		await screen.findByText('Nice Global CSS');
	});

	it('renders a hidden input with the list of selected extensions', async () => {
		render(
			<GlobalCSSCETsConfiguration
				globalCSSCETSelectorURL=""
				globalCSSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS',
					},
					{
						cetExternalReferenceCode: 'anotherNiceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS v2',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalCSSCETsEventName=""
			/>
		);

		await screen.findByDisplayValue('niceId');
		await screen.findByDisplayValue('anotherNiceId');
	});

	it('opens a selection modal when "add" button is pressed', async () => {
		render(
			<GlobalCSSCETsConfiguration
				globalCSSCETSelectorURL=""
				globalCSSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalCSSCETsEventName=""
			/>
		);

		await userEvent.click(
			await screen.findByRole('button', {
				name: 'add-css-client-extensions',
			})
		);

		expect(openSelectionModalMock).toHaveBeenCalled();
	});

	it('removes duplicated extensions if any', async () => {
		openSelectionModalMock.mockImplementation(() => () => {});

		render(
			<GlobalCSSCETsConfiguration
				globalCSSCETSelectorURL=""
				globalCSSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalCSSCETsEventName=""
			/>
		);

		await userEvent.click(
			await screen.findByRole('button', {
				name: 'add-css-client-extensions',
			})
		);

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
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS',
					}),
					JSON.stringify({
						cetExternalReferenceCode: 'someNiceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Some Nice Global CSS',
					}),
				],
			});
		});

		await screen.findByDisplayValue('niceId');
		await screen.findByDisplayValue('someNiceId');
	});

	it('allows removing extensions by pressing dropdown "remove" button', async () => {
		render(
			<GlobalCSSCETsConfiguration
				globalCSSCETSelectorURL=""
				globalCSSCETs={[
					{
						cetExternalReferenceCode: 'niceId',
						inherited: false,
						inheritedLabel: '',
						name: 'Nice Global CSS',
					},
				]}
				isReadOnly={false}
				portletNamespace=""
				selectGlobalCSSCETsEventName=""
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

		await screen.findByText('no-css-client-extensions-were-loaded');
	});
});
