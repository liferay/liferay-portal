import BundleRouter from 'route-middleware/BundleRouter';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import {DEVELOPER_MODE} from 'shared/util/constants';
import {ENABLE_BLOCKLIST_KEYWORDS} from 'shared/util/feature-flags';
import {Route, Routes as RouterRoutes} from 'react-router-dom';

const EventBlockList = lazy(
	() =>
		import(/* webpackChunkName: "BlockList" */ '../events/pages/BlockList')
);

const Overview = lazy(
	() => import(/* webpackChunkName: "DefinitionsOverview" */ './Overview')
);

const IndividualAttributes = lazy(
	() =>
		import(

			/* webpackChunkName: "DefinitionsIndividualAttributes" */ './IndividualAttributes'
		)
);

const InterestTopics = lazy(
	() =>
		import(

			/* webpackChunkName: "DefinitionsInterestTopics" */ './InterestTopics'
		)
);

const TrackedBehaviors = lazy(
	() =>
		import(/* webpackChunkName: "TrackedBehaviors" */ './TrackedBehaviors')
);

const Search = lazy(
	() => import(/* webpackChunkName: "DefinitionsSearch" */ './search/Search')
);

const Events = lazy(
	() =>
		import(

			/* webpackChunkName: "DefinitionsEvents" */ '../events/pages/Events'
		)
);

const EventAttributes = lazy(
	() =>
		import(

			/* webpackChunkName: "DefinitionsEvents" */ '../event-attributes/pages/EventAttributes'
		)
);

const EventView = lazy(
	() =>
		import(

			/* webpackChunkName: "DefinitionsEventView" */ '../events/pages/View'
		)
);

const AttributeView = lazy(
	() =>
		import(

			/* webpackChunkName: "DefinitionsEventAttributesView" */ '../event-attributes/pages/AttributeView'
		)
);

interface IDefinitionsProps extends React.HTMLAttributes<HTMLDivElement> {}

const Definitions: React.FC<IDefinitionsProps> = () => (
	<Suspense fallback={<Loading />}>
		<RouterRoutes>
			<Route element={<BundleRouter data={Overview} />} index />

			{ENABLE_BLOCKLIST_KEYWORDS && (
				<Route
					element={<BundleRouter data={InterestTopics} />}
					path="interest-topics"
				/>
			)}

			<Route
				element={<BundleRouter data={IndividualAttributes} />}
				path="individual-attributes"
			/>

			<Route element={<BundleRouter data={Search} />} path="search" />

			{DEVELOPER_MODE && (

				// TODO: LRAC-4511 Remove when new TrackedBehavior page exists

				<Route
					element={<BundleRouter data={TrackedBehaviors} />}
					path="behaviors"
				/>
			)}

			<Route
				element={<BundleRouter data={AttributeView} />}
				path="event-attributes/:attributeId"
			/>

			<Route
				element={<BundleRouter data={Events} />}
				path="events/custom/*"
			/>

			<Route
				element={<BundleRouter data={Events} />}
				path="events/default/*"
			/>

			<Route
				element={<BundleRouter data={EventAttributes} />}
				path="event-attributes/local/*"
			/>

			<Route
				element={<BundleRouter data={EventAttributes} />}
				path="event-attributes/global/*"
			/>

			<Route
				element={<BundleRouter data={EventBlockList} />}
				path="events/block-list/*"
			/>

			<Route
				element={<BundleRouter data={EventView} />}
				path="events/:eventId"
			/>

			<Route element={<ErrorPage />} path="*" />
		</RouterRoutes>
	</Suspense>
);

export default Definitions;
