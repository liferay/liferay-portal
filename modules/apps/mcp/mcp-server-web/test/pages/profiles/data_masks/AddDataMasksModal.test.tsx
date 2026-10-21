/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import AddDataMasksModal from '../../../../src/main/resources/META-INF/resources/js/pages/profiles/data_masks/AddDataMasksModal';

import type {
	DataMask,
	DataMaskTypeKey,
} from '../../../../src/main/resources/META-INF/resources/js/types/DataMask';

const PROFILE_ERC = 'PROFILE_ERC';

function createDataMask(
	key: DataMaskTypeKey,
	name: string,
	externalReferenceCode: string
): DataMask {
	return {
		detectionRegex: '\\d+',
		externalReferenceCode,
		maskType: {key, name: key === 'system' ? 'System' : 'Custom'},
		name,
		replacementValue: '[X]',
	};
}

const systemAndCustomDataMasks = [
	createDataMask('system', 'Email Address', 'SYSTEM_EMAIL'),
	createDataMask('system', 'Phone Number', 'SYSTEM_PHONE'),
	createDataMask('custom', 'Project Codename', 'CUSTOM_CODENAME'),
];

function renderModal({
	dataMasks = systemAndCustomDataMasks,
	onAdded = jest.fn(),
	onClose = jest.fn(),
} = {}) {
	const {container} = render(
		<AddDataMasksModal
			dataMasks={dataMasks}
			nextExecutionOrder={1}
			onAdded={onAdded}
			onClose={onClose}
			profileExternalReferenceCode={PROFILE_ERC}
		/>
	);

	return {container, onAdded, onClose};
}

function checkbox(name: string) {
	return screen.getByRole('checkbox', {name});
}

function search(value: string) {
	return userEvent.type(
		screen.getByRole('searchbox', {name: 'search'}),
		value
	);
}

describe('AddDataMasksModal', () => {
	beforeAll(() => {
		const style = document.createElement('style');

		style.textContent = '.d-none { display: none !important; }';

		document.head.appendChild(style);
	});

	it('shows next to each group how many masks it holds', () => {
		renderModal();

		expect(
			within(screen.getByRole('treeitem', {name: 'System'})).getByText(
				'2'
			)
		).toBeVisible();
		expect(
			within(screen.getByRole('treeitem', {name: 'Custom'})).getByText(
				'1'
			)
		).toBeVisible();
		expect(checkbox('System')).toHaveAccessibleDescription('2-items');
		expect(checkbox('Custom')).toHaveAccessibleDescription('1-item');
		expect(checkbox('Email Address')).not.toHaveAccessibleDescription();
	});

	it('highlights the count of a group once every mask in it is selected', async () => {
		renderModal();

		const badge = () =>
			within(screen.getByRole('treeitem', {name: 'System'}))
				.getByText('2')
				.closest('.badge');

		await userEvent.click(checkbox('Email Address'));

		expect(badge()).toHaveClass('badge-secondary');

		await userEvent.click(checkbox('Phone Number'));

		expect(badge()).toHaveClass('badge-primary');
	});

	it('hides the masks that do not match the query and keeps their group', async () => {
		renderModal();

		await search('email');

		expect(checkbox('Email Address')).toBeVisible();
		expect(checkbox('System')).toBeVisible();
		expect(
			screen.queryByRole('checkbox', {name: 'Phone Number'})
		).toBeNull();
		expect(screen.queryByRole('checkbox', {name: 'Custom'})).toBeNull();
	});

	it('selects and adds the masks hidden by the filter when their group is checked', async () => {
		const {onAdded} = renderModal();

		await search('email');

		await userEvent.click(checkbox('System'));

		expect(screen.getByText('2-items-selected')).toBeInTheDocument();

		fetch.mockResponse(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: 'add'}));

		await waitFor(() => expect(onAdded).toHaveBeenCalledTimes(1));

		expect(
			fetch.mock.calls.map(([, request]) =>
				JSON.parse(String(request?.body))
			)
		).toEqual([
			expect.objectContaining({
				dataMaskExternalReferenceCode: 'SYSTEM_EMAIL',
			}),
			expect.objectContaining({
				dataMaskExternalReferenceCode: 'SYSTEM_PHONE',
			}),
		]);
	});

	it('keeps the selection when the query is cleared', async () => {
		renderModal();

		await search('email');

		await userEvent.click(checkbox('System'));

		await userEvent.clear(screen.getByRole('searchbox', {name: 'search'}));

		expect(checkbox('Phone Number')).toBeChecked();
		expect(screen.getByText('2-items-selected')).toBeInTheDocument();
	});

	it('announces that nothing matches the query', async () => {
		renderModal();

		await search('nothing-like-this');

		expect(screen.getByText('no-results-found')).toBeInTheDocument();
		expect(
			screen.queryByRole('checkbox', {name: 'Email Address'})
		).toBeNull();
		expect(screen.queryByText('no-data-masks-were-found')).toBeNull();
	});

	it('offers no search and no selection bar when there are no masks', () => {
		renderModal({dataMasks: []});

		expect(
			screen.getByText('no-data-masks-were-found')
		).toBeInTheDocument();
		expect(screen.queryByRole('searchbox', {name: 'search'})).toBeNull();
		expect(screen.queryByText('nothing-selected')).toBeNull();
	});

	it('has no accessibility violations with a group selected', async () => {
		const {container} = renderModal();

		await userEvent.click(checkbox('System'));

		await checkAccessibility({
			bestPractices: true,
			context: container,
		});
	});

	it('has no accessibility violations under a filter', async () => {
		const {container} = renderModal();

		await search('email');

		await checkAccessibility({bestPractices: true, context: container});
	});
});
