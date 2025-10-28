/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ImageSelectorDescription} from '../../../../src/main/resources/META-INF/resources/page_editor/common/components/ImageSelectorDescription';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

describe('ImageSelectorDescription', () => {
	it('synchronizes imageDescription prop with input value', () => {
		render(
			<StoreMother.Component>
				<ImageSelectorDescription
					imageDescription="Random description"
					onImageDescriptionChanged={() => {}}
				/>
			</StoreMother.Component>
		);

		expect(
			screen.getByLabelText('image-description', {
				selector: 'input',
			}).value
		).toBe('Random description');
	});

	it('call onImageDescriptionChanged on blur', async () => {
		const onImageDescriptionChanged = jest.fn();

		render(
			<StoreMother.Component>
				<ImageSelectorDescription
					imageDescription=""
					onImageDescriptionChanged={onImageDescriptionChanged}
				/>
			</StoreMother.Component>
		);

		const input = screen.getByLabelText('image-description', {
			selector: 'input',
		});

		await userEvent.clear(input);
		await userEvent.type(input, 'Some other thing');

		fireEvent.blur(input);

		expect(onImageDescriptionChanged).toHaveBeenCalledWith(
			'Some other thing'
		);
	});
});
