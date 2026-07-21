/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FOCUSABLE_ELEMENTS,
	getFocusableList,
	useInteractionFocus,
} from '@clayui/shared';
import React from 'react';

export default function InitialFocus({
	children,
	menuRef,
}: {
	children: React.ReactElement;
	menuRef: React.RefObject<HTMLElement>;
}) {
	const {isFocusVisible} = useInteractionFocus();

	React.useEffect(() => {
		if (!isFocusVisible()) {
			return;
		}

		setTimeout(() => {
			const [firstFocusableElement] = getFocusableList(
				menuRef,
				FOCUSABLE_ELEMENTS
			);

			firstFocusableElement?.focus();
		}, 10);
	}, [isFocusVisible, menuRef]);

	return children;
}
