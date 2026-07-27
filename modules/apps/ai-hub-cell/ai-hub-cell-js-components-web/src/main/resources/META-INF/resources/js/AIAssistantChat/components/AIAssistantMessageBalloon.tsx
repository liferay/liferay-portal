/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import FeedbackActionsRow from '../../ReportFeedback/FeedbackActionsRow';

import '../chat.scss';
import renderAIAssistantMessageMarkdown from '../utils/renderAIAssistantMessageMarkdown';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

interface AssistantMessageBalloonProps {
	error: boolean;
	feedbackGiven?: boolean;
	message: string;
	onReport?: () => void;
	onThumbsUp?: () => void;
}

const AssistantMessageBalloon: React.FC<AssistantMessageBalloonProps> = ({
	error,
	feedbackGiven,
	message,
	onReport,
	onThumbsUp,
}) => {
	return (
		<div
			className={`${error ? 'ai-assistant-chat__ai-assistant-error-message-balloon' : 'ai-assistant-chat__ai-assistant-message-balloon'} d-flex flex-column mb-2 rounded`}
		>
			<div className="d-flex flex-row">
				<AIAssistantMessageBalloonIcon error={error} />

				{error ? (
					<span className="m-2">
						{message ||
							Liferay.Language.get('generating-content-failed')}
					</span>
				) : (
					<div
						className="flex-grow-1 m-2"
						dangerouslySetInnerHTML={{
							__html: renderAIAssistantMessageMarkdown(message),
						}}
					/>
				)}
			</div>

			{onReport && !error && (
				<FeedbackActionsRow
					className="mb-1 ml-2"
					feedbackGiven={feedbackGiven}
					onReport={onReport}
					onThumbsUp={onThumbsUp}
				/>
			)}
		</div>
	);
};

export default AssistantMessageBalloon;
