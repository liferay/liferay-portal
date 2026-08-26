/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ViewTags from '../../../../../src/main/resources/META-INF/resources/js/main_view/categorization/tags/ViewTags';

const mockFrontendDataSet = jest.fn((_props: any) => null);

jest.mock('@liferay/frontend-data-set-web', () => ({
	...(jest.requireActual('@liferay/frontend-data-set-web') as any),
	FrontendDataSet: (props: any) => mockFrontendDataSet(props),
}));

const defaultProps = {
	actionItems: [] as any,
	cmsGroupId: 1,
	dataSetId: 'tags',
	invalidTagCharacters: '',
	tagUsagesURL: '',
	tagsURL: '',
	vocabulariesURL: '',
};

describe('[CMS Categorization] Components: ViewTags', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-99403'] = true;
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('does not pass a Project filter to FrontendDataSet when CMP is disabled', () => {
		render(<ViewTags {...defaultProps} cmpEnabled={false} />);

		const [{filters}] = mockFrontendDataSet.mock.calls[0] as any;

		expect(
			filters.find((filter: any) => filter.id === 'projects')
		).toBeUndefined();
	});

	it('passes a project filter to FrontendDataSet when CMP is enabled', () => {
		render(<ViewTags {...defaultProps} cmpEnabled />);

		const [{filters}] = mockFrontendDataSet.mock.calls[0] as any;

		const projectFilter = filters.find(
			(filter: any) => filter.id === 'projects'
		);

		expect(projectFilter?.apiURL).toContain(
			"objectDefinitionExternalReferenceCode eq 'l_cmp_project'"
		);
	});

	it('passes a Space-typed asset library filter to FrontendDataSet', () => {
		render(<ViewTags {...defaultProps} />);

		const [{filters}] = mockFrontendDataSet.mock.calls[0] as any;

		const spaceFilter = filters.find(
			(filter: any) => filter.id === 'groupIds'
		);

		expect(spaceFilter?.apiURL).toContain("filter=type eq 'Space'");
	});

	it('searches as the user types and offers search suggestions', () => {
		render(<ViewTags {...defaultProps} />);

		const [props] = mockFrontendDataSet.mock.calls[0] as any;

		expect(props.searchAsYouType).toBe(true);
		expect(props.searchSuggestionsEnabled).toBe(true);
	});
});
