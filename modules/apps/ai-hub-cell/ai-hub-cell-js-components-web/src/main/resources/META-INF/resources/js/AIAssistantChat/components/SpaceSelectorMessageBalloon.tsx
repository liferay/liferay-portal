/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React, {useId, useState} from 'react';

import {ChatContext} from '../api';
import {Space} from '../services/getSpaces';

import '../chat.scss';

interface SpaceSelectorMessageBalloonProps {
	contextRef: React.MutableRefObject<ChatContext>;
	message: string;
	onSelectSpace: (space: Space) => void;
	spaces: Space[];
}

const SpaceSelectorMessageBalloon: React.FC<
	SpaceSelectorMessageBalloonProps
> = ({contextRef, message, onSelectSpace, spaces}) => {
	const [siteId, setSiteId] = useState('');
	const [submitted, setSubmitted] = useState(false);

	const selectId = useId();

	function handleChange(event: React.ChangeEvent<HTMLSelectElement>) {
		const value = event.target.value;

		setSiteId(value);

		const space = spaces.find((space) => String(space.siteId) === value);

		if (!space) {
			return;
		}

		// eslint-disable-next-line react-compiler/react-compiler
		contextRef.current = {
			...contextRef.current,
			spaceId: value,
		};

		setSubmitted(true);

		onSelectSpace(space);
	}

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon ai-assistant-chat__content-generation-balloon">
			<div className="ai-assistant-chat__content-generation-balloon-header">
				<ClayIcon spritemap={Liferay.Icons.spritemap} symbol="stars" />

				<span>{message}</span>
			</div>

			<div className="ai-assistant-chat__content-generation-balloon-form">
				<ClayForm.Group>
					<label htmlFor={selectId}>
						{Liferay.Language.get('space')}
					</label>

					<ClaySelectWithOption
						disabled={submitted}
						id={selectId}
						onChange={handleChange}
						options={[
							{
								disabled: true,
								label: Liferay.Language.get('select-a-space'),
								value: '',
							},
							...spaces.map((space) => ({
								label: space.name,
								value: String(space.siteId),
							})),
						]}
						value={siteId}
					/>
				</ClayForm.Group>
			</div>
		</div>
	);
};

export default SpaceSelectorMessageBalloon;
