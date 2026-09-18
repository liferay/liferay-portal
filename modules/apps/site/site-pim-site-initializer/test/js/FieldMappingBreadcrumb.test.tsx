/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldMappingBreadcrumb from '../../src/main/resources/META-INF/resources/js/FieldMappingBreadcrumb';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	Breadcrumb: ({
		actionItems = [],
		breadcrumbItems,
	}: {
		actionItems: {href: string; label: string}[];
		breadcrumbItems: {label: string}[];
	}) => (
		<>
			<ul>
				{breadcrumbItems.map(({label}) => (
					<li key={label}>{label}</li>
				))}
			</ul>

			<ul>
				{actionItems.map(({href, label}) => (
					<li key={label}>
						<a href={href}>{label}</a>
					</li>
				))}
			</ul>
		</>
	),
}));

describe('FieldMappingBreadcrumb', () => {
	it('renders the breadcrumb items it receives', () => {
		render(
			<FieldMappingBreadcrumb
				breadcrumbItems={[
					{href: '/connectors', label: 'Connectors'},
					{active: true, label: 'Ushio Commerce'},
				]}
				hideSpace
				size="lg"
			/>
		);

		expect(screen.getByText('Connectors')).toBeInTheDocument();
		expect(screen.getByText('Ushio Commerce')).toBeInTheDocument();
	});

	it('offers the edit action next to the connector name', () => {
		render(
			<FieldMappingBreadcrumb
				actionItems={[
					{href: '/edit-connector?objectEntryId=42', label: 'edit'},
				]}
				breadcrumbItems={[{active: true, label: 'Ushio Commerce'}]}
			/>
		);

		expect(screen.getByRole('link', {name: 'edit'})).toHaveAttribute(
			'href',
			'/edit-connector?objectEntryId=42'
		);
	});
});
