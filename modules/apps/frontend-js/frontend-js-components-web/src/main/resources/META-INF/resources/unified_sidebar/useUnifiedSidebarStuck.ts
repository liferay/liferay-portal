/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import {UnifiedSidebarItem} from './types';

const SCROLLER_SELECTOR = '.sidebar-body';

export function useUnifiedSidebarStuck(
	ref: React.RefObject<HTMLElement | null>,
	items: Array<UnifiedSidebarItem>,
	anchorSelector?: string
) {
	const [stuck, setStuck] = useState(false);

	useEffect(() => {
		const scroller =
			ref.current?.querySelector<HTMLElement>(SCROLLER_SELECTOR);

		if (!scroller) {
			return;
		}

		// Place the sentinel before the anchor item when there is one, so the
		// list counts as stuck once that item sticks to the top.

		const anchorItem = anchorSelector
			? scroller.querySelector<HTMLElement>(anchorSelector)
			: null;

		const sentinel = document.createElement(anchorItem ? 'li' : 'div');

		sentinel.setAttribute('role', 'none');

		if (anchorItem) {
			anchorItem.before(sentinel);
		}
		else {
			scroller.prepend(sentinel);
		}

		const observer = new IntersectionObserver(
			([entry]) => setStuck(!entry.isIntersecting),
			{root: scroller}
		);

		observer.observe(sentinel);

		return () => {
			observer.disconnect();

			sentinel.remove();
		};
	}, [anchorSelector, items, ref]);

	return stuck;
}
