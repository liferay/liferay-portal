/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useId, useMemo} from 'react';

import useStableCallback from '../../hooks/useStableCallback';

interface UseChatContainerParams {
	id?: string;
	onClose: () => void;
}

export default function useChatContainer({
	id,
	onClose,
}: UseChatContainerParams) {
	const generatedId = useId();
	const titleId = useId();

	const dialogId = id ?? generatedId;

	const stableOnClose = useStableCallback(onClose);

	const contextValue = useMemo(
		() => ({dialogId, onClose: stableOnClose, titleId}),
		[dialogId, stableOnClose, titleId]
	);

	return contextValue;
}
