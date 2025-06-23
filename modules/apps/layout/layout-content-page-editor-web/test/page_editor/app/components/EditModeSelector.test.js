/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import togglePermissions from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/togglePermission';
import EditModeSelector from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/EditModeSelector';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/actions/togglePermission',
	() => jest.fn()
);

const INITIAL_STATE = {
	permissions: {
		LOCKED_SEGMENTS_EXPERIMENT: false,
		SWITCH_EDIT_MODE: true,
	},
};

const mockDispatch = jest.fn((a) => {
	if (typeof a === 'function') {
		return a(mockDispatch);
	}
});

const renderComponent = (state = INITIAL_STATE) => {
	return render(
		<StoreMother.Component dispatch={mockDispatch} getState={() => state}>
			<EditModeSelector />;
		</StoreMother.Component>
	);
};

const selectContentEditingOption = async (option) => {
	await userEvent.click(option);

	expect(mockDispatch).toHaveBeenCalledWith(togglePermissions());
	expect(togglePermissions).toHaveBeenCalledWith(
		'UPDATE_LAYOUT_LIMITED',
		false
	);
};

const selectPageDesignOption = async (option) => {
	await userEvent.click(option);

	expect(mockDispatch).toHaveBeenCalledWith(togglePermissions());
	expect(togglePermissions).toHaveBeenCalledWith(
		'UPDATE_LAYOUT_LIMITED',
		true
	);
};

describe('EditModeSelector', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders EditModeSelector component', () => {
		renderComponent();

		expect(screen.getByText('page-design')).toBeInTheDocument();
	});

	it('renders EditModeSelector component and make sure it has tooltip and aria-label', () => {
		renderComponent();

		expect(
			screen.getAllByLabelText(
				'select-edit-mode.-current-edit-mode-page-design'
			)
		).toHaveLength(2);
		expect(screen.getByTitle('select-edit-mode')).toBeInTheDocument();
	});

	it('disables the selectors when the LOCKED_SEGMENTS_EXPERIMENT and SWITCH_EDIT_MODE permissions are "true" and "false" respectively', () => {
		renderComponent({
			permissions: {
				LOCKED_SEGMENTS_EXPERIMENT: true,
				SWITCH_EDIT_MODE: false,
			},
		});

		const selectors = screen.getAllByRole('combobox');

		selectors.forEach((selector) => {
			expect(selector).toBeDisabled();
		});
	});

	describe('Desktop selector', () => {
		it('calls mockDispatch and togglePermissions with its corresponding parameters when Content Editing option is selected', async () => {
			renderComponent();

			await userEvent.click(screen.getByText('page-design'));

			const option = screen.getByRole('option', {
				name: 'content-editing',
			});

			await waitFor(() => selectContentEditingOption(option));
		});

		it('calls mockDispatch and togglePermissions with its corresponding parameters when Page Design option is selected', async () => {
			renderComponent();

			await userEvent.click(screen.getByText('page-design'));

			const option = screen.getByRole('option', {name: 'page-design'});

			await waitFor(() => selectPageDesignOption(option));
		});
	});

	describe('Mobile selector', () => {
		it('calls mockDispatch and togglePermissions with its corresponding parameters when Content Editing option is selected', async () => {
			renderComponent();

			await userEvent.click(screen.getByTitle('select-edit-mode'));

			const option = screen.getByRole('option', {
				name: 'content-editing',
			});

			await waitFor(() => selectContentEditingOption(option));
		});

		it('calls mockDispatch and togglePermissions with its corresponding parameters when Page Design option is selected', async () => {
			renderComponent();

			await userEvent.click(screen.getByTitle('select-edit-mode'));

			const option = screen.getByRole('option', {name: 'page-design'});

			await waitFor(() => selectPageDesignOption(option));
		});
	});
});
