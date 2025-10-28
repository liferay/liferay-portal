/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

export default function LockedArticleModal({
	actionLabel: initialActionLabel,
	actionURL: initialActionURL,
	groupAdmin,
	open,
	portletNamespace,
	userName: initialUserName,
}) {
	const [showModal, setShowModal] = useState(open);
	const [actionLabel, setActionLabel] = useState(initialActionLabel);
	const [actionURL, setActionURL] = useState(initialActionURL);
	const [userName, setUserName] = useState(initialUserName);

	const handleOnClose = () => {
		setShowModal(false);
	};

	const {observer, onClose} = useModal({
		onClose: handleOnClose,
	});

	useEffect(() => {
		const bridgeComponentId = `${portletNamespace}LockedKBArticleModal`;

		if (!Liferay.component(bridgeComponentId)) {
			Liferay.component(
				bridgeComponentId,
				{
					open: (actionLabel, actionURL, userName) => {
						setShowModal(true);
						setActionLabel(actionLabel);
						setActionURL(actionURL);
						setUserName(userName);
					},
				},
				{
					destroyOnNavigate: true,
				}
			);
		}

		return () => {
			Liferay.destroyComponent(bridgeComponentId);
		};
	}, [portletNamespace]);

	return (
		<>
			{showModal && (
				<ClayModal observer={observer} size="md" status="info">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('article-in-edition')}
					</ClayModal.Header>

					<ClayModal.Body>
						<p>
							{groupAdmin
								? sub(
										Liferay.Language.get(
											'article-in-edition-by-user-x-description'
										),
										userName
									)
								: Liferay.Language.get(
										'article-in-edition-description'
									)}
						</p>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							groupAdmin ? (
								<ClayButton.Group spaced>
									<ClayButton
										displayType="secondary"
										onClick={onClose}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayLink
										button={true}
										displayType="primary"
										href={actionURL}
									>
										{sub(
											Liferay.Language.get(
												'take-control-and-x'
											),
											actionLabel
										)}
									</ClayLink>
								</ClayButton.Group>
							) : (
								<ClayButton
									displayType="primary"
									onClick={onClose}
								>
									{Liferay.Language.get('ok')}
								</ClayButton>
							)
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
