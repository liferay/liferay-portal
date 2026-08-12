/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, waitFor} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';
import ResizeObserver from 'resize-observer-polyfill';

import RoomService from '../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import {IRoomInitializerProps} from '../../../src/main/resources/META-INF/resources/js/common/utils/types';
import RoomInitializer from '../../../src/main/resources/META-INF/resources/js/components/RoomInitializer';
import {setFieldValue} from '../utils';

global.ResizeObserver = ResizeObserver;

const renderComponent = ({
	closeModal,
	numberOfSteps = 1,
	siteTemplates = [],
}: IRoomInitializerProps) => {
	return render(
		<RoomInitializer
			closeModal={closeModal}
			numberOfSteps={numberOfSteps}
			siteTemplates={siteTemplates}
		></RoomInitializer>
	);
};

describe('RoomInitializer', () => {
	afterEach(() => {
		fetchMock.restore();
		jest.clearAllMocks();

		cleanup();
	});

	it('renders the correct UI elements', async () => {
		renderComponent({
			closeModal: jest.fn(),
			numberOfSteps: 1,
		});

		expect(
			screen.queryByRole('button', {name: 'cancel'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'save'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('heading', {
				name: 'create-new-digital-sales-room',
			})
		).toBeInTheDocument();
	});

	it('navigates between steps', async () => {
		fetchMock.get(/headless-admin-user\/.*\/accounts.*/i, () => {
			return {
				items: [
					{
						id: 100,
						name: 'account1',
					},
					{
						id: 101,
						name: 'account2',
					},
				],
			};
		});

		renderComponent({
			closeModal: jest.fn(),
			numberOfSteps: 2,
		});

		expect(
			screen.queryByRole('button', {name: 'back'})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'cancel'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'next'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'save'})
		).not.toBeInTheDocument();

		await setFieldValue(screen.getByTestId('selectAccountInput'), 'ac');

		await waitFor(() => {
			screen.getByRole('option', {name: 'A account1'}).click();
		});

		await waitFor(() => {
			screen.getByRole('button', {name: 'next'}).click();
		});

		await waitFor(
			() =>
				expect(
					screen.queryByTestId('selectAccountInput')
				).not.toBeInTheDocument(),
			{
				timeout: 1000,
			}
		);

		expect(
			screen.queryByRole('button', {name: 'back'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'cancel'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'next'})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'save'})
		).toBeInTheDocument();

		await waitFor(() => {
			screen.getByRole('button', {name: 'back'}).click();
		});

		expect(screen.queryByTestId('selectAccountInput')).toBeInTheDocument();
		expect(screen.queryByTestId('selectAccountInput')).toHaveValue(
			'account1'
		);
		expect(
			screen.queryByRole('button', {name: 'back'})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'cancel'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'next'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'save'})
		).not.toBeInTheDocument();
	});

	it('closes modal on cancel button', async () => {
		fetchMock.get(/headless-admin-user\/.*\/accounts.*/i, () => {
			return {
				items: [
					{
						id: 100,
						name: 'account1',
					},
					{
						id: 101,
						name: 'account2',
					},
				],
			};
		});

		const closeModal = jest.fn();

		renderComponent({
			closeModal,
			numberOfSteps: 1,
		});

		screen.getByRole('button', {name: 'cancel'}).click();

		expect(closeModal).toBeCalledTimes(1);
	});

	it('calls API on save button with all the steps', async () => {
		fetchMock.get(/headless-admin-user\/.*\/accounts.*/i, () => {
			return {
				items: [
					{
						id: 100,
						name: 'account1',
					},
					{
						id: 101,
						name: 'account2',
					},
				],
			};
		});

		const spyOnAddRoom = jest.spyOn(RoomService, 'addRoom');

		renderComponent({
			closeModal: jest.fn(),
			numberOfSteps: 3,
			siteTemplates: [
				{
					description: 'description1',
					friendlyURL: 'friendlyURL1',
					name: 'name1',
					uuid: 'uuid1',
				},
				{
					description: 'description2',
					friendlyURL: 'friendlyURL2',
					name: 'name2',
					uuid: 'uuid2',
				},
			],
		});

		await setFieldValue(screen.getByTestId('selectAccountInput'), 'ac');

		expect(
			screen.getByRole('option', {name: 'A account1'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'A account2'})
		).toBeInTheDocument();

		await waitFor(() => {
			screen.getByRole('option', {name: 'A account1'}).click();
		});

		await waitFor(() => {
			screen.getByRole('button', {name: 'next'}).click();
		});

		expect(screen.getByTestId(`template_uuid1`)).toBeInTheDocument();
		expect(screen.getByTestId(`template_uuid2`)).toBeInTheDocument();

		await waitFor(() => {
			screen.getByTestId(`template_uuid1`).click();
		});

		await waitFor(() => {
			expect(
				screen.getByTestId('templatePreviewFrame')
			).toBeInTheDocument();
		});

		await waitFor(() => {
			screen.getByRole('button', {name: 'next'}).click();
		});

		await waitFor(() => {
			expect(screen.getByTestId('roomNameInput')).toBeInTheDocument();
		});

		await setFieldValue(
			screen.getByTestId('roomNameInput'),
			'testRoomName'
		);
		await setFieldValue(
			screen.getByTestId('friendlyURLInput'),
			'testFriendlyURL'
		);

		await waitFor(() => {
			screen.getByRole('button', {name: 'save'}).click();
		});

		expect(spyOnAddRoom).toBeCalledWith({
			accountEntryId: 100,
			friendlyURL: 'testFriendlyURL',
			name: 'testRoomName',
			siteTemplateKey: 'uuid1',
		});
	});
});
