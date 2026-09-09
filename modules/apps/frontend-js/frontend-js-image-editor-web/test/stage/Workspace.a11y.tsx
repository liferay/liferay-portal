/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, screen} from '@testing-library/react';
import React, {useReducer, useState} from 'react';

import '@testing-library/jest-dom';

import {BottomBar} from '../../src/main/resources/META-INF/resources/js/chrome/BottomBar';
import {LoadedImage} from '../../src/main/resources/META-INF/resources/js/imaging/loadImage';
import {Workspace} from '../../src/main/resources/META-INF/resources/js/stage/Workspace';
import {
	editorReducer,
	initialHistory,
} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {renderEditor} from '../__lib__/renderEditor';

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
		<>
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
		</>
	);
}

describe('Editor workspace composition', () => {
	it('zooms with plus and minus while the workspace has focus', () => {
		renderEditor(<EditorHarness />);

		const workspace = screen.getByRole('region', {
			name: 'Image workspace',
		});

		fireEvent.keyDown(workspace, {key: '+'});

		expect(screen.getByText('75%')).toBeInTheDocument();
	});
});
