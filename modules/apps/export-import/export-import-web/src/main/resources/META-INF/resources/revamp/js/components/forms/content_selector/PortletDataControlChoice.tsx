/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classnames from 'classnames';
import React from 'react';

import {PreviewPortletDataHandlerChoice} from '../../../types/portletDataHandler';
import FieldSelectWithOption from '../FieldSelectWithOption';

interface PortletDataControlChoiceProps {
	className?: string;
	onChange: (value: string) => void;
	previewPortletDataHandlerChoice: PreviewPortletDataHandlerChoice;
	value: string;
}

export default function PortletDataControlChoice({
	className = 'ml-1',
	onChange,
	previewPortletDataHandlerChoice,
	value,
}: PortletDataControlChoiceProps) {
	return (
		<FieldSelectWithOption
			className="w-auto"
			formGroupProps={{className: classnames('mb-0', className)}}
			label={previewPortletDataHandlerChoice.label}
			name={previewPortletDataHandlerChoice.name}
			onChange={(event) => {
				onChange(event.target.value);
			}}
			options={previewPortletDataHandlerChoice.choices.map(
				({label, name}) => ({
					label,
					value: name,
				})
			)}
			value={value}
		/>
	);
}
