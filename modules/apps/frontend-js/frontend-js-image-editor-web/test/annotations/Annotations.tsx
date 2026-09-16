/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {act, fireEvent, render, screen, within} from '@testing-library/react';
import React, {useReducer, useRef, useState} from 'react';

import '@testing-library/jest-dom';

import {AnnotatePanel} from '../../src/main/resources/META-INF/resources/js/annotations/AnnotatePanel';
import {LayersPanel} from '../../src/main/resources/META-INF/resources/js/annotations/LayersPanel';
import {
	EditorInstanceProvider,
	EditorRootProvider,
} from '../../src/main/resources/META-INF/resources/js/chrome/instance';
import {
	ANNOTATE_TOOLS,
	AnnotateTool,
} from '../../src/main/resources/META-INF/resources/js/editorConfig';
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
	tools = ANNOTATE_TOOLS,
}: {
	start?: () => EditorHistory;
	tools?: AnnotateTool[];
}) {
	const [history, dispatch] = useReducer(editorReducer, undefined, start);

	const [selectedId, setSelectedId] = useState<string | null>(null);

	const [proportional, setProportional] = useState(false);

	const [multiIds, setMultiIds] = useState<string[]>([]);

	const [clipboard, setClipboard] = useState<Overlay | null>(null);

	const selectAsEditor = (id: string | null) => {
		setSelectedId(id);

		setMultiIds((ids) => (id !== null && ids.includes(id) ? ids : []));
	};

	const toggleMulti = (id: string) => {
		const base = multiIds.length
			? multiIds
			: selectedId && selectedId !== id
				? [selectedId]
				: [];

		const next = multiIds.includes(id)
			? multiIds.filter((candidate) => candidate !== id)
			: [...base, id];

		setMultiIds(next.length >= 2 ? next : []);

		setSelectedId(id);
	};

	return (
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<EditorInstanceProvider value="aie-">
				<Workspace
					aspectLocked={false}
					dispatch={dispatch}
					image={IMAGE}
					multiSelectedIds={multiIds}
					onAnnounce={() => {}}
					onCenterCrop={() => {}}
					onCopyOverlay={(id) =>
						setClipboard(
							history.present.overlays.find(
								(overlay) => overlay.id === id
							) ?? null
						)
					}
					onMultiSelectToggle={toggleMulti}
					onPasteOverlay={() => {
						if (clipboard) {
							dispatch({
								overlay: {
									...clipboard,
									id: `${clipboard.id}-copy`,
									x: clipboard.x + 16,
									y: clipboard.y + 16,
								},
								type: 'add-overlay',
							});
						}
					}}
					onSelectOverlay={selectAsEditor}
					onZoom={() => {}}
					onZoomActual={() => {}}
					onZoomFit={() => {}}
					proportional={proportional}
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
					tools={tools}
				/>

				<LayersPanel
					dispatch={dispatch}
					multiSelectedIds={multiIds}
					onAnnounce={() => {}}
					onProportionalChange={setProportional}
					onSelect={selectAsEditor}
					overlays={history.present.overlays}
					proportional={proportional}
					selectedId={selectedId}
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

const layerNames = () =>
	[...document.querySelectorAll('.editor-layer-name')].map((node) =>
		node.getAttribute('aria-label')
	);

/**
 * The stage node and the layer row share the annotation's name, so a row
 * is looked up inside the list.
 */
const row = (name: string) =>
	within(
		document.querySelector('.editor-layer-list') as HTMLElement
	).getByRole('button', {name});

describe('layers', () => {
	it('lists the layers topmost first and reorders them from their rows', () => {
		render(<AnnotationHarness start={withCaption} />);

		addShape('rectangle');

		expect(layerNames()).toEqual(['rectangle', 'text-x']);

		fireEvent.click(
			screen.getAllByRole('button', {name: 'move-x-down'})[0]
		);

		expect(layerNames()).toEqual(['text-x', 'rectangle']);

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(layerNames()).toEqual(['rectangle', 'text-x']);
	});

	it('hides the panel until there is a layer, and again after the last one goes', () => {
		render(<AnnotationHarness />);

		expect(screen.queryByText('layers')).toBeNull();

		addShape('rectangle');

		expect(screen.getByText('layers')).toBeInTheDocument();

		fireEvent.keyDown(
			screen.getByRole('button', {name: 'rectangle', pressed: true}),
			{key: 'Delete'}
		);

		expect(screen.queryByText('layers')).toBeNull();
	});

	it('edits the selected layer from the properties', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		expect(shape(container)).toHaveAttribute('fill', '#0b5fff');

		// Width commits on Enter.

		const widthInput = screen.getByLabelText('width');

		fireEvent.change(widthInput, {target: {value: '500'}});
		fireEvent.keyDown(widthInput, {key: 'Enter'});

		expect(shape(container)).toHaveAttribute('width', '500');

		// Color previews while the picker moves and commits on blur. A
		// shape has a color, only a caption has a text color.

		expect(screen.queryByLabelText('text-color')).toBeNull();

		const colorInput = screen.getByLabelText('color');

		fireEvent.change(colorInput, {target: {value: '#00ff00'}});

		expect(shape(container)).toHaveAttribute('fill', '#00ff00');

		fireEvent.blur(colorInput);

		// Opacity wraps the node in a translucent group.

		const opacityInput = screen.getByLabelText('opacity');

		fireEvent.change(opacityInput, {target: {value: '50'}});
		fireEvent.keyDown(opacityInput, {key: 'Enter'});

		expect(shape(container).closest('g[opacity]')).toHaveAttribute(
			'opacity',
			'0.5'
		);

		// Position, which is what makes dragging optional for a pointer
		// user who cannot drag (WCAG 2.2, 2.5.7).

		const xInput = screen.getByLabelText('x-position');
		const yInput = screen.getByLabelText('y-position');

		fireEvent.change(xInput, {target: {value: '120'}});
		fireEvent.keyDown(xInput, {key: 'Enter'});
		fireEvent.change(yInput, {target: {value: '340'}});
		fireEvent.keyDown(yInput, {key: 'Enter'});

		expect(shape(container)).toHaveAttribute('x', '120');
		expect(shape(container)).toHaveAttribute('y', '340');

		// Rotation spins the whole interactive group around the center.

		const rotationInput = screen.getByLabelText('rotation');

		fireEvent.change(rotationInput, {target: {value: '45'}});
		fireEvent.keyDown(rotationInput, {key: 'Enter'});

		expect(
			shape(container).closest('g[transform]')?.getAttribute('transform')
		).toContain('rotate(45');
	});

	it('steps a number with the arrows and clamps it to its range', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		const opacityInput = screen.getByLabelText('opacity');

		fireEvent.keyDown(opacityInput, {key: 'ArrowUp', shiftKey: true});

		expect(opacityInput).toHaveValue(100);

		fireEvent.keyDown(opacityInput, {key: 'ArrowDown', shiftKey: true});
		fireEvent.keyDown(opacityInput, {key: 'ArrowDown'});

		expect(opacityInput).toHaveValue(89);
		expect(shape(container).closest('g[opacity]')).toHaveAttribute(
			'opacity',
			'0.89'
		);

		fireEvent.change(opacityInput, {target: {value: 'abc'}});
		fireEvent.blur(opacityInput);

		expect(opacityInput).toHaveValue(89);
	});

	it('leaves a shape free to stretch, and locks on request', () => {
		render(<AnnotationHarness />);

		addShape('rectangle');

		const padlock = screen.getByRole('button', {
			name: 'lock-aspect-ratio',
		});

		expect(padlock).toHaveAttribute('aria-pressed', 'false');

		const width = screen.getByLabelText('width') as HTMLInputElement;
		const height = screen.getByLabelText('height') as HTMLInputElement;

		fireEvent.change(width, {target: {value: '200'}});
		fireEvent.keyDown(width, {key: 'Enter'});

		expect(height).toHaveValue(120);

		// Locked, the side that was not typed follows.

		fireEvent.click(padlock);

		expect(padlock).toHaveAttribute('aria-pressed', 'true');

		fireEvent.change(width, {target: {value: '100'}});
		fireEvent.keyDown(width, {key: 'Enter'});

		expect(height).toHaveValue(60);
	});

	it('syncs the selection between the stage and the layers panel', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		addShape('rectangle');

		const hits = container.querySelectorAll('.overlay-hit');

		// Focusing the caption on the stage presses its row.

		fireEvent.focus(hits[0]);

		expect(
			screen.getByRole('button', {name: 'text-x', pressed: true})
		).toBeInTheDocument();

		// Pressing the rectangle row rings it on the stage.

		fireEvent.blur(hits[0]);

		fireEvent.click(
			screen.getByRole('button', {name: 'rectangle', pressed: false})
		);

		expect(container.querySelectorAll('.selection-ring')).toHaveLength(1);
		expect(
			screen
				.getByText('selected-layer-x')
				.closest('.editor-layer-properties')
		).toContainElement(screen.getByLabelText('width'));
	});

	it('jumps from the stage node to its properties on Enter', async () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.keyDown(hit(container), {key: 'Enter'});

		await act(() => new Promise((resolve) => setTimeout(resolve, 20)));

		expect(document.activeElement?.id).toBe('aie-layer-prop-color');
	});

	it('jumps from a layer row to its node on the stage on Enter', async () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		const row = screen.getByRole('button', {
			name: 'rectangle',
			pressed: true,
		});

		expect(row).toHaveAttribute(
			'aria-describedby',
			'aie-layer-name-description'
		);

		fireEvent.keyDown(row, {key: 'Enter'});

		await act(() => new Promise((resolve) => setTimeout(resolve, 20)));

		expect(document.activeElement).toBe(hit(container));
	});

	it('duplicates a layer from its row and selects the copy', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.click(screen.getByRole('button', {name: 'duplicate-x'}));

		expect(layerNames()).toEqual(['rectangle', 'rectangle']);

		// The copy is selected: one pressed row, one ring on the stage, and
		// the copy sits offset from the original.

		expect(
			screen.getAllByRole('button', {name: 'rectangle', pressed: true})
		).toHaveLength(1);
		expect(container.querySelectorAll('.selection-ring')).toHaveLength(1);

		const shapes = container.querySelectorAll(
			'.editor-workspace rect[fill]:not([class])'
		);

		expect(Number(shapes[1].getAttribute('x'))).toBe(
			Number(shapes[0].getAttribute('x')) + 16
		);
	});

	it('removes a layer from its row and keeps the focus in the list', async () => {
		render(<AnnotationHarness start={withCaption} />);

		addShape('rectangle');

		fireEvent.click(screen.getAllByRole('button', {name: 'delete-x'})[0]);

		expect(layerNames()).toEqual(['text-x']);

		await act(() => new Promise((resolve) => setTimeout(resolve, 20)));

		expect(document.activeElement).toBe(row('text-x'));
	});

	it('roves through the rows and skips the disabled actions', () => {
		render(<AnnotationHarness start={withCaption} />);

		addShape('rectangle');

		const top = row('rectangle');

		act(() => top.focus());

		expect(
			document.querySelectorAll('.editor-layer-list [tabindex="0"]')
		).toHaveLength(1);

		// The topmost layer cannot move up, so the first arrow to the right
		// lands on "move down".

		fireEvent.keyDown(top, {key: 'ArrowRight'});

		expect(document.activeElement).toHaveAccessibleName('move-x-down');

		// Down a row lands on the same column when it is enabled, and on
		// the row's name when it is not: the bottom layer cannot move down.

		fireEvent.keyDown(document.activeElement as Element, {
			key: 'ArrowDown',
		});

		expect(document.activeElement).toBe(row('text-x'));

		fireEvent.keyDown(document.activeElement as Element, {
			key: 'ArrowRight',
		});

		expect(document.activeElement).toHaveAccessibleName('move-x-up');

		fireEvent.keyDown(document.activeElement as Element, {key: 'Home'});

		expect(document.activeElement).toBe(top);
	});

	it('changes the caption and its font from the properties', () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.click(row('text-x'));

		expect(screen.getByLabelText('text-color')).toBeInTheDocument();

		const textInput = screen.getByLabelText('text');

		fireEvent.change(textInput, {target: {value: 'Liferay'}});
		fireEvent.keyDown(textInput, {key: 'Enter'});

		expect(caption(container)).toHaveTextContent('Liferay');

		fireEvent.change(screen.getByLabelText('font-family'), {
			target: {value: 'monospace'},
		});

		expect(caption(container)).toHaveAttribute('font-family', 'monospace');

		const size = screen.getByLabelText('font-size');

		fireEvent.change(size, {target: {value: '72'}});
		fireEvent.keyDown(size, {key: 'Enter'});

		expect(caption(container)).toHaveAttribute('font-size', '72');

		// An empty caption is refused: the field falls back to the text.

		fireEvent.change(textInput, {target: {value: '   '}});
		fireEvent.blur(textInput);

		expect(textInput).toHaveValue('Liferay');
	});

	it('aims an arrow from its tip fields and opens its head', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('arrow');

		// Its two ends are the properties, and rotation is not one of
		// them: where an arrow points is already said by its ends.

		expect(screen.queryByLabelText('rotation')).toBeNull();

		const tipY = screen.getByLabelText('tip-y-position');
		const tailY = Number(
			screen.getByLabelText('y-position').getAttribute('value')
		);

		fireEvent.change(tipY, {target: {value: '120'}});
		fireEvent.keyDown(tipY, {key: 'Enter'});

		expect(screen.getByLabelText('tip-y-position')).toHaveValue(120);
		expect(screen.getByLabelText('y-position')).toHaveValue(tailY);

		// The open head is the same two barbs left as strokes, and its
		// shaft runs the whole way to the tip.

		fireEvent.change(screen.getByLabelText('arrow-head'), {
			target: {value: 'open'},
		});

		expect(
			container.querySelectorAll('.editor-workspace polygon')
		).toHaveLength(0);

		const shaft = container.querySelector(
			'.editor-workspace line[stroke-linecap="round"]'
		) as SVGLineElement;

		expect(Number(shaft.getAttribute('y2'))).toBe(120);
		expect(Number(shaft.getAttribute('y1'))).toBe(tailY);

		const thickness = screen.getByLabelText('thickness');

		fireEvent.change(thickness, {target: {value: '20'}});
		fireEvent.keyDown(thickness, {key: 'Enter'});

		expect(shaft).toHaveAttribute('stroke-width', '20');
	});

	it('dresses a rectangle in the hand-drawn style and back', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		expect(shape(container)).toBeInTheDocument();

		fireEvent.change(screen.getByLabelText('style'), {
			target: {value: 'sketchy'},
		});

		expect(shape(container)).toBeNull();

		const path = container.querySelector(
			'.editor-workspace path[fill="#0b5fff"]'
		) as SVGPathElement;

		const wobble = path.getAttribute('d')!;

		expect(wobble.endsWith('Z')).toBe(true);

		// The seed lives in the state, so a re-render redraws the same
		// wobble instead of a new one.

		fireEvent.click(row('rectangle'));

		expect(
			container
				.querySelector('.editor-workspace path[fill="#0b5fff"]')
				?.getAttribute('d')
		).toBe(wobble);

		fireEvent.change(screen.getByLabelText('style'), {
			target: {value: 'clean'},
		});

		expect(shape(container)).toBeInTheDocument();
	});

	it('draws no border until one is asked for', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		expect(shape(container)).not.toHaveAttribute('stroke');

		const width = screen.getByLabelText('border-width');

		fireEvent.change(width, {target: {value: '4'}});
		fireEvent.keyDown(width, {key: 'Enter'});

		expect(shape(container)).toHaveAttribute('stroke-width', '4');
		expect(shape(container)).toHaveAttribute('stroke', '#272833');

		const color = screen.getByLabelText('border-color');

		fireEvent.change(color, {target: {value: '#ff0000'}});
		fireEvent.blur(color);

		expect(shape(container)).toHaveAttribute('stroke', '#ff0000');

		fireEvent.change(width, {target: {value: '0'}});
		fireEvent.keyDown(width, {key: 'Enter'});

		expect(shape(container)).not.toHaveAttribute('stroke');
	});

	it('keeps a 24 pixel target on an annotation smaller than that', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		for (const [label, value] of [
			['width', '8'],
			['height', '8'],
		]) {
			const field = screen.getByLabelText(label);

			fireEvent.change(field, {target: {value}});
			fireEvent.keyDown(field, {key: 'Enter'});
		}

		// What is painted shrinks to what was asked for; what can be hit
		// does not go below the minimum (WCAG 2.2, 2.5.8). The harness
		// renders at 50%, so those 24 screen pixels are 48 image units.

		expect(shape(container)).toHaveAttribute('width', '8');
		expect(hit(container)).toHaveAttribute('width', '48');
		expect(hit(container)).toHaveAttribute('height', '48');

		expect(Number(hit(container).getAttribute('x'))).toBe(
			Number(shape(container).getAttribute('x')) - 20
		);
	});

	it('hides the stretch handles while the proportions are locked', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.focus(hit(container));

		expect(container.querySelectorAll('.object-handle')).toHaveLength(9);

		fireEvent.click(
			screen.getByRole('button', {name: 'lock-aspect-ratio'})
		);

		expect(container.querySelectorAll('.object-handle')).toHaveLength(5);
	});
});

