/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openConfirmModal} from 'frontend-js-components-web';
import React from 'react';

import DiscardDraftButton from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/DiscardDraftButton';
import useDisabledDiscardDraft from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/useDisabledDiscardDraft';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/useDisabledDiscardDraft',
	() => jest.fn(() => false)
);

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openConfirmModal: jest.fn(({onConfirm}) => onConfirm(true)),
}));

const renderComponent = () => render(<DiscardDraftButton />);

describe('DiscardDraftButton', () => {
	it('calls openConfirmModal when the Discard Draft button is pressed', async () => {
		renderComponent();

		await userEvent.click(screen.getByText('discard-draft'));

		expect(openConfirmModal).toHaveBeenCalledWith({
			message:
				'are-you-sure-you-want-to-discard-current-draft-and-apply-latest-published-changes',
			onConfirm: expect.any(Function),
		});
	});

	it('does not disable the Discard Draft Button when useDisabledDiscardDraft returns false', () => {
		renderComponent();

		expect(screen.getByText('discard-draft')).not.toBeDisabled();
	});

	it('disables the Discard Draft Button when useDisabledDiscardDraft returns true', () => {
		useDisabledDiscardDraft.mockImplementation(jest.fn(() => true));

		renderComponent();

		expect(screen.getByText('discard-draft')).toBeDisabled();
	});
});
