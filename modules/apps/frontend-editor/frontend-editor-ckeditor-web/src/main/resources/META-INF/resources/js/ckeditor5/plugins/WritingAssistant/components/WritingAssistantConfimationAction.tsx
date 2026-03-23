/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import ConfirmationBalloon from './ConfimartionBalloon';

export default function WritingAssistantConfirmationAction({
	containerRef,
	handleAccept,
	handleDiscard,
	hideBalloon,
}: {
	containerRef: HTMLElement;
	handleAccept: () => void;
	handleDiscard: () => void;
	hideBalloon: () => void;
}) {
	const [active, setActive] = useState(true);

	const actions = [
		{
			disabled: false,
			name: Liferay.Language.get('accept'),
			onClick: () => {
				handleAccept();
				setActive(false);
				hideBalloon();
			},
			symbolLeft: 'check',
		},
		{
			disabled: false,
			name: Liferay.Language.get('discard'),
			onClick: () => {
				handleDiscard();
				setActive(false);
				hideBalloon();
			},
			symbolLeft: 'times',
		},
		{
			disabled: true,
			name: Liferay.Language.get('regenerate'),
			onClick: () => {},
			symbolLeft: 'reset',
		},
	];

	useEffect(() => {
		function handleDocumentClick(event: MouseEvent) {
			if (
				active &&
				containerRef &&
				!containerRef.contains(event.target as Node)
			) {
				setActive(false);
				hideBalloon();
			}
		}
		document.addEventListener('mousedown', handleDocumentClick);

		return () => {
			document.removeEventListener('mousedown', handleDocumentClick);
		};
	}, [active, containerRef, hideBalloon]);

	if (!active) {
		return null;
	}

	return <ConfirmationBalloon actions={actions} />;
}
