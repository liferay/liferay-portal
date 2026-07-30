/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, waitFor} from '@testing-library/react';
import React from 'react';

import ContentGapMatrixHeader from '../../js/components/content_gap_matrix/ContentGapMatrixHeader';
import {
	EMPTY_MATRIX,
	FULL_COVERAGE_MATRIX,
	PARTIAL_COVERAGE_MATRIX,
} from '../../js/components/content_gap_matrix/services/fixtures';

const mockGetObjectFields = jest.fn();
const mockGetSpaces = jest.fn();
const mockRenderAIAssistantTriggerButton = jest.fn();

jest.mock('@liferay/ai-hub-cell-js-components-web', () => ({
	AIAssistantTriggerButton: (props: {label: string}) => {
		mockRenderAIAssistantTriggerButton(props);

		return require('react').createElement('button', null, props.label);
	},
	getObjectFields: (externalReferenceCode: string) =>
		mockGetObjectFields(externalReferenceCode),
	getSpaces: () => mockGetSpaces(),
}));

describe('ContentGapMatrixHeader', () => {
	beforeEach(() => {
		mockGetObjectFields.mockResolvedValue({items: []});
		mockGetSpaces.mockResolvedValue([]);
	});

	afterEach(() => {
		Liferay.FeatureFlags['LPD-62272'] = false;

		jest.clearAllMocks();
	});

	it('colors the coverage badge secondary at partial coverage', () => {
		const {container} = render(
			<ContentGapMatrixHeader data={PARTIAL_COVERAGE_MATRIX} />
		);

		const label = container.querySelector(
			'.lfr-cmp__content-gap-matrix-header-stats .label'
		);

		expect(label).toHaveClass('label-inverse-secondary');
	});

	it('colors the coverage badge success at full coverage', () => {
		const {container} = render(
			<ContentGapMatrixHeader data={FULL_COVERAGE_MATRIX} />
		);

		const label = container.querySelector(
			'.lfr-cmp__content-gap-matrix-header-stats .label'
		);

		expect(label).toHaveClass('label-inverse-success');
	});

	it('colors the coverage badge warning at zero coverage', () => {
		const {container} = render(
			<ContentGapMatrixHeader data={EMPTY_MATRIX} />
		);

		const label = container.querySelector(
			'.lfr-cmp__content-gap-matrix-header-stats .label'
		);

		expect(label).toHaveClass('label-inverse-warning');
	});

	it('gives the AI assistant the project, its content type, and the available spaces', async () => {
		Liferay.FeatureFlags['LPD-62272'] = true;

		const objectFields = [
			{
				businessType: 'Text',
				name: 'title',
				readOnly: 'false',
				required: true,
			},
			{
				businessType: 'RichText',
				name: 'content',
				readOnly: 'false',
				required: false,
			},
		];

		mockGetObjectFields.mockResolvedValue({items: objectFields});

		mockGetSpaces.mockResolvedValue([
			{
				externalReferenceCode: 'MARKETING_ERC',
				id: 1,
				name: 'Marketing',
				siteId: 20123,
			},
			{
				externalReferenceCode: 'SUPPORT_ERC',
				id: 2,
				name: 'Support',
				siteId: 20456,
			},
		]);

		render(
			<ContentGapMatrixHeader
				cmpProjectObjectEntryId="42"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		expect(mockGetObjectFields).toHaveBeenCalledWith(
			'L_CMS_BASIC_WEB_CONTENT'
		);

		await waitFor(() => {
			const [{getContext}] =
				mockRenderAIAssistantTriggerButton.mock.calls.at(-1);

			expect(getContext()).toEqual({
				focusScope: 'full-matrix',
				objectDefinitionName: 'CMSBasicWebContent',
				objectFields,
				projectId: '42',
				projectScopeKey: undefined,
				spacesJSONArray: [
					{
						externalReferenceCode: 'MARKETING_ERC',
						id: '20123',
						label: 'Marketing',
					},
					{
						externalReferenceCode: 'SUPPORT_ERC',
						id: '20456',
						label: 'Support',
					},
				],
			});
		});
	});

	it('passes the project scope key to the AI insights trigger context', () => {
		Liferay.FeatureFlags['LPD-62272'] = true;

		render(
			<ContentGapMatrixHeader
				cmpProjectObjectEntryId="42"
				cmpProjectScopeKey="my-project-scope"
				data={PARTIAL_COVERAGE_MATRIX}
			/>
		);

		const [{getContext}] =
			mockRenderAIAssistantTriggerButton.mock.calls.at(-1);

		expect(getContext()).toEqual(
			expect.objectContaining({
				projectId: '42',
				projectScopeKey: 'my-project-scope',
			})
		);
	});

	it('replaces the critical gaps count with "No Assets Found" when the project has no assets', () => {
		const {getByText, queryByText} = render(
			<ContentGapMatrixHeader data={EMPTY_MATRIX} />
		);

		expect(getByText('no-assets-found')).toBeInTheDocument();
		expect(
			queryByText('x-critical-gaps', {exact: false})
		).not.toBeInTheDocument();
		expect(getByText('x-covered')).toBeInTheDocument();
	});

	it('shows only the title (no badges) when no data is provided, as in the unconfigured project state', () => {
		const {getByText, queryByText} = render(<ContentGapMatrixHeader />);

		expect(getByText('content-coverage-matrix')).toBeInTheDocument();
		expect(
			queryByText('x-covered', {exact: false})
		).not.toBeInTheDocument();
		expect(
			queryByText('x-critical-gaps', {exact: false})
		).not.toBeInTheDocument();
	});

	it('shows the critical gaps count when the project has assets', () => {
		const {getByText, queryByText} = render(
			<ContentGapMatrixHeader data={PARTIAL_COVERAGE_MATRIX} />
		);

		expect(
			getByText('x-critical-gaps', {exact: false})
		).toBeInTheDocument();
		expect(queryByText('no-assets-found')).not.toBeInTheDocument();
	});

	it('renders the AI insights trigger when the feature flag is enabled', () => {
		Liferay.FeatureFlags['LPD-62272'] = true;

		const {getByText} = render(
			<ContentGapMatrixHeader data={PARTIAL_COVERAGE_MATRIX} />
		);

		expect(getByText('get-ai-insights')).toBeInTheDocument();
	});
});
