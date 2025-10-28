/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import {StoreContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import PageContents from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_content/components/PageContents';

const contents = {
	Collection: [
		{
			classPK: '39694',
			subtype: 'Web Content Article - Basic Web Content',
			title: 'Collection1',
			type: 'Collection',
		},
	],
	Document: [
		{
			actions: {},
			classPK: '39615',
			subtype: 'Basic Document',
			title: 'mountain.png',
			type: 'Document',
		},
	],
	['Inline text']: [
		{
			config: {},
			defaultValue: 'Heading Example',
			editableId: '39835-element-text',
			en_US: 'This is a inline text',
			title: 'This is a inline text',
		},
	],
	['Web Content Article']: [
		{
			actions: {},
			classPK: '39807',
			subtype: 'Basic Web Content',
			title: 'WC1',
			type: 'Web Content Article',
		},
		{
			actions: {},
			classPK: '39808',
			subtype: 'Basic Web Content',
			title: 'WC2',
			type: 'Web Content Article',
		},
	],
};

const selectOption = async (option) => {
	const dropdown = screen.getByRole('combobox');

	await userEvent.click(dropdown, {
		advanceTimers: jest.advanceTimersByTime,
	});

	const dropdownItems = document.querySelectorAll('.dropdown-item');

	fireEvent.click(dropdownItems[option]);
};

const renderPageContents = ({pageContents = contents} = {}) =>
	render(
		<StoreContextProvider
			initialState={{
				permissions: {UPDATE: true, UPDATE_LAYOUT_CONTENT: true},
			}}
		>
			<PageContents pageContents={pageContents} />
		</StoreContextProvider>
	);

describe('PageContent', () => {
	afterEach(() => {
		jest.useFakeTimers();
	});

	it('shows properly the list of page contents', () => {
		renderPageContents();

		Object.entries(contents).forEach(([key, values]) => {
			values.forEach((value) => {
				expect(screen.getByText(value.title)).toBeInTheDocument();
			});
			expect(screen.getAllByText(key)[0]).toBeInTheDocument();
		});
	});

	it('filters content according to a input value', async () => {
		renderPageContents();
		const input = screen.getByLabelText('search-content');

		await act(async () => {
			await userEvent.type(input, 'WC', {
				advanceTimers: jest.advanceTimersByTime,
			});

			jest.runAllTimers();
		});

		expect(screen.queryByText('WC1')).toBeInTheDocument();
		expect(screen.queryByText('WC2')).toBeInTheDocument();
		expect(
			screen.queryByText('This is a inline text')
		).not.toBeInTheDocument();
		expect(screen.queryByText('mountain.png')).not.toBeInTheDocument();
	});

	it('filters content according to a type value: Collections', async () => {
		renderPageContents();

		await selectOption(1);

		expect(screen.queryByText('Collection1')).toBeInTheDocument();
		expect(screen.queryByText('mountain.png')).not.toBeInTheDocument();
		expect(
			screen.queryByText('This is a inline text')
		).not.toBeInTheDocument();
		expect(screen.queryByText('WC1')).not.toBeInTheDocument();
	});

	it('filters content according to a type value: Document', async () => {
		renderPageContents();

		await selectOption(2);

		expect(screen.queryByText('mountain.png')).toBeInTheDocument();
		expect(screen.queryByText('Collection1')).not.toBeInTheDocument();
		expect(
			screen.queryByText('This is a inline text')
		).not.toBeInTheDocument();
		expect(screen.queryByText('WC1')).not.toBeInTheDocument();
	});

	it('filters content according to a type value: Inline Text', async () => {
		renderPageContents();

		await selectOption(3);

		expect(screen.queryByText('This is a inline text')).toBeInTheDocument();
		expect(screen.queryByText('mountain.png')).not.toBeInTheDocument();
		expect(screen.queryByText('Collection1')).not.toBeInTheDocument();
		expect(screen.queryByText('WC1')).not.toBeInTheDocument();
	});

	it('filters content according to a type value: Web Content', async () => {
		renderPageContents();

		await selectOption(4);

		expect(screen.queryByText('WC1')).toBeInTheDocument();
		expect(
			screen.queryByText('This is a inline text')
		).not.toBeInTheDocument();
		expect(screen.queryByText('mountain.png')).not.toBeInTheDocument();
		expect(screen.queryByText('Collection1')).not.toBeInTheDocument();
	});
});
