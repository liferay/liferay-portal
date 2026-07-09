/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';
import ReactMarkdown, {Components} from 'react-markdown';
import remarkGfm from 'remark-gfm';

import ChatbotAvatar from './ChatbotAvatar';
import FeedbackActions from './FeedbackActions';

const MARKDOWN_COMPONENTS: Components = {
	a: ({node: _node, ...props}) => (
		<a {...props} rel="noopener noreferrer" target="_blank" />
	),
};

const REMARK_PLUGINS = [remarkGfm];

interface AssistantMessageProps {
	avatar?: string;
	feedbackGiven?: boolean;
	onThumbsDown?: () => void;
	onThumbsUp?: () => void;
	text: string;
	title: string;
}

export default function AssistantMessage({
	avatar,
	feedbackGiven,
	onThumbsDown,
	onThumbsUp,
	text,
	title,
}: AssistantMessageProps) {
	return (
		<div className="aihub-msg-assistant">
			<div className="aihub-msg-assistant-icon">
				<ChatbotAvatar
					avatar={avatar}
					className="aihub-msg-assistant-company-logo"
					fallback={<ClayIcon symbol="stars" />}
					title={title}
				/>
			</div>

			<div className="aihub-msg-assistant-text">
				<ReactMarkdown
					components={MARKDOWN_COMPONENTS}
					remarkPlugins={REMARK_PLUGINS}
				>
					{text}
				</ReactMarkdown>

				{onThumbsDown && onThumbsUp && (
					<FeedbackActions
						feedbackGiven={feedbackGiven}
						onThumbsDown={onThumbsDown}
						onThumbsUp={onThumbsUp}
					/>
				)}
			</div>
		</div>
	);
}
