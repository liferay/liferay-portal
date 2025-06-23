/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {navigate} from 'frontend-js-web';

import {IFrontendDataSetContext} from '../../FrontendDataSetContext';
import {OPEN_MODAL, OPEN_SIDE_PANEL} from '../../utils/eventsDefinitions';
import {resolveModalSize} from '../../utils/modals/resolveModalSize';
import {ICreationActionItem} from '../types';
import {ACTION_ITEM_TARGETS} from './constants';
import formatActionURL from './formatActionURL';

const {
	BLANK,
	EVENT,
	MODAL,
	MODAL_FULL_SCREEN,
	MODAL_LARGE,
	MODAL_SMALL,
	SIDE_PANEL,
} = ACTION_ITEM_TARGETS;

export function triggerAction(
	item: ICreationActionItem,
	context: IFrontendDataSetContext
) {
	const {href: actionTargetURL, target: actionTarget} = item;
	const {loadData, modalId, sidePanelId} = context;

	switch (actionTarget) {
		case BLANK:
			window.open(actionTargetURL);
			break;
		case MODAL:
		case MODAL_FULL_SCREEN:
		case MODAL_LARGE:
		case MODAL_SMALL:
			Liferay.fire(OPEN_MODAL, {
				disableHeader: item.data?.disableHeader,
				id: modalId,
				onClose: loadData,
				size: item.data?.size || resolveModalSize(actionTarget),
				title: item.data?.title,
				url: actionTargetURL,
			});
			break;
		case SIDE_PANEL:
			Liferay.fire(OPEN_SIDE_PANEL, {
				disableHeader: item.data?.disableHeader,
				id: sidePanelId,
				onAfterSubmit: loadData,
				title: item.data?.title,
				url: actionTargetURL,
			});
			break;
		case EVENT:
			actionTargetURL && Liferay.fire(actionTargetURL);
			break;
		default:
			navigate(formatActionURL(actionTargetURL, item, actionTarget));
			break;
	}
}
