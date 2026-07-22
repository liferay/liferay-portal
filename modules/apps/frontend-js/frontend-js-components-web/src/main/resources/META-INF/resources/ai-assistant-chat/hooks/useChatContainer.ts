/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useId, useMemo} from 'react';

import useStableCallback from '../../hooks/useStableCallback';

interface UseChatContainerParams {
	id?: string;
	onClose: () => void;
	titleBarLeading?: React.ReactNode;
	titleBarProps?: React.HTMLAttributes<HTMLDivElement>;
}

export default function useChatContainer({
	id,
	onClose,
	titleBarLeading,
	titleBarProps,
}: UseChatContainerParams) {
	const generatedId = useId();
	const titleId = useId();

	const dialogId = id ?? generatedId;

	const stableOnClose = useStableCallback(onClose);

	const contextValue = useMemo(
		() => ({
			dialogId,
			onClose: stableOnClose,
			titleBarLeading,
			titleBarProps,
			titleId,
		}),
		[dialogId, stableOnClose, titleBarLeading, titleBarProps, titleId]
	);

	return contextValue;
}
