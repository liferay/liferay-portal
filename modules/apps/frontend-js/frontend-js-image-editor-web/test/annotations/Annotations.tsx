/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {act, fireEvent, render, screen, within} from '@testing-library/react';
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

/**
 * Shapes live behind a menu of drawings, so adding one is two steps: open
 * the menu, then pick the cell. The cells are named rather than labelled
 * in text, and the query goes through the grid because the same name also
 * belongs to the stage node it creates.
 */
function addShape(shape: string) {
	fireEvent.click(screen.getByRole('button', {name: 'add-shape'}));

	fireEvent.click(
		within(screen.getByRole('grid', {name: 'add-shape'})).getByRole(
			'button',
			{name: shape}
		)
	);
}

const shape = (container: HTMLElement) =>
	container.querySelector(
		'.editor-workspace rect[fill]:not([class])'
	) as SVGRectElement;

describe('shapes and arrows', () => {
	it('offers the rectangle, the square, the circle and the arrow from one menu', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		expect(shape(container)).toHaveAttribute('width', '300');
		expect(shape(container)).toHaveAttribute('height', '120');
		expect(hit(container)).toHaveAttribute('aria-label', 'rectangle');

		addShape('square');

		const squares = container.querySelectorAll(
			'.editor-workspace rect[fill]:not([class])'
		);

		expect(squares[1].getAttribute('width')).toBe(
			squares[1].getAttribute('height')
		);

		addShape('circle');

		expect(
			container.querySelectorAll('.editor-workspace ellipse')
		).toHaveLength(1);

		addShape('arrow');

		expect(
			container.querySelectorAll('.editor-workspace polygon')
		).toHaveLength(1);
		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(4);
	});

	it('centers a new shape on the crop, not on the image', () => {
		const {container} = render(<AnnotationHarness start={cropped} />);

		addShape('circle');

		const target = hit(container);

		const centerX =
			Number(target.getAttribute('x')) +
			Number(target.getAttribute('width')) / 2;
		const centerY =
			Number(target.getAttribute('y')) +
			Number(target.getAttribute('height')) / 2;

		// Center of the crop (900, 600), not of the image (600, 400).

		expect(Math.round(centerX)).toBe(900);
		expect(Math.round(centerY)).toBe(600);
	});

	it('resizes a rectangle from a corner, and keeps the proportions with Shift', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.focus(hit(container));

		// Four corners, four edges and the rotation knob.

		const handles = container.querySelectorAll('.object-handle');

		expect(handles).toHaveLength(9);

		// The bottom right corner sits at (750, 460); dragging it 50 by
		// 20 screen pixels at 50% zoom moves it 100 by 40 image pixels,
		// and the box grows from its center on both sides.

		const corner = handles[2];

		fireEvent.pointerDown(corner, {clientX: 0, clientY: 0});
		fireEvent.pointerMove(corner, {clientX: 50, clientY: 20});
		fireEvent.pointerUp(corner);

		expect(shape(container)).toHaveAttribute('width', '500');
		expect(shape(container)).toHaveAttribute('height', '200');
		expect(shape(container)).toHaveAttribute('x', '350');

		fireEvent.pointerDown(corner, {clientX: 0, clientY: 0});
		fireEvent.pointerMove(corner, {
			clientX: 50,
			clientY: 0,
			shiftKey: true,
		});
		fireEvent.pointerUp(corner);

		const width = Number(shape(container).getAttribute('width'));
		const height = Number(shape(container).getAttribute('height'));

		expect(width / height).toBeCloseTo(2.5, 1);
	});

	it('stretches one side from an edge handle and anchors the other', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.focus(hit(container));

		// The east edge handle sits at (750, 400).

		const edge = container.querySelectorAll('.object-handle')[5];

		fireEvent.pointerDown(edge, {clientX: 0, clientY: 0});
		fireEvent.pointerMove(edge, {clientX: 50, clientY: 30});
		fireEvent.pointerUp(edge);

		expect(shape(container)).toHaveAttribute('width', '400');
		expect(shape(container)).toHaveAttribute('height', '120');
		expect(shape(container)).toHaveAttribute('x', '450');
	});

	it('rotates a rectangle with the knob, snapping to 15 degrees with Shift', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.focus(hit(container));

		// The knob hangs 48 image pixels above the top edge, at (600, 292);
		// swinging it to the right of the center is a quarter turn.

		const knob = container.querySelector(
			'.object-handle-rotate'
		) as SVGCircleElement;

		fireEvent.pointerDown(knob, {clientX: 0, clientY: 0});
		fireEvent.pointerMove(knob, {clientX: 54, clientY: 54});

		expect(
			shape(container).closest('g[transform]')?.getAttribute('transform')
		).toBe('rotate(90 600 400)');

		fireEvent.pointerMove(knob, {clientX: 54, clientY: 50, shiftKey: true});
		fireEvent.pointerUp(knob);

		expect(
			shape(container).closest('g[transform]')?.getAttribute('transform')
		).toBe('rotate(90 600 400)');

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(shape(container).closest('g[transform]')).toBeNull();
	});

	it('aims an arrow by its tip and leaves the tail where it was', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('arrow');

		fireEvent.focus(hit(container));

		const ends = container.querySelectorAll('.object-handle');

		expect(ends).toHaveLength(2);

		// The tip sits at (720, 400); 50 screen pixels down is 100 image
		// pixels down.

		fireEvent.pointerDown(ends[1], {clientX: 0, clientY: 0});
		fireEvent.pointerMove(ends[1], {clientX: 0, clientY: 50});
		fireEvent.pointerUp(ends[1]);

		const shaft = container.querySelector(
			'.editor-workspace line[stroke-linecap="round"]'
		) as SVGLineElement;

		expect(shaft).toHaveAttribute('x1', '480');
		expect(shaft).toHaveAttribute('y1', '400');
		expect(
			container.querySelector('.editor-workspace polygon')
		).toHaveAttribute('points', expect.stringMatching(/^720,500/));

		// The tail pivots on the tip.

		fireEvent.pointerDown(ends[0], {clientX: 0, clientY: 0});
		fireEvent.pointerMove(ends[0], {clientX: -50, clientY: 0});
		fireEvent.pointerUp(ends[0]);

		expect(shaft).toHaveAttribute('x1', '380');
		expect(
			container.querySelector('.editor-workspace polygon')
		).toHaveAttribute('points', expect.stringMatching(/^720,500/));
	});

	it('reverts a cancelled resize entirely', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.focus(hit(container));

		const corner = container.querySelectorAll('.object-handle')[2];

		fireEvent.pointerDown(corner, {clientX: 0, clientY: 0});
		fireEvent.pointerMove(corner, {clientX: 50, clientY: 20});

		expect(shape(container)).toHaveAttribute('width', '500');

		fireEvent.pointerCancel(corner);

		expect(shape(container)).toHaveAttribute('width', '300');
	});

	it('roves a single tab stop through the annotate controls', () => {
		render(<AnnotationHarness />);

		const addText = screen.getByRole('button', {name: 'add-text'});

		addText.focus();

		fireEvent.keyDown(addText, {key: 'ArrowRight'});

		expect(document.activeElement).toHaveAccessibleName('add-shape');

		// One tab stop for the whole panel, wherever the roving index
		// happens to be sitting.

		expect(
			document.querySelectorAll(
				'.editor-annotate-actions [data-index][tabindex="0"]'
			)
		).toHaveLength(1);

		// On a menu button the vertical arrows belong to the menu, so
		// they must not walk the panel.

		fireEvent.keyDown(document.activeElement as Element, {
			key: 'ArrowUp',
		});

		expect(document.activeElement).toHaveAccessibleName('add-shape');

		fireEvent.keyDown(document.activeElement as Element, {key: 'Home'});

		expect(document.activeElement).toHaveAccessibleName('add-text');
	});
});
