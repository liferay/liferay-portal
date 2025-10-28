/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {DisplayPageItemContextualSidebar} from '../../src/main/resources/META-INF/resources/js/index';

const DEFAULT_LANGUAGE_ID = 'en_US';

const LOCALES = [
	{
		id: 'en_US',
		label: 'en-US',
		symbol: 'en-us',
	},
	{
		id: 'es_ES',
		label: 'es-ES',
		symbol: 'es-es',
	},
];

const renderContextualSidebar = ({
	hasDisplayPage = true,
	item = {},
	localizedNames = {},
	subtype,
	type = '',
	useCustomName = false,
}) => {
	return render(
		<DisplayPageItemContextualSidebar
			chooseItemProps={{}}
			defaultLanguageId={DEFAULT_LANGUAGE_ID}
			hasDisplayPage={hasDisplayPage}
			item={item}
			itemSubtype={subtype}
			itemType={type}
			locales={LOCALES}
			localizedNames={localizedNames}
			namespace="namespace"
			useCustomName={useCustomName}
		/>
	);
};

describe('DisplayPageItemContextualSidebar', () => {
	it('renders name input disabled if custom name is disabled', () => {
		renderContextualSidebar({useCustomName: false});

		const nameInput = screen.getByLabelText('name');

		expect(nameInput).toHaveAttribute('disabled');
	});

	it('renders name input enabled if custom name is enabled', () => {
		renderContextualSidebar({useCustomName: true});

		const nameInput = screen.getByLabelText('name');

		expect(nameInput).not.toHaveAttribute('disabled');
	});

	it('renders name input with translated custom name for the default language when custom name is enabled', () => {
		const localizedNames = {
			[DEFAULT_LANGUAGE_ID]: 'localized-name',
		};

		renderContextualSidebar({localizedNames, useCustomName: true});

		const nameInput = screen.getByLabelText('name');

		expect(nameInput).toHaveValue('localized-name');
	});

	it('renders type and subtype', () => {
		renderContextualSidebar({subtype: 'item-subtype', type: 'item-type'});

		expect(screen.getByText('item-type')).toBeInTheDocument();
		expect(screen.getByText('item-subtype')).toBeInTheDocument();
	});

	it('renders extra data if it is not empty', () => {
		const data = [
			{
				title: 'Vocabulary',
				value: 'Animals',
			},
			{
				title: 'Site',
				value: 'Liferay',
			},
		];

		renderContextualSidebar({item: {data}});

		data.forEach(({title, value}) => {
			expect(screen.getByText(title)).toBeInTheDocument();
			expect(screen.getByText(value)).toBeInTheDocument();
		});
	});

	it('renders feedback message when item does not have display page', () => {
		renderContextualSidebar({hasDisplayPage: false});

		expect(screen.getByText('no-display-page')).toBeInTheDocument();
	});
});
