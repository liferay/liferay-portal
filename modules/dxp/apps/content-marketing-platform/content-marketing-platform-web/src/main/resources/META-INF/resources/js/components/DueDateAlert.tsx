/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

/**
 * Returns the hidden input of the date fragment whose `inputFieldId` is
 * configured as `ObjectField_dueDate` in
 * content-marketing-platform-site-initializer/src/main/resources/site-initializer/layout-page-templates/display-page-templates/task-edit/page-definition.json
 */
const getDueDateInput = () =>
	document.querySelector<HTMLInputElement>('[name="ObjectField_dueDate"]');

export default function DueDateAlert({
	parentDueDate,
}: {
	parentDueDate?: string;
}) {
	const [dueDate, setDueDate] = useState(() => getDueDateInput()?.value);

	useEffect(() => {
		const dueDateInput = getDueDateInput();

		if (!dueDateInput) {
			return;
		}

		const mutationObserver = new MutationObserver(() =>
			setDueDate(dueDateInput.value)
		);

		mutationObserver.observe(dueDateInput, {
			attributeFilter: ['value'],
			attributes: true,
		});

		return () => mutationObserver.disconnect();
	}, []);

	if (!parentDueDate || !dueDate || dueDate <= parentDueDate) {
		return null;
	}

	return (
		<ClayAlert
			displayType="info"
			title={`${Liferay.Language.get('info')}:`}
			variant="feedback"
		>
			<span className="component-text">
				{sub(
					Liferay.Language.get(
						'this-date-is-after-the-parent-task-due-date-x'
					),
					new Date(`${parentDueDate}T00:00`).toLocaleDateString(
						Liferay.ThemeDisplay.getBCP47LanguageId()
					)
				)}
			</span>
		</ClayAlert>
	);
}
