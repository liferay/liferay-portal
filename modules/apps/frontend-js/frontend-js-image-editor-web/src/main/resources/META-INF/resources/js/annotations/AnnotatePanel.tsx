/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../css/Annotations.scss';

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {useEditorId, useEditorRoot} from '../chrome/instance';
import {
	AnnotateTool,
	SHAPE_TOOLS,
	ShapeTool,
	isShapeTool,
} from '../editorConfig';
import {overlayLabel, textWidth} from '../imaging/overlayShapes';
import {focusOverlayNode} from '../stage/focusOverlayNode';
import {EditorAction} from '../state/editorReducer';
import {nextId} from '../state/ids';
import {CropRect, Overlay} from '../state/types';
import {MenuGrid} from './MenuGrid';
import {TEXT_DIALOG_CLOSE_MS, TextDialog} from './TextDialog';

const SHAPE_COLOR = '#0b5fff';

const SHAPE_LABELS: Record<ShapeTool, string> = {
	arrow: Liferay.Language.get('arrow'),
	circle: Liferay.Language.get('circle'),
	rectangle: Liferay.Language.get('rectangle'),
	square: Liferay.Language.get('square'),
};

function ToolTile({
	icon,
	label,
	menu,
}: {
	icon: string;
	label: string;

	menu?: boolean;
}) {
	return (
		<>
			<ClayIcon aria-hidden="true" symbol={icon} />

			<span className="editor-tool-tile-label">{label}</span>

			{menu && (
				<ClayIcon
					aria-hidden="true"
					className="editor-tool-tile-caret"
					symbol="angle-down-small"
				/>
			)}
		</>
	);
}

function ShapePreview({shape}: {shape: ShapeTool}) {
	return (
		<svg
			aria-hidden="true"
			className="editor-menu-preview"
			focusable="false"
			height={22}
			viewBox="0 0 16 16"
			width={22}
		>
			{shape === 'rectangle' && (
				<rect fill="currentColor" height={8} width={14} x={1} y={4} />
			)}

			{shape === 'square' && (
				<rect fill="currentColor" height={12} width={12} x={2} y={2} />
			)}

			{shape === 'circle' && (
				<circle cx={8} cy={8} fill="currentColor" r={6} />
			)}

			{shape === 'arrow' && (
				<>
					<line
						stroke="currentColor"
						strokeLinecap="round"
						strokeWidth={2}
						x1={2}
						x2={10}
						y1={8}
						y2={8}
					/>

					<polygon fill="currentColor" points="15,8 9,11 9,5" />
				</>
			)}
		</svg>
	);
}

interface Props {
	area: CropRect;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;

	onStartDrawing: (via: 'keyboard' | 'pointer') => void;

	tools: AnnotateTool[];
}

