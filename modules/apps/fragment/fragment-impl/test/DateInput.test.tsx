/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {DateInput} from '../src/main/resources/META-INF/resources/js/api/DateInput';

// Only `LocalizationSelect` reaches CKEditor, which Jest cannot parse.

jest.mock(
	'../src/main/resources/META-INF/resources/js/api/LocalizationSelect',
	() => ({
		EVENT_INPUT_REGISTERED: 'localizedInput:registered',
		EVENT_TRANSLATION_STATUS: 'localizedInput:translationStatus',
		getSelectedLanguageId: () => null,
	})
);

const NAME = 'ObjectField_date';
const NAMESPACE = 'fragment-1';

function setUpFragment(value: string) {
	document.body.innerHTML = `
		<div class="date-input">
			<label for="${NAMESPACE}-date-input" id="${NAMESPACE}-date-input-label">
				Expiration Date

				<span class="d-none" id="${NAMESPACE}-date-read-only">(read-only)</span>
			</label>

			<div id="${NAMESPACE}-date-input-picker"></div>

			<input id="${NAMESPACE}-date-input-value" name="${NAME}" type="hidden" value="${value}" />

			<span id="${NAMESPACE}-date-input-translations"></span>

			<div class="d-none" id="${NAMESPACE}-unlocalized-info"></div>
		</div>`;

	return document.getElementById(`${NAMESPACE}-date-input-picker`)!;
}

function renderDateInput({
	value = '',
	...props
}: Partial<React.ComponentProps<typeof DateInput>> & {value?: string} = {}) {
	const container = setUpFragment(value);

	render(
		<DateInput
			defaultLanguageId="en_US"
			name={NAME}
			namespace={NAMESPACE}
			readOnlyLabelId={`${NAMESPACE}-date-read-only`}
			value={value}
			{...props}
		/>,
		{container}
	);
}

function getCalendarButton() {
	return screen.getByTestId('date-button');
}

function getInput() {
	return screen.getByLabelText(/Expiration Date/);
}

function getSubmittedValue() {
	return (
		document.getElementById(
			`${NAMESPACE}-date-input-value`
		) as HTMLInputElement
	).value;
}

/**
 * `Liferay.on` is a stub, so hand the event to the registered listener.
 */

function fireLocalizationEvent(
	event: string,
	payload: Record<string, unknown>
) {
	const call = (Liferay.on as jest.Mock).mock.calls.find(
		([name]) => name === `localizationSelect:${event}`
	);

	act(() => call![1](payload));
}

/**
 * A missing `value` attribute is how the portal marks a language untranslated.
 */

function getTranslation(languageId: string) {
	return document.getElementById(
		`${NAMESPACE}${NAMESPACE}-date-input-value_${languageId}`
	) as HTMLInputElement | null;
}

