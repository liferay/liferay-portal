/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classnames from 'classnames';
import React from 'react';

import {PortletDataHandlerChoice} from '../../../types/portletDataHandler';
import FieldSelectWithOption from '../FieldSelectWithOption';

interface PortletDataControlChoiceProps {
	className?: string;
	onChange: (value: string) => void;
	portletDataHandlerChoice: PortletDataHandlerChoice;
	value: string;
}

export default function PortletDataControlChoice({
	className = 'ml-1',
	onChange,
	portletDataHandlerChoice,
	value,
}: PortletDataControlChoiceProps) {
	return (
		<FieldSelectWithOption
			className="w-auto"
			formGroupProps={{className: classnames('mb-0', className)}}
			label={portletDataHandlerChoice.label ?? ''}
			name={portletDataHandlerChoice.name}
			onChange={(event) => {
				onChange(event.target.value);
			}}
			options={(portletDataHandlerChoice.choices ?? []).map(
				({label, name}) => ({
					label,
					value: name,
				})
			)}
			value={value}
		/>
	);
}
