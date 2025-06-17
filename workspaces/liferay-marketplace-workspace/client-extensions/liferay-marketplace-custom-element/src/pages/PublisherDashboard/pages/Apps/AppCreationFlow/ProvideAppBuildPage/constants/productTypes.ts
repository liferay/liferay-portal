/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ProductType} from '../../../../../../../enums/Product';
import i18n from '../../../../../../../i18n';

type ProductTypeOption = {
	description: string;
	label: string;
	value: ProductType;
};

export const ProductTypeOptions: ProductTypeOption[] = [
	{
		description:
			'Backend client extensions delivered as deployed services (only available to SaaS and PaaS clients).',
		label: i18n.translate('cloud-app'),
		value: ProductType.CLOUD,
	},
	{
		description:
			'Modular, decoupled components that allow developers to customize and extend Liferay DXP’s functionality without altering its core code. They interact with Liferay via headless APIs, providing flexibility and maintainability.',
		label: i18n.translate('client-extension'),
		value: ProductType.CLIENT_EXTENSION,
	},
	{
		description:
			'Complex app with multiple parts like i.e. OSGI extentions + CX + low-code applications.',
		label: i18n.translate('composite-app'),
		value: ProductType.COMPOSITE_APP,
	},
	{
		description:
			'Module-based apps delivered as .lpkg files that the user can install to modify native Liferay behavior.',
		label: i18n.translate('dxp-app'),
		value: ProductType.DXP,
	},
	{
		description:
			'Methods for building business applications faster without needing in-depth coding knowledge (i.e.: fragments , data set, object definitions, etc). ',
		label: i18n.translate('low-code-configuration'),
		value: ProductType.LOW_CODE_CONFIGURATION,
	},
	{
		description:
			'Apps that do not fit into the standard categories. This may include external integrations, legacy solutions, prototypes, or custom deployments.',
		label: i18n.translate('other'),
		value: ProductType.OTHER,
	},
];
