import { StrictMode } from 'react';
import ReactDOM from 'react-dom/client';

import Map from './components/Map'
import './index.css'

const Main = ({ promoCodeStore }) => (
	<StrictMode>
		<Map promoCodeStore={promoCodeStore} />
	</StrictMode>
);

class WebComponent extends HTMLElement {
	connectedCallback() {
		const mountPoint = document.createElement('span');
		const promoCodeStore = this.getAttribute('promoStore');
		const root = ReactDOM.createRoot(mountPoint);

		this.attachShadow({ mode: 'open' }).appendChild(mountPoint);
		
		root.render(<Main promoCodeStore={promoCodeStore} />);
	}
}

const ELEMENT_ID = 'clarity-solution-custom-element-distributors-map';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}
