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
import {LoadedImage} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';
import {Workspace} from '../../src/main/resources/META-INF/resources/js/stage/Workspace';
import {
	editorReducer,
	initialHistory,
} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';

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

	const zoomBy = (direction: -1 | 1) =>
		setZoom((current) => current + direction * 0.25);

	return (
		<ClayIconSpriteContext.Provider value="/icons.svg">
			<EditorInstanceProvider value="aie-">
				<Workspace
					image={IMAGE}
					onZoom={zoomBy}
					onZoomActual={() => setZoom(1)}
					onZoomFit={() => setZoom(0.5)}
					state={history.present}
					zoom={zoom}
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
					saving={false}
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
});
