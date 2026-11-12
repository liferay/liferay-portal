/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Plugin} from '@ckeditor/ckeditor5-core';
import {MentionEditing} from '@ckeditor/ckeditor5-mention';
import {TextWatcher} from '@ckeditor/ckeditor5-typing';
import {uid} from '@ckeditor/ckeditor5-utils';
import {LiferayEditorConfig} from 'frontend-editor-ckeditor-web';

import {MentionUser} from '../services/CommentService';

import type {ViewElement} from '@ckeditor/ckeditor5-engine';

export type MentionQuery = {
	getRect: () => DOMRect;
	insert: (user: MentionUser) => void;
	keywords: string;
};

export type MentionQueryHandler = {
	onKeyDown: (event: KeyboardEvent) => boolean;
	onQueryChange: (query: MentionQuery | null) => void;
};

const MENTION_QUERY_REGEXP = /(?:^|[\s([{"'])@([^\s@]*)$/u;

class MentionUserMarkup extends Plugin {
	init() {
		const {conversion} = this.editor;

		conversion.for('upcast').elementToAttribute({
			converterPriority: 'high',
			model: {
				key: 'mention',
				value: (viewElement: ViewElement) => {
					const textNode = viewElement.getChild(0);

					if (!textNode?.is('$text')) {
						return;
					}

					return {
						_text: textNode.data,
						id: textNode.data,
						uid: uid(),
						userId: viewElement
							.getAttribute('class')
							?.match(/mention-user-(\S+)/)?.[1],
					};
				},
			},
			view: {
				classes: ['mention', /^mention-user-/],
				name: 'span',
			},
		});

		conversion.for('downcast').attributeToElement({
			converterPriority: 'high',
			model: 'mention',
			view: (mention, {writer}) => {
				if (!mention?.userId) {
					return;
				}

				return writer.createAttributeElement(
					'span',
					{class: `mention mention-user-${mention.userId}`},
					{id: mention.uid, priority: 20}
				);
			},
		});
	}
}

export class MentionUserQuery extends Plugin {
	handler: MentionQueryHandler | null = null;

	init() {
		const {editing, model} = this.editor;

		const textWatcher = new TextWatcher(model, (text) => {
			const match = text.match(MENTION_QUERY_REGEXP);

			return match && {keywords: match[1]};
		});

		textWatcher.on('matched', (_, {keywords}: {keywords: string}) => {
			const focus = model.document.selection.focus!;

			const range = model.createRange(
				focus.getShiftedBy(-keywords.length - 1),
				focus
			);

			if (range.start.nodeAfter?.hasAttribute('mention')) {
				this.handler?.onQueryChange(null);

				return;
			}

			this.handler?.onQueryChange({
				getRect: () =>
					editing.view.domConverter
						.viewRangeToDom(
							editing.mapper.toViewRange(
								model.createRange(
									range.start,
									range.start.getShiftedBy(1)
								)
							)
						)
						.getBoundingClientRect(),
				insert: (user) => {
					const text = `@${user.name || user.screenName}`;

					this.editor.execute('mention', {
						marker: '@',
						mention: {id: text, userId: user.id},
						range,
						text,
					});
				},
				keywords,
			});
		});

		textWatcher.on('unmatched', () => this.handler?.onQueryChange(null));

		editing.view.document.on(
			'keydown',
			(event, data) => {
				if (this.handler?.onKeyDown(data.domEvent)) {
					data.preventDefault();
					event.stop();
				}
			},
			{priority: 'highest'}
		);
	}
}

export default function getCommentMentionConfig(
	editorConfig: LiferayEditorConfig
): LiferayEditorConfig {
	return {
		...editorConfig,
		extraPlugins: [
			...(editorConfig.extraPlugins ?? []),
			MentionEditing,
			MentionUserMarkup,
			MentionUserQuery,
		],
	};
}
