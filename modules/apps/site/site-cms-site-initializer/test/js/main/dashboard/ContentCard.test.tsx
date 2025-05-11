/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {ContentCard} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/ContentCard';

describe('[CMS Dashboard] Components: ContentCard', () => {
	it('renders correctly', () => {
		render(<ContentCard />);

		const Title = screen.getByText('CONTENT');
		expect(Title).toBeInTheDocument();

		const Description = screen.getByText(
			'this-metric-calculates-the-total-amount-of-content-assets-created-in-your-spaces'
		);
		expect(Description).toBeInTheDocument();

		const [RangeSelectorDropdown, ActionMenu] =
			screen.getAllByRole('button');
		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');
		expect(ActionMenu).toBeInTheDocument();

		/**
		 * This must be uncommented after implementing ContentCard link
		 *
		 * const MainMetric = screen.getByRole('link', {
		 *  name: 'x-new-content-items',
		 * });
		 * expect(MainMetric).toBeInTheDocument();
		 */

		const MainMetric = screen.getByText('x-new-content-items');
		expect(MainMetric).toBeInTheDocument();

		const Comparison = screen.getByText('x-vs-previous-period');
		expect(Comparison).toBeInTheDocument();

		const VocabulariesBreakdown = screen.getByText('vocabularies');
		expect(VocabulariesBreakdown).toBeInTheDocument();

		const CategoriesBreakdown = screen.getByText('categories');
		expect(CategoriesBreakdown).toBeInTheDocument();

		const TagsBreakdown = screen.getByText('tags');
		expect(TagsBreakdown).toBeInTheDocument();
	});
});
