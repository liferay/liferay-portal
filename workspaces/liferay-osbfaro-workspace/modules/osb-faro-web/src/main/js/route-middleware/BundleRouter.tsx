import React from 'react';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useLocation, useParams} from 'react-router-dom';
import {useQueryParams} from 'shared/hooks/useQueryParams';

interface IBundleRouterProps {
	componentProps?: Record<string, unknown>;
	data: React.ComponentType<any>;
	destructured?: boolean;
}

const BundleRouter = ({
	componentProps = {},
	data: Component,
	destructured = true,
}: IBundleRouterProps) => {
	const history = useHistoryAdapter();
	const location = useLocation();
	const params = useParams();
	const query = useQueryParams();

	if (destructured) {
		return (
			<Component
				history={history}
				location={location}
				{...query}
				{...params}
				{...componentProps}
			/>
		);
	}

	return (
		<Component
			history={history}
			location={location}
			router={{params, query}}
			{...componentProps}
		/>
	);
};

export default BundleRouter;
