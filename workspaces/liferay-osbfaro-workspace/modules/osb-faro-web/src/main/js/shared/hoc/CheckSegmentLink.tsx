import * as API from 'shared/api';
import React, {useEffect, useState} from 'react';
import {matchPath} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {WrapSafeResults} from 'shared/hoc/util';

type History = {
	replace: (path: string) => void;
};

type Location = {
	pathname: string;
};

interface IWrappedComponentProps {
	groupId: string;
	history: History;
	location: Location;
}

const checkSegmentLink =
	(WrappedComponent: React.ComponentType<IWrappedComponentProps>) =>
	({
		groupId,
		history,
		location,
		...otherProps
	}: IWrappedComponentProps & {[key: string]: any}) => {
		const [error, setError] = useState();
		const [loading, setLoading] = useState(false);

		useEffect(() => {
			const segment = matchPath(
				{
					end: true,
					path: Routes.CONTACTS_SEGMENT,
				},
				location.pathname
			);

			if (segment && !segment.params.channelId) {
				setLoading(true);

				API.individualSegment
					.fetch({groupId, segmentId: segment.params.id})
					.then(({channelId, id}) => {
						setLoading(false);

						history.replace(
							toRoute(Routes.CONTACTS_SEGMENT, {
								channelId,
								groupId,
								id,
							})
						);
					})
					.catch((err) => {
						setLoading(false);
						setError(err);
					});
			}
		}, []);

		return (
			<WrapSafeResults error={error} loading={loading} page pageDisplay>
				<WrappedComponent
					{...otherProps}
					groupId={groupId}
					history={history}
					location={location}
				/>
			</WrapSafeResults>
		);
	};

export default checkSegmentLink;
