/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {useAnnouncer} from '../chrome/Announcer';
import {BottomBar} from '../chrome/BottomBar';
import {
	EditorInstanceProvider,
	nextEditorInstancePrefix,
} from '../chrome/instance';
import {t} from '../i18n';
import {LoadedImage} from '../imaging/loadImage';
import {Workspace} from '../stage/Workspace';

const ZOOM_LEVELS = [0.05, 0.1, 0.15, 0.25, 0.35, 0.5, 0.75, 1, 1.5, 2, 3];

const STAGE_PADDING = 48;

function fitZoom(
	workspace: HTMLElement | null,
	width: number,
	height: number,
	max = 1
): number {
	const availableWidth = workspace
		? workspace.clientWidth - STAGE_PADDING
		: Math.max(window.innerWidth - 360, 240);
	const availableHeight = workspace
		? workspace.clientHeight - STAGE_PADDING
		: Math.max(window.innerHeight - 200, 240);

	const fit = Math.min(availableWidth / width, availableHeight / height, max);

	return Math.max(Math.floor(fit * 100) / 100, 0.01);
}

function stepZoom(zoom: number, direction: -1 | 1): number {
	if (direction === 1) {
		return ZOOM_LEVELS.find((level) => level > zoom + 1e-6) ?? zoom;
	}

	const smaller = ZOOM_LEVELS.filter((level) => level < zoom - 1e-6);

	return smaller.length ? smaller[smaller.length - 1] : zoom;
}

interface Props {
	image: LoadedImage;
	onClose: () => void;
}

export default function EditorModal({image, onClose}: Props) {
	const [instancePrefix] = useState(nextEditorInstancePrefix);

	const announce = useAnnouncer();

	const {observer} = useModal({onClose});

	const editorRef = useRef<HTMLDivElement | null>(null);

	const [zoom, setZoom] = useState(() =>
		fitZoom(null, image.width, image.height)
	);

	const workspaceRef = useRef<HTMLDivElement | null>(null);

	const autoFitRef = useRef(true);

	useEffect(() => {
		announce(t('editor-loaded', image.width, image.height));
	}, [announce, image]);

	const resizeObserverRef = useRef<ResizeObserver | null>(null);

	const handleWorkspaceRef = useCallback(
		(element: HTMLDivElement | null) => {
			resizeObserverRef.current?.disconnect();
			resizeObserverRef.current = null;

			workspaceRef.current = element;

			if (!element) {
				return;
			}

			const observer = new ResizeObserver(() => {
				if (autoFitRef.current) {
					setZoom(fitZoom(element, image.width, image.height));
				}
			});

			observer.observe(element);

			resizeObserverRef.current = observer;
		},
		[image]
	);

	const announceZoom = (level: number) =>
		announce(t('zoom-level', Math.round(level * 100)));

	const zoomBy = (direction: -1 | 1) => {
		autoFitRef.current = false;

		const next = stepZoom(zoom, direction);

		if (next === zoom) {
			return;
		}

		setZoom(next);
		announceZoom(next);
	};

	const zoomToActual = () => {
		autoFitRef.current = false;

		setZoom(1);

		announceZoom(1);
	};

	const zoomToFit = () => {
		autoFitRef.current = true;

		const next = fitZoom(workspaceRef.current, image.width, image.height);

		setZoom(next);
		announceZoom(next);
	};

	return (
		<EditorInstanceProvider value={instancePrefix}>
			<ClayModal
				className="image-editor-modal"
				observer={observer}
				size="full-screen"
			>
				<ClayModal.Header closeButtonAriaLabel={t('close')} withTitle>
					{t('editing-image')}
				</ClayModal.Header>

				<div className="image-editor" ref={editorRef}>
					<div className="editor-main">
						<Workspace
							image={image}
							onZoom={zoomBy}
							onZoomActual={zoomToActual}
							onZoomFit={zoomToFit}
							workspaceRef={handleWorkspaceRef}
							zoom={zoom}
						/>
					</div>

					<BottomBar
						onZoom={zoomBy}
						onZoomFit={zoomToFit}
						zoom={zoom}
					/>
				</div>
			</ClayModal>
		</EditorInstanceProvider>
	);
}
