/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createContext} from 'react';

export interface InsightsViewContextValue {
	selectInsight: (externalReferenceCode: string) => void;
}

export const InsightsViewContext = createContext<InsightsViewContextValue>({
	selectInsight: () => {},
});
