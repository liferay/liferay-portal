/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import RoomsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/components/props_transformer/RoomsFDSPropsTransformer';

const mockOpenModal = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openModal: (...args: unknown[]) => mockOpenModal(...args),
	openToast: jest.fn(),
}));

describe('RoomsFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	const clickArchive = (maintenanceModeEnabled: boolean) => {
		const {onActionDropdownItemClick} = RoomsFDSPropsTransformer({
			additionalProps: {maintenanceModeEnabled},
			creationMenu: {},
			itemsActions: [],
			otherProps: {},
		} as any);

		onActionDropdownItemClick({
			action: {data: {id: 'archive'}},
			event: {preventDefault: jest.fn()} as any,
			itemData: {id: 1, name: 'Room Name'} as any,
			loadData: jest.fn(),
		});

		return mockOpenModal.mock.calls[0][0];
	};

	it('warns that archiving is not available when maintenance mode is disabled', () => {
		const {bodyHTML, buttons, status, title} = clickArchive(false);

		expect(mockOpenModal).toHaveBeenCalledTimes(1);

		expect(bodyHTML).toBe(
			'please-enable-the-site-level-maintenance-mode-feature-flag-to-archive-this-room'
		);
		expect(buttons).toHaveLength(1);
		expect(status).toBe('warning');
		expect(title).toBe('archiving-is-not-available');
	});

	it('asks to confirm the archive when maintenance mode is enabled', () => {
		const {bodyHTML, buttons} = clickArchive(true);

		expect(mockOpenModal).toHaveBeenCalledTimes(1);

		expect(bodyHTML).toBe('archive-digital-sales-room-confirmation-body');
		expect(buttons).toHaveLength(2);
	});
});
