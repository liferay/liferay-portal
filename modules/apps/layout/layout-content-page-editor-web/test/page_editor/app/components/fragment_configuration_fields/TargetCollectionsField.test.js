/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';

import '@testing-library/jest-dom';
import React from 'react';

import {TargetCollectionsField} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/TargetCollectionsField';
import {StoreContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			portletNamespace: 'pageEditor',
		},
	})
);

const renderComponent = (
	{enableCompatibleCollections, onValueSelect, value} = {
		enableCompatibleCollections: false,
		onValueSelect: () => {},
		value: [],
	}
) => {
	const collectionItems = {
		['collection-display-a']: {
			config: {collection: {title: 'Collection A'}},
			itemId: 'collection-display-a',
			supportedFilters: ['categories'],
		},
		['collection-display-b']: {
			config: {collection: {title: 'Collection B'}},
			itemId: 'collection-display-b',
			supportedFilters: ['keywords'],
		},
	};

	return render(
		<StoreContextProvider
			initialState={{layoutData: {items: collectionItems}}}
			reducer={(state) => state}
		>
			<TargetCollectionsField
				enableCompatibleCollections={enableCompatibleCollections}
				filterableCollections={collectionItems}
				onValueSelect={onValueSelect}
				value={value}
			/>
		</StoreContextProvider>
	);
};

describe('TargetCollectionsField', () => {
	afterEach(cleanup);

	it('renders specified collections', () => {
		const {getByLabelText} = renderComponent();

		expect(getByLabelText('Collection A')).toBeInTheDocument();
		expect(getByLabelText('Collection B')).toBeInTheDocument();
	});

	it('marks specified collections as selected', () => {
		const {getByLabelText} = renderComponent({
			value: ['collection-display-a'],
		});

		expect(getByLabelText('Collection A')).toBeChecked();
		expect(getByLabelText('Collection B')).not.toBeChecked();
	});

	it('executes onValueSelect when some collection is selected', () => {
		const onValueSelect = jest.fn();

		const {getByLabelText} = renderComponent({
			onValueSelect,
			value: ['collection-display-a'],
		});

		fireEvent.click(getByLabelText('Collection B'));

		expect(onValueSelect).toHaveBeenCalledWith('targetCollections', [
			'collection-display-a',
			'collection-display-b',
		]);
	});

	it('only allow selecting compatible collections if enableCompatibleCollections is true', () => {
		const {getByLabelText} = renderComponent({
			enableCompatibleCollections: true,
			value: ['collection-display-a'],
		});

		expect(getByLabelText('Collection B')).toBeDisabled();
	});

	it('shows warning message if enableCompatibleCollections is true', () => {
		const {getByRole} = renderComponent({
			enableCompatibleCollections: true,
		});

		expect(getByRole('alert', {hidden: true})).toHaveTextContent(
			'multiple-selection-must-have-at-least-one-filter-in-common'
		);
	});
});
