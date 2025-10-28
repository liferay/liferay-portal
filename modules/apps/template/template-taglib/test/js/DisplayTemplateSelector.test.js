/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import DisplayTemplateSelector, {
	getOptionData,
} from '../../src/main/resources/META-INF/resources/js/DisplayTemplateSelector';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	getOpener: jest.fn(() => ({
		Liferay: {
			fire: jest.fn(),
		},
	})),
}));

const DEFAULT_PROPS = {
	displayStyle: 'IconValue',
	displayStyleGroupId: 1,
	displayStyleGroupKey: 'IconGroupKey',
	items: [
		{
			items: [
				{
					groupId: 20119,
					groupKey: '68468904643977',
					label: 'Icon',
					value: 'ddmTemplate_LANGUAGE-ICON-FTL',
				},
				{
					groupId: 20120,
					groupKey: '43412343123142',
					label: 'Icon Menu',
					value: 'ddmTemplate_LANGUAGE-ICON-MENU-FTL',
				},
				{
					groupId: 20121,
					groupKey: '43445647432322',
					label: 'Long Text',
					value: 'ddmTemplate_LANGUAGE-LONG-TEXT-FTL',
				},
			],
			label: 'Global',
		},
	],
};

const selectTemplate = (optionValue) => {
	fireEvent.click(screen.getByLabelText('display-template'));

	act(() => {
		fireEvent.click(
			screen.getByText(optionValue, {
				selector: '[role="option"]',
			})
		);
	});
};

function renderComponent() {
	render(
		<DisplayTemplateSelector namespace="namespace" props={DEFAULT_PROPS} />
	);
}

describe('DisplayTemplateSelector', () => {
	it('calls Liferay.fire with correct value when selecting a template', async () => {
		renderComponent();

		selectTemplate('Icon Menu');

		expect(Liferay.fire).toBeCalledWith(
			'templateSelector:changedTemplate',
			{value: 'ddmTemplate_LANGUAGE-ICON-MENU-FTL'}
		);

		selectTemplate('Long Text');

		expect(Liferay.fire).toBeCalledWith(
			'templateSelector:changedTemplate',
			{value: 'ddmTemplate_LANGUAGE-LONG-TEXT-FTL'}
		);
	});
});

describe('getOptionData', () => {
	it('returns correct values when they include hyphens', async () => {
		const OPTION_WITH_HYPHEN = 'groupId-1__groupKey-2__value-3-4-5';

		expect(getOptionData(OPTION_WITH_HYPHEN)).toStrictEqual({
			groupId: 'groupId-1',
			groupKey: 'groupKey-2',
			value: 'value-3-4-5',
		});
	});
});
