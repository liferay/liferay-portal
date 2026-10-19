/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from './Alert';
import {ClayToaster} from './Toaster';
import {createToastStore} from './createToastStore';
import {usePrefersPersistentAlerts} from './usePrefersPersistentAlerts';

import type {DisplayType, IClayAlertProps} from './Alert';
import type {
	ToastItem,
	ToastStore,
	ToastStoreListener,
} from './createToastStore';

export {ClayToaster, createToastStore, usePrefersPersistentAlerts};
export type {
	DisplayType,
	IClayAlertProps,
	ToastItem,
	ToastStore,
	ToastStoreListener,
};
export default ClayAlert;
