/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DateTimeRenderer} from '@liferay/frontend-data-set-web';
import React from 'react';

import {NO_VALUE} from '../../../common/utils/constants';

const DueDateRenderer = ({
	options,
	value,
}: {
	options?: Parameters<typeof DateTimeRenderer>[0]['options'];
	value?: string;
}) => {
	if (!value) {
		return <span className="text-secondary">{NO_VALUE}</span>;
	}

	return DateTimeRenderer({options, value});
};

export default DueDateRenderer;
