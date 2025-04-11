/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AJAX from '../AJAX/index';
import CommerceCookie from '../cookies';
import {createCommerceCart} from '../createCommerceCart';
import * as Events from '../eventsDefinitions';
import DDMFormHandler from '../forms/DDMFormHandler';
import * as FormUtils from '../forms/index';
import * as BaseUtils from '../index';

export default {
	AJAX,
	BaseUtils,
	CommerceCookie,
	Events,
	FormUtils: {
		...FormUtils,
		DDMFormHandler,
	},
	createCommerceCart,
};
