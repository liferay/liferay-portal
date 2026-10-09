import '@clayui/css/lib/css/atlas.css';

import '../css/main.scss';
import './external-scripts';

import App from './App';

import React from 'react';
import {createRoot} from 'react-dom/client';

const root = createRoot(document.getElementById('faroApp'));
root.render(<App />);
