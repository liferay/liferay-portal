/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {openConfirmModal} from '@liferay/layout-js-components-web';
import {addParams, navigate} from 'frontend-js-web';
import React, {useEffect} from 'react';

import Toolbar from '../../common/components/Toolbar';
import {config} from '../config';
import {useCache, useStaleCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectHistory from '../selectors/selectHistory';
import selectOperation from '../selectors/selectOperation';
import selectState from '../selectors/selectState';
import selectStructureId from '../selectors/selectStructureId';
import selectStructureLocalizedLabel from '../selectors/selectStructureLocalizedLabel';
import selectStructureStatus from '../selectors/selectStructureStatus';
import selectUnsavedChanges from '../selectors/selectUnsavedChanges';
import handlePublishStructure from '../utils/handlePublishStructure';
import handleSaveStructure from '../utils/handleSaveStructure';
import {useValidate} from '../utils/validation';
import AsyncButton from './AsyncButton';

export default function StructureBuilderToolbar() {
	const label = useSelector(selectStructureLocalizedLabel);
	const status = useSelector(selectStructureStatus);

	const dispatch = useStateDispatch();

	const {load, status: objectDefinitionStatus} =
		useCache('object-definitions');

	useEffect(() => {
		if (objectDefinitionStatus === 'stale') {
			load().then((objectDefinitions) =>
				dispatch({
					objectDefinitions,
					type: 'refresh-referenced-structures',
				})
			);
		}
	}, [dispatch, load, objectDefinitionStatus]);

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
				<CustomizeEditorButton />
			</Toolbar.Item>

			<Toolbar.Item className="d-none d-sm-flex">
				<ClayLink
					className="btn btn-outline-borderless btn-outline-secondary btn-sm"
					data-canonical-name={Liferay.Language.get('cancel')}
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

function CustomizeEditorButton() {
	const dispatch = useStateDispatch();
	const validate = useValidate();

	const history = useSelector(selectHistory);
	const state = useSelector(selectState);
	const status = useSelector(selectStructureStatus);
	const structureId = useSelector(selectStructureId);
	const unsavedChanges = useSelector(selectUnsavedChanges);

	const {data: objectDefinitions} = useCache('object-definitions');
	const {data: spaces} = useCache('spaces');

	const staleCache = useStaleCache();

	return (
		<ClayButton
			aria-label={`${Liferay.Language.get('customize-editor')} ${Liferay.Language.get('opens-new-window')}`}
			borderless
			className="font-weight-semi-bold mr-md-2"
			data-canonical-name={Liferay.Language.get('customize-editor')}
			displayType="primary"
			onClick={() => {
				if (
					status === 'published' &&
					!!history.deletedChildren.length
				) {
					openConfirmModal({
						buttonLabel: Liferay.Language.get('publish'),
						center: true,
						onConfirm: async () => {
							await handlePublishStructure({
								dispatch,
								objectDefinitions,
								showExperienceLink: true,
								showWarnings: false,
								spaces,
								staleCache,
								state,
								validate,
							});
						},
						status: 'danger',
						text: Liferay.Language.get(
							'to-customize-the-editor-you-need-to-publish-the-content-structure-first.-you-have-made-changes-to-the-content-structure-that-may-impact-existing-stored-data-once-published'
						),
						title: Liferay.Language.get(
							'publish-to-customize-editor'
						),
					});
				}
				else if (status !== 'published' || unsavedChanges) {
					openConfirmModal({
						buttonLabel: Liferay.Language.get('publish'),
						center: true,
						onConfirm: async () => {
							await handlePublishStructure({
								dispatch,
								objectDefinitions,
								showExperienceLink: true,
								spaces,
								staleCache,
								state,
								validate,
							});
						},
						status: 'warning',
						text: Liferay.Language.get(
							'to-customize-the-editor-you-need-to-publish-the-content-structure-first'
						),
						title: Liferay.Language.get(
							'publish-to-customize-editor'
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
			<span className="d-md-inline d-none">
				{Liferay.Language.get('customize-editor')}

				<ClayIcon className="ml-2" symbol="shortcut" />
			</span>

			<ClayIcon
				className="d-md-none lfr-tooltip-scope"
				data-title={`${Liferay.Language.get('customize-editor')} ${Liferay.Language.get('opens-new-window')}`}
				symbol="edit-layout"
			/>
		</ClayButton>
	);
}

function SaveButton() {
	const dispatch = useStateDispatch();
	const validate = useValidate();

	const operation = useSelector(selectOperation);
	const state = useSelector(selectState);

	const onSave = async () => {
		await handleSaveStructure({
			dispatch,
			state,
			validate,
		});
	};

	return (
		<>
			<AsyncButton
				className="d-md-flex d-none"
				displayType="secondary"
				label={Liferay.Language.get('save')}
				onClick={onSave}
				status={operation === 'saving' ? 'loading' : 'idle'}
			/>

			<ClayButtonWithIcon
				className="d-md-none"
				data-canonical-name={Liferay.Language.get('save-mobile')}
				displayType="secondary"
				onClick={onSave}
				size="sm"
				symbol="disk"
				title={Liferay.Language.get('save')}
			/>
		</>
	);
}

function PublishButton() {
	const dispatch = useStateDispatch();
	const validate = useValidate();

	const operation = useSelector(selectOperation);
	const state = useSelector(selectState);

	const {data: objectDefinitions} = useCache('object-definitions');
	const {data: spaces} = useCache('spaces');

	const staleCache = useStaleCache();

	const onPublish = async () => {
		await handlePublishStructure({
			dispatch,
			objectDefinitions,
			showExperienceLink: !config.autogeneratedDisplayPage,
			spaces,
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
			status={operation === 'publishing' ? 'loading' : 'idle'}
		/>
	);
}
