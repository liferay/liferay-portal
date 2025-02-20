/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openConfirmModal,
	openModal,
	openToast,
} from 'frontend-js-components-web';
import {fetch, objectToFormData, runScriptsInElement} from 'frontend-js-web';

const MAP_CMD_REVISION = {
	redo: 'redo_layout_revision',
	undo: 'undo_layout_revision',
};

const MAP_TEXT_REVISION = {
	redo: Liferay.Language.get(
		'are-you-sure-you-want-to-redo-your-last-changes'
	),
	undo: Liferay.Language.get(
		'are-you-sure-you-want-to-undo-your-last-changes'
	),
};

const updateRevision = (cmd, layoutRevisionId, layoutSetBranchId) => {
	const updateLayoutData = {
		cmd,
		layoutRevisionId,
		layoutSetBranchId,
		p_auth: Liferay.authToken,
		p_l_id: themeDisplay.getPlid(),
		p_v_l_s_g_id: themeDisplay.getSiteGroupId(),
	};

	fetch(themeDisplay.getPathMain() + '/portal/update_layout', {
		body: objectToFormData(updateLayoutData),
		method: 'POST',
	})
		.then(() => {
			window.location.reload();
		})
		.catch(() => {
			openToast({
				message: Liferay.Language.get(
					'there-was-an-unexpected-error.-please-refresh-the-current-page'
				),
				toastProps: {
					autoClose: 10000,
				},
				type: 'warning',
			});
		});
};

const ACTIONS = {
	redo: ({layoutRevisionId, layoutSetBranchId}) => {
		const cmd = MAP_CMD_REVISION['redo'];
		const confirmText = MAP_TEXT_REVISION['redo'];

		openConfirmModal({
			message: confirmText,
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					updateRevision(cmd, layoutRevisionId, layoutSetBranchId);
				}
			},
		});
	},

	undo: ({layoutRevisionId, layoutSetBranchId}) => {
		const cmd = MAP_CMD_REVISION['undo'];
		const confirmText = MAP_TEXT_REVISION['undo'];

		openConfirmModal({
			message: confirmText,
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					updateRevision(cmd, layoutRevisionId, layoutSetBranchId);
				}
			},
		});
	},

	viewHistory: ({viewHistoryURL}) => {
		openModal({
			iframeBodyCssClass: '',
			onClose: () => {
				window.location.reload();
			},
			title: Liferay.Language.get('history'),
			url: viewHistoryURL,
		});
	},

	viewLayoutBranches: ({viewLayoutBranchesURL}) => {
		openModal({
			id: 'layoutBranchesDialog',
			onClose: () => {
				window.location.reload();
			},
			title: Liferay.Language.get('page-variations'),
			url: viewLayoutBranchesURL,
		});
	},

	viewLayoutSetBranches: ({viewLayoutSetBranchesURL}) => {
		openModal({
			id: 'layoutSetBranchesDialog',
			onClose: () => {
				window.location.reload();
			},
			title: Liferay.Language.get('site-pages-variation'),
			url: viewLayoutSetBranchesURL,
		});
	},
};

export default function propsTransformer({
	actions,
	additionalProps,
	items,
	portletNamespace,
	...props
}) {
	const dropdownClass = `${portletNamespace}staging-version-options`;

	const layoutRevisionDetails = document.getElementById(
		`${portletNamespace}layoutRevisionDetails`
	);

	if (layoutRevisionDetails) {
		Liferay.after('updatedLayout', () => {
			fetch(additionalProps.markAsReadyForPublicationURL)
				.then((response) => response.text())
				.then((response) => {
					layoutRevisionDetails.innerHTML = response;
					runScriptsInElement(layoutRevisionDetails);

					Liferay.fire('updatedStatus');
				})
				.catch(() => {
					layoutRevisionDetails.innerHTML = Liferay.Language.get(
						'there-was-an-unexpected-error.-please-refresh-the-current-page'
					);
				});
		});
	}

	const layoutRevisionStatus = document.getElementById(
		`${portletNamespace}layoutRevisionStatus`
	);

	if (layoutRevisionStatus) {
		Liferay.after('updatedStatus', () => {
			fetch(additionalProps.layoutRevisionStatusURL)
				.then((response) => response.text())
				.then((response) => {
					layoutRevisionStatus.innerHTML = response;
					runScriptsInElement(layoutRevisionStatus);
				})
				.catch(() => {
					layoutRevisionStatus.innerHTML = Liferay.Language.get(
						'there-was-an-unexpected-error.-please-refresh-the-current-page'
					);
				});
		});
	}

	const handleDropdownMenuOpen = (event) => {
		const portlet = event.target.closest('.portlet');

		if (portlet) {
			portlet.classList.add('focus');
		}

		const listener = (event) => {
			if (!event.target.closest(`.${dropdownClass}`)) {
				if (portlet) {
					portlet.classList.remove('focus');
				}

				document.removeEventListener('mousedown', listener);
				document.removeEventListener('touchstart', listener);
			}
		};

		document.addEventListener('mousedown', listener);
		document.addEventListener('touchstart', listener);
	};

	const updateItem = (item) => {
		const newItem = {
			...item,
			onClick(event) {
				const action = item.data?.action;

				if (action) {
					event.preventDefault();

					ACTIONS[action]?.(item.data);
				}
			},
		};

		if (Array.isArray(item.items)) {
			newItem.items = item.items.map(updateItem);
		}

		return newItem;
	};

	return {
		...props,
		actions: actions?.map(updateItem),
		items: items?.map(updateItem),
		menuProps: {
			className: dropdownClass,
		},
		onClick: (event) => {
			handleDropdownMenuOpen(event);
		},
		onKeyDown: (event) => {
			if (event.key === 'Enter' || event.key === 'ArrowDown') {
				handleDropdownMenuOpen(event);
			}
		},
	};
}
