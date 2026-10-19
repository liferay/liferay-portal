/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import DOMPurify from 'dompurify';
import React, {useState} from 'react';

import '../chat.scss';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

interface FieldValueMessageBalloonProps {
	onApply: () => void;
	onRegenerate: () => void;
	richTextFieldNames: string[];
	values: Record<string, string>;
}

const FieldValueMessageBalloon: React.FC<FieldValueMessageBalloonProps> = ({
	onApply,
	onRegenerate,
	richTextFieldNames,
	values,
}) => {
	const [applied, setApplied] = useState(false);

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon d-flex flex-column mb-2 rounded">
			<div className="d-flex flex-row font-weight-semi-bold">
				<AIAssistantMessageBalloonIcon />

				<div className="m-2">
					{Object.entries(values).map(([name, value]) => {
						if (richTextFieldNames.includes(name)) {
							return (
								<div

									// XSS: value is sanitized by DOMPurify, since
									// rich text values are HTML by design

									dangerouslySetInnerHTML={{
										__html: DOMPurify.sanitize(value),
									}}
									key={name}
								/>
							);
						}

						return <div key={name}>{value}</div>;
					})}
				</div>
			</div>

			<div className="d-flex justify-content-end mb-2 mr-2">
				<ClayButton
					className="mr-2"
					disabled={applied}
					displayType="secondary"
					onClick={onRegenerate}
					size="sm"
				>
					<ClayIcon
						className="mr-2"
						spritemap={Liferay.Icons.spritemap}
						symbol="reload"
					/>

					{Liferay.Language.get('try-again')}
				</ClayButton>

				<ClayButton
					disabled={applied}
					displayType="primary"
					onClick={() => {
						setApplied(true);

						onApply();
					}}
					size="sm"
				>
					{Liferay.Language.get('apply')}
				</ClayButton>
			</div>
		</div>
	);
};

export default FieldValueMessageBalloon;
