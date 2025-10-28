/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {IssueDetail} from '../../../../src/main/resources/META-INF/resources/js/components/ItemDetail';

const MOCK_ISSUES = {
	aspectRatio: {
		description: 'Page displays images with incorrect aspect ratio.',
		failingElements: [
			{
				actualAspectRatio: 'element-1-actual-aspect-ratio',
				displayedAspectRatio: 'element-1-displayed-aspect-ratio',
				url: 'element-1-url',
			},
			{
				actualAspectRatio: 'element-2-actual-aspect-ratio',
				displayedAspectRatio: 'element-2-displayed-aspect-ratio',
				url: 'element-2-url',
			},
		],
		key: 'incorrect-image-aspect-ratios',
		tips: 'Incorrect image aspect ratios can be caused by...',
		title: 'Incorrect image aspect ratios',
		total: '2',
	},

	canonicalURL: {
		description:
			'When multiple pages have similar content, search engines...',
		failingElements: [
			{
				content: 'If the problem is that the canonical URL does...',
			},
		],
		key: 'invalid-canonical-url',
		tips: 'In a Liferay site, canonical URLs are automatically generated...',
		title: 'Invalid Canonical URL',
		total: '1',
	},

	contrastRatio: {
		description: 'Background and foreground colors',
		failingElements: [
			{
				node: {
					explanation: 'Fix any of the following...',
					nodeLabel: 'color-contrast',
				},
			},
		],
		key: 'low-contrast-ratio',
		tips: 'Text that is 1pt, or 14pt and bold, needs a contrast ratio of 3',
		title: 'Low Contrast Ratio',
		total: '1',
	},

	crawlableLink: {
		description: 'Search engines may use href attributes...',
		failingElements: [
			{
				node: {
					nodeLabel: 'crawlable-anchors',
					snippet: '<a>',
				},
			},
		],
		key: 'not-all-links-are-crawlable',
		tips: 'Google can follow links only if they are...',
		title: 'Not All Links Are Crawlable',
		total: '1',
	},

	fontSize: {
		description: 'Many search engines rank pages based on how mobile...',
		failingElements: [
			{
				fontSize: '2px',
				selector: {
					snippet: '<span style="font-size: 2px;">',
				},
			},
			{
				fontSize: '3px',
				selector: {
					snippet: '<span style="font-size: 3px;">',
				},
			},
			{
				fontSize: '4px',
				selector: {
					snippet: '<span style="font-size: 4px;">',
				},
			},
		],
		key: 'illegible-font-sizes',
		tips: 'This issue occurs when 60% or more of the text...',
		title: 'Illegible Font Sizes',
		total: '3',
	},

	hreflang: {
		description: 'Many sites provide different versions of a page',
		failingElements: [
			{
				source: {
					nodeLabel: 'link',
					snippet: '<link href="https://example.com" />',
				},
			},
			{
				source: {
					nodeLabel: 'link2',
					snippet: '<link href="https://example2.com" />',
				},
			},
		],
		key: 'invalid-hreflang',
		tips: 'In a Liferay site, hreflang links are automatically generated.',
		title: 'Invalid Hreflang',
		total: '2',
	},

	imgAlt: {
		description: 'Informative elements should aim for short text.',
		failingElements: [
			{
				node: {
					nodeLabel: 'img',
					snippet: '<img src="https://pixy.org/src/17/173316.jpg">',
				},
			},
		],
		key: 'missing-img-alt-attributes',
		tips: 'Image ALT attributes can be added.',
		title: 'Missing Img ALT Attributes',
		total: '1',
	},

	inputAlt: {
		description: 'When an image is being used as a button...',
		failingElements: [
			{
				node: {
					nodeLabel: 'input',
					snippet: '<input type="image">',
				},
			},
		],
		key: 'missing-input-alt-attributes',
		tips: 'Input ALT attributes can be added in the fragment editor.',
		title: 'Missing Input ALT Attributes',
		total: '1',
	},

	linkText: {
		description: 'Link text is the clickable word or phrase in a hyperlink',
		failingElements: [
			{
				href: 'https://www.google.com/',
				text: 'Click here',
			},
		],
		key: 'link-texts',
		tips: 'Make link texts more descriptive',
		title: 'Link Texts',
		total: '1',
	},

	metaDescription: {
		description: 'The meta description provides a summary of a page',
		failingElements: [
			{
				content: 'Add a description from the configuration',
			},
		],
		key: 'missing-meta-description',
		tips: 'Meta description can be added in the configuration',
		title: 'Missing Meta Description',
		total: '1',
	},

	pageIndexing: {
		description: 'Search engines are unable to include your page',
		failingElements: [
			{
				content:
					'Change the robots.txt setting from the configuration section of this page.',
			},
		],
		key: 'page-blocked-from-indexing',
		tips: 'noindex specification for robots can be changed',
		title: 'Page Blocked from Indexing',
		total: '1',
	},

	tapTarget: {
		description: 'Tap targets are the areas of a web page',
		failingElements: [
			{
				overlappingTarget: {
					nodeLabel: 'Overlapping Targget label',
				},
				size: '4x4',
				tapTarget: {
					snippet:
						'<button onclick="alert(\'hola\')" style="padding: 0; width: 1px; height: 1px">',
				},
			},
		],
		key: 'small-tap-targets',
		tips: 'Targets that are smaller than 48px',
		title: 'Small Tap Targets',
		total: '1',
	},

	titleElement: {
		description: 'Search engine users rely on the title',
		failingElements: [
			{
				content:
					'Add a title from the configuration section of this page.',
			},
		],
		key: 'missing-title-element',
		tips: 'Element is automatically generated from the asset title',
		title: 'Missing <title> Element',
		total: '1',
	},
};

