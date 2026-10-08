/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import moment from 'moment/min/moment-with-locales';
import React, {useContext, useEffect} from 'react';

import {removeEmptyValues} from '../../utils/data';
import Color from '../color/Color';
import {SidebarContext} from '../sidebar/SidebarContext';

export default function List({data, field, summary, totalEntries, type}) {
	const {portletNamespace, toggleSidebar} = useContext(SidebarContext);

	useEffect(() => {
		const firstLi = document.querySelector(
			`#${portletNamespace}_entry_slidebar_0`
		);
		if (firstLi) {
			firstLi.focus();
		}
	});

	const formatDate = (field, isDateTime) => {
		const locale = themeDisplay.getLanguageId().split('_', 1).join('');
		const date = moment(field).locale(locale).format('L');

		if (!isDateTime) {
			return date;
		}

		const time = moment(field).locale(locale).format('LT');

		return `${date} ${time}`;
	};

	const formatDocument = (field) => {
		const {title: documentTitle, url: documentURL} = JSON.parse(field);

		return (
			<div>
				<a
					className="document-title-link"
					download={documentTitle}
					href={documentURL}
				>
					{documentTitle}
				</a>
			</div>
		);
	};

	const checkType = (field, type) => {
		switch (type) {
			case 'color':
				return <Color hexColor={field} />;
			case 'date':
				return formatDate(field);
			case 'date_time':
				return formatDate(field, true);
			case 'document_library':
				return formatDocument(field);
			case 'image':
				return formatDocument(field);
			default:
				return field;
		}
	};

	data = removeEmptyValues(data);

	return (
		<div className="field-list">
			<ul className="entries-list">
				{Array.isArray(data) &&
					data.map((value, index) => (
						<li
							data-index={index}
							id={`${portletNamespace}_entry_${
								field ? field.name : 'slidebar'
							}_${index}`}
							key={index}
							tabIndex={0}
						>
							{checkType(value, type)}
						</li>
					))}

				{data.length === 5 && totalEntries > 5 ? (
					<li
						id={`${portletNamespace}${field.name}-see-more`}
						key="see-more"
					>
						<ClayButton
							displayType="link"
							onClick={() =>
								toggleSidebar(
									field,
									summary,
									totalEntries,
									type
								)
							}
						>
							{Liferay.Language.get('see-all-entries')}
						</ClayButton>
					</li>
				) : null}
			</ul>
		</div>
	);
}
