/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

const modalStack: Array<React.RefObject<HTMLDivElement>> = [];

function getZIndexAbove(
	lowerElement: HTMLElement,
	element: HTMLElement
): number | undefined {
	const zIndexes = [lowerElement, element]
		.map((item) => Number.parseInt(getComputedStyle(item).zIndex, 10))
		.filter(Number.isFinite);

	return zIndexes.length ? Math.max(...zIndexes) + 1 : undefined;
}

export function useModalStack(
	modalRef: React.RefObject<HTMLDivElement>,
	{active, disabled}: {active: boolean; disabled: boolean}
) {
	const [zIndex, setZIndex] = useState<number>();

	useEffect(() => {
		if (!active) {
			return;
		}

		modalStack.push(modalRef);

		return () => {
			const index = modalStack.indexOf(modalRef);

			if (index >= 0) {
				modalStack.splice(index, 1);
			}
		};
	}, [active]);

	useEffect(() => {
		if (!active || disabled) {
			return;
		}

		const lowerModalRef = modalStack[modalStack.indexOf(modalRef) - 1];

		if (modalRef.current && lowerModalRef?.current) {
			const nextZIndex = getZIndexAbove(
				lowerModalRef.current,
				modalRef.current
			);

			setZIndex((current) => current ?? nextZIndex);
		}
	}, [active, disabled]);

	return {zIndex};
}
