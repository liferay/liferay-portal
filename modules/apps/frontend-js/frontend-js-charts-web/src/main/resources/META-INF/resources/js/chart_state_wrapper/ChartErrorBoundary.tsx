/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

interface Props {
	children: React.ReactNode;
	fallback: React.ReactNode;
	onError?: (error: Error) => void;
}

interface State {
	hasError: boolean;
}

export default class ChartErrorBoundary extends React.Component<Props, State> {
	static getDerivedStateFromError(): State {
		return {hasError: true};
	}

	constructor(props: Props) {
		super(props);

		this.state = {hasError: false};
	}

	componentDidCatch(error: Error): void {
		this.props.onError?.(error);
	}

	render() {
		if (this.state.hasError) {
			return this.props.fallback;
		}

		return this.props.children;
	}
}
