/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {useHasStyleErrors} from '@liferay/layout-js-components-web';
import React, {useRef, useState} from 'react';

import {StyleErrorsModal} from './StyleErrorsModal';
import {config} from './config';

export default function PublishButton() {
	const formRef = useRef();
	const hasStyleErrors = useHasStyleErrors();
	const [openPublishModal, setOpenPublishModal] = useState(false);
	const [openStyleErrorsModal, setOpenStyleErrorsModal] = useState(false);

	const {observer: observerPublishModal, onClose: onClosePublishModal} =
		useModal({
			onClose: () => setOpenPublishModal(false),
		});

	const handleSubmit = () => {
		if (formRef.current) {
			formRef.current.submit();
		}
	};

	return (
		<>
			<form action={config.publishURL} method="POST" ref={formRef}>
				<input
					name={`${config.namespace}redirect`}
					type="hidden"
					value={config.redirectURL}
				/>

				<input
					name={`${config.namespace}styleBookEntryId`}
					type="hidden"
					value={config.styleBookEntryId}
				/>

				<ClayButton
					aria-label={Liferay.Language.get('publish')}
					disabled={config.pending}
					displayType="primary"
					onClick={
						hasStyleErrors
							? () => setOpenStyleErrorsModal(true)
							: () => setOpenPublishModal(true)
					}
					size="sm"
					type="button"
				>
					{Liferay.Language.get('publish')}
				</ClayButton>
			</form>

			{openStyleErrorsModal && hasStyleErrors && (
				<StyleErrorsModal
					onCloseModal={() => setOpenStyleErrorsModal(false)}
					onPublish={() => {
						setOpenStyleErrorsModal(false);
						setOpenPublishModal(true);
					}}
				/>
			)}

			{openPublishModal && (
				<ClayModal
					aria-label={Liferay.Language.get('publishing-info')}
					observer={observerPublishModal}
					status="info"
				>
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('publishing-info')}
					</ClayModal.Header>

					<ClayModal.Body>
						<p>
							{Liferay.Language.get(
								'once-published-these-changes-will-affect-all-instances-of-the-site-using-these-properties-do-you-want-to-publish-now'
							)}
						</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={onClosePublishModal}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									displayType="info"
									onClick={handleSubmit}
									type="submit"
								>
									{Liferay.Language.get('publish')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
