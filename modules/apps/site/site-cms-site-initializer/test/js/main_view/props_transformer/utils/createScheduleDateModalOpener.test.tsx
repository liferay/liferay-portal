/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';

import {openCMSModal} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import ScheduleDateModalContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/modal/ScheduleDateModalContent';
import {
	isScheduleDateActionId,
	openScheduleDateModal,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/createScheduleDateModalOpener';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/modal/ScheduleDateModalContent',
	() => ({__esModule: true, default: jest.fn(() => null)})
);

const ITEM = {
	embedded: {
		expirationDate: '2099-01-31T10:00:00Z',
		reviewDate: '2099-12-31T10:00:00Z',
	},
};

function getModalContentProps() {
	const {contentComponent} = (openCMSModal as jest.Mock).mock.calls[0][0];

	render(contentComponent({closeModal: jest.fn()}));

	return (ScheduleDateModalContent as unknown as jest.Mock).mock.calls[0][0];
}

describe('[CMS] openScheduleDateModal', () => {
	beforeEach(() => jest.clearAllMocks());

	it('recognizes only the schedule date actions', () => {
		expect(isScheduleDateActionId('update-expiration-date')).toBe(true);
		expect(isScheduleDateActionId('update-review-date')).toBe(true);
		expect(isScheduleDateActionId('download')).toBe(false);
		expect(isScheduleDateActionId()).toBe(false);
	});

	it('preloads the expiration date of the item', () => {
		openScheduleDateModal({
			actionId: 'update-expiration-date',
			itemData: ITEM,
		});

		expect(getModalContentProps()).toEqual(
			expect.objectContaining({
				date: ITEM.embedded.expirationDate,
				fieldName: 'expirationDate',
			})
		);
	});

	it('preloads the review date of the item', () => {
		openScheduleDateModal({
			actionId: 'update-review-date',
			itemData: ITEM,
		});

		expect(getModalContentProps()).toEqual(
			expect.objectContaining({
				date: ITEM.embedded.reviewDate,
				fieldName: 'reviewDate',
			})
		);
	});

	it('preloads no date for a selection', () => {
		openScheduleDateModal({
			actionId: 'update-review-date',
			selectedData: {items: [], selectAll: true},
		});

		expect(getModalContentProps().date).toBeUndefined();
	});
});
