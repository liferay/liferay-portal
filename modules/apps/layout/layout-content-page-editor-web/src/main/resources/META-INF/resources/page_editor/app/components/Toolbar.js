/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import {ReactPortal, useIsMounted} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {
	FeatureIndicator,
	openConfirmModal,
	openToast,
} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import ExperienceToolbarSection from '../../plugins/experience/components/ExperienceToolbarSection';
import * as Actions from '../actions/index';
import {LAYOUT_TYPES} from '../config/constants/layoutTypes';
import {SERVICE_NETWORK_STATUS_TYPES} from '../config/constants/serviceNetworkStatusTypes';
import {config} from '../config/index';
import {useSelectItem} from '../contexts/ControlsContext';
import {useEditableProcessorUniqueId} from '../contexts/EditableProcessorContext';
import {useDispatch, useSelector} from '../contexts/StoreContext';
import selectCanPublish from '../selectors/selectCanPublish';
import {useDropClear} from '../utils/drag_and_drop/useDragAndDrop';
import DiscardDraftButton from './DiscardDraftButton';
import EditModeSelector from './EditModeSelector';
import ExperimentsLabel from './ExperimentsLabel';
import HideSidebarButton from './HideSidebarButton';
import NetworkStatusBar from './NetworkStatusBar';
import PublishButton from './PublishButton';
import ToggleConfigurationSidebarButton from './ToggleConfigurationSidebarButton';
import ToolbarActionsDropdown from './ToolbarActionsDropdown';
import Translation from './Translation';
import ViewportSizeSelector from './ViewportSizeSelector';
import ZoomAlert from './ZoomAlert';
import Undo from './undo/Undo';

const {useRef} = React;