const renderIssueDetail = (issue) => render(<IssueDetail issue={issue} />);

const checkContentOfElement = (issue) => {
	const {getByText} = renderIssueDetail(issue);

	expect(getByText(issue.description)).toBeInTheDocument();
	expect(getByText(issue.tips)).toBeInTheDocument();

	issue.failingElements.forEach((element) => {
		Object.keys(element).forEach((key) =>
			expect(getByText(element[key])).toBeInTheDocument()
		);
	});
};

const checkContentOfElementWithNode = (issue) => {
	const {getByText} = renderIssueDetail(issue);

	expect(getByText(issue.description)).toBeInTheDocument();
	expect(getByText(issue.tips)).toBeInTheDocument();

	issue.failingElements.forEach((element) => {
		Object.keys(element.node).forEach((key) =>
			expect(getByText(element.node[key])).toBeInTheDocument()
		);
	});
};

describe('IssueDetail', () => {
	afterEach(cleanup);

	it('renders description, tips and failing elements sections', () => {
		const {getByText} = renderIssueDetail(MOCK_ISSUES.aspectRatio);

		expect(getByText('description')).toBeInTheDocument();
		expect(getByText('tips')).toBeInTheDocument();
		expect(getByText('failing-elements')).toBeInTheDocument();
	});

	it('renders correct content for Aspect Ratio issues', () => {
		const issue = MOCK_ISSUES.aspectRatio;

		const {getAllByText, getByText} = renderIssueDetail(issue);

		expect(getByText(issue.description)).toBeInTheDocument();
		expect(getByText(issue.tips)).toBeInTheDocument();

		expect(getAllByText('source-file:').length).toBe(
			issue.failingElements.length
		);
		expect(getAllByText('displayed-aspect-ratio:').length).toBe(
			issue.failingElements.length
		);
		expect(getAllByText('actual-aspect-ratio:').length).toBe(
			issue.failingElements.length
		);

		issue.failingElements.forEach((element) => {
			Object.keys(element).forEach((key) =>
				expect(getByText(element[key])).toBeInTheDocument()
			);
		});
	});

	it('renders correct content for Page Font Size issues', () => {
		const issue = MOCK_ISSUES.fontSize;

		const {getAllByText, getByText} = renderIssueDetail(issue);

		expect(getByText(issue.description)).toBeInTheDocument();
		expect(getByText(issue.tips)).toBeInTheDocument();

		expect(getAllByText('font-size:').length).toBe(
			issue.failingElements.length
		);

		issue.failingElements.forEach((element) => {
			expect(getByText(element.fontSize)).toBeInTheDocument();
			expect(getByText(element.selector.snippet)).toBeInTheDocument();
		});
	});

	it('renders correct content for Invalid Hreflang issues', () => {
		const issue = MOCK_ISSUES.hreflang;

		const {getByText} = renderIssueDetail(issue);

		expect(getByText(issue.description)).toBeInTheDocument();
		expect(getByText(issue.tips)).toBeInTheDocument();

		issue.failingElements.forEach((element) => {
			Object.keys(element.source).forEach((key) =>
				expect(getByText(element.source[key])).toBeInTheDocument()
			);
		});
	});

	it('renders correct content for Link Text issues', () => {
		const issue = MOCK_ISSUES.linkText;

		const {getAllByText, getByText} = renderIssueDetail(issue);

		expect(getByText(issue.description)).toBeInTheDocument();
		expect(getByText(issue.tips)).toBeInTheDocument();

		expect(getAllByText('link-text:').length).toBe(
			issue.failingElements.length
		);
		expect(getAllByText('link-destination-url:').length).toBe(
			issue.failingElements.length
		);

		issue.failingElements.forEach((element) => {
			Object.keys(element).forEach((key) =>
				expect(getByText(element[key])).toBeInTheDocument()
			);
		});
	});

	it('renders correct content for Tap Target issues', () => {
		const issue = MOCK_ISSUES.tapTarget;

		const {getAllByText, getByText} = renderIssueDetail(issue);

		expect(getByText(issue.description)).toBeInTheDocument();
		expect(getByText(issue.tips)).toBeInTheDocument();

		expect(getAllByText('size:').length).toBe(issue.failingElements.length);
		expect(getAllByText('overlapping-target:').length).toBe(
			issue.failingElements.length
		);

		issue.failingElements.forEach((element) => {
			expect(
				getByText(element.overlappingTarget.nodeLabel)
			).toBeInTheDocument();
			expect(getByText(element.size)).toBeInTheDocument();
			expect(getByText(element.tapTarget.snippet)).toBeInTheDocument();
		});
	});

	it('renders correct content for Contrast Ratio issues', () => {
		checkContentOfElementWithNode(MOCK_ISSUES.contrastRatio);
	});

	it('renders correct content for IMG Alt issues', () => {
		checkContentOfElementWithNode(MOCK_ISSUES.imgAlt);
	});

	it('renders correct content for Input Alt issues', () => {
		checkContentOfElementWithNode(MOCK_ISSUES.inputAlt);
	});

	it('renders correct content for Crawlable Link issues', () => {
		checkContentOfElementWithNode(MOCK_ISSUES.crawlableLink);
	});

	it('renders correct content for Canonical URL issues', () => {
		checkContentOfElement(MOCK_ISSUES.canonicalURL);
	});

	it('renders correct content for Page Indexing issues', () => {
		checkContentOfElement(MOCK_ISSUES.pageIndexing);
	});

	it('renders correct content for Meta Description issues', () => {
		checkContentOfElement(MOCK_ISSUES.metaDescription);
	});

	it('renders correct content for Title Element issues', () => {
		checkContentOfElement(MOCK_ISSUES.titleElement);
	});

	it('does not view more button if there are less than 10 elements', () => {
		const failingElements = [];

		for (let i = 0; i < 9; i++) {
			failingElements.push(`Failure ${i}`);
		}

		const issue = {
			description: 'Example with less than 10 items',
			failingElements,
			key: 'example-with-less-than-10-items',
			tips: 'Example',
			title: 'Example',
			total: '9',
		};

		renderIssueDetail(issue);

		expect(screen.queryByText('view-more')).not.toBeInTheDocument();
	});

	it('renders view more button if there are more than 10 elements', () => {
		const failingElements = [];

		for (let i = 0; i < 15; i++) {
			failingElements.push(`Failure ${i}`);
		}

		const issue = {
			description: 'Example with more than 10 items',
			failingElements,
			key: 'example-with-more-than-10-items',
			tips: 'Example',
			title: 'Example',
			total: '15',
		};

		renderIssueDetail(issue);

		expect(screen.getByText('view-more')).toBeInTheDocument();
	});
});
