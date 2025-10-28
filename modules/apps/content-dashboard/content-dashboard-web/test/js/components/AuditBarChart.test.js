/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AuditBarChart from '../../../src/main/resources/META-INF/resources/js/components/AuditGraphApp/AuditBarChart';

import '@testing-library/jest-dom';

// Mock needed due to a bug in ResponsiveContainer Recharts component
// See https://github.com/recharts/recharts/issues/2268

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}) => (
			<OriginalModule.ResponsiveContainer aspect={1} width={100}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

const mockOneVocabulary = [
	{
		key: 'business-decision-maker',
		name: 'Business Decision Maker',
		value: 987,
		vocabularyName: 'Audience',
	},
	{
		key: 'business-end-user',
		name: 'Business End User',
		value: 1095,
		vocabularyName: 'Audience',
	},
	{
		key: 'technical-decision-maker',
		name: 'Technical Decision Maker',
		value: 2020,
		vocabularyName: 'Audience',
	},
	{
		key: 'technical-end-user',
		name: 'Technical End User',
		value: 422,
		vocabularyName: 'Audience',
	},
];

const mockTwoVocabularies = [
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 478,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 1055,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 822,
				vocabularyName: 'Stage',
			},
		],
		key: 'business-decision-maker',
		name: 'Business Decision Maker',
		vocabularyName: 'Audience',
	},
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 125,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 1906,
				vocabularyName: 'Stage',
			},
			{
				key: 'solution',
				name: 'Solution',
				value: 987,
				vocabularyName: 'Stage',
			},
		],
		key: 'business-end-user',
		name: 'Business End User',
		vocabularyName: 'Audience',
	},
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 444,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 1733,
				vocabularyName: 'Stage',
			},
			{
				key: 'solution',
				name: 'Solution',
				value: 1807,
				vocabularyName: 'Stage',
			},
		],
		key: 'technical-decision-maker',
		name: 'Technical Decision Maker',
		vocabularyName: 'Audience',
	},
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 125,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 317,
				vocabularyName: 'Stage',
			},
			{
				key: 'solution',
				name: 'Solution',
				value: 187,
				vocabularyName: 'Stage',
			},
		],
		key: 'technical-end-user',
		name: 'Technical End User',
		vocabularyName: 'Audience',
	},
];

const mockTwoVocabulariesWithCategoriesInTheFirstVocabulary = [
	{
		key: 'business-decision-maker',
		name: 'Business Decision Maker',
		vocabularyName: 'Audience',
	},
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 125,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 317,
				vocabularyName: 'Stage',
			},
		],
		key: 'business-end-user',
		name: 'Business End User',
		vocabularyName: 'Audience',
	},
];

const mockTwoVocabulariesWithNoneCategory = [
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 478,
				vocabularyName: 'Stage',
			},
			{
				key: 'selection',
				name: 'Selection',
				value: 1055,
				vocabularyName: 'Stage',
			},
			{
				key: 'none',
				name: 'No Stage Specified',
				value: 822,
				vocabularyName: 'Stage',
			},
		],
		key: 'business-decision-maker',
		name: 'Business Decision Maker',
		vocabularyName: 'Audience',
	},
	{
		categories: [
			{
				key: 'education',
				name: 'Education',
				value: 125,
				vocabularyName: 'Stage',
			},
		],
		key: 'business-end-user',
		name: 'Business End User',
		vocabularyName: 'Audience',
	},
];