export function AnnotatePanel({
	area,
	dispatch,
	onAnnounce,
	onStartDrawing,
	tools,
}: Props) {
	const eid = useEditorId();

	const editorRoot = useEditorRoot();

	const [textDialogOpen, setTextDialogOpen] = useState(false);

	const [shapeMenuOpen, setShapeMenuOpen] = useState(false);

	const [rovingIndex, setRovingIndex] = useState(0);

	const panelRef = useRef<HTMLDivElement>(null);

	const shapeTools = tools.filter(isShapeTool);

	const controls: string[] = [
		...(tools.includes('text') ? ['text'] : []),
		...(shapeTools.length ? ['shapes'] : []),
		...(tools.includes('draw') ? ['draw'] : []),
	];

	const indexOf = (control: string) => controls.indexOf(control);

	const handlePanelKeyDown = (event: React.KeyboardEvent) => {
		const origin = (event.target as Element).closest('[data-index]');

		if (!origin) {
			return;
		}

		const isMenu = origin.hasAttribute('data-menu-trigger');

		let index = Number(origin.getAttribute('data-index'));

		switch (event.key) {
			case 'ArrowDown':
				if (isMenu) {
					return;
				}

				index = Math.min(index + 1, controls.length - 1);
				break;

			case 'ArrowRight':
				index = Math.min(index + 1, controls.length - 1);
				break;

			case 'ArrowUp':
				if (isMenu) {
					return;
				}

				index = Math.max(index - 1, 0);
				break;

			case 'ArrowLeft':
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

		focusOverlayNode(editorRoot, overlay.id, delay);
	};

	const addRectangle = () =>
		add({
			color: SHAPE_COLOR,
			height: Math.round(area.height * 0.15),
			id: nextId('shape'),
			kind: 'shape',
			width: Math.round(area.width * 0.25),
			x: Math.round(centerX - area.width * 0.125),
			y: Math.round(centerY - area.height * 0.075),
		});

	const addSquare = () => {
		const size = Math.round(Math.min(area.width, area.height) * 0.2);

		add({
			color: SHAPE_COLOR,
			height: size,
			id: nextId('shape'),
			kind: 'shape',
			width: size,
			x: Math.round(centerX - size / 2),
			y: Math.round(centerY - size / 2),
		});
	};

	const addCircle = () => {
		const size = Math.round(Math.min(area.width, area.height) * 0.2);

		add({
			color: SHAPE_COLOR,
			height: size,
			id: nextId('circle'),
			kind: 'circle',
			width: size,
			x: Math.round(centerX - size / 2),
			y: Math.round(centerY - size / 2),
		});
	};

	const addArrow = () => {
		const length = Math.round(Math.min(area.width, area.height) * 0.3);

		add({
			color: SHAPE_COLOR,
			dx: length,
			dy: 0,
			head: 'filled',
			id: nextId('arrow'),
			kind: 'arrow',
			thickness: Math.max(
				2,
				Math.round(Math.min(area.width, area.height) * 0.012)
			),
			x: Math.round(centerX - length / 2),
			y: centerY,
		});
	};

	const ADD_SHAPE: Record<ShapeTool, () => void> = {
		arrow: addArrow,
		circle: addCircle,
		rectangle: addRectangle,
		square: addSquare,
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
				{tools.includes('text') && (
					<ClayButton
						{...rovingProps(indexOf('text'))}
						aria-haspopup="dialog"
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
				)}

				{!!shapeTools.length && (
					<ClayDropDown
						active={shapeMenuOpen}
						menuElementAttrs={{className: 'editor-menu-popover'}}
						onActiveChange={setShapeMenuOpen}
						trigger={
							<ClayButton
								{...rovingProps(indexOf('shapes'))}
								aria-label={Liferay.Language.get('add-shape')}
								className="editor-tool-tile"
								data-menu-trigger
								displayType="secondary"
							>
								<ToolTile
									icon="squares"
									label={Liferay.Language.get('shape')}
									menu
								/>
							</ClayButton>
						}
					>
						<MenuGrid
							choices={SHAPE_TOOLS.filter((shape) =>
								shapeTools.includes(shape)
							).map((shape) => ({
								art: <ShapePreview shape={shape} />,
								id: shape,
								label: SHAPE_LABELS[shape],
							}))}
							columns={4}
							label={Liferay.Language.get('add-shape')}
							onChoose={(shape) => {
								setShapeMenuOpen(false);

								ADD_SHAPE[shape as ShapeTool]();
							}}
						/>
					</ClayDropDown>
				)}

				{tools.includes('draw') && (
					<ClayButton
						{...rovingProps(indexOf('draw'))}
						aria-label={Liferay.Language.get('draw')}
						className="editor-tool-tile"
						displayType="secondary"
						onClick={(event: React.MouseEvent) =>

							// A click a keyboard produced reports no
							// detail: that is the browser's own record of
							// how the button was pressed.

							onStartDrawing(
								event.detail === 0 ? 'keyboard' : 'pointer'
							)
						}
					>
						<ToolTile
							icon="pencil"
							label={Liferay.Language.get('draw')}
						/>
					</ClayButton>
				)}
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
						TEXT_DIALOG_CLOSE_MS
					)
				}
				onOpenChange={setTextDialogOpen}
				open={textDialogOpen}
			/>
		</EditorSection>
	);
}
