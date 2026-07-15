/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IItemsActions, getItemActionURL} from '@liferay/frontend-data-set-web';
import {Immutable} from '@liferay/frontend-js-state-web';
import {
	displayErrorToast,
	displayRequestSuccessToast,
} from '@liferay/site-cms-site-initializer';
import {navigate} from 'frontend-js-web';
import React from 'react';

import DeleteTaskModal from '../components/modal/DeleteTaskModal';
import EditAssigneeModalContent from '../components/modal/EditAssigneeModalContent';
import {
	deleteTaskById,
	getUserAccount,
	patchTaskById,
	postSubscribeTaskByExternalReferenceCode,
	postUnsubscribeTaskByExternalReferenceCode,
} from './api';
import {openCMPModal} from './openCMPModal';
import {
	displayAssignSuccessToast,
	displayDeleteSuccessToast,
} from './toastUtil';
import {ITaskObjectEntry} from './types';

export default function getTaskItemsActions(
	itemsActions: IItemsActions[],
	loadData: Function,
	task: {
		actions?: ITaskObjectEntry['actions'];
		embedded: Immutable<ITaskObjectEntry> | ITaskObjectEntry;
	}
) {
	const topItems = [];

	if (task.actions?.update) {
		topItems.push({
			label: Liferay.Language.get('edit'),
			onClick: () => {
				const editURL = getItemActionURL(itemsActions, 'edit', task);

				if (editURL) {
					navigate(editURL);
				}
			},
			symbolLeft: 'pencil',
		});
	}

	if (task.actions?.get) {
		topItems.push({
			label: Liferay.Language.get('view'),
			onClick: () => {
				const viewURL = getItemActionURL(
					itemsActions,
					'actionLink',
					task
				);

				if (viewURL) {
					navigate(viewURL);
				}
			},
			symbolLeft: 'view',
		});
	}

	const middleItems = [];

	if (task.actions?.subscribe) {
		middleItems.push({
			label: Liferay.Language.get('watch-task'),
			onClick: async () => {
				const {error} = await postSubscribeTaskByExternalReferenceCode({
					externalReferenceCode: task.embedded.externalReferenceCode,
					scopeKey: task.embedded.scopeKey,
				});

				if (!error) {
					loadData();
					displayRequestSuccessToast();
				}
				else {
					displayErrorToast(error);
				}
			},
			symbolLeft: 'bell-on',
		});
	}

	if (task.actions?.unsubscribe) {
		middleItems.push({
			label: Liferay.Language.get('stop-watching-task'),
			onClick: async () => {
				const {error} =
					await postUnsubscribeTaskByExternalReferenceCode({
						externalReferenceCode:
							task.embedded.externalReferenceCode,
						scopeKey: task.embedded.scopeKey,
					});

				if (!error) {
					loadData();
					displayRequestSuccessToast();
				}
				else {
					displayErrorToast(error);
				}
			},
			symbolLeft: 'bell-off',
		});
	}

	if (task.actions?.assignToMe) {
		middleItems.push({
			label: Liferay.Language.get('assign-to-me'),
			onClick: async () => {
				const user = (await getUserAccount(
					Liferay.ThemeDisplay.getUserId().toString()
				)) as {
					externalReferenceCode: string;
					name: string;
				};

				const {error} = await patchTaskById({
					body: {
						assignTo: {
							externalReferenceCode: user.externalReferenceCode,
							name: user.name,
							type: 'User',
						},
					},
					taskId: String(task.embedded.id),
				});

				if (!error) {
					loadData();
					displayAssignSuccessToast(task.embedded.title, user.name);
				}
				else {
					displayErrorToast(error);
				}
			},
		});
	}

	if (task.actions?.update) {
		middleItems.push({
			label: Liferay.Language.get('assign-to-...'),
			onClick: async () => {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<EditAssigneeModalContent
							closeModal={closeModal}
							loadData={loadData}
							taskId={String(task.embedded.id)}
							taskTitle={task.embedded.title}
							value={task.embedded.assignTo}
						/>
					),
					size: 'md',
				});
			},
		});
	}

	const bottomItems = [];

	if (task.actions?.delete) {
		bottomItems.push({

			// @ts-ignore

			className: 'text-danger',
			label: Liferay.Language.get('delete'),
			onClick: async () => {
				await openCMPModal({
					center: true,
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<DeleteTaskModal
							closeModal={closeModal}
							onSubmit={async () => {
								const {error} = await deleteTaskById({
									taskId: String(task.embedded.id),
								});

								if (!error) {
									loadData();
									displayDeleteSuccessToast(
										task.embedded.title
									);
								}
								else {
									displayErrorToast(error);
								}
								closeModal();
							}}
							title={task.embedded.title}
						/>
					),
					size: 'md',
					status: 'danger',
				});
			},
			symbolLeft: 'trash',
		});
	}

	const items = [];

	for (const section of [topItems, middleItems, bottomItems]) {
		if (!section.length) {
			continue;
		}

		if (items.length) {
			items.push({type: 'divider' as const});
		}

		for (const item of section) {
			items.push(item);
		}
	}

	return items;
}
