/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import * as React from 'react';

import LayoutPageTemplateEntryCard from '../../src/main/resources/META-INF/resources/js/LayoutPageTemplateEntryCard';

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	createPortletURL: jest.fn(),
	fetch: jest.fn(),
}));

const openModalMock = openModal as jest.Mock<typeof openModal>;

const fetchMock = fetch as any;

const renderComponent = () => {
	const user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});

	render(
		<LayoutPageTemplateEntryCard
			addLayoutURL=""
			getLayoutPageTemplateEntryListURL=""
			layoutPageTemplateEntryId="1"
			portletNamespace="portletNamespace"
			subtitle="subtitle"
			thumbnailURL=""
			title="title"
		/>
	);

	return user;
};

describe('LayoutPageTemplateEntryCard', () => {
	beforeEach(() => {
		openModalMock.mockReset();

		fetchMock.mockReturnValue(
			Promise.resolve({
				json: () => {
					return [
						{
							layoutPageTemplateEntryId: '1',
							name: 'name1',
							previewLayout: '',
						},
						{
							layoutPageTemplateEntryId: '2',
							name: 'name2',
							previewLayout: '',
						},
					];
				},
			})
		);

		jest.useRealTimers();
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('renders', () => {
		renderComponent();

		expect(screen.getByTitle('title')).toBeInTheDocument();
	});

	it('call openModal when clicking on the card', async () => {
		const user = renderComponent();

		await user.click(screen.getByTitle('title'));

		expect(openModalMock).toBeCalled();
	});

	it('shows a modal when clicking the preview button', async () => {
		const user = renderComponent();

		await user.click(screen.getByTitle('preview-page-template'));

		await act(async () => {
			jest.runAllTimers();
		});

		const button = await screen.findByText(
			'create-page-from-this-template'
		);

		expect(button).toBeInTheDocument();
	});

	it('open creation modal when clicking on modal button', async () => {
		const user = renderComponent();

		await user.click(screen.getByTitle('preview-page-template'));

		await act(async () => {
			jest.runAllTimers();
		});

		const button = await screen.findByText(
			'create-page-from-this-template'
		);

		await user.click(button);

		expect(openModalMock).toBeCalled();
	});
});
jest.fn();
