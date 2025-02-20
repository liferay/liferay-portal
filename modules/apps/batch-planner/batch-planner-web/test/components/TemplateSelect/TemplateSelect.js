/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import fetchMock from 'fetch-mock';
import React from 'react';

import TemplateSelect from '../../../src/main/resources/META-INF/resources/js/TemplateSelect';
import {
	HEADLESS_BATCH_PLANNER_URL,
	TEMPLATE_SELECTED_EVENT,
} from '../../../src/main/resources/META-INF/resources/js/constants';

const BASE_PROPS = {
	initialTemplateOptions: [
		{label: 'ProviamoTemplate', selected: false, value: 106902},
		{label: 'Hello', selected: true, value: '42147'},
	],
	portletNamespace: 'test',
};
const internalClassName =
	'com.liferay.headless.commerce.admin.channel.dto.v1_0.Channel';
const internalClassNameKey = internalClassName;
const mockedMapping = {
	currencyCode: 'currencyCode',
	id: 'externalReferenceCode',
	siteGroupId: 'siteGroupId',
	testFieldName: 'name',
	type: 'type',
};

const initialTemplate = {
	externalType: 'JSONL',
	internalClassNameKey,
	mappings: mockedMapping,
};
const mockPlanId = 106902;

const getPlanInfoURL = `${HEADLESS_BATCH_PLANNER_URL}/plans/${mockPlanId}`;

jest.mock('frontend-js-components-web', () => {
	jest.fn();
});

describe('TemplateSelect', () => {
	beforeEach(() => {
		fetchMock.mock(getPlanInfoURL, mockGetPlan);
	});

	afterEach(() => {
		fetchMock.restore();

		jest.resetAllMocks();
	});

	it('must have label', () => {
		const {getByLabelText} = render(<TemplateSelect {...BASE_PROPS} />);

		getByLabelText(Liferay.Language.get('template'));
	});

	it('must fire new template event when preselected', async () => {
		const mockTemplateSelected = jest.fn();

		Liferay.on(TEMPLATE_SELECTED_EVENT, mockTemplateSelected);

		render(
			<TemplateSelect {...BASE_PROPS} initialTemplate={initialTemplate} />
		);

		const documentReadyEvent = document.createEvent('CustomEvent');

		documentReadyEvent.initEvent('readystatechange', false, true);
		document.dispatchEvent(documentReadyEvent);

		await waitFor(() => {
			const expectedEvent = new CustomEvent(TEMPLATE_SELECTED_EVENT);

			expectedEvent.template = {...initialTemplate};

			expect(mockTemplateSelected).toBeCalledWith(expectedEvent);
		});
	});

	it('must fire empty event when no template get selected', async () => {
		const mockTemplateSelected = jest.fn();

		Liferay.on(TEMPLATE_SELECTED_EVENT, mockTemplateSelected);

		const {getByLabelText} = render(
			<TemplateSelect
				{...BASE_PROPS}
				selectedTemplateClassName={internalClassNameKey}
				selectedTemplateMapping={mockedMapping}
			/>
		);

		act(() => {
			fireEvent.change(getByLabelText(Liferay.Language.get('template')), {
				target: {value: 0},
			});
		});

		await waitFor(() => {
			const expectedEvent = new CustomEvent(TEMPLATE_SELECTED_EVENT);

			expectedEvent.template = null;

			expect(mockTemplateSelected).toHaveBeenLastCalledWith(
				expectedEvent
			);
		});
	});

	it.skip('must fire event with right template configuration', async () => {
		const mockTempalteSelected = jest.fn();

		Liferay.on(TEMPLATE_SELECTED_EVENT, mockTempalteSelected);

		const {getByLabelText} = render(<TemplateSelect {...BASE_PROPS} />);

		act(() => {
			fireEvent.change(getByLabelText(Liferay.Language.get('template')), {
				target: {value: mockPlanId},
			});
		});

		await waitFor(() => {
			const expectedEvent = new CustomEvent(TEMPLATE_SELECTED_EVENT);

			expectedEvent.template = {...initialTemplate};

			expect(mockTempalteSelected).toBeCalledWith(expectedEvent);
		});
	});
});

const mockGetPlan = {
	active: false,
	export: true,
	externalType: 'JSONL',
	externalURL: '/',
	id: 106902,
	internalClassName,
	internalClassNameKey,
	mappings: [
		{
			externalFieldName: 'type',
			externalFieldType: 'String',
			id: 2206,
			internalFieldName: 'type',
			internalFieldType: 'String',
			planId: 106902,
			script: '',
		},
		{
			externalFieldName: 'siteGroupId',
			externalFieldType: 'String',
			id: 2205,
			internalFieldName: 'siteGroupId',
			internalFieldType: 'String',
			planId: 106902,
			script: '',
		},
		{
			externalFieldName: 'name',
			externalFieldType: 'String',
			id: 2204,
			internalFieldName: 'testFieldName',
			internalFieldType: 'String',
			planId: 106902,
			script: '',
		},
		{
			externalFieldName: 'id',
			externalFieldType: 'String',
			id: 2203,
			internalFieldName: 'id',
			internalFieldType: 'String',
			planId: 106902,
			script: '',
		},
		{
			externalFieldName: 'externalReferenceCode',
			externalFieldType: 'String',
			id: 2202,
			internalFieldName: 'id',
			internalFieldType: 'String',
			planId: 106902,
			script: '',
		},
		{
			externalFieldName: 'currencyCode',
			externalFieldType: 'String',
			id: 2201,
			internalFieldName: 'currencyCode',
			internalFieldType: 'String',
			planId: 106902,
			script: '',
		},
	],
	name: 'Channel template JSON',
	policies: [
		{
			id: 106904,
			name: 'saveExport',
			planId: 106902,
			value: 'saveExport',
		},
		{
			id: 106903,
			name: 'containsHeaders',
			planId: 106902,
			value: 'containsHeaders',
		},
	],
	taskItemDelegateName: 'DEFAULT',
};
