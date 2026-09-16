/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {
	act,
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
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
import {useOverlaySelection} from '../../src/main/resources/META-INF/resources/js/hooks/useOverlaySelection';
import {LoadedImage} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';
import {
	DrawResult,
	strokeFromDrawing,
} from '../../src/main/resources/META-INF/resources/js/stage/DrawSurface';
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
	pixelUrls: {coarse: 'c.png', fine: 'f.png', medium: 'm.png', tiny: 't.png'},
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
	onAnnounce = () => {},
	start = () => initialHistory(IMAGE.width, IMAGE.height),
	tools = ANNOTATE_TOOLS,
}: {
	onAnnounce?: (message: string) => void;
	start?: () => EditorHistory;
	tools?: AnnotateTool[];
}) {
	const [history, dispatch] = useReducer(editorReducer, undefined, start);

	const [drawing, setDrawing] = useState<null | {guided: boolean}>(null);

	const finishDrawing = (result: DrawResult | null) => {
		setDrawing(null);

		if (result) {
			dispatch({
				overlay: strokeFromDrawing(result, history.present.crop),
				type: 'add-overlay',
			});
		}
	};

	const {
		layerProportional,
		multiSelectedIds,
		selectOverlay,
		selectedOverlayId,
		setLayerProportional,
		toggleMultiSelect,
	} = useOverlaySelection(() => {});

	const [clipboard, setClipboard] = useState<Overlay | null>(null);

	return (
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<EditorInstanceProvider value="aie-">
				<Workspace
					aspectLocked={false}
					dispatch={dispatch}
					drawing={drawing}
					image={IMAGE}
					multiSelectedIds={multiSelectedIds}
					onAnnounce={onAnnounce}
					onCenterCrop={() => {}}
					onCopyOverlay={(id) =>
						setClipboard(
							history.present.overlays.find(
								(overlay) => overlay.id === id
							) ?? null
						)
					}
					onFinishDrawing={finishDrawing}
					onMultiSelectToggle={toggleMultiSelect}
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
					onSelectOverlay={selectOverlay}
					onZoom={() => {}}
					onZoomActual={() => {}}
					onZoomFit={() => {}}
					proportional={layerProportional}
					selectedOverlayId={selectedOverlayId}
					showCrop
					showRecenter
					state={history.present}
					zoom={0.5}
				/>

				<AnnotatePanel
					area={history.present.crop}
					dispatch={dispatch}
					onAnnounce={() => {}}
					onStartDrawing={(via) =>
						setDrawing({guided: via === 'keyboard'})
					}
					tools={tools}
				/>

				<LayersPanel
					dispatch={dispatch}
					multiSelectedIds={multiSelectedIds}
					onAnnounce={() => {}}
					onProportionalChange={setLayerProportional}
					onSelect={selectOverlay}
					overlays={history.present.overlays}
					proportional={layerProportional}
					selectedId={selectedOverlayId}
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

const withStroke = () =>
	editorReducer(initialHistory(IMAGE.width, IMAGE.height), {
		overlay: {
			color: '#0b5fff',
			id: 'stroke-1',
			kind: 'stroke',
			points: [0, 0, 200, 100],
			smooth: false,
			width: 10,
			x: 300,
			y: 400,
		},
		type: 'add-overlay',
	});

const hit = (container: HTMLElement) =>
	container.querySelector('.overlay-hit') as SVGRectElement;

const caption = (container: HTMLElement) =>
	container.querySelector('.editor-workspace text') as SVGTextElement;

const workspace = () =>
	within(screen.getByRole('region', {name: 'image-workspace'}));

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
		fireEvent.change(screen.getByLabelText('text-color'), {
			target: {value: '#ff0000'},
		});
		fireEvent.submit(input.closest('form') as HTMLFormElement);

		const text = caption(container);

		expect(text).toHaveTextContent('Liferay');
		expect(text).toHaveAttribute('font-size', '40');
		expect(text).toHaveAttribute('font-family', 'serif');
		expect(text).toHaveAttribute('fill', '#ff0000');

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

	it('edits a caption in place on double click', async () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.doubleClick(hit(container));

		const editor = workspace().getByRole('textbox', {name: 'text'});

		expect(editor).toHaveValue('Hello');

		fireEvent.change(editor, {target: {value: 'Liferay'}});
		fireEvent.keyDown(editor, {key: 'Enter'});

		expect(workspace().queryByRole('textbox', {name: 'text'})).toBeNull();
		expect(caption(container)).toHaveTextContent('Liferay');

		await waitFor(() => expect(hit(container)).toHaveFocus());
	});

	it('abandons an in-place edit with Escape', async () => {
		const {container} = render(<AnnotationHarness start={withCaption} />);

		fireEvent.doubleClick(hit(container));

		const editor = workspace().getByRole('textbox', {name: 'text'});

		fireEvent.change(editor, {target: {value: 'Liferay'}});
		fireEvent.keyDown(editor, {key: 'Escape'});

		expect(caption(container)).toHaveTextContent('Hello');

		await waitFor(() => expect(hit(container)).toHaveFocus());
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

		await waitFor(() =>
			expect(
				screen.getByRole('region', {name: 'image-workspace'})
			).toHaveFocus()
		);
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

async function addEmoji(name: string) {
	fireEvent.click(screen.getByRole('button', {name: 'add-emoji'}));

	fireEvent.click(
		within(await screen.findByRole('grid', {name: 'add-emoji'})).getByRole(
			'button',
			{name}
		)
	);
}

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

		expect(Math.round(centerX)).toBe(900);
		expect(Math.round(centerY)).toBe(600);
	});

	it('resizes a rectangle from a corner, and keeps the proportions with Shift', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.focus(hit(container));

		const handles = container.querySelectorAll('.object-handle');

		expect(handles).toHaveLength(9);

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

		expect(addText).toHaveAttribute('aria-haspopup', 'dialog');
		expect(screen.getByRole('button', {name: 'add-shape'})).toHaveAttribute(
			'aria-haspopup'
		);

		addText.focus();

		fireEvent.keyDown(addText, {key: 'ArrowRight'});

		expect(document.activeElement).toHaveAccessibleName('add-shape');

		expect(
			document.querySelectorAll(
				'.editor-annotate-actions [data-index][tabindex="0"]'
			)
		).toHaveLength(1);

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

		const widthInput = screen.getByLabelText('width');

		fireEvent.change(widthInput, {target: {value: '500'}});
		fireEvent.keyDown(widthInput, {key: 'Enter'});

		expect(shape(container)).toHaveAttribute('width', '500');

		expect(screen.queryByLabelText('text-color')).toBeNull();

		const colorInput = screen.getByLabelText('color');

		fireEvent.change(colorInput, {target: {value: '#00ff00'}});

		expect(shape(container)).toHaveAttribute('fill', '#00ff00');

		fireEvent.blur(colorInput);

		const opacityInput = screen.getByLabelText('opacity');

		fireEvent.change(opacityInput, {target: {value: '50'}});
		fireEvent.keyDown(opacityInput, {key: 'Enter'});

		expect(shape(container).closest('g[opacity]')).toHaveAttribute(
			'opacity',
			'0.5'
		);

		const xInput = screen.getByLabelText('x-position');
		const yInput = screen.getByLabelText('y-position');

		expect(xInput).not.toHaveAttribute('min');
		expect(screen.getByLabelText('opacity')).toHaveAttribute('min', '0');

		fireEvent.change(xInput, {target: {value: '120'}});
		fireEvent.keyDown(xInput, {key: 'Enter'});
		fireEvent.change(yInput, {target: {value: '340'}});
		fireEvent.keyDown(yInput, {key: 'Enter'});

		expect(shape(container)).toHaveAttribute('x', '120');
		expect(shape(container)).toHaveAttribute('y', '340');

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

		fireEvent.focus(hits[0]);

		expect(
			screen.getByRole('button', {name: 'text-x', pressed: true})
		).toBeInTheDocument();

		fireEvent.blur(hits[0]);

		fireEvent.click(
			screen.getByRole('button', {name: 'rectangle', pressed: false})
		);

		expect(container.querySelectorAll('.selection-ring')).toHaveLength(1);
		expect(
			screen.getByRole('group', {name: 'selected-layer-x'})
		).toContainElement(screen.getByLabelText('width'));
	});

	it('jumps from the stage node to its properties on Enter', async () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.keyDown(hit(container), {key: 'Enter'});

		await waitFor(() =>
			expect(document.activeElement?.id).toBe('aie-layer-prop-color')
		);
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

		await waitFor(() =>
			expect(document.activeElement).toBe(hit(container))
		);
	});

	it('duplicates a layer from its row and selects the copy', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');

		fireEvent.click(screen.getByRole('button', {name: 'duplicate-x'}));

		expect(layerNames()).toEqual(['rectangle', 'rectangle']);

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

		await waitFor(() => expect(document.activeElement).toBe(row('text-x')));
	});

	it('roves through the rows and skips the disabled actions', () => {
		render(<AnnotationHarness start={withCaption} />);

		addShape('rectangle');

		const top = row('rectangle');

		act(() => top.focus());

		expect(
			document.querySelectorAll('.editor-layer-list [tabindex="0"]')
		).toHaveLength(1);

		fireEvent.keyDown(top, {key: 'ArrowRight'});

		expect(document.activeElement).toHaveAccessibleName('move-x-down');

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

		fireEvent.change(textInput, {target: {value: '   '}});
		fireEvent.blur(textInput);

		expect(textInput).toHaveValue('Liferay');
	});

	it('aims an arrow from its tip fields and opens its head', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('arrow');

		expect(screen.queryByLabelText('rotation')).toBeNull();

		const tipY = screen.getByLabelText('tip-y-position');
		const tailY = Number(
			screen.getByLabelText('y-position').getAttribute('value')
		);

		fireEvent.change(tipY, {target: {value: '120'}});
		fireEvent.keyDown(tipY, {key: 'Enter'});

		expect(screen.getByLabelText('tip-y-position')).toHaveValue(120);
		expect(screen.getByLabelText('y-position')).toHaveValue(tailY);

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

		fireEvent.click(
			within(screen.getByRole('grid', {name: 'add-shape'})).getByRole(
				'button',
				{name: 'rectangle'}
			)
		);

		fireEvent.click(within(second).getByRole('button', {name: 'delete-x'}));

		await waitFor(() =>
			expect(document.activeElement).toBe(
				second.querySelector('.editor-workspace')
			)
		);
	});
});

