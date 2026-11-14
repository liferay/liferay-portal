import AppProviders from './AppProviders';
import React from 'react';
import {RouterProvider} from 'react-router-dom';
import {router} from './routes';

const App = () => (
	<AppProviders>
		<RouterProvider router={router} />
	</AppProviders>
);

export default App;
