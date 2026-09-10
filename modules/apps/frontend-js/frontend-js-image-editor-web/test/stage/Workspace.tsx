/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {fireEvent, render, screen} from '@testing-library/react';
import React, {useReducer, useState} from 'react';

import '@testing-library/jest-dom';

import {BottomBar} from '../../src/main/resources/META-INF/resources/js/chrome/BottomBar';
import {EditorInstanceProvider} from '../../src/main/resources/META-INF/resources/js/chrome/instance';
import {RATIO_PRESETS} from '../../src/main/resources/META-INF/resources/js/editorConfig';
import {LoadedImage} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';
import {CropPanel} from '../../src/main/resources/META-INF/resources/js/panels/CropPanel';
import {Workspace} from '../../src/main/resources/META-INF/resources/js/stage/Workspace';
import {
	editorReducer,
	initialHistory,
} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {
	RatioPreset,
	rotatedSize,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const IMAGE: LoadedImage = {
	blob: new Blob(),
	fileName: 'test.jpg',
	height: 800,
	previewUrl: 'test.jpg',
	type: 'image/jpeg',
	width: 1200,
};

function EditorHarness() {
	const [history, dispatch] = useReducer(editorReducer, undefined, () =>
		initialHistory(IMAGE.width, IMAGE.height)
	);
	const [zoom, setZoom] = useState(0.5);

	const [aspectLocked, setAspectLocked] = useState(false);

	const zoomBy = (direction: -1 | 1) =>
		setZoom((current) => current + direction * 0.25);

	return (
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<EditorInstanceProvider value="aie-">
				<Workspace
					aspectLocked={aspectLocked}
					dispatch={dispatch}
					image={IMAGE}
					onAnnounce={() => {}}
					onCenterCrop={() => {}}
					onZoom={zoomBy}
					onZoomActual={() => setZoom(1)}
					onZoomFit={() => setZoom(0.5)}
					showCrop
					showRecenter
					state={history.present}
					zoom={zoom}
				/>

				<CropPanel
					angle={history.present.angle}
					aspectLocked={aspectLocked}
					bounds={rotatedSize(history.present)}
					crop={history.present.crop}
					dispatch={dispatch}
					onAnnounce={() => {}}
					onAspectLockedChange={setAspectLocked}
					showStraighten
				/>

				<BottomBar
					canRedo={!!history.future.length}
					canUndo={!!history.past.length}
					dispatch={dispatch}
					onAnnounce={() => {}}
					onCancel={() => {}}
					onRedo={() => dispatch({type: 'redo'})}
					onSave={() => {}}
					onShowShortcuts={() => {}}
					onUndo={() => dispatch({type: 'undo'})}
					onZoom={zoomBy}
					onZoomFit={() => setZoom(0.5)}
					ratio={history.present.ratio}
					ratios={RATIO_PRESETS}
					saving={false}
					showRotate
					zoom={zoom}
				/>
			</EditorInstanceProvider>
		</ClayIconSpriteContext.Provider>
	);
}

describe('Editor workspace composition', () => {
	it('exposes the stage as a described, focusable region', () => {
		render(<EditorHarness />);

		const workspace = screen.getByRole('region', {
			name: 'image-workspace',
		});

		expect(workspace).toHaveAttribute('tabindex', '0');
		expect(workspace).toHaveAccessibleDescription(
			'scrollable-view-of-the-image-use-the-zoom-buttons-or-plus-and-minus-keys-to-zoom-tab-to-reach-the-crop-area-and-its-handles'
		);
	});

	it('scales the stage with the zoom', () => {
		render(<EditorHarness />);

		const stage = document.querySelector('.editor-stage')!;

		expect(stage).toHaveAttribute('width', '600');
		expect(stage).toHaveAttribute('height', '400');
	});

	it('zooms with plus and minus while the workspace has focus', () => {
		render(<EditorHarness />);

		const workspace = screen.getByRole('region', {
			name: 'image-workspace',
		});

		fireEvent.keyDown(workspace, {key: '+'});

		expect(screen.getByText('x-percent')).toBeInTheDocument();
		expect(document.querySelector('.editor-stage')).toHaveAttribute(
			'width',
			'900'
		);

		fireEvent.click(screen.getByRole('button', {name: 'zoom-out'}));

		expect(document.querySelector('.editor-stage')).toHaveAttribute(
			'width',
			'600'
		);
	});

	it('rotates the stage and undoes it from the bar', () => {
		render(<EditorHarness />);

		fireEvent.click(
			screen.getByRole('button', {name: 'rotate-90-degrees-clockwise'})
		);

		expect(document.querySelector('.editor-stage')).toHaveAttribute(
			'width',
			'400'
		);

		fireEvent.click(screen.getByRole('button', {name: 'undo'}));

		expect(document.querySelector('.editor-stage')).toHaveAttribute(
			'width',
			'600'
		);
	});

	it('exposes the crop area and all eight handles as labelled buttons', () => {
		render(<EditorHarness />);

		expect(
			screen.getByRole('button', {name: 'crop-area'})
		).toBeInTheDocument();

		[
			'crop-handle-top-left-corner',
			'crop-handle-top-edge',
			'crop-handle-top-right-corner',
			'crop-handle-right-edge',
			'crop-handle-bottom-right-corner',
			'crop-handle-bottom-edge',
			'crop-handle-bottom-left-corner',
			'crop-handle-left-edge',
		].forEach((name) => {
			expect(screen.getByRole('button', {name})).toBeInTheDocument();
		});
	});

	it('moves a crop handle with the keyboard', () => {
		render(<EditorHarness />);

		const rightHandle = screen.getByRole('button', {
			name: 'crop-handle-right-edge',
		});

		fireEvent.keyDown(rightHandle, {key: 'ArrowLeft', shiftKey: true});
		fireEvent.keyUp(rightHandle, {key: 'ArrowLeft', shiftKey: true});

		const widthInput = screen.getByLabelText('width') as HTMLInputElement;

		expect(widthInput.value).toBe('1190');
	});

	it('commits a numeric panel edit on Enter and keeps it inside the image', () => {
		render(<EditorHarness />);

		const xInput = screen.getByLabelText('x-position') as HTMLInputElement;
		const widthInput = screen.getByLabelText('width') as HTMLInputElement;

		fireEvent.change(widthInput, {target: {value: '600'}});
		fireEvent.keyDown(widthInput, {key: 'Enter'});

		expect(widthInput.value).toBe('600');

		fireEvent.change(xInput, {target: {value: '900'}});
		fireEvent.keyDown(xInput, {key: 'Enter'});

		expect(xInput.value).toBe('600');
	});

	it('paints the dim layer above the crop area and the border above both', () => {
		render(<EditorHarness />);

		const classes = [
			...(document.querySelectorAll(
				'.editor-stage > g > *'
			) as NodeListOf<Element>),
		].map((node) => node.getAttribute('class') ?? node.tagName);

		expect(classes.indexOf('crop-dim')).toBeGreaterThan(
			classes.indexOf('crop-move')
		);
		expect(classes.indexOf('crop-border')).toBeGreaterThan(
			classes.indexOf('crop-dim')
		);
	});

	it('shows the thirds grid only while a crop gesture runs', () => {
		render(<EditorHarness />);

		const handle = screen.getByRole('button', {
			name: 'crop-handle-right-edge',
		});

		expect(document.querySelectorAll('.crop-grid line')).toHaveLength(4);
		expect(document.querySelector('.crop-grid-visible')).toBeNull();

		fireEvent.keyDown(handle, {key: 'ArrowLeft'});

		expect(
			document.querySelector('.crop-grid-visible')
		).toBeInTheDocument();

		fireEvent.keyUp(handle, {key: 'ArrowLeft'});

		expect(document.querySelector('.crop-grid-visible')).toBeNull();
	});

	it('offers the recenter control only once the crop is a selection', () => {
		render(<EditorHarness />);

		const recenter = () =>
			screen.queryByRole('button', {
				name: 'center-the-crop-in-the-view',
			});

		expect(recenter()).toBeNull();

		const widthInput = screen.getByLabelText('width');

		fireEvent.change(widthInput, {target: {value: '400'}});
		fireEvent.keyDown(widthInput, {key: 'Enter'});

		expect(recenter()).toBeInTheDocument();
	});

	it('keeps the proportions of a numeric edit while the aspect is locked', () => {
		render(<EditorHarness />);

		const widthInput = screen.getByLabelText('width') as HTMLInputElement;
		const heightInput = screen.getByLabelText('height') as HTMLInputElement;

		fireEvent.click(screen.getByLabelText('lock-aspect-ratio'));

		fireEvent.change(widthInput, {target: {value: '600'}});
		fireEvent.keyDown(widthInput, {key: 'Enter'});

		expect(widthInput.value).toBe('600');
		expect(heightInput.value).toBe('400');
	});

	it('offers only the corner handles while the aspect is locked', () => {
		render(<EditorHarness />);

		fireEvent.click(screen.getByLabelText('lock-aspect-ratio'));

		expect(
			screen.queryByRole('button', {name: 'crop-handle-right-edge'})
		).toBeNull();
		expect(
			screen.getByRole('button', {name: 'crop-handle-top-left-corner'})
		).toBeInTheDocument();
	});

	it('centers a square crop from the ratio select and drops back to custom on a free edit', () => {
		render(<EditorHarness />);

		const select = screen.getByLabelText('ratio') as HTMLSelectElement;

		fireEvent.change(select, {target: {value: '1:1'}});

		expect(select.value).toBe('1:1');
		expect((screen.getByLabelText('width') as HTMLInputElement).value).toBe(
			'800'
		);
		expect(
			(screen.getByLabelText('x-position') as HTMLInputElement).value
		).toBe('200');

		const heightInput = screen.getByLabelText('height');

		fireEvent.change(heightInput, {target: {value: '500'}});
		fireEvent.keyDown(heightInput, {key: 'Enter'});

		expect(select.value).toBe('custom');
	});

	it('clips the stage only while the image is straightened', () => {
		render(<EditorHarness />);

		const imageGroup = () =>
			document.querySelector('.editor-stage > g:not(.crop-grid)')!;

		expect(imageGroup()).not.toHaveAttribute('clip-path');

		const slider = screen.getByLabelText('straighten');

		fireEvent.change(slider, {target: {value: '8'}});
		fireEvent.keyUp(slider, {key: 'ArrowRight'});

		expect(imageGroup()).toHaveAttribute(
			'clip-path',
			'url(#aie-stage-clip)'
		);
		expect(document.querySelector('image')!.parentElement).toHaveAttribute(
			'transform',
			expect.stringContaining('rotate(8')
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'reset-the-straighten-angle'})
		);

		expect(imageGroup()).not.toHaveAttribute('clip-path');
	});
});

