/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import React, {useId, useState} from 'react';

import {AIAssistantActionOutcome, requestActionOutcome} from '../api';
import {AgentComponent, AgentComponentOption} from '../types';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

import '../chat.scss';

export interface QuickRepliesMessageBalloonProps {
	component: AgentComponent;
	onAction?: (outcome: AIAssistantActionOutcome) => void;
	setIsGenerating: React.Dispatch<React.SetStateAction<boolean>>;
}

const QuickRepliesMessageBalloon: React.FC<QuickRepliesMessageBalloonProps> = ({
	component,
	onAction,
	setIsGenerating,
}) => {
	const [submitted, setSubmitted] = useState(false);

	const titleId = useId();

	async function handleClick(option: AgentComponentOption) {
		setSubmitted(true);

		setIsGenerating(true);

		const outcome = await requestActionOutcome(
			option.action['http-request']
		);

		if (!outcome.success) {
			setIsGenerating(false);

			setSubmitted(false);
		}

		onAction?.(outcome);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__content-generation-balloon">
			<div className="ai-assistant-chat__content-generation-balloon-header">
				<AIAssistantMessageBalloonIcon />

				<span
					className="ai-assistant-chat__content-generation-balloon-title"
					id={titleId}
				>
					{component.title}
				</span>
			</div>

			<div
				aria-labelledby={titleId}
				className="ai-assistant-chat__quick-replies"
				role="group"
			>
				{component.options.map((option, index) => (
					<ClayButton
						className="ai-assistant-chat__quick-reply"
						disabled={submitted}
						displayType="secondary"
						key={index}
						onClick={() => handleClick(option)}
						size="sm"
					>
						{option.label}
					</ClayButton>
				))}
			</div>
		</div>
	);
};

export default QuickRepliesMessageBalloon;
