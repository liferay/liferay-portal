/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {fetch, runScriptsInElement} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

export default function PermissionsOptions({formId, permissionsURL}) {
	const [loading, setLoading] = useState(true);
	const [content, setContent] = useState('');
	const isMounted = useIsMounted();

	useEffect(() => {
		fetch(permissionsURL)
			.then((response) => response.text())
			.then((content) => {
				if (isMounted()) {
					setContent(content);
					setLoading(false);
				}
			})
			.catch((error) => {
				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			});
	}, [isMounted, permissionsURL]);

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	return <PermissionsOptionsContent content={content} formId={formId} />;
}

class PermissionsOptionsContent extends React.Component {
	constructor(props) {
		super(props);

		this._ref = React.createRef();
	}

	componentDidMount() {
		if (this._ref.current) {
			runScriptsInElement(this._ref.current);

			const inputs = this._ref.current.querySelectorAll('input');

			inputs.forEach((input) => {
				input.setAttribute('form', this.props.formId);
			});

			const selects = this._ref.current.querySelectorAll('select');

			selects.forEach((select) => {
				select.setAttribute('form', this.props.formId);
			});
		}
	}

	shouldComponentUpdate() {
		return false;
	}

	render() {
		return (
			<div
				dangerouslySetInnerHTML={{__html: this.props.content}}
				ref={this._ref}
			/>
		);
	}
}
