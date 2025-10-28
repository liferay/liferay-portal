/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import VocabulariesSelectionBox from '../../../src/main/resources/META-INF/resources/js/components/VocabulariesSelectionBox';

const mockProps = {
	leftBoxName: 'availableAssetVocabularyIds',
	leftList: [
		{
			global: false,
			label: 'Extension (My company)',
			site: 1,
			value: 'extension',
		},
		{
			global: true,
			label: 'Stage (Global)',
			site: 0,
			value: 'stage',
		},
		{
			global: true,
			label: 'Topic (Global)',
			site: 0,
			value: 'topic',
		},
	],
	portletNamespace:
		'_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_',
	rightBoxName: 'currentAssetVocabularyIds',
	rightList: [
		{
			global: true,
			label: 'Audience (Global)',
			site: 0,
			value: 'audience',
		},
		{
			global: false,
			label: 'Region (Liferay)',
			site: 2,
			value: 'region',
		},
	],
};

const mockPropsWithoutSelected = {
	leftBoxName: 'availableAssetVocabularyIds',
	leftList: [
		{
			global: false,
			label: 'Extension (My company)',
			site: 1,
			value: 'extension',
		},
		{
			global: false,
			label: 'Test vocabulary',
			site: 1,
			value: 'test',
		},
		{
			global: true,
			label: 'Stage (Global)',
			site: 0,
			value: 'stage',
		},
		{
			global: true,
			label: 'Topic (Global)',
			site: 0,
			value: 'topic',
		},
		{
			global: true,
			label: 'Audience (Global)',
			site: 0,
			value: 'audience',
		},
		{
			global: false,
			label: 'Region (Liferay)',
			site: 2,
			value: 'region',
		},
		{
			global: false,
			label: 'Another Region (Extra site)',
			site: 3,
			value: 'another-region',
		},
	],
	portletNamespace:
		'_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_',
	rightBoxName: 'currentAssetVocabularyIds',
	rightList: [],
};

describe('VocabulariesSelectionBox', () => {
	it('renders a VocabulariesSelectionBox with selected and not selected vocabularies', () => {
		const {container, getByText} = render(
			<VocabulariesSelectionBox {...mockProps} />
		);

		expect(getByText('Extension (My company)')).toBeInTheDocument();
		expect(getByText('Audience (Global)')).toBeInTheDocument();

		const availableVocabularies = container.querySelectorAll(
			'#availableAssetVocabularyIds option'
		);
		const selectedVocabularies = container.querySelectorAll(
			'#_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_currentAssetVocabularyIds option'
		);

		expect(availableVocabularies.length).toBe(3);
		expect(selectedVocabularies.length).toBe(2);
	});

	it('renders vocabularies from other sites disabled when we have a vocabulary from another site (not Global) selected', () => {
		const {container} = render(<VocabulariesSelectionBox {...mockProps} />);

		const disabledVocabularies = container.querySelectorAll(
			"#availableAssetVocabularyIds option[value='extension']:disabled"
		);
		expect(disabledVocabularies.length).toBe(1);
	});

	it('moves a vocabulary from left to right dislabing vocabularies from other sites', () => {
		const {container} = render(
			<VocabulariesSelectionBox {...mockPropsWithoutSelected} />
		);

		const availableVocabularies = container.querySelectorAll(
			'#availableAssetVocabularyIds option'
		);
		const selectedVocabularies = container.querySelectorAll(
			'#_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_currentAssetVocabularyIds option'
		);

		expect(availableVocabularies.length).toBe(7);
		expect(selectedVocabularies.length).toBe(0);

		const availableSelect = container.querySelector(
			'#availableAssetVocabularyIds'
		);
		fireEvent.change(availableSelect, {target: {value: 'extension'}});
		fireEvent.click(container.querySelector('.transfer-button-ltr'));

		const availableVocabulariesAfterLTR = container.querySelectorAll(
			'#availableAssetVocabularyIds option'
		);
		const selectedVocabulariesAfterLTR = container.querySelectorAll(
			'#_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_currentAssetVocabularyIds option'
		);

		expect(availableVocabulariesAfterLTR.length).toBe(6);
		expect(selectedVocabulariesAfterLTR.length).toBe(1);

		// Region  vocabulary must be disabled now becasue is from another site

		const disabledVocabularies = container.querySelectorAll(
			"#availableAssetVocabularyIds option[value='region']:disabled"
		);
		expect(disabledVocabularies.length).toBe(1);
	});

	it('moves a vocabulary from right to left maintaining the vocabulary selected after moving it', () => {
		const {container} = render(<VocabulariesSelectionBox {...mockProps} />);

		const selectedSelect = container.querySelector(
			'#_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_currentAssetVocabularyIds'
		);
		fireEvent.change(selectedSelect, {target: {value: 'region'}});
		fireEvent.click(container.querySelector('.transfer-button-rtl'));

		const availableVocabulariesAfterRTL = container.querySelectorAll(
			'#availableAssetVocabularyIds option'
		);
		const selectedVocabulariesAfterRTL = container.querySelectorAll(
			'#_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_currentAssetVocabularyIds option'
		);

		expect(availableVocabulariesAfterRTL.length).toBe(4);
		expect(selectedVocabulariesAfterRTL.length).toBe(1);

		// There must be one disbled vocabulary because Region is from site 2

		const disabledVocabularies = container.querySelectorAll(
			'#availableAssetVocabularyIds option:disabled'
		);
		expect(disabledVocabularies.length).toBe(1);
	});

	it('prevents to move more than two vocabularies from Available to In Use', () => {
		const {container} = render(
			<VocabulariesSelectionBox {...mockPropsWithoutSelected} />
		);

		const availableSelect = container.querySelector(
			'#availableAssetVocabularyIds'
		);

		userEvent.selectOptions(availableSelect, ['extension']);

		// We need to fire change event after the select options

		fireEvent.change(availableSelect);

		let checkedVocabularies = container.querySelectorAll(
			'#availableAssetVocabularyIds option:checked'
		);
		const leftToRightButton = container.querySelector(
			'.transfer-button-ltr'
		);

		expect(checkedVocabularies.length).toBe(1);
		expect(leftToRightButton.disabled).toBe(false);

		userEvent.selectOptions(availableSelect, [
			'extension',
			'test',
			'stage',
		]);
		fireEvent.change(availableSelect);

		checkedVocabularies = container.querySelectorAll(
			'#availableAssetVocabularyIds option:checked'
		);

		expect(checkedVocabularies.length).toBe(3);
		expect(leftToRightButton.disabled).toBe(true);
	});

	it('prevents to move non-global vocabularies from different sites, when selecting then via click and hold', () => {
		const {container} = render(
			<VocabulariesSelectionBox {...mockPropsWithoutSelected} />
		);

		const availableSelect = container.querySelector(
			'#availableAssetVocabularyIds'
		);

		userEvent.selectOptions(availableSelect, ['region', 'another-region']);
		fireEvent.change(availableSelect);

		const checkedVocabularies = container.querySelectorAll(
			'#availableAssetVocabularyIds option:checked'
		);
		const leftToRightButton = container.querySelector(
			'.transfer-button-ltr'
		);

		expect(checkedVocabularies.length).toBe(2);
		expect(leftToRightButton.disabled).toBe(true);
	});
});
