/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useEffect, useMemo, useRef, useState} from 'react';

import {useEditorId} from '../chrome/instance';
import {EmojiCatalog, loadEmojiCatalog} from './emojiLoader';

import type {EmojiEntry} from './emojiData';

const COLUMNS = 8;

const COMMON = [
	'😀',
	'😃',
	'😄',
	'😁',
	'😆',
	'😅',
	'😂',
	'🤣',
	'🙂',
	'🙃',
	'😉',
	'😊',
	'😇',
	'🥰',
	'😍',
	'🤩',
	'😘',
	'😗',
	'😙',
	'😚',
	'😋',
	'😛',
	'😜',
	'🤪',
	'🤗',
	'🤭',
	'🤫',
	'🤔',
	'😐',
	'😑',
	'😶',
	'🙄',
	'😏',
	'😒',
	'😬',
	'🥱',
	'😴',
	'🤤',
	'😌',
	'😔',
	'🥳',
	'🤠',
	'😎',
	'🤓',
	'🧐',
	'😕',
	'😟',
	'🙁',
	'😲',
	'😳',
	'🥺',
	'😢',
	'😭',
	'😤',
	'😡',
	'🤬',
	'😱',
	'😨',
	'😰',
	'🤯',
	'😖',
	'😣',
	'😞',
	'😓',
	'👍',
	'👎',
	'👌',
	'🤌',
	'✌️',
	'🤞',
	'🫶',
	'🤟',
	'🤘',
	'🤙',
	'👈',
	'👉',
	'👆',
	'👇',
	'☝️',
	'✋',
	'🤚',
	'🖐️',
	'🖖',
	'👋',
	'🤝',
	'🙏',
	'👏',
	'🙌',
	'❤️',
	'🧡',
	'💛',
	'💚',
	'💙',
	'💜',
	'🖤',
	'🤍',
	'🤎',
	'💔',
	'❣️',
	'💕',
	'💞',
	'💓',
	'💗',
	'💖',
	'💯',
	'✨',
	'🎉',
	'🎊',
	'🎈',
	'🎁',
	'🔥',
	'⭐',
	'✅',
	'❌',
	'⚠️',
	'❓',
	'❗',
	'👀',
	'💬',
	'🚀',
];

const PAGE = 120;

function commonEntries(catalog: EmojiCatalog): EmojiEntry[] {
	return COMMON.map((character) => catalog.byCharacter.get(character)).filter(
		Boolean
	) as EmojiEntry[];
}

interface Props {
	onChoose: (emoji: EmojiEntry) => void;
}

export function EmojiPicker({onChoose}: Props) {
	const eid = useEditorId();

	const [catalog, setCatalog] = useState<EmojiCatalog | null>(null);

	const [query, setQuery] = useState('');

	useEffect(() => {
		let alive = true;

		loadEmojiCatalog().then((loaded) => {
			if (alive) {
				setCatalog(loaded);
			}
		});

		return () => {
			alive = false;
		};
	}, []);

	const [limit, setLimit] = useState(PAGE);

	const gridRef = useRef<HTMLDivElement>(null);

	const searching = Boolean(query.trim());

	const matches = useMemo(() => {
		if (!catalog) {
			return [];
		}

		const needle = query.trim().toLowerCase();

		if (!needle) {
			return commonEntries(catalog);
		}

		const words = needle.split(/\s+/);

		return catalog.entries.filter((_, index) => {
			const name = catalog.searchKeys[index];

			return words.every((word) => name.includes(word));
		});
	}, [catalog, query]);

	const shown = matches.slice(0, limit);

	const rows: EmojiEntry[][] = [];

	for (let index = 0; index < shown.length; index += COLUMNS) {
		rows.push(shown.slice(index, index + COLUMNS));
	}

	const move = (index: number) => {
		const cells =
			gridRef.current?.querySelectorAll<HTMLButtonElement>('button');

		if (!cells?.length) {
			return;
		}

		cells[Math.min(Math.max(index, 0), cells.length - 1)]?.focus();
	};

	const handleGridKeyDown = (event: React.KeyboardEvent) => {
		const index = Number(
			(event.target as Element)
				.closest('[data-cell]')
				?.getAttribute('data-cell')
		);

		if (Number.isNaN(index)) {
			return;
		}

		switch (event.key) {
			case 'ArrowDown':
				move(index + COLUMNS);
				break;

			case 'ArrowLeft':
				move(index - 1);
				break;

			case 'ArrowRight':
				move(index + 1);
				break;

			case 'ArrowUp':
				if (index < COLUMNS) {
					gridRef.current
						?.closest('.editor-emoji-picker')
						?.querySelector<HTMLInputElement>('input')
						?.focus();

					break;
				}

				move(index - COLUMNS);
				break;

			case 'End':
				move(shown.length - 1);
				break;

			case 'Home':
				move(0);
				break;

			default:
				return;
		}

		event.preventDefault();
		event.stopPropagation();
	};

	if (!catalog) {
		return <div aria-hidden="true" className="editor-emoji-popover" />;
	}

	return (
		<div className="editor-emoji-picker">
			<div className="editor-emoji-header">
				<ClayInput
					aria-describedby={eid('emoji-search-count')}
					aria-label={Liferay.Language.get('search-emoji')}
					autoFocus
					id={eid('emoji-search')}
					onChange={(event) => {
						setQuery(event.target.value);

						setLimit(PAGE);
					}}
					onKeyDown={(event: React.KeyboardEvent) => {
						if (event.key === 'ArrowDown') {
							event.preventDefault();
							event.stopPropagation();

							move(0);
						}
					}}
					placeholder={Liferay.Language.get('search-emoji')}
					sizing="sm"
					type="search"
					value={query}
				/>

				<div
					className="editor-emoji-count"
					id={eid('emoji-search-count')}
					role="status"
				>
					{searching
						? sub(Liferay.Language.get('x-emoji'), matches.length)
						: sub(
								Liferay.Language.get(
									'search-to-find-any-of-the-x-emoji'
								),
								catalog.entries.length
							)}
				</div>
			</div>

			{!matches.length && (
				<div className="editor-emoji-grid">
					<p className="editor-emoji-empty">
						{sub(
							Liferay.Language.get('there-are-no-results-for-x'),
							query.trim()
						)}
					</p>
				</div>
			)}

			<div
				aria-label={Liferay.Language.get('add-emoji')}
				className="editor-emoji-grid editor-menu-grid"
				hidden={!matches.length}
				onKeyDown={handleGridKeyDown}
				onScroll={(event) => {
					const box = event.currentTarget;

					if (
						box.scrollTop + box.clientHeight >=
							box.scrollHeight - 64 &&
						limit < matches.length
					) {
						setLimit((current) => current + PAGE);
					}
				}}
				ref={gridRef}
				role="grid"
			>
				{rows.map((row, rowIndex) => (
					<div className="editor-emoji-row" key={rowIndex} role="row">
						{row.map((entry, columnIndex) => {
							const index = rowIndex * COLUMNS + columnIndex;

							return (
								<span key={entry.c} role="gridcell">
									<button
										aria-label={entry.n}
										className="btn editor-emoji-cell editor-menu-cell"
										data-cell={index}
										onClick={() => onChoose(entry)}
										tabIndex={index === 0 ? 0 : -1}
										title={entry.n}
										type="button"
									>
										{entry.c}
									</button>
								</span>
							);
						})}
					</div>
				))}
			</div>
		</div>
	);
}
