/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import type {IClayAlertProps} from './Alert';

interface IProps {
	children: React.ReactNode;
	className?: string;
	variant?: IClayAlertProps['variant'];
}

export function ConditionalContainer({children, className, variant}: IProps) {
	return variant === 'stripe' ? (
		<div className={classNames('container', className)}>{children}</div>
	) : (
		<>{children}</>
	);
}
