/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import openCreationModal from '../../src/main/resources/META-INF/resources/js/openCreationModal';

const mockLoadModule = jest.fn();
const mockOpenModal = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openModal: (props: any) => mockOpenModal(props),
}));

jest.mock('frontend-js-web', () => ({
	loadModule: (module: string) => mockLoadModule(module),
}));

const CREATION_ITEM = {
	id: 'add-foo',
	label: 'new-foo',
	module: 'http://localhost/foo-web',
	moduleProps: {name: 'foo'},
};

function renderModalContent() {
	const ContentComponent = mockOpenModal.mock.calls[0][0].contentComponent;

	return render(<ContentComponent closeModal={() => {}} />);
}

describe('openCreationModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('loads the module before opening the modal', async () => {
		let resolveLoadModule: (component: unknown) => void = () => {};

		mockLoadModule.mockReturnValue(
			new Promise((resolve) => {
				resolveLoadModule = resolve;
			})
		);

		const opened = openCreationModal(CREATION_ITEM);

		expect(mockLoadModule).toHaveBeenCalledWith(CREATION_ITEM.module);
		expect(mockOpenModal).not.toHaveBeenCalled();

		resolveLoadModule(() => <div />);

		await opened;

		expect(mockOpenModal).toHaveBeenCalledTimes(1);
	});

	it('renders the loaded module with its module props', async () => {
		mockLoadModule.mockResolvedValue(({name}: {name: string}) => (
			<div>{name}</div>
		));

		await openCreationModal(CREATION_ITEM);

		renderModalContent();

		expect(screen.getByText('foo')).toBeInTheDocument();
	});

	it('passes closeModal to the loaded module', async () => {
		const closeModal = jest.fn();

		mockLoadModule.mockResolvedValue(
			({closeModal}: {closeModal: () => void}) => (
				<button onClick={closeModal}>close</button>
			)
		);

		await openCreationModal(CREATION_ITEM);

		const ContentComponent =
			mockOpenModal.mock.calls[0][0].contentComponent;

		render(<ContentComponent closeModal={closeModal} />);

		screen.getByText('close').click();

		expect(closeModal).toHaveBeenCalled();
	});

	it('opens no modal when the module fails to load', async () => {
		const consoleError = jest
			.spyOn(console, 'error')
			.mockImplementation(() => {});

		mockLoadModule.mockRejectedValue(new Error('unreachable'));

		await openCreationModal(CREATION_ITEM);

		expect(mockOpenModal).not.toHaveBeenCalled();
		expect(consoleError).toHaveBeenCalled();

		consoleError.mockRestore();
	});
});
