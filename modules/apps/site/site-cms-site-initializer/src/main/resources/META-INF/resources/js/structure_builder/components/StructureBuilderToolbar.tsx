/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {openConfirmModal} from '@liferay/layout-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {addParams, navigate} from 'frontend-js-web';
import React, {Dispatch} from 'react';

import Toolbar from '../../common/components/Toolbar';
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
import selectStructureChildren from '../selectors/selectStructureChildren';
import selectStructureERC from '../selectors/selectStructureERC';
import selectStructureId from '../selectors/selectStructureId';
import selectStructureLabel from '../selectors/selectStructureLabel';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureName from '../selectors/selectStructureName';
import selectStructureSpaces from '../selectors/selectStructureSpaces';
import selectStructureStatus from '../selectors/selectStructureStatus';
import selectStructureUuid from '../selectors/selectStructureUuid';
import selectStructureWorkflows from '../selectors/selectStructureWorkflows';
import selectUnsavedChanges from '../selectors/selectUnsavedChanges';
import DisplayPageService from '../services/DisplayPageService';
import StructureService from '../services/StructureService';
import {useValidate} from '../utils/validation';
import AsyncButton from './AsyncButton';

export default function StructureBuilderToolbar() {
	const label = useSelector(selectStructureLocalizedLabel);
	const status = useSelector(selectStructureStatus);

	return (
		<Toolbar
			backURL="structures"
			title={
				status === 'published'
					? label
					: Liferay.Language.get('new-content-structure')
			}
		>
			<Toolbar.Item className="nav-divider-end">
				<CustomizeExperienceButton />
			</Toolbar.Item>

			<Toolbar.Item>
				<ClayLink
					className="btn btn-outline-borderless btn-outline-secondary btn-sm"
					href="structures"
				>
					{Liferay.Language.get('cancel')}
				</ClayLink>
			</Toolbar.Item>

			{status !== 'published' ? (
				<Toolbar.Item>
					<SaveButton />
				</Toolbar.Item>
			) : null}

			<Toolbar.Item>
				<PublishButton />
			</Toolbar.Item>
		</Toolbar>
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
			className="font-weight-semi-bold mr-2"
			displayType="primary"
			onClick={() => {
				if (status === 'published' && history.deletedChildren) {
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
							'to-customize-the-experience-you-need-to-publish-the-content-structure-first.-you-removed-one-or-more-fields-from-the-content-structure'
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
							'to-customize-the-experience-you-need-to-publish-the-content-structure-first'
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
									objectDefinitionId: structureId,
								},
								config.structureBuilderURL
							),
							objectDefinitionId: structureId,
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

	const children = useSelector(selectStructureChildren);
	const erc = useSelector(selectStructureERC);
	const id = useSelector(selectStructureId);
	const label = useSelector(selectStructureLabel);
	const localizedLabel = useSelector(selectStructureLocalizedLabel);
	const name = useSelector(selectStructureName);
	const spaces = useSelector(selectStructureSpaces);
	const status = useSelector(selectStructureStatus);
	const workflows = useSelector(selectStructureWorkflows);
	const uuid = useSelector(selectStructureUuid);

	const onError = () =>
		dispatch({
			error: 'unexpected',
			property: 'global',
			type: 'add-error',
			uuid,
		});

	const onSave = async () => {
		const valid = validate();

		if (!valid) {
			return;
		}

		if (status === 'new') {
			const {data, error} = await StructureService.createStructure({
				children,
				erc,
				label,
				name,
				spaces,
				status: 'draft',
				workflows,
			});

			if (error) {
				onError();

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
				id,
				label,
				name,
				spaces,
				status: 'draft',
				workflows,
			});

			if (error) {
				onError();

				return;
			}
			else {
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
			!history.deletedChildren &&
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
			history.deletedChildren &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish'),
				center: true,
				status: 'danger',
				text: Liferay.Language.get(
					'you-removed-one-or-more-fields-from-the-content-structure'
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
			history.deletedChildren &&
			!(await openConfirmModal({
				buttonLabel: Liferay.Language.get('publish-and-propagate'),
				center: true,
				status: 'danger',
				text: Liferay.Language.get(
					'you-removed-one-or-more-fields-from-the-content-structure-and-this-content-structure-is-being-used'
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
	const id = selectStructureId(state);
	const label = selectStructureLabel(state);

	const localizedLabel = selectStructureLocalizedLabel(state);
	const name = selectStructureName(state);
	const spaces = selectStructureSpaces(state);
	const status = selectStructureStatus(state);
	const structureId = selectStructureId(state);
	const workflows = selectStructureWorkflows(state);
	const uuid = selectStructureUuid(state);

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
						{Liferay.Language.get('customize-experience')}

						<ClayIcon className="ml-2" symbol="shortcut" />
					</ClayButton>
				),
			},
		});
	};

	const onError = () =>
		dispatch({
			error: 'unexpected',
			property: 'global',
			type: 'add-error',
			uuid,
		});

	if (status === 'new') {
		const {data, error} = await StructureService.createStructure({
			children,
			erc,
			label,
			name,
			spaces,
			status: 'published',
			workflows,
		});

		if (error) {
			onError();

			return;
		}
		else if (data) {
			dispatch({id: data.id, type: 'publish-structure'});
		}
	}
	else if (status === 'draft') {
		const {error} = await StructureService.updateStructure({
			children,
			erc,
			id,
			label,
			name,
			spaces,
			status: 'published',
			workflows,
		});

		if (error) {
			onError();

			return;
		}
		else {
			dispatch({type: 'publish-structure'});
		}
	}
	else if (status === 'published') {
		const {error} = await StructureService.updateStructure({
			children,
			erc,
			id,
			label,
			name,
			spaces,
			status: 'published',
			workflows,
		});

		if (error) {
			onError();

			return;
		}
		else {
			dispatch({type: 'publish-structure'});
		}
	}

	if (config.autogeneratedDisplayPage) {
		await DisplayPageService.resetDisplayPage({id: structureId});
	}

	await DisplayPageService.resetTranslationDisplayPage({id: structureId});

	onSuccess();
}