describe('two editors on one page', () => {
	function ScopedHarness({label}: {label: string}) {
		const rootRef = useRef<HTMLDivElement>(null);

		return (
			<div data-editor={label} ref={rootRef}>
				<EditorInstanceProvider value={`${label}-`}>
					<EditorRootProvider value={rootRef}>
						<AnnotationHarness />
					</EditorRootProvider>
				</EditorInstanceProvider>
			</div>
		);
	}

	it('hands the focus to its own workspace after the last deletion', async () => {
		const {container} = render(
			<>
				<ScopedHarness label="one" />
				<ScopedHarness label="two" />
			</>
		);

		const second = container.querySelector(
			'[data-editor="two"]'
		) as HTMLElement;

		fireEvent.click(
			within(second).getByRole('button', {name: 'add-shape'})
		);

		// The shape menu portals to the body; the one open right now is
		// the second editor's.

		fireEvent.click(
			within(screen.getByRole('grid', {name: 'add-shape'})).getByRole(
				'button',
				{name: 'rectangle'}
			)
		);

		fireEvent.click(within(second).getByRole('button', {name: 'delete-x'}));

		await act(() => new Promise((resolve) => setTimeout(resolve, 20)));

		expect(document.activeElement).toBe(
			second.querySelector('.editor-workspace')
		);
	});
});

