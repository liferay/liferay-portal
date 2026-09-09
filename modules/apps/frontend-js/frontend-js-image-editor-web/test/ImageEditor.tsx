/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {ImageEditor} from '../src/main/resources/META-INF/resources/js/ImageEditor';
import {LoadedImage} from '../src/main/resources/META-INF/resources/js/imaging/loadImage';

const image = (previewUrl: string): LoadedImage => ({
	blob: new Blob(),
	fileName: 'a.jpg',
	height: 800,
	previewUrl,
	type: 'image/jpeg',
	width: 1200,
});

const HOST = {onClose: () => {}, onSave: () => {}};

const undo = () => screen.getByRole('button', {name: 'undo'});

describe('the editing session is keyed to the image', () => {
	it('starts a fresh history when the image changes', () => {
		const {rerender} = render(
			<ImageEditor
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'rotate-90-degrees-clockwise'})
		);

		expect(undo()).toBeEnabled();

		rerender(
			<ImageEditor
				image={image('blob:b')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		expect(undo()).toBeDisabled();
	});

	it('keeps the history while the same image is re-rendered', () => {
		const same = image('blob:a');

		const {rerender} = render(
			<ImageEditor image={same} {...HOST} spritemap="/icons.svg" />
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'rotate-90-degrees-clockwise'})
		);

		rerender(<ImageEditor image={same} {...HOST} spritemap="/icons.svg" />);

		expect(undo()).toBeEnabled();
	});
});
