/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {openConfirmModal} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {addParams, navigate, sub} from 'frontend-js-web';
import React, {Dispatch} from 'react';

import SpaceService from '../../common/services/SpaceService';
import StructureService, {
	StructureServiceError,
} from '../../common/services/StructureService';
import {ObjectDefinitions} from '../../common/types/ObjectDefinition';
import {Space} from '../../common/types/Space';
import {config} from '../config';
import {CacheKey} from '../contexts/CacheContext';
import {Action, State} from '../contexts/StateContext';
import selectHistory from '../selectors/selectHistory';
import selectPublishedChildren from '../selectors/selectPublishedChildren';
import selectStructureChildren from '../selectors/selectStructureChildren';
import selectStructureERC from '../selectors/selectStructureERC';
import selectStructureId from '../selectors/selectStructureId';
import selectStructureLabel from '../selectors/selectStructureLabel';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureName from '../selectors/selectStructureName';
import selectStructurePath from '../selectors/selectStructurePath';
import selectStructureSettings from '../selectors/selectStructureSettings';
import selectStructureSlug from '../selectors/selectStructureSlug';
import selectStructureSpaces from '../selectors/selectStructureSpaces';
import selectStructureStatus from '../selectors/selectStructureStatus';
import selectStructureUuid from '../selectors/selectStructureUuid';
import selectStructureWorkflows from '../selectors/selectStructureWorkflows';
import DisplayPageService from '../services/DisplayPageService';
import buildStructureErrorAction from './buildStructureErrorAction';

type Props = {
	dispatch: Dispatch<Action>;
	objectDefinitions: ObjectDefinitions;
	showExperienceLink: boolean;
	showWarnings?: boolean;
	spaces: Space[];
	staleCache: (key: CacheKey) => void;
	state: State;
	validate: () => boolean;
};

