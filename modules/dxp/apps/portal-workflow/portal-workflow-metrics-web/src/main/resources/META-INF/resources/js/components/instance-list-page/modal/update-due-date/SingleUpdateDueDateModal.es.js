/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useContext, useMemo, useState} from 'react';

import ContentView from '../../../../shared/components/content-view/ContentView.es';
import RetryButton from '../../../../shared/components/list/RetryButton.es';
import PromisesResolver from '../../../../shared/components/promises-resolver/PromisesResolver.es';
import {useToaster} from '../../../../shared/components/toaster/hooks/useToaster.es';
import {useFetch} from '../../../../shared/hooks/useFetch.es';
import {usePost} from '../../../../shared/hooks/usePost.es';
import {InstanceListContext} from '../../InstanceListPageProvider.es';
import {ModalContext} from '../ModalProvider.es';
import UpdateDueDateStep from './UpdateDueDateStep.es';

export default function SingleUpdateDueDateModal() {
	const [errorToast, setErrorToast] = useState(false);
	const [retry, setRetry] = useState(0);
	const [sendingPost, setSendingPost] = useState(false);

	const toaster = useToaster();

	const {closeModal, setUpdateDueDate, updateDueDate, visibleModal} =
		useContext(ModalContext);
	const {selectedInstance, setSelectedItems} =
		useContext(InstanceListContext);

	const {comment, dueDate} = updateDueDate;

	const onCloseModal = (refetch) => {
		closeModal(refetch);
		setSelectedItems([]);
		setUpdateDueDate({
			comment: undefined,
			dueDate: undefined,
		});
	};

	const {observer, onClose} = useModal({
		onClose: onCloseModal,
	});

	const {data, fetchData} = useFetch({
		admin: true,
		params: {completed: false, page: 1, pageSize: 1},
		url: `/workflow-instances/${selectedInstance?.id}/workflow-tasks`,
	});

	const {dateDue, id: taskId} = data?.items?.[0] || {};

	const {postData} = usePost({
		admin: true,
		body: {comment, dueDate},
		callback: () => {
			toaster.success(
				Liferay.Language.get(
					'the-due-date-for-this-task-has-been-updated'
				)
			);

			onCloseModal(true);
			setSendingPost(false);
			setErrorToast(false);
		},
		url: `/workflow-tasks/${taskId}/update-due-date`,
	});

	const handleDone = useCallback(() => {
		setSendingPost(true);
		setErrorToast(false);

		postData().catch((dataError) => {
			const errorMessage = `${Liferay.Language.get(
				'your-request-has-failed'
			)} ${Liferay.Language.get('select-done-to-retry')}`;

			setErrorToast(dataError?.title ?? errorMessage);
			setSendingPost(false);
		});

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [toaster]);

	const promises = useMemo(() => {
		setErrorToast(false);

		if (selectedInstance?.id && visibleModal === 'updateDueDate') {
			return [
				fetchData().catch((error) => {
					setErrorToast(
						Liferay.Language.get('your-request-has-failed')
					);

					return Promise.reject(error);
				}),
			];
		}

		return [];

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [retry, selectedInstance, visibleModal]);

	const statesProps = {
		errorProps: {
			actionButton: (
				<RetryButton onClick={() => setRetry((retry) => retry + 1)} />
			),
			className: 'mt-5 py-5',
			hideAnimation: true,
			message: Liferay.Language.get('unable-to-retrieve-data'),
			messageClassName: 'small',
		},
		loadingProps: {className: 'mt-3 py-7'},
	};

	return (
		<>
			<PromisesResolver promises={promises}>
				{visibleModal === 'updateDueDate' && (
					<ClayModal observer={observer} size="md">
						<ClayModal.Header
							closeButtonAriaLabel={Liferay.Language.get('close')}
						>
							{Liferay.Language.get('update-task-due-date')}
						</ClayModal.Header>

						{errorToast && (
							<ClayAlert
								className="mb-0"
								displayType="danger"
								title={Liferay.Language.get('error')}
							>
								{errorToast}
							</ClayAlert>
						)}

						<ContentView {...statesProps}>
							<UpdateDueDateStep dueDate={dateDue} />
						</ContentView>

						<ClayModal.Footer
							last={
								<>
									<ClayButton
										className="mr-3"
										disabled={sendingPost}
										displayType="secondary"
										onClick={onClose}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayButton
										disabled={sendingPost || !dueDate}
										onClick={handleDone}
									>
										{Liferay.Language.get('done')}
									</ClayButton>
								</>
							}
						/>
					</ClayModal>
				)}
			</PromisesResolver>
		</>
	);
}
