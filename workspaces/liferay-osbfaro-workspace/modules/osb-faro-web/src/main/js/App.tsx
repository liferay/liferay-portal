import ChannelProvider from 'shared/context/channel';
import client from 'shared/apollo/client';
import React, {useEffect, useState} from 'react';
import RouterLink from 'shared/components/RouterLink';
import store from 'shared/store';
import UnassignedSegmentsProvider from 'shared/context/unassignedSegments';
import {
	ApolloProvider,
	ApolloProvider as ApolloProviderHooks,
} from '@apollo/client';

import {ClayIconSpriteContext} from '@clayui/icon';
import {CLAY_ICONS_SPRITEMAP} from 'shared/components/Icon';
import {ClayLinkContext} from '@clayui/link';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {RouterProvider} from 'react-router-dom';
import {OnboardingContext} from 'shared/context/onboarding';
import {Provider} from 'react-redux';
import {router} from './routes';
import {saveState} from 'shared/store/local-storage';
import {throttle} from 'lodash';

const App = () => {
	const [onboardingTriggered, setOnboardingTriggered] = useState(false);

	useEffect(() => {
		store.subscribe(throttle(() => saveState(store.getState()), 1000));
	}, []);

	return (
		<ApolloProvider client={client}>
			<ApolloProviderHooks client={client}>
				<Provider store={store}>
					<ClayIconSpriteContext.Provider
						value={CLAY_ICONS_SPRITEMAP}
					>
						<ClayLinkContext.Provider value={RouterLink}>
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
												<RouterProvider
													router={router}
												/>
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
