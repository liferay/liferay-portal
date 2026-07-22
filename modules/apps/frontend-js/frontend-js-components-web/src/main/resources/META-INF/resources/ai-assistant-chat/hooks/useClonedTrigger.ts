/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface UseClonedTriggerParams {
	dialogId: string;
	onClick?: (event: React.MouseEvent<HTMLElement>) => void;
	open: boolean;
	triggerRef:
		| React.RefObject<HTMLElement | null>
		| React.MutableRefObject<HTMLElement | null>;
}

export default function useClonedTrigger(
	trigger: React.ReactElement & {ref?: React.Ref<HTMLElement>},
	{dialogId, onClick, open, triggerRef}: UseClonedTriggerParams
) {

	/*
	 * React Compiler cannot statically prove that cloning the trigger element
	 * is safe. That's the reason we're adding the eslint-disable below. This follows
	 * the same pattern as modal/components/Modal.tsx.
	 */

	// eslint-disable-next-line react-compiler/react-compiler
	return React.cloneElement(trigger, {
		'aria-controls': dialogId,
		'aria-expanded': open,
		'aria-haspopup': 'dialog',
		...(onClick && {onClick}),
		'ref': triggerRef,
	});
}
