/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {useEditorId, useEditorRoot} from '../chrome/instance';
import {overlayLabel} from '../imaging/overlayShapes';
import {focusOverlayNode} from '../stage/focusOverlayNode';
import {EditorAction} from '../state/editorReducer';
import {nextId} from '../state/ids';
import {Overlay} from '../state/types';
import {LayerProperties} from './LayerProperties';

interface Props {
	dispatch: (action: EditorAction) => void;

	multiSelectedIds: string[];
	onAnnounce: (message: string) => void;
	onProportionalChange: (proportional: boolean) => void;
	onSelect: (id: string | null) => void;
	overlays: Overlay[];

	proportional: boolean;

	selectedId: string | null;
}

export function LayersPanel({
	dispatch,
	multiSelectedIds,
	onAnnounce,
	onProportionalChange,
	onSelect,
	overlays,
	proportional,
	selectedId,
}: Props) {
	const eid = useEditorId();

	const editorRoot = useEditorRoot();

	const items = [...overlays].reverse();

	const selected =
		items.find((overlay) => overlay.id === selectedId) ?? items[0] ?? null;

	const [rovingPos, setRovingPos] = useState({column: 0, row: 0});

	const listRef = useRef<HTMLUListElement>(null);

	const selectedRow = items.findIndex(
		(overlay) => overlay.id === selected?.id
	);

	useEffect(() => {
		if (selectedRow >= 0) {
			setRovingPos((pos) =>
				pos.row === selectedRow ? pos : {column: 0, row: selectedRow}
			);
		}
	}, [selectedRow]);

	if (!items.length) {
		return null;
	}

	const handleListKeyDown = (event: React.KeyboardEvent) => {
		const origin = (event.target as Element).closest('[data-row]');

		if (!origin) {
			return;
		}

		let row = Number(origin.getAttribute('data-row'));
		let column = Number(origin.getAttribute('data-column'));

		const lastRow = items.length - 1;
		const lastColumn = 4;

		let horizontal = 0;

		switch (event.key) {
			case 'ArrowDown':
				row = Math.min(row + 1, lastRow);
				break;

			case 'ArrowUp':
				row = Math.max(row - 1, 0);
				break;

			case 'ArrowRight':
				horizontal = 1;
				break;

			case 'ArrowLeft':
				horizontal = -1;
				break;

			case 'End':
				row = lastRow;
				column = 0;
				break;

			case 'Home':
				row = 0;
				column = 0;
				break;

			default:
				return;
		}

		event.preventDefault();
		event.stopPropagation();

		const buttonAt = (targetRow: number, targetColumn: number) =>
			listRef.current?.querySelector<HTMLButtonElement>(
				`[data-row="${targetRow}"][data-column="${targetColumn}"]`
			);

		if (horizontal) {
			let enabled: number | undefined;

			for (
				let next = column + horizontal;
				next >= 0 && next <= lastColumn;
				next += horizontal
			) {
				if (!buttonAt(row, next)?.disabled) {
					enabled = next;
					break;
				}
			}

			if (enabled === undefined) {
				return;
			}

			column = enabled;
		}
		else if (buttonAt(row, column)?.disabled) {
			column = 0;
		}

		const target = buttonAt(row, column);

		if (target) {
			setRovingPos({column, row});

			target.focus();
		}
	};

	const rovingProps = (row: number, column: number) => ({
		'data-column': column,
		'data-row': row,
		'onFocus': () => setRovingPos({column, row}),
		'tabIndex':
			rovingPos.row === row && rovingPos.column === column ? 0 : -1,
	});

	const remove = (overlay: Overlay) => {
		window.setTimeout(() => {
			const next =
				listRef.current?.querySelector<HTMLElement>(
					'.editor-layer-name'
				) ??
				editorRoot().querySelector<HTMLElement>('.editor-workspace');

			next?.focus();
		}, 0);

		if (
			multiSelectedIds.length > 1 &&
			multiSelectedIds.includes(overlay.id)
		) {
			dispatch({ids: multiSelectedIds, type: 'remove-overlays'});

			onAnnounce(
				sub(
					Liferay.Language.get('x-annotations-removed'),
					multiSelectedIds.length
				)
			);

			onSelect(null);

			return;
		}

		dispatch({ids: [overlay.id], type: 'remove-overlays'});

		onAnnounce(
			sub(
				Liferay.Language.get('x-removed-from-the-image'),
				overlayLabel(overlay)
			)
		);

		onSelect(null);
	};

	const duplicate = (overlay: Overlay) => {
		const newId = nextId(overlay.kind);

		dispatch({id: overlay.id, newId, type: 'duplicate-overlay'});

		onAnnounce(
			sub(Liferay.Language.get('x-duplicated'), overlayLabel(overlay))
		);

		onSelect(newId);
	};

	const reorder = (
		overlay: Overlay,
		visualDirection: -1 | 1,
		row?: number
	) => {

		// Visually up (-1) means later in paint order (+1 in the array).

		dispatch({
			direction: visualDirection === -1 ? 1 : -1,
			id: overlay.id,
			type: 'move-overlay-layer',
		});

		// The layer moves to a new row, and the button just used may now
		// be disabled (it reached the end): follow the layer and fall back
		// to its name so focus is never dropped.

		if (row !== undefined) {
			const targetRow = row + visualDirection;
			const column = visualDirection === -1 ? 1 : 2;

			window.setTimeout(() => {
				const list = listRef.current;

				const action = list?.querySelector<HTMLButtonElement>(
					`[data-row="${targetRow}"][data-column="${column}"]`
				);

				const fallback = list?.querySelector<HTMLButtonElement>(
					`[data-row="${targetRow}"][data-column="0"]`
				);

				(action && !action.disabled ? action : fallback)?.focus();
			}, 0);
		}

		onAnnounce(
			sub(
				visualDirection === -1
					? Liferay.Language.get('x-moved-up')
					: Liferay.Language.get('x-moved-down'),
				overlayLabel(overlay)
			)
		);
	};

	return (
		<EditorSection
			title={Liferay.Language.get('layers')}
			titleId={eid('layers-panel-title')}
		>
			<span className="sr-only" id={eid('layer-name-description')}>
				{Liferay.Language.get(
					'press-enter-to-move-focus-to-this-element-on-the-image-press-delete-to-remove-it'
				)}
			</span>

			<ul
				className="editor-layer-list list-unstyled small"
				onKeyDown={handleListKeyDown}
				ref={listRef}
			>
				{items.map((overlay, index) => {
					const label = overlayLabel(overlay);
					const isSelected = overlay.id === selected?.id;
					const row = index;

					return (
						<li
							className={classNames('editor-layer-item', {
								'editor-layer-item-grouped':
									multiSelectedIds.includes(overlay.id),
								'editor-layer-item-selected': isSelected,
							})}
							key={overlay.id}
						>
							<button
								{...rovingProps(row, 0)}
								aria-describedby={eid('layer-name-description')}
								aria-label={label}
								aria-pressed={isSelected}
								className="editor-layer-name"
								onClick={() => onSelect(overlay.id)}
								onKeyDown={(event: React.KeyboardEvent) => {
									if (event.key === 'Enter') {

										// Jump to the element on the
										// stage, ready to be moved.

										event.preventDefault();

										onSelect(overlay.id);

										focusOverlayNode(
											editorRoot,
											overlay.id
										);
									}
									else if (
										event.key === 'Delete' ||
										event.key === 'Backspace'
									) {
										event.preventDefault();
										remove(overlay);
									}
								}}
								type="button"
							>

								{/*
								 * The character itself, in front of its
								 * name: decoration a sighted eye scans
								 * faster than a word, hidden from the
								 * name the row already has.
								 */}

								{overlay.kind === 'emoji' && (
									<span
										aria-hidden="true"
										className="editor-layer-glyph"
									>
										{overlay.character}
									</span>
								)}

								{label}
							</button>

							<span className="editor-layer-actions">
								<ClayButtonWithIcon
									{...rovingProps(row, 1)}
									aria-label={sub(
										Liferay.Language.get('move-x-up'),
										label
									)}
									borderless
									disabled={index === 0}
									displayType="secondary"
									onClick={() => reorder(overlay, -1, row)}
									size="xs"
									symbol="angle-up"
									title={sub(
										Liferay.Language.get('move-x-up'),
										label
									)}
								/>

								<ClayButtonWithIcon
									{...rovingProps(row, 2)}
									aria-label={sub(
										Liferay.Language.get('move-x-down'),
										label
									)}
									borderless
									disabled={index === items.length - 1}
									displayType="secondary"
									onClick={() => reorder(overlay, 1, row)}
									size="xs"
									symbol="angle-down"
									title={sub(
										Liferay.Language.get('move-x-down'),
										label
									)}
								/>

								<ClayButtonWithIcon
									{...rovingProps(row, 3)}
									aria-label={sub(
										Liferay.Language.get('duplicate-x'),
										label
									)}
									borderless
									displayType="secondary"
									onClick={() => duplicate(overlay)}
									size="xs"
									symbol="copy"
									title={sub(
										Liferay.Language.get('duplicate-x'),
										label
									)}
								/>

								<ClayButtonWithIcon
									{...rovingProps(row, 4)}
									aria-label={sub(
										Liferay.Language.get('delete-x'),
										label
									)}
									borderless
									displayType="secondary"
									onClick={() => remove(overlay)}
									size="xs"
									symbol="trash"
									title={sub(
										Liferay.Language.get('delete-x'),
										label
									)}
								/>
							</span>
						</li>
					);
				})}
			</ul>

			{/*
			 * A status region, mounted at all times: a live region that
			 * appears together with its message is not reliably spoken,
			 * one that already exists and changes is. It carries the
			 * group's count for everyone, which is also why the global
			 * announcer stays quiet about it.
			 */}

			<p className="editor-group-note" role="status">
				{multiSelectedIds.length >= 2
					? sub(
							Liferay.Language.get(
								'x-annotations-are-grouped-they-move-and-delete-together-select-one-without-shift-to-edit-its-properties'
							),
							multiSelectedIds.length
						)
					: ''}
			</p>

			{multiSelectedIds.length < 2 && selected && (
				<LayerProperties
					dispatch={dispatch}
					key={selected.id}
					onAnnounce={onAnnounce}
					onProportionalChange={onProportionalChange}
					overlay={selected}
					proportional={proportional}
				/>
			)}
		</EditorSection>
	);
}