function ToolbarBody({className}) {
	const discardDraftFormRef = useRef();
	const dispatch = useDispatch();
	const dropClearRef = useDropClear();
	const editableProcessorUniqueId = useEditableProcessorUniqueId();
	const formRef = useRef();
	const selectItem = useSelectItem();
	const store = useSelector((state) => state);

	const canPublish = selectCanPublish(store);

	const [publishPending, setPublishPending] = useState(false);

	const {
		network,
		segmentsExperienceId,
		segmentsExperimentStatus,
		selectedViewportSize,
	} = store;

	const onPublish = () => {
		if (!config.masterUsed) {
			setPublishPending(true);
		}
		else {
			openConfirmModal({
				message: Liferay.Language.get(
					'changes-made-on-this-master-are-going-to-be-propagated-to-all-page-templates,-display-page-templates,-and-pages-using-it.are-you-sure-you-want-to-proceed'
				),
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						setPublishPending(true);
					}
				},
			});
		}
	};

	const deselectItem = (event) => {
		if (event.target === event.currentTarget) {
			selectItem(null);
		}
	};

	const isCMSFreeTier = config.isCMS && config.freeTier;

	let publishButtonLabel = Liferay.Language.get('publish');

	if (config.launchSetId > 0) {
		publishButtonLabel = Liferay.Language.get('mark-as-ready-to-publish');
	}
	else if (config.layoutType === LAYOUT_TYPES.master) {
		publishButtonLabel = Liferay.Language.get('publish-master');
	}
	else if (config.singleSegmentsExperienceMode) {
		publishButtonLabel = Liferay.Language.get('save-variant');
	}
	else if (config.workflowEnabled) {
		publishButtonLabel = Liferay.Language.get('submit-for-workflow');
	}

	const ariaLabel = isCMSFreeTier
		? Liferay.Language.get('publish-locked')
		: publishButtonLabel;

	const icon = isCMSFreeTier ? 'lock' : null;

	useEffect(() => {
		if (
			(network.status === SERVICE_NETWORK_STATUS_TYPES.draftSaved ||
				!network.status) &&
			!editableProcessorUniqueId &&
			publishPending &&
			formRef.current
		) {
			formRef.current.submit();
		}
	}, [publishPending, network, editableProcessorUniqueId]);

	const backURL = new URLSearchParams(window.location.search).get('backURL');

	return (
		<ClayLayout.ContainerFluid
			className={classNames(
				'page-editor__theme-adapter-buttons',
				className
			)}
			onClick={deselectItem}
			ref={dropClearRef}
			size={false}
		>
			<ZoomAlert />

			<ul className="navbar-nav start" onClick={deselectItem}>
				{config.isCMS && backURL ? (
					<li className="nav-item">
						<ClayLink
							aria-label={Liferay.Language.get('back')}
							borderless
							displayType="secondary"
							href={backURL}
							monospaced
							outline
							small
						>
							<ClayIcon symbol="angle-left" />
						</ClayLink>
					</li>
				) : null}

				{isCMSFreeTier && (
					<FeatureIndicator interactive type="enterprise" />
				)}

				<li className="nav-item">
					<ExperienceToolbarSection />
				</li>

				<li className="nav-item">
					<Translation
						availableLanguages={config.availableLanguages}
						defaultLanguageId={config.defaultLanguageId}
						dispatch={dispatch}
						fragmentEntryLinks={store.fragmentEntryLinks}
						languageId={store.languageId}
						segmentsExperienceId={segmentsExperienceId}
					/>
				</li>

				{!config.singleSegmentsExperienceMode &&
					segmentsExperimentStatus && (
						<li className="nav-item pl-2">
							<ExperimentsLabel
								label={segmentsExperimentStatus.label}
								value={segmentsExperimentStatus.value}
							/>
						</li>
					)}
			</ul>

			<ul
				className="middle navbar-nav"
				onClick={deselectItem}
				role="presentation"
			>
				<li className="nav-item" role="presentation">
					<ViewportSizeSelector
						onSizeSelected={(size) => {
							if (size !== selectedViewportSize) {
								dispatch(Actions.switchViewportSize({size}));
							}
						}}
						selectedSize={selectedViewportSize}
					/>
				</li>
			</ul>

			<ul className="end navbar-nav" onClick={deselectItem}>
				<li className="nav-item">
					<NetworkStatusBar {...network} />
				</li>

				<li className="d-lg-flex d-none nav-item">
					<Undo />
				</li>

				<li className="nav-item">
					<EditModeSelector />
				</li>

				<li className="d-lg-flex d-none nav-item">
					<ul className="navbar-nav">
						<li className="nav-item">
							<HideSidebarButton />
						</li>
					</ul>
				</li>

				<li className="d-lg-flex d-none nav-item">
					<form
						action={config.discardDraftURL}
						method="POST"
						ref={discardDraftFormRef}
					>
						<DiscardDraftButton />
					</form>
				</li>

				<li className="d-lg-none nav-item">
					<ToolbarActionsDropdown
						discardDraftFormRef={discardDraftFormRef}
					/>
				</li>

				<li className="nav-item">
					<PublishButton
						ariaLabel={ariaLabel}
						canPublish={canPublish}
						disabled={isCMSFreeTier}
						formRef={formRef}
						icon={icon}
						label={publishButtonLabel}
						onPublish={onPublish}
					/>
				</li>

				{config.isCMS ? (
					<li className="nav-item">
						<ClayDropDownWithItems
							hasLeftSymbols
							items={[
								{
									label: Liferay.Language.get(
										'autogenerate-default-experience'
									),
									onClick: regenerateDisplayPage,
									symbolLeft: 'order-form-pencil',
								},
							]}
							trigger={
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get('actions')}
									borderless
									displayType="secondary"
									monospaced
									size="sm"
									symbol="ellipsis-v"
									title={Liferay.Language.get('actions')}
								/>
							}
						/>
					</li>
				) : null}

				<li className="d-md-none nav-item">
					<ToggleConfigurationSidebarButton />
				</li>
			</ul>
		</ClayLayout.ContainerFluid>
	);
}

export default function Toolbar() {
	const container = document.getElementById(config.toolbarId);
	const isMounted = useIsMounted();

	if (!isMounted()) {

		// First time here, must empty JSP-rendered markup from container.

		while (container.firstChild) {
			container.removeChild(container.firstChild);
		}
	}

	return (
		<ReactPortal container={container} wrapper={false}>
			<ToolbarBody />
		</ReactPortal>
	);
}

function regenerateDisplayPage() {
	fetch(config.regenerateDisplayPageURL, {method: 'POST'}).then(
		(response) => {
			if (response.ok) {
				window.location.reload();
			}
			else {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred-while-autogenerating-default-experience'
					),
					type: 'danger',
				});
			}
		}
	);
}
