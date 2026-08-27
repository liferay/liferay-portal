/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import React, {useId, useState} from 'react';

import {ChatContext} from '../api';
import {getObjectFields} from '../services/getObjectFields';

import '../chat.scss';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

export interface ContentType {
	externalReferenceCode: string;
	label: string;
	name: string;
}

interface ContentTypeSelectorMessageBalloonProps {
	contentTypes: ContentType[];
	contextRef: React.MutableRefObject<ChatContext>;
	message: string;
	sendMessage: (text: string) => Promise<boolean>;
	setIsGenerating: (isGenerating: boolean) => void;
}

const ContentTypeSelectorMessageBalloon: React.FC<
	ContentTypeSelectorMessageBalloonProps
> = ({contentTypes, contextRef, message, sendMessage, setIsGenerating}) => {
	const [externalReferenceCode, setExternalReferenceCode] = useState('');
	const [submitted, setSubmitted] = useState(false);

	const selectId = useId();

	function resetSelection() {
		setExternalReferenceCode('');
		setSubmitted(false);

		setIsGenerating(false);
	}

	function reportFailure() {
		resetSelection();

		Liferay.Util.openToast({
			message: Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'danger',
		});
	}

	async function handleChange(event: React.ChangeEvent<HTMLSelectElement>) {
		const value = event.target.value;

		setExternalReferenceCode(value);

		const contentType = contentTypes.find(
			(contentType) => contentType.externalReferenceCode === value
		);

		if (!contentType) {
			return;
		}

		setIsGenerating(true);
		setSubmitted(true);

		try {
			const objectFields = await getObjectFields(
				contentType.externalReferenceCode
			);

			// eslint-disable-next-line react-compiler/react-compiler
			contextRef.current = {
				...contextRef.current,
				objectDefinitionName: contentType.name,
				objectFields: JSON.stringify(objectFields),
			};

			const sent = await sendMessage(
				`${Liferay.Language.get('generate')} ${contentType.label}`
			);

			if (!sent) {
				resetSelection();
			}
		}
		catch {
			reportFailure();
		}
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__content-generation-balloon">
			<div className="ai-assistant-chat__content-generation-balloon-header">
				<AIAssistantMessageBalloonIcon />

				<span>{message}</span>
			</div>

			<div className="ai-assistant-chat__content-generation-balloon-form">
				<ClayForm.Group>
					<label htmlFor={selectId}>
						{Liferay.Language.get('content-type')}
					</label>

					<ClaySelectWithOption
						disabled={submitted}
						id={selectId}
						onChange={handleChange}
						options={[
							{
								disabled: true,
								label: Liferay.Language.get(
									'choose-a-content-type'
								),
								value: '',
							},
							...contentTypes.map((contentType) => ({
								label: contentType.label,
								value: contentType.externalReferenceCode,
							})),
						]}
						value={externalReferenceCode}
					/>
				</ClayForm.Group>
			</div>
		</div>
	);
};

export default ContentTypeSelectorMessageBalloon;
