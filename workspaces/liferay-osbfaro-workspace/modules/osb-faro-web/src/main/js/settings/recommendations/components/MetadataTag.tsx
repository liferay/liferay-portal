import ClayLabel from '@clayui/label';
import React from 'react';

const MetadataTag: React.FC<{value: string}> = ({value}) => (
	<ClayLabel
		className="metadata-tag-root"
		displayType="secondary"
		inverse
		size="lg"
	>
		{value}
	</ClayLabel>
);

export default MetadataTag;
