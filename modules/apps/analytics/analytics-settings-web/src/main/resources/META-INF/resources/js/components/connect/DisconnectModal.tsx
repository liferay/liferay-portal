/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React, {useState} from 'react';

import {EPageView, Events, useDispatch} from '../..';
import {deleteConnection} from '../../utils/api';
import Loading from '../Loading';

interface IDisconnectModalProps {
	observer: any;
	onOpenChange: (value: boolean) => void;
}

const DisconnectModal: React.FC<
	{children?: React.ReactNode | undefined} & IDisconnectModalProps
> = ({observer, onOpenChange}) => {
	const [submitting, setSubmitting] = useState(false);

	const dispatch = useDispatch();

	return (
		<ClayModal center observer={observer} status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('disconnecting-data-source')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					<strong>
						{Liferay.Language.get(
							'are-you-sure-you-want-to-disconnect-your-analytics-cloud-workspace-from-this-dxp-instance'
						)}
					</strong>
				</p>

				<p className="text-secondary">
					{Liferay.Language.get(
						'this-will-stop-any-syncing-of-analytics-or-contact-data-to-your-analytics-cloud-workspace'
					)}
				</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onOpenChange(false)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={submitting}
							displayType="warning"
							onClick={async () => {
								setSubmitting(true);

								const {ok} = await deleteConnection();

								setSubmitting(false);

								if (ok) {
									onOpenChange(false);

									dispatch({
										payload: {
											connected: false,
											token: '',
										},
										type: Events.Connect,
									});

									dispatch({
										payload: EPageView.Wizard,
										type: Events.ChangePageView,
									});

									Liferay.Util.openToast({
										message: Liferay.Language.get(
											'workspace-disconnected'
										),
									});
								}
							}}
						>
							{submitting && <Loading inline />}

							{Liferay.Language.get('disconnect')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default DisconnectModal;
