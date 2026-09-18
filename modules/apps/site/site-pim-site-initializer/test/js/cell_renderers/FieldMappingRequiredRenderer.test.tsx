/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldMappingRequiredRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingRequiredRenderer';

describe('FieldMappingRequiredRenderer', () => {
	it('reads yes for a required channel field', () => {
		render(<FieldMappingRequiredRenderer value={true} />);

		expect(screen.getByText('yes')).toBeInTheDocument();
	});

	it('reads no for an optional channel field', () => {
		render(<FieldMappingRequiredRenderer value={false} />);

		expect(screen.getByText('no')).toBeInTheDocument();
	});
});