describe('AuditBarChart', () => {
	const {ResizeObserver} = window;

	beforeAll(() => {
		delete window.ResizeObserver;
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	afterAll(() => {
		cleanup();
		window.ResizeObserver = ResizeObserver;
		jest.restoreAllMocks();
	});

	it('renders audit bar chart from one vocabulary', () => {
		const {container, getByText} = render(
			<AuditBarChart
				namespace="demo_namespace"
				rtl={false}
				vocabularies={mockOneVocabulary}
			/>
		);

		expect(getByText('Audience')).toBeInTheDocument();

		expect(getByText('Business Decision Maker')).toBeInTheDocument();
		expect(getByText('Business End User')).toBeInTheDocument();
		expect(getByText('Technical Decision Maker')).toBeInTheDocument();
		expect(getByText('Technical End User')).toBeInTheDocument();

		const bars = container.getElementsByClassName(
			'recharts-layer recharts-bar-rectangle'
		);
		expect(bars.length).toBe(4);
	});

	it('renders audit bar chart from two vocabularies', () => {
		const {container, getByText} = render(
			<AuditBarChart
				namespace="demo_namespace"
				rtl={false}
				vocabularies={mockTwoVocabularies}
			/>
		);

		expect(getByText('Stage:')).toBeInTheDocument();
		expect(getByText('Education')).toBeInTheDocument();
		expect(getByText('Selection')).toBeInTheDocument();
		expect(getByText('Solution')).toBeInTheDocument();

		expect(getByText('Audience')).toBeInTheDocument();
		expect(getByText('Business Decision Maker')).toBeInTheDocument();
		expect(getByText('Business End User')).toBeInTheDocument();
		expect(getByText('Technical Decision Maker')).toBeInTheDocument();
		expect(getByText('Technical End User')).toBeInTheDocument();

		const bars = container.getElementsByClassName(
			'recharts-layer recharts-bar-rectangle'
		);
		expect(bars.length).toBe(12);
	});

	it('renders audit bar chart from two vocabularies without categories in the first one', () => {
		const {container, getByText} = render(
			<AuditBarChart
				namespace="demo_namespace"
				rtl={false}
				vocabularies={
					mockTwoVocabulariesWithCategoriesInTheFirstVocabulary
				}
			/>
		);

		expect(getByText('Stage:')).toBeInTheDocument();
		expect(getByText('Education')).toBeInTheDocument();
		expect(getByText('Selection')).toBeInTheDocument();

		expect(getByText('Audience')).toBeInTheDocument();
		expect(getByText('Business Decision Maker')).toBeInTheDocument();
		expect(getByText('Business End User')).toBeInTheDocument();

		const bars = container.getElementsByClassName(
			'recharts-layer recharts-bar-rectangle'
		);
		expect(bars.length).toBe(4);
	});

	it('renders audit bar chart from two vocabularies with none category', () => {
		const {container, getByText} = render(
			<AuditBarChart
				namespace="demo_namespace"
				rtl={false}
				vocabularies={mockTwoVocabulariesWithNoneCategory}
			/>
		);

		expect(getByText('Stage:')).toBeInTheDocument();
		expect(getByText('Education')).toBeInTheDocument();
		expect(getByText('Selection')).toBeInTheDocument();
		expect(getByText('No Stage Specified')).toBeInTheDocument();

		expect(getByText('Audience')).toBeInTheDocument();
		expect(getByText('Business Decision Maker')).toBeInTheDocument();
		expect(getByText('Business End User')).toBeInTheDocument();

		const bars = container.getElementsByClassName(
			'recharts-layer recharts-bar-rectangle'
		);
		expect(bars.length).toBe(6);
	});

	it.skip('renders audit bar chart only from checked categories from legend', () => {
		const {container, getByLabelText} = render(
			<AuditBarChart
				namespace="demo_namespace"
				rtl={false}
				vocabularies={mockTwoVocabularies}
			/>
		);

		const bars = container.getElementsByClassName(
			'recharts-layer recharts-bar-rectangle'
		);

		const educationCheckbox = getByLabelText('Education');
		const selectionCheckbox = getByLabelText('Selection');
		const solutionCheckbox = getByLabelText('Solution');

		userEvent.click(educationCheckbox);
		expect(educationCheckbox.checked).toEqual(false);
		expect(bars.length).toBe(8);

		userEvent.click(selectionCheckbox);
		expect(selectionCheckbox.checked).toEqual(false);
		expect(bars.length).toBe(4);

		userEvent.click(solutionCheckbox);
		expect(solutionCheckbox.checked).toEqual(false);
		expect(bars.length).toBe(0);
	});

	it.skip('renders audit bar chart message when there are no vocabularies selected', () => {
		const {getByLabelText, getByText} = render(
			<AuditBarChart
				namespace="demo_namespace"
				rtl={false}
				vocabularies={mockTwoVocabularies}
			/>
		);

		const educationCheckbox = getByLabelText('Education');
		const selectionCheckbox = getByLabelText('Selection');
		const solutionCheckbox = getByLabelText('Solution');

		userEvent.click(educationCheckbox);
		userEvent.click(selectionCheckbox);
		userEvent.click(solutionCheckbox);

		expect(
			getByText('there-are-no-categories-selected')
		).toBeInTheDocument();
		expect(
			getByText(
				'select-categories-from-the-checkboxes-in-the-legend-above'
			)
		).toBeInTheDocument();
	});
});
