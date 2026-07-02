/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import expireEntriesBulkAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/expireEntriesBulkAction';

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
}));

function getModalConfig() {
	return (openModal as jest.Mock).mock.calls[0][0];
}

describe('expireEntriesBulkAction', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('shows the singular confirmation for one selected item', () => {
		expireEntriesBulkAction({
			selectedData: {items: [{}], selectAll: false} as any,
		});

		const {bodyHTML, title} = getModalConfig();

		expect(title).toBe('expire-items');
		expect(bodyHTML).toContain('expire-item-confirmation');
	});

	it('shows the plural confirmation for several selected items', () => {
		expireEntriesBulkAction({
			selectedData: {items: [{}, {}, {}], selectAll: false} as any,
		});

		const {bodyHTML, title} = getModalConfig();

		expect(title).toBe('expire-items');
		expect(bodyHTML).toContain('expire-x-items-confirmation');
	});

	it('shows the all-items confirmation for a select-all selection', () => {
		expireEntriesBulkAction({
			selectedData: {items: [], selectAll: true} as any,
		});

		expect(getModalConfig().bodyHTML).toContain(
			'expire-all-items-confirmation'
		);
	});

	it('triggers the bulk action only after confirming', () => {
		const fireSpy = jest.spyOn(Liferay, 'fire');

		expireEntriesBulkAction({
			selectedData: {items: [{}], selectAll: false} as any,
		});

		expect(fireSpy).not.toHaveBeenCalled();

		const processClose = jest.fn();

		getModalConfig()
			.buttons.find((button: any) => button.label === 'expire')
			.onClick({processClose});

		expect(processClose).toHaveBeenCalledTimes(1);
		expect(fireSpy).toHaveBeenCalledTimes(1);

		fireSpy.mockRestore();
	});
});
