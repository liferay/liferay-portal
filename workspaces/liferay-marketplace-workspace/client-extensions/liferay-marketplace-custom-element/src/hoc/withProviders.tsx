/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Component, Fragment} from 'react';

import ErrorBoundary from '../components/ErrorBoundary';
import Providers from '../providers';
import {MarketplaceProperties} from '../utils/attributes';

/**
 * @description due the lazy rendering, the context needs to be initialized
 * after the lazy component is loaded, this needs to be imported
 * to each lazy rendered component
 * @param WrappedComponent
 */

export default function withProviders<T extends object>(
	WrappedComponent: React.ComponentType<T>,
	properties?: {
		withErrorBoundary: boolean;
	}
) {
	return class extends Component<T & {properties: MarketplaceProperties}> {
		render() {
			const Wrapper = properties?.withErrorBoundary
				? ErrorBoundary
				: Fragment;

			return (
				<Wrapper>
					<Providers properties={this.props.properties}>
						<WrappedComponent {...this.props} />
					</Providers>
				</Wrapper>
			);
		}
	};
}
