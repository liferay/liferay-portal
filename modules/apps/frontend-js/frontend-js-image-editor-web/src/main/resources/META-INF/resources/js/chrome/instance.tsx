/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext, useCallback, useContext} from 'react';

const EditorInstanceContext = createContext('aie-');

let instanceCounter = 0;

export function nextEditorInstancePrefix(): string {
	return `aie${++instanceCounter}-`;
}

export const EditorInstanceProvider = EditorInstanceContext.Provider;

/**
 * The id minter: `eid('crop-angle')` is this instance's id for the
 * angle slider, stable for the component's lifetime.
 */
export function useEditorId(): (name: string) => string {
	const prefix = useContext(EditorInstanceContext);

	return useCallback((name: string) => prefix + name, [prefix]);
}
