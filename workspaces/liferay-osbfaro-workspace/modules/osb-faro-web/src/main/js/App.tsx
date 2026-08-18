import AlertFeed from 'shared/components/AlertFeed';
import BundleRouter from './route-middleware/BundleRouter';
import ChannelProvider from 'shared/context/channel';
import client from 'shared/apollo/client';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import ModalRenderer from 'shared/components/ModalRenderer';
import React, {lazy, Suspense, useEffect, useState} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import store from 'shared/store';
import UnassignedSegmentsProvider from 'shared/context/unassignedSegments';
import {
	ApolloProvider,
	ApolloProvider as ApolloProviderHooks,
} from '@apollo/client';

import {ClayIconSpriteContext} from '@clayui/icon';
import {ClayLinkContext} from '@clayui/link';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {close, modalTypes, open} from 'shared/actions/modals';
import {ENABLE_ADD_TRIAL_WORKSPACE} from 'shared/util/constants';
import {
	Link,
	matchPath,
	Route,
	BrowserRouter as Router,
	Switch,
	useLocation,
} from 'react-router-dom';
import {OnboardingContext} from 'shared/context/onboarding';
import {Pendo} from 'shared/util/pendo';
import {Project} from 'shared/util/records';
import {Provider, useSelector} from 'react-redux';
import {Routes} from 'shared/util/router';
import {saveState} from 'shared/store/local-storage';
import {throttle} from 'lodash';
import {useFetchCurrentUser} from 'shared/hooks/useCurrentUser';

// Workspaces

const AddWorkspace = lazy(
	() =>
		import(

			/* webpackChunkName: "AddWorkspace" */ './shared/pages/AddWorkspace'
		)
);
const SelectWorkspaceAccount = lazy(
	() =>
		import(

			/* webpackChunkName: "SelectWorkspaceAccount" */ './shared/pages/SelectWorkspaceAccount'
		)
);
const Workspaces = lazy(
	() =>
		import(/* webpackChunkName: "Workspaces" */ './shared/pages/Workspaces')
);

// WorkspaceLayer

const WorkspaceLayer = lazy(
	() =>
		import(

			/* webpackChunkName: "WorkspaceLayer" */ './shared/components/WorkspaceLayer'
		)
);

// Other

const OAuthReceive = lazy(
	() =>
		import(

			/* webpackChunkName: "OAuthReceive" */ './settings/pages/OAuthReceive'
		)
);

const RoutesContainer = ({children}: {children: React.ReactNode}) => {
	const location = useLocation();

	const matchingPath = matchPath<any>(location.pathname, {
		path: Routes.WORKSPACE_WITH_ID,
	});

	const groupId = matchingPath?.params.groupId ?? '0';

	const project: Project = useSelector<any, any>((state) =>
		state.getIn(['projects', groupId, 'data'])
	);

	const {data: currentUser, loading} = useFetchCurrentUser(groupId);

	useEffect(() => {
		if (currentUser?.id && project?.corpProjectName) {
			const pendo = new Pendo();

			pendo.initialize({currentUser, project});
		}
	}, [currentUser?.id, project?.corpProjectName]);

	if (loading) {
		return <Loading />;
	}

	if ((location?.state as any)?.notFoundError) {
		return <ErrorPage />;
	}

	return children as React.ReactElement;
};

const App = () => {
	const [onboardingTriggered, setOnboardingTriggered] = useState(false);

	useEffect(() => {
		store.subscribe(throttle(() => saveState(store.getState()), 1000));
	}, []);

	const handleUserConfirmation = (
		message: string,
		callback: (confirmed: boolean) => void
	) => {
		store.dispatch(
			open(modalTypes.CONFIRMATION_MODAL, {
				cancelMessage: Liferay.Language.get('stay-on-page'),
				message,
				modalVariant: 'modal-warning',
				onClose: () => {
					callback(false);

					store.dispatch(close());
				},
				onSubmit: () => {
					callback(true);
				},
				submitButtonDisplay: 'warning',
				submitMessage: Liferay.Language.get('leave-page'),
				title: Liferay.Language.get('unsaved-changes'),
				titleIcon: 'warning-full',
			})
		);
	};

	return (
		<ApolloProvider client={client}>
			<ApolloProviderHooks client={client}>
				<Provider store={store}>
					<ClayIconSpriteContext.Provider value="/o/osb-faro-web/dist/sprite.svg">
						<ClayLinkContext.Provider
							value={({
								children,
								externalLink = false,
								href,
								...otherProps
							}: {
								children?: React.ReactNode;
								externalLink?: boolean;
								href?: string;
							}) => {
								if (href?.startsWith('http') || externalLink) {
									return (
										<a {...otherProps} href={href}>
											{children}
										</a>
									);
								}

								return (
									<Link {...otherProps} to={href || ''}>
										{children}
									</Link>
								);
							}}
						>
							<UnassignedSegmentsProvider>
								<OnboardingContext.Provider
									value={{
										onboardingTriggered,
										setOnboardingTriggered: () =>
											setOnboardingTriggered(true),
									}}
								>
									<ChannelProvider>
										<ClayTooltipProvider>
											<div>
												<Router
													getUserConfirmation={
														handleUserConfirmation
													}
												>
													<RoutesContainer>
														<AlertFeed />

														<ModalRenderer />

														<Suspense
															fallback={
																<Loading />
															}
														>
															<Switch>
																<BundleRouter
																	data={
																		Workspaces
																	}
																	exact
																	path={
																		Routes.BASE
																	}
																/>

																<BundleRouter
																	data={
																		Workspaces
																	}
																	exact
																	path={
																		Routes.WORKSPACES
																	}
																/>

																<BundleRouter
																	data={
																		SelectWorkspaceAccount
																	}
																	exact
																	path={
																		Routes.WORKSPACE_ADD
																	}
																/>

																{ENABLE_ADD_TRIAL_WORKSPACE && (
																	<BundleRouter
																		data={
																			AddWorkspace
																		}
																		exact
																		path={
																			Routes.WORKSPACE_ADD_TRIAL
																		}
																	/>
																)}

																<BundleRouter
																	data={
																		AddWorkspace
																	}
																	exact
																	path={
																		Routes.WORKSPACE_ADD_WITH_CORP_PROJECT_UUID
																	}
																/>

																<BundleRouter
																	data={
																		SelectWorkspaceAccount
																	}
																	exact
																	path={
																		Routes.WORKSPACE_SELECT_ACCOUNT
																	}
																/>

																<BundleRouter
																	data={
																		OAuthReceive
																	}
																	exact
																	path={
																		Routes.OAUTH_RECEIVE
																	}
																/>

																<Route
																	component={
																		Loading
																	}
																	path={
																		Routes.LOADING
																	}
																/>

																<WorkspaceLayer />

																<RouteNotFound />
															</Switch>
														</Suspense>
													</RoutesContainer>
												</Router>
											</div>
										</ClayTooltipProvider>
									</ChannelProvider>
								</OnboardingContext.Provider>
							</UnassignedSegmentsProvider>
						</ClayLinkContext.Provider>
					</ClayIconSpriteContext.Provider>
				</Provider>
			</ApolloProviderHooks>
		</ApolloProvider>
	);
};

export default App;
