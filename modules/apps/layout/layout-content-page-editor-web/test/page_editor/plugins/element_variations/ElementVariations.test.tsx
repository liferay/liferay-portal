/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ElementVariations from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/ElementVariations';

type Props = React.ComponentProps<typeof ElementVariations>;

const AUDIENCES = [
	{label: 'Loyal Customers', value: 'audience-1'},
	{label: 'New Visitors', value: 'audience-2'},
];

const EXPERIENCES = [
	{
		audienceEntryERCs: ['audience-1'],
		label: 'Default',
		segmentsExperienceERC: 'experience-1',
		segmentsExperienceId: 1,
	},
];

const ITEM_NAMES = {'item-1': 'Title'};

const PREVIEW_HTML =
	'<div class="lfr-layout-structure-item-item-1">' +
	'<h1 data-lfr-editable-id="title-editable">Hello</h1>' +
	'</div>';

const TARGET_ELEMENT_LABEL = 'Title (title-editable)';

const ELEMENT_VARIATIONS = [
	{
		active: true,
		audienceEntryERCs: ['audience-1'],
		externalReferenceCode: 'element-variation-1',
		hide: 'false',
		html: {en_US: '<p>Hello</p>'},
		js: {},
		name: 'My Variation',
		segmentsExperienceERC: 'experience-1',
		targetElement: '.title',
	},
];

function renderElementVariations(props: Partial<Props> = {}) {
	return render(
		<ElementVariations
			addElementVariationURL="/add"
			audiences={AUDIENCES}
			createAudienceURL="/create-audience"
			defaultLanguageId="en_US"
			deleteElementVariationURL="/delete"
			elementVariations={[]}
			experiences={EXPERIENCES}
			itemNames={ITEM_NAMES}
			locales={[{id: 'en_US', label: 'English', symbol: 'en-us'}]}
			plid={1}
			portletNamespace="_com_liferay_test_"
			previewURL="/preview?p=1"
			selectedSegmentsExperienceId={1}
			updateAudiencesPriorityURL="/update-audiences-priority"
			updateElementVariationURL="/update"
			{...props}
		/>
	);
}

function loadPreview() {
	const iframe = screen.getByTitle('element-variations') as HTMLIFrameElement;

	const iframeDocument = iframe.contentDocument as Document;

	iframeDocument.write(PREVIEW_HTML);
	iframeDocument.close();

	(iframe.contentWindow as any).Liferay = Liferay;

	fireEvent.load(iframe);
}

describe('ElementVariations', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('has no accessibility violations when there are no element variations', async () => {
		const {container} = renderElementVariations();

		expect(
			await screen.findByText('no-element-variations')
		).toBeInTheDocument();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations when there are no audiences', async () => {
		const {container} = renderElementVariations({audiences: []});

		expect(
			await screen.findByText('create-new-audience')
		).toBeInTheDocument();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations when element variations are listed', async () => {
		const {container} = renderElementVariations({
			elementVariations: ELEMENT_VARIATIONS,
		});

		loadPreview();

		expect(await screen.findByText('My Variation')).toBeInTheDocument();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations while editing an element variation', async () => {
		const {container} = renderElementVariations();

		await userEvent.click(await screen.findByText('new'));

		expect(screen.getByLabelText('name')).toBeInTheDocument();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('has no accessibility violations once the element variation is filled', async () => {
		const {container} = renderElementVariations();

		loadPreview();

		await userEvent.click(await screen.findByText('new'));

		await userEvent.type(screen.getByLabelText('name'), 'My Variation');
		await userEvent.tab();

		await userEvent.click(screen.getByLabelText('page-element'));
		await userEvent.click(screen.getByText(TARGET_ELEMENT_LABEL));

		await userEvent.click(screen.getByLabelText('audience'));
		await userEvent.click(
			screen.getByRole('option', {name: 'Loyal Customers'})
		);

		await userEvent.type(screen.getByLabelText('html'), '<p>Hello</p>');
		await userEvent.type(
			screen.getByLabelText('javascript'),
			'console.log("hi");'
		);

		expect(screen.getByDisplayValue('My Variation')).toBeInTheDocument();
		expect(screen.getByLabelText('page-element')).toHaveTextContent(
			TARGET_ELEMENT_LABEL
		);
		expect(screen.getByText('Loyal Customers')).toBeInTheDocument();
		expect(screen.getByLabelText('html')).toHaveValue('<p>Hello</p>');
		expect(screen.getByLabelText('javascript')).toHaveValue(
			'console.log("hi");'
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
