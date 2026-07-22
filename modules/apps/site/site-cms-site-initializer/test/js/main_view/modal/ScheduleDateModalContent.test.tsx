/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ScheduleDateModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/ScheduleDateModalContent';

const FUTURE_DATE = '2099-12-31T10:00:00Z';
const PAST_DATE = '2020-01-01T10:00:00Z';
const SAVE_HINT =
	'enter-a-review-date-or-select-never-review-to-enable-the-save-button';

function renderModal({
	closeModal = jest.fn(),
	date = FUTURE_DATE,
	onSave = jest.fn().mockResolvedValue(true),
}: {
	closeModal?: () => void;
	date?: string;
	onSave?: (date: string) => Promise<boolean>;
} = {}) {
	return render(
		<ScheduleDateModalContent
			closeModal={closeModal}
			date={date}
			fieldLabel="review-date"
			fieldName="reviewDate"
			neverLabel="never-review"
			onSave={onSave}
			saveRequirementLabel={SAVE_HINT}
			title="update-review-date"
		/>
	);
}

describe('ScheduleDateModalContent', () => {
	it('renders the date field, the never checkbox, and the actions', () => {
		renderModal();

		expect(screen.getByText('update-review-date')).toBeInTheDocument();
		expect(screen.getByLabelText('never-review')).toBeInTheDocument();
		expect(screen.getByText('save')).toBeInTheDocument();
		expect(screen.getByText('cancel')).toBeInTheDocument();
		expect(screen.queryByText(SAVE_HINT)).not.toBeInTheDocument();
	});

	it('does not preselect never when no date is given', () => {
		renderModal({date: ''});

		expect(screen.getByLabelText('never-review')).not.toBeChecked();
		expect(screen.getAllByRole('textbox')[0]).toBeEnabled();
	});

	it('does not clear the date when nothing is picked', async () => {
		const onSave = jest.fn().mockResolvedValue(true);

		renderModal({date: '', onSave});

		await userEvent.click(screen.getByText('save'));

		expect(onSave).not.toHaveBeenCalled();
	});

	it('saves the date as an ISO string with a trailing Z', async () => {
		const closeModal = jest.fn();
		const onSave = jest.fn().mockResolvedValue(true);

		renderModal({closeModal, onSave});

		await userEvent.click(screen.getByText('save'));

		await waitFor(() =>
			expect(onSave).toHaveBeenCalledWith('2099-12-31T10:00:00Z')
		);

		await waitFor(() => expect(closeModal).toHaveBeenCalled());
	});

	it('does not save an unchanged past date', async () => {
		const onSave = jest.fn().mockResolvedValue(true);

		renderModal({date: PAST_DATE, onSave});

		await userEvent.click(screen.getByText('save'));

		expect(onSave).not.toHaveBeenCalled();
		expect(
			screen.getByText('the-date-entered-is-in-the-past')
		).toBeInTheDocument();
	});

	it('disables save and explains why while the date is empty', async () => {
		renderModal({date: ''});

		const saveButton = screen.getByText('save');

		expect(saveButton).toBeDisabled();
		expect(screen.getByText(SAVE_HINT)).toBeInTheDocument();

		await userEvent.type(
			screen.getAllByRole('textbox')[0],
			'12/31/2099 10:00 AM'
		);
		await userEvent.tab();

		expect(saveButton).toBeEnabled();
		expect(screen.queryByText(SAVE_HINT)).not.toBeInTheDocument();
	});

	it('enables save with an empty date when never is checked', async () => {
		renderModal({date: ''});

		await userEvent.click(screen.getByLabelText('never-review'));

		expect(screen.getByText('save')).toBeEnabled();
		expect(screen.queryByText(SAVE_HINT)).not.toBeInTheDocument();
	});

	it('disables save when the date is in the past', async () => {
		renderModal();

		const saveButton = screen.getByText('save');

		expect(saveButton).toBeEnabled();

		const dateInput = screen.getAllByRole('textbox')[0];

		await userEvent.clear(dateInput);
		await userEvent.type(dateInput, '01/01/2020 10:00 AM');
		await userEvent.tab();

		expect(saveButton).toBeDisabled();
	});

	it('clears the date when never is checked', async () => {
		const onSave = jest.fn().mockResolvedValue(true);

		renderModal({onSave});

		await userEvent.click(screen.getByLabelText('never-review'));
		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(onSave).toHaveBeenCalledWith(''));
	});
});
