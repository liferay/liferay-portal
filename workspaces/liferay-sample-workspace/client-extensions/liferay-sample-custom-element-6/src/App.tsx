/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import {Liferay} from './services/liferay';

function App() {
	const [count, setCount] = useState(0);
	const [user, setUser] = useState({});

	useEffect(() => {
		Liferay.Util.fetch(`/o/headless-admin-user/v1.0/my-user-account`)
			.then((response) => response.json())
			.then(setUser)
			.catch(console.error);
	}, []);

	return (
		<div className="container">
			<h1 className="liferay-sample-header">Liferay + Vite + React</h1>

			<code>
				<pre>{JSON.stringify(user, null, 2)}</pre>
			</code>

			<div className="card">
				<button onClick={() => setCount((count) => count + 1)}>
					count is {count}
				</button>
				<p>
					Edit <code>src/App.tsx</code> and save to test HMR
				</p>
			</div>

			<p className="read-the-docs">
				Click on the Vite and React logos to learn more
			</p>
		</div>
	);
}

export default App;