describe('the controls agree with the state from the first render', () => {
	const bar = (ratios: RatioPreset[]) => {
		const history = initialHistory(IMAGE.width, IMAGE.height, {ratios});

		render(
			<ClayIconSpriteContext.Provider value="/icons.svg">
				<EditorInstanceProvider value="aie-">
					<BottomBar
						canRedo={false}
						canUndo={false}
						dispatch={() => {}}
						onAnnounce={() => {}}
						onCancel={() => {}}
						onRedo={() => {}}
						onSave={() => {}}
						onShowShortcuts={() => {}}
						onUndo={() => {}}
						onZoom={() => {}}
						onZoomFit={() => {}}
						ratio={history.present.ratio}
						ratios={ratios}
						saving={false}
						showRotate
						zoom={1}
					/>
				</EditorInstanceProvider>
			</ClayIconSpriteContext.Provider>
		);

		return screen.getByLabelText('ratio') as HTMLSelectElement;
	};

	it('shows the forced ratio as the selected option', () => {
		const select = bar(['1:1']);

		expect(select.value).toBe('1:1');
		expect(
			Array.from(select.options).map((option) => option.value)
		).toEqual(['1:1']);
	});

	it('starts a custom-plus-presets config on custom, never outside it', () => {
		const select = bar(['custom', '16:9']);

		expect(select.value).toBe('custom');
		expect(
			Array.from(select.options).map((option) => option.value)
		).toEqual(['custom', '16:9']);
	});
});
