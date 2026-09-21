/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {ImageEditor} from '../src/main/resources/META-INF/resources/js/ImageEditor';
import {LoadedImage} from '../src/main/resources/META-INF/resources/js/imaging/loadImage';

const image = (previewUrl: string): LoadedImage => ({
	blob: new Blob(),
	fileName: 'a.jpg',
	height: 800,
	pixelUrls: {coarse: 'c.png', fine: 'f.png', medium: 'm.png', tiny: 't.png'},
	previewUrl,
	thumbUrl: 'thumb.jpg',
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

describe('the host configuration', () => {
	it('hides the crop tools when the host turns them off', () => {
		render(
			<ImageEditor
				config={{crop: false}}
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		expect(screen.queryByRole('button', {name: 'crop-area'})).toBeNull();
		expect(screen.queryByLabelText('ratio')).toBeNull();
		expect(
			screen.queryByRole('button', {name: 'rotate-90-degrees-clockwise'})
		).toBeNull();
	});

	it('shows the crop tools by default', () => {
		render(
			<ImageEditor
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		expect(
			screen.getByRole('button', {name: 'crop-area'})
		).toBeInTheDocument();
		expect(screen.getByLabelText('straighten')).toBeInTheDocument();
	});
});

describe('the annotations', () => {
	async function addRectangle() {
		fireEvent.click(screen.getByRole('button', {name: 'add-shape'}));

		fireEvent.click(
			within(screen.getByRole('grid', {name: 'add-shape'})).getByRole(
				'button',
				{name: 'rectangle'}
			)
		);

		const hits = document.querySelectorAll('.overlay-hit');

		await waitFor(() => expect(hits[hits.length - 1]).toHaveFocus());
	}

	it('pastes a copy beside the original and hands it the focus', async () => {
		const {container} = render(
			<ImageEditor
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		await addRectangle();

		const original = container.querySelector(
			'.overlay-hit'
		) as SVGRectElement;

		fireEvent.keyDown(original, {ctrlKey: true, key: 'c'});

		fireEvent.keyDown(
			screen.getByRole('region', {name: 'image-workspace'}),
			{ctrlKey: true, key: 'v'}
		);

		const hits = container.querySelectorAll('.overlay-hit');

		expect(hits).toHaveLength(2);

		await waitFor(() => expect(hits[1]).toHaveFocus());

		expect(
			screen.getAllByRole('button', {name: 'rectangle', pressed: true})
		).toHaveLength(1);
	});

	it('frees the aspect lock when the selection changes', async () => {
		const {container} = render(
			<ImageEditor
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		await addRectangle();

		const lock = () =>
			within(
				screen.getByRole('group', {name: 'selected-layer-x'})
			).getByRole('button', {name: 'lock-aspect-ratio'});

		fireEvent.click(lock());

		expect(lock()).toHaveAttribute('aria-pressed', 'true');

		await addRectangle();

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(2);
		expect(lock()).toHaveAttribute('aria-pressed', 'false');
	});
});

describe('the annotation tools of the host', () => {
	it('hides the annotate and layers panels when the host turns them off', () => {
		render(
			<ImageEditor
				config={{annotate: false}}
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		expect(screen.queryByText('annotate')).toBeNull();
		expect(screen.queryByRole('button', {name: 'add-shape'})).toBeNull();
	});

	it('drops the whole sidebar when nothing is left to show in it', () => {
		render(
			<ImageEditor
				config={{
					adjustments: false,
					annotate: false,
					crop: false,
					filters: false,
					frames: false,
				}}
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		expect(document.querySelector('.editor-sidebar')).toBeNull();
	});
});

describe('the view', () => {
	const stageWidth = () =>
		document.querySelector('.editor-stage')?.getAttribute('width');

	const measureWorkspace = (width: number, height: number) => {
		const workspace = screen.getByRole('region', {
			name: 'image-workspace',
		});

		Object.defineProperty(workspace, 'clientWidth', {
			configurable: true,
			value: width,
		});
		Object.defineProperty(workspace, 'clientHeight', {
			configurable: true,
			value: height,
		});
	};

	it('fits the picture to the workspace and brings it back after a zoom step', () => {
		render(
			<ImageEditor
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		measureWorkspace(648, 448);

		fireEvent.click(
			screen.getByRole('button', {name: 'fit-image-to-window'})
		);

		expect(stageWidth()).toBe('600');

		fireEvent.click(screen.getByRole('button', {name: 'zoom-in'}));

		expect(stageWidth()).toBe('900');

		fireEvent.click(
			screen.getByRole('button', {name: 'fit-image-to-window'})
		);

		expect(stageWidth()).toBe('600');
	});

	it('lists the keyboard shortcuts on request and closes the list', async () => {
		render(
			<ImageEditor
				image={image('blob:a')}
				{...HOST}
				spritemap="/icons.svg"
			/>
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'keyboard-shortcuts'})
		);

		expect(await screen.findByText('Esc')).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'close'}));

		await waitFor(() =>
			expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
		);
	});
});
