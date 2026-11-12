/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {hasDisplayedVisualizationMode} from '../../src/main/resources/META-INF/resources/js/utils/getVisualizationModes';

const FIELDS = [{fieldName: 'name'}];

describe('hasDisplayedVisualizationMode', () => {
	it('does not display a data set without fields', () => {
		expect(hasDisplayedVisualizationMode({})).toBe(false);
	});

	it('displays a data set with fields in an enabled default mode', () => {
		expect(
			hasDisplayedVisualizationMode({
				dataSetToDataSetTableSections: FIELDS,
			})
		).toBe(true);
	});

	it('does not display a data set whose only mode with fields is disabled', () => {
		expect(
			hasDisplayedVisualizationMode({
				dataSetToDataSetCardsSections: FIELDS,
				visualizationModes: JSON.stringify([
					{enabled: false, name: 'cards'},
				]),
			})
		).toBe(false);
	});

	it('displays a data set with an enabled client extension mode', () => {
		expect(
			hasDisplayedVisualizationMode({
				visualizationModes: JSON.stringify([
					{enabled: true, name: 'LXC:timeline'},
				]),
			})
		).toBe(true);
	});

	it('falls back to the modes with fields when every default mode is disabled', () => {
		expect(
			hasDisplayedVisualizationMode({
				dataSetToDataSetCardsSections: FIELDS,
				visualizationModes: JSON.stringify([
					{enabled: false, name: 'cards'},
					{enabled: false, name: 'list'},
					{enabled: false, name: 'table'},
				]),
			})
		).toBe(true);
	});
});
