/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {useControlledState} from '@liferay/layout-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useMemo} from 'react';

import {SelectField} from '../../../../../../app/components/fragment_configuration_fields/SelectField';
import {FORM_MAPPING_SOURCES} from '../../../../../../app/config/constants/formMappingSources';
import {LAYOUT_TYPES} from '../../../../../../app/config/constants/layoutTypes';
import {config} from '../../../../../../app/config/index';
import {
	useDispatch,
	useSelector,
	useSelectorRef,
} from '../../../../../../app/contexts/StoreContext';
import selectSegmentsExperienceId from '../../../../../../app/selectors/selectSegmentsExperienceId';
import {formIsMapped} from '../../../../../../app/utils/formIsMapped';
import {hasLocalizationSelect} from '../../../../../../app/utils/hasLocalizationSelect';
import {openAddLocalizationSelect} from '../../../../../../app/utils/openAddLocalizationSelect';
import {openInfoFieldSelector} from '../../../../../../common/openInfoFieldSelector';

export default function FormMappingOptions({
	hideLabel = false,
	item,
	onValueSelect,
}) {
	const segmentsExperienceId = useSelector(selectSegmentsExperienceId);

	const dispatch = useDispatch();

	const formTypes = useMemo(() => getTypes(), []);

	const [classNameId, setClassNameId] = useControlledState(
		item.config.classNameId
	);
	const [classTypeId, setClassTypeId] = useControlledState(
		item.config.classTypeId
	);

	const fragmentEntryLinksRef = useSelectorRef(
		(state) => state.fragmentEntryLinks
	);

	const selectedType = formTypes.find(({value}) => value === classNameId);

	const onSelect = useCallback(
		(classNameId, classTypeId) => {
			const type = formTypes.find(({value}) => value === classNameId);

			const resetMapping = () => {
				setClassNameId(item.config.classNameId);
				setClassTypeId(item.config.classTypeId);
			};

			const saveMapping = (fields = []) =>
				onValueSelect(
					{
						classNameId,
						classTypeId,
						formConfig: FORM_MAPPING_SOURCES.otherContentType,
						formType: item.config.formType
							? item.config.formType
							: 'simple',
					},
					fields.map(({uniqueId}) => uniqueId)
				);

			if (classNameId !== '0') {
				openInfoFieldSelector({
					formItemId: item.itemId,
					itemType: type.className,
					onCancel: resetMapping,
					onSave: (fields) => {
						saveMapping(fields);

						if (
							fields.some((field) => field.localizable) &&
							!hasLocalizationSelect(
								fragmentEntryLinksRef.current
							)
						) {
							openAddLocalizationSelect({
								dispatch,
								formId: item.itemId,
							});
						}
					},
					segmentsExperienceId,
				});
			}
			else {
				saveMapping();
			}
		},
		[
			formTypes,
			fragmentEntryLinksRef,
			dispatch,
			item,
			onValueSelect,
			setClassNameId,
			setClassTypeId,
			segmentsExperienceId,
		]
	);

	return (
		<>
			<SelectField
				className="mb-2"
				field={{
					hideLabel,
					label: Liferay.Language.get('content-type'),
					name: 'classNameId',
					typeOptions: {
						validValues: formTypes.map(({label, value}) => ({
							label,
							value,
						})),
					},
				}}
				onValueSelect={(_name, classNameId) => {
					setClassNameId(classNameId);

					const type = formTypes.find(
						({value}) => value === classNameId
					);

					if (type?.subtypes?.length) {
						return;
					}

					onSelect(classNameId, classTypeId);
				}}
				value={classNameId}
			/>

			{formIsMapped(item) ? (
				<ClayButton
					displayType="secondary"
					onClick={() => onSelect(classNameId, classTypeId)}
					size="xs"
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="forms" />
					</span>

					{Liferay.Language.get('manage-form-fields')}
				</ClayButton>
			) : null}

			{selectedType?.subtypes?.length > 0 && (
				<SelectField
					field={{
						hideLabel,
						label: Liferay.Language.get('subtype'),
						name: 'classTypeId',
						typeOptions: {
							validValues: [
								{
									label: Liferay.Language.get('none'),
									value: '',
								},
								...selectedType.subtypes,
							],
						},
					}}
					onValueSelect={(_name, classTypeId) => {
						setClassTypeId(classTypeId);

						onSelect(classNameId, classTypeId);
					}}
					value={classTypeId}
				/>
			)}
		</>
	);
}

function getTypes() {
	let formTypes = [
		{
			label: Liferay.Language.get('none'),
			value: '0',
		},
		...config.formTypes
			.filter((formType) => !formType.isRestricted)
			.map((formType) => ({
				...formType,
				subtypes: formType.subtypes.filter(
					(subtype) => !subtype.isRestricted
				),
			})),
	];

	if (config.layoutType === LAYOUT_TYPES.display) {
		formTypes = formTypes.map((formType) => {
			if (formType.value === config.selectedMappingTypes.type.id) {
				return {
					...formType,
					label: sub(
						Liferay.Language.get('x-default'),
						config.selectedMappingTypes.type.label
					),
				};
			}

			return formType;
		});
	}

	return formTypes;
}
