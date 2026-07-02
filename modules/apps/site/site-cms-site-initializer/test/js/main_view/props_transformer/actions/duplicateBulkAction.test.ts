/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import duplicateBulkAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/duplicateBulkAction';

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
}));

function getModalConfig() {
	return (openModal as jest.Mock).mock.calls[0][0];
}

describe('duplicateBulkAction', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('shows the singular confirmation for one selected item', () => {
		duplicateBulkAction({
			selectedData: {items: [{}], selectAll: false} as any,
		});

		const {bodyHTML, title} = getModalConfig();

		expect(title).toBe('duplicate-items');
		expect(bodyHTML).toContain('duplicate-item-confirmation');
	});

	it('shows the plural confirmation for several selected items', () => {
		duplicateBulkAction({
			selectedData: {items: [{}, {}, {}], selectAll: false} as any,
		});

		const {bodyHTML, title} = getModalConfig();

		expect(title).toBe('duplicate-items');
		expect(bodyHTML).toContain('duplicate-x-items-confirmation');
	});

	it('shows the all-items confirmation for a select-all selection', () => {
		duplicateBulkAction({
			selectedData: {items: [], selectAll: true} as any,
		});

		expect(getModalConfig().bodyHTML).toContain(
			'duplicate-all-items-confirmation'
		);
	});

	it('triggers the bulk action only after confirming', () => {
		const fireSpy = jest.spyOn(Liferay, 'fire');

		duplicateBulkAction({
			selectedData: {items: [{}], selectAll: false} as any,
		});

		expect(fireSpy).not.toHaveBeenCalled();

		const processClose = jest.fn();

		getModalConfig()
			.buttons.find((button: any) => button.label === 'duplicate')
			.onClick({processClose});

		expect(processClose).toHaveBeenCalledTimes(1);
		expect(fireSpy).toHaveBeenCalledTimes(1);

		fireSpy.mockRestore();
	});
});
