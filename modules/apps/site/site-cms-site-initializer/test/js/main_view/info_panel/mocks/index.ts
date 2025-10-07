/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DOCUMENT_OBJECT_ENTRY = {
	actions: {
		delete: {
			href: 'http://localhost:8080/o/cms/basic-documents/38373',
			method: 'DELETE',
		},
		expire: {
			href: 'http://localhost:8080/o/cms/basic-documents/38373/expire',
			method: 'POST',
		},
		get: {
			href: 'http://localhost:8080/o/cms/basic-documents/38373',
			method: 'GET',
		},
		permissions: {
			href: 'http://localhost:8080/o/cms/basic-documents/38373/permissions',
			method: 'GET',
		},
		replace: {
			href: 'http://localhost:8080/o/cms/basic-documents/38373',
			method: 'PUT',
		},
		update: {
			href: 'http://localhost:8080/o/cms/basic-documents/38373',
			method: 'PATCH',
		},
	},
	dateCreated: '2025-06-27T14:27:40Z',
	dateModified: '2025-06-27T14:27:40Z',
	embedded: {
		creator: {
			additionalName: '',
			contentType: 'UserAccount',
			externalReferenceCode: '94996290-ab59-03cc-52fd-f6f4d49144cd',
			familyName: 'Test',
			givenName: 'Test',
			id: 20132,
			name: 'Test Test',
		},
		dateCreated: '2025-06-27T14:27:40Z',
		dateModified: '2025-06-27T14:27:40Z',
		defaultLanguageId: 'en_US',
		expirationDate: '2025-06-27T14:27:40Z',
		externalReferenceCode: '97753061-a0cd-128c-2f29-02ab5eac6632',
		file: {
			externalReferenceCode: 'c3c0a482-7021-f28e-08b0-4ab38587ca57',
			id: 38368,
			link: {
				href: '/documents/38365/38367/591.pdf/c3c0a482-7021-f28e-08b0-4ab38587ca57?version=1.0&t=1751034460199&download=true&objectDefinitionExternalReferenceCode=L_BASIC_DOCUMENT&objectEntryExternalReferenceCode=97753061-a0cd-128c-2f29-02ab5eac6632',
				label: '591.pdf',
			},
			name: '591.pdf',
			previewURL: '',
			thumbnailURL:
				'/documents/38365/38367/591.pdf/c3c0a482-7021-f28e-08b0-4ab38587ca57?version=1.0&t=1751034460199&documentThumbnail=1',
		},
		friendlyUrlPath: '591.pdf',
		friendlyUrlPath_i18n: {en_US: '591.pdf'},
		id: 38373,
		keywords: [],
		objectEntryFolderExternalReferenceCode: 'L_FILES',
		objectEntryFolderId: 34167,
		reviewDate: '2025-06-27T14:27:40Z',
		scopeId: 34164,
		scopeKey: 'Default',
		status: {code: 0, label: 'approved', label_i18n: 'Approved'},
		systemProperties: {version: {number: 1}},
		taxonomyCategoryBriefs: [],
		title: '591.pdf',
		title_i18n: {en_US: '591.pdf'},
	},
	entryClassName: 'com.liferay.object.model.ObjectDefinition#Z7P5',
	score: 1,
};
