/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import ClayIcon from '@clayui/icon';
import {openToast} from 'frontend-js-components-web';
import {PropTypes} from 'prop-types';
import React, {Component} from 'react';

import {sub} from '../../utils/language.es';

class ErrorBoundary extends Component {
	static propTypes = {
		component: PropTypes.string,
	};

	state = {
		hasError: false,
		message: this.props.component
			? sub(
					Liferay.Language.get(
						'an-error-has-occurred-and-we-were-unable-to-load-x'
					),
					[this.props.component]
				)
			: Liferay.Language.get(
					'an-error-has-occurred-and-we-were-unable-to-load-the-results'
				),
	};

	static getDerivedStateFromError() {
		return {hasError: true};
	}

	/**
	 * Displays a notification toast when the component is unable to load.
	 */
	componentDidCatch() {
		if (this.props.toast) {
			openToast({
				message: this.state.message,
				type: 'danger',
			});
		}
	}

	render() {
		return this.state.hasError
			? !this.props.toast && (
					<ClayEmptyState
						description={this.state.message}
						image={
							<ClayIcon
								className="c-empty-state-icon c-empty-state-icon-empty-state"
								illustration
								symbol="empty-state"
							/>
						}
						title={Liferay.Language.get('unable-to-load-content')}
					/>
				)
			: this.props.children;
	}
}

export default ErrorBoundary;
