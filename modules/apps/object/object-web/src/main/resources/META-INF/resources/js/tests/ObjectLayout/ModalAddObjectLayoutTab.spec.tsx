/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useResource} from '@clayui/data-provider';
import {act, cleanup, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {ModalAddObjectLayoutTab} from '../../components/Layout/LayoutScreen/ModalAddObjectLayoutTab';
import {LayoutContextProvider} from '../../components/Layout/objectLayoutContext';

jest.mock('@clayui/data-provider', () => {
	const originalModule = jest.requireActual('@clayui/data-provider');

	return {
		...originalModule,
		useResource: jest.fn(),
	};
});

const mockObserver = {
	disconnect: () => {},
	dispatch: () => {},
	mutation: [true, true] as [boolean, boolean],
	observe: () => {},
};

const mockOnClose = jest.fn();

const TestComponent = ({
	initialState = {
		creationLanguageId: 'en-US',
		enableCategorization: false,
		enableFriendlyURLCustomization: false,
		isViewOnly: true,
		objectDefinitionExternalReferenceCode: 'test',
		objectFieldBusinessTypes: [],
		objectFields: [],
		objectLayout: {
			objectLayoutTabs: [{id: 'existing-tab'}],
		},
		objectLayoutId: '123',
		objectRelationships: [],
	},
	onClose = mockOnClose,
	observer = mockObserver,
}) => {
	return (
		<LayoutContextProvider value={initialState}>
			<ModalAddObjectLayoutTab observer={observer} onClose={onClose} />
		</LayoutContextProvider>
	);
};

describe('ModalAddObjectLayoutTab', () => {
	const resizeObserverMock = {
		disconnect: () => {},
		observe: () => {},
		unobserve: () => {},
	};

	window.ResizeObserver = jest
		.fn()
		.mockImplementation(() => resizeObserverMock);

	afterEach(cleanup);

	afterEach(() => {
		(Liferay.ThemeDisplay.getPathContext as jest.Mock).mockReturnValue('/');
	});

	it('calls onClose when the cancel button is clicked', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		fireEvent.click(screen.getByText('cancel'));
		expect(mockOnClose).toHaveBeenCalled();
	});

	it('does not show the relationship autocomplete when the "Fields" tab is active', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		expect(
			screen.queryByLabelText('relationships' + 'mandatory')
		).not.toBeInTheDocument();
	});

	it('renders the modal with the correct title', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		expect(screen.getByText('add-tab')).toBeInTheDocument();
	});

	it('renders the "Fields" tab as active by default', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		const fieldsTab = screen
			.getByText('fields')
			.closest('.layout-tab__tab-types');
		expect(fieldsTab).toHaveClass('active');
	});

	it('searches for relationships via API with the correct search parameter', () => {
		(useResource as jest.Mock).mockReturnValue({
			resource: {items: []},
		});

		render(<TestComponent />);

		const relationshipsTab = screen.getByText('relationships');
		fireEvent.click(relationshipsTab);

		const autocomplete = screen.getByLabelText(
			'relationship' + 'mandatory'
		);
		fireEvent.change(autocomplete, {target: {value: 'my-test'}});

		expect(useResource).toHaveBeenLastCalledWith(
			expect.objectContaining({
				variables: expect.objectContaining({search: 'my-test'}),
			})
		);
	});

	it('requests relationships with the context path prefixed', () => {
		(Liferay.ThemeDisplay.getPathContext as jest.Mock).mockReturnValue(
			'/myportal'
		);
		(useResource as jest.Mock).mockReturnValue({resource: null});

		render(<TestComponent />);

		expect(useResource).toHaveBeenCalledWith(
			expect.objectContaining({
				link: 'http://localhost:8080/myportal/o/object-admin/v1.0/object-definitions/by-external-reference-code/test/object-relationships',
			})
		);
	});

	it('shows a validation error if the label is empty on submit', async () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		fireEvent.click(screen.getByText('save'));

		await act(async () => {
			expect(await screen.findByText('required')).toBeInTheDocument();
		});
	});

	it('shows a validation error if the relationship is not selected on submit', async () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		const relationshipsTab = screen.getByText('relationships');
		fireEvent.click(relationshipsTab);

		fireEvent.change(screen.getByLabelText('label' + 'mandatory'), {
			target: {value: 'New Tab'},
		});

		fireEvent.click(screen.getByText('save'));

		await act(async () => {
			expect(await screen.findByText('required')).toBeInTheDocument();
		});
	});

	it('shows the relationship autocomplete when the "Relationships" tab is active', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		render(<TestComponent />);

		const relationshipsTab = screen.getByText('relationships');

		fireEvent.click(relationshipsTab);

		expect(
			screen.getByLabelText('relationship' + 'mandatory')
		).toBeInTheDocument();
	});
});
