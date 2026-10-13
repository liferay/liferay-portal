/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import {createPortletURL, navigate as navigateUtil, sub} from 'frontend-js-web';
import React, {useCallback, useRef, useState} from 'react';

import ChangeTrackingLayoutChangesSidePanel from './ChangeTrackingLayoutChangesSidePanel';
import ChangeTrackingRenderView from './ChangeTrackingRenderView';

export default function ChangeTrackingChangeView({
	changeURL,
	changes,
	contextView,
	dataURL,
	defaultLocale,
	discardURL,
	entryFromURL,
	layoutContentChangesURL,
	modelData,
	moveChangesURL,
	namespace,
	siteNames,
	spritemap,
	typeNames,
	userInfo,
}) {
	const CHANGE_TYPE_ADDITION = 0;
	const CHANGE_TYPE_DELETION = 1;
	const GLOBAL_SITE_NAME = Liferay.Language.get('global');

	const containerRef = useRef(null);
	const [openSidePanel, setOpenSidePanel] = useState(true);
	const showChangesSidePanel = layoutContentChangesURL ? true : false;

	const getNodeId = useCallback(
		(modelKey) => {
			if (!contextView) {
				return modelKey;
			}

			const stack = [contextView.everything];

			while (stack.length) {
				const element = stack.pop();

				if (element.modelKey === modelKey) {
					return element.nodeId;
				}

				if (!element.children) {
					continue;
				}

				for (let i = 0; i < element.children.length; i++) {
					stack.push(element.children[i]);
				}
			}

			return 0;
		},
		[contextView]
	);

	const modelsRef = useRef(null);

	if (modelsRef.current === null) {
		modelsRef.current = JSON.parse(JSON.stringify(modelData));

		const modelKeys = Object.keys(modelsRef.current);

		for (let i = 0; i < modelKeys.length; i++) {
			const model = modelsRef.current[modelKeys[i]];

			if (model.groupId) {
				model.siteName = siteNames[model.groupId];
			}
			else {
				model.groupId = 0;
				model.siteName = GLOBAL_SITE_NAME;
			}

			model.nodeId = getNodeId(Number(modelKeys[i]));
			model.typeName = typeNames[model.modelClassNameId];

			if (model.ctEntryId) {
				const user = userInfo[model.userId.toString()];

				if (user) {
					model.portraitURL = user.portraitURL;
					model.userName = user.userName;
				}
				else {
					model.userName = '';
				}

				if (model.siteName === GLOBAL_SITE_NAME) {
					let key = Liferay.Language.get('x-modified-a-x-x-ago');

					if (model.changeType === CHANGE_TYPE_ADDITION) {
						key = Liferay.Language.get('x-added-a-x-x-ago');
					}
					else if (model.changeType === CHANGE_TYPE_DELETION) {
						key = Liferay.Language.get('x-deleted-a-x-x-ago');
					}

					model.description = sub(
						key,
						model.userName,
						model.typeName,
						model.timeDescription
					);
				}
				else {
					let key = Liferay.Language.get('x-modified-a-x-in-x-x-ago');

					if (model.changeType === CHANGE_TYPE_ADDITION) {
						key = Liferay.Language.get('x-added-a-x-in-x-x-ago');
					}
					else if (model.changeType === CHANGE_TYPE_DELETION) {
						key = Liferay.Language.get('x-deleted-a-x-in-x-x-ago');
					}

					model.description = sub(
						key,
						model.userName,
						model.typeName,
						model.siteName,
						model.timeDescription
					);
				}
			}
		}
	}

	const contextViewRef = useRef(null);

	if (contextView && contextViewRef.current === null) {
		contextViewRef.current = JSON.parse(JSON.stringify(contextView));
	}

	const getModels = useCallback((nodes) => {
		if (!nodes) {
			return [];
		}

		const models = [];

		for (let i = 0; i < nodes.length; i++) {
			const node = nodes[i];

			let modelKey = node;

			if (typeof node === 'object') {
				modelKey = node.modelKey;
			}

			if (
				!Object.prototype.hasOwnProperty.call(
					modelsRef.current,
					modelKey.toString()
				)
			) {
				continue;
			}

			const json = JSON.parse(
				JSON.stringify(modelsRef.current[modelKey])
			);

			if (typeof node === 'object') {
				json.nodeId = node.nodeId;
			}

			models.push(json);
		}

		return models;
	}, []);

	const getNode = useCallback(
		(nodeId) => {
			const rootNode = {children: getModels(changes), nodeId: 0};

			if (!nodeId) {
				return rootNode;
			}

			let modelKey = null;

			if (typeof nodeId === 'string') {
				const parts = nodeId.split('-');

				if (parts.length !== 2) {
					return rootNode;
				}

				const modelClassNameId = parts[0];
				const modelClassPK = parts[1];

				const keys = Object.keys(modelsRef.current);

				for (let i = 0; i < keys.length; i++) {
					const model = modelsRef.current[keys[i]];

					if (
						String(model.modelClassNameId) === modelClassNameId &&
						String(model.modelClassPK) === modelClassPK
					) {
						if (!contextView) {
							return model;
						}

						modelKey = Number(keys[i]);

						break;
					}
				}

				if (modelKey === null) {
					return rootNode;
				}
			}

			if (!contextView) {
				const keys = Object.keys(modelsRef.current);

				for (let i = 0; i < keys.length; i++) {
					const model = modelsRef.current[keys[i]];

					if (model.nodeId === nodeId) {
						return model;
					}
				}

				return rootNode;
			}

			let node = null;
			const parentsMap = {};

			const stack = [contextViewRef.current.everything];

			while (stack.length) {
				const element = stack.pop();

				if (
					(modelKey !== null && element.modelKey === modelKey) ||
					element.nodeId === nodeId
				) {
					if (element.parentNodeId) {
						parentsMap[element.parentModelKey] =
							element.parentNodeId;
					}

					if (node === null) {
						node = JSON.parse(
							JSON.stringify(modelsRef.current[element.modelKey])
						);

						node.children = getModels(element.children);
						node.nodeId = element.nodeId;
					}

					if (modelKey === null) {
						modelKey = element.modelKey;
					}
				}

				if (!element.children) {
					continue;
				}

				for (let i = 0; i < element.children.length; i++) {
					const child = element.children[i];

					child.parentModelKey = element.modelKey;
					child.parentNodeId = element.nodeId;

					stack.push(child);
				}
			}

			if (node === null) {
				return rootNode;
			}

			const parents = [];

			const keys = Object.keys(parentsMap);

			for (let i = 0; i < keys.length; i++) {
				const modelKey = keys[i];

				const nodeId = parentsMap[modelKey];

				parents.push({
					modelKey,
					nodeId,
				});
			}

			node.parents = getModels(parents);

			return node;
		},
		[changes, contextView, getModels]
	);

	const initialNode = getNode(entryFromURL);

	const navigate = useCallback(
		(nodeId) => {
			const node = getNode(nodeId);

			const newChangeURL = createPortletURL(changeURL, {
				modelClassNameId: node.modelClassNameId,
				modelClassPK: node.modelClassPK,
			});

			navigateUtil(newChangeURL.toString());
		},
		[changeURL, getNode]
	);

	const setParameter = useCallback(
		(url, name, value) => {
			return (
				url + '&' + namespace + name + '=' + encodeURIComponent(value)
			);
		},
		[namespace]
	);

	const getDataURL = (node) => {
		if (node.ctEntryId) {
			return setParameter(dataURL, 'ctEntryId', node.ctEntryId);
		}

		const url = setParameter(
			dataURL,
			'modelClassNameId',
			node.modelClassNameId
		);

		return setParameter(url, 'modelClassPK', node.modelClassPK);
	};

	const getDiscardURL = useCallback(
		(node) => {
			if (!discardURL) {
				return null;
			}

			const url = setParameter(
				discardURL,
				'modelClassNameId',
				node.modelClassNameId
			);

			return setParameter(url, 'modelClassPK', node.modelClassPK);
		},
		[discardURL, setParameter]
	);

	const getMoveChangesURL = useCallback(
		(node) => {
			if (!moveChangesURL) {
				return null;
			}

			const url = setParameter(
				moveChangesURL,
				'modelClassNameId',
				node.modelClassNameId
			);

			return setParameter(url, 'modelClassPK', node.modelClassPK);
		},
		[moveChangesURL, setParameter]
	);

	const onOpenSidePanelChange = () => {
		setOpenSidePanel(!openSidePanel);
	};

	const renderMainContent = () => {
		return (
			<div>
				<div
					className="publications-changes-content row"
					ref={containerRef}
				>
					<div className="col-md-12" style={{paddingRight: '0rem'}}>
						{initialNode.modelClassNameId ? (
							<ChangeTrackingRenderView
								childEntries={initialNode.children}
								ctEntry={!!initialNode.ctEntryId}
								defaultLocale={defaultLocale}
								description={
									initialNode.description
										? initialNode.description
										: initialNode.typeName
								}
								discardURL={getDiscardURL(initialNode)}
								handleNavigation={(nodeId) => navigate(nodeId)}
								handleOpenSidePanel={onOpenSidePanelChange}
								initialDataURL={getDataURL(initialNode)}
								isOpenSidePanel={openSidePanel}
								moveChangesURL={getMoveChangesURL(initialNode)}
								namespace={namespace}
								parentEntries={initialNode.parents}
								showChangesSidePanel={showChangesSidePanel}
								showDropdown={initialNode.modelClassNameId}
								showWorkflow={initialNode.showWorkflow}
								spritemap={spritemap}
								title={initialNode.title}
								workflowStatus={initialNode.workflowStatus}
							/>
						) : (
							<ClayLayout.Sheet>
								<ClayEmptyState
									className="mt-0"
									description={Liferay.Language.get(
										'no-changes-were-found'
									)}
									image={
										<ClayIcon
											className="c-empty-state-icon c-empty-state-icon-empty-state"
											illustration
											symbol="empty-state"
										/>
									}
									title={Liferay.Language.get(
										'no-results-found'
									)}
								/>
							</ClayLayout.Sheet>
						)}
					</div>

					{showChangesSidePanel ? (
						<ChangeTrackingLayoutChangesSidePanel
							containerRef={containerRef}
							handleOpenSidePanel={onOpenSidePanelChange}
							isOpen={openSidePanel}
							layoutContentChangesURL={layoutContentChangesURL}
							namespace={namespace}
							spritemap={spritemap}
						/>
					) : null}
				</div>
			</div>
		);
	};

	return renderMainContent();
}
