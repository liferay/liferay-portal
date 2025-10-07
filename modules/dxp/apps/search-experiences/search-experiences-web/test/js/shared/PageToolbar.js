/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React from 'react';

import PageToolbar from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/PageToolbar';
import ThemeContext from '../../../src/main/resources/META-INF/resources/sxp_blueprint_admin/js/shared/ThemeContext';

import '@testing-library/jest-dom/extend-expect';

jest.useFakeTimers();

const context = {
	availableLanguages: {
		de_DE: 'German (Germany)',
		en_US: 'English (United States)',
		es_ES: 'Spanish (Spain)',
		fr_FR: 'French (France)',
	},
	defaultLocale: 'en_US',
	locale: 'en_US',
};

const description_i18n = {
	'en-US': 'Description in English',
	'es-ES': 'Descripcion en Espanol',
};
const title_i18n = {
	'en-US': 'Title in English',
	'es-ES': 'Titulo en Espanol',
};

const onSubmit = jest.fn();
const onTitleAndDescriptionChange = jest.fn();

function PageToolbarComponent(props) {
	return (
		<PageToolbar
			description={description_i18n['en-US']}
			descriptionI18n={description_i18n}
			onCancel="/link"
			onChangeTab={jest.fn()}
			onSubmit={onSubmit}
			onTitleAndDescriptionChange={onTitleAndDescriptionChange}
			tab="query-builder"
			tabs={{
				'query-builder': 'query-builder',
			}}
			title={title_i18n['en-US']}
			titleI18n={title_i18n}
			{...props}
		/>
	);
}

function renderPageToolbar(props) {
	return render(PageToolbarComponent(props));
}

function renderPageToolbarWithContext(context, props) {
	return render(
		<ThemeContext.Provider value={context}>
			{PageToolbarComponent(props)}
		</ThemeContext.Provider>
	);
}

describe('PageToolbar', () => {
	it('renders the page toolbar form', () => {
		const {container} = renderPageToolbar();

		expect(container).not.toBeNull();
	});

	it('renders the title', () => {
		const {getByText} = renderPageToolbar();

		getByText(title_i18n['en-US']);
	});

	it('calls onTitleAndDescriptionChange when updating title', () => {
		const {getByLabelText, getByText} = renderPageToolbar();

		getByText(title_i18n['en-US']);

		fireEvent.click(getByLabelText('edit-title'));

		act(() => jest.runAllTimers());

		fireEvent.change(getByLabelText('title'), {
			target: {value: 'Updated Title'},
		});

		fireEvent.click(getByText('done'));

		act(() => jest.runAllTimers());

		expect(onTitleAndDescriptionChange).toHaveBeenCalled();
	});

	it('calls onTitleAndDescriptionChange when updating description', () => {
		const {getByLabelText, getByText} = renderPageToolbar();

		getByText(description_i18n['en-US']);

		fireEvent.click(getByLabelText('edit-description'));

		act(() => jest.runAllTimers());

		fireEvent.change(getByLabelText('description'), {
			target: {value: 'Updated Description'},
		});

		fireEvent.click(getByText('done'));

		act(() => jest.runAllTimers());

		expect(onTitleAndDescriptionChange).toHaveBeenCalled();
	});

	it('offers link to cancel', () => {
		const {getByText} = renderPageToolbar();

		expect(getByText('cancel')).toHaveAttribute('href', '/link');
	});

	it('calls onSubmit when clicking on Save', () => {
		const {getByText} = renderPageToolbar();

		fireEvent.click(getByText('save'));

		expect(onSubmit).toHaveBeenCalled();
	});

	it('disables submit button when submitting', () => {
		const {getByText} = renderPageToolbar({isSubmitting: true});

		expect(getByText('save')).toBeDisabled();
	});

	// Disabled, behavior when opening Modal focuses on the modal first to
	// announce that it is open.

	xit('focuses on the title input when clicked on', () => {
		const {getByLabelText} = renderPageToolbar();

		fireEvent.click(getByLabelText('edit-title'));

		act(() => jest.runAllTimers());

		expect(getByLabelText('title')).toHaveFocus();
	});

	// Disabled, behavior when opening Modal focuses on the modal first to
	// announce that it is open.

	xit('focuses on the description input when clicked on', () => {
		const {getByLabelText} = renderPageToolbar();

		fireEvent.click(getByLabelText('edit-description'));

		act(() => jest.runAllTimers());

		expect(getByLabelText('description')).toHaveFocus();
	});

	it('switches locales in modal with language selector', () => {
		const {
			getAllByText,
			getAllByTitle,
			getByDisplayValue,
			getByLabelText,
			getByText,
		} = renderPageToolbarWithContext(context);

		fireEvent.click(getByLabelText('edit-description'));

		act(() => jest.runAllTimers());

		fireEvent.click(getAllByTitle('en-US')[0]);

		fireEvent.click(getAllByText('es-ES')[0]);

		getByDisplayValue(title_i18n['es-ES']);

		getByText(description_i18n['es-ES']);
	});
});
