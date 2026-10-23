/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type LocalizedValue<T> = Liferay.Language.LocalizedValue<T>;

declare module '@ckeditor/ckeditor5-core/dist/index.js' {
	export * from '@ckeditor/ckeditor5-core';
}
declare module '@ckeditor/ckeditor5-engine/dist/index.js' {
	export * from '@ckeditor/ckeditor5-engine';
}
declare module '@ckeditor/ckeditor5-ui/dist/index.js' {
	export * from '@ckeditor/ckeditor5-ui';
}
