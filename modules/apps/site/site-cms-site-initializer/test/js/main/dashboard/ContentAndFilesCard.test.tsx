/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {
	ContentAndFilesCard,
	IContentAndFilesCard,
	TrendClassification,
} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/ContentAndFilesCard';

describe('[CMS Dashboard] Components: ContentAndFilesCard', () => {
	const mockedProps: IContentAndFilesCard = {
		categories: 1,
		tags: 3,
		title: 'x-new-content-items',
		trend: {
			classification: TrendClassification.Neutral,
			percentage: 0,
		},
		vocabularies: 2,
	};

	it('renders correctly with given props', () => {
		render(<ContentAndFilesCard {...mockedProps} />);

		const Title = screen.getByText('x-new-content-items');
		expect(Title).toBeInTheDocument();

		const Trend = screen.getByText('x-vs-previous-period');
		expect(Trend).toBeInTheDocument();

		const VocabulariesBreakdown = screen.getByText('vocabularies');
		expect(VocabulariesBreakdown).toBeInTheDocument();

		const CategoriesBreakdown = screen.getByText('categories');
		expect(CategoriesBreakdown).toBeInTheDocument();

		const TagsBreakdown = screen.getByText('tags');
		expect(TagsBreakdown).toBeInTheDocument();
	});

	it('renders correctly with POSITIVE trend', () => {
		const mockedPropsWithPositiveTrend: IContentAndFilesCard = {
			...mockedProps,
			trend: {
				classification: TrendClassification.Positive,
				percentage: -42,
			},
		};

		render(<ContentAndFilesCard {...mockedPropsWithPositiveTrend} />);

		const Trend = screen.getByText('42%').parentElement;
		expect(Trend).toBeInTheDocument();
		expect(Trend).toHaveTextContent('42%');
		expect(Trend).toHaveClass('text-success');
	});

	it('renders correctly with NEGATIVE trend', () => {
		const mockedPropsWithPositiveTrend: IContentAndFilesCard = {
			...mockedProps,
			trend: {
				classification: TrendClassification.Negative,
				percentage: 42,
			},
		};

		render(<ContentAndFilesCard {...mockedPropsWithPositiveTrend} />);

		const Trend = screen.getByText('42%').parentElement;
		expect(Trend).toBeInTheDocument();
		expect(Trend).toHaveTextContent('42%');
		expect(Trend).toHaveClass('text-danger');
	});
});
