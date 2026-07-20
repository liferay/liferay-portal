/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';
import {describe, expect, it, vi} from 'vitest';

import FeedbackActions from '../../components/FeedbackActions';

describe('FeedbackActions', () => {
	it('invokes the thumbs-up and thumbs-down callbacks on click', () => {
		const onThumbsDown = vi.fn();
		const onThumbsUp = vi.fn();

		render(
			<FeedbackActions
				onThumbsDown={onThumbsDown}
				onThumbsUp={onThumbsUp}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'Good response'}));

		expect(onThumbsUp).toHaveBeenCalledTimes(1);

		fireEvent.click(
			screen.getByRole('button', {name: 'Report Bad Result'})
		);

		expect(onThumbsDown).toHaveBeenCalledTimes(1);
	});

	it('disables both feedback buttons once feedback is given', () => {
		const onThumbsDown = vi.fn();
		const onThumbsUp = vi.fn();

		render(
			<FeedbackActions
				feedbackGiven
				onThumbsDown={onThumbsDown}
				onThumbsUp={onThumbsUp}
			/>
		);

		const thumbsUpButton = screen.getByRole('button', {
			name: 'Good response',
		});
		const reportButton = screen.getByRole('button', {
			name: 'Report Bad Result',
		});

		expect(thumbsUpButton).toBeDisabled();
		expect(reportButton).toBeDisabled();

		fireEvent.click(thumbsUpButton);
		fireEvent.click(reportButton);

		expect(onThumbsUp).not.toHaveBeenCalled();
		expect(onThumbsDown).not.toHaveBeenCalled();
	});
});
