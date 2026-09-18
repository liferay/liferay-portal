/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FieldMappingStatusRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/FieldMappingStatusRenderer';

describe('FieldMappingStatusRenderer', () => {
	it('labels a mapped channel field as mapped', () => {
		render(
			<FieldMappingStatusRenderer
				itemData={{required: true}}
				value={true}
			/>
		);

		expect(screen.getByText('mapped')).toBeInTheDocument();
	});

	it('warns when a required channel field has no source', () => {
		render(
			<FieldMappingStatusRenderer
				itemData={{required: true}}
				value={false}
			/>
		);

		expect(screen.getByText('no-source-data-required')).toBeInTheDocument();
	});

	it('stays neutral when an optional channel field has no source', () => {
		render(
			<FieldMappingStatusRenderer
				itemData={{required: false}}
				value={false}
			/>
		);

		expect(screen.getByText('no-source-data')).toBeInTheDocument();
	});
});