describe('groups and the clipboard', () => {
	it('moves a shift-built group together, and only moves it', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');
		addShape('circle');

		const hits = container.querySelectorAll('.overlay-hit');

		// Select the circle plainly, then Shift+click the rectangle: the
		// pair is seeded from the standing selection.

		fireEvent.focus(hits[1]);
		fireEvent.pointerDown(hits[0], {shiftKey: true});

		// Both wear a ring, and the manipulation handles are gone: a
		// group grants movement and nothing else.

		expect(
			container.querySelectorAll('.selection-ring, .focus-ring-outer')
				.length
		).toBeGreaterThanOrEqual(2);
		expect(container.querySelectorAll('.object-handle')).toHaveLength(0);

		const circle = () =>
			container.querySelector(
				'.editor-workspace ellipse'
			) as SVGEllipseElement;

		const rectangleX = Number(shape(container).getAttribute('x'));
		const circleX = Number(circle().getAttribute('cx'));

		// An arrow on one member moves both, and a drag on one does too.

		fireEvent.keyDown(hits[1], {key: 'ArrowRight', shiftKey: true});
		fireEvent.keyUp(hits[1], {key: 'ArrowRight', shiftKey: true});

		expect(Number(shape(container).getAttribute('x'))).toBe(
			rectangleX + 10
		);
		expect(Number(circle().getAttribute('cx'))).toBe(circleX + 10);

		fireEvent.pointerDown(hits[0], {clientX: 0, clientY: 0});
		fireEvent.pointerMove(hits[0], {clientX: 5, clientY: 0});
		fireEvent.pointerMove(hits[0], {clientX: 10, clientY: 0});
		fireEvent.pointerUp(hits[0]);

		expect(Number(shape(container).getAttribute('x'))).toBe(
			rectangleX + 30
		);
		expect(Number(circle().getAttribute('cx'))).toBe(circleX + 30);

		// The whole formation is one undo step.

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(Number(shape(container).getAttribute('x'))).toBe(
			rectangleX + 10
		);
		expect(Number(circle().getAttribute('cx'))).toBe(circleX + 10);

		// While the group lives, the properties yield to a note: editing
		// "the selected layer" beside two rings would change one and read
		// as a lie.

		expect(screen.queryByText('selected-layer-x')).toBeNull();
		expect(screen.getByRole('status')).toHaveTextContent(
			'x-annotations-are-grouped'
		);
		expect(
			document.querySelectorAll('.editor-layer-item-grouped')
		).toHaveLength(2);

		// A plain click on a member keeps the group (that is how it is
		// dragged); a plain click on another annotation dissolves it.

		fireEvent.pointerDown(hits[0]);
		fireEvent.pointerUp(hits[0]);

		expect(screen.getByRole('status')).not.toBeEmptyDOMElement();

		addShape('square');

		const third = container.querySelectorAll('.overlay-hit')[2];

		fireEvent.pointerDown(third);
		fireEvent.pointerUp(third);

		expect(screen.getByRole('status')).toBeEmptyDOMElement();
		expect(screen.getByText('selected-layer-x')).toBeInTheDocument();
	});

	it('deletes a whole group with one key and undoes it whole', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');
		addShape('circle');

		const hits = container.querySelectorAll('.overlay-hit');

		fireEvent.focus(hits[1]);
		fireEvent.pointerDown(hits[0], {shiftKey: true});

		fireEvent.keyDown(hits[0], {key: 'Delete'});

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(0);

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(2);
	});

	it('deletes a whole group from a layer row as well', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');
		addShape('circle');

		const hits = container.querySelectorAll('.overlay-hit');

		fireEvent.focus(hits[1]);
		fireEvent.pointerDown(hits[0], {shiftKey: true});

		fireEvent.click(screen.getAllByRole('button', {name: 'delete-x'})[0]);

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(0);
		expect(screen.queryByText('layers')).toBeNull();
	});

	it('copies the focused annotation and pastes it into the workspace', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		const target = hit(container);

		fireEvent.keyDown(target, {ctrlKey: true, key: 'c'});

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(1);

		fireEvent.keyDown(
			screen.getByRole('region', {name: 'image-workspace'}),
			{ctrlKey: true, key: 'v'}
		);

		const shapes = container.querySelectorAll(
			'.editor-workspace rect[fill]:not([class])'
		);

		expect(shapes).toHaveLength(2);
		expect(Number(shapes[1].getAttribute('x'))).toBe(
			Number(shapes[0].getAttribute('x')) + 16
		);
	});
});

describe('the host configuration', () => {
	it('offers only the tools the host asked for, in the menu order', () => {
		render(<AnnotationHarness tools={['arrow', 'circle']} />);

		expect(screen.queryByRole('button', {name: 'add-text'})).toBeNull();

		fireEvent.click(screen.getByRole('button', {name: 'add-shape'}));

		const cells = within(
			screen.getByRole('grid', {name: 'add-shape'})
		).getAllByRole('button');

		expect(cells.map((cell) => cell.getAttribute('aria-label'))).toEqual([
			'circle',
			'arrow',
		]);
	});

	it('keeps the single tab stop on the only control left', () => {
		render(<AnnotationHarness tools={['text']} />);

		expect(screen.queryByRole('button', {name: 'add-shape'})).toBeNull();
		expect(screen.getByRole('button', {name: 'add-text'})).toHaveAttribute(
			'tabindex',
			'0'
		);
	});
});
