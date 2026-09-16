/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Annotations.scss';

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {useEditorId, useEditorRoot} from '../chrome/instance';
import {overlayLabel, textWidth} from '../imaging/overlayShapes';
import {EditorAction} from '../state/editorReducer';
import {CropRect, Overlay} from '../state/types';
import {TextDialog} from './TextDialog';

function focusOverlay(root: () => ParentNode, id: string, delay = 0): void {
	window.setTimeout(() => {
		window.requestAnimationFrame(() => {
			const node = root().querySelector<SVGElement>(
				`[data-overlay-id="${id}"]`
			);

			if (!node) {
				return;
			}

			(node as unknown as HTMLElement).focus?.({preventScroll: true});

			revealInWorkspace(node);
		});
	}, delay);
}

function revealInWorkspace(node: SVGElement): void {
	const workspace = node.closest<HTMLElement>('.editor-workspace');

	if (!workspace) {
		return;
	}

	const area = workspace.getBoundingClientRect();
	const box = node.getBoundingClientRect();

	const overflow = (start: number, end: number, low: number, high: number) =>
		start < low ? start - low : end > high ? end - high : 0;

	workspace.scrollLeft += overflow(
		box.left,
		box.right,
		area.left,
		area.right
	);
	workspace.scrollTop += overflow(box.top, box.bottom, area.top, area.bottom);
}

function ToolTile({icon, label}: {icon: string; label: string}) {
	return (
		<>
			<ClayIcon aria-hidden="true" symbol={icon} />

			<span className="editor-tool-tile-label">{label}</span>
		</>
	);
}

interface Props {
	area: CropRect;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
}

export function AnnotatePanel({area, dispatch, onAnnounce}: Props) {
	const eid = useEditorId();

	const editorRoot = useEditorRoot();

	const [textDialogOpen, setTextDialogOpen] = useState(false);

	const [rovingIndex, setRovingIndex] = useState(0);

	const panelRef = useRef<HTMLDivElement>(null);

	const controls = ['text'];

	const indexOf = (control: string) => controls.indexOf(control);

	const handlePanelKeyDown = (event: React.KeyboardEvent) => {
		const origin = (event.target as Element).closest('[data-index]');

		if (!origin) {
			return;
		}

		let index = Number(origin.getAttribute('data-index'));

		switch (event.key) {
			case 'ArrowDown':
			case 'ArrowRight':
				index = Math.min(index + 1, controls.length - 1);
				break;

			case 'ArrowLeft':
			case 'ArrowUp':
				index = Math.max(index - 1, 0);
				break;

			case 'End':
				index = controls.length - 1;
				break;

			case 'Home':
				index = 0;
				break;

			default:
				return;
		}

		event.preventDefault();

		const target = panelRef.current?.querySelector<HTMLButtonElement>(
			`[data-index="${index}"]`
		);

		if (target) {
			setRovingIndex(index);

			target.focus();
		}
	};

	const rovingProps = (index: number) => ({
		'data-index': index,
		'onFocus': () => setRovingIndex(index),
		'tabIndex': rovingIndex === index ? 0 : -1,
	});

	const centerX = Math.round(area.x + area.width / 2);
	const centerY = Math.round(area.y + area.height / 2);

	const add = (overlay: Overlay, delay = 0) => {
		dispatch({overlay, type: 'add-overlay'});

		onAnnounce(
			sub(
				Liferay.Language.get('x-added-to-the-center-of-the-crop-area'),
				overlayLabel(overlay)
			)
		);

		focusOverlay(editorRoot, overlay.id, delay);
	};

	return (
		<EditorSection
			title={Liferay.Language.get('annotate')}
			titleId={eid('annotate-panel-title')}
		>
			<div
				className="editor-annotate-actions"
				onKeyDown={handlePanelKeyDown}
				ref={panelRef}
			>
				<ClayButton
					{...rovingProps(indexOf('text'))}
					aria-label={Liferay.Language.get('add-text')}
					className="editor-tool-tile"
					displayType="secondary"
					onClick={() => setTextDialogOpen(true)}
				>
					<ToolTile
						icon="text"
						label={Liferay.Language.get('text')}
					/>
				</ClayButton>
			</div>

			<TextDialog
				onAdd={(overlay) =>
					add(
						{
							...overlay,
							x:
								centerX -
								textWidth(
									overlay.text,
									overlay.fontFamily,
									overlay.fontSize
								) /
									2,
							y: centerY,
						},
						450
					)
				}
				onOpenChange={setTextDialogOpen}
				open={textDialogOpen}
			/>
		</EditorSection>
	);
}
