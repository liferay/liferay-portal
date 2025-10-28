/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {StyleErrorsContextProvider} from '@liferay/layout-js-components-web';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import PublishButton from '../../src/main/resources/META-INF/resources/js/style-book-editor/PublishButton';
import {config} from '../../src/main/resources/META-INF/resources/js/style-book-editor/config';

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/config',
	() => ({
		config: {
			pending: false,
			portletNamespace: 'portletNamespace',
			publishURL: 'publishURL',
			redirectURL: 'redirectURL',
		},
	})
);

const ERRORS = {
	defaultId: {background: {error: 'I am an error', value: 'error'}},
};

const renderComponent = ({handleSubmit = () => {}, errors}) => {
	const ref = React.createRef();

	return render(
		<StyleErrorsContextProvider initialState={errors}>
			<PublishButton formRef={ref} handleSubmit={handleSubmit} />
		</StyleErrorsContextProvider>
	);
};

describe('PublishButton', () => {
	it('renders PublishButton component', () => {
		const {getByLabelText} = renderComponent({});

		expect(getByLabelText('publish')).toBeInTheDocument();
	});

	it('opens a modal when the publish button is clicked', async () => {
		const handleSubmit = jest.fn();
		const {getByLabelText} = renderComponent({handleSubmit});
		const button = getByLabelText('publish');

		fireEvent.click(button);

		expect(getByLabelText('publishing-info')).toBeInTheDocument();
	});

	it('opens a modal when the publish button is clicked and there are errors', async () => {
		const handleSubmit = jest.fn();
		const {getByLabelText} = renderComponent({
			errors: ERRORS,
			handleSubmit,
		});
		const button = getByLabelText('publish');

		fireEvent.click(button);

		expect(getByLabelText('style-errors-detected')).toBeInTheDocument();
	});

	it('does not allow to publish if pending is true', () => {
		const handleSubmit = jest.fn((event) => event.preventDefault());
		const {getByLabelText} = renderComponent({
			handleSubmit,
		});
		const button = getByLabelText('publish');

		config.pending = true;

		fireEvent.click(button);

		expect(handleSubmit).not.toHaveBeenCalled();
		expect(button).toBeDisabled();
	});
});
