/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import {
	Wizard,
	WizardStep,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/components/Wizard';

const Step = ({children}: {children: React.ReactNode}) => <div>{children}</div>;

const renderWizard = (backURL = '/back', onSubmit: () => void = () => {}) => {
	const user = userEvent.setup();

	render(
		<Wizard backURL={backURL}>
			<WizardStep onSubmit={onSubmit} title="Step 1">
				<Step>
					<h1>Step 1 Content</h1>
				</Step>
			</WizardStep>

			<WizardStep
				actionButton={
					<ClayButton type="submit">
						<span className="inline-item inline-item-before">
							<ClayIcon className="mr-1" symbol="export" />
						</span>

						{Liferay.Language.get('export')}
					</ClayButton>
				}
				title="Step 2"
			>
				<Step>
					<h1>Step 2 Content</h1>
				</Step>
			</WizardStep>
		</Wizard>
	);

	return {user};
};

describe('Wizard', () => {
	it('calls onSubmit on the last step', async () => {
		const handleSubmit = jest.fn();
		const {user} = renderWizard('/back', handleSubmit);

		await user.click(screen.getByRole('button', {name: 'continue'}));

		expect(screen.getByText('Step 2 Content')).toBeInTheDocument();

		const submitButton = screen.getByRole('button', {name: /export/i});
		expect(submitButton).toBeInTheDocument();
		expect(submitButton).toHaveAttribute('type', 'submit');
		await user.click(submitButton);

		expect(handleSubmit).toHaveBeenCalledTimes(1);
	});

	it('checks that the "Cancel" button has the correct link', () => {
		const backURL = '/initial-page';

		renderWizard(backURL);

		const cancelButton = screen.getByRole('link', {name: 'cancel'});

		expect(cancelButton).toBeInTheDocument();
		expect(cancelButton).toHaveAttribute('href', backURL);
	});

	it('navigates through the steps with "Continue" and "Previous" buttons', async () => {
		const {user} = renderWizard();

		expect(screen.getByText('Step 1 Content')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: 'continue'}));

		expect(screen.queryByText('Step 1 Content')).not.toBeInTheDocument();
		expect(screen.getByText('Step 2 Content')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: /previous/i}));

		expect(screen.getByText('Step 1 Content')).toBeInTheDocument();
		expect(screen.queryByText('Step 2 Content')).not.toBeInTheDocument();
	});

	it('renders the first step of the Wizard', () => {
		renderWizard();

		expect(screen.getByText('Step 1 Content')).toBeInTheDocument();
		expect(screen.queryByText('Step 2 Content')).not.toBeInTheDocument();
	});
});
