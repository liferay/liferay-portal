/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {openConfirmModal} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../FrontendDataSetContext';
import formatActionURL from '../utils/actionItems/formatActionURL';
import {openPermissionsModal} from '../utils/modals/openPermissionsModal';
import DefaultContent from './DefaultRenderer';

function ActionLinkRenderer({actions, itemData, itemId, options, value}) {
	const {
		executeAsyncItemAction,
		highlightItems,
		onInfoPanelToggleButtonClick,
		openModal,
		openSidePanel,
	} = useContext(FrontendDataSetContext);

	if (!actions || !actions.length) {
		return value ? <DefaultContent value={value} /> : null;
	}

	let currentAction = options?.actionId
		? actions.find((action) => action.data?.id === options.actionId)
		: actions[0];

	if (!currentAction) {
		return null;
	}

	if (currentAction.data?.permissionKey) {
		if (itemData.actions[currentAction.data.permissionKey]) {
			if (currentAction.target === 'headless') {
				currentAction = {
					...currentAction,
					...itemData.actions[currentAction.data.id],
					method: currentAction.method ?? currentAction.data?.method,
				};
			}
		}
		else {
			return value ? <DefaultContent value={value} /> : null;
		}
	}

	const formattedHref =
		currentAction.href &&
		formatActionURL(currentAction.href, itemData, currentAction.target);

	function handleClickOnLink(event) {
		const doAction = () => {
			if (currentAction.target === 'modal') {
				event.preventDefault();

				openModal({
					size: currentAction.size || 'lg',
					title: currentAction.title,
					url: formattedHref,
				});
			}
			if (currentAction.target === 'infoPanel') {
				event.preventDefault();

				onInfoPanelToggleButtonClick();
			}
			else if (currentAction.target === 'modal-permissions') {
				event.preventDefault();

				openPermissionsModal(formattedHref);
			}
			else if (currentAction.target === 'sidePanel') {
				event.preventDefault();

				highlightItems([itemId]);
				openSidePanel({
					size: currentAction.size || 'lg',
					title: currentAction.title,
					url: formattedHref,
				});
			}
			else if (
				currentAction.target === 'async' ||
				currentAction.target === 'headless'
			) {
				event.preventDefault();

				executeAsyncItemAction({
					errorMessage: currentAction.data?.errorMessage,
					method: currentAction.method,
					url: formattedHref,
				});
			}
			else if (currentAction.onClick) {
				event.preventDefault();

				if (typeof currentAction.onClick === 'function') {
					currentAction.onClick({itemData});
				}
				else {
					event.target.setAttribute('onClick', currentAction.onClick);
					event.target.onclick();
					event.target.removeAttribute('onClick');
				}
			}
		};

		if (currentAction?.data?.confirmMessage) {
			openConfirmModal({
				message: currentAction.data.confirmationMessage,
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						doAction();
					}
				},
			});
		}
		else {
			doAction();
		}
	}

	function isNotALink() {
		return Boolean(
			(currentAction.target && currentAction.target !== 'link') ||
				currentAction.onClick
		);
	}

	return (
		<div className="table-list-title">
			<ClayLink
				data-senna-off
				href={formattedHref || '#'}
				onClick={
					isNotALink()
						? handleClickOnLink
						: (event) => {
								const confirmMessage =
									currentAction.data?.confirmationMessage;

								if (confirmMessage) {
									event.preventDefault();

									openConfirmModal({
										message: confirmMessage,
										onConfirm: (isConfirmed) => {
											if (isConfirmed) {
												navigate(formattedHref);
											}
										},
									});
								}
							}
				}
			>
				{value || <ClayIcon symbol={currentAction.icon} />}
			</ClayLink>
		</div>
	);
}

ActionLinkRenderer.propTypes = {
	actions: PropTypes.arrayOf(
		PropTypes.shape({
			data: PropTypes.shape({
				confirmationMessage: PropTypes.string,
				errorMessage: PropTypes.string,
				method: PropTypes.oneOf(['delete', 'get', 'patch', 'post']),
				permissionKey: PropTypes.string,
				successMessage: PropTypes.string,
			}),
			href: PropTypes.string,
			icon: PropTypes.string,
			method: PropTypes.oneOf(['delete', 'get', 'patch', 'post']),
			onClick: PropTypes.oneOfType([PropTypes.func, PropTypes.string]),
			size: PropTypes.string,
			target: PropTypes.oneOf([
				'infoPanel',
				'modal',
				'modal-permissions',
				'sidePanel',
				'link',
				'async',
				'headless',
			]),
			title: PropTypes.string,
		})
	),
	itemData: PropTypes.object,
	itemId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
	options: PropTypes.shape({
		actionId: PropTypes.string,
	}),
	value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
};

export default ActionLinkRenderer;
