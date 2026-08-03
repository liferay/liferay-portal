/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import UpdateReviewDateModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/UpdateReviewDateModalContent';

const FUTURE_REVIEW_DATE = '2099-12-31T10:00:00Z';
const PAST_REVIEW_DATE = '2020-01-01T10:00:00Z';

describe('UpdateReviewDateModalContent', () => {
	it('renders the review date field, the never review checkbox, and the actions', () => {
		render(
			<UpdateReviewDateModalContent
				closeModal={jest.fn()}
				onSave={jest.fn().mockResolvedValue(true)}
				reviewDate={FUTURE_REVIEW_DATE}
			/>
		);

		expect(screen.getByText('update-review-date')).toBeInTheDocument();
		expect(screen.getByLabelText('never-review')).toBeInTheDocument();
		expect(screen.getByText('save')).toBeInTheDocument();
		expect(screen.getByText('cancel')).toBeInTheDocument();
	});

	it('does not preselect never review when no review date is given', () => {
		render(
			<UpdateReviewDateModalContent
				closeModal={jest.fn()}
				onSave={jest.fn().mockResolvedValue(true)}
			/>
		);

		expect(screen.getByLabelText('never-review')).not.toBeChecked();
		expect(screen.getAllByRole('textbox')[0]).toBeEnabled();
	});

	it('does not clear the review date when nothing is picked', async () => {
		const onSave = jest.fn().mockResolvedValue(true);

		render(
			<UpdateReviewDateModalContent
				closeModal={jest.fn()}
				onSave={onSave}
			/>
		);

		await userEvent.click(screen.getByText('save'));

		expect(onSave).not.toHaveBeenCalled();
	});

	it('saves the review date as an ISO string with a trailing Z', async () => {
		const closeModal = jest.fn();
		const onSave = jest.fn().mockResolvedValue(true);

		render(
			<UpdateReviewDateModalContent
				closeModal={closeModal}
				onSave={onSave}
				reviewDate={FUTURE_REVIEW_DATE}
			/>
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() =>
			expect(onSave).toHaveBeenCalledWith('2099-12-31T10:00:00Z')
		);

		await waitFor(() => expect(closeModal).toHaveBeenCalled());
	});

	it('does not save an unchanged past review date', async () => {
		const onSave = jest.fn().mockResolvedValue(true);

		render(
			<UpdateReviewDateModalContent
				closeModal={jest.fn()}
				onSave={onSave}
				reviewDate={PAST_REVIEW_DATE}
			/>
		);

		await userEvent.click(screen.getByText('save'));

		expect(onSave).not.toHaveBeenCalled();
		expect(
			screen.getByText('the-date-entered-is-in-the-past')
		).toBeInTheDocument();
	});

	it('disables save when the review date is in the past', async () => {
		render(
			<UpdateReviewDateModalContent
				closeModal={jest.fn()}
				onSave={jest.fn().mockResolvedValue(true)}
				reviewDate={FUTURE_REVIEW_DATE}
			/>
		);

		const saveButton = screen.getByText('save');

		expect(saveButton).toBeEnabled();

		const dateInput = screen.getAllByRole('textbox')[0];

		await userEvent.clear(dateInput);
		await userEvent.type(dateInput, '01/01/2020 10:00 AM');
		await userEvent.tab();

		expect(saveButton).toBeDisabled();
	});

	it('clears the review date when never review is checked', async () => {
		const onSave = jest.fn().mockResolvedValue(true);

		render(
			<UpdateReviewDateModalContent
				closeModal={jest.fn()}
				onSave={onSave}
				reviewDate={FUTURE_REVIEW_DATE}
			/>
		);

		await userEvent.click(screen.getByLabelText('never-review'));
		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(onSave).toHaveBeenCalledWith(''));
	});
});
