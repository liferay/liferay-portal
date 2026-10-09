/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openModal, openSimpleInputModal} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React from 'react';

import SelectLayoutPageTemplateEntryMasterLayoutVerticalCardPropsTransformer from '../../../src/main/resources/META-INF/resources/js/propsTransformers/SelectLayoutPageTemplateEntryMasterLayoutVerticalCardPropsTransformer';

jest.mock('@liferay/frontend-icons-web', () => ({
	getSpritemap: () => 'spritemap',
}));

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
	openSimpleInputModal: jest.fn(),
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => {
	const actual = jest.requireActual('frontend-js-web');

	return {
		...actual,
		fetch: jest.fn(() =>
			Promise.resolve({
				json: () => ({}),
			})
		),
		navigate: jest.fn(),
	};
});

const ADDITIONAL_PROPS = {
	addLayoutPageTemplateEntryURL: 'addLayoutPageTemplateEntryURL',
	dialogTitle: 'add-page-template',
	mainFieldLabel: 'name',
	mainFieldName: 'name',
	mainFieldPlaceholder: 'name',
};

const clickCard = (additionalProps) => {
	const {onClick} =
		SelectLayoutPageTemplateEntryMasterLayoutVerticalCardPropsTransformer({
			additionalProps,
			portletNamespace: '_namespace_',
		});

	onClick({preventDefault: jest.fn()});
};

describe('SelectLayoutPageTemplateEntryMasterLayoutVerticalCardPropsTransformer', () => {
	it('asks for the set of the page template only when the card has no set', async () => {
		clickCard(ADDITIONAL_PROPS);

		expect(openSimpleInputModal).toHaveBeenCalledWith({
			dialogTitle: 'add-page-template',
			formSubmitURL: 'addLayoutPageTemplateEntryURL',
			mainFieldLabel: 'name',
			mainFieldName: 'name',
			mainFieldPlaceholder: 'name',
			namespace: '_namespace_',
			spritemap: 'spritemap',
		});
		expect(openModal).not.toHaveBeenCalled();

		clickCard({
			...ADDITIONAL_PROPS,
			addPageTemplateSetURL: 'addPageTemplateSetURL',
			pageTemplateSets: [{id: 2, name: 'set-1'}],
		});

		expect(openSimpleInputModal).toHaveBeenCalledTimes(1);

		const [[{contentComponent: ContentComponent}]] = openModal.mock.calls;

		render(<ContentComponent closeModal={jest.fn()} />);

		await act(async () => {
			await userEvent.type(
				screen.getByLabelText('page-template-name'),
				'Page Template 1'
			);
			await userEvent.selectOptions(
				screen.getByLabelText('page-template-set'),
				'2'
			);
			await userEvent.click(screen.getByText('save'));
		});

		const [url, {body}] = fetch.mock.calls[0];

		expect(url).toBe('addLayoutPageTemplateEntryURL');
		expect(Object.fromEntries(body.entries())).toEqual({
			_namespace_layoutPageTemplateCollectionId: '2',
			_namespace_name: 'Page Template 1',
		});
	});
});
