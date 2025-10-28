/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

import {MarketplaceProduct} from '../../core/MarketplaceProduct';
import {Product} from '../../types';
import PublisherSupportInfoCard from './PublisherSupportInfoCard';

type PublisherSupportModalProps = {
	onClose: () => void;
	product: Product;
};

const PublisherSupportModal = ({
	onClose,
	product,
}: PublisherSupportModalProps) => {
	const marketplaceProduct = new MarketplaceProduct(product);

	const {PUBLISHER_WEBSITE_URL, SUPPORT_EMAIL_ADDRESS, SUPPORT_PHONE} =
		marketplaceProduct.specificationValues;

	const {observer} = useModal({onClose});

	return (
		<ClayModal center observer={observer} size={'md' as any}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('publisher-support-contact-info')}
			</ClayModal.Header>

			<ClayModal.Body className="p-3">
				<PublisherSupportInfoCard
					symbol="picture"
					urlImage={marketplaceProduct.productImage}
					value={marketplaceProduct.catalogName}
				/>

				{PUBLISHER_WEBSITE_URL && (
					<PublisherSupportInfoCard
						symbol="globe"
						title={Liferay.Language.get('publisher-support-url')}
						value={
							<a
								className="modal-link"
								href={PUBLISHER_WEBSITE_URL}
								target="_blank"
							>
								{PUBLISHER_WEBSITE_URL}
							</a>
						}
					/>
				)}

				{SUPPORT_EMAIL_ADDRESS && (
					<PublisherSupportInfoCard
						symbol="envelope-closed"
						title={Liferay.Language.get('support-email-address')}
						value={
							<a
								className="modal-link"
								href={`mailto:${SUPPORT_EMAIL_ADDRESS}`}
								target="_blank"
							>
								{SUPPORT_EMAIL_ADDRESS}
							</a>
						}
					/>
				)}

				{SUPPORT_PHONE && (
					<PublisherSupportInfoCard
						symbol="phone"
						title={Liferay.Language.get('phone')}
						value={
							<a
								className="modal-link"
								href={`tel:${SUPPORT_PHONE}`}
								target="_blank"
							>
								{SUPPORT_PHONE}
							</a>
						}
					/>
				)}
			</ClayModal.Body>
		</ClayModal>
	);
};

export default PublisherSupportModal;
