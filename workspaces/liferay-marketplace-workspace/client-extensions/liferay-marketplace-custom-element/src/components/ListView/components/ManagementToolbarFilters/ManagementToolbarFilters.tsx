/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useRef,
	useState,
} from 'react';
import {useLocation, useNavigate, useParams} from 'react-router-dom';
import useSWR from 'swr';

import CreateFilters from '../../../../core/CreateFilters';
import i18n from '../../../../i18n';
import {FilterSchema, RendererFields} from '../../../../schema/filters';
import fetcher from '../../../../services/fetcher';
import {safeJSONParse} from '../../../../utils/util';
import Form from '../../../Form/index';
import {ListViewContext, ListViewTypes} from '../../hooks/ListViewContext';
import useUpdateUrlParams from '../../hooks/useUpdateUrlParams';

import './ManagementToolbarFilters.scss';

type ManagementToolbarFilterProps = {
	filterSchema?: FilterSchema;
};

export type Option = {label: string; value: string};

type FilterBodyProps = {
	filterSchema: FilterSchema | undefined;
	setIsVisible: React.Dispatch<React.SetStateAction<boolean>>;
};

const FilterBody: React.FC<FilterBodyProps> = ({
	filterSchema,
	setIsVisible,
}) => {
	const [listViewContext, dispatch] = useContext(ListViewContext);
	const location = useLocation();
	const navigate = useNavigate();
	const params = useParams();
	const updateUrlParams = useUpdateUrlParams();

	const fields = useMemo(
		() => filterSchema?.fields as RendererFields[],
		[filterSchema?.fields]
	);

	const initialFilters = useMemo(() => {
		const initialValues: {[key: string]: string} = {};

		for (const field of fields) {
			initialValues[field.name] = '';
		}

		return initialValues;
	}, [fields]);

	const [form, setForm] = useState(() => ({
		...initialFilters,
		...listViewContext.filters.filter,
	}));

	const clearButtonDisabled = Object.values(form).every(
		(value) => !value || !value.length
	);

	const onClear = () => setForm(initialFilters);

	const onChange = (event: any) => {
		const {
			target: {name, options, type},
		} = event;

		let {value} = event.target;

		if (type === 'date-range') {
			value = [
				{
					label: value,
					value,
				},
			];
		}
		else if (type === 'select-one') {
			value = [
				{
					label: options.item(options.selectedIndex).label,
					value: Number(value) || value,
				},
			];
		}

		if (Array.isArray(value)) {
			if (!value[0]) {
				value = '';
			}
			else if (typeof value[0] === 'object') {
				value = !value[0].label ? '' : value;
			}
		}

		setForm({
			...form,
			[name]: value,
		});
	};

	const handleRemoveItemFromFilter = useCallback(() => {
		const searchParams = new URLSearchParams(location.search);
		searchParams.delete('filter');
		searchParams.delete('filterSchema');

		return navigate({
			search: `?${searchParams.toString()}`,
		});
	}, [location.search, navigate]);

	const paramsMemoized = useMemo(() => {
		return JSON.stringify({...params});
	}, [params]);

	const fieldsMemoized = useMemo(() => filterSchema?.fields, [filterSchema]);

	const {data: fieldOptions = {}, isLoading} = useSWR(
		filterSchema?.fields?.length ? `/filter-${filterSchema?.name}` : null,
		async () => {
			const parameters = safeJSONParse(paramsMemoized, {});

			const fieldsWithResource = fieldsMemoized?.filter(
				({resource}) => resource
			);

			const _fieldOptions: any = {};

			if (fieldsWithResource) {
				await Promise.all(
					fieldsWithResource.map((field) =>
						fetcher(
							(typeof field.resource === 'function'
								? field.resource(parameters)
								: field.resource) as string
						)
					)
				).then((results) =>
					results.forEach((result, index) => {
						const field = fieldsWithResource[index];

						if (field.transformData) {
							const parsedValue = field.transformData(result);

							_fieldOptions[field.name] = parsedValue;
						}
					})
				);
			}

			return _fieldOptions;
		}
	);

	const onApply = useCallback(() => {
		const filterCleaned = CreateFilters.removeEmptyFilter(form);

		const entries = Object.keys(filterCleaned).map((key) => ({
			label: fields?.find(({name}) => name === key)?.label,
			name: key,
			value: filterCleaned[key],
		}));

		const filters = Object.keys(filterCleaned).map((key) => ({
			name: key,
			value: Array.isArray(filterCleaned[key])
				? (filterCleaned as any)[key].map((options: Option) =>
						options?.value
							? options?.value
							: options?.label || options
					)
				: filterCleaned[key],
		}));

		const formattedFilter = filters.reduce(
			(previousValue, currentValue) => {
				return {
					...previousValue,
					[currentValue.name]: currentValue.value,
				};
			},
			{}
		);

		if (filterSchema) {
			updateUrlParams({
				filter: JSON.stringify(formattedFilter),
				filterSchema: filterSchema?.name as string,
				page: '1',
			});
		}

		if (!Object.keys(formattedFilter).length) {
			handleRemoveItemFromFilter();
		}

		dispatch({
			payload: {filters: {entries, filter: filterCleaned}},
			type: ListViewTypes.SET_FILTERS,
		});

		setIsVisible(false);
	}, [
		dispatch,
		fields,
		filterSchema,
		form,
		handleRemoveItemFromFilter,
		setIsVisible,
		updateUrlParams,
	]);

	useEffect(() => {
		const searchParams = new URLSearchParams(location.search);

		if (!searchParams.get('filter')) {
			setForm(initialFilters);
		}
	}, [initialFilters, location.search]);

	return (
		<div className="align-content-between d-flex flex-column">
			<ClayDropDown.Section>
				<div className="management-toolbar-body">
					<div className="dropdown-filter-content" tabIndex={1}>
						<Form.Renderer
							fieldOptions={fieldOptions}
							fields={fields}
							filterSchema={filterSchema?.name as string}
							form={form}
							isLoading={isLoading}
							onApply={onApply}
							onChange={onChange}
						/>
					</div>
				</div>
			</ClayDropDown.Section>

			<ClayDropDown.Section className="d-flex dropdown-footer justify-content-center">
				<ClayButton className="mt-2" onClick={onApply}>
					{i18n.translate('apply')}
				</ClayButton>

				<ClayButton
					className="ml-3 mt-2"
					disabled={clearButtonDisabled}
					displayType="secondary"
					onClick={onClear}
				>
					{i18n.translate('clear')}
				</ClayButton>
			</ClayDropDown.Section>
		</div>
	);
};

