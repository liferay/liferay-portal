/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';

const showToast = ({autoClose, message, title, type}) => {
	openToast({
		autoClose,
		message: message && escapeHTML(message),
		title: title ? escapeHTML(title) : '',
		type,
	});
};

const toaster = {
	danger: (message, title, autoClose = 5000) => {
		showToast({
			autoClose,
			message: message || Liferay.Language.get('error'),
			title: title || `${Liferay.Language.get('error')}:`,
			type: 'danger',
		});
	},
	info: (message, title, autoClose = 5000) => {
		showToast({autoClose, message, title, type: 'info'});
	},
	success: (message, title, autoClose = 5000) => {
		showToast({
			autoClose,
			message: message || Liferay.Language.get('success'),
			title: title || `${Liferay.Language.get('success')}:`,
			type: 'success',
		});
	},
	warning: (message, title, autoClose = 5000) => {
		showToast({autoClose, message, title, type: 'warning'});
	},
};

const useToaster = () => toaster;

export {useToaster};
