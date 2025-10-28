/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import updateLanguageId from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/updateLanguageId';
import Translation from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/Translation';
import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import {StoreAPIContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/actions/updateLanguageId',
	() => jest.fn(() => () => {})
);

const availableLanguages = {
	language_1: {
		default: true,
		displayName: 'language-1',
		languageIcon: 'language-1',
		languageId: 'language-1',
		w3cLanguageId: 'language-1',
	},
	language_2: {
		default: true,
		displayName: 'language-2',
		languageIcon: 'language-2',
		languageId: 'language-2',
		w3cLanguageId: 'language-2',
	},
	language_3: {
		default: true,
		displayName: 'language-3',
		languageIcon: 'language-3',
		languageId: 'language-3',
		w3cLanguageId: 'language-3',
	},
	language_4: {
		default: true,
		displayName: 'language-4',
		languageIcon: 'language-4',
		languageId: 'language-4',
		w3cLanguageId: 'language-4',
	},
};
const FRAGMENT_ENTRY_LINK_ID = '1';
const FRAGMENT_ENTRY_LINK_ID_2 = '2';
const fragmentEntryLink = {
	configuration: {
		fieldSets: [
			{
				fields: [
					{
						dataType: 'string',
						defaultValue: 'h1',
					},
				],
			},
		],
	},
	editableValues: {
		[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
			'element-text': {
				defaultValue: 'Text',
				language_1: 'Text language 1',
			},
		},
		[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {},
	},
	fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
	name: 'Heading',
};
const fragmentEntryLink2 = {
	...fragmentEntryLink,
	editableValues: {
		...fragmentEntryLink.editableValues,
		[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
			'element-text': {
				defaultValue: 'Text',
			},
		},
	},
};

const languageId = 'language_2';

const defaultState = {
	availableSegmentsExperiences: {
		0: {
			hasLockedSegmentsExperiment: false,
			name: 'Default Experience',
			priority: -1,
			segmentsEntryId: 'test-segment-id-00',
			segmentsExperienceId: '0',
			segmentsExperimentStatus: undefined,
			segmentsExperimentURL: 'https//:default-experience.com',
		},
	},
	defaultSegmentsExperienceId: '0',
	fragmentEntryLinks: {[FRAGMENT_ENTRY_LINK_ID]: fragmentEntryLink},
};

const renderTranslation = ({state}) => {
	return render(
		<StoreAPIContextProvider getState={() => state}>
			<Translation
				availableLanguages={availableLanguages}
				defaultLanguageId={languageId}
				dispatch={() => {}}
				fragmentEntryLinks={state.fragmentEntryLinks}
				languageId={languageId}
				segmentsExperienceId={state.defaultSegmentsExperienceId}
			/>
		</StoreAPIContextProvider>
	);
};

const getElementIndicatorText = (element) => {
	return element.parentElement.parentElement.nextSibling.textContent;
};

describe('Translation', () => {
	afterEach(cleanup);

	it('renders Translation component', async () => {
		const {getByRole, getByText} = renderTranslation({state: defaultState});

		const button = getByRole('combobox');
		await userEvent.click(button);

		expect(getByText('language-4')).toBeInTheDocument();
	});

	it('dispatches languageId when a language is selected', async () => {
		const {getByRole, getByText} = renderTranslation({state: defaultState});

		const button = getByRole('combobox');
		await userEvent.click(button);

		const option = getByText('language-3').parentElement;

		await waitFor(() => {
			userEvent.click(option);
			expect(updateLanguageId).toHaveBeenLastCalledWith({
				languageId: 'language_3',
			});
		});
	});

	it('sets label "translated" when there is a translated language', async () => {
		const {getByRole, getByText} = renderTranslation({state: defaultState});

		const button = getByRole('combobox');
		await userEvent.click(button);

		const indicator = getElementIndicatorText(getByText('language-1'));

		expect(indicator).toBe('translated');
	});

	it('sets label 1/n when there is one language traduction', async () => {
		const newState = {
			...defaultState,
			fragmentEntryLinks: {
				[FRAGMENT_ENTRY_LINK_ID]: fragmentEntryLink,
				[FRAGMENT_ENTRY_LINK_ID_2]: fragmentEntryLink2,
			},
		};
		const {getByRole, getByText} = renderTranslation({state: newState});

		const button = getByRole('combobox');
		await userEvent.click(button);

		const indicator = getElementIndicatorText(getByText('language-1'));

		expect(indicator).toBe('translating 1/2');
	});

	it('only takes into account elements which do not come from the master page', async () => {
		const newState = {
			...defaultState,
			fragmentEntryLinks: {
				[FRAGMENT_ENTRY_LINK_ID]: {
					...fragmentEntryLink,
					masterLayout: true,
				},
				[FRAGMENT_ENTRY_LINK_ID_2]: {
					...fragmentEntryLink,
					masterLayout: false,
				},
			},
		};
		const {getByRole, getByText} = renderTranslation({state: newState});

		const button = getByRole('combobox');
		await userEvent.click(button);

		const indicator = getElementIndicatorText(getByText('language-1'));

		expect(indicator).toBe('translated');
	});

	it('allows navigating through languages and selecting them with keyboard', async () => {
		const {getByRole, getByText} = renderTranslation({state: defaultState});

		const button = getByRole('combobox');
		await userEvent.click(button);

		const option = getByText('language-3').parentElement;

		option.focus();

		document.body.dispatchEvent(
			new KeyboardEvent('keydown', {
				code: 'Enter',
			})
		);

		expect(updateLanguageId).toHaveBeenLastCalledWith({
			languageId: 'language_3',
		});
	});
});
