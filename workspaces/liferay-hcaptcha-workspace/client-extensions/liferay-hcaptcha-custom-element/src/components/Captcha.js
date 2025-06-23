/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import HCaptcha from '@hcaptcha/react-hcaptcha';
import React from 'react';

const Captcha = () => {
	return (
		<div>
			<HCaptcha sitekey="your-site-key" />
		</div>
	);
};

export default Captcha;
