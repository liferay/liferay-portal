/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {escapeHTML, navigate} from 'frontend-js-web';

import LaunchService from '../services/LaunchService';
import {openErrorToast, openSuccessToast} from '../util/toasts';

interface Props {
	loadData?: () => void;
	unscheduleLaunchURL: string;
}

export default function confirmAndUnscheduleLaunchAction({
	loadData,
	unscheduleLaunchURL,
}: Props) {
	openModal({
		bodyHTML: escapeHTML(
			Liferay.Language.get(
				'unscheduling-the-launch-will-delete-the-scheduled-job-and-the-launch-will-be-moved-to-in-progress.-do-you-want-to-proceed'
			)
		),
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				onClick: ({processClose}: {processClose: () => void}) =>
					processClose(),
			},
			{
				displayType: 'primary',
				label: Liferay.Language.get('confirm'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					processClose();

					const {data, error} =
						await LaunchService.unscheduleLaunch(
							unscheduleLaunchURL
						);

					if (error) {
						openErrorToast(error);

						return;
					}

					if (!data) {
						return;
					}

					if (loadData) {
						loadData();

						openSuccessToast(
							Liferay.Language.get(
								'your-request-completed-successfully'
							)
						);
					}
					else if (data.redirect) {
						navigate(data.redirect);
					}
					else {
						window.location.reload();
					}
				},
			},
		],
		role: 'alertdialog',
		title: Liferay.Language.get('unschedule'),
	});
}
