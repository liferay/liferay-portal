/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RefObject, useEffect} from 'react';

export default function useFocusFirstInvalidField(
	formRef: RefObject<HTMLFormElement>,
	{isSubmitting, submitCount}: {isSubmitting: boolean; submitCount: number}
) {
	useEffect(() => {
		if (!submitCount || isSubmitting) {
			return;
		}

		formRef.current
			?.querySelector<HTMLElement>('[aria-invalid="true"]')
			?.focus();
	}, [formRef, isSubmitting, submitCount]);
}
