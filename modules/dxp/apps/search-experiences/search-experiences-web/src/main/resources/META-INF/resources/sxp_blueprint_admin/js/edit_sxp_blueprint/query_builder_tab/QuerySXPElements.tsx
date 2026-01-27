/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useState} from 'react';

import CustomPanel from '../../shared/CustomPanel';

// @ts-ignore

import JSONSXPElement from '../../shared/JSONSXPElement';

// @ts-ignore

import SXPElement from '../../shared/sxp_element/index';
import {SXP_ELEMENT_PREFIX} from '../../utils/constants';
import {setStorageAddSXPElementSidebar} from '../../utils/sessionStorage';

// @ts-ignore

import isCustomJSONSXPElement from '../../utils/sxp_element/is_custom_json_sxp_element';
import {
	IElementInstance,
	IIndexField,
	ISearchableType,
} from '../../utils/types';

function QuerySXPElements({
	elementInstances,
	entityJSON,
	errors = [],
	indexFields,
	isIndexCompany,
	isSubmitting,
	onBlur,
	onChange,
	onChangeAddSXPElementVisibility,
	onDeleteSXPElement,
	searchableTypes,
	setFieldTouched,
	setFieldValue,
	touched = [],
}: {
	elementInstances: IElementInstance[];
	entityJSON: {[key: string]: any};
	errors: (
		| {[uiConfigurationValues: string]: {[field: string]: string}}
		| undefined
	)[];
	indexFields: IIndexField[];
	isIndexCompany: boolean;
	isSubmitting: boolean;
	onBlur: (event: React.FocusEvent<any>) => void;
	onChange: (event: React.ChangeEvent<any>) => void;
	onChangeAddSXPElementVisibility: (visible?: boolean) => void;
	onDeleteSXPElement: (id: number) => void;
	searchableTypes: ISearchableType[];
	setFieldTouched: (field: string, touched?: boolean) => void;
	setFieldValue: (field: string, value: any) => void;
	touched: (
		| {[uiConfigurationValues: string]: {[field: string]: boolean}}
		| undefined
	)[];
}) {
	const [collapseAll, setCollapseAll] = useState(false);

	const _handleClickAddQueryElement = () => {
		setStorageAddSXPElementSidebar();

		onChangeAddSXPElementVisibility();
	};

	return (
		<CustomPanel
			classNames="query-sxp-elements"
			headerContent={
				<>
					<ClayButton
						aria-label={Liferay.Language.get('collapse-all')}
						className="c-mr-3 text-3 text-secondary text-weight-bold"
						displayType="unstyled"
						onClick={() => setCollapseAll(!collapseAll)}
					>
						{collapseAll
							? Liferay.Language.get('expand-all')
							: Liferay.Language.get('collapse-all')}
					</ClayButton>

					<ClayTooltipProvider>
						<ClayButton
							aria-label={Liferay.Language.get(
								'add-query-element'
							)}
							className="c-mr-2"
							displayType="primary"
							monospaced
							onClick={_handleClickAddQueryElement}
							size="sm"
							title={Liferay.Language.get('add-query-element')}
						>
							<ClayIcon symbol="plus" />
						</ClayButton>
					</ClayTooltipProvider>
				</>
			}
			title={Liferay.Language.get('query-elements')}
		>
			<>
				{!elementInstances.length ? (
					<span className="text-4 text-secondary">
						{Liferay.Language.get(
							'add-elements-to-optimize-search-results-for-your-use-cases'
						)}
					</span>
				) : (
					elementInstances.map(
						({id, sxpElement, uiConfigurationValues}, index) => {
							return isCustomJSONSXPElement(
								uiConfigurationValues
							) ? (
								<JSONSXPElement
									collapseAll={collapseAll}
									error={errors[index]}
									id={id}
									index={index}
									isSubmitting={isSubmitting}
									key={id}
									onDeleteSXPElement={onDeleteSXPElement}
									prefixedId={`${SXP_ELEMENT_PREFIX.QUERY}-${index}`}
									setFieldTouched={setFieldTouched}
									setFieldValue={setFieldValue}
									sxpElement={sxpElement}
									touched={touched[index]}
									uiConfigurationValues={
										uiConfigurationValues
									}
								/>
							) : (
								<SXPElement
									collapseAll={collapseAll}
									entityJSON={entityJSON}
									error={errors[index]}
									id={id}
									index={index}
									indexFields={indexFields}
									isIndexCompany={isIndexCompany}
									isSubmitting={isSubmitting}
									key={id}
									onBlur={onBlur}
									onChange={onChange}
									onDeleteSXPElement={onDeleteSXPElement}
									prefixedId={`${SXP_ELEMENT_PREFIX.QUERY}-${index}`}
									searchableTypes={searchableTypes}
									setFieldTouched={setFieldTouched}
									setFieldValue={setFieldValue}
									sxpElement={sxpElement}
									touched={touched[index]}
									uiConfigurationValues={
										uiConfigurationValues
									}
								/>
							);
						}
					)
				)}
			</>
		</CustomPanel>
	);
}

export default React.memo(QuerySXPElements);