const ManagementToolbarFilter: React.FC<ManagementToolbarFilterProps> = ({
	filterSchema,
}) => {
	const buttonRef = useRef<HTMLButtonElement | null>(null);

	const [isVisible, setIsVisible] = useState(false);

	const hasOneFilter = filterSchema?.fields?.length === 1;

	const handleExpand = (
		event: React.MouseEvent<HTMLButtonElement, MouseEvent>
	) => {
		buttonRef.current = event.target as HTMLButtonElement;

		setIsVisible((isVisible) => !isVisible);
	};

	return (
		<>
			<div className="align-items-center d-flex justify-content-between">
				<ClayButton
					className="align-items-center btn-secondary d-flex justify-content-between management-toolbar-filter-button ml-3 mr-2 px-2"
					displayType="unstyled"
					onClick={handleExpand}
				>
					<ClayIcon className="mr-2" symbol="filter" />
					{i18n.translate('filter')}
				</ClayButton>
			</div>

			{isVisible && (
				<ClayDropDown.Menu
					active={isVisible}
					alignElementRef={buttonRef}
					alignmentPosition={Align.BottomLeft}
					className={classNames('management-toolbar-dropdown', {
						'dropdown-management-toolbar-small': hasOneFilter,
					})}
					closeOnClickOutside
					onActiveChange={() =>
						setIsVisible((isVisible) => !isVisible)
					}
				>
					<div className="management-toolbar-dropdown-body">
						<FilterBody
							filterSchema={filterSchema}
							setIsVisible={setIsVisible}
						/>
					</div>
				</ClayDropDown.Menu>
			)}
		</>
	);
};

export default ManagementToolbarFilter;
