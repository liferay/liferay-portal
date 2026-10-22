/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import MappingStatusRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/MappingStatusRenderer';

describe('MappingStatusRenderer', () => {
	it('labels a complete mapping as complete', () => {
		render(<MappingStatusRenderer value={true} />);

		expect(
			screen.getByText('mapping-complete').closest('.label')
		).toHaveClass('label-success');
	});

	it('labels an incomplete mapping as incomplete', () => {
		render(<MappingStatusRenderer value={false} />);

		expect(screen.getByText('incomplete').closest('.label')).toHaveClass(
			'label-warning'
		);
	});
});
