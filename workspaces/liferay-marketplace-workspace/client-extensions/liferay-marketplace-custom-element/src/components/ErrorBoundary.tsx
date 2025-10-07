/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Component, ErrorInfo} from 'react';

import EmptyState from './EmptyState';

type ErrorBoundaryProps = {
	children: any;
	className?: string;
	errorMessage?: string;
};

type ErrorBoundaryState = {
	error?: Error;
	errorInfo?: ErrorInfo;
	hasError: boolean;
};

export default class ErrorBoundary extends Component<
	ErrorBoundaryProps,
	ErrorBoundaryState
> {
	constructor(props: ErrorBoundaryProps) {
		super(props);
		this.state = {error: undefined, errorInfo: undefined, hasError: false};
	}

	componentDidCatch(error: Error, errorInfo: ErrorInfo) {
		console.error('Error caught by ErrorBoundary:', error, errorInfo);

		this.setState({error, errorInfo, hasError: true});
	}

	render() {
		if (this.state.hasError) {
			return (
				<div className={this.props.className}>
					<EmptyState
						description={
							<>
								{this.props.errorMessage ||
									this.state.error?.message}

								<br />

								<ClayButton
									displayType="secondary"
									onClick={() => window.location.reload()}
								>
									Reload Content
								</ClayButton>
							</>
						}
						title="Oops... Something went wrong"
						type="BLANK"
					/>
				</div>
			);
		}

		return this.props.children;
	}
}
