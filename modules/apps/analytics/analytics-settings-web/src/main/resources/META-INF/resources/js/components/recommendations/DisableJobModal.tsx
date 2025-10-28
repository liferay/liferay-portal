/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React from 'react';

interface IDisableJobModalProps {
	observer: any;
	onCancel: () => void;
	onDisable: () => void;
	title: string;
}

const DisableJobModal: React.FC<IDisableJobModalProps> = ({
	observer,
	onCancel,
	onDisable,
	title,
}) => {
	return (
		<ClayModal observer={observer} status="warning">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(Liferay.Language.get('disable-x'), [`"${title}"`])}
			</ClayModal.Header>

			<ClayModal.Body>
				<Text weight="bold">
					{Liferay.Language.get(
						'are-you-sure-you-want-to-disable-this-recommendation-model?'
					)}
				</Text>

				<p>
					<Text>
						{Liferay.Language.get(
							'disabling-this-recommendation-model-will-stop-updates-for-pages-using-it,-and-those-pages-will-no-longer-receive-new-recommendations'
						)}
					</Text>
				</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onCancel()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="warning"
							onClick={() => onDisable()}
						>
							{Liferay.Language.get('disable')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default DisableJobModal;
