/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import React from 'react';

import FieldMappingsBreadcrumb from '../../src/main/resources/META-INF/resources/js/FieldMappingsBreadcrumb';

const mockBreadcrumb = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	Breadcrumb: (props: any) => {
		mockBreadcrumb(props);

		return null;
	},
}));

const ACTION_ITEMS = [
	{label: 'edit'},
	{id: 'execute', label: 'execute'},
	{label: 'schedule'},
	{label: 'delete'},
];

function getActionItemLabels() {
	const [props] = mockBreadcrumb.mock.calls.at(-1);

	return props.actionItems.map(({label}: {label: string}) => label);
}

describe('FieldMappingsBreadcrumb', () => {
	let handlers: Record<string, Function>;

	beforeEach(() => {
		jest.clearAllMocks();

		handlers = {};

		Liferay.detach = jest.fn();
		Liferay.on = jest.fn((name: string, handler: Function) => {
			handlers[name] = handler;
		}) as any;
	});

	it('keeps execute while the mapping is complete', () => {
		render(
			<FieldMappingsBreadcrumb
				actionItems={ACTION_ITEMS}
				breadcrumbItems={[]}
				mappingStatus="complete"
			/>
		);

		expect(getActionItemLabels()).toEqual([
			'edit',
			'execute',
			'schedule',
			'delete',
		]);

		const [props] = mockBreadcrumb.mock.calls.at(-1);

		expect(props.actionItems[1]).not.toHaveProperty('id');
	});

	it('hides execute once a field mapping change leaves the mapping incomplete', () => {
		render(
			<FieldMappingsBreadcrumb
				actionItems={ACTION_ITEMS}
				breadcrumbItems={[]}
				mappingStatus="complete"
			/>
		);

		act(() => {
			handlers['pimConnectorFieldMappings:change']({
				mappingStatus: 'incomplete',
			});
		});

		expect(getActionItemLabels()).toEqual(['edit', 'schedule', 'delete']);
	});

	it('stops listening when it unmounts', () => {
		const {unmount} = render(
			<FieldMappingsBreadcrumb
				actionItems={ACTION_ITEMS}
				breadcrumbItems={[]}
				mappingStatus="complete"
			/>
		);

		unmount();

		expect(Liferay.detach).toHaveBeenCalledWith(
			'pimConnectorFieldMappings:change',
			handlers['pimConnectorFieldMappings:change']
		);
	});
});
