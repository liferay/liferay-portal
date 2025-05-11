/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import React, {ComponentProps, useState} from 'react';

import MarketplaceModal from './MarketplaceModal';

import '../../../css/MarketplaceModal.scss';

import {AppsPermissions} from '@liferay/marketplace-js-components-web';

import MarketplaceViews from './MarketplaceViews';

interface MarketplacePresentationModalProps {
	body: string;
	heading: string;
	onCloseModal: () => void;
	permissions: AppsPermissions;
	portletNamespace: string;
}

export default function MarketplacePresentationModal({
	body,
	heading,
	onCloseModal,
	permissions,
	portletNamespace,
	...marketplaceViewProps
}: MarketplacePresentationModalProps &
	ComponentProps<typeof MarketplaceViews>) {
	const [openMarketplace, setOpenMarketplace] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			onCloseModal();
		},
	});

	return openMarketplace ? (
		<MarketplaceModal
			openOnRender={true}
			permissions={permissions}
			portletNamespace={portletNamespace}
			trigger={null}
			{...marketplaceViewProps}
		/>
	) : (
		<ClayModal center observer={observer}>
			<ClayModal.Header>{heading}</ClayModal.Header>

			<ClayModal.Body className="c-p-0">
				<div className="marketplace-modal__image-background">
					<img
						alt=""
						src={`${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/layout-js-components-web/images/marketplace.svg`}
					/>
				</div>

				<p className="c-p-4">{body}</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							aria-label={Liferay.Language.get(
								'explore-marketplace'
							)}
							displayType="primary"
							onClick={() => setOpenMarketplace(true)}
							title={Liferay.Language.get('explore-marketplace')}
						>
							<ClayIcon
								className="inline-item inline-item-before"
								symbol="marketplace"
							/>

							{Liferay.Language.get('explore-marketplace')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
