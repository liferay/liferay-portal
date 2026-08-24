/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import MultiSelectManager from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/MultiSelectManager';
import {
	useActivateMultiSelect,
	useActiveItemIds,
	useMultiSelectType,
	useSelectItem,
} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext',
	() => {
		const activateMultiSelect = jest.fn();
		const selectItem = jest.fn();

		return {
			useActivateMultiSelect: () => activateMultiSelect,
			useActiveItemIds: jest.fn(() => []),
			useMultiSelectType: jest.fn(() => null),
			useSelectItem: () => selectItem,
		};
	}
);

const renderComponent = () =>
	render(
		<StoreMother.Component>
			<MultiSelectManager />
		</StoreMother.Component>
	);

describe('MultiSelectManager', () => {
	afterEach(() => {
		useMultiSelectType.mockImplementation(() => null);

		jest.clearAllMocks();
	});

	describe('Simple multiselect', () => {
		it('activates simple multiselect when pressing ctrl + click', () => {
			renderComponent();

			document.body.dispatchEvent(
				new MouseEvent('click', {
					ctrlKey: true,
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith('simple');
		});

		it('activates simple multiselect when pressing ctrl + "Enter"', () => {
			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keydown', {
					ctrlKey: true,
					key: 'Enter',
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith('simple');
		});

		it('activates simple multiselect when pressing ctrl + "Space"', () => {
			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keydown', {
					code: 'Space',
					ctrlKey: true,
					key: ' ',
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith('simple');
		});

		it('disable simple multiselect when the ctrl key is released', () => {
			useMultiSelectType.mockImplementation(() => 'simple');

			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keyup', {
					key: 'Control',
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith(null);
		});

		it('keeps simple multiselect when the shift key is released while ctrl is pressed', () => {
			useMultiSelectType.mockImplementation(() => 'simple');

			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keyup', {
					ctrlKey: true,
					key: 'Shift',
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).not.toBeCalled();
		});

		it('does not activate simple multiselect when pressing AltGr', () => {
			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keydown', {
					altKey: true,
					ctrlKey: true,
					key: '@',
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).not.toBeCalled();
		});
	});

	describe('Range multiselect', () => {
		it('activates range multiselect when pressing shift + click', () => {
			renderComponent();

			document.body.dispatchEvent(
				new MouseEvent('click', {
					ctrlKey: false,
					shiftKey: true,
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith('range');
		});

		it('activates range multiselect when pressing shift', () => {
			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keydown', {
					ctrlKey: false,
					key: 'ArrowUp',
					shiftKey: true,
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith('range');
		});

		it('does not activate range multiselect when typing with shift', () => {
			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keydown', {
					key: 'A',
					shiftKey: true,
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).not.toBeCalled();
		});

		it('disable range multiselect when the shift key is released', () => {
			useMultiSelectType.mockImplementation(() => 'range');

			renderComponent();

			document.body.dispatchEvent(
				new KeyboardEvent('keyup', {
					key: 'Shift',
				})
			);

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith(null);
		});
	});

	describe('Lost keyup', () => {
		it('disables multiselect on the next interaction without modifiers', () => {
			useMultiSelectType.mockImplementation(() => 'simple');

			renderComponent();

			document.body.dispatchEvent(new MouseEvent('mousedown'));

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith(null);
		});

		it('disables multiselect when the window loses the focus', () => {
			useMultiSelectType.mockImplementation(() => 'simple');

			renderComponent();

			window.dispatchEvent(new Event('blur'));

			const activateMultiSelect = useActivateMultiSelect();

			expect(activateMultiSelect).toBeCalledWith(null);
		});
	});

	it('deselects items when escape is pressed', () => {
		useActiveItemIds.mockImplementation(() => ['item-1', 'item-2']);

		renderComponent();

		document.body.dispatchEvent(
			new KeyboardEvent('keydown', {
				key: 'Escape',
			})
		);

		const selectItem = useSelectItem();

		expect(selectItem).toBeCalledWith(null);
	});
});
