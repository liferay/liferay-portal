/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {useSaveController} from '../../src/main/resources/META-INF/resources/js/hooks/useSaveController';
import {exportEditedImage} from '../../src/main/resources/META-INF/resources/js/imaging/exportImage';
import {LoadedImage} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';
import {initialEditState} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';

jest.mock(
	'../../src/main/resources/META-INF/resources/js/imaging/exportImage',
	() => ({exportEditedImage: jest.fn()})
);

const IMAGE: LoadedImage = {
	blob: new Blob(),
	fileName: 'test.jpg',
	height: 800,
	previewUrl: 'test.jpg',
	type: 'image/jpeg',
	width: 1200,
};

const STATE = initialEditState(IMAGE.width, IMAGE.height);

const exported = {blob: new Blob(['x']), fileName: 'test-edited.jpg'};

function Harness({onClose, onSave}: {onClose: () => void; onSave: jest.Mock}) {
	const announce = jest.fn();

	const {handleSave, saveError, saving} = useSaveController(
		IMAGE,
		STATE,
		onSave,
		announce,
		onClose
	);

	return (
		<>
			<button disabled={saving} onClick={handleSave}>
				save
			</button>

			{saveError && <div role="alert">failed</div>}
		</>
	);
}

beforeEach(() => {
	(exportEditedImage as jest.Mock).mockReset();
});

describe('useSaveController', () => {
	it('exports, hands the result to the host and closes', async () => {
		(exportEditedImage as jest.Mock).mockResolvedValue(exported);

		const onClose = jest.fn();
		const onSave = jest.fn().mockResolvedValue(undefined);

		render(<Harness onClose={onClose} onSave={onSave} />);

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'save'}));
		});

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));

		expect(onSave).toHaveBeenCalledWith(
			{...exported, state: STATE},
			expect.any(AbortSignal)
		);
	});

	it('shows the error and stays open when the host refuses', async () => {
		(exportEditedImage as jest.Mock).mockResolvedValue(exported);

		const onClose = jest.fn();
		const onSave = jest.fn().mockRejectedValue(new Error('refused'));

		render(<Harness onClose={onClose} onSave={onSave} />);

		await act(async () => {
			fireEvent.click(screen.getByRole('button', {name: 'save'}));
		});

		await waitFor(() =>
			expect(screen.getByRole('alert')).toHaveTextContent('failed')
		);

		expect(onClose).not.toHaveBeenCalled();
		expect(screen.getByRole('button', {name: 'save'})).toBeEnabled();
	});
});
