/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {openConfirmModal} from '@liferay/layout-js-components-web';
import {ManagementToolbar, openToast} from 'frontend-js-components-web';
import {addParams, navigate} from 'frontend-js-web';
import React, {Dispatch} from 'react';

import {config} from '../config';
import {CacheKey, useStaleCache} from '../contexts/CacheContext';
import {
	Action,
	State,
	useSelector,
	useStateDispatch,
} from '../contexts/StateContext';
import selectHistory from '../selectors/selectHistory';
import selectState from '../selectors/selectState';
import selectStructureERC from '../selectors/selectStructureERC';
import selectStructureFields from '../selectors/selectStructureFields';
import selectStructureId from '../selectors/selectStructureId';
import selectStructureLabel from '../selectors/selectStructureLabel';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureName from '../selectors/selectStructureName';
import selectStructureSpaces from '../selectors/selectStructureSpaces';
import selectStructureStatus from '../selectors/selectStructureStatus';
import selectUnsavedChanges from '../selectors/selectUnsavedChanges';
import DisplayPageService from '../services/DisplayPageService';
import StructureService from '../services/StructureService';
import {useValidate} from '../utils/validation';
import AsyncButton from './AsyncButton';
import ManagementBar from './ManagementBar';

export default function StructureBuilderManagementBar() {
	const label = useSelector(selectStructureLocalizedLabel);
	const status = useSelector(selectStructureStatus);

	return (
		<ManagementBar
			backURL="structures"
			title={
				status === 'published'
					? label
					: Liferay.Language.get('new-structure')
			}
		>
			<ManagementToolbar.Item>
				<CustomizeExperienceButton />
			</ManagementToolbar.Item>

			<ManagementToolbar.Item>
				<div className="vertical-divider" />
			</ManagementToolbar.Item>

			<ManagementToolbar.Item>
				<ClayLink
					className="btn btn-outline-borderless btn-outline-secondary btn-sm"
					href="structures"
				>
					{Liferay.Language.get('cancel')}
				</ClayLink>
			</ManagementToolbar.Item>

			{status !== 'published' ? (
				<ManagementToolbar.Item>
					<SaveButton />
				</ManagementToolbar.Item>
			) : null}

			<ManagementToolbar.Item>
				<PublishButton />
			</ManagementToolbar.Item>
		</ManagementBar>
	);
}

function CustomizeExperienceButton() {
	const dispatch = useStateDispatch();
	const validate = useValidate();

	const history = useSelector(selectHistory);
	const state = useSelector(selectState);
	const status = useSelector(selectStructureStatus);
	const structureId = useSelector(selectStructureId);
	const unsavedChanges = useSelector(selectUnsavedChanges);

	const staleCache = useStaleCache();

	return (
		<ClayButton
			borderless
			className="font-weight-semi-bold"
			displayType="primary"
			onClick={() => {
				if (status === 'published' && history.deletedFields) {
					openConfirmModal({
						buttonLabel: Liferay.Language.get('publish'),
						center: true,
						onConfirm: async () => {
							await publishStructure({
								dispatch,
								showExperienceLink: true,
								showWarnings: false,
								staleCache,
								state,
								validate,
							});
						},
						status: 'danger',
						text: Liferay.Language.get(
							'to-customize-the-experience-you-need-to-publish-the-structure-first.-you-removed-one-or-more-fields-from-the-structure'
						),
						title: Liferay.Language.get(
							'publish-to-customize-experience'
						),
					});
				}
				else if (status !== 'published' || unsavedChanges) {
					openConfirmModal({
						buttonLabel: Liferay.Language.get('publish'),
						center: true,
						onConfirm: async () => {
							await publishStructure({
								dispatch,
								showExperienceLink: true,
								staleCache,
								state,
								validate,
							});
						},
						status: 'warning',
						text: Liferay.Language.get(
							'to-customize-the-experience-you-need-to-publish-the-structure-first'
						),
						title: Liferay.Language.get(
							'publish-to-customize-experience'
						),
					});
				}
				else {
					const editStructureDisplayPageURL = addParams(
						{
							backURL: addParams(
								{
									objectDefinitionId: String(structureId),
								},
								config.structureBuilderURL
							),
							objectDefinitionId: String(structureId),
						},
						config.editStructureDisplayPageURL
					);

					navigate(editStructureDisplayPageURL);
				}
			}}
			size="sm"
		>
			{Liferay.Language.get('customize-experience')}

			<ClayIcon className="ml-2" symbol="shortcut" />
		</ClayButton>
	);
}

