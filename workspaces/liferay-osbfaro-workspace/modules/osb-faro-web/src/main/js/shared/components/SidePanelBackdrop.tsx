import React from 'react';

interface ISidePanelBackdropProps {
	onClick: () => void;
}

/**
 * Dims the page behind a side panel shown as an overlay and closes it on
 * click. Render it inside the panel: Clay makes everything outside an open
 * mobile panel inert, which would swallow the click.
 */
const SidePanelBackdrop: React.FC<ISidePanelBackdropProps> = ({onClick}) => (
	<div
		className="modal-backdrop show side-panel-backdrop"
		data-testid="side-panel-backdrop"
		onClick={onClick}
	/>
);

export default SidePanelBackdrop;
