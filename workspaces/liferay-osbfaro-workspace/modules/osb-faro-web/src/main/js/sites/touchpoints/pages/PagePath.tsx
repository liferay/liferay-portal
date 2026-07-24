import PagePathCard from 'sites/touchpoints/components/sankey/PagePathCard';
import React from 'react';
import {RangeSelectors} from 'shared/types';

interface ITouchpointPathPageProps {
	rangeSelectors: RangeSelectors;
	selectedSegment?: {id: string};
}

const TouchpointPathPage = (props: ITouchpointPathPageProps) => (
	<div className="row">
		<div className="col-sm-12">
			<PagePathCard {...props} />
		</div>
	</div>
);

export default TouchpointPathPage;
