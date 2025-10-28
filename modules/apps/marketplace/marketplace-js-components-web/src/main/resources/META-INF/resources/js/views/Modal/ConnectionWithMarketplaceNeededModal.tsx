/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import {createRenderURL} from 'frontend-js-web';
import React from 'react';

type ConnectionWithMarketplaceNeededModalProps = {
	message?: string;
	observer: Observer;
	open: boolean;
};

const goToInstanceSettings = () =>
	Liferay.Util.navigate(
		createRenderURL('group/control_panel/manage', {
			configurationScreenKey: 'marketplace',
			mvcRenderCommandName:
				'/configuration_admin/view_configuration_screen',
			p_p_id: Liferay.PortletKeys.INSTANCE_SETTINGS,
		})
	);

export function ConnectionWithMarketplaceNeededModal(
	props: ConnectionWithMarketplaceNeededModalProps
) {
	if (!props.open) {
		return null;
	}

	return (
		<ClayModal center observer={props.observer} status="info">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('connection-with-marketplace-needed')}
			</ClayModal.Header>

			<ClayModal.Body>{props.message}</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="info"
							onClick={goToInstanceSettings}
						>
							{Liferay.Language.get('go-to-instance-settings')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
