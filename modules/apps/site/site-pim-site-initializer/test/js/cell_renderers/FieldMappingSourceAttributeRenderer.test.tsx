/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldMappingSourceAttributeRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingSourceAttributeRenderer';

describe('FieldMappingSourceAttributeRenderer', () => {
	it('shows a chip for every mapped source attribute', () => {
		render(
			<FieldMappingSourceAttributeRenderer value={['Code', 'Name']} />
		);

		expect(screen.getByText('Code')).toBeInTheDocument();
		expect(screen.getByText('Name')).toBeInTheDocument();
	});

	it('counts the source attributes it cannot fit', () => {
		render(
			<FieldMappingSourceAttributeRenderer
				value={['a', 'b', 'c', 'd', 'e']}
			/>
		);

		expect(screen.getByText('a')).toBeInTheDocument();
		expect(screen.getByText('c')).toBeInTheDocument();
		expect(screen.queryByText('d')).not.toBeInTheDocument();
		expect(screen.getByText('+2')).toBeInTheDocument();
	});

	it('shows nothing when the channel field is not mapped', () => {
		const {container} = render(
			<FieldMappingSourceAttributeRenderer value={[]} />
		);

		expect(container).toBeEmptyDOMElement();
	});
});
