/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export interface ChatContainerProps
	extends React.HTMLAttributes<HTMLDivElement> {
	children: React.ReactNode;
	onOpenChange?: (open: boolean) => void;
	open?: boolean;
	trigger: React.ReactElement & {
		ref?: React.Ref<HTMLElement>;
	};
}
