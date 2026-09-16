/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React, {useReducer, useState} from 'react';

import '@testing-library/jest-dom';

import {AnnotatePanel} from '../../src/main/resources/META-INF/resources/js/annotations/AnnotatePanel';
import {EditorInstanceProvider} from '../../src/main/resources/META-INF/resources/js/chrome/instance';
import {LoadedImage} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';
import {Workspace} from '../../src/main/resources/META-INF/resources/js/stage/Workspace';
import {
	editorReducer,
	initialHistory,
} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {
	EditorHistory,
	Overlay,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const IMAGE: LoadedImage = {
	blob: new Blob(),
	fileName: 'test.jpg',
	height: 800,
	previewUrl: 'test.jpg',
	thumbUrl: 'thumb.jpg',
	type: 'image/jpeg',
	width: 1200,
};

const CAPTION: Overlay = {
	color: '#ffffff',
	fontFamily: 'sans-serif',
	fontSize: 48,
	id: 'text-1',
	kind: 'text',
	text: 'Hello',
	x: 100,
	y: 100,
};

function AnnotationHarness({
	start = () => initialHistory(IMAGE.width, IMAGE.height),
}: {
	start?: () => EditorHistory;
}) {
	const [history, dispatch] = useReducer(editorReducer, undefined, start);

	const [selectedId, setSelectedId] = useState<string | null>(null);

	return (
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<EditorInstanceProvider value="aie-">
				<Workspace
					aspectLocked={false}
					dispatch={dispatch}
					image={IMAGE}
					onAnnounce={() => {}}
					onCenterCrop={() => {}}
					onSelectOverlay={setSelectedId}
					onZoom={() => {}}
					onZoomActual={() => {}}
					onZoomFit={() => {}}
					selectedOverlayId={selectedId}
					showCrop
					showRecenter
					state={history.present}
					zoom={0.5}
				/>

				<AnnotatePanel
					area={history.present.crop}
					dispatch={dispatch}
					onAnnounce={() => {}}
				/>

				<button onClick={() => dispatch({type: 'undo'})}>undo</button>
			</EditorInstanceProvider>
		</ClayIconSpriteContext.Provider>
	);
}

const withCaption = () =>
	editorReducer(initialHistory(IMAGE.width, IMAGE.height), {
		overlay: CAPTION,
		type: 'add-overlay',
	});

const cropped = () =>
	editorReducer(initialHistory(IMAGE.width, IMAGE.height), {
		crop: {height: 400, width: 600, x: 600, y: 400},
		type: 'set-crop',
	});

const hit = (container: HTMLElement) =>
	container.querySelector('.overlay-hit') as SVGRectElement;

const caption = (container: HTMLElement) =>
	container.querySelector('.editor-workspace text') as SVGTextElement;

describe('text annotations', () => {
	it('writes a caption from the dialog and centers it on the crop', async () => {
		const {container} = render(<AnnotationHarness start={cropped} />);

		fireEvent.click(screen.getByRole('button', {name: 'add-text'}));

		const input = await screen.findByRole('textbox', {name: 'text'});

		fireEvent.change(input, {target: {value: 'Liferay'}});
		fireEvent.change(screen.getByLabelText('font-size'), {
			target: {value: '40'},
		});
		fireEvent.change(screen.getByLabelText('font-family'), {
			target: {value: 'serif'},
		});
		fireEvent.submit(input.closest('form') as HTMLFormElement);

		const text = caption(container);

		expect(text).toHaveTextContent('Liferay');
		expect(text).toHaveAttribute('font-size', '40');
		expect(text).toHaveAttribute('font-family', 'serif');

		// Center of the crop (900, 600), not of the image (600, 400): the
		// anchor sits on the baseline, so the text is centered by its
		// measured width and hangs from the vertical middle.

		expect(Number(text.getAttribute('x'))).toBe(900 - (7 * 40 * 0.6) / 2);
		expect(Number(text.getAttribute('y'))).toBe(600);

		expect(hit(container)).toHaveAttribute('aria-label', 'text-x');
	});

	it('refuses an empty caption', async () => {
		const {container} = render(<AnnotationHarness />);

		fireEvent.click(screen.getByRole('button', {name: 'add-text'}));

		const input = await screen.findByRole('textbox', {name: 'text'});

		expect(screen.getByRole('button', {name: 'add'})).toBeDisabled();

		fireEvent.submit(input.closest('form') as HTMLFormElement);

		expect(caption(container)).toBeNull();
	});

	it('moves a caption with the arrow keys and commits once on release', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		const target = hit(container);

		fireEvent.keyDown(target, {key: 'ArrowRight', shiftKey: true});
		fireEvent.keyDown(target, {key: 'ArrowDown'});

		expect(caption(container)).toHaveAttribute('x', '110');
		expect(caption(container)).toHaveAttribute('y', '101');

		fireEvent.keyUp(target, {key: 'ArrowDown'});

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(caption(container)).toHaveAttribute('x', '100');
		expect(caption(container)).toHaveAttribute('y', '100');
	});

	it('drags a caption with the pointer, in image units whatever the zoom', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		const target = hit(container);

		fireEvent.pointerDown(target, {clientX: 100, clientY: 100});
		fireEvent.pointerMove(target, {clientX: 160, clientY: 130});
		fireEvent.pointerUp(target);

		expect(caption(container)).toHaveAttribute('x', '220');
		expect(caption(container)).toHaveAttribute('y', '160');
	});

	it('reverts a cancelled drag entirely', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		const target = hit(container);

		fireEvent.pointerDown(target, {clientX: 100, clientY: 100});
		fireEvent.pointerMove(target, {clientX: 160, clientY: 140});
		fireEvent.pointerCancel(target);

		expect(caption(container)).toHaveAttribute('x', '100');
		expect(screen.getByRole('button', {name: 'undo'})).toBeInTheDocument();
	});

	it('edits a caption in place on double click', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.doubleClick(hit(container));

		const editor = container.querySelector(
			'.overlay-text-editor'
		) as HTMLInputElement;

		expect(editor.value).toBe('Hello');

		fireEvent.change(editor, {target: {value: 'Liferay'}});
		fireEvent.keyDown(editor, {key: 'Enter'});

		expect(container.querySelector('.overlay-text-editor')).toBeNull();
		expect(caption(container)).toHaveTextContent('Liferay');
	});

	it('abandons an in-place edit with Escape', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.doubleClick(hit(container));

		const editor = container.querySelector(
			'.overlay-text-editor'
		) as HTMLInputElement;

		fireEvent.change(editor, {target: {value: 'Liferay'}});
		fireEvent.keyDown(editor, {key: 'Escape'});

		expect(caption(container)).toHaveTextContent('Hello');
	});

	it('selects on focus and clears the selection on a click elsewhere', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.focus(hit(container));

		expect(container.querySelector('.focus-ring-outer')).not.toBeNull();

		fireEvent.blur(hit(container));

		expect(container.querySelector('.selection-ring')).not.toBeNull();

		fireEvent.pointerDown(
			screen.getByRole('region', {name: 'image-workspace'})
		);

		expect(container.querySelector('.selection-ring')).toBeNull();
	});

	it('removes a caption with Delete and hands the focus to the workspace', async () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.keyDown(hit(container), {key: 'Delete'});

		expect(hit(container)).toBeNull();

		await act(() => new Promise((resolve) => setTimeout(resolve, 20)));

		expect(
			screen.getByRole('region', {name: 'image-workspace'})
		).toHaveFocus();
	});

	it('paints the annotations under the dim layer and the marquee', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		const classes = [
			...(container.querySelectorAll(
				'.editor-stage > g > *'
			) as NodeListOf<Element>),
		].map((node) => node.getAttribute('class') ?? node.tagName);

		const annotations = [
			...(container.querySelectorAll(
				'.editor-stage > g > g'
			) as NodeListOf<Element>),
		].findIndex((node) => node.querySelector('.overlay-hit'));

		expect(annotations).toBeGreaterThan(-1);
		expect(classes.indexOf('crop-dim')).toBeGreaterThan(
			classes.indexOf('crop-move')
		);
	});
});
