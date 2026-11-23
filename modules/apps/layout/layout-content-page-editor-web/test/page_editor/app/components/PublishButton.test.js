/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import PublishButton from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/PublishButton';
import useCheckFormsValidity from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/useCheckFormsValidity';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			pending: false,
			portletNamespace: 'portletNamespace',
			publishURL: 'publishURL',
			redirectURL: 'redirectURL',
		},
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/components/FormValidationModal',
	() => ({FormValidationModal: () => 'Form Validation Modal'})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/utils/useCheckFormsValidity',
	() => jest.fn()
);

const renderComponent = ({
	canPublish = true,
	onAddToLaunch,
	onPublish = () => {},
} = {}) => {
	const ref = React.createRef();

	return render(
		<PublishButton
			canPublish={canPublish}
			formRef={ref}
			label="publish"
			onAddToLaunch={onAddToLaunch}
			onPublish={onPublish}
		/>
	);
};

describe('PublishButton', () => {
	afterEach(() => {
		useCheckFormsValidity.mockClear();
	});

	it('renders PublishButton component', () => {
		useCheckFormsValidity.mockImplementation(
			() => () => Promise.resolve(true)
		);

		renderComponent();

		expect(
			screen.getByRole('button', {name: 'publish'})
		).toBeInTheDocument();
	});

	it('calls onPublish when the button is clicked', async () => {
		const onPublish = jest.fn(() => {});

		renderComponent({onPublish});

		const button = screen.getByRole('button', {name: 'publish'});

		await fireEvent.click(button);

		expect(onPublish).toHaveBeenCalled();
	});

	it('does not allow to publish if canPublish is false', () => {
		const onPublish = jest.fn(() => {});

		renderComponent({
			canPublish: false,
			onPublish,
		});

		const button = screen.getByRole('button', {name: 'publish'});

		fireEvent.click(button);

		expect(onPublish).not.toHaveBeenCalled();
		expect(button).toBeDisabled();
	});

	it('does not call onPublish when some form is invalid', async () => {
		useCheckFormsValidity.mockImplementation(
			() => () => Promise.resolve(false)
		);

		const onPublish = jest.fn(() => {});

		renderComponent({onPublish});

		const button = screen.getByRole('button', {name: 'publish'});

		await act(async () => {
			fireEvent.click(button);
		});

		expect(onPublish).not.toHaveBeenCalled();
	});

	it('does not render the add to launch option without onAddToLaunch', () => {
		renderComponent();

		expect(
			screen.queryByRole('button', {name: 'more-actions'})
		).not.toBeInTheDocument();
	});

	it('calls onAddToLaunch when the add to launch option is clicked', async () => {
		const onAddToLaunch = jest.fn();

		renderComponent({onAddToLaunch});

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'more-actions'}));
		});

		await act(async () => {
			fireEvent.click(screen.getByText('add-to-launch'));
		});

		expect(onAddToLaunch).toHaveBeenCalled();
	});
});