export default async function handlePublishStructure({
	dispatch,
	objectDefinitions,
	showExperienceLink,
	showWarnings = true,
	spaces: allSpaces,
	staleCache,
	state,
	validate,
}: Props) {
	const valid = validate();

	if (!valid) {
		return;
	}

	const history = selectHistory(state);

	if (showWarnings) {
		if (
			config.isReferenced &&
			!history.deletedChildren.length &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish-and-propagate'),
				center: true,
				status: 'warning',
				text: Liferay.Language.get(
					'this-content-structure-is-being-used-in-other-existing-content-structures'
				),
				title: Liferay.Language.get(
					'publish-content-structure-changes'
				),
			}))
		) {
			return;
		}

		if (
			!config.isReferenced &&
			history.deletedChildren.length &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish'),
				center: true,
				status: 'danger',
				text: Liferay.Language.get(
					'you-have-made-changes-to-the-content-structure-that-may-impact-existing-stored-data-once-published'
				),
				title: Liferay.Language.get(
					'publish-content-structure-changes'
				),
			}))
		) {
			return;
		}

		if (
			config.isReferenced &&
			history.deletedChildren.length &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish-and-propagate'),
				center: true,
				status: 'danger',
				text: Liferay.Language.get(
					'you-have-made-changes-to-the-content-structure-that-may-impact-existing-stored-data-once-published'
				),
				title: Liferay.Language.get(
					'publish-content-structure-changes'
				),
			}))
		) {
			return;
		}
	}

	const children = selectStructureChildren(state);
	const erc = selectStructureERC(state);
	const slug = selectStructureSlug(state);
	const id = selectStructureId(state);
	const label = selectStructureLabel(state);
	const localizedLabel = selectStructureLocalizedLabel(state);
	const name = selectStructureName(state);
	const path = selectStructurePath(state);
	const publishedChildren = selectPublishedChildren(state);
	const settings = selectStructureSettings(state);
	const structureSpaces = selectStructureSpaces(state);
	const status = selectStructureStatus(state);
	let structureId = selectStructureId(state);
	const workflows = selectStructureWorkflows(state);
	const uuid = selectStructureUuid(state);

	const objectDefinition = objectDefinitions[erc];

	const spaces = structureSpaces === 'all' ? 'all' : [...structureSpaces];

	if (spaces !== 'all') {
		const removedSpaces = getRemovedSpaces(
			objectDefinition,
			structureSpaces,
			allSpaces
		);

		const restoredSpaceNames = [];

		for (const removedSpace of removedSpaces) {
			const {data, error} = await SpaceService.getSpaceContents({
				path,
				siteId: removedSpace.siteId,
			});

			if (!data || error) {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});

				return;
			}

			const contents = data.totalCount;

			if (contents > 0) {
				spaces.push(removedSpace.externalReferenceCode);

				restoredSpaceNames.push(removedSpace.name);
			}
		}

		if (restoredSpaceNames.length) {
			const text =
				restoredSpaceNames.length === 1
					? sub(
							Liferay.Language.get(
								'the-space-x-cannot-be-removed-because-it-has-content-created-from-this-structure.-when-publishing-this-space-will-still-be-available-for-this-structure'
							),
							restoredSpaceNames[0]
						)
					: sub(
							Liferay.Language.get(
								'the-spaces-x-cannot-be-removed-because-they-have-content-created-from-this-structure'
							),
							restoredSpaceNames.join(', ')
						);

			const confirm = await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish'),
				center: true,
				status: 'warning',
				text,
				title: Liferay.Language.get('space-availability'),
			});

			if (!confirm) {
				return;
			}

			dispatch({
				spaces,
				type: 'update-structure',
			});
		}
	}

	const onSuccess = async () => {
		staleCache('object-definitions');

		if (!showExperienceLink) {
			openToast({
				message: Liferay.Util.sub(
					Liferay.Language.get('x-was-published-successfully'),
					localizedLabel
				),
				type: 'success',
			});

			return;
		}

		openToast({
			message: Liferay.Util.sub(
				Liferay.Language.get(
					'x-was-published-successfully.-remember-to-review-the-customized-editor-if-needed'
				),
				localizedLabel
			),
			toastProps: {
				actions: (
					<ClayButton
						displayType="success"
						onClick={() => {
							const editStructureDisplayPageURL = addParams(
								{
									backURL: addParams(
										{
											objectDefinitionId: structureId,
										},
										config.structureBuilderURL
									),
									objectDefinitionId: structureId,
								},
								config.editStructureDisplayPageURL
							);

							navigate(editStructureDisplayPageURL);
						}}
						size="sm"
					>
						{Liferay.Language.get('customize-editor')}

						<ClayIcon className="ml-2" symbol="shortcut" />
					</ClayButton>
				),
			},
		});
	};

	const previousStatus = state.structure.status;

	const onError = (error: StructureServiceError) =>
		dispatch(buildStructureErrorAction({error, previousStatus, uuid}));

	dispatch({status: 'publishing', type: 'set-structure-status'});

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
			status: 'published',
			workflows,
		});

		if (error) {
			onError(error);

			return;
		}
		else if (data) {
			structureId = data.id;

			dispatch({id: data.id, type: 'publish-structure'});
		}
	}
	else if (status === 'draft' || status === 'published') {
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
			status: 'published',
			workflows,
		});

		if (error) {
			onError(error);

			return;
		}
		else {
			dispatch({type: 'publish-structure'});
		}
	}

	if (status === 'published') {
		if (config.autogeneratedDisplayPage) {
			await DisplayPageService.resetDisplayPage({id: structureId});
		}

		await DisplayPageService.resetTranslationDisplayPage({id: structureId});
	}

	onSuccess();
}

function getRemovedSpaces(
	objectDefinition: ObjectDefinitions[string] | undefined,
	spaces: State['structure']['spaces'],
	allSpaces: Space[]
): Space[] {
	if (!objectDefinition) {
		return [];
	}

	const settings = objectDefinition.objectDefinitionSettings || [];

	const acceptAllGroups = settings.find(
		({name}) => name === 'acceptAllGroups'
	)?.value;

	if (acceptAllGroups === 'true') {
		return [];
	}

	const acceptedGroupExternalReferenceCodes = settings.find(
		({name}) => name === 'acceptedGroupExternalReferenceCodes'
	)?.value;

	const previousSpaces =
		acceptedGroupExternalReferenceCodes?.split(',').filter(Boolean) || [];

	if (spaces === 'all') {
		return [];
	}

	const currentSpaces = new Set(spaces);

	return previousSpaces
		.filter((erc) => !currentSpaces.has(erc))
		.map((erc) =>
			allSpaces.find(
				({externalReferenceCode}) => externalReferenceCode === erc
			)
		)
		.filter((space): space is Space => Boolean(space));
}
