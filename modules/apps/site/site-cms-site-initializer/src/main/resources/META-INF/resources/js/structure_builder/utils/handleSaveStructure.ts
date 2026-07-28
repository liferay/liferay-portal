/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {Dispatch} from 'react';

import StructureService, {
	StructureServiceError,
} from '../../common/services/StructureService';
import {Action, State} from '../contexts/StateContext';
import selectHistory from '../selectors/selectHistory';
import selectPublishedChildren from '../selectors/selectPublishedChildren';
import selectStructureChildren from '../selectors/selectStructureChildren';
import selectStructureERC from '../selectors/selectStructureERC';
import selectStructureId from '../selectors/selectStructureId';
import selectStructureLabel from '../selectors/selectStructureLabel';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureName from '../selectors/selectStructureName';
import selectStructureSettings from '../selectors/selectStructureSettings';
import selectStructureSlug from '../selectors/selectStructureSlug';
import selectStructureSpaces from '../selectors/selectStructureSpaces';
import selectStructureStatus from '../selectors/selectStructureStatus';
import selectStructureUuid from '../selectors/selectStructureUuid';
import selectStructureWorkflows from '../selectors/selectStructureWorkflows';
import buildStructureErrorAction from './buildStructureErrorAction';

type Props = {
	dispatch: Dispatch<Action>;
	state: State;
	validate: () => boolean;
};

export default async function handleSaveStructure({
	dispatch,
	state,
	validate,
}: Props) {
	const valid = validate();

	if (!valid) {
		return;
	}

	const children = selectStructureChildren(state);
	const erc = selectStructureERC(state);
	const slug = selectStructureSlug(state);
	const history = selectHistory(state);
	const id = selectStructureId(state);
	const label = selectStructureLabel(state);
	const localizedLabel = selectStructureLocalizedLabel(state);
	const name = selectStructureName(state);
	const publishedChildren = selectPublishedChildren(state);
	const settings = selectStructureSettings(state);
	const spaces = selectStructureSpaces(state);
	const status = selectStructureStatus(state);
	const workflows = selectStructureWorkflows(state);
	const uuid = selectStructureUuid(state);

	const previousStatus = state.structure.status;

	const onError = (error: StructureServiceError) =>
		dispatch(buildStructureErrorAction({error, previousStatus, uuid}));

	dispatch({status: 'saving', type: 'set-structure-status'});

	if (status === 'new') {
		const {data, error} = await StructureService.createStructure({
			children,
			erc,
			label,
			name,
			publishedChildren,
			settings,
			slug,
			spaces,
			status: 'draft',
			workflows,
		});

		if (error) {
			onError(error);

			return;
		}
		else if (data) {
			dispatch({id: data.id, type: 'create-structure'});
		}
	}
	else {
		const {error} = await StructureService.updateStructure({
			children,
			erc,
			history,
			id,
			label,
			name,
			publishedChildren,
			settings,
			slug,
			spaces,
			status: 'draft',
			workflows,
		});

		if (error) {
			onError(error);

			return;
		}
		else {
			dispatch({status: 'draft', type: 'set-structure-status'});
			dispatch({type: 'clear-errors'});
		}
	}

	openToast({
		message: Liferay.Util.sub(
			Liferay.Language.get('x-was-saved-successfully'),
			localizedLabel
		),
		type: 'success',
	});
}
