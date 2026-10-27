/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import Configuration from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/edit_sxp_blueprint/configuration_tab/index';

import '@testing-library/jest-dom';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/CodeMirrorEditor',
	() =>
		({onChange, value}) => (
			<textarea
				aria-label="text-area"
				onChange={onChange}
				value={value}
			/>
		)
);

function renderBuilder(props) {
	return render(
		<Configuration
			advancedConfig="{}"
			aggregationConfig="{}"
			frameworkConfig={{
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: ['*'],
				searchableAssetTypes: [],
			}}
			highlightConfig="{}"
			indexConfig={{
				external: false,
				indexName: '',
			}}
			parameterConfig="{}"
			searchIndexes={[]}
			setFieldTouched={jest.fn()}
			setFieldValue={jest.fn()}
			touched={{}}
			{...props}
		/>
	);
}

describe('Configuration', () => {
	global.URL.createObjectURL = jest.fn();

	it('renders the builder', () => {
		const {container} = renderBuilder();

		expect(container).not.toBeNull();
	});

	it('renders the enable as a collection provider toggle but not legacy return type asset toggle', () => {
		const {getByText, queryByText} = renderBuilder({
			frameworkConfig: {
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: ['*'],
				collectionProvider: true,
				collectionProviderType:
					'com.liferay.asset.kernel.model.AssetEntry',
				searchableAssetTypes: [],
			},
		});

		getByText('enable-as-a-collection-provider');

		expect(
			queryByText(
				'enable-as-a-collection-provider-with-return-type-asset'
			)
		).not.toBeInTheDocument();
	});

	it('renders the legacy return type asset toggle', () => {
		const {getByText} = renderBuilder({
			frameworkConfig: {
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: ['*'],
				collectionProvider: true,
				collectionProviderType:
					'com.liferay.asset.kernel.model.AssetEntry',
				legacyAssetCollectionProvider: true,
				searchableAssetTypes: [],
			},
		});

		getByText('enable-as-a-collection-provider-with-return-type-asset');
	});

	describe('missing type reference warning', () => {
		const className = 'com.liferay.object.model.ObjectDefinition#A1B2';

		function renderWarning({
			collectionProvider = true,
			legacyAssetCollectionProvider = false,
			searchableAssetTypes = [className],
			searchableTypes,
		}) {
			return renderBuilder({
				frameworkConfig: {
					clauseContributorsExcludes: [],
					clauseContributorsIncludes: ['*'],
					collectionProvider,
					collectionProviderType: className,
					legacyAssetCollectionProvider,
					searchableAssetTypes,
				},
				searchableTypes,
			});
		}

		it('renders when the object definition type is missing', () => {
			const {getByText} = renderWarning({searchableTypes: []});

			getByText('missing-type-reference-collection-provider-warning');
		});

		it('does not render when the object definition type exists', () => {
			const {queryByText} = renderWarning({
				searchableTypes: [{className, displayName: 'Object'}],
			});

			expect(
				queryByText(
					'missing-type-reference-collection-provider-warning'
				)
			).not.toBeInTheDocument();
		});

		it('does not render when the collection provider is disabled', () => {
			const {queryByText} = renderWarning({
				collectionProvider: false,
				searchableTypes: [],
			});

			expect(
				queryByText(
					'missing-type-reference-collection-provider-warning'
				)
			).not.toBeInTheDocument();
		});

		it('does not render when the legacy asset collection provider is enabled', () => {
			const {queryByText} = renderWarning({
				legacyAssetCollectionProvider: true,
				searchableTypes: [],
			});

			expect(
				queryByText(
					'missing-type-reference-collection-provider-warning'
				)
			).not.toBeInTheDocument();
		});

		it('does not render when the missing type is not an object definition', () => {
			const {queryByText} = renderWarning({
				searchableAssetTypes: [
					'com.liferay.journal.model.JournalArticle',
				],
				searchableTypes: [],
			});

			expect(
				queryByText(
					'missing-type-reference-collection-provider-warning'
				)
			).not.toBeInTheDocument();
		});

		it('does not render when the searchable types have different class names', () => {
			const {queryByText} = renderWarning({
				searchableAssetTypes: [
					className,
					'com.liferay.object.model.ObjectDefinition#C3D4',
				],
				searchableTypes: [],
			});

			expect(
				queryByText(
					'missing-type-reference-collection-provider-warning'
				)
			).not.toBeInTheDocument();
		});
	});
});
