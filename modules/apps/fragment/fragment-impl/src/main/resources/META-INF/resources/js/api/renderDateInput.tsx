/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import React from 'react';

import {DateInput} from './DateInput';

type Props = React.ComponentProps<typeof DateInput>;

export function renderDateInput(container: HTMLElement, props: Props): void {
	render(
		DateInput,
		{componentId: `${props.namespace}-date-input`, ...props},
		container
	);
}
