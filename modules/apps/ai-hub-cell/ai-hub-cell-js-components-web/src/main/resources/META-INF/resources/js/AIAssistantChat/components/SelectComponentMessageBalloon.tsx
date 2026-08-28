/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import React, {useId, useState} from 'react';

import {AIAssistantActionOutcome, requestActionOutcome} from '../api';
import {AgentComponent} from '../types';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

import '../chat.scss';

export interface SelectComponentMessageBalloonProps {
	component: AgentComponent;
	onAction?: (outcome: AIAssistantActionOutcome) => void;
	setIsGenerating: React.Dispatch<React.SetStateAction<boolean>>;
}

const SelectComponentMessageBalloon: React.FC<
	SelectComponentMessageBalloonProps
> = ({component, onAction, setIsGenerating}) => {
	const [selectedIndex, setSelectedIndex] = useState('');
	const [submitted, setSubmitted] = useState(false);

	const titleId = useId();

	async function handleChange(event: React.ChangeEvent<HTMLSelectElement>) {
		const value = event.target.value;

		setSelectedIndex(value);

		const option = component.options[Number(value)];

		if (!option) {
			return;
		}

		setSubmitted(true);

		setIsGenerating(true);

		const outcome = await requestActionOutcome(
			option.action['http-request']
		);

		if (!outcome.success) {
			setIsGenerating(false);

			setSubmitted(false);

			setSelectedIndex('');
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

			<div className="ai-assistant-chat__content-generation-balloon-form">
				<ClayForm.Group>
					<ClaySelectWithOption
						aria-labelledby={titleId}
						disabled={submitted}
						onChange={handleChange}
						options={[
							{
								disabled: true,
								label: Liferay.Language.get('choose-an-option'),
								value: '',
							},
							...component.options.map((option, index) => ({
								label: option.label,
								value: String(index),
							})),
						]}
						value={selectedIndex}
					/>
				</ClayForm.Group>
			</div>
		</div>
	);
};

export default SelectComponentMessageBalloon;
