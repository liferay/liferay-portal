/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@clayui/css/lib/css/atlas.css';

import 'swagger-ui-react/swagger-ui.css';

import APIGUI from './APIGUI';

import 'graphiql/style.css';

import '../css/main.css';

const App = (props) => {
	return <APIGUI props={props} />;
};

export default App;
