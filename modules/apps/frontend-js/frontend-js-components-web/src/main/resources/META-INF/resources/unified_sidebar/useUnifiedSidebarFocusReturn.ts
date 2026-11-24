/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useRef} from 'react';

const SETTLE_TIMEOUT = 500;

export function useUnifiedSidebarFocusReturn({
	buttonRef,
	navigationId,
	visible,
}: {
	buttonRef: React.RefObject<HTMLButtonElement | null>;
	navigationId: string;
	visible: boolean;
}) {
	const shouldFocusRef = useRef(false);

	const trackFocus = useCallback(
		(visible: boolean) => {
			shouldFocusRef.current =
				!visible &&
				!!document
					.getElementById(navigationId)
					?.contains(document.activeElement);
		},
		[navigationId]
	);

	useEffect(() => {
		if (visible) {
			return;
		}

		// Keep keyboard users in place when the navigation hides with focus
		// inside it. The side panel may also return focus to the toggler
		// while it still moves, so wait for it to settle and focus it again,
		// which aligns its tooltip with where it ends up.

		const button = buttonRef.current;

		if (!button) {
			return;
		}

		let frameId: number;
		let left = button.getBoundingClientRect().left;
		let stableFrames = 0;

		const start = performance.now();

		const focusWhenSettled = () => {
			const currentLeft = button.getBoundingClientRect().left;

			const settled =
				currentLeft === left &&
				!document
					.getElementById(navigationId)
					?.classList.contains('c-slideout-transition');

			stableFrames = settled ? stableFrames + 1 : 0;

			left = currentLeft;

			if (
				stableFrames >= 2 ||
				performance.now() - start > SETTLE_TIMEOUT
			) {
				if (
					shouldFocusRef.current ||
					document.activeElement === button
				) {
					shouldFocusRef.current = false;

					button.blur();

					frameId = requestAnimationFrame(() => button.focus());
				}

				return;
			}

			frameId = requestAnimationFrame(focusWhenSettled);
		};

		frameId = requestAnimationFrame(focusWhenSettled);

		return () => cancelAnimationFrame(frameId);
	}, [buttonRef, navigationId, visible]);

	return trackFocus;
}
