/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

const PrettyPrintArray = ({value}) => (
	<span>
		[
		{value.map((item, i) => (
			<React.Fragment key={i}>
				<span className="badge badge-pill badge-secondary">{item}</span>

				{i + 1 !== value.length && ', '}
			</React.Fragment>
		))}
		]
	</span>
);

/**
 * Used for displaying hidden values for easier debugging. This component
 * should only be used during development.
 */
const FormValueDebugger = ({values}) => (
	<div
		className="alert alert-dark"
		style={{
			margin: '0 auto',
			maxWidth: '1000px',
		}}
	>
		<p>
			<strong>Form hidden values for debugging</strong>

			{
				' (Only the values from the frontend component. There are others defined in the JSP)'
			}
		</p>

		<table className="table table-bordered table-striped">
			<thead>
				<tr>
					<th>Name</th>

					<th>Value</th>
				</tr>
			</thead>

			<tbody>
				{values.map(({name, value}, index) => (
					<tr key={`${index}-${name}`}>
						<td>{name}</td>

						<td>
							{Array.isArray(value) ? (
								<PrettyPrintArray value={value} />
							) : (
								value
							)}
						</td>
					</tr>
				))}
			</tbody>
		</table>
	</div>
);

export default FormValueDebugger;