function SaveButton() {
	const dispatch = useStateDispatch();
	const validate = useValidate();

	const erc = useSelector(selectStructureERC);
	const fields = useSelector(selectStructureFields);
	const label = useSelector(selectStructureLabel);
	const localizedLabel = useSelector(selectStructureLocalizedLabel);
	const name = useSelector(selectStructureName);
	const spaces = useSelector(selectStructureSpaces);
	const status = useSelector(selectStructureStatus);
	const structureId = useSelector(selectStructureId);

	const onError = (error: string) =>
		dispatch({
			error:
				error ||
				Liferay.Language.get(
					'an-unexpected-error-occurred-while-saving-or-publishing-the-structure'
				),
			type: 'set-error',
		});

	const onSave = async () => {
		const valid = validate();

		if (!valid) {
			return;
		}

		if (status === 'new') {
			const {data, error} = await StructureService.createStructure({
				erc,
				fields,
				label,
				name,
				spaces,
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
				erc,
				fields,
				id: structureId,
				label,
				name,
				spaces,
			});

			if (error) {
				onError(error);

				return;
			}
			else {
				dispatch({type: 'clear-error'});
			}
		}

		openToast({
			message: Liferay.Util.sub(
				Liferay.Language.get('x-was-saved-successfully'),
				localizedLabel
			),
			type: 'success',
		});
	};

	return (
		<AsyncButton
			displayType="secondary"
			label={Liferay.Language.get('save')}
			onClick={onSave}
		/>
	);
}

function PublishButton() {
	const dispatch = useStateDispatch();
	const validate = useValidate();
	const state = useSelector(selectState);

	const staleCache = useStaleCache();

	const onPublish = async () => {
		await publishStructure({
			dispatch,
			showExperienceLink: !config.autogeneratedDisplayPage,
			staleCache,
			state,
			validate,
		});
	};

	return (
		<AsyncButton
			displayType="primary"
			label={Liferay.Language.get('publish')}
			onClick={onPublish}
		/>
	);
}

async function publishStructure({
	dispatch,
	showExperienceLink,
	showWarnings = true,
	staleCache,
	state,
	validate,
}: {
	dispatch: Dispatch<Action>;
	showExperienceLink: boolean;
	showWarnings?: boolean;
	staleCache: (key: CacheKey) => void;
	state: State;
	validate: () => boolean;
}) {
	const valid = validate();

	if (!valid) {
		return;
	}

	const history = selectHistory(state);

	if (showWarnings) {
		if (
			config.isReferenced &&
			!history.deletedFields &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish-and-propagate'),
				center: true,
				status: 'warning',
				text: Liferay.Language.get(
					'this-structure-is-being-used-in-other-existing-structures'
				),
				title: Liferay.Language.get('publish-structure-changes'),
			}))
		) {
			return;
		}

		if (
			!config.isReferenced &&
			history.deletedFields &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish'),
				center: true,
				status: 'danger',
				text: Liferay.Language.get(
					'you-removed-one-or-more-fields-from-the-structure'
				),
				title: Liferay.Language.get('publish-structure-changes'),
			}))
		) {
			return;
		}

		if (
			config.isReferenced &&
			history.deletedFields &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish-and-propagate'),
				center: true,
				status: 'danger',
				text: Liferay.Language.get(
					'you-removed-one-or-more-fields-from-the-structure-and-this-structure-is-being-used'
				),
				title: Liferay.Language.get('publish-structure-changes'),
			}))
		) {
			return;
		}
	}

	const erc = selectStructureERC(state);
	const fields = selectStructureFields(state);
	const label = selectStructureLabel(state);

	const localizedLabel = selectStructureLocalizedLabel(state);
	const name = selectStructureName(state);
	const spaces = selectStructureSpaces(state);
	const status = selectStructureStatus(state);
	const structureId = selectStructureId(state);

	let id = structureId;

	const onSuccess = async ({id}: {id: State['id']}) => {
		staleCache('structures');

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
					'x-was-published-successfully.-remember-to-review-the-customized-experience-if-needed'
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
											objectDefinitionId: id,
										},
										config.structureBuilderURL
									),
									objectDefinitionId: String(id),
								},
								config.editStructureDisplayPageURL
							);

							navigate(editStructureDisplayPageURL);
						}}
						size="sm"
					>
						{Liferay.Language.get('customize-experience')}

						<ClayIcon className="ml-2" symbol="shortcut" />
					</ClayButton>
				),
			},
		});
	};

	const onError = (error: string) =>
		dispatch({
			error:
				error ||
				Liferay.Language.get(
					'an-unexpected-error-occurred-while-saving-or-publishing-the-structure'
				),
			type: 'set-error',
		});

	if (status === 'new') {
		const {data, error} = await StructureService.createStructure({
			erc,
			fields,
			label,
			name,
			spaces,
		});

		if (error) {
			onError(error);

			return;
		}
		else if (data && data.id) {
			id = data.id;

			await StructureService.publishStructure({id});

			dispatch({id, type: 'publish-structure'});
		}
	}
	else if (status === 'draft') {
		const {error} = await StructureService.updateStructure({
			erc,
			fields,
			id: structureId,
			label,
			name,
			spaces,
		});

		if (error) {
			onError(error);

			return;
		}
		else {
			await StructureService.publishStructure({id: structureId});

			dispatch({type: 'publish-structure'});
		}
	}
	else if (status === 'published') {
		const {error} = await StructureService.updateStructure({
			erc,
			fields,
			id: structureId,
			label,
			name,
			spaces,
		});

		if (error) {
			onError(error);

			return;
		}
		else {
			dispatch({type: 'publish-structure'});
		}
	}

	if (config.autogeneratedDisplayPage) {
		await DisplayPageService.resetDisplayPage({id});
	}

	onSuccess({id});
}