describe('DateInput', () => {
	describe('Date', () => {
		it('submits a typed date in server format', async () => {
			renderDateInput();

			await userEvent.type(getInput(), '12/25/2026');

			expect(getSubmittedValue()).toBe('2026-12-25');
		});

		it('shows the submitted value in the user locale format', () => {
			renderDateInput({value: '2026-07-09'});

			expect(getInput()).toHaveValue('07/09/2026');

			expect(getSubmittedValue()).toBe('2026-07-09');
		});

		it('empties the submitted value while the typed date is incomplete', async () => {
			renderDateInput();

			await userEvent.type(getInput(), '12/25');

			expect(getSubmittedValue()).toBe('');
		});
	});

	describe('DateTime', () => {
		it('keeps the T separator the server expects on the submitted value', () => {
			renderDateInput({time: true, value: '2026-07-09T14:30'});

			expect(getSubmittedValue()).toBe('2026-07-09T14:30');
		});

		it('shows the submitted date time in the user locale format', () => {
			renderDateInput({time: true, value: '2026-07-09T14:30'});

			expect(getInput()).toHaveValue('07/09/2026 02:30 PM');
		});

		it('submits a typed date time separated by T', async () => {
			renderDateInput({time: true});

			await userEvent.type(getInput(), '12/25/2026 02:30 PM');

			expect(getSubmittedValue()).toBe('2026-12-25T14:30');
		});
	});

	describe('Validation', () => {
		it('marks the input invalid when the typed date cannot be read', async () => {
			renderDateInput();

			await userEvent.type(getInput(), '99/99/9999');

			expect((getInput() as HTMLInputElement).validationMessage).toBe(
				'please-enter-a-valid-date'
			);
		});

		it('clears the invalid state once the date can be read', async () => {
			renderDateInput();

			await userEvent.type(getInput(), '99/99/9999');
			await userEvent.clear(getInput());
			await userEvent.type(getInput(), '12/25/2026');

			expect((getInput() as HTMLInputElement).validationMessage).toBe('');
		});
	});

	describe('States', () => {
		it('disables the input', () => {
			renderDateInput({disabled: true});

			expect(getInput()).toBeDisabled();
		});

		it('makes the input read only', () => {
			renderDateInput({readOnly: true});

			expect(getInput()).toHaveAttribute('readonly');
		});

		it('marks the input as required', () => {
			renderDateInput({required: true});

			expect(getInput()).toBeRequired();
		});

		it('opens the calendar when the input is editable', async () => {
			renderDateInput({value: '2026-07-09'});

			await userEvent.click(getCalendarButton());

			expect(getCalendarButton()).toHaveAttribute(
				'aria-expanded',
				'true'
			);
		});

		// `readonly` only stops typing, so the calendar needs its own guard.

		it('does not open the calendar when the input is read only', async () => {
			renderDateInput({readOnly: true, value: '2026-07-09'});

			await userEvent.click(getCalendarButton());

			expect(getCalendarButton()).toHaveAttribute(
				'aria-expanded',
				'false'
			);

			expect(getSubmittedValue()).toBe('2026-07-09');
		});
	});

	describe('Unlocalized', () => {
		beforeEach(() => {
			(Liferay.on as jest.Mock).mockClear();
		});

		it('does not open the calendar on a language it cannot translate', async () => {
			renderDateInput({
				unlocalizedFieldsState: 'read-only',
				value: '2026-07-09',
			});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			await userEvent.click(getCalendarButton());

			expect(getCalendarButton()).toHaveAttribute(
				'aria-expanded',
				'false'
			);

			expect(getSubmittedValue()).toBe('2026-07-09');
		});

		it('disables the calendar on a language it cannot translate', () => {
			renderDateInput({
				unlocalizedFieldsState: 'disabled',
				value: '2026-07-09',
			});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			expect(getInput()).toBeDisabled();
			expect(getCalendarButton()).toBeDisabled();
		});

		it('opens the calendar again on the default language', async () => {
			renderDateInput({
				unlocalizedFieldsState: 'read-only',
				value: '2026-07-09',
			});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});
			fireLocalizationEvent('localeChanged', {languageId: 'en_US'});

			expect(getInput()).not.toHaveAttribute('readonly');

			await userEvent.click(getCalendarButton());

			expect(getCalendarButton()).toHaveAttribute(
				'aria-expanded',
				'true'
			);
		});
	});

	// The page locale stays `en_US`, so the displayed format never moves.

	describe('Localization', () => {

		// Dropped so each test reaches the listener its own render made.

		beforeEach(() => {
			(Liferay.on as jest.Mock).mockClear();
		});

		const renderLocalized = (valueI18n: Record<string, string>) =>
			renderDateInput({
				availableLanguageIds: ['en_US', 'es_ES'],
				localizable: true,
				value: valueI18n.en_US ?? '',
				valueI18n,
			});

		it('falls back to the default language in an untranslated language', () => {
			renderLocalized({en_US: '2026-12-25'});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			expect(getInput()).toHaveValue('12/25/2026');
			expect(getSubmittedValue()).toBe('2026-12-25');
		});

		it('shows the translation of a translated language', () => {
			renderLocalized({en_US: '2026-12-25', es_ES: '2026-01-31'});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			expect(getInput()).toHaveValue('01/31/2026');
			expect(getSubmittedValue()).toBe('2026-01-31');
		});

		it('shows nothing when the default language has no value', () => {
			renderLocalized({});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			expect(getInput()).toHaveValue('');
			expect(getSubmittedValue()).toBe('');
		});

		it('keeps a date typed in one language on that language', async () => {
			renderLocalized({en_US: '2026-12-25'});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			await userEvent.clear(getInput());
			await userEvent.type(getInput(), '01/31/2026');

			fireLocalizationEvent('localeChanged', {languageId: 'en_US'});

			expect(getInput()).toHaveValue('12/25/2026');

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			expect(getInput()).toHaveValue('01/31/2026');
			expect(getTranslation('es_ES')).toHaveValue('2026-01-31');
		});

		it('does not translate a language that was only visited', () => {
			renderLocalized({en_US: '2026-12-25'});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});

			expect(getTranslation('es_ES')).toBeNull();
		});

		it('submits an auto translation without showing it on another language', () => {
			renderLocalized({en_US: '2026-12-25'});

			fireLocalizationEvent('autoTranslate', {
				fields: {[NAME]: '2026-01-31'},
				languageId: 'es_ES',
			});

			expect(getTranslation('es_ES')).toHaveValue('2026-01-31');
			expect(getInput()).toHaveValue('12/25/2026');
		});

		it('shows an auto translation for the language on screen', () => {
			renderLocalized({en_US: '2026-12-25'});

			fireLocalizationEvent('localeChanged', {languageId: 'es_ES'});
			fireLocalizationEvent('autoTranslate', {
				fields: {[NAME]: '2026-01-31'},
				languageId: 'es_ES',
			});

			expect(getInput()).toHaveValue('01/31/2026');
			expect(getSubmittedValue()).toBe('2026-01-31');
		});
	});
});
