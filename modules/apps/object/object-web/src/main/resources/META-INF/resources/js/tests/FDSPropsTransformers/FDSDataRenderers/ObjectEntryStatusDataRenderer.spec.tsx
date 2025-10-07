/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';

// @ts-ignore

import React from 'react';

import ObjectEntryStatusDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/ObjectEntryStatusDataRenderer';

const restContextPath = '/o/object-admin/v1.0';

const mockItemData = {
	actions: {},
	creator: {
		additionalName: '',
		contentType: '',
		familyName: '',
		givenName: '',
		id: 0,
		name: '',
	},
	dateCreated: '',
	dateModified: '',
	externalReferenceCode: 'erc',
	id: 1,
	name: '',
	status: {
		code: 0,
		label: 'approved',
		label_i18n: 'Approved',
	},
};

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

describe('The ObjectEntryStatusDataRenderer component', () => {
	it.each`
		className          | status                                                  | statusLabel
		${'label-success'} | ${{code: 0, label: 'approved', label_i18n: 'Approved'}} | ${'Approved'}
		${'label-warning'} | ${{code: 9, label: 'empty', label_i18n: 'Empty'}}       | ${'Empty'}
		${'label-warning'} | ${{code: 3, label: 'expired', label_i18n: 'Expired'}}   | ${'Expired'}
		${'label-info'}    | ${{code: 1, label: 'pending', label_i18n: 'Pending'}}   | ${'Pending'}
	`('renders with $statusLabel status', ({className, status}) => {
		render(
			<ObjectEntryStatusDataRenderer
				itemData={{...mockItemData, status}}
				restContextPath={restContextPath}
			/>
		);

		const [label] = document.getElementsByClassName(className);

		expect(label).toBeVisible();
		expect(label).toHaveTextContent(status.label_i18n);
	});

	it('renders with scheduled status and display date', () => {
		const itemData = {
			...mockItemData,
			displayDate: '2025-10-20T10:00:00Z',
			status: {
				code: 7,
				label: 'scheduled',
				label_i18n: 'Scheduled',
			},
		};

		render(
			<ObjectEntryStatusDataRenderer
				itemData={itemData}
				restContextPath={restContextPath}
			/>
		);

		const [scheduledLabel] = document.getElementsByClassName('label-info');

		expect(scheduledLabel).toBeVisible();
		expect(scheduledLabel).toHaveTextContent('Scheduled');

		expect(
			screen.getByTitle(
				'this-entry-will-be-published-on-Oct 20, 2025 10:00 AM'
			)
		).toBeVisible();
	});
});
