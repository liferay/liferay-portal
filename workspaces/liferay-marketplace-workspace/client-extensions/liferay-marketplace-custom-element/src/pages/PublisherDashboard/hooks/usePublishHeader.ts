/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLayoutEffect} from 'react';

const usePublishHeader = () => {
	useLayoutEffect(() => {
		const marketplaceHeader = document.querySelector('.marketplace-header');
		const marketplacePublisherContainer = document.querySelector(
			'.publisher-dashboard-container'
		);

		if (marketplaceHeader) {
			marketplaceHeader.classList.add('d-none');
		}

		if (marketplacePublisherContainer) {
			marketplacePublisherContainer.classList.add(
				'marketplace-publisher-header'
			);
		}

		return () => {
			marketplaceHeader?.classList?.remove('d-none');
			marketplacePublisherContainer?.classList?.remove(
				'marketplace-publisher-header'
			);
		};
	}, []);
};

export default usePublishHeader;
