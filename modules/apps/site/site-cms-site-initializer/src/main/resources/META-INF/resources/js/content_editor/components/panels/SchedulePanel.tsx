/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {datetimeUtils} from '@liferay/object-js-components-web';
import React from 'react';

import {ScheduleFields, UpdateScheduleProps} from '../ContentEditorSidePanel';
import ScheduleField from '../ScheduleField';

const LABELS = {
	expirationDate: Liferay.Language.get('expiration-date'),
	reviewDate: Liferay.Language.get('review-date'),
};

export default function SchedulePanel({
	dateConfig,
	onUpdateSchedule,
	scheduleFields: fields,
}: {
	dateConfig: datetimeUtils.DateConfig;
	onUpdateSchedule: (props: UpdateScheduleProps) => void;
	scheduleFields: ScheduleFields;
}) {
	return (
		<div className="px-3">
			<p className="text-3 text-secondary">
				{Liferay.Language.get(
					'including-an-expiration-date-will-allow-your-files-to-expire-automatically-and-become-unpublished'
				)}
			</p>

			{Object.entries(fields).map(([name, values]) => {
				const label = LABELS[name as keyof typeof LABELS];

				return (
					<ScheduleField
						date={values.value}
						dateConfig={dateConfig}
						error={values.error}
						key={name}
						label={label}
						name={name}
						neverExpire={!values.serverValue}
						updateFieldData={onUpdateSchedule}
					/>
				);
			})}
		</div>
	);
}
