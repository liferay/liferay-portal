/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import openModuleModal from '../../src/main/resources/META-INF/resources/js/openModuleModal';

const mockLoadModule = jest.fn();
const mockOpenModal = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openModal: (props: any) => mockOpenModal(props),
}));

jest.mock('frontend-js-web', () => ({
	loadModule: (module: string) => mockLoadModule(module),
}));

const MODULE_ITEM = {
	module: 'http://localhost/foo-web',
	moduleProps: {name: 'foo'},
};

describe('openModuleModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('opens the loaded module in a modal', async () => {
		let resolveLoadModule: (component: unknown) => void = () => {};

		mockLoadModule.mockReturnValue(
			new Promise((resolve) => {
				resolveLoadModule = resolve;
			})
		);

		const opened = openModuleModal(MODULE_ITEM);

		expect(mockLoadModule).toHaveBeenCalledWith(MODULE_ITEM.module);
		expect(mockOpenModal).not.toHaveBeenCalled();

		resolveLoadModule(
			({closeModal, name}: {closeModal: () => void; name: string}) => (
				<button onClick={closeModal}>{name}</button>
			)
		);

		await opened;

		expect(mockOpenModal).toHaveBeenCalledTimes(1);

		const closeModal = jest.fn();
		const ContentComponent =
			mockOpenModal.mock.calls[0][0].contentComponent;

		render(<ContentComponent closeModal={closeModal} />);

		await userEvent.click(screen.getByRole('button', {name: 'foo'}));

		expect(closeModal).toHaveBeenCalledTimes(1);
	});

	it('opens no modal when the module fails to load', async () => {
		const consoleError = jest
			.spyOn(console, 'error')
			.mockImplementation(() => {});

		mockLoadModule.mockRejectedValue(new Error('unreachable'));

		await openModuleModal(MODULE_ITEM);

		expect(mockOpenModal).not.toHaveBeenCalled();
		expect(consoleError).toHaveBeenCalled();

		consoleError.mockRestore();
	});
});
