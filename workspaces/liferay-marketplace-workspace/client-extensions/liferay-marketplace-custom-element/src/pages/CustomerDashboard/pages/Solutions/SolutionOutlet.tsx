/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '../../../../i18n';
import {BaseOutlet} from '../Apps/App/AppOutlet';

const SolutionOutlet = () => (
	<BaseOutlet
		backTitle={i18n.translate('back-to-my-solutions')}
		backURL="../solutions"
		routes={[{name: i18n.translate('details'), path: ''}]}
		showActions={false}
	/>
);

export default SolutionOutlet;
