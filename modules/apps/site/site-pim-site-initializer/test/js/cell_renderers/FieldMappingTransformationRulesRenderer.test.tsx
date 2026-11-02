/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldMappingTransformationRulesRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingTransformationRulesRenderer';

describe('FieldMappingTransformationRulesRenderer', () => {
	it('shows two rules and counts the others', () => {
		render(
			<FieldMappingTransformationRulesRenderer
				value={['Strip HTML Tags', 'Trim Whitespace', 'URL Encode']}
			/>
		);

		expect(screen.getByText('Strip HTML Tags')).toBeInTheDocument();
		expect(screen.getByText('Trim Whitespace')).toBeInTheDocument();
		expect(screen.queryByText('URL Encode')).not.toBeInTheDocument();
		expect(screen.getByText('+1')).toBeInTheDocument();
	});
});
