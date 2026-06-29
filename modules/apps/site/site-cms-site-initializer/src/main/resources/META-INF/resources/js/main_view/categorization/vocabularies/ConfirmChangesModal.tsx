/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

const CONFIRMATION_MESSAGES = {
	ASSET_TYPES: {
		description: Liferay.Language.get(
			'removing-asset-types-will-detach-vocabulary'
		),
		title: Liferay.Language.get('confirm-asset-type-change'),
	},
	BOTH: {
		description: Liferay.Language.get(
			'removing-both-will-make-the-vocabulary-unavailable'
		),
		title: Liferay.Language.get('confirm-changes'),
	},
	PROJECTS: {
		description: Liferay.Language.get(
			'removing-a-project-will-make-the-vocabulary-unavailable'
		),
		title: Liferay.Language.get('confirm-project-change'),
	},
	SPACES: {
		description: Liferay.Language.get(
			'removing-a-space-will-make-the-vocabulary-unavailable'
		),
		title: Liferay.Language.get('confirm-space-change'),
	},
};

export default function ConfirmChangesModal({
	assetTypeChange,
	observer,
	onOpenChange,
	onSave,
	open,
	projectChange,
	spaceChange,
}: {
	assetTypeChange: boolean;
	observer: any;
	onOpenChange: (value: boolean) => void;
	onSave: Function;
	open: any;
	projectChange: boolean;
	spaceChange: boolean;
}) {
	const confirmationMessages = (() => {
		if (
			(assetTypeChange && (projectChange || spaceChange)) ||
			(projectChange && spaceChange)
		) {
			return CONFIRMATION_MESSAGES.BOTH;
		}
		else if (assetTypeChange) {
			return CONFIRMATION_MESSAGES.ASSET_TYPES;
		}
		else if (spaceChange) {
			return CONFIRMATION_MESSAGES.SPACES;
		}
		else {
			return CONFIRMATION_MESSAGES.PROJECTS;
		}
	})();

	return (
		<>
			{open && (
				<ClayModal observer={observer} status="warning">
					<ClayModal.Header
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						{confirmationMessages.title}
					</ClayModal.Header>

					<ClayModal.Body>
						{confirmationMessages.description}
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
									className="btn btn-warning"
									onClick={async () => {
										onOpenChange(false);

										onSave();
									}}
								>
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	);
}
