/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

export default function LanguageIdIcon({languageId}: {languageId: string}) {
	return (
		<ClayIcon
			className="ai-assistant-chat__language-id-icon"
			spritemap={Liferay.Icons.spritemap}
			symbol={languageId.replace(/_/g, '-').toLowerCase()}
		/>
	);
}
