/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import PageToolbar from '../../../src/main/resources/META-INF/resources/js/components/PageToolbar.es';
import {STATUS_TYPES} from '../../../src/main/resources/META-INF/resources/js/utils/constants.es';

import '@testing-library/jest-dom';

function renderTestPageToolbar(props) {
	return render(
		<PageToolbar
			onCancel="cancel"
			onChangeActive={jest.fn()}
			onPublish={jest.fn()}
			status={STATUS_TYPES.ACTIVE}
			submitDisabled={false}
			{...props}
		/>
	);
}

describe('PageToolbar', () => {
	it('disables the save button', () => {
		const {getByText} = renderTestPageToolbar({submitDisabled: true});

		expect(getByText('save')).toBeDisabled();
	});

	it('enables the save button', () => {
		const {getByText} = renderTestPageToolbar();

		expect(getByText('save')).toBeEnabled();
	});

	it('shows the active state', () => {
		const {getByLabelText} = renderTestPageToolbar();

		expect(getByLabelText('active')).toHaveAttribute('checked');
	});

	it('shows the inactive state', () => {
		const {getByLabelText} = renderTestPageToolbar({
			status: STATUS_TYPES.INACTIVE,
		});

		expect(getByLabelText('inactive')).not.toHaveAttribute('checked');
	});

	it('calls the onChangeActive function', () => {
		const onChangeActive = jest.fn();

		const {getByLabelText} = renderTestPageToolbar({onChangeActive});

		fireEvent.click(getByLabelText('active'));

		expect(onChangeActive.mock.calls.length).toBe(1);
	});
});
