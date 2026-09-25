import {StrictMode} from 'react';
import {createRoot} from 'react-dom/client';

import DistributorsTable from './components/DistributorTable.jsx';

class WebComponent extends HTMLElement {
	connectedCallback() {
		this.root = createRoot(this);

		this.root.render(
			<StrictMode>
				<DistributorsTable />
			</StrictMode>,
			this
		);
	}
}

const ELEMENT_ID = 'clarity-solution-custom-element-distributors-table';

if (!customElements.get(ELEMENT_ID)) {
	customElements.define(ELEMENT_ID, WebComponent);
}
