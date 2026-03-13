/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EditableValue} from '../../types/editables/EditableValue';
import {config} from '../config/index';
import {ObjectFields} from '../contexts/ObjectDataContext';
import {State} from '../reducers';
import {PageContent} from '../utils/usePageContents';
import serviceFetch from './serviceFetch';

function getAvailableListItemRenderers({
	itemSubtype,
	itemType,
	listStyle,
}: {
	itemSubtype: string;
	itemType: string;
	listStyle: string;
}) {
	return serviceFetch(config.getAvailableListItemRenderersURL, {
		body: {
			itemSubtype,
			itemType,
			listStyle,
		},
	});
}

function getAvailableListRenderers({
	className,
	key,
}: {
	className: string;
	key: string;
}) {
	return serviceFetch(config.getAvailableListRenderersURL, {
		body: {
			className,
			key,
		},
	});
}

function getAvailableStructureMappingFields({
	classNameId,
	classTypeId,
}: {
	classNameId: string;
	classTypeId: string;
}) {
	return serviceFetch<ObjectFields>(config.mappingFieldsURL, {
		body: {
			classNameId,
			classTypeId,
		},
	});
}

function getAvailableTemplates({
	className,
	classPK,
	externalReferenceCode,
}: {
	className: string;
	classPK: string;
	externalReferenceCode: string;
}) {
	const body: {
		className: string;
		classPK?: string;
		externalReferenceCode?: string;
	} = {
		className,
	};

	if (classPK) {
		body.classPK = classPK;
	}

	if (externalReferenceCode) {
		body.externalReferenceCode = externalReferenceCode;
	}

	return serviceFetch(config.getAvailableTemplatesURL, {
		body,
	});
}

function getInfoItemActionErrorMessage({
	classNameId,
	fieldId,
}: {
	classNameId: string;
	fieldId: string;
}) {
	return serviceFetch(config.getInfoItemActionErrorMessageURL, {
		body: {
			classNameId,
			fieldId,
		},
	});
}

function getInfoItemFieldValue({
	classNameId,
	classPK,
	editableTypeOptions,
	externalReferenceCode,
	fieldId,
	languageId,
}: {
	classNameId: string;
	classPK: string;
	editableTypeOptions: EditableValue['config'];
	externalReferenceCode: string;
	fieldId: string;
	languageId: Liferay.Language.Locale;
}) {
	const body: {
		classNameId: string;
		classPK?: string;
		editableTypeOptions: string;
		externalReferenceCode?: string;
		fieldId: string;
		languageId: Liferay.Language.Locale;
	} = {
		classNameId,
		editableTypeOptions: JSON.stringify(editableTypeOptions),
		fieldId,
		languageId,
	};

	if (classPK) {
		body.classPK = classPK;
	}

	if (externalReferenceCode) {
		body.externalReferenceCode = externalReferenceCode;
	}

	return serviceFetch(config.getInfoItemFieldValueURL, {
		body,
	});
}

function getPageContents({
	segmentsExperienceId,
}: {
	segmentsExperienceId: State['segmentsExperienceId'];
}): Promise<PageContent[]> {
	return serviceFetch(config.getPageContentsURL, {
		body: {
			segmentsExperienceId,
		},
	});
}

function getInfoItemRelationships({
	classNameId,
	classTypeId,
}: {
	classNameId: string;
	classTypeId?: string;
}) {
	const body: {
		classNameId: string;
		classTypeId?: string;
	} = {
		classNameId,
	};

	if (classTypeId) {
		body.classTypeId = classTypeId;
	}

	return serviceFetch(config.getInfoItemOneToManyRelationshipsURL, {
		body: {
			classNameId,
			classTypeId,
		},
	});
}

export default {
	getAvailableListItemRenderers,
	getAvailableListRenderers,
	getAvailableStructureMappingFields,
	getAvailableTemplates,
	getInfoItemActionErrorMessage,
	getInfoItemFieldValue,
	getInfoItemRelationships,
	getPageContents,
};
