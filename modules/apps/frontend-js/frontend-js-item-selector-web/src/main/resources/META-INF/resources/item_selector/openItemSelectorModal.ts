/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import DetachedItemSelectorModal, {
	TDetachedItemSelectorModal,
} from './DetachedItemSelectorModal';

/**
 * This utility enables item selector to be rendered from any JS context.
 */

export default function openItemSelectorModal<T>(
	props: TDetachedItemSelectorModal<T>
) {

	// Mount in detached node; Clay will take care of appending to `document.body`.
	// See: https://github.com/liferay/clay/blob/master/packages/clay-shared/src/Portal.tsx

	return render(

		// @ts-ignore

		DetachedItemSelectorModal,
		props,
		document.createElement('div')
	);
}
