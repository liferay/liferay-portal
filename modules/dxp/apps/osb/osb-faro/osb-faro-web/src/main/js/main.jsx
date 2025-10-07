import '@clayui/css/lib/css/atlas.css';

// eslint-disable-next-line sort-imports-es6-autofix/sort-imports-es6
import '../css/main.scss';
import './external-scripts';

import App from './App';

import React from 'react';
import ReactDOM from 'react-dom';

ReactDOM.render(<App />, document.getElementById('faroApp'));