describe('groups and the clipboard', () => {
	it('moves a shift-built group together, and only moves it', () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');
		addShape('circle');

		const hits = container.querySelectorAll('.overlay-hit');

		fireEvent.focus(hits[1]);
		fireEvent.pointerDown(hits[0], {shiftKey: true});

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

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(Number(shape(container).getAttribute('x'))).toBe(
			rectangleX + 10
		);
		expect(Number(circle().getAttribute('cx'))).toBe(circleX + 10);

		expect(screen.queryByText('selected-layer-x')).toBeNull();
		expect(screen.getByRole('status')).toHaveTextContent(
			'x-annotations-are-grouped'
		);
		expect(
			document.querySelectorAll('.editor-layer-item-grouped')
		).toHaveLength(2);

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

	it('deletes a whole group with one key and undoes it whole', async () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');
		addShape('circle');

		const hits = container.querySelectorAll('.overlay-hit');

		fireEvent.focus(hits[1]);
		fireEvent.pointerDown(hits[0], {shiftKey: true});

		fireEvent.keyDown(hits[0], {key: 'Delete'});

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(0);

		await waitFor(() =>
			expect(container.querySelector('.editor-workspace')).toHaveFocus()
		);

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(2);
	});

	it('deletes a whole group from a layer row as well', async () => {
		const {container} = render(<AnnotationHarness />);

		addShape('rectangle');
		addShape('circle');

		const hits = container.querySelectorAll('.overlay-hit');

		fireEvent.focus(hits[1]);
		fireEvent.pointerDown(hits[0], {shiftKey: true});

		fireEvent.click(screen.getAllByRole('button', {name: 'delete-x'})[0]);

		expect(container.querySelectorAll('.overlay-hit')).toHaveLength(0);
		expect(screen.queryByText('layers')).toBeNull();

		await waitFor(() =>
			expect(container.querySelector('.editor-workspace')).toHaveFocus()
		);
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

describe('drawing', () => {
	const press = (
		target: Element,
		key: string,
		times = 1,
		shiftKey = false
	) => {
		for (let index = 0; index < times; index++) {
			fireEvent.keyDown(target, {key, shiftKey});
		}
	};

	const startDrawing = async (detail: number) => {
		fireEvent.click(screen.getByRole('button', {name: 'draw'}), {detail});

		const surface = screen.getByRole('application', {
			name: 'drawing-area',
		});

		await waitFor(() => expect(surface).toHaveFocus());

		await waitFor(() =>
			expect(
				surface.parentElement?.querySelector('.focus-ring-outer')
			).toBeInTheDocument()
		);

		return surface;
	};

	const stroke = (container: HTMLElement) =>
		container.querySelector(
			'.editor-workspace path[stroke="#0b5fff"][transform]'
		) as SVGPathElement;

	it('draws a guided line with the keyboard alone', async () => {
		const {container} = render(<AnnotationHarness />);

		const surface = await startDrawing(0);

		press(surface, 'ArrowRight', 4, true);
		press(surface, 'Enter');
		press(surface, 'ArrowDown', 3, true);
		press(surface, 'Enter');

		expect(
			screen.queryByRole('application', {name: 'drawing-area'})
		).toBeNull();

		expect(
			within(
				document.querySelector('.editor-layer-list') as HTMLElement
			).getByText('stroke')
		).toBeInTheDocument();

		expect(stroke(container)).toHaveAttribute(
			'transform',
			'translate(600 400)'
		);
		expect(stroke(container).getAttribute('d')).toMatch(/^M0 0 C/);

		expect(screen.getByLabelText('thickness')).toBeInTheDocument();
		expect(screen.getByLabelText('line-style')).toHaveValue('smooth');
		expect(screen.queryByLabelText('width')).toBeNull();
	});

	it('refuses to set a line with no length', async () => {
		const announce = jest.fn();

		render(<AnnotationHarness onAnnounce={announce} />);

		const surface = await startDrawing(0);

		press(surface, 'Enter');

		expect(announce).toHaveBeenLastCalledWith(
			'move-the-end-away-from-the-start-first'
		);
		expect(surface).toBeInTheDocument();
	});

	it('steps back from the bend', async () => {
		const {container} = render(<AnnotationHarness />);

		const surface = await startDrawing(0);

		press(surface, 'ArrowRight', 2, true);
		press(surface, 'Enter');
		press(surface, 'Backspace');
		press(surface, 'ArrowRight', 2, true);
		press(surface, 'Enter');
		press(surface, 'Enter');

		expect(stroke(container).getAttribute('d')).toBe(
			'M0 0 C13.33 0 66.67 0 80 0'
		);
	});

	it('abandons a drawing with Escape', async () => {
		const announce = jest.fn();

		const {container} = render(<AnnotationHarness onAnnounce={announce} />);

		const surface = await startDrawing(0);

		press(surface, 'ArrowRight', 2, true);
		press(surface, 'Escape');

		expect(
			screen.queryByRole('application', {name: 'drawing-area'})
		).toBeNull();
		expect(stroke(container)).toBeNull();
		expect(announce).toHaveBeenLastCalledWith('drawing-was-canceled');
		expect(container.querySelector('.editor-workspace')).toHaveFocus();
	});

	it('scales a stroke from a corner, thickness and all', () => {
		const {container} = render(<AnnotationHarness start={withStroke} />);

		fireEvent.focus(hit(container));

		const handles = container.querySelectorAll('.object-handle');

		// Four corners and the rotation knob: a stroke has no edges to
		// stretch, so it scales as a whole.

		expect(handles).toHaveLength(5);

		fireEvent.pointerDown(handles[2], {clientX: 0, clientY: 0});
		fireEvent.pointerMove(handles[2], {clientX: 50, clientY: 20});
		fireEvent.pointerUp(handles[2]);

		const path = stroke(container);

		const [x2, y2] = path
			.getAttribute('d')!
			.replace('M0 0 L', '')
			.split(' ')
			.map(Number);

		expect(x2).toBeGreaterThan(200);
		expect(x2 / 200).toBeCloseTo(y2 / 100, 2);

		expect(Number(path.getAttribute('stroke-width'))).toBeCloseTo(
			(10 * x2) / 200,
			0
		);

		// The center holds, so the stroke grows around what it marks.

		const [x, y] = path
			.getAttribute('transform')!
			.replace('translate(', '')
			.replace(')', '')
			.split(' ')
			.map(Number);

		expect(x + x2 / 2).toBeCloseTo(400, 0);
		expect(y + y2 / 2).toBeCloseTo(450, 0);
	});

	it('places pen points with clicks and finishes on the last one', async () => {
		const {container} = render(<AnnotationHarness />);

		const surface = await startDrawing(1);

		const tap = (clientX: number, clientY: number) => {
			fireEvent.pointerDown(surface, {clientX, clientY, pointerId: 1});
			fireEvent.pointerUp(surface, {clientX, clientY, pointerId: 1});
		};

		tap(100, 100);
		tap(150, 100);
		tap(150, 150);
		tap(150, 150);

		expect(stroke(container)).toHaveAttribute(
			'transform',
			'translate(200 200)'
		);
		expect(stroke(container).getAttribute('d')).toMatch(
			/^M0 0 C.* 100 0 C.* 100 100$/
		);
	});

	it('commits a freehand drag on release, simplified', async () => {
		const {container} = render(<AnnotationHarness />);

		const surface = await startDrawing(1);

		fireEvent.pointerDown(surface, {clientX: 0, clientY: 0, pointerId: 1});

		for (let step = 1; step <= 20; step++) {
			fireEvent.pointerMove(surface, {
				clientX: step * 5,
				clientY: 0,
				pointerId: 1,
			});
		}

		fireEvent.pointerUp(surface, {clientX: 100, clientY: 0, pointerId: 1});

		expect(stroke(container).getAttribute('d')).toBe(
			'M0 0 C33.33 0 166.67 0 200 0'
		);
	});
});

describe('a redaction', () => {
	const revealed = (container: HTMLElement) =>
		container.querySelector(
			'[clip-path*="redact-clip-"] image'
		) as SVGImageElement;

	it('pixelates through a clipped source and blurs from the picture', () => {
		const {container} = render(<AnnotationHarness />);

		fireEvent.click(screen.getByRole('button', {name: 'add-redaction'}));

		expect(
			within(
				document.querySelector('.editor-layer-list') as HTMLElement
			).getByText('redacted-area')
		).toBeInTheDocument();

		expect(revealed(container)).toHaveAttribute('href', 'f.png');
		expect(screen.queryByLabelText('color')).toBeNull();

		fireEvent.change(screen.getByLabelText('strength'), {
			target: {value: 'tiny'},
		});

		expect(revealed(container)).toHaveAttribute('href', 't.png');

		fireEvent.change(screen.getByLabelText('type'), {
			target: {value: 'blur'},
		});

		expect(revealed(container)).toHaveAttribute('href', 'test.jpg');
		expect(
			container.querySelector('filter[id^="redact-blur-"] feGaussianBlur')
		).toBeInTheDocument();

		fireEvent.change(screen.getByLabelText('type'), {
			target: {value: 'pixel'},
		});

		expect(revealed(container)).toHaveAttribute('href', 't.png');
	});

	it('is a box, with the handles of a rectangle', () => {
		const {container} = render(<AnnotationHarness />);

		fireEvent.click(screen.getByRole('button', {name: 'add-redaction'}));

		act(() => {
			(container.querySelector('.overlay-hit') as SVGElement).focus();
		});

		expect(container.querySelectorAll('.object-handle')).toHaveLength(9);
		expect(screen.getByLabelText('width')).toBeInTheDocument();
	});
});

describe('an emoji annotation', () => {
	it('adds an emoji as a layer of its own, sized but never coloured', async () => {
		const {container} = render(<AnnotationHarness />);

		await addEmoji('star');

		expect(hit(container)).toHaveAttribute('aria-label', 'star');

		expect(
			container.querySelector('.editor-layer-glyph')?.textContent
		).toBe('⭐');

		expect(screen.getByLabelText('size')).toBeInTheDocument();
		expect(screen.queryByLabelText('color')).toBeNull();
		expect(screen.queryByLabelText('font-family')).toBeNull();
	});

	it('finds an emoji whatever the capitalisation of its name', async () => {
		render(<AnnotationHarness />);

		fireEvent.click(screen.getByRole('button', {name: 'add-emoji'}));

		fireEvent.change(await screen.findByLabelText('search-emoji'), {
			target: {value: 'spain'},
		});

		expect(
			within(screen.getByRole('grid', {name: 'add-emoji'})).getByRole(
				'button',
				{name: 'flag: Spain'}
			)
		).toBeInTheDocument();
	});

	it('moves with the keyboard like every other annotation', async () => {
		const {container} = render(<AnnotationHarness />);

		await addEmoji('star');

		const initialX = Number(hit(container).getAttribute('x'));

		fireEvent.keyDown(hit(container), {key: 'ArrowRight', shiftKey: true});
		fireEvent.keyUp(hit(container), {key: 'ArrowRight', shiftKey: true});

		expect(Number(hit(container).getAttribute('x'))).toBe(initialX + 10);
	});

	it('is centered on the crop, not on the image', async () => {
		const {container} = render(<AnnotationHarness start={cropped} />);

		await addEmoji('star');

		const target = hit(container);

		const centerX =
			Number(target.getAttribute('x')) +
			Number(target.getAttribute('width')) / 2;
		const centerY =
			Number(target.getAttribute('y')) +
			Number(target.getAttribute('height')) / 2;

		expect(Math.round(centerX)).toBe(900);
		expect(Math.round(centerY)).toBe(600);
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
