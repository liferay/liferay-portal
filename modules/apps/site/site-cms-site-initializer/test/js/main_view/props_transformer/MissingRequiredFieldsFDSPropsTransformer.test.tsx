/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import MissingRequiredFieldsFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/MissingRequiredFieldsFDSPropsTransformer';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: jest.fn(() => ({data: {id: 'edit'}, href: '/edit/{id}'})),
	replaceTokens: jest.fn(() => '/edit/1'),
	useFDSRecordVisit: () => jest.fn(),
}));

const LANGUAGE_KEYS: Record<string, string> = {
	'x-missing-required-field': '{0} Missing Required Field',
	'x-missing-required-fields': '{0} Missing Required Fields',
};

const TITLE = 'Webinar Landing Page';

describe('[CMS Missing Required Data] MissingRequiredFieldsFDSPropsTransformer', () => {
	const transform = () =>
		MissingRequiredFieldsFDSPropsTransformer({
			additionalProps: {},
			id: 'com.liferay.site.cms.site.initializer-missingRequiredFieldsSection',
		}) as any;

	const renderTitle = (itemData: object) => {
		const [{component: TitleRenderer}] =
			transform().customRenderers.listSection;

		render(
			<TitleRenderer
				actions={[{data: {id: 'edit'}, href: '/edit/{id}'}]}
				itemData={{title: TITLE, ...itemData}}
				value={TITLE}
			/>
		);
	};

	beforeEach(() => {
		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('sorts the missing required fields assets by title', () => {
		expect(transform().sorts).toEqual([
			{active: true, direction: 'asc', key: 'title', label: 'title'},
		]);
	});

	it('links the title with the update permission', () => {
		renderTitle({actions: {update: {}}, missingRequiredFieldsCount: 1});

		expect(screen.getByRole('link', {name: TITLE})).toBeInTheDocument();
	});

	it('does not link the title without the update permission', () => {
		renderTitle({missingRequiredFieldsCount: 1});

		expect(screen.queryByRole('link', {name: TITLE})).toBeNull();
	});

	it.each([
		[1, '1 Missing Required Field'],
		[3, '3 Missing Required Fields'],
	])(
		'counts %i missing required fields in the singular or the plural',
		(missingRequiredFieldsCount, subtitle) => {
			renderTitle({missingRequiredFieldsCount});

			expect(screen.getByText(subtitle)).toBeInTheDocument();
		}
	);
});
