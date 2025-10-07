/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React, {ChangeEvent, useState} from 'react';

import '../../../css/home/SearchBar.scss';

export default function SearchBar({
	searchResultsURL,
	userFirstName,
}: {
	searchResultsURL: string;
	userFirstName: string;
}) {
	const [term, setTerm] = useState('');

	const handleOnChange = (event: ChangeEvent<HTMLInputElement>): void => {
		setTerm(event.target.value);
	};

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		window.location.href = searchResultsURL + '?q=' + term;
	};

	return (
		<div className="align-items-center d-flex flex-column home-section p-4">
			<h1>Welcome, {userFirstName}!</h1>

			<div className="container mt-5">
				<div className="justify-content-center row">
					<div className="col-10 position-relative">
						<form onSubmit={handleSubmit}>
							<ClayInput
								className="search-bar"
								id="search"
								onChange={handleOnChange}
								placeholder={Liferay.Language.get('search')}
								type="text"
							/>

							<button
								className="btn btn-unstyled position-absolute search-button"
								type="submit"
							>
								<ClayIcon symbol="search" />
							</button>
						</form>
					</div>
				</div>
			</div>
		</div>
	);
}
