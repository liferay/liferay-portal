import {StrictMode} from 'react';
import {createRoot} from 'react-dom/client';

import DistributorDetails from './components/DistributorDetails.jsx';

class WebComponent extends HTMLElement {
	connectedCallback() {
		this.root = createRoot(this);

		this.root.render(
			<StrictMode>
				<DistributorDetails />
			</StrictMode>,
			this
		);
	}
}

const ELEMENT_ID = 'clarity-solution-custom-element-distributor-details';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}
