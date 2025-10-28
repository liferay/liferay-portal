/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {WorkflowInstanceTracker} from '@liferay/portal-workflow-instance-tracker-web';
import React, {useState} from 'react';

export default function StatusLabel({
	baseResourceURL,
	instanceId,
	showInstanceTracker,
	statusMessage,
	statusStyle,
}) {
	const [showInstanceTrackerModal, setShowInstanceTrackerModal] =
		useState(false);

	const {observer} = useModal({
		onClose: () => {
			setShowInstanceTrackerModal(false);
		},
	});

	if (!showInstanceTracker) {
		return <ClayLabel displayType={statusStyle}>{statusMessage}</ClayLabel>;
	}

	return (
		<>
			<ClayTooltipProvider>
				<ClayLink
					data-tooltip-align="bottom"
					onClick={() => setShowInstanceTrackerModal(true)}
					title={Liferay.Language.get('track-workflow')}
				>
					<ClayLabel displayType={statusStyle}>
						{statusMessage}
					</ClayLabel>
				</ClayLink>
			</ClayTooltipProvider>

			{showInstanceTrackerModal && (
				<ClayModal observer={observer} size="full-screen">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{Liferay.Language.get('track-workflow')}
					</ClayModal.Header>

					<ClayModal.Body>
						<WorkflowInstanceTracker
							baseResourceURL={baseResourceURL}
							workflowInstanceId={instanceId}
						/>
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
}
