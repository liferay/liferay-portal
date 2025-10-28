/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ClauseContributorsSidebar from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/edit_sxp_blueprint/clause_contributors_sidebar/index';
import filterAndSortClassNames from '../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/functions/filter_and_sort_class_names';
const Toasts = require('../../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/utils/toasts');
import {mockClassNames} from '../../mocks/data';

import '@testing-library/jest-dom';

// Prevents "TypeError: Liferay.component is not a function" error on openToast

Toasts.openSuccessToast = jest.fn();

const keywordQueryContributors = filterAndSortClassNames(
	mockClassNames('KeywordQueryContributor')
);
const modelPrefilterContributors = filterAndSortClassNames(
	mockClassNames('ModelPrefilterContributor')
);
const queryPrefilterContributors = filterAndSortClassNames(
	mockClassNames('QueryPrefilterContributor')
);

function renderClause(props) {
	return render(
		<ClauseContributorsSidebar
			frameworkConfig={{
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: [],
			}}
			initialClauseContributorsList={[
				{
					label: 'KeywordQueryContributor',
					value: keywordQueryContributors,
				},
				{
					label: 'ModelPrefilterContributor',
					value: modelPrefilterContributors,
				},
				{
					label: 'QueryPrefilterContributor',
					value: queryPrefilterContributors,
				},
			]}
			onFrameworkConfigChange={jest.fn()}
			visible={false}
			{...props}
		/>
	);
}

describe('QueryBuilder', () => {
	it('renders the clause contributors sidebar', () => {
		const {container} = renderClause();

		expect(container).not.toBeNull();
	});

	it('renders the list of contributors', () => {
		const {getByText} = renderClause();

		keywordQueryContributors.forEach((className) => getByText(className));

		modelPrefilterContributors.forEach((className) => getByText(className));

		queryPrefilterContributors.forEach((className) => getByText(className));
	});

	it('enables the correct number of active contributors', () => {
		const activeKeywordContributors = mockClassNames(
			'KeywordQueryContributor',
			false,
			5
		);

		const {getAllByLabelText} = renderClause({
			frameworkConfig: {
				clauseContributorsExcludes: [],
				clauseContributorsIncludes: activeKeywordContributors,
			},
		});

		expect(getAllByLabelText('on').length).toEqual(
			activeKeywordContributors.length
		);
	});
});
