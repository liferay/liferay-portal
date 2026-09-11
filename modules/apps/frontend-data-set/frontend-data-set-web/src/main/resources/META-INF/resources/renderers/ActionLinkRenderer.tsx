/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import classNames from 'classnames';
import {openConfirmModal} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {useContext} from 'react';

import FrontendDataSetContext from '../FrontendDataSetContext';
import filterItemActions from '../utils/actionItems/filterItemActions';
import findAction from '../utils/actionItems/findAction';
import formatActionURL from '../utils/actionItems/formatActionURL';
import {openPermissionsModal} from '../utils/modals/openPermissionsModal';
import {EItemActionsType, IItemsActions} from '../utils/types';
import {useRecordVisit} from '../utils/useRecordVisit';
import DefaultContent from './DefaultRenderer';

interface IActionLinkRendererProps {
	actions: IItemsActions[];
	itemData: any;
	itemId: number | string;
	options?: {
		actionId?: string;
		decoration?: React.ComponentProps<typeof ClayLink>['decoration'];
		displayType?: React.ComponentProps<typeof ClayLink>['displayType'];
	};
	value: number | string | null | undefined;
}

function hasValue(value: any): boolean {
	if (value === null || value === undefined) {
		return false;
	}

	return typeof value === 'string' ? value.trim() !== '' : true;
}

function ActionLinkRenderer({
	actions,
	itemData,
	itemId,
	options,
	value,
}: IActionLinkRendererProps) {
	const {
		executeAsyncItemAction,
		highlightItems,
		infoPanelOpen,
		onInfoPanelToggleButtonClick,
		openModal,
		openSidePanel,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
	} = useContext(FrontendDataSetContext);

	const recordVisit = useRecordVisit();

	if (!actions || !actions.length) {
		return hasValue(value) ? <DefaultContent value={value} /> : null;
	}

	const formattedActions = filterItemActions({
		actions,
		infoPanelOpen,
		itemData,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
	});

	const currentAction = options?.actionId
		? findAction(formattedActions, options.actionId)
		: formattedActions[0]?.type === EItemActionsType.GROUP
			? formattedActions[0]?.items?.[0]
			: formattedActions[0];

	if (!currentAction) {
		return hasValue(value) ? <DefaultContent value={value} /> : null;
	}

	const formattedHref =
		currentAction.href &&
		formatActionURL(currentAction.href, itemData, currentAction.target);

	const showValue = hasValue(value);

	function handleClickOnLink(event: React.MouseEvent) {
		const doAction = () => {
			if (currentAction?.target === 'modal') {
				event.preventDefault();

				openModal({
					size: currentAction?.data?.size || 'lg',
					title: currentAction?.data?.title,
					url: formattedHref,
				});
			}
			else if (currentAction?.target === 'infoPanel') {
				event.preventDefault();

				onInfoPanelToggleButtonClick();
			}
			else if (currentAction?.target === 'modal-permissions') {
				event.preventDefault();

				if (formattedHref) {
					openPermissionsModal(formattedHref);
				}
			}
			else if (currentAction?.target === 'sidePanel') {
				event.preventDefault();

				highlightItems([itemId]);

				openSidePanel({
					size: currentAction?.data?.size || 'lg',
					title: currentAction?.data?.title,
					url: formattedHref,
				});
			}
			else if (
				currentAction?.target === 'async' ||
				currentAction?.target === 'headless'
			) {
				event.preventDefault();

				if (formattedHref) {
					executeAsyncItemAction({
						errorMessage: currentAction?.data?.errorMessage || '',
						method: currentAction?.method,
						url: formattedHref,
					});
				}
			}
			else if (currentAction?.onClick) {
				event.preventDefault();

				currentAction?.onClick({itemData});
			}

			if (!event.defaultPrevented) {
				recordItemVisit();
			}
		};

		if (currentAction?.data?.confirmationMessage) {
			openConfirmModal({
				message: currentAction.data.confirmationMessage || '',
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

	function recordItemVisit() {
		recordVisit(itemData, {
			href: formattedHref,
			label: showValue
				? String(value)
				: currentAction?.accessibleName || currentAction?.label,
		});
	}

	function isNotALink() {
		return Boolean(
			(currentAction?.target && currentAction?.target !== 'link') ||
				currentAction?.onClick
		);
	}

	return (
		<div
			className={classNames({'table-list-title': !options?.displayType})}
		>
			<ClayLink
				aria-label={
					showValue
						? undefined
						: currentAction.accessibleName || currentAction.label
				}
				data-senna-off
				decoration={options?.decoration}
				displayType={options?.displayType}
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
											if (formattedHref && isConfirmed) {
												recordItemVisit();

												navigate(formattedHref);
											}
										},
									});
								}
								else {
									recordItemVisit();

									event.stopPropagation();
								}
							}
				}
			>
				{showValue
					? value
					: currentAction.icon && (
							<ClayIcon symbol={currentAction.icon} />
						)}
			</ClayLink>
		</div>
	);
}

export default ActionLinkRenderer;
